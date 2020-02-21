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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TestConfigMapper;
import com.qaprosoft.zafira.models.db.TestConfig;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qaprosoft.zafira.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.TEST_RUN_NOT_FOUND;
import static com.qaprosoft.zafira.service.util.XmlConfigurationUtil.readArguments;

@Service
public class TestConfigService {

    private static final String ERR_MSG_TEST_RUN_NOT_FOUND = "Test run with id %s can not be found";

    @Autowired
    private TestConfigMapper testConfigMapper;

    @Autowired
    private TestRunService testRunService;

    @Transactional
    public void createTestConfig(TestConfig testConfig) {
        testConfigMapper.createTestConfig(testConfig);
    }

    @Transactional
    public TestConfig createTestConfigForTest(Long testRunId, String testConfigXML) {
        TestRun testRun = testRunService.getTestRunById(testRunId);
        if (testRun == null) {
            throw new ResourceNotFoundException(TEST_RUN_NOT_FOUND, ERR_MSG_TEST_RUN_NOT_FOUND, testRunId);
        }

        List<Argument> testRunConfig = readArguments(testRun.getConfigXML()).getArg();
        List<Argument> testConfig = readArguments(testConfigXML).getArg();

        TestConfig config = new TestConfig()
                .init(testRunConfig)
                .init(testConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }

        return config;
    }

    @Transactional
    public TestConfig createTestConfigForTestRun(String configXML) {
        List<Argument> testRunConfig = readArguments(configXML).getArg();

        TestConfig config = new TestConfig().init(testRunConfig);

        TestConfig existingTestConfig = searchTestConfig(config);
        if (existingTestConfig != null) {
            config = existingTestConfig;
        } else {
            createTestConfig(config);
        }
        return config;
    }

    @Transactional(readOnly = true)
    public List<String> getPlatforms() {
        return testConfigMapper.getPlatforms();
    }

    @Transactional(readOnly = true)
    public List<String> getBrowsers() {
        return testConfigMapper.getBrowsers();
    }

    @Transactional(readOnly = true)
    public TestConfig searchTestConfig(TestConfig testConfig) {
        return testConfigMapper.searchTestConfig(testConfig);
    }
}
