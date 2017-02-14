package org.gcube.common.dbinterface.attributes;


public class AggregatedAttribute extends SimpleAttribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2421501403722182599L;
	
	public AggregationFunctions aggregationFunction;
	
	public AggregatedAttribute(String attributeName, String tableAlias, AggregationFunctions aggregationFunction) {
		super(attributeName, tableAlias);
		this.aggregationFunction= aggregationFunction;
	}

	public AggregatedAttribute(String attributeName, AggregationFunctions aggregationFunction) {
		super(attributeName);
		this.aggregationFunction= aggregationFunction;
	}
	
	public String getAttribute(){
		return this.aggregationFunction.getSqlFunction()+"("+super.getAttribute()+")";
	}
	
	public String toString(){
		return this.getAttribute();
	}
}
