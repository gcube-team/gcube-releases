package org.gcube.data.analysis.tabulardata.expression.evaluator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;


import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Division;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Modulus;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Multiplication;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Subtraction;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotGreater;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotLess;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Levenshtein;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Lower;
import org.gcube.data.analysis.tabulardata.expression.composite.text.MD5;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Similarity;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Soundex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Trim;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Upper;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TestEvaluator {

	private static Table testTable;

	private static ReferenceResolver evaluatorUtil;

	private static SQLExpressionEvaluatorFactory evaluatorFactory;

	@BeforeClass
	public static void beforeClass() {
		testTable = ResourceGenerator.createTestTable();
		evaluatorUtil = new ReferenceResolver() {

			@Override
			public Table getTable(ColumnReference columnRef) {
				return testTable;
			}

			@Override
			public Column getColumn(ColumnReference columnRef) {
				return testTable.getColumnById(columnRef.getColumnId());
			}

		};
		evaluatorFactory = new SQLExpressionEvaluatorFactory(evaluatorUtil);
	}

	@Test
	public void test() throws EvaluatorException, MalformedExpressionException {
		Expression expression = createExpression();

		String result = evaluatorFactory.getEvaluator(expression).evaluate();
		System.out.println(result);
		
		Expression in=new ValueIsIn(new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId()), new ExternalReferenceExpression(new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId()), new TDBoolean(true)));
		System.out.println(evaluatorFactory.getEvaluator(in).evaluate());
		
		// test for substring by index
		Expression sourceString=new TDText("Test");
		Expression subString=new TDText(" ");
		Expression substringTest=new SubstringByIndex(sourceString, new SubstringPosition(subString, sourceString), new Addition(new Length(sourceString), new TDInteger(1)));
		System.err.println(evaluatorFactory.getEvaluator(substringTest).evaluate());
		
		// test levenshtein & similarity
		
		
		
		
	}
	
	
	@Test
	public void testExpression(){
		System.out.println(evaluatorFactory.getEvaluator(new Levenshtein(new TDText("bla"), new TDText("gna"))).evaluate());
		
	}
	
	@Test
	public void testExpressionCoverage() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		// arguments vars
		ColumnReference targetColumnReference = new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId());
		
		ConstantList list=new ConstantList(Arrays.asList(new TDTypeValue[]{
				new TDText("value1"),
				new TDText("value2"),
				new TDText("value3"),
				new TDText("value4"),
		}));
		
		for(Class<? extends Expression> exprClass:availableImplementations){
			Expression instance=null;
			
			if(ValueIsIn.class.isAssignableFrom(exprClass)){
				instance=new ValueIsIn(targetColumnReference, list);
			}else if(Between.class.isAssignableFrom(exprClass)){
				instance=new Between(targetColumnReference, targetColumnReference, targetColumnReference);
			}else if(Cast.class.isAssignableFrom(exprClass)){
				ColumnReference typed=new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId(), new TextType());
				instance=new Cast(typed,new TextType());
			}else if(UnaryExpression.class.isAssignableFrom(exprClass)){
				instance=exprClass.getConstructor(Expression.class).newInstance(targetColumnReference);				
			}else if(BinaryExpression.class.isAssignableFrom(exprClass)){
				instance=exprClass.getConstructor(Expression.class,Expression.class).newInstance(targetColumnReference,targetColumnReference);				
			}else if(MultipleArgumentsExpression.class.isAssignableFrom(exprClass)){
				instance=exprClass.getConstructor(List.class).newInstance(
						Arrays.asList(new Expression[]{(Expression)targetColumnReference,targetColumnReference}));
			}else {
				// special handling here
			}
			System.out.println(instance);
			if(instance==null) System.err.println("NOT CHECKED : "+exprClass);
			else System.out.println(evaluatorFactory.getEvaluator(instance).evaluate());						
		}		
	}
	
	@Test
	public void testBetween(){	
		ColumnReference targetColumnReference = new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId());
		Between bet=new Between(targetColumnReference,new TDText("left"), new TDText("Right"));
		
		System.out.println(evaluatorFactory.getEvaluator(bet).evaluate());
	}
	

	private Expression createExpression() throws MalformedExpressionException {

		ColumnReference targetColumnReference = new ColumnReference(testTable.getId(), testTable.getColumns().get(0)
				.getLocalId());
		ColumnReference anotherTargetColumnReference = new ColumnReference(testTable.getId(), testTable.getColumns()
				.get(0).getLocalId());
		
		ColumnReferencePlaceholder placeholder = new ColumnReferencePlaceholder(new BooleanType(), "myCol", "My Column");
		ConstantList list=new ConstantList(Arrays.asList(new TDTypeValue[]{
				new TDText("value1"),
				new TDText("value2"),
				new TDText("value3"),
				new TDText("value4"),
		}));
		Addition addition=new Addition(targetColumnReference,placeholder);
		
		
		
		// Other expression
		
		
		Expression isNull = new IsNull(targetColumnReference);
		Expression isNotNull = new IsNotNull(targetColumnReference);
		Expression columnIsIn = new ValueIsIn(targetColumnReference, new ConstantList(Arrays.asList(new TDTypeValue[]{
				new TDInteger(10)
		})));

		// Text expression
		Expression textContains = new TextContains(placeholder, new Concat(new TDText("test"),new MD5(new TDText("-ed"))));
//				Expression textEquals = new TextEquals(createColumnReference(), new TDText("test"));
				Expression textMatchRegexp = new TextMatchSQLRegexp(targetColumnReference, new TDText("[a-b]*"));
				Expression substringByRegex= new SubstringByRegex(targetColumnReference, new Trim(new Upper(new Lower(new TDText("[a-b]")))));
				Expression substringByIndex= new SubstringByIndex(targetColumnReference, new SubstringPosition(targetColumnReference, substringByRegex), new Length(substringByRegex));
				
				Expression regexpSubst = new TextReplaceMatchingRegex(targetColumnReference, new TDText("[a-b]*"), new TDText("MATCHED"));
				Expression regexpNull=new IsNotNull(regexpSubst);
				Expression valueIsIn=new ValueIsIn(placeholder, new ExternalReferenceExpression(targetColumnReference, textContains));
				Expression valueIsInSet=new ValueIsIn(placeholder,list);
				
				// Comparable
				Expression equals = new Equals(targetColumnReference, new TDDate(new Date()));
				Expression greaterThan = new Equals(targetColumnReference, new TDInteger(5));
				Expression lessThan = new LessThan(targetColumnReference, new TDNumeric(5.1d));
				Expression notEquals = new NotEquals(targetColumnReference, new TDBoolean(false));

				Expression between = new Between(addition,new TDInteger(5),new TDNumeric(0.5d));
				
				
				
				
				
				// Composite
				Expression and = new And(new Cast(new TDText("true"),new BooleanType()),textContains, isNull, columnIsIn, textContains,isNotNull,between,regexpNull,new IsNotNull(substringByIndex));
		
		
		Expression or = new Or(and, textMatchRegexp, equals, greaterThan, lessThan, notEquals, valueIsIn,valueIsInSet);
		Expression not= new Not(or);
		not.validate();
		return not;
	}

	private static class ResourceGenerator {

		private static Table createTestTable() {
			Table t = new Table(new DatasetTableType());
			t.setId(new TableId(50));
			t.setMetadata(new ImportMetadata("SDMX", "http://localhost:8080/FusionRegistry", new Date()));

			t.setName("tablename");
			t.setColumns(createColumns());
			return t;
		}

		private static List<Column> createColumns() {
			List<Column> columns = Lists.newArrayList();
			columns.add(createCodeColumn());
			columns.add(createCodeNameColumn());
			columns.add(createAnnotationGeometryColumn());
			columns.add(createAnnotationBooleanColumn());
			columns.add(createDimensionColumn());
			return columns;
		}

		private static Column createCodeColumn() {
			Column column = new CodeColumnFactory().createDefault();
			column.setName(generateRandomString());
			return column;
		}

		private static Column createCodeNameColumn() {
			Column column = new CodeNameColumnFactory().create("en");
			column.setName(generateRandomString());
			return column;
		}

		private static Column createAnnotationGeometryColumn() {
			Column column = new AnnotationColumnFactory().createDefault();
			column.setName(generateRandomString());
			return column;
		}

		private static Column createAnnotationBooleanColumn() {
			Column column = new AnnotationColumnFactory().createDefault();
			column.setName(generateRandomString());
			return column;
		}

		private static Column createDimensionColumn() {
			ColumnRelationship cr = new ImmutableColumnRelationship(new TableId(50), new ColumnLocalId("test"));
			Column column = new DimensionColumnFactory().create(new ImmutableLocalizedText("MyDimension", "en"), cr);
			column.setName(generateRandomString());
			return column;
		}

		private static String generateRandomString() {
			int length=6;
				String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
				StringBuilder salt = new StringBuilder();
				Random rnd = new Random();
				while (salt.length() < length) {
					int index = (int) (rnd.nextFloat() * SALTCHARS.length());
					salt.append(SALTCHARS.charAt(index));
				}
				String saltStr = salt.toString();
				return saltStr;
		}

	}

	
	// All implementations
	
	private static final List<Class<? extends Expression>> availableImplementations;


	static{
		availableImplementations=new ArrayList<>();		
		availableImplementations.add(Addition.class);
		availableImplementations.add(Division.class);
		availableImplementations.add(Equals.class);
		availableImplementations.add(GreaterOrEquals.class);
		availableImplementations.add(GreaterThan.class);
		availableImplementations.add(LessOrEquals.class);
		availableImplementations.add(LessThan.class);
		availableImplementations.add(Modulus.class);
		availableImplementations.add(Multiplication.class);
		availableImplementations.add(NotEquals.class);
		availableImplementations.add(NotGreater.class);
		availableImplementations.add(NotLess.class);
		availableImplementations.add(Subtraction.class);
		availableImplementations.add(TextBeginsWith.class);
		availableImplementations.add(TextContains.class);
		availableImplementations.add(TextEndsWith.class);
		availableImplementations.add(TextMatchSQLRegexp.class);
		availableImplementations.add(TextReplaceMatchingRegex.class);
		availableImplementations.add(SubstringByRegex.class);
		availableImplementations.add(SubstringByIndex.class);
		availableImplementations.add(SubstringPosition.class);
		availableImplementations.add(Trim.class);
		availableImplementations.add(Length.class);
		availableImplementations.add(Upper.class);
		availableImplementations.add(Lower.class);
		availableImplementations.add(Concat.class);
		availableImplementations.add(Soundex.class);
		availableImplementations.add(Similarity.class);
		availableImplementations.add(Levenshtein.class);
		
		
		availableImplementations.add(MD5.class);
		availableImplementations.add(ValueIsIn.class);
		availableImplementations.add(ExternalReferenceExpression.class);

		availableImplementations.add(Between.class);

		availableImplementations.add(And.class); 
		availableImplementations.add(Or.class);

		availableImplementations.add(IsNotNull.class);
		availableImplementations.add(IsNull.class);
		availableImplementations.add(Not.class);

		availableImplementations.add(ColumnReference.class);	
		availableImplementations.add(ConstantList.class);
		//availableImplementations.add(TypedColumnReference.class);
		availableImplementations.add(ColumnReferencePlaceholder.class);

		availableImplementations.add(TDBoolean.class);
		availableImplementations.add(TDDate.class);
		availableImplementations.add(TDInteger.class);
		availableImplementations.add(TDNumeric.class);
		availableImplementations.add(TDText.class);
		availableImplementations.add(Cast.class);
		availableImplementations.add(Avg.class);
		availableImplementations.add(Count.class);
		availableImplementations.add(Max.class);
		availableImplementations.add(Min.class);
		availableImplementations.add(Sum.class);
		availableImplementations.add(ST_Extent.class);
	}
	
}
