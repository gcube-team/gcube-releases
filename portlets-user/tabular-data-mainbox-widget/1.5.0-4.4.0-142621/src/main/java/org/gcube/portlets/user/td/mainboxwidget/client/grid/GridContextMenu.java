package org.gcube.portlets.user.td.mainboxwidget.client.grid;

import java.util.ArrayList;

import org.gcube.portlets.user.td.mainboxwidget.client.resources.MainboxResources;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.GridContextMenuItemEvent;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.GridOperationId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;
import org.gcube.portlets.user.tdwx.client.TabularDataXGridPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 *         Defines the context menu of grid
 */
public class GridContextMenu {
	protected Menu tableContextMenu;
	protected final TabularDataXGridPanel gridPanel;
	protected EventBus eventBus;

	public GridContextMenu(final TabularDataXGridPanel gridPanel,
			final EventBus eventBus) {
		this.gridPanel = gridPanel;
		this.eventBus = eventBus;
		tableContextMenu = new Menu();
		GridContextMenuMessages msgs = GWT.create(GridContextMenuMessages.class);
		
		MenuItem addRowItem = new MenuItem(msgs.addRowItem());
		addRowItem.setId(GridOperationId.ROWADD.toString());
		addRowItem.setIcon(MainboxResources.INSTANCE.rowInsert());
		addRowItem.setToolTip(msgs.addRowItemToolTip());
		addRowItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				//Log.debug("gridPanel is: " + gridPanel);
				if (gridPanel != null) {
					GridContextMenuItemEvent eventGridContextMenu = new GridContextMenuItemEvent(
							GridOperationId.ROWADD);
					eventBus.fireEvent(eventGridContextMenu);

				}

			}
		});
		tableContextMenu.add(addRowItem);

		MenuItem editRowItem = new MenuItem(msgs.editRowItem());
		editRowItem.setId(GridOperationId.ROWEDIT.toString());
		editRowItem.setIcon(MainboxResources.INSTANCE.rowEdit());
		editRowItem.setToolTip(msgs.editRowItemToolTip());
		editRowItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				//Log.debug("gridPanel is: " + gridPanel);
				if (gridPanel != null) {

					ArrayList<RowRaw> rows = gridPanel.getSelectedRowsAsRaw();
					if (rows != null) {
						GridContextMenuItemEvent eventGridContextMenu = new GridContextMenuItemEvent(
								GridOperationId.ROWEDIT);
						eventGridContextMenu.setRowsRaw(rows);
						eventBus.fireEvent(eventGridContextMenu);
					}
				}
			}
		});
		tableContextMenu.add(editRowItem);

		MenuItem deleteRowItem = new MenuItem(msgs.deleteRowItem());
		deleteRowItem.setId(GridOperationId.ROWDELETE.toString());
		deleteRowItem.setIcon(MainboxResources.INSTANCE.tableRowDeleteSelected());
		deleteRowItem.setToolTip(msgs.deleteRowItemToolTip());
		deleteRowItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				//Log.debug("gridPanel is: " + gridPanel);
				if (gridPanel != null) {
					ArrayList<String> rows = gridPanel.getSelectedRowsId();
					GridContextMenuItemEvent eventGridContextMenu = new GridContextMenuItemEvent(
							GridOperationId.ROWDELETE);
					eventGridContextMenu.setRows(rows);
					eventBus.fireEvent(eventGridContextMenu);
				}

			}
		});
		tableContextMenu.add(deleteRowItem);

		MenuItem replaceRowsItem = new MenuItem(msgs.replaceRowsItem());
		replaceRowsItem.setId(GridOperationId.REPLACE.toString());
		replaceRowsItem.setIcon(MainboxResources.INSTANCE.columnReplaceAll());
		replaceRowsItem.setToolTip(msgs.replaceRowsItemToolTip());
		replaceRowsItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				//Log.debug("gridPanel is: " + gridPanel);
				if (gridPanel != null) {
					CellData cellData = gridPanel.getSelectedCell();
					if (cellData != null) {
						GridContextMenuItemEvent eventGridContextMenu = new GridContextMenuItemEvent(
								GridOperationId.REPLACE, cellData);
						eventBus.fireEvent(eventGridContextMenu);
					} else {

					}
				}

			}
		});
		tableContextMenu.add(replaceRowsItem);

	}

	/**
	 * 
	 * @return context menu for grid
	 */
	public Menu getMenu() {
		return tableContextMenu;
	}
}
