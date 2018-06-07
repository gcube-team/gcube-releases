package org.gcube.portlets.admin.tokengeneratoradmin.client;

import org.gcube.portlets.admin.tokengeneratoradmin.client.ui.TokenWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TokenGeneratorAdmin implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("token-generator-admin-DIV").add(new TokenWidget());
		RootPanel.get("token-generator-admin-DIV").getElement().getStyle().setPadding(10, Unit.PX);
	}
}
