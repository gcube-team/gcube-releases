package org.gcube.portlets.user.statisticalmanager.server.accounting;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

public class UsedSmAlgorithmLogEntry extends AccessLogEntry{


	private String user;
	
	private String algorithmName;
	
	public UsedSmAlgorithmLogEntry(String user, String name)
	{
		super("StatisticalManager_Algorithms");		
		this.user=user;
		this.algorithmName=name;
		
	}
	@Override
	public String getLogMessage() {
		String message="";
		
		message += "Algorithm "+ algorithmName+ " execute from user "+ user;
	 
			return message;
	}

}
