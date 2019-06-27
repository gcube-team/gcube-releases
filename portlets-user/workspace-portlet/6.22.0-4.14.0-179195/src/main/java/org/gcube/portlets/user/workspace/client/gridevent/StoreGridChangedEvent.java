package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class StoreGridChangedEvent extends GwtEvent<StoreGridChangedEventHandler> {
  public static Type<StoreGridChangedEventHandler> TYPE = new Type<StoreGridChangedEventHandler>();
  
  	private int size = -1;


	/**
	 * @param size
	 */
	public StoreGridChangedEvent(int size) {
		this.size = size;
	}

	@Override
	public Type<StoreGridChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(StoreGridChangedEventHandler handler) {
		handler.onStoreChanged(this);
		
	}

	public int getSize() {
		return size;
	}
}