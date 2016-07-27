package org.gcube.portlets.widgets.pickitem.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface CssAndImages extends ClientBundle {

	public static final CssAndImages INSTANCE = GWT.create(CssAndImages.class);

	@Source("PickItem.css")
	public CssResource css();
	
	@Source("team-icon.gif")
	public ImageResource iconTeam();
	
}
