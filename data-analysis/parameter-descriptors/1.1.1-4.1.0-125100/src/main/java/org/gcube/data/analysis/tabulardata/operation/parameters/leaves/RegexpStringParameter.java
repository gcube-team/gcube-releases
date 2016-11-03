package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegexpStringParameter extends LeafParameter<String> {
	
	private String regexp = null;
	
	@SuppressWarnings("unused")
	private RegexpStringParameter() {}

	public RegexpStringParameter(String identifier, String name, String description, Cardinality cardinality,
			String regexp) {
		super(identifier, name, description, cardinality);
		if (regexp == null || regexp.isEmpty())
			throw new IllegalArgumentException("Regexp cannot be null or empty");
		this.regexp = regexp;
	}

	public String getRegexp() {
		return regexp;
	}

	@Override
	public Class<String> getParameterType() {
		return String.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((regexp == null) ? 0 : regexp.hashCode());
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
		RegexpStringParameter other = (RegexpStringParameter) obj;
		if (regexp == null) {
			if (other.regexp != null)
				return false;
		} else if (!regexp.equals(other.regexp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegexpStringParameter [getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append(", getRegexp()=");
		builder.append(getRegexp());
		builder.append(", getParameterType()=");
		builder.append(getParameterType());
		builder.append("]");
		return builder.toString();
	}

	
	@Override
	public void validateValue(Object valueObj) throws Exception {
		super.validateValue(valueObj);
		String value=(String) valueObj;
		if(!value.matches(regexp)) throw new Exception(String.format("Passed argument %s doesn't match regexp constraint %s",value,regexp));
	}
}
