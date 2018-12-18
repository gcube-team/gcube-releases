package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc

/**
 * The Class TrainingCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 6, 2018
 */
public class TrainingCourseEvent extends GwtEvent<TrainingCourseEventHandler> {
	
	/** The type. */
	public static Type<TrainingCourseEventHandler> TYPE = new Type<TrainingCourseEventHandler>();
	
	/** The project. */
	private TrainingCourseObj project;

	private EventType eventType;
	
	public static enum EventType {CREATED, UPDATED}

	
	/**
	 * Instantiates a new training course event.
	 *
	 * @param project the project
	 */
	public TrainingCourseEvent(TrainingCourseObj project, EventType eventType) {
		this.project = project;
		this.eventType = eventType;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TrainingCourseEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(TrainingCourseEventHandler handler) {
		handler.onCourseEvent(this);
	}

	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public TrainingCourseObj getProject() {
		return project;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	

}
