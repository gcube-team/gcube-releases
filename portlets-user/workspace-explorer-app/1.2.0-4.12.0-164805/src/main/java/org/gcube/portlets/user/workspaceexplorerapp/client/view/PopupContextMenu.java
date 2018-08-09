/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.view;

import org.gcube.portlets.user.workspaceexplorerapp.client.grid.MenuMoreOptionsOnItem;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemInterface;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * The Class PopupContextMenu.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 11, 2016
 */
public class PopupContextMenu extends PopupPanel{


	private MenuMoreOptionsOnItem menuOpts;

	/**
	 * Instantiates a new popup context menu.
	 *
	 * @param autohide the autohide
	 * @param eventBus the event bus
	 * @param itemToDownload the item to download
	 */
	public PopupContextMenu(boolean autohide, HandlerManager eventBus, ItemInterface itemToDownload) {
		super(autohide);
		GWT.log("PopupContextMenu opened");
		menuOpts = new MenuMoreOptionsOnItem(eventBus);
		setWidth("100px");
		addStyleName("context-menu-we");

		Command cmd = new Command() {

			@Override
			public void execute() {
				hide();
			}
		};

		menuOpts.initOnItem(itemToDownload, cmd);
		add(menuOpts);
	}

	/**
	 * Show popup at x,y position
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void showPopup(int x, int y){

		setPopupPosition(x, y);
		show();
	}
}
