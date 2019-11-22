package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IntegerParameter extends LeafParameter<Integer>{

	private Integer min=Integer.MIN_VALUE;
	private Integer max=Integer.MAX_VALUE;
	
	@Override
	public Class<Integer> getParameterType() {
		return Integer.class;
	}
	
	@SuppressWarnings("unused")
	private IntegerParameter() {}

	public IntegerParameter(String identifier, String name, String description,
			Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}
	
	
	public IntegerParameter(String identifier, String name, String description,
			Cardinality cardinality, Integer min, Integer max) {
		super(identifier, name, description, cardinality);
		this.min = min;
		this.max = max;
	}

	public Integer getMin() {
		return min;
	}
	
	public Integer getMax() {
		return max;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IntegerParameter [min=");
		builder.append(min);
		builder.append(", max=");
		builder.append(max);
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
		Integer value=(Integer)valueObj;
		if(value<min||value>max) throw new Exception (String.format("Passed argument %s is not in allowed range %s..%s",value,min,max));
	}
}
