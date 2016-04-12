package org.gcube.portlets.user.tokengenerator.client;

import org.gcube.portlets.user.tokengenerator.client.ui.TokenWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI CNR
 * @author Costantino Perciante, ISTI CNR
 *
 */
public class TokenGenerator implements EntryPoint {


	public void onModuleLoad() {

		// check if jQuery is available
		boolean jQueryLoaded = isjQueryLoaded();

		if(jQueryLoaded)
			GWT.log("Injecting : http://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js");
		else{
			ScriptInjector.fromUrl("http://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js")
			.setWindow(ScriptInjector.TOP_WINDOW)
			.inject();
		}

		RootPanel.get("token-generator-DIV").add(new TokenWidget());
	}

	/**
	 * Checks if jQuery is loaded.
	 *
	 * @return true, if jQuery is loaded, false otherwise
	 */
	private native boolean isjQueryLoaded() /*-{

		return (typeof $wnd['jQuery'] !== 'undefined');

	}-*/;
}
