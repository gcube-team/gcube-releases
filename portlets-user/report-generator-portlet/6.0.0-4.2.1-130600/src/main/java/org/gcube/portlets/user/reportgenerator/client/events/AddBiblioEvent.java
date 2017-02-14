package org.gcube.portlets.user.reportgenerator.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class AddBiblioEvent extends GwtEvent<AddBiblioEventHandler>{
	public static Type<AddBiblioEventHandler> TYPE = new Type<AddBiblioEventHandler>();
	private final String citekey;
	private final String citetext;

	public AddBiblioEvent(String key, String text) {
		citekey = key;
		citetext = text;
	}

	public String getCitekey() {
		return citekey;
	}

	public String getCitetext() {
		return citetext;
	}

	@Override
	public Type<AddBiblioEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddBiblioEventHandler handler) {
		handler.onAddCitation(this);
	}
}
