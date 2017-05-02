package org.gcube.portlets.user.workspaceexplorerapp.client.resources.newres;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface WorkspaceExplorerIcons extends ClientBundle {

	/** Get access to the css resource during gwt compilation */
	@Source("baseIcons.css")
	CssResource css();

	@Source("cancel.png")
	ImageResource cancel();

	/**
	 * @return
	 */
	@Source("loading.gif")
	ImageResource loading();

	/**
	 * Our sample image icon. Makes the image resource for the gwt-compiler's
	 * css composer accessible
	 */
	@Source("loading.gif")
	ImageResource logo();

	@Source("vre_folder.png")
	ImageResource vre_folder();

	@Source("new_folder.png")
	ImageResource new_folder();

	@Source("home.png")
	ImageResource home();

	@Source("info-icon.png")
	ImageResource info();
}

