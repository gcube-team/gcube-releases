package org.gcube.data.analysis.tabulardata.operation.sdmx.agencies;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgenciesList extends ArrayList<String> implements List<String> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8311399119029910603L;
	private long expiringTime;
	
	public AgenciesList (long timeout)
	{
		super ();
		this.expiringTime = new Date().getTime()+timeout;
	}

	public boolean isExpired ()
	{
		return new Date().getTime() >= this.expiringTime;
	}
}
