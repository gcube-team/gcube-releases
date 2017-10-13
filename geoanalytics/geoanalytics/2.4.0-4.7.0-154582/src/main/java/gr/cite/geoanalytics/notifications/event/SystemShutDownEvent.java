package gr.cite.geoanalytics.notifications.event;

import gr.cite.geoanalytics.notifications.Event;
import gr.cite.geoanalytics.notifications.EventType;

public class SystemShutDownEvent extends Event 
{
	private long shutdownTime;
	
	public SystemShutDownEvent()
	{
		type = EventType.SystemShutDown;
	}
	
	public SystemShutDownEvent(long timeToShutdown)
	{
		this();
		this.shutdownTime = timeToShutdown;
	}
	
	public long getShutdownTime()
	{
		return shutdownTime;
	}
	
	public void setShutdown(long timeToShutdown)
	{
		this.shutdownTime = timeToShutdown;
	}
}
