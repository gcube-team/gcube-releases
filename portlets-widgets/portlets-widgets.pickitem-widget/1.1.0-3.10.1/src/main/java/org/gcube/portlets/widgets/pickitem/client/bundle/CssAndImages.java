package org.gcube.portlets.widgets.pickitem.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface CssAndImages extends ClientBundle {

	public static final CssAndImages INSTANCE = GWT.create(CssAndImages.class);

	@Source("PickItem.css")
	public CssResource css();
	
}
