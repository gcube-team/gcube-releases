package org.gcube.portlets.user.trainingcourse.client.event;

import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.google.gwt.event.shared.GwtEvent;



// TODO: Auto-generated Javadoc
/**
 * The Class DeleteProjectEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2018
 */
public class DeleteProjectEvent extends GwtEvent<DeleteProjectEventHandler> {
	
	/** The type. */
	public static Type<DeleteProjectEventHandler> TYPE = new Type<DeleteProjectEventHandler>();
	
	/** The project. */
	private TrainingCourseObj project;

	
	/**
	 * Instantiates a new delete project event.
	 *
	 * @param project the project
	 */
	public DeleteProjectEvent(TrainingCourseObj project) {
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DeleteProjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteProjectEventHandler handler) {
		handler.onDeleteProject(this);
	}
	
	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public TrainingCourseObj getProject() {
		return project;
	}

}
