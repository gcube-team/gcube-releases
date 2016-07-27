package it.eng.rdlab.soa3.pm.connector.javaapi.beans;

public class ResponseBean 
{
	public static final int RULE_CREATE_UPDATE_WARNING_ON_EXTRA_PARAMETERS = -1;
	public static final int RULE_CREATE_UPDATE_NOT_CREATED_OR_UPDATED = -2;
	public static final int RULE_UPDATE_RULE_NOT_FOUND = -3;
	public static final int RULE_CREATE_UPDATE_OK = 0;

	
	private int status;
	private String info;
	
	public ResponseBean (int status, String info)
	{
		this.status = status;
		this.info = info;
	}

	public int getStatus() {
		return status;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public String toString ()
	{
		return "status = "+status+" info "+info;
	}
	

}
