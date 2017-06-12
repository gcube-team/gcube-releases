package gr.cite.geoanalytics.notifications;

public abstract class Event 
{
	protected EventType type = null;
	
	public EventType getType()
	{
		return type;
	}
}
