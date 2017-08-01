package gr.uoa.di.madgik.commons.server;

import java.net.Socket;

public interface ITCPConnectionManagerEntry
{
	public enum NamedEntry
	{
		gRS2,
		gRS2Store,
		ExecutionEngineCallback,
		ExecutionEngine,
		Channel
	}
	
	public NamedEntry GetName();
	
	public void HandleConnection(Socket socket);
}
