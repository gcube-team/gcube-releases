/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SetCommonNamesEvent extends GwtEvent<SetCommonNamesEventHandler> {
	
	public static final GwtEvent.Type<SetCommonNamesEventHandler> TYPE = new Type<SetCommonNamesEventHandler>();
	private BaseModelData baseModelData;

	@Override
	public Type<SetCommonNamesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCommonNamesEventHandler handler) {
		handler.onSetCommonNames(this);	
	}
	
	public SetCommonNamesEvent(BaseModelData data) {
		this.baseModelData = data;
	}

	public BaseModelData getBaseModelData() {
		return baseModelData;
	}

}
