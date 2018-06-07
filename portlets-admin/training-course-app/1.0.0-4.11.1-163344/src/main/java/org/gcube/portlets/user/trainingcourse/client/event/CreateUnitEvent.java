package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class CreateNewCourseEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 9, 2018
 */
public class CreateUnitEvent extends GwtEvent<CreateUnitEventHandler> {
	
	/** The type. */
	public static Type<CreateUnitEventHandler> TYPE = new Type<CreateUnitEventHandler>();

	
	public CreateUnitEvent() {

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CreateUnitEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CreateUnitEventHandler handler) {
		handler.onCreateFolder(this);
	}

}
