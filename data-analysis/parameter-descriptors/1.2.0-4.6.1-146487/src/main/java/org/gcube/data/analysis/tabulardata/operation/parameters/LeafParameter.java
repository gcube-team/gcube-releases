package org.gcube.data.analysis.tabulardata.operation.parameters;

public abstract class LeafParameter<T> extends Parameter {
	
	protected LeafParameter(){}
	
	public LeafParameter(String identifier, String name, String description, Cardinality cardinality) {
		super(identifier, name, description, cardinality);
	}

	public abstract Class<T> getParameterType();

	public void validateValue(Object value)throws Exception{
		if(!getParameterType().isInstance(value)) throw new Exception(String.format("Invalid %s parameter instance class. Found %s, expected %s.",this.getName(),value.getClass(),this.getParameterType()));
	}
	
}
