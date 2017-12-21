/**
 * 
 */
package org.gcube.portlets.admin.authportletmanager.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public interface AuthResources extends ClientBundle {

	public static final AuthResources INSTANCE = GWT
			.create(AuthResources.class);

	@Source("Auth.css")
	AuthCSS authCSS();
	
	

	@Source("accounting-manager_128.png")
	ImageResource accountingManager128();

	@Source("loaderHorizontal.gif")
	ImageResource loaderIcon();


}
