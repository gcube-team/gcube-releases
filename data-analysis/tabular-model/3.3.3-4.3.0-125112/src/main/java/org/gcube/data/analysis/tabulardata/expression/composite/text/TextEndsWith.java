package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
public class TextEndsWith extends CaseSensitiveTextExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6439172469451372997L;

	
	@SuppressWarnings("unused")
	private TextEndsWith() {}

	public TextEndsWith(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);
	}

	
	public TextEndsWith(Expression leftArgument, Expression rightArgument,
			boolean caseSensitive) {
		super(leftArgument, rightArgument, caseSensitive);
	}

	@Override
	public Operator getOperator() {
		return Operator.ENDS_WITH;
	}

	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}
