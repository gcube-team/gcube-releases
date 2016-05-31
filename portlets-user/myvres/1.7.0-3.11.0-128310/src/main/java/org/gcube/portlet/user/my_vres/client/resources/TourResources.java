package org.gcube.portlet.user.my_vres.client.resources;

import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;

import com.google.gwt.resources.client.ClientBundle;

public interface TourResources extends ClientBundle {
	 
	  @Source("GuidedTour.xml")
	  GuidedTourResource quickTour();

}