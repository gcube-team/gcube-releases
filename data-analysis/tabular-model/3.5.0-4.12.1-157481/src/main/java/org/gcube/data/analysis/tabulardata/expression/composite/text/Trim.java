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
public class Trim extends UnaryExpression implements TextExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6396355912996646769L;
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
		return Operator.TRIM;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType();
	}

	@SuppressWarnings("unused")
	private Trim() {
	}

	public Trim(Expression argument) {
		super(argument);
	}
	
	
}
