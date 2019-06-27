package gr.cite.geoanalytics.notifications;

import java.util.List;

public interface NotificationManager 
{
	public void register(String notificationId, EventType eventType) throws Exception;
	
	public void unregister(String notificationId) throws Exception;
	public void unregister(String notificationId, EventType eventType) throws Exception;
	
	public void broadcast(Event event) throws Exception;
	public void notify(String notificationId, Event event) throws Exception;
	
	/**
	 * Poll events without purging.
	 * Equivalent to {@link NotificationManager#poll(<code>notificationId</code>,<code>false</code>) }.
	 * Calling this method is not necessary if the underlying implementation uses push technology.
	 * @return A list of all pending events for notificationId
	 */
	public List<Event> poll(String notificationId) throws Exception;
	/**
	 * Poll events, optionally purging them from the outstanding event queue.
	 * Purging is optional; underlying implementations are free to ignore the purge argument. 
	 * Calling this method is not necessary if the underlying implementation uses push technology.
	 * @param purge
	 * @return A list of all pending events for notificationId
	 */
	public List<Event> poll(String notificationId, boolean purge) throws Exception;
	/**
	 * Poll events of type eventType without purging.
	 * Equivalent to {@link NotificationManager#poll(<code>notificationId</code>,<code>eventType</code>,<code>false</code>) }.
	 * Calling this method is not necessary if the underlying implementation uses push technology.
	 * Calling this method is not necessary if the underlying implementation uses push technology.
	 * @return A list of all pending events for notificationId
	 */
	public List<Event> poll(String notificationId, EventType eventType) throws Exception;
	/**
	 * Poll events of type eventType, optionally purging them from the outstanding event queue.
	 * Purging is optional; underlying implementations are free to ignore the purge argument. 
	 * Calling this method is not necessary if the underlying implementation uses push technology.
	 * @param purge
	 * @return A list of all pending events for notificationId
	 */
	public List<Event> poll(String notificationId, EventType eventType, boolean purge) throws Exception;
}
