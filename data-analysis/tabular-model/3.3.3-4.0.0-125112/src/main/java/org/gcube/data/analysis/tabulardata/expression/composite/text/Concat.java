package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concat extends BaseTextExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2510108480762238197L;

	
	@SuppressWarnings("unused")
	private Concat() {}
	
	
	
	public Concat(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);
	}



	@Override
	public Operator getOperator() {
		return Operator.CONCAT;
	}

	@Override
	public DataType getReturnedDataType(){
		return new TextType();
	}

}
