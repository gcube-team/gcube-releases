/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * The Interface WorkspaceLightTreeResources.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 8, 2015
 */
public interface WorkspaceExplorerResources extends ClientBundle {

	public static final WorkspaceExplorerResources INSTANCE =  GWT.create(WorkspaceExplorerResources.class);

	/**
	 * Root.
	 *
	 * @return the image resource
	 */
	@Source("_32/folder.png")
	ImageResource folder();

	/**
	 * Folder.
	 *
	 * @return the image resource
	 */
	@Source("_32/groups_folder.png")
	ImageResource shared_folder();


	/**
	 * Warning.
	 *
	 * @return the image resource
	 */
	@Source("warning.png")
	ImageResource warning();



	/**
	 * E unhappy.
	 *
	 * @return the image resource
	 */
	@Source("e-unhappy.png")
	ImageResource eUnhappy();

}
