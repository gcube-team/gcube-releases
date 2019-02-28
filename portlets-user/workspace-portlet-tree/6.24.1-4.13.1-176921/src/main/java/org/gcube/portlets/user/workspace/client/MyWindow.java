package org.gcube.portlets.user.workspace.client;

import com.google.gwt.core.client.JavaScriptObject;

public class MyWindow extends JavaScriptObject {
	  // All types that extend JavaScriptObject must have a protected,
	  // no-args constructor. 
	  protected MyWindow() {}

	  public final static native MyWindow open(String url, String target, String options) /*-{
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