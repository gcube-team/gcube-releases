package it.eng.rdlab.soa3.pm.connector.beans;

public class AttributeBean 
{
	private String 	id,
					value;

	public AttributeBean() 
	{
	}
	
	public AttributeBean (String id, String value)
	{
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
