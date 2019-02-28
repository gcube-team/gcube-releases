package org.gcube.data.analysis.tabulardata.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ColumnMetadataParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocaleParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextChoiceParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.LocalizedTextParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.RegexpStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetColumnParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TargetTableParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class OperationTest {

	@Inject
	private CubeManager cubeManager; 
	
	@Inject
	private Utils utils;
	
	//// PARAMS 
	
	static BooleanParameter bool=new BooleanParameter("bool", "myBool", "a boolean", Cardinality.ONE);
	static ColumnMetadataParameter meta=new ColumnMetadataParameter("meta", "meta", "meta", Cardinality.ONE); 
	static DataTypeParameter dataType=new DataTypeParameter("dataType", "dataType", "theDataType", Cardinality.OPTIONAL);
	static ExpressionParameter expr=new ExpressionParameter("expr", "myExpr", "simple Expression", new Cardinality(0, 100));
	static IntegerParameter integer=new IntegerParameter("int", "int", "an integer", new Cardinality(1, 100));
	static LocaleParameter locale=new LocaleParameter("locale", "locale", "locale", new Cardinality(2, 100));
	static LocalizedTextChoiceParameter textChoice=new LocalizedTextChoiceParameter("choice", "choice", "choice", Cardinality.ONE, Arrays.asList(new ImmutableLocalizedText[]{new ImmutableLocalizedText("value")}));
	static LocalizedTextParameter localText=new LocalizedTextParameter("localized", "localized", "localized", Cardinality.ONE);
	static MultivaluedStringParameter multi=new MultivaluedStringParameter("multi", "multi", "multi", Cardinality.ONE, Arrays.asList(new String[]{"value"}));
	static RegexpStringParameter regex=new RegexpStringParameter("regex", "regex", "regex", Cardinality.OPTIONAL, "ab");
	static SimpleStringParameter simple=new SimpleStringParameter("simpleString", "myString", "myDescription", new Cardinality(1, 1));
	static TargetColumnParameter col=new TargetColumnParameter("col", "col", "target", Cardinality.ONE);
	static TargetTableParameter tab=new TargetTableParameter("tab", "tab", "target", Cardinality.ONE);
	static TDTypeValueParameter type=new TDTypeValueParameter("tdType", "tdtype", "descr", Cardinality.ONE);
	
	static CompositeParameter compo=new CompositeParameter("compo", "compo", "compo", new Cardinality(1,5), Arrays.asList(new Parameter[]{
			bool,simple,col,tab
	}));
	static MapParameter map=new MapParameter("map", "map", "map", Cardinality.ONE, String.class, Object.class);
	
	
	private Table table;
	
	@Before
	public void init(){
		table=utils.createTable();
	}
	
	private Map<String,Object> getParameters(){
		
		
		Column column=table.getColumnsExceptTypes(IdColumnType.class).get(0);
		ColumnReference ref=table.getColumnReference(column);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(bool.getIdentifier(),new Boolean(false));
		parameters.put(meta.getIdentifier(), new DataLocaleMetadata(Locales.getDefaultLocale()));
		
		parameters.put(dataType.getIdentifier(),new IntegerType());
		parameters.put(expr.getIdentifier(),Arrays.asList(new Expression[]{
				ref,
				new IsNotNull(ref)
		}));
		parameters.put(integer.getIdentifier(),new Integer(10));
		parameters.put(locale.getIdentifier(),Arrays.asList(new Locale[]{Locale.CANADA,Locale.CHINA}));
		parameters.put(textChoice.getIdentifier(),new ImmutableLocalizedText("value"));
		parameters.put(localText.getIdentifier(),new ImmutableLocalizedText("value"));
		parameters.put(multi.getIdentifier(),"value");
		parameters.put(regex.getIdentifier(),"ab");
		parameters.put(simple.getIdentifier(),"asdglhker");
		parameters.put(col.getIdentifier(),ref);
		parameters.put(tab.getIdentifier(),table.getId());
		parameters.put(type.getIdentifier(),new TDBoolean(false));
		
		Map<String, Object> firstCompoMap = new HashMap<String, Object>();
		firstCompoMap.put(bool.getIdentifier(),new Boolean(false));
		firstCompoMap.put(simple.getIdentifier(),"asdglhker");
		firstCompoMap.put(col.getIdentifier(), ref);
		firstCompoMap.put(tab.getIdentifier(), table.getId());
		
		Map<String, Object> secondCompoMap = new HashMap<String, Object>();
		secondCompoMap.put(bool.getIdentifier(),new Boolean(false));
		secondCompoMap.put(simple.getIdentifier(),"asdglhker");
		secondCompoMap.put(col.getIdentifier(), ref);
		secondCompoMap.put(tab.getIdentifier(), table.getId());
		
		ArrayList<Map<String,Object>> compoInstances=new ArrayList<Map<String,Object>>();
		compoInstances.add(firstCompoMap);
		compoInstances.add(secondCompoMap);
		
		parameters.put(compo.getIdentifier(), compoInstances);
		
		Map<String,Object> myMap=new HashMap<String,Object>();
		myMap.put("string",new String("bla"));
		myMap.put("integer",new Integer(10));
		myMap.put("object",new TDText("bla"));
		parameters.put(map.getIdentifier(), myMap);
		return parameters;
	}
	
	
	private static final Logger log = LoggerFactory.getLogger(OperationTest.FakeDataWorker.class);

	FakeWorkerFactory factory = new FakeWorkerFactory();

	@Test
	public void testGetDescriptor() {
		Assert.assertNotNull(factory.getOperationDescriptor());
	}

	@Test
	public void testCreator() {
		OperationDescriptor descriptor = factory.getOperationDescriptor();
		OperationInvocation invocation = InvocationCreator.getCreator(descriptor).setParameters(getParameters())
				.setTargetTable(table.getId()).create();
	}
	@Test
	public void testInvocation() throws InvalidInvocationException{
		OperationDescriptor descriptor = factory.getOperationDescriptor();		
//		OperationInvocation invocation = InvocationCreator.getCreator(descriptor).setParameters(getParameters())
//				.setTargetTable(new TableId(table.getId().getValue()+1)).setToUpdateReferredTable(table.getId()).create();
		OperationInvocation invocation = InvocationCreator.getCreator(descriptor).setParameters(getParameters())
				.setTargetTable(table.getId()).create();
		
		factory.createWorker(invocation);
	}
	
	
	public class FakeDataWorker extends DataWorker {

		public FakeDataWorker(OperationInvocation sourceInvocation) {
			super(sourceInvocation);
		}

		@Override
		protected WorkerResult execute() throws WorkerException {
			log.info("Doing something");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateProgress(0.3f,"Doing something");
			log.info("Doing something");
			updateProgress(0.6f,"Doing something");
			log.info("Doing something");
			return new ImmutableWorkerResult(new Table(new DatasetTableType()));
		}

	}

	
	
	
	public class FakeWorkerFactory extends TableTransformationWorkerFactory {

		private List<Parameter> parameters = new ArrayList<Parameter>();

		
		@Override
		public String describeInvocation(
				OperationInvocation toDescribeInvocation)
				throws InvalidInvocationException {
			return "";
		}
		
		
		public FakeWorkerFactory() {
			parameters.add(bool);
			parameters.add(dataType);
			parameters.add(expr);
			parameters.add(integer);
			parameters.add(locale);
			parameters.add(textChoice);
			parameters.add(localText);
			parameters.add(multi);
			parameters.add(regex);
			parameters.add(simple);
			parameters.add(col);
			parameters.add(tab);
			parameters.add(type);
			parameters.add(compo);
			parameters.add(map);
		}
		
		public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
			performBaseChecks(invocation,cubeManager);
			OperationHelper.getParameter(bool, invocation);
			return new FakeDataWorker(invocation);
		}

		@Override
		protected String getOperationName() {
			return "Sample Operation";
		}

		@Override
		protected String getOperationDescription() {
			return "Sample Operation Description";
		}

		@Override
		protected List<Parameter> getParameters() {
			return parameters;
		}

	}
	
	public class TestObserver implements Observer {
		
		public Worker worker = null;

		public void update(Observable o, Object arg) {
			if (o instanceof Worker) worker = (Worker) o;
			log.info("Worker has been updated: " + worker);
		}

	}

	

}
