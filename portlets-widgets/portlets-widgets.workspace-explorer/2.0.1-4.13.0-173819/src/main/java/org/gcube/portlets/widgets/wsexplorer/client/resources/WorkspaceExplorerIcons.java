package org.gcube.portlets.widgets.wsexplorer.client.resources;

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

	@Source("baseline_folder_special_white_24dp.png")
	ImageResource vre_folder();

	@Source("baseline_create_new_folder_white_24dp.png")
	ImageResource new_folder();

	@Source("baseline_home_white_24dp.png")
	ImageResource home();

	@Source("info-icon.png")
	ImageResource info();

	/**
	 * @return
	 */

	@Source("info-square.png")
	ImageResource infoSquare();
}

