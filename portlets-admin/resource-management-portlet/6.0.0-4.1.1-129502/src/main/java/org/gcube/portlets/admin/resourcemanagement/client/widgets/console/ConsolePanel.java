/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ConsolePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.console;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.DetachablePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.DetachablePanel.DetachablePanelHandler;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.ScrollablePanel;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * Consists of a widgets containing a grid with
 * all the {@link ConsoleMessage} raised in the system.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ConsolePanel implements DetachablePanelHandler {
	private GroupingStore<ConsoleMessage> store;
	private DetachablePanel rootPanel = null;
	private boolean enableGrouping = false;
	private Grid<ConsoleMessage> grid  = null;
	// The column on which the grouping is applied
	private final static String collapsibleColumn = "type";

	public ConsolePanel(final DetachablePanel rootPanel) {
		super();
		this.rootPanel = rootPanel;
		this.rootPanel.addHandler(this);

		store = new GroupingStore<ConsoleMessage>();
		if (enableGrouping) {
			store.groupBy(collapsibleColumn);
		}
		store.sort("timestamp", SortDir.DESC);
		this.onLoad();
	}

	private void onLoad() {
		this.grid = createGrid();
		ScrollablePanel gridContainer = new ScrollablePanel("console-grid-container", grid);
		rootPanel.insertMainWidget(gridContainer);
		//rootPanel.getToolBar().
		Button groupButton = new Button("Group/Ungroup") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				if (enableGrouping) {
					store.clearGrouping();
				} else {
					store.groupBy(collapsibleColumn);
				}
				enableGrouping = !enableGrouping;
			}
		};
		groupButton.setIconStyle("grid-icon");
		rootPanel.getToolBar().add(groupButton);

		Button clearButton = new Button("Clear") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				store.removeAll();
			}
		};
		clearButton.setIconStyle("clear-icon");
		rootPanel.getToolBar().add(clearButton);
	}

	private Grid<ConsoleMessage> createGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("timestamp");
		column.setHeader("Time");
		column.setWidth(105);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("invoker");
		column.setHeader("Invoker");
		column.setWidth(420);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("type");
		column.setHeader("Type");
		column.setWidth(75);

		column.setRenderer(new GridCellRenderer<ConsoleMessage>() {
			public Object render(
					final ConsoleMessage model,
					final String property,
					final ColumnData config,
					final int rowIndex,
					final int colIndex,
					final ListStore<ConsoleMessage> store,
					final Grid<ConsoleMessage> grid
			) {
				if (model.get("type") == ConsoleLogSeverity.WARNING) {
					return "<img src=\"/rm/images/icons/warning.png\" width=\"10\"><font color=\"#F88017\">" + model.get("type") + "</font>";
				}
				if (model.get("type") == ConsoleLogSeverity.ERROR) {
					return "<img src=\"/rm/images/icons/error.png\" width=\"10\"><font color=\"red\">" + model.get("type") + "</font>";
				}
				return "<img src=\"/rm/images/icons/log.png\" width=\"10\">" + model.get("type");
			}
		});

		configs.add(column);

		column = new ColumnConfig();
		column.setId("message");
		column.setHeader("Message");
		configs.add(column);

		final ColumnModel cm = new ColumnModel(configs);

		// Builds the grouping structure to collapse elements
		// having the same type (log severity).
		GroupingView view = new GroupingView();
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(final GroupColumnData data) {
				int s = data.models.size();
				String f = cm.getColumnById(data.field).getHeader();
				String l = s == 1 ? "Item" : "Items";
				return f + ": " + data.group + " (" + s + " " + l + ")";
			}
		});
		view.setShowGroupedColumn(false);

		Grid<ConsoleMessage> grid = new Grid<ConsoleMessage>(store, cm);
		grid.setView(view);
		grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn("message");
		grid.setId("console-internal-grid");
		return grid;
	}

	public final boolean handle(final ConsoleMessage event) {
		try {
			store.add(event);
			store.commitChanges();
			//this.rootPanel.layout(true);
			//this.refresh();
			return true;
		} catch (Exception e) {
			GWT.log("During console handle", e);
			return false;
		}
	}

	/**
	 * Is invoked by the time renderer on client side.
	 * It forces the row to refresh after it has received from
	 * the server side the required data.
	 * @see ConsoleMessage
	 */
	public final void refresh() {
		this.rootPanel.layout(true);
		this.rootPanel.getRootPanel().sync(true);
		this.grid.sync(true);
		this.grid.getView().refresh(true);
		this.store.commitChanges();
		// Reapplies the sorting
		this.store.sort(this.store.getSortField(), this.store.getSortDir());
	}

	public final DetachablePanel getContainer() {
		return this.rootPanel;
	}

	public final Widget getWidget() {
		return this.grid;
	}

	public final void onDetachEvent(final DetachablePanel container, final Component content) {
		this.grid.getView().refresh(true);
	}

	public final void onEmbedEvent(final DetachablePanel container, final Component content) {
		this.grid.getView().refresh(true);
	}
}
