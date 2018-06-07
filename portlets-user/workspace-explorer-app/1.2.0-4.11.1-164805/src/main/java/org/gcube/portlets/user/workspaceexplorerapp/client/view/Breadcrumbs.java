/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.view;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.user.workspaceexplorerapp.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.BreadcrumbInitEvent;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class Breadcrumbs.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 23, 2015
 */
public class Breadcrumbs extends Composite {

	public static final String DIVIDER = "/";
	private LinkedHashMap<String, Item> hashListItems = new LinkedHashMap<String, Item>();// Ordered-HashMap
	private Item lastParent;
	private static BreadcrumbsUiBinder uiBinder = GWT.create(BreadcrumbsUiBinder.class);

	@UiField
	com.github.gwtbootstrap.client.ui.Breadcrumbs breadcrumbs;

	private HandlerManager eventBus;

	/**
	 * The Interface BreadcrumbsUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jun 23,
	 *         2015
	 */
	interface BreadcrumbsUiBinder extends UiBinder<Widget, Breadcrumbs> {
	}

	/**
	 * Instantiates a new breadcrumbs.
	 */
	public Breadcrumbs(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().setId("breadcrumbs_we");
		this.getElement().setAttribute("id", "breadcrumbs_we");
		this.setStyleName("breadcrumbs-we");
		this.eventBus = eventBus;
		breadcrumbs.setDivider(DIVIDER);
//		init();
		// initBreadcrumb(true);

	}

	/**
	 * Sets the path.
	 *
	 * @param parents
	 *            the new path
	 */
	public void setPath(List<Item> parents) {
		resetBreadcrumbs();
		if(parents!=null){
			GWT.log("parent size is: "+parents.size());
			if (parents.size() > 0) {
				for (Item parent : parents) {
					GWT.log("parent is: "+parent);
					if(parent!=null){ //PARENTS NULL ARE SKIPPED, THEY ARE NULL WHEN BREADCRUMB LIMIT IS reached
						addNavigationLink(parent);
						hashListItems.put(parent.getId(), parent);
					}
				}
				lastParent = parents.get(parents.size() - 1);
			}
		}
	}

	/**
	 * Checks if is root or special folder.
	 *
	 * @param item the item
	 * @return true, if is root or special folder
	 */
	private boolean isRootOrSpecialFolder(Item item){
		GWT.log(item.getName() +" is root "+(item.getParent()==null));
		GWT.log(item.getName() +" is special folder "+item.isSpecialFolder());
		return item.getParent()==null || item.isSpecialFolder();
	}

	/**
	 * Reset breadcrumbs.
	 */
	private void resetBreadcrumbs() {
		breadcrumbs.clear();
		hashListItems.clear();
	}

	// @UiHandler("add")

	/**
	 * Adds the navigation link.
	 *
	 * @param parent the parent
	 */
	private void addNavigationLink(Item parent) {
		if(parent==null)
			return;

		final NavLink navLink = new NavLink(parent.getName());
		navLink.setName(parent.getId());
		breadcrumbs.add(navLink);
		navLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Item target = hashListItems.get(navLink.getName());
//				WorkspaceExplorerController.eventBus.fireEvent(new LoadFolderEvent(target));
				eventBus.fireEvent(new BreadcrumbClickEvent(target));
			}
		});
	}

	/**
	 * Breadcrumb is empty.
	 *
	 * @return true, if successful
	 */
	public boolean breadcrumbIsEmpty() {

		if (hashListItems.size() == 0)
			return true;

		return false;
	}

	/**
	 * Gets the last parent.
	 *
	 * @return the last parent
	 */
	public Item getLastParent() {
		return lastParent;
	}


	/**
	 * Inits the.
	 *
	 * @param item the item
	 */
	public void init(Item item) {
		resetBreadcrumbs();
		addNavigationLink(item);
		hashListItems.put(item.getId(), item);
		lastParent = item;
		eventBus.fireEvent(new BreadcrumbInitEvent(item));
	}


	/**
	 * Clear.
	 */
	public void clear() {
		resetBreadcrumbs();
	}
}