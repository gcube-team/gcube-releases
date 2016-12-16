package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpressionParameter extends LeafParameter<Expression>{
	
	@SuppressWarnings("unused")
	private ExpressionParameter() {}
	
	public ExpressionParameter(String identifier, String name, String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}

	@Override
	public Class<Expression> getParameterType() {
		return Expression.class;
	}
	
	public boolean validate(Expression value){
		try{
			value.validate();
			return true;
		}catch(MalformedExpressionException e){
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExpressionParameter [getParameterType()=");
		builder.append(getParameterType());
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append("]");
		return builder.toString();
	}

	
	@Override
	public void validateValue(Object value) throws Exception {
		super.validateValue(value);
		((Expression)value).validate();
	}
	
}
