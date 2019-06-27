package org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class BrowserWindowSupport  extends JavaScriptObject {
	// All types that extend JavaScriptObject must have a protected,
	// no-args constructor.
	protected BrowserWindowSupport() {
	}

	public static native BrowserWindowSupport open(String url, String target,
			String options) /*-{
		return $wnd.open(url, target, options);
	}-*/;

	public final native void close() /*-{
		this.close();
	}-*/;

	public final native void setUrl(String url) /*-{
		if (this.location) {
			this.location = url;
		}
	}-*/;
}