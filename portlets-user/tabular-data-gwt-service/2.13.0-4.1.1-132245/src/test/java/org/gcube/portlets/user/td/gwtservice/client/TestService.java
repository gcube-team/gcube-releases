package org.gcube.portlets.user.td.gwtservice.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TestService {

	protected Map<String, Object> getParameterInvocationSDMX() {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances
				.put("registryBaseUrl",
						"http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		parameterInstances.put("id", "CL_SPECIES");
		parameterInstances.put("version", "1.0");
		parameterInstances.put("agency", "FAO");
		return parameterInstances;
	}

	@Test
	public void createSDMX() throws Exception {
		System.out.println("------------Create SDMX TR--------------");
		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
		
		AuthorizationProvider.instance.set(new AuthorizationToken(Constants.DEFAULT_USER));
		TabularDataService service = TabularDataServiceFactory.getService();
		
		TabularResource resource = service.createTabularResource();
		System.out.println("after createTabResource "
				+ ScopeProvider.instance.get());
		System.out.println("name: "
				+ ((NameMetadata) resource.getMetadata(NameMetadata.class))
						.getValue());

		System.out.println("tabular data id: " + resource.getId().getValue());
		TabularResourceId resourceId = resource.getId();
		
		System.out.println("------------List Operations------------");
		
		List<OperationDefinition> ops = service.getCapabilities();
		OperationDefinition csvop = null;
		for (OperationDefinition op : ops) {
			System.out.println(op.getName());
			if (op.getName().compareTo("SDMX Codelist import")==0)
			{	
				System.out.println("----Takes");				
				csvop = op;
			
			}
		}
		//
		System.out.println("------------Invocation------------");
				
		OperationExecution invocation = new OperationExecution(csvop.getOperationId(), getParameterInvocationSDMX());
		
		
		//
		System.out.println("------------Execute------------");
		Task task = service.execute(invocation, resource.getId());
		System.out.println("------------Check Task------------");
		while (task.getStatus() != TaskStatus.FAILED
				&& task.getStatus() != TaskStatus.SUCCEDED) {
			System.out.println(task.getStatus() + " "
					+ task.getStartTime().getTime());

			Thread.sleep(3000);
		}
		
		System.out.println("------------Show Task------------");
		
		System.out.println(task.getStatus() + " "
				+ task.getStartTime().getTime() + " "
				+ task.getEndTime().getTime());

		System.out.println("Task Progress:" + task.getProgress());
		Assert.assertNotNull(task.getResult());
		System.out.println("Task getResult: " + task.getResult());
		Table resultTable = task.getResult().getPrimaryTable();

		System.out.println("resultTable :" + resultTable);

		System.out.println(service.getLastTable(resourceId).getName());

		System.out.println(service.getQueryLenght(resultTable.getId(), null));
		System.out.println(service.queryAsJson(resultTable.getId(),
				new QueryPage(0, 200)));

	}
	
	
	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";
	
	private Map<String, Object> getParameterInvocationCSV(){
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "smp://importCSV/import.csv?5ezvFfBOLqZDktisNW+YdntfYeiRwwsx6mcSk7QrncHxpcgkYS6DSZuEmrrVwjNcQni7q/eoILP1k7BwnoPjFNqJykZW71mI7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1aaspqr2Xc3LqUp9Q101dAT29nbhl9SyJqgEi0BgUQmMaZs/n4coLNw==");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		return parameterInstances;
	}
	
	@Test
	public void createCSV() throws Exception{
		System.out.println("------------Create CSV TR--------------");
		try{
			ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
			AuthorizationProvider.instance.set(new AuthorizationToken(Constants.DEFAULT_USER));
			TabularDataService service = TabularDataServiceFactory.getService();
			
			TabularResource resource = service.createTabularResource();	
			System.out.println("after createTabResource "+ScopeProvider.instance.get());
			System.out.println("name: "+((NameMetadata)resource.getMetadata(NameMetadata.class)).getValue());
 
 
			System.out.println("tabular data id: "+resource.getId().getValue());
			TabularResourceId resourceId = resource.getId();
 
			List<OperationDefinition> ops =service.getCapabilities();
			OperationDefinition csvop = null; 
			for (OperationDefinition op : ops ){
				System.out.println(op.getName());
				if (op.getName().equals("CSV Import"))
					csvop = op;
			}
 
			
			
			OperationExecution invocation = new OperationExecution(csvop.getOperationId(), getParameterInvocationCSV());
			
			
			Task task = service.execute(invocation, resource.getId());
 
			while (task.getStatus()!= TaskStatus.FAILED && task.getStatus()!= TaskStatus.SUCCEDED){
				System.out.println(task.getStatus()+" "+task.getStartTime().getTime());
 
				Thread.sleep(3000);
			}
 
			System.out.println(task.getStatus());
 
			if (task.getStatus()== TaskStatus.FAILED)
				System.out.println("Failed: "+task.getErrorCause());
 
			Assert.assertEquals(TaskStatus.SUCCEDED, task.getStatus());
 
			System.out.println(task.getStartTime().getTime()+" "+task.getEndTime().getTime());
 
 
			Assert.assertNotNull(task.getResult());
 
			Table resultTable = task.getResult().getPrimaryTable();
 
			System.out.println("resultTable :"+task.getResult().getPrimaryTable());
 
			System.out.println(service.getLastTable(resourceId).getName());
 
			Assert.assertNotNull(service);
 
			System.out.println(service.queryAsJson(resultTable.getId(), new QueryPage(0, 200)));
 
			System.out.println(service.getQueryLenght(resultTable.getId(), null));
 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void listTR() throws Exception {
		System.out.println("------------List TR--------------");
		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
		AuthorizationProvider.instance.set(new AuthorizationToken(Constants.DEFAULT_USER));
		TabularDataService service = TabularDataServiceFactory.getService();
		
		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);
		System.out.println("---Tabular Resource---");
		Table lastTable=null;
		for (TabularResource tr : trs) {
			lastTable=service.getLastTable(tr.getId());
			
			if(lastTable!=null){
				System.out.println("TabularResource: [" + tr.getId()+", lastTable: "+lastTable.getTableType().getName()+"]");
			} else {
				System.out.println("TabularResource: [" + tr.getId()+", lastTable: "+lastTable+"]");
				
			}
		}
		
		if(lastTable!=null){
			System.out.println("---Test Column---");
			System.out.println("Table "+lastTable.getId()+" "+lastTable.getName());
			for(Column col:lastTable.getColumns()){
				System.out.println("Column: [name:" + col.getName()+", localId:"+col.getLocalId()+", dataType:"+col.getDataType()+"]");
				
			}
			
		}
		
	}
	
	
	
}
