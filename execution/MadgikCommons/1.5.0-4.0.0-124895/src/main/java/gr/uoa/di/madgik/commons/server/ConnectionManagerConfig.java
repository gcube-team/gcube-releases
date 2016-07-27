package gr.uoa.di.madgik.commons.server;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public class ConnectionManagerConfig {
	private static final int  SOCKET_TIMEOUT = 60*1000;
	
	public List<PortRange> Ports=new ArrayList<PortRange>();
	public boolean UseRandomIfNoneAvailable=false;
	public String HostName=null;
	public int timeout = SOCKET_TIMEOUT;
	
	public ConnectionManagerConfig(){}

	public ConnectionManagerConfig(List<PortRange> Ports)
	{
		this.Ports=Ports;
	}

	public ConnectionManagerConfig(String HostName,List<PortRange> Ports,boolean UseRandomIfNoneAvailable)
	{
		this(HostName, Ports, UseRandomIfNoneAvailable, SOCKET_TIMEOUT);
	}
	
	public ConnectionManagerConfig(String HostName,List<PortRange> Ports,boolean UseRandomIfNoneAvailable, int timeout)
	{
		this.HostName=HostName;
		this.Ports=Ports;
		this.UseRandomIfNoneAvailable=UseRandomIfNoneAvailable;
		this.timeout = timeout;
	} 
}
