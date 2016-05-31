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
public class ItemSelectionEvent extends GwtEvent<ItemSelectionHandler> {

	/**
	 * Handler type.
	 */
	private static final Type<ItemSelectionHandler> TYPE = new Type<ItemSelectionHandler>();

	/**
	 * Fires a selection event on all registered handlers in the handler
	 * manager.
	 * 
	 * @param source the source of the handlers
	 * @param selectedItem the selected item
	 */
	public static void fire(HasItemSelectionHandlers source, Item selectedItem, boolean selectable) {
		ItemSelectionEvent event = new ItemSelectionEvent(selectedItem, selectable);
		source.fireEvent(event);
	}

	/**
	 * Gets the type associated with this event.
	 * @return returns the handler type
	 */
	public static Type<ItemSelectionHandler> getType() {
		return TYPE;
	}

	protected Item selectedItem;
	protected boolean selectable;

	/**
	 * @param selectedItem
	 * @param selectable
	 */
	public ItemSelectionEvent(Item selectedItem, boolean selectable) {
		this.selectedItem = selectedItem;
		this.selectable = selectable;
	}

	/**
	 * @return the selectedItem
	 */
	public Item getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @return the isSelectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(ItemSelectionHandler handler) {
		handler.onSelection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<ItemSelectionHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemSelectionEvent [selectable=");
		builder.append(selectable);
		builder.append(", selectedItem=");
		builder.append(selectedItem);
		builder.append("]");
		return builder.toString();
	}

}
