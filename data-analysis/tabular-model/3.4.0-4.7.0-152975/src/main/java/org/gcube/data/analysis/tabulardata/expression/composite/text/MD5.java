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
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MD5 extends UnaryExpression implements TextExpression{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6352794228045224114L;
	private static final List<Class<? extends DataType>> ALLOWED_TYPES=new ArrayList<>();
	
	static {
		ALLOWED_TYPES.add(TextType.class);
	}

	@SuppressWarnings("unused")
	private  MD5() {
	}
	
	
	
	public MD5(Expression argument) {
		super(argument);
	}



	@Override
	public List<Class<? extends DataType>> allowedDataTypes() {		
		return ALLOWED_TYPES;
	}
	
	@Override
	public Operator getOperator() {
		return Operator.MD5;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType(32);
	}

}
