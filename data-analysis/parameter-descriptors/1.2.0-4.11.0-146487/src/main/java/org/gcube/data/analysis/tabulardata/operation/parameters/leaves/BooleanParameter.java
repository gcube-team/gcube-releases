package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BooleanParameter extends LeafParameter<Boolean> {
	
	@SuppressWarnings("unused")
	private BooleanParameter() {}

	public BooleanParameter(String identifier, String name, String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}

	@Override
	public Class<Boolean> getParameterType() {
		return Boolean.class;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BooleanParameter [getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append(", getParameterType()=");
		builder.append(getParameterType());
		builder.append("]");
		return builder.toString();
	}

}
