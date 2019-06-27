/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class CompletedLoadDataSourceEvent extends GwtEvent<CompletedLoadDataSourceEventHandler> {
	
	public static final GwtEvent.Type<CompletedLoadDataSourceEventHandler> TYPE = new Type<CompletedLoadDataSourceEventHandler>();

	@Override
	public Type<CompletedLoadDataSourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompletedLoadDataSourceEventHandler handler) {
		handler.onCompletedLoadDataSource(this);	
	}
	
	public CompletedLoadDataSourceEvent() {
	}
}
