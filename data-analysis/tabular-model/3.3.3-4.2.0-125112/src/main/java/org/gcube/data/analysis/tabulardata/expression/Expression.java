package org.gcube.data.analysis.tabulardata.expression;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
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
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Exponentiation;
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
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Levenshtein;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Lower;
import org.gcube.data.analysis.tabulardata.expression.composite.text.MD5;
import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Similarity;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Soundex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Trim;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Upper;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;

import com.thoughtworks.xstream.XStream;

@XmlSeeAlso({ 
	//Binary
	Addition.class,
	Division.class,
	Exponentiation.class,
	Equals.class,
	GreaterOrEquals.class,
	GreaterThan.class,
	LessOrEquals.class,
	LessThan.class,
	Modulus.class,
	Multiplication.class,
	NotEquals.class,
	NotGreater.class,
	NotLess.class,
	Subtraction.class,
	TextBeginsWith.class,
	TextContains.class,
	TextEndsWith.class,
	TextMatchSQLRegexp.class,
	TextMatchPosixRegexp.class,
	TextReplaceMatchingRegex.class,
	SubstringByRegex.class,
	SubstringByIndex.class,
	SubstringPosition.class,
	RepeatText.class,
	Trim.class,
	Length.class,
	Upper.class,
	Lower.class,
	Concat.class,
	MD5.class,
	Soundex.class,
	Similarity.class,
	Levenshtein.class,
	
	
	
	ValueIsIn.class,

	ExternalReferenceExpression.class,

	Between.class,

	//Multiple	
	And.class, 
	Or.class,
	
	Case.class,
	
	//Unary
	IsNotNull.class,
	IsNull.class,
	Not.class,

	//Leaf
	ColumnReference.class,	
	ConstantList.class,
	//TypedColumnReference.class,
	ColumnReferencePlaceholder.class,

	//TDTypes
	TDBoolean.class,
	TDDate.class,
	TDInteger.class,
	TDNumeric.class,
	TDText.class,
	TDGeometry.class,

	Cast.class,


	//Aggregation
	Avg.class,
	Count.class,
	Max.class,
	Min.class,
	Sum.class,	
	ST_Extent.class

})
public abstract class Expression implements Serializable{

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
	
	
	
	
	private static XStream stream= new XStream();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Validates correctness of the Expression
	 * 
	 * @throws MalformedExpressionException 
	 */
	public abstract void validate() throws MalformedExpressionException;

	/**
	 * Validates the returned data type of the Expression. Invalid expression may return a wrong datatype
	 * 
	 * @return
	 * @throws MalformedExpressionException
	 */
	public abstract DataType getReturnedDataType()throws NotEvaluableDataTypeException;

	@Override
	public Object clone(){
		/*stream.registerConverter(new SingleValueConverter() {
			
			@Override
			public boolean canConvert(Class type) {
				if (type.equals(TypedColumnReference.class)) return true;
				return false;
			}
			
			@Override
			public String toString(Object obj) {
				TypedColumnReference tcr = (TypedColumnReference) obj;
				return tcr.getTableId().getValue()+"<DIVIDE>"+tcr.getColumnId().getValue()+"<DIVIDE>"+tcr.getReturnedDataType().toString();
			}
			
			@Override
			public Object fromString(String str) {
				String[] splitted = str.split("<DIVIDE>");
				return new ColumnReference(new TableId(Long.parseLong(splitted[0])), new ColumnLocalId(splitted[1]));
			}
		});*/
		return stream.fromXML(stream.toXML(this));
	}


	public static final List<Operator> getAppliableOperators(DataType type) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		ArrayList<Operator> toReturn=new ArrayList<>();		
		for(Class<? extends Expression> clazz: availableImplementations){
			if(CompositeExpression.class.isAssignableFrom(clazz)){
				if(ValueIsIn.class.isAssignableFrom(clazz)){
					ValueIsIn in=new ValueIsIn(null, null);
					if(in.allowedLeftDataTypes().contains(type.getClass())||in.allowedRightDataTypes().contains(type.getClass()))
						toReturn.add(in.getOperator());
//				}else if(Between.class.isAssignableFrom(clazz)){
//					Between bet=new Between(null, null);
//					if(bet.allowedLeftDataTypes().contains(type.getClass())||bet.allowedRightDataTypes().contains(type.getClass()))
//						toReturn.add(bet.getOperator());
				}else if(Cast.class.isAssignableFrom(clazz)){
					// do nothing
				}else if(UnaryExpression.class.isAssignableFrom(clazz)){
					UnaryExpression unary=(UnaryExpression) clazz.getConstructor(Expression.class).newInstance((Expression)null);	
					if(unary.allowedDataTypes().contains(type.getClass()))
					toReturn.add(unary.getOperator());
				}else if(BinaryExpression.class.isAssignableFrom(clazz)){
					BinaryExpression binary=(BinaryExpression)clazz.getConstructor(Expression.class,Expression.class).newInstance((Expression)null,(Expression)null);
					if(binary.allowedLeftDataTypes().contains(type.getClass())||binary.allowedRightDataTypes().contains(type.getClass()))
					toReturn.add(binary.getOperator());
				}else if(MultipleArgumentsExpression.class.isAssignableFrom(clazz)){
					MultipleArgumentsExpression multi=(MultipleArgumentsExpression)clazz.getConstructor(List.class).newInstance((List<Expression>)null);
					if(multi.allowedDataTypes().contains(type.getClass()))
					toReturn.add(multi.getOperator());
				}
			}
		}
		return toReturn;
	}

	public abstract List<Expression> getLeavesByType(Class<? extends LeafExpression> type);	

	public static final List<Class<? extends Expression>> getExpressionsByCategory(Class<? extends ExpressionCategory> category){
		ArrayList<Class <? extends Expression>> toReturn=new ArrayList<>();
		for(Class<? extends Expression> clazz: availableImplementations)
			if(category.isAssignableFrom(clazz)) toReturn.add(clazz);
		return toReturn;
	}

}
