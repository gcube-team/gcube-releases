/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.event;

import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class ActionCompletedEvent extends GwtEvent<ActionCompletedEventHandler>  {
	
	public static final GwtEvent.Type<ActionCompletedEventHandler> TYPE = new Type<ActionCompletedEventHandler>();
	private TabularDataAction action;

	@Override
	public Type<ActionCompletedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	
	public ActionCompletedEvent(TabularDataAction action){
		this.action = action;
	}


	/**
	 * @return the action
	 */
	public TabularDataAction getAction() {
		return action;
	}


	@Override
	protected void dispatch(ActionCompletedEventHandler handler) {
		handler.onActionCompleted(this);
	}
}
