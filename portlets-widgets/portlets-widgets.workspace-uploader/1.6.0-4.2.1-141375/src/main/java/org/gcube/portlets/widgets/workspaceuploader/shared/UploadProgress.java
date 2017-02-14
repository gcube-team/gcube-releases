package org.gcube.portlets.widgets.workspaceuploader.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspaceuploader.shared.event.UploadEvent;

/**
 * The Class UploadProgress.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 1, 2015
 */
public final class UploadProgress implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2797791973584424842L;
	private List<UploadEvent> events = new ArrayList<UploadEvent>();

	/**
	 * Instantiates a new upload progress.
	 */
	public UploadProgress() {
	}

	/**
	 * Gets the events.
	 *
	 * @return the events
	 */
	public List<UploadEvent> getEvents() {
		return events;
	}

	/**
	 * Adds the.
	 *
	 * @param event the event
	 */
	public void add(final UploadEvent event) {
		events.add(event);
	}
	
	/**
	 * Gets the last event.
	 *
	 * @return the last event
	 */
	public UploadEvent getLastEvent(){
		if(isEmpty())
			return null;
		return events.get(events.size()-1);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		events = new ArrayList<UploadEvent>();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return events.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UploadProgress [events=");
		builder.append(events);
		builder.append("]");
		return builder.toString();
	}
}


