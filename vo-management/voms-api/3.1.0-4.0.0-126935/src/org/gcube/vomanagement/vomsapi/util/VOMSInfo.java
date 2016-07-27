package org.gcube.vomanagement.vomsapi.util;


public class VOMSInfo implements VOMSFQANInfo {

	private String voName;
	
	public VOMSInfo (String voName)
	{
		this.voName = voName;
	}
	
	public VOMSInfo ()
	{
		
	}
	
	@Override
	public String getFQAN() 
	{
		return null;
	}

	@Override
	public String getVoName() 
	{
		return this.voName;
	}

	@Override
	public void setVoName(String voName) 
	{
		this.voName = voName;
	}

	@Override
	public String getString() 
	{
		return this.voName;
	}

}
