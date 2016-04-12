package org.gcube.data.analysis.tabulardata.expression;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.AggregationExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.ArithmeticExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.ComparableExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.LogicalExpression;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.junit.Test;

public class ExpressionExample {
	
	@Test
	public void testCreation() throws MalformedExpressionException{
		
		System.out.println(getExample());
		Expression expr = testCase1();
		System.out.println(expr);
		expr.validate();
		System.out.println(expr.getLeavesByType(ColumnReference.class));
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotValidGeometry(){
		TDGeometry geo = new TDGeometry("PINTA");
		System.out.println(geo.getReturnedDataType());
	}

	@Test
	public void testValidGeometry(){
		TDGeometry geo = new TDGeometry("POINT(45 56)");
		System.out.println(geo.getReturnedDataType());
	}
	
	@Test
	public void testCategories(){
		System.out.println("Aggregations : "+asOpList(Expression.getExpressionsByCategory(AggregationExpression.class)));
		System.out.println("Arithmetic : "+asOpList(Expression.getExpressionsByCategory(ArithmeticExpression.class)));
		System.out.println("Text : "+asOpList(Expression.getExpressionsByCategory(ComparableExpression.class)));
		System.out.println("Logical : "+asOpList(Expression.getExpressionsByCategory(LogicalExpression.class)));
	}
	
	private List<Operator> asOpList(List<Class<? extends Expression>> toCheck){
		ArrayList<Operator> toReturn=new ArrayList<>();
		for(Class<? extends Expression> clazz:toCheck){
			toReturn.add(Operator.getByExpressionClass((Class<? extends CompositeExpression>) clazz));
		}
		return toReturn;
	}
	
	@Test
	public void testTypeChecks() throws MalformedExpressionException, NotEvaluableDataTypeException{
		Expression expr=getExample();
		System.out.println("Returned data type : "+expr.getReturnedDataType().getName());		
	}
	
	
	@Test(expected = MalformedExpressionException.class)
	public void testDataTypeConstraint() throws MalformedExpressionException, NotEvaluableDataTypeException{
		new And(Arrays.asList(new Expression[]{				
		new TDInteger(1),
		new TDBoolean(false)})).validate();
	}
	
	
	public static Expression testCase1() {
		ColumnReference ref=new ColumnReference(new TableId(44), new ColumnLocalId("65412728-dcbf-4045-80d8-dc93693ec931"), new NumericType());
		GreaterThan gt=new GreaterThan(ref, new TDNumeric(0.0d));
		LessThan lt=new LessThan(ref, new TDNumeric(1.1d));
		Between bet=new Between(ref, new TDNumeric(0d), new TDNumeric(1d));
		return new And(gt,lt,bet);
	}

	public static Expression getExample() throws MalformedExpressionException{
		
		
		
		
		
		
		
		//Leaves
		ColumnReference targetColumnReference = new ColumnReference(new TableId(10), new ColumnLocalId("test"), new BooleanType());
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
		Expression columnIsIn = new ValueIsIn(targetColumnReference, list);

		// Text expression
		
		
		
		Expression textContains = new TextContains(placeholder, new TDText("test"));
//		Expression textEquals = new TextEquals(createColumnReference(), new TDText("test"));
		Expression textMatchRegexp = new TextMatchSQLRegexp(targetColumnReference, new TDText("[a-b]*"));
		Expression textReplaceRegex=new TextReplaceMatchingRegex(targetColumnReference, new TDText("[a-z]*"),new TDText("bla!"));
		Expression valueIsIn=new ValueIsIn(placeholder, new ExternalReferenceExpression(targetColumnReference, textContains));
		Expression valueIsInSet=new ValueIsIn(placeholder,list);
		
		// Comparable
		Expression equals = new Equals(targetColumnReference, new TDDate(new Date()));
		Expression greaterThan = new Equals(targetColumnReference, new TDInteger(5));
		Expression lessThan = new LessThan(targetColumnReference, new TDNumeric(5.1d));
		Expression notEquals = new NotEquals(targetColumnReference, new TDBoolean(false));

		Expression between = new Between(addition,targetColumnReference,placeholder);
		
		// Composite
		Expression and = new And(textContains, isNull, columnIsIn, textContains,isNotNull,between,textReplaceRegex);
		
		
		Expression or = new Or(and, textMatchRegexp, equals, greaterThan, lessThan, notEquals, valueIsIn,valueIsInSet);
		Expression not= new Not(or);
		not.validate();
		return not;
	}
	
	@Test
	public void operatorsPerDataType() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		ArrayList<DataType> existingDataTypes=new ArrayList<>();
		existingDataTypes.add(new IntegerType());
		existingDataTypes.add(new NumericType());
		existingDataTypes.add(new TextType());
		existingDataTypes.add(new BooleanType());
		existingDataTypes.add(new GeometryType());
		
		for(DataType type:existingDataTypes){
			System.out.println("Appliable operators per "+type.getName());
			System.out.println(Expression.getAppliableOperators(type));
		}
	}
	
	
}
