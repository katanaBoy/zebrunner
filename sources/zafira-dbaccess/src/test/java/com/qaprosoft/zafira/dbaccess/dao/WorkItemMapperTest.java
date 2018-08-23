/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import com.qaprosoft.zafira.dbaccess.utils.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.WorkItemMapper;
import com.qaprosoft.zafira.models.db.application.User;
import com.qaprosoft.zafira.models.db.application.WorkItem;
import com.qaprosoft.zafira.models.db.application.WorkItem.Type;

@Test
@ContextConfiguration("classpath:com/qaprosoft/zafira/dbaccess/dbaccess-test.xml")
public class WorkItemMapperTest extends AbstractTestNGSpringContextTests
{
	/**
	 * Turn this on to enable this test
	 */
	private static final boolean ENABLED = false;
	
	private static final WorkItem WORK_ITEM = new WorkItem()
	{
		private static final long serialVersionUID = 1L;
		{
			User user = new User();
			user.setId(1L);
			
			setJiraId("JIRA-123");
			setType(Type.BUG);
			setDescription("d1");
			setBlocker(true);
			setHashCode(KeyGenerator.getKey());
			setUser(user);
			setTestCaseId(1L);
		}
	};

	@Autowired
	private WorkItemMapper workItemMapper;
	
	
	@Test(enabled = ENABLED)
	public void createWorkItem()
	{
		workItemMapper.createWorkItem(WORK_ITEM);

		assertNotEquals(WORK_ITEM.getId(), 0, "WorkItem ID must be set up by autogenerated keys");
	}
	
	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createWorkItem" }, expectedExceptions =
	{ DuplicateKeyException.class })
	public void createWorkItemFail()
	{
		workItemMapper.createWorkItem(WORK_ITEM);
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createWorkItem" })
	public void getWorkItemById()
	{
		checkWorkItem(workItemMapper.getWorkItemById(WORK_ITEM.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createWorkItem" })
	public void getWorkItemByJiraId()
	{
		checkWorkItem(workItemMapper.getWorkItemByJiraIdAndType(WORK_ITEM.getJiraId(), Type.BUG));
	}

	@Test(enabled = ENABLED, dependsOnMethods =
	{ "createWorkItem" })
	public void updateWorkItem()
	{
		WORK_ITEM.setJiraId("JIRA-3344");
		WORK_ITEM.setType(Type.TASK);
		WORK_ITEM.setDescription("d2");
		WORK_ITEM.setBlocker(false);
		WORK_ITEM.setHashCode(KeyGenerator.getKey());
		WORK_ITEM.getUser().setId(2L);
		WORK_ITEM.setTestCaseId(2L);
		
		workItemMapper.updateWorkItem(WORK_ITEM);

		checkWorkItem(workItemMapper.getWorkItemById(WORK_ITEM.getId()));
	}

	/**
	 * Turn this in to delete workItem after all tests
	 */
	private static final boolean DELETE_ENABLED = true;

	/**
	 * If true, then <code>deleteWorkItem</code> will be used to delete workItem after all tests, otherwise -
	 * <code>deleteWorkItemById</code>
	 */
	private static final boolean DELETE_BY_WORK_ITEM = false;

	@Test(enabled = ENABLED && DELETE_ENABLED && DELETE_BY_WORK_ITEM, dependsOnMethods =
	{ "createWorkItem", "createWorkItemFail", "getWorkItemById", "getWorkItemByJiraId", "updateWorkItem" })
	public void deleteWorkItem()
	{
		workItemMapper.deleteWorkItem(WORK_ITEM);

		assertNull(workItemMapper.getWorkItemById(WORK_ITEM.getId()));
	}

	@Test(enabled = ENABLED && DELETE_ENABLED && !DELETE_BY_WORK_ITEM, dependsOnMethods =
	{ "createWorkItem", "createWorkItemFail", "getWorkItemById", "getWorkItemByJiraId", "updateWorkItem" })
	public void deleteWorkItemById()
	{
		workItemMapper.deleteWorkItemById((WORK_ITEM.getId()));

		assertNull(workItemMapper.getWorkItemById(WORK_ITEM.getId()));
	}

	private void checkWorkItem(WorkItem workItem)
	{
		assertEquals(workItem.getJiraId(), WORK_ITEM.getJiraId(), "Jira ID must match");
		assertEquals(workItem.getHashCode(), WORK_ITEM.getHashCode(), "Hash code must match");
		assertEquals(workItem.getDescription(), WORK_ITEM.getDescription(), "Description must match");
		assertEquals(workItem.isBlocker(), WORK_ITEM.isBlocker(), "Is blocker value must match");
		assertEquals(workItem.getType(), WORK_ITEM.getType(), "Type must match");
		assertEquals(workItem.getUser().getId(), WORK_ITEM.getUser().getId(), "User must match");
		assertEquals(workItem.getTestCaseId(), WORK_ITEM.getTestCaseId(), "Test case id must match");
	}
}
