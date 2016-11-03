package gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver;

import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.callback.CallbackManager;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecEngCallbackTCPConnManagerEntry implements ITCPConnectionManagerEntry
{
	private static Logger logger=LoggerFactory.getLogger(ExecEngCallbackTCPConnManagerEntry.class);

	public ExecEngCallbackTCPConnManagerEntry(){}
	
	public NamedEntry GetName()
	{
		return NamedEntry.ExecutionEngineCallback;
	}

	public void HandleConnection(Socket socket)
	{
		try
		{
			String ID=new DataInputStream(socket.getInputStream()).readUTF();
			CallbackManager.CallbackEvent(ID,socket);
		}catch(Exception ex)
		{
			logger.warn("Could not initialize boundary side protocol handling",ex);
		}
	}
}
