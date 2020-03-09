/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.web.v2;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.cache.TestRunStatisticsCacheableService;
import com.qaprosoft.zafira.service.v2.TestServiceV2;
import com.qaprosoft.zafira.web.AbstractController;
import com.qaprosoft.zafira.web.v2.dto.TestDTO;
import com.qaprosoft.zafira.web.v2.dto.TestResultDTO;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(path = "api/v2/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestControllerV2 extends AbstractController {

    private final TestService testService;
    private final SimpMessagingTemplate websocketTemplate;
    private final TestRunStatisticsCacheableService statisticsService;

    private final TestServiceV2 testServiceV2;
    private final Mapper mapper;

    public TestControllerV2(TestService testService, SimpMessagingTemplate websocketTemplate, TestRunStatisticsCacheableService statisticsService, TestServiceV2 testServiceV2, Mapper mapper) {
        this.testService = testService;
        this.websocketTemplate = websocketTemplate;
        this.statisticsService = statisticsService;
        this.testServiceV2 = testServiceV2;
        this.mapper = mapper;
    }

    @PostMapping
    public TestDTO register(@RequestBody @Valid TestDTO testDTO) {
        TestCase testCase = mapper.map(testDTO, TestCase.class);
        Test test = mapper.map(testDTO, Test.class);

        test = testServiceV2.startTest(test, testCase);

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        mapper.map(test, testDTO);
        return testDTO;
    }

    @PutMapping("/{id}/results")
    public TestDTO finish(@RequestBody @Valid TestResultDTO testResult, @PathVariable("id") long id) {
        Test test = mapper.map(testResult, Test.class);
        test.setId(id);

        test = testService.finishTest(test, null, null);

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        return mapper.map(test, TestDTO.class);
    }

    @GetMapping("/{ciRunId}")
    public List<TestDTO> getByTestsForRerun(
            @PathVariable("ciRunId") String ciRunId,
            @RequestParam(name = "tests", required = false) List<Long> testIds,
            @RequestParam(name = "statuses", required = false) List<Status> statuses
    ) {
        List<Test> tests = testService.getTestsByTestRunId(ciRunId);

        if (testIds != null) {
            tests = tests.stream()
                         .filter(test -> testIds.contains(test.getId()))
                         .collect(Collectors.toList());
        }

        if (statuses != null) {
            tests = tests.stream()
                         .filter(test -> statuses.contains(test.getStatus()))
                         .collect(Collectors.toList());
        }

        return tests.stream()
                    .map(test -> mapper.map(test, TestDTO.class))
                    .collect(Collectors.toList());
    }

}
