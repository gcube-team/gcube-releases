package org.gcube.portlets.widgets.pickitem.client.events;

import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.event.shared.GwtEvent;



public class PickedItemEvent  extends GwtEvent<PickedItemEventHandler> {
	public static Type<PickedItemEventHandler> TYPE = new Type<PickedItemEventHandler>();
	
	private ItemBean item;
	private char triggerChar;
	private int itemCursorIndexStart;
	private int itemCursorIndexEnd;
	
	public PickedItemEvent(ItemBean item, char triggerChar, int itemCursorIndexStart, int itemCursorIndexEnd) {
		super();
		this.item = item;
		this.triggerChar = triggerChar;
		this.itemCursorIndexStart = itemCursorIndexStart;
		this.itemCursorIndexEnd = itemCursorIndexEnd;
	}

	@Override
	public Type<PickedItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PickedItemEventHandler handler) {
		handler.onSelectedItem(this);
	}
		
	public int getItemCursorIndexStart() {
		return itemCursorIndexStart;
	}

	public int getItemCursorIndexEnd() {
		return itemCursorIndexEnd;
	}

	public ItemBean getSelectedItem() {
		return item;
	}
	
	public char getTriggerChar() {
		return triggerChar;
	}
}
