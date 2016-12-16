package gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver;

import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryHandler;

import java.io.ObjectInputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecEngTCPConnManagerEntry implements ITCPConnectionManagerEntry
{
	private static Logger logger=LoggerFactory.getLogger(ExecEngTCPConnManagerEntry.class);

	public ExecEngTCPConnManagerEntry(){}
	
	public NamedEntry GetName()
	{
		return NamedEntry.ExecutionEngine;
	}

	public void HandleConnection(Socket socket)
	{
		try
		{
			BoundaryHandler h=new BoundaryHandler(socket);
			h.BoundarySideProtocol();
		}catch(Exception ex)
		{
			logger.warn("Could not initialize boundary side protocol handling",ex);
		}
	}
}
