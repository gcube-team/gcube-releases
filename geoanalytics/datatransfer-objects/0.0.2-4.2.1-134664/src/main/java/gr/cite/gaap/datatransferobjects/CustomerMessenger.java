package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public class CustomerMessenger
{
	private String name = null;
	private String originalName = null;
	private String code = null;
	private String eMail = null;
	private boolean active = false;
	
	public CustomerMessenger() { }
	
	public CustomerMessenger(Tenant c, boolean active)
	{
		this.name = c.getName();
		this.code = c.getCode();
		this.eMail = c.getEmail();
		this.active = active;
	}

	public String getName()
	{
		return name;
	}

	public void setOriginalName(String originalName)
	{
		this.originalName = originalName;
	}
	
	public String getOriginalName()
	{
		return originalName;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String geteMail()
	{
		return eMail;
	}

	public void seteMail(String eMail)
	{
		this.eMail = eMail;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
}
