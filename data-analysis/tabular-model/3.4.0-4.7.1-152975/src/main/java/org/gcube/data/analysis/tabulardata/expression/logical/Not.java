package org.gcube.data.analysis.tabulardata.expression.logical;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
@XmlRootElement
public class Not extends UnaryExpression implements LogicalExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1916660370859383155L;

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
	}
	
	
	@SuppressWarnings("unused")
	private Not() {}
	
	public Not(Expression argument) {
		super(argument);
	}


	@Override
	public Operator getOperator() {
		return Operator.NOT;
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}
