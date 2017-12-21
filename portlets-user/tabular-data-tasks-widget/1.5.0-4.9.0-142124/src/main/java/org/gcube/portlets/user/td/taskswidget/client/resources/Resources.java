/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 30, 2014
 *
 */
public interface Resources extends ClientBundle {

	public static final Resources INSTANCE = GWT.create(Resources.class);
	
	@Source("arrow-down1.png")
	ImageResource getArrowDown();

	@Source("arrow-right1.png")
	ImageResource getArrowRight();

	@Source("reload-icon.png")
	ImageResource getReload();

	@Source("working.gif")
	ImageResource working();
	
	@Source("success.png")
	ImageResource success();
	
	@Source("clock.png")
	ImageResource clock();
	
	@Source("initializing.png")
	ImageResource initializing();
	
	@Source("fail-icon.png")
	ImageResource failicon();
	
	@Source("attention.png")
	ImageResource attention();
	
	@Source("attention.png")
	ImageResource stopped();
	
	@Source("unknown.png")
	ImageResource unknown();
	
	@Source("info.png")
	ImageResource info();
	
	@Source("pending.png")
	ImageResource pending();
	
	@Source("validating.png")
	ImageResource validating();
	
	@Source("validating2.png")
	ImageResource validating2();
	
	@Source("stop.png")
	ImageResource stop();

}
