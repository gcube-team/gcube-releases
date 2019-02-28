package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowRevertOperationWidgetEvent extends GwtEvent<ShowRevertOperationWidgetEventHandler> {
	
	public static Type<ShowRevertOperationWidgetEventHandler> TYPE = new Type<ShowRevertOperationWidgetEventHandler>();
	
	private String encryptedUrl;
	
	/**
	 * 
	 * @param encryptedUrl
	 */
	public ShowRevertOperationWidgetEvent(String encryptedUrl) {
		this.encryptedUrl = encryptedUrl;
	}
	
	public String getEncryptedUrl() {
		return encryptedUrl;
	}

	public void setEncryptedUrl(String encryptedUrl) {
		this.encryptedUrl = encryptedUrl;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ShowRevertOperationWidgetEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowRevertOperationWidgetEventHandler handler) {
		handler.onShowRevertOperationWidgetEvent(this);
	}

}
