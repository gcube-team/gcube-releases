package org.gcube.data.td;


import javax.xml.ws.Endpoint;

import org.gcube.data.analysis.tabulardata.service.HistoryManagerImpl;
import org.gcube.data.analysis.tabulardata.service.OperationManagerImpl;
import org.gcube.data.analysis.tabulardata.service.QueryManagerImpl;
import org.gcube.data.analysis.tabulardata.service.RuleManagerImpl;
import org.gcube.data.analysis.tabulardata.service.TabularResourceManagerImpl;
import org.gcube.data.analysis.tabulardata.service.TaskManagerImpl;
import org.gcube.data.analysis.tabulardata.service.TemplateManagerImpl;
import org.junit.Test;

public class TomcatTest {

	@Test
	public void operatioManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new OperationManagerImpl());
	}

	@Test
	public void tabularResourceManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new TabularResourceManagerImpl());
	}

	@Test
	public void taskManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new TaskManagerImpl());
	}

	@Test
	public void historyManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new HistoryManagerImpl());
	}

	@Test
	public void queryManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new QueryManagerImpl());
	}

	@Test
	public void templateManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new TemplateManagerImpl());
	}

	@Test
	public void ruleManagerTest() throws Exception{
		Endpoint.publish("http://localhost:9090/",new RuleManagerImpl());
	}
}