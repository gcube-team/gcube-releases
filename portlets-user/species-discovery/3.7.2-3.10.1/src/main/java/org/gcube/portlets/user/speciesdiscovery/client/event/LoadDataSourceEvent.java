/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class LoadDataSourceEvent extends GwtEvent<LoadDataSourceEventHandler> {
	
	public static final GwtEvent.Type<LoadDataSourceEventHandler> TYPE = new Type<LoadDataSourceEventHandler>();

	@Override
	public Type<LoadDataSourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadDataSourceEventHandler handler) {
		handler.onLoadDataSource(this);	
	}
	
	public LoadDataSourceEvent() {
	}
}
