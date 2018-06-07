package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.google.gwt.event.shared.GwtEvent;



// TODO: Auto-generated Javadoc
/**
 * The Class LoadListOfCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 */
public class OpenProjectEvent extends GwtEvent<OpenProjectEventHandler> {
	
	/** The type. */
	public static Type<OpenProjectEventHandler> TYPE = new Type<OpenProjectEventHandler>();
	private TrainingCourseObj project;
	
	/**
	 * Instantiates a new load list of course event.
	 */
	public OpenProjectEvent(TrainingCourseObj project) {
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<OpenProjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(OpenProjectEventHandler handler) {
		handler.onLoadProject(this);
	}

	public TrainingCourseObj getProject() {
		return project;
	}
	
	
	

}
