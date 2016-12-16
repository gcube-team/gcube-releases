package org.gcube.portlets.widgets.sessionchecker.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface CheckSessionBundle extends ClientBundle {

	public static final CheckSessionBundle INSTANCE = GWT.create(CheckSessionBundle.class);

	@Source("CheckSession.css")
	public CssResource css();

	@Source("session_expired.jpg")
	ImageResource expired();
}
