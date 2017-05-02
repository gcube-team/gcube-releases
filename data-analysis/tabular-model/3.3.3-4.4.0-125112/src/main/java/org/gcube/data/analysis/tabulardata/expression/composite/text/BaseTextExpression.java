package org.gcube.data.analysis.tabulardata.expression.composite.text;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseTextExpression extends BinaryExpression implements TextExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1038382757016138183L;

	private static final List<Class<? extends DataType>> allowedTypes=new ArrayList<Class<? extends DataType>>();
	
	
	static {
		allowedTypes.add(TextType.class);
	}
	
	protected BaseTextExpression() {
		super();
	}

	protected BaseTextExpression(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);
	}

	@Override
	public List<Class<? extends DataType>> allowedLeftDataTypes() {
				return allowedTypes;
	}
	
	@Override
	public List<Class<? extends DataType>> allowedRightDataTypes() {
		return allowedTypes;
	}
	
	
}
