package gr.uoa.di.madgik.execution.plan.element.invocable.callback;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class CallbackManager
{
	private static Map<String, CallbackRegistryEntry> Entries=new HashMap<String, CallbackRegistryEntry>();
	private static final Boolean lockMe=new Boolean(false);
	
	public static void RegisterCallback(CallbackRegistryEntry entry)
	{
		synchronized (CallbackManager.lockMe)
		{
			CallbackManager.Entries.put(entry.ID, entry);
		}
	}
	
	public static void UnregisterCallback(CallbackRegistryEntry entry)
	{
		synchronized (CallbackManager.lockMe)
		{
			CallbackManager.Entries.put(entry.ID, entry);
		}
	}
	
	public static void CallbackEvent(String ID,Socket sock)
	{
		synchronized (CallbackManager.lockMe)
		{
			CallbackRegistryEntry entry=CallbackManager.Entries.get(ID);
			if(entry!=null)
			{
				entry.NotifyCallback(sock);
			}
		}
	}

}
