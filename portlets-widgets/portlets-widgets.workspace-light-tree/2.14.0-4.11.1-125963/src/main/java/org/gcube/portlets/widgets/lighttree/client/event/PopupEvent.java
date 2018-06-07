/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import org.gcube.portlets.widgets.lighttree.client.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class PopupEvent extends GwtEvent<PopupHandler> {

	/**
	 * Handler type.
	 */
	private static final Type<PopupHandler> TYPE = new Type<PopupHandler>();

	/**
	 * Fires a popup cancel event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 */
	public static void fireCanceled(HasPopupHandlers source) {
		PopupEvent event = new PopupEvent(true, null, null);
		source.fireEvent(event);
	}

	/**
	 * Fires a data load event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 */
	public static void fireItemSelected(HasPopupHandlers source, Item selectedItem) {
		PopupEvent event = new PopupEvent(false, selectedItem, null);
		source.fireEvent(event);
	}
	
	public static void fireItemSelected(HasPopupHandlers source, Item selectedItem, String name) {
		PopupEvent event = new PopupEvent(false, selectedItem, name);
		source.fireEvent(event);
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<PopupHandler> getType() {
		return TYPE;
	}

	protected boolean canceled;
	protected Item selectedItem;
	protected String name;


	/**
	 * @param canceled
	 * @param selectedItem
	 * @param name
	 */
	public PopupEvent(boolean canceled, Item selectedItem, String name) {
		this.canceled = canceled;
		this.selectedItem = selectedItem;
		this.name = name;
	}
	
	/**
	 * @return the canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @return the selectedItem
	 */
	public Item getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(PopupHandler handler) {
		handler.onPopup(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<PopupHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PopupEvent [canceled=");
		builder.append(canceled);
		builder.append(", name=");
		builder.append(name);
		builder.append(", selectedItem=");
		builder.append(selectedItem);
		builder.append("]");
		return builder.toString();
	}
	
	
}
