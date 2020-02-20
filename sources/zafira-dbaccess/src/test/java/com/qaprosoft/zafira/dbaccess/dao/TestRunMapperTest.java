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
package com.qaprosoft.zafira.dbaccess.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.qaprosoft.zafira.dbaccess.PersistenceTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestRunMapper;
import com.qaprosoft.zafira.models.db.Job;
import com.qaprosoft.zafira.models.db.Project;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.TestRun.Initiator;
import com.qaprosoft.zafira.models.db.TestSuite;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.db.config.Argument;

@Test
@ContextConfiguration(classes = PersistenceTestConfig.class)
public class TestRunMapperTest extends AbstractTestNGSpringContextTests {
    /**
     * Turn this on to enable this test
     */
    private static final boolean ENABLED = false;

    private static final TestRun TEST_RUN = new TestRun() {
        private static final long serialVersionUID = 1L;
        {
            User user = new User();
            user.setId(1L);

            Job job = new Job();
            job.setId(1L);

            Project project = new Project();
            project.setId(1L);

            WorkItem workItem = new WorkItem();
            workItem.setId(1L);

            TestSuite testSuite = new TestSuite();
            testSuite.setId(1L);

            setUser(user);
            setTestSuite(testSuite);
            setProject(project);
            setScmBranch("prod");
            setScmCommit("sdfsdfsdfs234234132ff");
            setScmURL("http://localhost:8080/lc");
            setJob(job);
            setUpstreamJob(job);
            setUpstreamJobBuildNumber(2);
            setConfigXML("<xml>");
            setBuildNumber(5);
            setStatus(Status.PASSED);
            setStartedBy(Initiator.HUMAN);
            setWorkItem(workItem);
            setCiRunId(UUID.randomUUID().toString());
            setKnownIssue(true);
            setBlocker(true);
            setEnv("e1");
        }
    };

    @Autowired
    private TestRunMapper testRunMapper;

    @Test(enabled = ENABLED)
    public void createTestRun() {
        testRunMapper.createTestRun(TEST_RUN);

        assertNotEquals(TEST_RUN.getId(), 0, "TestRun ID must be set up by autogenerated keys");
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createTestRun" })
    public void getTestRunById() {
        checkTestRun(testRunMapper.getTestRunById(TEST_RUN.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createTestRun" })
    public void getTestRunByCiRunId() {
        checkTestRun(testRunMapper.getTestRunByCiRunId(TEST_RUN.getCiRunId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createTestRun" })
    public void updateTestRun() {
        TEST_RUN.getUser().setId(2L);
        TEST_RUN.getTestSuite().setId(2L);
        TEST_RUN.getProject().setId(2L);
        TEST_RUN.setScmBranch("stg");
        TEST_RUN.setScmCommit("sdfsdsdffs4132ff");
        TEST_RUN.setScmURL("http://localhost:8080/lc2");
        TEST_RUN.getJob().setId(1L);
        TEST_RUN.getUpstreamJob().setId(1L);
        TEST_RUN.setUpstreamJobBuildNumber(5);
        TEST_RUN.setConfigXML("<xml/>");
        TEST_RUN.setBuildNumber(6);
        TEST_RUN.setStatus(Status.FAILED);
        TEST_RUN.setStartedBy(Initiator.SCHEDULER);
        TEST_RUN.setKnownIssue(false);
        TEST_RUN.setBlocker(false);
        TEST_RUN.setEnv("e1");

        testRunMapper.updateTestRun(TEST_RUN);

        checkTestRun(testRunMapper.getTestRunById(TEST_RUN.getId()));
    }

    /**
     * Turn this in to delete testRun after all tests
     */
    private static final boolean DELETE_ENABLED = true;

    /**
     * If true, then <code>deleteTestRun</code> will be used to delete testRun after all tests, otherwise -
     * <code>deleteTestRunById</code>
     */
    private static final boolean DELETE_BY_TEST_RUN = false;

    @Test(enabled = ENABLED && DELETE_ENABLED && DELETE_BY_TEST_RUN, dependsOnMethods = { "createTestRun", "getTestRunById", "updateTestRun",
            "getTestRunByCiRunId" })
    public void deleteTestRun() {
        testRunMapper.deleteTestRun(TEST_RUN);

        assertNull(testRunMapper.getTestRunById(TEST_RUN.getId()));
    }

    @Test(enabled = ENABLED && DELETE_ENABLED && !DELETE_BY_TEST_RUN, dependsOnMethods = { "createTestRun", "getTestRunById", "updateTestRun" })
    public void deleteTestRunById() {
        testRunMapper.deleteTestRunById((TEST_RUN.getId()));

        assertNull(testRunMapper.getTestRunById(TEST_RUN.getId()));
    }

    @Test(enabled = ENABLED)
    public void getTestRunForRerun() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("url", "http://localhost:8080"));
        Assert.assertNotNull(testRunMapper.getTestRunsForRerun(1L, 1L, 1L, 1L, args));
    }

    private void checkTestRun(TestRun testRun) {
        assertEquals(testRun.getUser().getId(), TEST_RUN.getUser().getId(), "User ID must match");
        assertEquals(testRun.getCiRunId(), TEST_RUN.getCiRunId(), "CI run ID must match");
        assertEquals(testRun.getTestSuite().getId(), TEST_RUN.getTestSuite().getId(), "Test suite ID must match");
        assertEquals(testRun.getStatus(), TEST_RUN.getStatus(), "Status must match");
        assertEquals(testRun.getScmURL(), TEST_RUN.getScmURL(), "SCM URL must match");
        assertEquals(testRun.getScmBranch(), TEST_RUN.getScmBranch(), "SCM branch must match");
        assertEquals(testRun.getScmCommit(), TEST_RUN.getScmCommit(), "SCM commit must match");
        assertEquals(testRun.getConfigXML(), TEST_RUN.getConfigXML(), "Config XML must match");
        assertEquals(testRun.getStartedBy(), TEST_RUN.getStartedBy(), "Initiator must match");
        assertEquals(testRun.getBuildNumber(), TEST_RUN.getBuildNumber(), "Build number must match");
        assertEquals(testRun.getWorkItem().getId(), TEST_RUN.getWorkItem().getId(), "Work item ID must match");
        assertEquals(testRun.getJob().getId(), TEST_RUN.getJob().getId(), "Job ID must match");
        assertEquals(testRun.getUpstreamJob().getId(), TEST_RUN.getUpstreamJob().getId(), "Upstream job ID must match");
        assertEquals(testRun.getUpstreamJobBuildNumber(), TEST_RUN.getUpstreamJobBuildNumber(), "Upstream job build number must match");
        assertEquals(testRun.getProject().getId(), TEST_RUN.getProject().getId(), "Project must match");
        assertEquals(testRun.isKnownIssue(), TEST_RUN.isKnownIssue(), "Known issue must match");
        assertEquals(testRun.isBlocker(), TEST_RUN.isBlocker(), "Blocker must match");
        assertEquals(testRun.getEnv(), TEST_RUN.getEnv(), "Env must match");
    }
}
