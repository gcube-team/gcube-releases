package gr.uoa.di.madgik.commons.server;

import java.util.List;


public class TCPConnectionManagerConfig extends ConnectionManagerConfig
{
	public TCPConnectionManagerConfig(String HostName,List<PortRange> Ports,boolean UseRandomIfNoneAvailable){
		super(HostName, Ports, UseRandomIfNoneAvailable);
	}
}
