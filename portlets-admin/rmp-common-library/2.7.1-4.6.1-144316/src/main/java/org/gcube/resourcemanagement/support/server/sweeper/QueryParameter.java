package org.gcube.resourcemanagement.support.server.sweeper;

public class QueryParameter {
	protected String name;
	protected String value;
	
	public QueryParameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}	

	@Override
	public String toString() {
		return "QueryParameter [name=" + name + ", value=" + value + "]";
	}	
}
