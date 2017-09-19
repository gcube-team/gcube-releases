package org.gcube.common.dbinterface.attributes;

public class SimpleAttribute implements Attribute{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5732146301297169027L;
	
	public static final SimpleAttribute ALL = new SimpleAttribute("*"); 
	
	protected String attributeName;
	protected String tableAlias;
	
	public SimpleAttribute(String attributeName, String tableAlias) {
		this.attributeName = attributeName;
		this.tableAlias = tableAlias;
	}

	public SimpleAttribute(String attributeName) {
		this.attributeName = attributeName;
		this.tableAlias= null;
	}
	
	public String getAttribute(){
		return this.tableAlias==null?this.attributeName:this.tableAlias+"."+this.attributeName;
	}
	
	public String toString(){
		return this.getAttribute();
	}

	public String getAttributeName() {
		return attributeName;
	}
}
