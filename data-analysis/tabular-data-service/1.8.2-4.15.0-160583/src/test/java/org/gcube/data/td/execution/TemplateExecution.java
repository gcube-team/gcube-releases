package org.gcube.data.td.execution;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Utils;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair.AggregationFunction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.DeleteColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.GenerateTimeDimensionGroup;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.NormalizeTableAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TimeAggregationAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ValidateExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.export.csv.Constants;
import org.gcube.data.analysis.tabulardata.operation.importer.csv.CSVImportFactory;
import org.gcube.data.analysis.tabulardata.operation.validation.ColumnTypeCastValidatorFactory;
import org.gcube.data.analysis.tabulardata.templates.TemplateEngineFactory;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class TemplateExecution {

	@Inject
	CubeManager cm;

	@Inject
	TemplateEngineFactory templateEngineFactory;

	@Inject
	QueryManager queryManager;

	@Inject
	HistoryManager historyManager;

	@Inject
	CSVImportFactory importFactory;

	@Inject
	Factories factories;

	@Inject
	ColumnTypeCastValidatorFactory columnTypeCastValidator;

	@Inject
	TaskManager taskManager;

	@Inject
	TabularResourceManager trm;

	@Inject
	OperationManager operationManager;

	@Inject
	TemplateManager templateManager;


	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";


	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void getTable(){
		Table table =cm.getTable(new TableId(5));
		System.out.println(table);

	}

	@Test
	public void tstTemplateWithTqableRules() throws Throwable{
		Template template = createTemplateWithTableRule();
		executeTemplateOperation(template, getImportParameter("endsWith2Template.csv"));
	}
	
	@Test
	public void tstTemplateExecution() throws Throwable{
		Template template = createTemplateWithTimeDimensionGeenrator();
		executeTemplateOperation(template, getImportParameter("templateTest.csv"));
	}

	@Test
	public void groupAndAggregateStartingWithTimeDimensionColumn() throws Throwable{
		Template template = createTemplateWithTimeAggregation();
		executeTemplateOperation(template, getImportParameter("testDimFile.csv"));
	}

	
	@Test
	public void tstNormalizationTemplateExecution() throws Throwable{
		Template template = createTemplateforNormalization();
		executeTemplateOperation(template, getImportParameter("denormalizedDataset.csv"));
	}

	@Test
	public void tstNormalizationTemplate(){
		printStructure(createTemplateforNormalization());
	}

	@Test
	public void tsExecuteTemplateWithDiscard() throws Throwable{
		Template template = createTemplateWithDiscard();
		executeTemplateOperation(template, getImportParameter("endsWith2Template.csv"));
	}
	
	@Test
	public void tsExecuteTemplateWithExpression() throws Throwable{
		Template template = createTemplateWithExpressionOnAdd();
		executeTemplateOperation(template, getImportParameter("endsWith2Template.csv"));
	}
	
	private Template createTemplateWithTimeAggregation(){

		TemplateColumn<TextType> yearColumn = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(TextType.class).get(0).getId()));

		TemplateColumn<TextType> quarterColumn = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(TextType.class).get(0).getId()));

		TemplateColumn<IntegerType> column = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class , new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));

		Template template = Template.create(TemplateCategory.GENERIC,column, yearColumn, quarterColumn );

		Assert.assertEquals(template.getColumns(), template.getActualStructure());
	

		List<TemplateColumn<?>> groups = new ArrayList<>();
		groups.add(yearColumn);

		TimeAggregationAction taa = new TimeAggregationAction(column, PeriodType.YEAR, groups, new AggregationPair(quarterColumn, AggregationFunction.COUNT));

		template.addAction(taa);
		
		TemplateColumn<TextType> toAdd = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
		
		
		
		template.addAction(new AddColumnAction(toAdd, new Concat(new ColumnReferencePlaceholder(new TextType(), yearColumn.getId(), "label"),new Concat(new TDText("[[[]]]"), new ColumnReferencePlaceholder(new TextType(), quarterColumn.getId(),"label")))));
		
		template.setOnErrorAction(OnRowErrorAction.ASK);

		return template;
	}


	private Template createTemplateWithTimeDimensionGeenrator(){

		TemplateColumn<IntegerType> yearColumn = new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));

		TemplateColumn<IntegerType> quarterColumn = new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));
		//quarterColumn.addExpression(new NotEquals(new ColumnReferencePlaceholder(new TextType(),"column1" ,"label"), new TDText("lucio")));
		TemplateColumn<TextType> attColumn = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(TextType.class).get(0).getId()));

		TemplateColumn<NumericType> column = new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class , new FormatReference(DataTypeFormats.getFormatsPerDataType(NumericType.class).get(0).getId()));

		Template template = Template.create(TemplateCategory.GENERIC, yearColumn, quarterColumn, attColumn, column);

		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		GenerateTimeDimensionGroup gtdg = new GenerateTimeDimensionGroup(yearColumn, quarterColumn, false, "newLabel");

		template.addAction(gtdg);

		Assert.assertNotEquals(template.getColumns(), template.getActualStructure());

		template.setOnErrorAction(OnRowErrorAction.ASK);

		return template;
	}

	private Template createTemplateforNormalization(){

		TemplateColumn<TextType> country = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE);

		TemplateColumn<IntegerType> first = new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class, Utils.getDefaultFormatReferenceForType(IntegerType.class));

		TemplateColumn<NumericType> second = new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class, Utils.getDefaultFormatReferenceForType(NumericType.class));

		TemplateColumn<IntegerType> third = new TemplateColumn<IntegerType>(ColumnCategory.ATTRIBUTE, IntegerType.class, Utils.getDefaultFormatReferenceForType(IntegerType.class));

		Template template = Template.create(TemplateCategory.GENERIC, country, first, second, third);

		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		//AddColumnAction acolAction = new AddColumnAction(column, initialValue)

		//TimeAggregationAction taa = new TimeAggregationAction(column, periodType, groupColumns, aggregationPairs)
		NormalizeTableAction norm = new NormalizeTableAction(Arrays.asList(first, second, third), "norm" , "quantity");

		template.addAction(norm);

		DeleteColumnAction rca = new DeleteColumnAction(norm.getCreatedColumns().get(0));

		template.addAction(rca);

		Assert.assertNotEquals(template.getColumns(), template.getActualStructure());

		template.setOnErrorAction(OnRowErrorAction.ASK);

		return template;
	}

	public void executeTemplateOperation(Template template, Map<String, Object> importParameters) throws Throwable{

		TabularResource tr = runImport(importParameters);				

		long templateId = templateManager.saveTemplate("test", "test", "CNR", template);

		try{
			TaskInfo info = templateManager.apply(templateId, tr.getId());

			while (!info.getStatus().isFinal()){
				printTaskInfo(info);
				Thread.sleep(2000);
				info  = taskManager.get(new String[]{info.getIdentifier()}).get(0);
			}

			printTaskInfo(info);

			if (info.getStatus()==TaskStatus.FAILED)
				throw info.getErrorCause();

			Table table = historyManager.getLastTable(tr.getId());
			System.out.println(table);

			if (table.contains(DatasetViewTableMetadata.class))
				System.out.println(queryManager.queryAsJson(table.getMetadata(DatasetViewTableMetadata.class).getTargetDatasetViewTableId().getValue(), null, null, null, null, null));
			else System.out.println(queryManager.queryAsJson(table.getId().getValue(), null, null, null, null, null));


		}finally{
			trm.remove(tr.getId());
			templateManager.removeTemplate(templateId);
		}
	}

	private Template createTemplateWithExpressionOnAdd(){
		
		TemplateColumn<TextType> col = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
				
		Template template = Template.create(TemplateCategory.GENERIC, col);
		
		TemplateColumn<TextType> toAdd1 = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
		template.addAction(new AddColumnAction(toAdd1, new Concat(new ColumnReferencePlaceholder(new TextType(), col.getId(),"label"), new TDText("-concat"))));
		
		TemplateColumn<TextType> toAdd = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
		template.addAction(new AddColumnAction(toAdd, new ColumnReferencePlaceholder(new TextType(), col.getId(),"label")));
		
		return template;
				
	}
	
	private Template createTemplateWithTableRule(){
		
		TemplateColumn<TextType> col = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
				
		Template template = Template.create(TemplateCategory.GENERIC, col);
		
		template.addAction(new ValidateExpressionAction("rest", new Equals(new ColumnReferencePlaceholder(new TextType(), col.getId(),"label"), new TDText("pippo"))));
		
		return template;
				
	}

	private Template createTemplateWithDiscard(){
		
		TemplateColumn<TextType> col = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, Utils.TEXT_FORMAT_REFERENCE); 
		col.addExpression(new TextEndsWith(new ColumnReferencePlaceholder(new TextType(), col.getId(),"label"), new TDText("1")));
		
		Template template = Template.create(TemplateCategory.GENERIC, col);
		template.setOnErrorAction(OnRowErrorAction.DISCARD);
		return template;
				
	}


	private TabularResource runImport(Map<String, Object> importParameter) throws Exception{
		TabularResource tr = trm.createTabularResource(TabularResourceType.STANDARD);

		OperationExecution oe = new OperationExecution(100, importParameter);

		ExecuteRequest request = new ExecuteRequest(tr.getId(), oe);
		TaskInfo info = null;
		try {
			info = operationManager.execute(request);
		} catch (NoSuchTabularResourceException | OperationNotFoundException
				| InternalSecurityException e) {
			e.printStackTrace();
		}

		Thread.sleep(200);

		while (!info.getStatus().isFinal())
			try {
				info = taskManager.get(new String[]{info.getIdentifier()}).get(0);
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		printTaskInfo(info);
		return tr;

	}

	private Map<String, Object> getImportParameter(String templateFile) {

		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFile);
		IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "TDM", AccessType.SHARED).getClient();
		String id = client.put(true).LFile(stream).RFile("/test/test.csv");
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(URL, id);
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		return parameterInstances;	
	}

	private void printTaskInfo(TaskInfo info){
		System.out.println("--------TASK INFO ----------");
		System.out.println(info.getStatus());
		for (TaskStep step : info.getTaskSteps()){
			System.out.println("task: "+step.toString());
			if (!step.getValidations().isEmpty())
				for (ValidationDescriptor validation: step.getValidations())
					System.out.println("validation: "+ validation);

		}
		System.out.println("----------------------------");
	}

	private void printStructure(Template template){
		System.out.println("--------TemplateStructure ----------");

		for (TemplateColumn<?> col : template.getActualStructure())
			System.out.println(String.format("column %s : %s %s ", col.getLabel(), col.getColumnType().name(), col.getValueType().getSimpleName()));
		System.out.println("----------------------------");
	}

}
