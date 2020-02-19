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

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.TestSuite;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.push.AbstractPush;
import com.qaprosoft.zafira.models.push.TestRunPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.LauncherCallbackService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestSuiteService;
import com.qaprosoft.zafira.service.cache.TestRunStatisticsCacheableService;
import com.qaprosoft.zafira.web.AbstractController;
import com.qaprosoft.zafira.web.v2.dto.TestRunDTO;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RequestMapping(path = "api/v2/tests/runs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestRunControllerV2 extends AbstractController {

    private final TestRunService testRunService;
    private final TestSuiteService testSuiteService;
    private final SimpMessagingTemplate websocketTemplate;
    private final TestRunStatisticsCacheableService statisticsService;
    private final LauncherCallbackService launcherCallbackService;
    private final Mapper mapper;

    public TestRunControllerV2(
            TestRunService testRunService,
            TestSuiteService testSuiteService,
            SimpMessagingTemplate websocketTemplate,
            Mapper mapper,
            TestRunStatisticsCacheableService statisticsService,
            LauncherCallbackService launcherCallbackService
    ) {
        this.testRunService = testRunService;
        this.testSuiteService = testSuiteService;
        this.websocketTemplate = websocketTemplate;
        this.mapper = mapper;
        this.statisticsService = statisticsService;
        this.launcherCallbackService = launcherCallbackService;
    }

    @PostMapping
    public TestRunDTO register(@RequestBody @Valid TestRunDTO testRunDTO) {

        Long principalId = getPrincipalId();
        User principal = new User();
        principal.setId(principalId);

        TestSuite testSuite = mapper.map(testRunDTO, TestSuite.class);
        testSuite.setUser(principal);
        testSuite = testSuiteService.createOrUpdateTestSuite(testSuite);

        TestRun testRun = mapper.map(testRunDTO, TestRun.class);
        testRun.setTestSuite(testSuite);
        testRun.setUser(principal);
        testRun = testRunService.startTestRun(testRun);

        sendTestRunPush(testRun);
        AbstractPush push = new TestRunStatisticPush(statisticsService.getTestRunStatistic(testRun.getId()));
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), push);

        mapper.map(testRun, testRunDTO);
        return testRunDTO;
    }

    @PutMapping("/{id}/results")
    public TestRunDTO finish(@PathVariable("id") @NotNull @Positive Long id) {
        TestRun testRun = testRunService.calculateTestRunResult(id, true);
        TestRun testRunFull = testRunService.getTestRunByIdFull(testRun.getId());

        launcherCallbackService.notifyOnTestRunFinish(testRun.getCiRunId());

        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(statisticsService.getTestRunStatistic(id)));
        sendTestRunPush(testRunFull);

        return mapper.map(testRun, TestRunDTO.class);
    }

    private void sendTestRunPush(TestRun testRun) {
        testRunService.hideJobUrlsIfNeed(List.of(testRun));
        websocketTemplate.convertAndSend(getTestRunsWebsocketPath(), new TestRunPush(testRun));
    }
}
