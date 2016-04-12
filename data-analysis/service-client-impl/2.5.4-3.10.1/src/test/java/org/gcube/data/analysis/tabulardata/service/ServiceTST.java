package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.history;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.HistoryManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Utils;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ValidateExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.utils.Licence;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.operation.ValidationJob;
import org.gcube.data.analysis.tabulardata.service.rules.RuleId;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.AgencyMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.LicenceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.RightsMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidSinceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidUntilMetadata;
import org.junit.Assert;
import org.junit.Test;


public class ServiceTST {

	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";


	@Test
	public void removeResource() throws Throwable{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
		TabularDataService service = TabularDataServiceFactory.getService();
		service.removeResurce(23);
	}

	@Test
	public void getResources() throws Throwable{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("giancarlo.panichi"));
		TabularDataService service = TabularDataServiceFactory.getService();
		for (ResourceDescriptor rd: service.getResources(new TabularResourceId(155))){
			System.out.println(rd.getName()+" "+rd.getDescription()+" "+rd.getResourceType()+" ");
			if (rd.getResource() instanceof InternalURI){
				System.out.println("is uri : "+((InternalURI) rd.getResource()).getUri().toString());
			}
		}
	}

	@Test
	public void getTabularResources() throws Throwable{
		try{
			ScopeProvider.instance.set("/d4science.research-infrastructures.eu/Ecosystem");
			AuthorizationProvider.instance.set(new AuthorizationToken("fabio.sinibaldi"));
			TabularDataService service = TabularDataServiceFactory.getService();
			for (TabularResource tr : service.getTabularResources()){
				System.out.println(tr.getMetadata(NameMetadata.class).getValue()+"----  "+tr.getId());

			}
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void foundWrongTR() throws Throwable{
		try{
			HistoryManagerProxy historyManager = history().build();
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			//TabularDataService service = TabularDataServiceFactory.getService();
			List<Integer> ids = Arrays.asList(34,58);

			for (Integer id: ids){
				try{
					try{
						for (HistoryData data : historyManager.getHistory(id))
							System.out.println("not wrong history : "+data.getId());							
					}catch(Exception e){
						System.out.println("wrong istory "+id);
						e.printStackTrace();
					}
					System.out.println("correct tabular resource "+id);
				}catch (Exception e) {
					System.err.println("worng tabular resource "+id);
				}
			}
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}


	@Test
	public void getTable() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			AuthorizationProvider.instance.set(new AuthorizationToken("fabio.sinibaldi"));
			TabularDataService service = TabularDataServiceFactory.getService();
			System.out.println(service.getTable(new TableId(2675)));
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void getTemplates() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			TabularDataService service = TabularDataServiceFactory.getService();
			for (TemplateDescription tr : service.getTemplates()){
				System.out.println(tr.getId()+"  --- "+ tr.getTemplate());

			}
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void createTemplates() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			TabularDataService service = TabularDataServiceFactory.getService();
			//Template template = Template.create(TemplateCategory.GENERIC, new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class));

			System.out.println(service.saveTemplate("testFM", "desc", "FM", createTemplateWithTableRule()));
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	private Template createTemplateWithTableRule(){

		TemplateColumn<TextType> col = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 

		Template template = Template.create(TemplateCategory.GENERIC, col);

		template.addAction(new ValidateExpressionAction("rest", new Equals(new ColumnReferencePlaceholder(new TextType(), col.getId(),"label"), new TDText("pippo"))));

		return template;

	}

	@Test
	public void share() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("giancarlo.panichi"));
			TabularDataService service = TabularDataServiceFactory.getService();
			service.share(new TabularResourceId(5), new AuthorizationToken("pasquale.pagano"), new AuthorizationToken("pasquale.pagano"));
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void remove() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
			TabularDataService service = TabularDataServiceFactory.getService();
			service.removeTabularResource(new TabularResourceId(3));
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void setTypeToCodelist() throws Throwable{

		Map<String, Object> changeTableParameter = new HashMap<String, Object>();
		changeTableParameter.put("tableType", new CodelistTableType().getName());
		OperationExecution changeToCodelistInvocation = new OperationExecution(1002, changeTableParameter);


		ScopeProvider.instance.set("/gcube/devsec");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
		TabularDataService service = TabularDataServiceFactory.getService();
		Task info = service.execute(changeToCodelistInvocation, new TabularResourceId(132));
		while (!info.getStatus().isFinal())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		if (info.getStatus() == TaskStatus.FAILED)
			throw info.getErrorCause();
	}


	@Test
	public void changeMetadata() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
			TabularDataService service = TabularDataServiceFactory.getService();
			TabularResource tr = service.getTabularResource(new TabularResourceId(132));
			System.out.println(tr);




		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void getTaskByTR() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			TabularDataService service = TabularDataServiceFactory.getService();

			SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");

			for (Task task: service.getTasks(new TabularResourceId(173))) 
			{

				System.out.println("----------------------");


				System.out.println(f.format(task.getStartTime()));
				System.out.println(task.getEndTime());
				System.out.println(task.getStatus());
				System.out.println(task.getProgress());
				System.out.println(task.getTaskType());
				System.out.println(task.getSubmitter());
				System.out.println(task.getTabularResourceId());
				System.out.println("--------");
				System.out.println(task.getTaskJobs().size());
				for(Job job : task.getTaskJobs()){
					System.out.println("job ----- "+job.getProgress());
					System.out.println("    ----- "+job.getStatus());
					System.out.println("    ----- "+job.getDescription());
					System.out.println("    ----- "+job.getHumaReadableStatus());
					System.out.println("    ----- "+job.getValidations());

					for (ValidationJob vJob: job.getValidationJobs()){
						System.out.println(vJob.getProgress());
						System.out.println(vJob.getHumaReadableStatus());
						System.out.println(vJob.getDescription());
					}
				}

				break;
			}
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}



	@Test
	public void getTask() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			TabularDataService service = TabularDataServiceFactory.getService();
			TaskId id = new TaskId("18e668e8-7f1d-4db9-adfa-0cccf22094df");
			Task task = service.getTask(id);

			System.out.println(task.getStartTime());
			System.out.println(task.getStatus());
			System.out.println(task.getProgress());
			System.out.println("--------");
			System.out.println(task.getTaskJobs().size());
			for(Job job : task.getTaskJobs()){
				System.out.println("job ----- "+job.getProgress());
				System.out.println("    ----- "+job.getStatus());
				System.out.println("    ----- "+job.getDescription());
				System.out.println("    ----- "+job.getHumaReadableStatus());
			}

		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void getCapabilities() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
			TabularDataService service = TabularDataServiceFactory.getService();
			for (OperationDefinition descriptor :service.getCapabilities()){
				System.out.println(descriptor.getOperationId()+" - "+descriptor.getName()+" - "+descriptor.getDescription());
			}

		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void removeValidations() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("giancarlo.panichi"));
		TabularDataService service = TabularDataServiceFactory.getService();
		Task task =service.removeValidations(new TabularResourceId(224));
		Assert.assertNotNull(task);
		while (!task.getStatus().isFinal())
			Thread.sleep(1000);
		System.out.println(task);
	}

	@Test
	public void getCapability() throws Throwable{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
			TabularDataService service = TabularDataServiceFactory.getService();
			System.out.println(service.getCapability(1011));

		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void getTabularResource() throws Throwable{
		try{
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			System.out.println(TabularDataServiceFactory.getService().getTabularResource(new TabularResourceId(140)));

		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void getLastTable() throws Throwable{
		try{
			AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			System.out.println(TabularDataServiceFactory.getService().getLastTable(new TabularResourceId(130)));

		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}


	@Test
	public void importCodelistFromOldSystem() throws Exception{

		File file = new File("/home/lucio/codelists/codelists");
		String[] files = file.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});



		ScopeProvider.instance.set("/gcube/devsec");
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
		TabularDataService service = TabularDataServiceFactory.getService();

		System.out.println("near to read file");
		for (String newfile: files){
			try{
				TabularResource resource = service.createTabularResource();
				List<TabularResourceMetadata<?>> metadata = new ArrayList<>();
				metadata.add(new NameMetadata("name"));
				metadata.add(new AgencyMetadata("CNR"));
				metadata.add(new DescriptionMetadata("a tabular resource for test"));
				metadata.add(new RightsMetadata("none"));
				metadata.add(new ValidSinceMetadata(Calendar.getInstance()));
				metadata.add(new ValidUntilMetadata(Calendar.getInstance()));
				metadata.add(new LicenceMetadata(Licence.AttributionNonCommercial));
				resource.setAllMetadata(metadata);


				OperationDefinition csvop = service.getCapability(100l); 

				Map<String, Object> parameterInstances = new HashMap<String, Object>();
				parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/codelists/"+newfile);
				parameterInstances.put(SEPARATOR, ",");
				parameterInstances.put(ENCODING, "UTF-8");
				parameterInstances.put(HASHEADER, true);



				OperationExecution invocation = new OperationExecution(csvop.getOperationId(), parameterInstances);

				Task task = service.execute(invocation, resource.getId());

				System.out.println("executing for file  "+newfile);

				while (!task.getStatus().isFinal()){
					System.out.println(task.getStatus()+" "+task.getStartTime().getTime());
					Thread.sleep(1000);
				}

				System.out.println(task.getStatus()+" for file "+newfile);

				if (task.getStatus()== TaskStatus.FAILED && task.getErrorCause()!=null)
					task.getErrorCause().printStackTrace();

			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}

	@Test
	public void importFileTST() throws Exception{

		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
		TabularDataService service = TabularDataServiceFactory.getService();

		System.out.println("near to read file");
		TabularResource resource = service.createTabularResource();	
		System.out.println("cretion date: "+resource.getCreationDate().getTime());	

		resource.setAllMetadata(
				Arrays.asList((TabularResourceMetadata<?>)new NameMetadata("test licence lucio"),
						(TabularResourceMetadata<?>)new AgencyMetadata("CNR"),  
						(TabularResourceMetadata<?>)new ValidSinceMetadata(Calendar.getInstance())
				,new LicenceMetadata(Licence.AttributionNonCommercialNoDerivs)));

		OperationDefinition csvop = service.getCapability(100l); 

		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/Datafile.csv");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);

		OperationExecution invocation = new OperationExecution(csvop.getOperationId(), parameterInstances);

		Task task = service.execute(invocation, resource.getId());

		while (!task.getStatus().isFinal()){
			System.out.println(task.getProgress()+" "+task.getTaskJobs().get(0).getProgress());
			Thread.sleep(1000);
		}

		if (task.getStatus()== TaskStatus.FAILED && task.getErrorCause()!=null)
			task.getErrorCause().printStackTrace();


	}

	@Test
	public void rollbackTo() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
		TabularDataService service = TabularDataServiceFactory.getService();
		Task task =service.rollbackTo(new TabularResourceId(145), new HistoryStepId(3));
		Assert.assertNotNull(task);
		while (!task.getStatus().isFinal())
			Thread.sleep(1000);
		System.out.println(task);
	}

	@Test
	public void getRules() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
		TabularDataService service = TabularDataServiceFactory.getService();
		for (RuleDescription descr : service.getRules())
			System.out.println(descr);
	}

	@Test
	public void removeRule() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
		TabularDataService service = TabularDataServiceFactory.getService();
		service.removeRuleById(new RuleId(1l));
	}

	@Test
	public void executeQuery() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("giancarlo.panichi"));
		TabularDataService service = TabularDataServiceFactory.getService();
		String json = service.queryAsJson(history().build().getLastTable(130).getId(), new QueryPage(0, 1));
		System.out.println(json);
	}

	@Test
	public void getRulesByType() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
		TabularDataService service = TabularDataServiceFactory.getService();
		//service.saveColumnRule("rulteTextTest", "testTextTest", new Equals(new ColumnReferencePlaceholder(new TextType(), "pluto"), new TDText("pli")), new BaseColumnRuleType(new TextType()));
		System.out.println(service.getApplicableBaseColumnRules(new TextType().getClass()).get(0).getName());
	}


	@Test
	public void executeTypeChange() throws Exception{

		ScopeProvider.instance.set("/gcube/devsec");

		TabularDataService service = TabularDataServiceFactory.getService();
		TabularResource resource = service.createTabularResource();	
		OperationExecution importInvocation = new OperationExecution(100l, getCSVParameterInvocation());
		Task task = service.execute(importInvocation, resource.getId());

		while (!task.getStatus().isFinal()){
			System.out.println(task.getStatus()+" "+task.getStartTime().getTime());
			Thread.sleep(1000);
		}
		Assert.assertEquals(TaskStatus.SUCCEDED, task.getStatus());
		System.out.println(task.getResult().getPrimaryTable());

		byte[] b= new byte[2048];
		System.in.read(b);
		String input = new String(b).trim();

		OperationExecution codeInvocation = new OperationExecution(2004l,new HashMap<String, Object>());
		codeInvocation.setColumnId(input);

		Task taskCode = service.execute(codeInvocation, resource.getId());

		while (!taskCode.getStatus().isFinal()){
			System.out.println(taskCode.getStatus()+" "+taskCode.getStartTime().getTime());
			Thread.sleep(1000);
		}
		Assert.assertEquals(TaskStatus.SUCCEDED, taskCode.getStatus());

		OperationExecution attrInvocation = new OperationExecution(2001l,Collections.singletonMap("targetDataType", (Object)new TextType()));
		attrInvocation.setColumnId(input);

		Task taskAttr = service.execute(attrInvocation, resource.getId());

		while (!taskAttr.getStatus().isFinal()){
			System.out.println(taskAttr.getStatus()+" "+taskAttr.getStartTime().getTime());
			Thread.sleep(1000);
		}
		Assert.assertEquals(TaskStatus.SUCCEDED, taskAttr.getStatus());


		System.out.println("executed ");
	}

	private Map<String, Object> getCSVParameterInvocation(){
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/2006.csv");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		parameterInstances.put("fieldMask", Arrays.asList(true, true, false, false, false, true, true));
		return parameterInstances;
	}

	/*private Map<String, Object> getSDMXParameterInvocation(){
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put("registryBaseUrl","http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		parameterInstances.put("id","CL_SPECIES");
		parameterInstances.put("version","1.0");
		parameterInstances.put("agency","FAO");
		return parameterInstances;
	}*/

}
