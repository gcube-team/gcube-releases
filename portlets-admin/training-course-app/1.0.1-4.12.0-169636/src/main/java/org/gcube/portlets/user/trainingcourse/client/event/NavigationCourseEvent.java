package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;

// TODO: Auto-generated Javadoc

/**
 * The Class NavigationCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 6, 2018
 */
public class NavigationCourseEvent extends GwtEvent<NavigationCourseEventHandler> {

	/** The type. */
	public static Type<NavigationCourseEventHandler> TYPE = new Type<NavigationCourseEventHandler>();

	/**
	 * The Enum NavigationEventType.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 6, 2018
	 */
	public static enum NavigationEventType {

		CREATE, EDIT
	}

	/** The event type. */
	private NavigationEventType eventType;

	/**
	 * Instantiates a new navigation course event.
	 *
	 * @param eventType
	 *            the event type
	 */
	public NavigationCourseEvent(NavigationEventType eventType) {
		this.eventType = eventType;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<NavigationCourseEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.
	 * EventHandler)
	 */
	@Override
	protected void dispatch(NavigationCourseEventHandler handler) {
		handler.onNavigationInteractionCourse(this);
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public NavigationEventType getEventType() {
		return eventType;
	}

}
