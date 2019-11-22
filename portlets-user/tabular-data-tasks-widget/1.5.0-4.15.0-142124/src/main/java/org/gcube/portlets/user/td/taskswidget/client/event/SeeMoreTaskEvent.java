/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 29, 2013
 *
 */
public class SeeMoreTaskEvent extends GwtEvent<SeeMoreTask> {
	
	public static final GwtEvent.Type<SeeMoreTask> TYPE = new Type<SeeMoreTask>();
	private int startIndex;

	@Override
	public Type<SeeMoreTask> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SeeMoreTask handler) {
		handler.onSeeMoreTask(this);	
	}

	public SeeMoreTaskEvent(int newStart) {
		this.startIndex = newStart;
	}
	public int getStart() {
		return startIndex;
	}
}
