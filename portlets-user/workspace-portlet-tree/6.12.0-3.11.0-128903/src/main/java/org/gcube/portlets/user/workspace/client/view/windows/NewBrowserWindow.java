package org.gcube.portlets.user.workspace.client.view.windows;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 4, 2013
 * 
 */
public final class NewBrowserWindow extends JavaScriptObject {
	// All types that extend JavaScriptObject must have a protected,
	// no-args constructor.
	protected NewBrowserWindow() {
	}

	public static native NewBrowserWindow open(String url, String target,
			String options) /*-{
		return $wnd.open(url, target, options);
	}-*/;

	public native void close() /*-{
		this.close();
	}-*/;

	public native void setUrl(String url) /*-{
		if (this.location) {
			this.location = url;
		}
	}-*/;
}
