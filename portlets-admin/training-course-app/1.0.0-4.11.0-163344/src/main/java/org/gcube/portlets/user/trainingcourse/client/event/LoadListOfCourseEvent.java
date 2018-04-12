package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;



// TODO: Auto-generated Javadoc
/**
 * The Class LoadListOfCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 */
public class LoadListOfCourseEvent extends GwtEvent<LoadListOfCourseEventHandler> {
	
	/** The type. */
	public static Type<LoadListOfCourseEventHandler> TYPE = new Type<LoadListOfCourseEventHandler>();
	
	/**
	 * Instantiates a new load list of course event.
	 */
	public LoadListOfCourseEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadListOfCourseEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadListOfCourseEventHandler handler) {
		handler.onLoadListOfCourses(this);
	}
	

}
