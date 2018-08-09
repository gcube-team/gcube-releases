package org.gcube.data.analysis.tabulardata.expression.logical;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IsNull extends UnaryExpression implements LogicalExpression{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6953670646502640349L;

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
		DEFAULT_ACCEPTED_TYPES.add(DateType.class);		
		DEFAULT_ACCEPTED_TYPES.add(IntegerType.class);
		DEFAULT_ACCEPTED_TYPES.add(NumericType.class);
		DEFAULT_ACCEPTED_TYPES.add(GeometryType.class);
	}

	@SuppressWarnings("unused")
	private IsNull() {}
	
	

	public IsNull(Expression argument) {
		super(argument);
	}

	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}

	@Override
	public Operator getOperator() {
		return Operator.IS_NULL;
	}
	@Override
	public List<Class<? extends DataType>> allowedDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}
}
