package gr.uoa.di.madgik.commons.test.channel;

import gr.uoa.di.madgik.commons.channel.nozzle.ChannelInlet;
import gr.uoa.di.madgik.commons.channel.nozzle.ChannelOutlet;
import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.ChannelLocatorFactory;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.channel.proxy.local.LocalNozzleConfig;
import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.channel.proxy.tcp.TCPServerNozzleConfig;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestChannel
{
	private static Logger logger=Logger.getLogger(TestChannel.class.getName());
	
	private enum ConfigType
	{
		local,
		tcp
	}
	
	static
	{
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Initializing Connection Manager");
		TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost",new ArrayList<gr.uoa.di.madgik.commons.server.PortRange>(),true));
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
	}
	
	public static void main(String []args) throws Exception
	{
		ConfigType ConfigurationToUse=ConfigType.tcp;
		Boolean AllowBroadcast=true;
		int RestrictBroadcast=5;
		int PortToUse=3000;
		int NumberOfEventsToProduce=10;
		int NumberOfOutlets=5;
		List<OutletThread> Toutlets=new ArrayList<OutletThread>();
		
		INozzleConfig config=TestChannel.CreateConfig(ConfigurationToUse, AllowBroadcast, RestrictBroadcast, PortToUse);
		ChannelInlet inlet=new ChannelInlet(config);
		InletThread Tinlet=new InletThread(inlet,NumberOfEventsToProduce);
		
		IChannelLocator locator= ChannelLocatorFactory.GetLocator(inlet.GetLocator().ToURI());
		
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO,"Locator of inlet is "+locator.ToURI());
		
		for(int i=0;i<NumberOfOutlets;i+=1)
		{
			ChannelOutlet outlet=new ChannelOutlet(locator);
			Toutlets.add(new OutletThread(outlet, i));
		}
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Waiting for inlet to complete");
		Tinlet.join();
	}
	
	private static INozzleConfig CreateConfig(ConfigType ConfigurationToUse,Boolean AllowBroadcast,int RestrictBroadcast,int PortToUse)
	{
		INozzleConfig Config=null;
		switch(ConfigurationToUse)
		{
			case local:
			{
				Config=new LocalNozzleConfig(AllowBroadcast, RestrictBroadcast);
				break;
			}
			case tcp:
			{
				Config=new TCPServerNozzleConfig(AllowBroadcast, RestrictBroadcast);
				break;
			}
		}
		return Config;
	}
}
