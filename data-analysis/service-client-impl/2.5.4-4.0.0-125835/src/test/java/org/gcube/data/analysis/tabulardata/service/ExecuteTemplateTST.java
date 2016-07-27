package org.gcube.data.analysis.tabulardata.service;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Job;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExecuteTemplateTST {

	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec");
		AuthorizationProvider.instance.set(new AuthorizationToken("test.user"));
	}
	
	@Test
	public void execute() throws Exception{
		TabularDataService service = TabularDataServiceFactory.getService();
		TabularResource resource =  service.createTabularResource();
		resource.setMetadata(new NameMetadata("templateTest"));
		
		TemplateId templateId = service.saveTemplate("testTemplate", "a test template", "CNR", createTemplate());
		
		OperationDefinition csvop = service.getCapability(100l); 
		
		OperationExecution invocation = new OperationExecution(csvop.getOperationId(), getCSVParameterInvocation());
		
		Task task = service.execute(invocation, resource.getId());
		
		System.out.println("--------------------------------------- IMPORT ------------");
		
		while (!task.getStatus().isFinal()){
			System.out.println("-------------");
			System.out.println(task.getProgress());
			System.out.println(task.getStatus());
			System.out.println("-------------");
			Thread.sleep(2000);
		}
		
		System.out.println("-------------");
		System.out.println(task.getProgress());
		System.out.println(task.getStatus());
		System.out.println("-------------");
		
		Assert.assertSame(TaskStatus.SUCCEDED, task.getStatus());
		
		System.out.println("resource id is "+resource.getId());
		
		Task templateTask = service.applyTemplate(templateId, resource.getId());
		
		Assert.assertNotNull(templateTask);
		System.out.println("--------------------------------------- TEMPLATE ------------");
		while (!templateTask.getStatus().isFinal()){
			System.out.println("-------------");
			System.out.println(templateTask.getProgress());
			System.out.println(templateTask.getStatus());
			for (Job job : templateTask.getTaskJobs()){
				System.out.println("job ----- "+job.getProgress());
				System.out.println("    ----- "+job.getStatus());
			}
			System.out.println("-------------");
			Thread.sleep(5000);
		}
		
		System.out.println("-------------");
		System.out.println(templateTask.getProgress());
		System.out.println(templateTask.getStatus());
		for (Job job : templateTask.getTaskJobs()){
			System.out.println("job ----- "+job.getProgress());
			System.out.println("    ----- "+job.getStatus());
		}
		System.out.println("-------------");
		
	}
	
	
	private Template createTemplate(){
		TemplateColumn<TextType> firstColumn= new TemplateColumn<TextType>(ColumnCategory.CODE, TextType.class);
		TemplateColumn<TextType> secondColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, new LocaleReference("en"));
		TemplateColumn<TextType> thirdColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, new LocaleReference("fr"));
		TemplateColumn<TextType> forthColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, new LocaleReference("la"));
		TemplateColumn<TextType> fifthColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, new LocaleReference("es"));
		TemplateColumn<TextType> sixthColumn= new TemplateColumn<TextType>(ColumnCategory.ANNOTATION, TextType.class);
		
		return Template.create(TemplateCategory.CODELIST, firstColumn, secondColumn, thirdColumn, forthColumn, fifthColumn, sixthColumn );
	}

	private Map<String, Object> getCSVParameterInvocation(){
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, "https://dl.dropboxusercontent.com/u/27316518/speciesCode.csv");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		return parameterInstances;
	}
	
}
