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
public class Soundex extends UnaryExpression implements TextExpression{

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9092423307003757878L;

	
	@SuppressWarnings("unused")
	private Soundex() {		
	}


	public Soundex(Expression argument) {
		super(argument);	
	}



	@Override
	public Operator getOperator() {
		return Operator.SOUNDEX;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType();
	}

	@Override
	public List<Class<? extends DataType>> allowedDataTypes() {
		return DEFAULT_ACCEPTED_TYPES;
	}
}
