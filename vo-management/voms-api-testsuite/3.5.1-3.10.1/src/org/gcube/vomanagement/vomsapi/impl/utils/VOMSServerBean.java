package org.gcube.vomanagement.vomsapi.impl.utils;

public class VOMSServerBean 
{
	String 	hostName, 
			hostDN, 
			voName;
	
	int hostPort;

	public VOMSServerBean ()
	{
		this.hostName = null; 
		this.hostDN = null;
		this.voName = null;
		this.hostPort = -1;
	}
	
	public VOMSServerBean (String hostName, String hostDN, int hostPort, String voName)
	{
		this.hostName = hostName; 
		this.hostDN = hostDN;
		this.voName = voName;
		this.hostPort = hostPort;
	}
	
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostDN() {
		return hostDN;
	}

	public void setHostDN(String hostDN) {
		this.hostDN = hostDN;
	}

	public String getVoName() {
		return voName;
	}

	public void setVoName(String voName) {
		this.voName = voName;
	}

	public int getHostPort() {
		return hostPort;
	}

	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
	
	
}
