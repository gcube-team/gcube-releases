package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class CourseChangeStatusEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2018
 */
public class CourseChangeStatusEvent extends GwtEvent<CourseChangeStatusEventHandler> {
	
	/** The type. */
	public static Type<CourseChangeStatusEventHandler> TYPE = new Type<CourseChangeStatusEventHandler>();
	private boolean isActive;

	
	/**
	 * Instantiates a new course change status event.
	 */
	public CourseChangeStatusEvent(boolean isActive) {
		this.isActive = isActive;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CourseChangeStatusEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CourseChangeStatusEventHandler handler) {
		handler.onChangeStatus(this);
	}
	
	public boolean isActive() {
		return isActive;
	}

}
