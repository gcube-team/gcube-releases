/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.grid;

import gwt.material.design.client.ui.MaterialLink;

import org.gcube.portlets.user.workspaceexplorerapp.client.download.DownloadType;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.DownloadItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 8, 2016
 */
public class MenuMoreOptionsOnItem extends Composite {

	private static MenuMoreOptionsOnItemUiBinder uiBinder =
		GWT.create(MenuMoreOptionsOnItemUiBinder.class);

	interface MenuMoreOptionsOnItemUiBinder
		extends UiBinder<Widget, MenuMoreOptionsOnItem> {
	}

	@UiField
	MaterialLink open;

	@UiField
	MaterialLink download;

	private HandlerManager eventBus;

	private ItemInterface itemToDownload;

	private Command commandOnClick;

	public MenuMoreOptionsOnItem(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		this.eventBus = eventBus;
		download.addClickHandler(new ClickHandler() {

			/* (non-Javadoc)
			 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
			 */
			@Override
			public void onClick(ClickEvent event) {

				MenuMoreOptionsOnItem.this.eventBus.fireEvent(new DownloadItemEvent(itemToDownload, DownloadType.DOWNLOAD));
				GWT.log("commandOnClick " + commandOnClick);
				if(commandOnClick!=null)
					commandOnClick.execute();
			}
		});

		open.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MenuMoreOptionsOnItem.this.eventBus.fireEvent(new DownloadItemEvent(itemToDownload, DownloadType.OPEN));
				GWT.log("commandOnClick " + commandOnClick);
				if(commandOnClick!=null)
					commandOnClick.execute();
			}
		});

	}


	/**
	 * Inits the on item.
	 *
	 * @param itemToDownload the item to download
	 * @param commandOnClick the command on click
	 */
	public void initOnItem(ItemInterface itemToDownload, Command commandOnClick){
		this.itemToDownload = itemToDownload;
		this.commandOnClick = commandOnClick;
	}
}
