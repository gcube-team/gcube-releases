/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ShowOnlySelectedRowEvent extends GwtEvent<ShowOnlySelectedRowEventHandler> {
	
	public static final GwtEvent.Type<ShowOnlySelectedRowEventHandler> TYPE = new Type<ShowOnlySelectedRowEventHandler>();

	@Override
	public Type<ShowOnlySelectedRowEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowOnlySelectedRowEventHandler handler) {
		handler.onShowOnlySelectedRow(this);	
	}
	
	protected boolean onlySelected;

	/**
	 * @param onlySelected
	 */
	public ShowOnlySelectedRowEvent(boolean onlySelected) {
		this.onlySelected = onlySelected;
	}

	/**
	 * @return the onlySelected
	 */
	public boolean isOnlySelected() {
		return onlySelected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShowOnlySelectedRowEvent [onlySelected=");
		builder.append(onlySelected);
		builder.append("]");
		return builder.toString();
	}	
}
