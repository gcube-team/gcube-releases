package org.gcube.data.analysis.tabulardata.expression.logical;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Between extends MultipleArgumentsExpression implements LogicalExpression{

	/** 
	 * arguments[0] BETWEEN arguments[1] AND arguments[2]
	 * 
	 */
	
	
	private static final long serialVersionUID = 3990333735232048499L;

	@SuppressWarnings("unused")
	private Between() {
		super();	
	}

	public Between(Expression leftArgument, Expression minRangeArgument, Expression maxRangeArgument) {
		super(Arrays.asList(new Expression[]{
				leftArgument,
				minRangeArgument,
				maxRangeArgument}));
	}

	@Override
	public Operator getOperator() {		
		return Operator.BETWEEN;
	}
	
	@Override
	public DataType getReturnedDataType(){		
		return new BooleanType();
	}
	
	@Override
	public void validate() throws MalformedExpressionException {		
		super.validate();		
	}
	
	
	public Expression getLeftArgument(){
		return arguments.get(0);
	}
	
	
	public Expression getMinRangeArgument(){
		return arguments.get(1);
	}
	
	public Expression getMaxRangeArgument(){
		return arguments.get(2);
	}
		
}
