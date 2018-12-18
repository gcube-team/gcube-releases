/**
 *
 */
package org.gcube.portlets.user.workspace.client.view.toolbars;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.gridevent.PathElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class Breadcrumbs.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 9, 2018
 */
public class Breadcrumbs extends Composite {

	public static final String DIVIDER = ">";
	private String rootName;
	private LinkedHashMap<String, FileModel> hashFileModel = new LinkedHashMap<String, FileModel>();// Ordered-HashMap
	private FileModel lastParent;
	private static BreadcrumbsUiBinder uiBinder = GWT.create(BreadcrumbsUiBinder.class);

	@UiField
	com.github.gwtbootstrap.client.ui.Breadcrumbs breadcrumbs;

	/**
	 * The Interface BreadcrumbsUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Dec 14, 2015
	 */
	interface BreadcrumbsUiBinder extends UiBinder<Widget, Breadcrumbs> {
	}

	/**
	 * Instantiates a new breadcrumbs.
	 *
	 * @param rootName the root name
	 */
	public Breadcrumbs(String rootName) {
		this.rootName = rootName;
		initWidget(uiBinder.createAndBindUi(this));
		breadcrumbs.setDivider(DIVIDER);
		// initBreadcrumb(true);

		Element ul = this.getElement().getFirstChildElement();
		ul.removeClassName("breadcrumb");
		ul.addClassName("Breadcrumbs-Personal");
	}

	/**
	 * Sets the path.
	 *
	 * @param parents the new path
	 */
	public void setPath(List<FileModel> parents) {

		resetBreadcrumbs();

		if (parents != null && parents.size() > 0) {

			hashFileModel = new LinkedHashMap<String, FileModel>();

			for (FileModel parent : parents) {
				if(parent!=null){
					addNavigationLink(parent);
					hashFileModel.put(parent.getIdentifier(), parent);
				}
			}

			lastParent = parents.get(parents.size() - 1);
		}
	}



	/**
	 * Gets the parent folder.
	 *
	 * @param folderId the folder id
	 * @return the parent folder
	 */
	public FileModel getParentFolder(String folderId){

		return hashFileModel.get(folderId);

	}



	/**
	 * Reset breadcrumbs.
	 */
	private void resetBreadcrumbs() {
		breadcrumbs.clear();
	}

	// @UiHandler("add")
	/**
	 * Adds the navigation link.
	 *
	 * @param parent the parent
	 */
	public void addNavigationLink(FileModel parent) {

		String linkName = parent.getName();

		if(parent.isRoot())
			linkName = "Workspace";

		final NavLink navLink = new NavLink(linkName);
		navLink.setName(parent.getIdentifier());
		breadcrumbs.add(navLink);

		navLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				FileModel target = hashFileModel.get(navLink.getName());
				AppController.getEventBus().fireEvent(new PathElementSelectedEvent(target));
			}
		});
	}

	/**
	 * Breadcrumb is empty.
	 *
	 * @return true, if successful
	 */
	public boolean breadcrumbIsEmpty() {

		if (hashFileModel.size() == 0)
			return true;

		return false;
	}

	/**
	 * Gets the last parent.
	 *
	 * @return the last parent
	 */
	public FileModel getLastParent() {
		return lastParent;
	}

}