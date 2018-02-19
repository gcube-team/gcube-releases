package org.gcube.data.analysis.tabulardata.operation.parameters;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CompositeParameter extends Parameter {

	private List<Parameter> parameters = null;

	@SuppressWarnings("unused")
	private CompositeParameter() {
		super();
	}

	public CompositeParameter(String identifier, String name, String description, Cardinality cardinality,
			List<Parameter> innerParameters) {
		super(identifier, name, description, cardinality);
		setParameters(innerParameters);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		if (parameters.isEmpty())
			throw new IllegalArgumentException("Must provide at least one inner parameter.");
		this.parameters = parameters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositeParameter other = (CompositeParameter) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompositeParameter [parameters=");
		builder.append(parameters);
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

	
	
}
