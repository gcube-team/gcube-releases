package org.gcube.portlets.user.workspace.client.view.toolbars;

import java.util.List;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.user.client.ui.Composite;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtBreadcrumbPathPanel {

	private static final String ROOT_NAME = "";
	private Breadcrumbs breadCrumbs = new Breadcrumbs(ROOT_NAME);
	

	public GxtBreadcrumbPathPanel() {
	}
	
	public Composite getToolBarPathPanel() {
		return breadCrumbs;
	}

	public void setPath(List<FileModel> parents) {
		breadCrumbs.setPath(parents);
	}
	
	public boolean breadcrumbIsEmpty(){
		return breadCrumbs.breadcrumbIsEmpty();
	}
	
	public FileModel getLastParent(){
		return breadCrumbs.getLastParent();
	}

	/**
	 * 
	 */
	public void refreshSize() {
		// TODO Auto-generated method stub
	}
}
