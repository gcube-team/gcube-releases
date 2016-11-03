package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultivaluedStringParameter extends LeafParameter<String> {

	private List<String> admittedValues = null;
	
	private boolean caseSensitive=true;
	
	@SuppressWarnings("unused")
	private MultivaluedStringParameter() {}

	public MultivaluedStringParameter(String identifier, String name, String description, Cardinality cardinality,
			List<String> admittedValues) {
		super(identifier, name, description, cardinality);
		if (admittedValues==null || admittedValues.isEmpty()) throw new IllegalArgumentException("Must provide a non empty list of admitted values");
		this.admittedValues = admittedValues;
	}
	
	
	
	public MultivaluedStringParameter(String identifier, String name,
			String description, Cardinality cardinality,
			List<String> admittedValues, boolean caseSensitive) {
		super(identifier, name, description, cardinality);
		this.admittedValues = admittedValues;
		this.caseSensitive = caseSensitive;
	}

	public boolean validate(String value){
		return getAdmittedValues().contains(value);
	}

	public List<String> getAdmittedValues() {
		return admittedValues;
	}

	@Override
	public Class<String> getParameterType() {
		return String.class;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((admittedValues == null) ? 0 : admittedValues.hashCode());
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
		MultivaluedStringParameter other = (MultivaluedStringParameter) obj;
		if (admittedValues == null) {
			if (other.admittedValues != null)
				return false;
		} else if (!admittedValues.equals(other.admittedValues))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MultivaluedStringParameter [admittedValues=");
		builder.append(admittedValues);
		builder.append(", caseSensitive=");
		builder.append(caseSensitive);
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
	public void validateValue(Object valueObj) throws Exception {
		super.validateValue(valueObj);
		String value=(String) valueObj;
		if(caseSensitive){
			if(!admittedValues.contains(value)) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,getAdmittedValues()));
		}else{
			boolean ok=false;
			for(String admitted:admittedValues)
				if(admitted.equalsIgnoreCase(value)) ok=true;
			if(!ok) throw new Exception(String.format("Passed argument %s is not among valid ones %s ",value,getAdmittedValues()));
		}
	}

	
}
