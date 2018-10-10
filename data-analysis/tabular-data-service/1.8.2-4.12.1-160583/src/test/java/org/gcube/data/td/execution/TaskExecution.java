package org.gcube.data.td.execution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.OperationTaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.export.csv.exporter.CSVExportFactory;
import org.gcube.data.analysis.tabulardata.operation.importer.csv.CSVImportFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQuery;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQueryFactory;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.task.executor.TaskWrapperProvider;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TaskExecution {

	@Inject 
	TaskWrapperProvider taskExecutorFactory;
	
	@Inject
	TabularJSONQueryFactory queryFactory;
	
	@Inject
	@Alternative
	DatabaseProvider databaseProvider;
	
	@Inject
	OperationManager operationManager;
	
	@Inject
	TaskManager taskManager;
	
	@Inject
	TabularResourceManager trm;
	
	@Inject 
	ExecutorService executor;
	
	@Inject
	TaskEngine taskEngine;
	
	@Inject
	HistoryManager historyManager;
		
	@Inject
	CSVImportFactory importFactory;
	
	@Inject
	CSVExportFactory exportFactory;
	
	@Inject
	private Factories factories;
	
	@Inject
	EntityManagerHelper te ;
	
	EntityManager em ;
	
	@Inject @Default
	CubeManager cm;
	
	
	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";
	
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec");
		em = te.getEntityManager();
	}
	
	
	
	@Test
	public void TSTTaskFactory(){
		System.out.println(em.getProperties());
		
		StorableTabularResource storableTabularResource = new StorableTabularResource(TabularResourceType.STANDARD, "lucio.lelii","test", "/gcube/devsec");
		Map<String, Serializable> props = new HashMap<String, Serializable>();
		props.put("testProp", "lucio");
		storableTabularResource.setProperties(props);
		em.persist(storableTabularResource);
		
		InternalInvocation importCouple = new InternalInvocation(getImportParameter(), importFactory);
		TaskContext context = new TaskContext(Collections.singletonList(importCouple), OnRowErrorAction.ASK);
		TaskInfo info = taskEngine.createTask("lucio.lelii", context, storableTabularResource);
		
		Assert.assertNotNull(em.find(StorableTask.class, info.getIdentifier()));
		
		try {
			taskEngine.get(info.getIdentifier(), em);
		} catch (NoSuchTaskException e) {
			e.printStackTrace();
		}		
	}
	
	@Test
	public void checkNullTR(){
		StorableTabularResource str = em.find(StorableTabularResource.class, 0l);
		Assert.assertNotNull(str);;
	}
	
	private void printTaskInfo(TaskInfo info){
		System.out.println("--------TASK INFO ----------");
		System.out.println(info.toString());
		for (TaskStep step : info.getTaskSteps())
			System.out.println("task: "+step.toString());
		System.out.println("----------------------------");
	}
	
	
	@Test
	public void TSTRunImport() throws Exception{
		TabularResource tr = trm.createTabularResource(TabularResourceType.STANDARD);
		
		OperationExecution oe = new OperationExecution(100, getImportParameter());
		System.out.println("parameters :"+getImportParameter());
		ExecuteRequest request = new ExecuteRequest(tr.getId(), oe);
		TaskInfo info = null;
		try {
			info = operationManager.execute(request);
		} catch (NoSuchTabularResourceException | OperationNotFoundException
				| InternalSecurityException e) {
			e.printStackTrace();
		}
		
		System.out.println("task info status is null?"+(info.getStatus()==null));
		
		Thread.sleep(200);
		
		while (!info.getStatus().isFinal())
			try {
				info = taskManager.get(new String[]{info.getIdentifier()}).get(0);
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		printTaskInfo(info);
		
		List<HistoryData> hs = historyManager.getHistory(tr.getId());
		
		for (HistoryData h: hs)
			System.out.println(h);
	}
		
	
	@Test
	public void TSTrunOperation(){
		
		InternalInvocation importCouple = new InternalInvocation(getImportParameter(), importFactory);
		
		TaskContext context = new TaskContext(Collections.singletonList(importCouple), OnRowErrorAction.ASK);
		
		StorableTabularResource storableTabularResource = new StorableTabularResource(TabularResourceType.STANDARD, "lucio.lelii","test", "/gcube/devsec");
		Map<String, Serializable> props = new HashMap<String, Serializable>();
		props.put("File CSV", "File CSV");
		props.put("EGIP Normalized CSV", "EGIP Normalized CSV");
		props.put("File CSV estracted in Egip via SM", "File CSV estracted in Egip via SM");
		
		storableTabularResource.setProperties(props);
		em.getTransaction().begin();
		em.persist(storableTabularResource);
		em.getTransaction().commit();
		//long id = storableTabularResource.getId();
		
		TaskInfo info = createTask("lucio.lelii", context, storableTabularResource);
		
		while (!info.getStatus().isFinal())
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println(info.getStatus());
		
		//storableTabularResource = em.find(StorableTabularResource.class,id);
		System.out.println("is tabularResource null??"+(storableTabularResource==null));
		System.out.println(storableTabularResource.getHistorySteps().size());
		
		System.out.println("tabular resource id is "+storableTabularResource.getId());
		
		System.out.println("storable tabular resource prop are "+storableTabularResource.getProperties());
		
		Assert.assertEquals(1, storableTabularResource.getHistorySteps().size());
		
		long tableId = storableTabularResource.getTableId();
		
		Table table = cm.getTable(new TableId(tableId));
			
		
		if (table.contains(TableDescriptorMetadata.class)){
			TableDescriptorMetadata tdm = table.getMetadata(TableDescriptorMetadata.class);
			System.out.println(tdm.getName());
			System.out.println(tdm.getRefId());
			System.out.println(tdm.getVersion());
		}
		
		TabularJSONQuery jsonQuery = queryFactory.get(info.getResult().getResultTable().getId());
		jsonQuery.getPage(new QueryPage(0,100));
		
	}
	
	@Test
	public void TSTexportOperation(){
		
		InternalInvocation importCouple = new InternalInvocation(getImportParameter(), importFactory);
		
		TaskContext context = new TaskContext(Collections.singletonList(importCouple), OnRowErrorAction.ASK);
		
		StorableTabularResource storableTabularResource = new StorableTabularResource(TabularResourceType.STANDARD,"lucio.lelii","test", "/gcube/devsec");
		Map<String, Serializable> props = new HashMap<String, Serializable>();
		props.put("testProp", "lucio");
		storableTabularResource.setProperties(props);
		em.getTransaction().begin();
		em.persist(storableTabularResource);
		em.getTransaction().commit();
		//long id = storableTabularResource.getId();
		
		TaskInfo info = createTask("lucio.lelii", context, storableTabularResource);
		
		while (!info.getStatus().isFinal())
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println(info.getStatus());
		
		//storableTabularResource = em.find(StorableTabularResource.class,id);
		System.out.println("is tabularResource null??"+(storableTabularResource==null));
		System.out.println(storableTabularResource.getHistorySteps().size());
		
		System.out.println("tabular resource id is "+storableTabularResource.getId());
		
		System.out.println("storable tabular resource prop are "+storableTabularResource.getProperties());
		
		Assert.assertEquals(1, storableTabularResource.getHistorySteps().size());
		
		long tableId = storableTabularResource.getTableId();
		
		Table table = cm.getTable(new TableId(tableId));
			
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		params.put("separator", ",");
		params.put("encoding", "UTF-8");
		params.put("columns", Arrays.asList(table.getColumns().get(1).getName()));
		
		InternalInvocation exportCouple = new InternalInvocation(params, exportFactory);
		
		TaskContext exportContext = new TaskContext(Collections.singletonList(exportCouple), OnRowErrorAction.ASK);
		
		info = createTask("lucio.lelii", exportContext, storableTabularResource);
		
		while (!info.getStatus().isFinal())
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("export: "+ info.getStatus());
						
		System.out.println(cm.getTable(table.getId()));
		
		
		StorableTabularResource fTR = em.find(StorableTabularResource.class, storableTabularResource.getId());
		System.out.println(fTR.getResources());
	
	}
	
	
	@Test
	public void TSTrunOperationWitRules(){
		
		InternalInvocation importCouple = new InternalInvocation(getImportParameter(), importFactory);
		
		TaskContext context = new TaskContext(Collections.singletonList(importCouple), OnRowErrorAction.DISCARD);
		
		StorableTabularResource storableTabularResource = new StorableTabularResource(TabularResourceType.STANDARD,"lucio.lelii","test", "/gcube/devsec");
		em.persist(storableTabularResource);
		em.close();
		long id = storableTabularResource.getId();
		
		TaskInfo info = createTask("lucio.lelii", context, storableTabularResource);
		
		System.out.println(info.getStatus());
		
		if (info.getStatus().equals(WorkerStatus.FAILED))
			info.getErrorCause().printStackTrace();
		
		Assert.assertEquals(TaskStatus.SUCCEDED,info.getStatus());
		
		Table table = info.getResult().getResultTable();
		
		Assert.assertNotNull(table);
		storableTabularResource = em.find(StorableTabularResource.class,id);
		WorkerFactory<?> nameChangeFactory = factories.get(new OperationId(710044479));
		
		InternalInvocation nameChangeCouple = new InternalInvocation(Collections.singletonMap("NAME_PARAMETER_ID", (Object)new ImmutableLocalizedText("newColumnName")),
				nameChangeFactory);
		
		nameChangeCouple.setColumnId(table.getColumns().get(2).getLocalId());
		/*
		WorkerFactory codelistCheckFactory = factories.get(new OperationId(5001));
		
		InvocationCouple codelistCouple = new InvocationCouple(null, codelistCheckFactory);
		*/
		context = new TaskContext(Arrays.asList(nameChangeCouple), OnRowErrorAction.ASK);
		
		storableTabularResource = em.find(StorableTabularResource.class, id);
		/*RuleMapping ruleMapping = getRuleMapping(table);
		
		storableTabularResource.addRule(ruleMapping);*/
		
		info = createTask("lucio.lelii", context, storableTabularResource);
								
		//TabularJSONQuery jsonQuery = queryFactory.get(info.getResult().getResultTable().getId());
		
		System.out.println(info);
		
		for (TaskStep step: context.getTasks())
			System.out.println(step);
		
		System.out.println(info.getResult().getResultTable());
		
		System.out.println(info.getStatus());
		/*
		StorableTabularResource postSTR = dataHolder.retrieve(StorableTabularResource.class, storableTabularResource.getId());
		
		System.out.println(postSTR);
		
		postSTR = dataHolder.retrieve(StorableTabularResource.class, storableTabularResource.getId());
		
		System.out.println(postSTR);*/
		TaskContext rC = em.find(StorableTask.class, info.getIdentifier()).getTaskContext();
		System.out.println(rC);
		
		Assert.assertEquals(2, storableTabularResource.getHistorySteps().size());
		
		long tableId = storableTabularResource.getHistorySteps().get(storableTabularResource.getHistorySteps().size()-1).getTableId();
		
		System.out.println(cm.getTable(new TableId(tableId)));
		
	}
	
	@Test
	public void TSTrollbackOperation() throws NoSuchTabularResourceException, HistoryNotFoundException, OperationNotFoundException, InternalSecurityException{
		ScopeProvider.instance.set("/gcube/devsec");
		TaskInfo task = operationManager.rollbackTo(3,3);
		System.out.println("just created task: "+task);
		while (!task.getStatus().isFinal())
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("finished task: "+task);
		System.out.println(task.getResult().getResultTable());
		
	}
	
	private TaskInfo createTask(String submitter, TaskContext context, StorableTabularResource str) {
		TaskInfo taskInfo = new OperationTaskInfo(submitter, str.getId());
		create(taskInfo, context, str);
		return taskInfo;
	}
	
	private void create(TaskInfo taskInfo, TaskContext context, StorableTabularResource str){
		//taskInfo.setTaskSteps(getTaskSteps(context.getInvocationCouples()));
		StorableTask storableTask = new StorableTask(taskInfo, str);
		em.persist(storableTask);
		startTask(taskInfo.getIdentifier(), storableTask,  str, context);
	}
	
	
	private void startTask(String identifier, StorableTask taskProxy,
			StorableTabularResource str, TaskContext context) {
		new Thread(ScopedTasks.bind(taskExecutorFactory.get(context, str, taskProxy, false))).start();
	}

	private Map<String, Object> getImportParameter() {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/cl_species.csv");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		return parameterInstances;	
	}
/*
	private RuleMapping getRuleMapping(Table table){
		String columnId = table.getColumns().get(2).getLocalId().getValue();
		
		ColumnReferencePlaceholder placeholder = new ColumnReferencePlaceholder(new TextType(), "myCol");
		Expression textMatchRegexp = new TextMatchSQLRegexp(placeholder, new TDText("[a-b]*"));
		return new RuleMapping(new ColumnRule(textMatchRegexp), Collections.singletonMap("myCol", columnId)); 
	}*/
	
}
