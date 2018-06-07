package org.gcube.data.analysis.tabulardata.expression.logical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Or extends MultipleArgumentsExpression implements LogicalExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6041634810824885695L;

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
	}
	
	@SuppressWarnings({"unused" })
	private Or() {}

	public Or(Expression... arguments) {
		super(Arrays.asList(arguments));
	}

	public Or(List<Expression> arguments) {
		super(arguments);
	}
	
	@Override
	public Operator getOperator() {
		return Operator.OR;
	}

	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
	
	@Override
	public List<Class<? extends DataType>> allowedDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}

}
