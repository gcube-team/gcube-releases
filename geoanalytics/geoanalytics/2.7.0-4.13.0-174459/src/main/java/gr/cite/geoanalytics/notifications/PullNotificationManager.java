package gr.cite.geoanalytics.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import gr.cite.geoanalytics.notifications.exception.EventNotRegisteredException;

@Component
public class PullNotificationManager implements NotificationManager
{

	public Map<String, Map<EventType, List<Event>>> events = new ConcurrentHashMap<String, Map<EventType, List<Event>>>();
	
	@Override
	public void register(String notificationId, EventType eventType)
	{
		if(events.get(notificationId) == null)
		{
			events.put(notificationId, new ConcurrentHashMap<EventType, List<Event>>());
			events.get(notificationId).put(eventType, new ArrayList<Event>());
		}
		
	}
	
	@Override
	public void unregister(String notificationId) throws EventNotRegisteredException
	{
		if(!events.containsKey(notificationId))
			return;//throw new EventNotRegisteredException("No events are registered for notification id " + notificationId);
		events.remove(notificationId);
	}
	
	@Override
	public void unregister(String notificationId, EventType eventType) throws EventNotRegisteredException
	{
		if(!events.containsKey(notificationId))
			throw new EventNotRegisteredException("No events are registered for notification id " + notificationId);
		if(!events.get(notificationId).containsKey(eventType))
			throw new EventNotRegisteredException("Event " + eventType + " is not registered for notification id " + notificationId);
		events.get(notificationId).remove(eventType);
	}

	@Override
	public void broadcast(Event event)
	{
		for(Map.Entry<String, Map<EventType, List<Event>>> e : events.entrySet())
		{
			if(e.getValue().containsKey(event.getType()))
			{
				synchronized(e.getValue().get(event.getType()))
				{
					e.getValue().get(event.getType()).add(event);
				}
			}
		}
		
		
	}

	@Override
	public void notify(String notificationId, Event event) throws EventNotRegisteredException
	{
		if(!events.containsKey(notificationId)) 
			throw new EventNotRegisteredException("Event " + event.getType() + " is not registered for notification id " + notificationId);
		if(!events.get(notificationId).containsKey(event.getType()))
			throw new EventNotRegisteredException("Event " + event.getType() + " is not registered for notification id " + notificationId);
		synchronized(events.get(notificationId).get(event.getType()))
		{
			events.get(notificationId).get(event.getType()).add(event);
		}
		
	}

	@Override
	public List<Event> poll(String notificationId) throws EventNotRegisteredException
	{
		return poll(notificationId, false);
	}

	@Override
	public List<Event> poll(String notificationId, boolean purge) throws EventNotRegisteredException
	{
		List<Event> res = new ArrayList<Event>();
		if(!events.containsKey(notificationId)) 
			throw new EventNotRegisteredException("Invalid notification id " + notificationId);
		for(Map.Entry<EventType, List<Event>> e : events.get(notificationId).entrySet())
		{
			synchronized(e.getValue())
			{
				res.addAll(e.getValue());
				if(purge) e.getValue().clear();
			}
		}
		return res;
	}

	@Override
	public List<Event> poll(String notificationId, EventType eventType) throws EventNotRegisteredException
	{
		return poll(notificationId, eventType, false);
		
	}

	@Override
	public List<Event> poll(String notificationId, EventType eventType,
			boolean purge) throws EventNotRegisteredException
	{
		List<Event> res = new ArrayList<Event>();
		if(!events.containsKey(notificationId)) 
			throw new EventNotRegisteredException("Event " + eventType + " is not registered for notification id " + notificationId);
		if(!events.get(notificationId).containsKey(eventType))
			throw new EventNotRegisteredException("Event " + eventType + " is not registered for notification id " + notificationId);
		synchronized(events.get(notificationId).get(eventType))
		{
			res.addAll(events.get(notificationId).get(eventType));
			if(purge) events.get(notificationId).get(eventType).clear();
		}
		return res;
	}

	
}
