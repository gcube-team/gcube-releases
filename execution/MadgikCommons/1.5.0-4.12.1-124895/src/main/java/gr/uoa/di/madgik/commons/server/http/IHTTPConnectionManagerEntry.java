package gr.uoa.di.madgik.commons.server.http;

import java.io.BufferedOutputStream;
import java.net.Socket;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public interface IHTTPConnectionManagerEntry
{
	public enum NamedEntry
	{
		ExecutionEngineCallback,
		ExecutionEngine,
		Channel,
		gRS2,
		gRS2Store
	}
	
	public NamedEntry GetName();
	
	public void HandleConnection(Socket socket, String request, BufferedOutputStream out, String key);

}
