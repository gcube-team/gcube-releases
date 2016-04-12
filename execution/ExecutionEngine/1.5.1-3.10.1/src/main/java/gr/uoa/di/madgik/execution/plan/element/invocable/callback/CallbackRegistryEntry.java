package gr.uoa.di.madgik.execution.plan.element.invocable.callback;

import java.net.Socket;

public class CallbackRegistryEntry
{
	public String ID=null;
	public final Object synchCallback=new Object();
	public Socket Sock=null;

	public void WaitForCallback(long timeout)
	{
		synchronized (this.synchCallback)
		{
			if(this.Sock!=null) return;
			try{this.synchCallback.wait(timeout);}catch(Exception ex){}
		}
	}
	
	public void NotifyCallback(Socket Sock)
	{
		synchronized (this.synchCallback)
		{
			this.Sock=Sock;
			this.synchCallback.notify();
		}
	}
}
