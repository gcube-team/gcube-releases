/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SelectAllEvent extends GwtEvent<SelectAllEventHandler> {
	
	public static final GwtEvent.Type<SelectAllEventHandler> TYPE = new Type<SelectAllEventHandler>();

	@Override
	public Type<SelectAllEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectAllEventHandler handler) {
		handler.onSelectAll(this);
	}
	
	protected boolean selectAll;

	/**
	 * @param onlySelected
	 */
	public SelectAllEvent(boolean onlySelected) {
		this.selectAll = onlySelected;
	}

	/**
	 * @return the onlySelected
	 */
	public boolean isOnlySelected() {
		return selectAll;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SelectAllEvent [onlySelected=");
		builder.append(selectAll);
		builder.append("]");
		return builder.toString();
	}
}
