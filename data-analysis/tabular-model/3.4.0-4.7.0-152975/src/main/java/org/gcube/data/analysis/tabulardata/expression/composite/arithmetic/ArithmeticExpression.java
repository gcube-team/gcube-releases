package org.gcube.data.analysis.tabulardata.expression.composite.arithmetic;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.ExpressionCategory;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;

public abstract class ArithmeticExpression extends BinaryExpression implements ExpressionCategory{


	private static final List<Class<? extends DataType>> ACCEPTED_DATA_TYPES=new ArrayList<>();
	
	static {
		ACCEPTED_DATA_TYPES.add(NumericType.class);
		ACCEPTED_DATA_TYPES.add(IntegerType.class);
		ACCEPTED_DATA_TYPES.add(DateType.class);		
	}
	
	
	
	
	public ArithmeticExpression() {
		super();
	}

	public ArithmeticExpression(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2532547252476912910L;

	
	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {		
		DataType left=getLeftArgument().getReturnedDataType();
		DataType right=getRightArgument().getReturnedDataType();
		if(left.equals(right)) return left;
		if(left instanceof NumericType) return left;
		if(right instanceof NumericType) return right;
		return left;
	}

	@Override
	public List<Class<? extends DataType>> allowedLeftDataTypes() {		
		return ACCEPTED_DATA_TYPES;
	}
	
	@Override
	public List<Class<? extends DataType>> allowedRightDataTypes() {		
		return ACCEPTED_DATA_TYPES;
	}
}
