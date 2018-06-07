package org.gcube.portlets.user.trainingcourse.client.event;



import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class SelectedWorkspaceItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2018
 */
public class ShareTrainingProjectEvent extends GwtEvent<ShareTrainingProjectEventHandler> {
	
	/** The type. */
	public static Type<ShareTrainingProjectEventHandler> TYPE = new Type<ShareTrainingProjectEventHandler>();
	private TrainingCourseObj theProject;
	private boolean isGroup;

	
	/**
	 * Instantiates a new selected workspace item event.
	 *
	 * @param itemId the item id
	 */
	public ShareTrainingProjectEvent(TrainingCourseObj theProject, boolean isGroup) {
		this.theProject = theProject;
		this.isGroup = isGroup;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShareTrainingProjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShareTrainingProjectEventHandler handler) {
		handler.onShareProject(this);
	}
	
	public TrainingCourseObj getProject(){
		return theProject;
	}
	
	public boolean isGroup() {
		return isGroup;
	}
}
