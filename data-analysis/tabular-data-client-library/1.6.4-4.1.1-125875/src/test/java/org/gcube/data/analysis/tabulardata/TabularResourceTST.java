package org.gcube.data.analysis.tabulardata;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.operation;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.query;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tabularResource;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo.TaskType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularResourceTST {

	Logger logger = LoggerFactory.getLogger(TabularResourceTST.class);

	@Before
	public void init(){
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void getTask(){
		System.out.println(tasks().build().get("5b1d9544-298d-435d-9e37-12fc5d8f5ed4"));
	}

	@Test
	public void printLastTable() throws NoSuchTabularResourceException, NoSuchTableException{
		System.out.println(query().build().getTable(4));
	}
	
	@Test
	public void cleanDb(){
		TabularResourceManagerProxy proxy = tabularResource().build();
		proxy.cleanDatabase();
	}

	@Test
	public void getAllTabularResources() throws Exception{
		logger.trace("executing getTabularDatamanager");
		try{
			TabularResourceManagerProxy proxy = tabularResource().build();
			Assert.assertNotNull(proxy);
			TaskManagerProxy task = tasks().build();
			for (TabularResource td: proxy.getAllTabularResources()){
				System.out.println(td.getName());
				List<TaskInfo> taskEntry = task.getTasksByTabularResource(td.getId());
				for (TaskInfo info : taskEntry)
					if (info.getType() == TaskType.TEMPLATE)
						System.out.println("TASK -- "+info.getIdentifier());
				
			}
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getTabularResource() throws Exception{
		logger.trace("executing getTabularDatamanager");
		try{
			TabularResourceManagerProxy proxy = tabularResource().build();
			Assert.assertNotNull(proxy);
			TabularResource td = proxy.getTabularResource(226);
			System.out.println(td.getId()+ " - "+td.getName());
			for (HistoryData hd: td.getHistory())
				System.out.println(hd);

		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void share() throws Exception{
		logger.trace("executing share");
		try{
			TabularResourceManagerProxy proxy = tabularResource().build();
			Assert.assertNotNull(proxy);
			proxy.share(4l, new AuthorizationToken("giancarlo.panichi"));
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createTabularResource() throws Exception{
		logger.trace("executing createTabularResource");
		try{
			TabularResourceManagerProxy proxy = tabularResource().build();
			Assert.assertNotNull(proxy);
			TabularResource resource = proxy.createTabularResource(TabularResourceType.STANDARD);
			System.out.println(resource.getCreationDate());
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getUnscopedEligibleOperationUnscoped() throws Exception{
		logger.trace("executing getTabularDatamanager");
		try{
			OperationManagerProxy proxy = operation().build();
			Assert.assertNotNull(proxy);
			System.out.println(proxy.getCapabilities());
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
