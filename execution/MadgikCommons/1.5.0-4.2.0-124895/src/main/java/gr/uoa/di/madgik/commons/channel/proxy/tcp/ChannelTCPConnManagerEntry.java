package gr.uoa.di.madgik.commons.channel.proxy.tcp;

import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryEntry;
import gr.uoa.di.madgik.commons.channel.registry.ChannelRegistryKey;
import gr.uoa.di.madgik.commons.channel.registry.RegisteredNozzle;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class follows the singleton pattern to initialize a single TCP server
 * capable of servicing outlet requests outside the boundaries of a VM. the first
 * time it is needed, the server is initialize to listen to a port chosen
 * from within a configurable selection of ranges or a random one. Everytime an
 * outlet is connected to the port, the server waits to read the channel UUID
 * the outlet is interested in, as well as the nozzle's identifier, updates
 * the {@link ChannelRegistry} through {@link ChannelRegistryEntry#RegisterNozzle(RegisteredNozzle)}
 * and waits for the next outlet to connect. Inlets initialized to
 * use the server will query the server to retrieve the socket they are interested
 * in based on the channel UUID they serve. From the above description it is obvious
 * that if an outlet opens a connection and then does not provide the channel UUID, no
 * other outlet can connect.
 *
 * TODO fix denial of service?
 * TODO refactor comments to reflect current
 * 
 * @author gpapanikos
 */
public class ChannelTCPConnManagerEntry implements ITCPConnectionManagerEntry
{
	private static Logger logger=Logger.getLogger(ChannelTCPConnManagerEntry.class.getName());
	
	public ChannelTCPConnManagerEntry(){}

	public NamedEntry GetName()
	{
		return NamedEntry.Channel;
	}

	public void HandleConnection(Socket socket)
	{
		try
		{
			DataInputStream din = new DataInputStream(socket.getInputStream());
			String nozzleUUID = din.readUTF();
			String channelUUID = din.readUTF();
			ChannelRegistryEntry entry = ChannelRegistry.Retrieve(new ChannelRegistryKey(channelUUID));
			if (entry != null)
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE,"New Connection opened");
				if(!entry.RegisterNozzle(new RegisteredNozzle(nozzleUUID, socket)))
				{
					if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE,"Registration of new connection not allowed");
					socket.close();
				}
			}
			else
			{
				if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Received incomming connection for non available channel");
				socket.close();
			}
		}catch(Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not handle incomming connection",ex);
		}
	}

}
