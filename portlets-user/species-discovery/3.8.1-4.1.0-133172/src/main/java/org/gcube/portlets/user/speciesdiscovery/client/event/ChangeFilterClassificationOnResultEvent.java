/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ChangeFilterClassificationOnResultEvent extends GwtEvent<ChangeFilterClassificationOnResultEventHandler> {
	
	public static final GwtEvent.Type<ChangeFilterClassificationOnResultEventHandler> TYPE = new Type<ChangeFilterClassificationOnResultEventHandler>();

	@Override
	public Type<ChangeFilterClassificationOnResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeFilterClassificationOnResultEventHandler handler) {
		handler.onChangeFilter(this);	
	}
}
