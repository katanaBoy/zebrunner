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

import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.TestRunStatistics;
import com.qaprosoft.zafira.models.push.TestPush;
import com.qaprosoft.zafira.models.push.TestRunStatisticPush;
import com.qaprosoft.zafira.service.TestCaseService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.service.cache.TestRunStatisticsCacheableService;
import com.qaprosoft.zafira.service.project.ProjectService;
import com.qaprosoft.zafira.web.AbstractController;
import com.qaprosoft.zafira.web.v2.dto.TestDTO;
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

@RequestMapping(path = "api/v2/tests", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class TestControllerV2 extends AbstractController {

    private final TestCaseService testCaseService;
    private final TestService testService;
    private final UserService userService;
    private final TestRunService testRunService;
    private final SimpMessagingTemplate websocketTemplate;
    private final TestRunStatisticsCacheableService statisticsService;
    private final ProjectService projectService;
    private final Mapper mapper;

    public TestControllerV2(TestCaseService testCaseService, TestService testService, UserService userService, TestRunService testRunService, SimpMessagingTemplate websocketTemplate, TestRunStatisticsCacheableService statisticsService, ProjectService projectService, Mapper mapper) {
        this.testCaseService = testCaseService;
        this.testService = testService;
        this.userService = userService;
        this.testRunService = testRunService;
        this.websocketTemplate = websocketTemplate;
        this.statisticsService = statisticsService;
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @PostMapping
    public TestDTO register(@RequestBody @Valid TestDTO testDTO) {

        User caseOwner = userService.getUserByUsername(testDTO.getOwnerUsername());
        Project project = projectService.getProjectByName("UNKNOWN");
        if (caseOwner == null) {
            caseOwner = userService.getUserByUsername("anonymous");
        }

        TestRun testRun = testRunService.getNotNullTestRunById(testDTO.getTestRunId());

        TestCase testCase = mapper.map(testDTO, TestCase.class);
        testCase.setPrimaryOwner(caseOwner);
        testCase.setTestSuiteId(testRun.getTestSuite().getId());
        testCase.setProject(project);
        testCase = testCaseService.createOrUpdateCase(testCase, "");

        Test test = mapper.map(testDTO, Test.class);
        test.setTestCaseId(testCase.getId());
        test = testService.startTest(test, null, null);

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        mapper.map(test, testDTO);
        return testDTO;
    }

    @PutMapping("/{id}/results")
    public TestDTO finish(@RequestBody @Valid TestDTO testDTO, @PathVariable("id") long id) {
        Test test = mapper.map(testDTO, Test.class);
        test.setId(id);

        test = testService.finishTest(test, null, null);

        TestRunStatistics testRunStatistic = statisticsService.getTestRunStatistic(test.getTestRunId());
        websocketTemplate.convertAndSend(getStatisticsWebsocketPath(), new TestRunStatisticPush(testRunStatistic));
        websocketTemplate.convertAndSend(getTestsWebsocketPath(test.getTestRunId()), new TestPush(test));

        mapper.map(test, testDTO);
        return testDTO;
    }

}
