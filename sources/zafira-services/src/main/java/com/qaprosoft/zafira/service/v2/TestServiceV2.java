package com.qaprosoft.zafira.service.v2;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestMapper;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestCase;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.service.TestCaseService;
import com.qaprosoft.zafira.service.TestRunService;
import com.qaprosoft.zafira.service.TestService;
import com.qaprosoft.zafira.service.UserService;
import com.qaprosoft.zafira.service.project.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestServiceV2 {

    private final UserService userService;
    private final ProjectService projectService;
    private final TestRunService testRunService;
    private final TestCaseService testCaseService;
    private final TestService testService;
    private final TestMapper testMapper;

    public TestServiceV2(UserService userService, ProjectService projectService, TestRunService testRunService, TestCaseService testCaseService, TestService testService, TestMapper testMapper) {
        this.userService = userService;
        this.projectService = projectService;
        this.testRunService = testRunService;
        this.testCaseService = testCaseService;
        this.testService = testService;
        this.testMapper = testMapper;
    }

    @Transactional
    public Test startTest(Test test, TestCase testCase) {
        User caseOwner = userService.getUserByUsername(test.getOwner());
        Project project = projectService.getProjectByName("UNKNOWN");
        if (caseOwner == null) {
            caseOwner = userService.getUserByUsername("anonymous");
        }

        TestRun testRun = testRunService.getNotNullTestRunById(test.getTestRunId());

        testCase.setPrimaryOwner(caseOwner);
        testCase.setTestSuiteId(testRun.getTestSuite().getId());
        testCase.setProject(project);
        testCase = testCaseService.createOrUpdateCase(testCase, "UNKNOWN");

        test.setTestCaseId(testCase.getId());

        Test existingTest = testMapper.getTestByTestRunIdAndUid(test.getTestRunId(), test.getUid());

        if (existingTest != null) {
            test.setId(existingTest.getId());
        }

        return testService.startTest(test, null, null);
    }
}
