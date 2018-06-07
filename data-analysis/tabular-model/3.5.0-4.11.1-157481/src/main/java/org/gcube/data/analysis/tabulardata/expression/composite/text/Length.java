package org.gcube.data.analysis.tabulardata.expression.composite.text;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Length extends UnaryExpression implements TextExpression{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6352794228045224114L;
	private static final List<Class<? extends DataType>> ALLOWED_TYPES=new ArrayList<>();
	
	static {
		ALLOWED_TYPES.add(TextType.class);
	}
	
	@Override
	public List<Class<? extends DataType>> allowedDataTypes() {		
		return ALLOWED_TYPES;
	}
	
	@Override
	public Operator getOperator() {
		return Operator.LENGTH;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new IntegerType();
	}

	
	@SuppressWarnings("unused")
	private Length() {
	}

	public Length(Expression argument) {
		super(argument);
	}
	
	
}
