package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class CreateNewCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 9, 2018
 */
public class ShowMoreInfoEvent extends GwtEvent<ShowMoreInfoEventHandler> {
	
	/** The type. */
	public static Type<ShowMoreInfoEventHandler> TYPE = new Type<ShowMoreInfoEventHandler>();
	private boolean showMoreInfo;

	
	public ShowMoreInfoEvent(boolean bool) {
		this.showMoreInfo = bool;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowMoreInfoEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowMoreInfoEventHandler handler) {
		handler.onShowMoreInfo(this);
	}

	public boolean getShowMoreInfo(){
		return showMoreInfo;
	}
}
