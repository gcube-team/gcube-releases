package org.gcube.portlets.user.workspace.client.view.toolbars;

import java.util.List;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.user.client.ui.Composite;


/**
 * The Class GxtBreadcrumbPathPanel.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 9, 2018
 */
public class GxtBreadcrumbPathPanel {

	private static final String ROOT_NAME = "";
	private Breadcrumbs breadCrumbs = new Breadcrumbs(ROOT_NAME);


	/**
	 * Instantiates a new gxt breadcrumb path panel.
	 */
	public GxtBreadcrumbPathPanel() {
	}

	/**
	 * Gets the tool bar path panel.
	 *
	 * @return the tool bar path panel
	 */
	public Composite getToolBarPathPanel() {
		return breadCrumbs;
	}

	/**
	 * Sets the path.
	 *
	 * @param parents the new path
	 */
	public void setPath(List<FileModel> parents) {
		breadCrumbs.setPath(parents);
	}

	/**
	 * Breadcrumb is empty.
	 *
	 * @return true, if successful
	 */
	public boolean breadcrumbIsEmpty(){
		return breadCrumbs.breadcrumbIsEmpty();
	}

	/**
	 * Gets the last parent.
	 *
	 * @return the last parent
	 */
	public FileModel getLastParent(){
		return breadCrumbs.getLastParent();
	}

	/**
	 * Gets the parent folder.
	 *
	 * @param folderId the folder id
	 * @return the parent folder
	 */
	public FileModel getParentFolder(String folderId){

		if(folderId==null)
			return null;

		return breadCrumbs.getParentFolder(folderId);
	}

	/**
	 * Refresh size.
	 */
	public void refreshSize() {
		// TODO Auto-generated method stub
	}
}
