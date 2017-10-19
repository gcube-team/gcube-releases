package org.gcube.portlets.admin.fhn_manager_portlet.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {

	public static final Icons ICONS = GWT.create(Icons.class);
	
	
	
	@Source("delete.png")
	ImageResource delete();

	@Source("new.png")
	ImageResource create();
	
	@Source("refresh.png")
	ImageResource refresh();
	
	@Source("magnifier.png")
	ImageResource inspect();
	
	@Source("start.png")
	ImageResource start();
	
	@Source("stop.png")
	ImageResource stop();
	
	@Source("loading.gif")
	ImageResource loading();
	
	@Source("error.png")
	ImageResource error();
	
	@Source("success.png")
	ImageResource success();
	
	@Source("no.png")
	ImageResource close();
	
	
	// RESOURCE
	
	@Source("provider.png")
	ImageResource vmProvider();
	
	@Source("remoteNode.png")
	ImageResource remoteNode();
	
	@Source("serviceProfile.png")
	ImageResource serviceProfile();
	
	@Source("vmTemplate.png")
	ImageResource vmTemplate();
}
