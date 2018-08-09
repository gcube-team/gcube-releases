package gr.uoa.di.madgik.commons.channel.registry;

import java.net.Socket;

/**
 * Utility class containing information on connected nozzles to a channel.
 * 
 * @author gpapanikos
 */
public class RegisteredNozzle
{
	
	/** The Nozzle id. */
	private String NozzleID = null;
	
	/** The Connected. */
	private Boolean Connected=true;
	
	/** The Client socket. */
	private Socket ClientSocket = null;

	/**
	 * Instantiates a new registered nozzle.
	 * 
	 * @param NozzleID the nozzle id
	 * @param ClientSocket the client socket
	 */
	public RegisteredNozzle(String NozzleID, Socket ClientSocket)
	{
		this.NozzleID = NozzleID;
		this.ClientSocket = ClientSocket;
	}

	/**
	 * Gets the nozzle id.
	 * 
	 * @return the identifier
	 */
	public String GetNozzleID()
	{
		return this.NozzleID;
	}

	/**
	 * Gets the client sock.
	 * 
	 * @return the socket
	 */
	public Socket GetClientSock()
	{
		return this.ClientSocket;
	}
	
	/**
	 * Gets whether the nozzle is still connected
	 * 
	 * @return true, if the nozzle is still connected
	 */
	public Boolean GetIsConnected()
	{
		return this.Connected;
	}
	
	/**
	 * Disposes the instance, sets that the nozzle is not connected and closes the client socket if
	 * the client was connected through a socket 
	 */
	public void Dispose()
	{
		try
		{
			if (this.ClientSocket != null)
			{
				this.ClientSocket.close();
				this.ClientSocket=null;
			}
			this.Connected=false;
		} catch (Exception ex)
		{
		}
	}
}
