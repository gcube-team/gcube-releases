package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import gr.uoa.di.madgik.grs.events.BufferEvent;

public class EventEntry {
	public final BufferEvent event;
	public final int id;
	
	/**
	 * Creates a new instance
	 * 
	 * @param event The event originating from reader with a given id
	 * @param id The id of the reader from which the event was received
	 */
	public EventEntry(BufferEvent event, int id) {
		this.event = event;
		this.id = id;
	}
}
