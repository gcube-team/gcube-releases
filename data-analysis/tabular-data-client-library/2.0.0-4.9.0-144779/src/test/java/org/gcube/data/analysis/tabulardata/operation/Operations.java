package org.gcube.data.analysis.tabulardata.operation;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.operation;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.query;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tabularResource;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.QueryManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TabularResourceManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class Operations {

	
	ParameterRetriever retriever = new FilterByExprParams();

	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		SecurityTokenProvider.instance.set("595ca591-9921-423c-bfca-f8be19f05882-98187548");
	}

	@Test
	public void getCapabilities() throws Exception{
		OperationManagerProxy operationProxy = operation().build();
		for (OperationDefinition descr : operationProxy.getCapabilities()){
			System.out.println("("+descr.getOperationId()+") "+descr.getName()+" with parameters: ");

			if (descr.getParameters()==null) System.out.println("has null parameters !!!!!!!!!!!!!!!!");
			else 
				for(Parameter param : descr.getParameters())
					System.out.println(param);
			System.out.println("-------------------");	
		}
	}

	@Test
	public void createTabularResource() throws Exception{

		TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
		System.out.println(tabularResourceProxy.createTabularResource(TabularResourceType.STANDARD));
	}

	@Test
	public void retrieveAllTr() throws Exception{
		TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
		//HistoryManagerProxy historyProxy =  history().build();
		List<TabularResource> identifiers = tabularResourceProxy.getAllTabularResources();
		System.out.println("size is "+identifiers.size());
		for (TabularResource tr : identifiers){
			System.out.println("found tr "+tr.getId()+ " "+tr.getName());
			for (HistoryData data: tr.getHistory())
				System.out.println(data.toString());
		}
	}

	@Test
	public void retrieveTrsByType() throws Exception{
		TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
		//HistoryManagerProxy historyProxy =  history().build();
		List<TabularResource> identifiers = tabularResourceProxy.getTabularResourcesByType(new GenericTableType().getName());
		System.out.println("size is "+identifiers.size());
		for (TabularResource tr : identifiers){
			System.out.println("found tr "+tr);
			for (HistoryData data: tr.getHistory())
				System.out.println(data.toString());
		}
	}
	
	@Test
	public void removeTR() throws Exception{
		TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
		//HistoryManagerProxy historyProxy =  history().build();
		tabularResourceProxy.remove(1l);
	}
	
	@Test
	public void execute() throws Exception{
		
		TabularResourceManagerProxy tabularResourceProxy = tabularResource().build();
		//HistoryManagerProxy historyProxy =  history().build();
		OperationManagerProxy operationProxy = operation().build();
		QueryManagerProxy queryProxy = query().build();
		TaskManagerProxy taskProxy = tasks().build();

		List<TabularResource> identifiers = tabularResourceProxy.getAllTabularResources();

		
		TabularResource selectedTabularResource = null;
		System.out.println("rettrieved tabular resource "+identifiers);
		Table table= null;
		for (TabularResource tr : identifiers){
			try{
				int historySize= tr.getHistory().size();
				if (historySize!=0)
					table = queryProxy.getTable(tr.getHistory().get(historySize-1).getResultTableId().getValue());
				else table=null;
				System.out.println("is tr valid "+retriever.verifyTable(table));
				if (retriever.verifyTable(table)){
					selectedTabularResource = tr;
					break;
				}
			}catch (Exception e) {
				System.out.println("id discarded for an error");
			}
		}
		if (selectedTabularResource==null) throw new Exception("no tabular reosurce compatible with the operation are available");

		System.out.println("tabular data id: "+selectedTabularResource.getId());
		
		
		System.out.println(table);
		

		//Table table = historyProxy.getLastTable(selectedTabularResource.getId());
		Map<String, Object> params = null;
		if (table!=null)
			params = retriever.getParameter(table, table.getColumns().get(1) );
		else params = retriever.getParameter();
		TaskInfo taskInfo = operationProxy.execute(selectedTabularResource.getId(), retriever.getInvocation(params, table));
		
		while (taskInfo.getStatus()!= TaskStatus.FAILED && taskInfo.getStatus()!= TaskStatus.SUCCEDED){
			System.out.println(taskInfo.getStatus()/*+" "+taskInfo.getStartTime().getTimeInMillis()*/);
			Thread.sleep(5000);
			taskInfo = taskProxy.get(new String[]{taskInfo.getIdentifier()}).get(0);
		}
		
		System.out.println(taskInfo.getStatus()+" "+taskInfo.getStartTime().getTimeInMillis()+" "+taskInfo.getEndTime().getTimeInMillis());
		
		if (taskInfo.getStatus()== TaskStatus.FAILED){
			taskInfo.getErrorCause().printStackTrace();
			return;
		}
			
		Assert.assertNotNull(taskInfo.getResult().getResultTable());
		
		System.out.println(taskInfo.getResult().getResultTable());
		
		System.out.println(query().build().getQueryLenght(taskInfo.getResult().getResultTable().getId().getValue(), null));
	}

}
