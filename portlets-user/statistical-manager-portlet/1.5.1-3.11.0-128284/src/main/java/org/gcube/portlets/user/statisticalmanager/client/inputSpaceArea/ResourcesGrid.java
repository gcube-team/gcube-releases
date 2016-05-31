/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManagerPortletServiceAsync;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem.Provenance;
import org.gcube.portlets.user.statisticalmanager.client.events.JobsGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.TablesGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;
import org.gcube.portlets.user.statisticalmanager.client.widgets.ResourcesExporter;
import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.user.tdw.client.TabularDataGridPanel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.widget.core.client.Dialog;

/**
 * @author ceras
 *
 */
public class ResourcesGrid extends TabItem {

	private Grid<ResourceItem> grid;
	private GroupingView view;
	protected static final String ERROR_GET_OPERATORS = "Operators not loaded.";
	private static final String LOADING_OPERATORS_MESSAGE = "Loading Operators...";
	private static final ImageResource ICON_CANCEL = StatisticalManager.resources
			.cancel();
	private static final ImageResource ICON_OPEN = StatisticalManager.resources
			.table();
	private static final ImageResource ICON_EXPORT = StatisticalManager.resources
			.save();
	protected static final String TEMPLATE_COLUMN = "template";
	protected static final String PROVENANCE_COLUMN = "provenance";
	private BaseListLoader<ListLoadResult<ResourceItem>> loader;
	private ContentPanel contentPanel = new ContentPanel();
	boolean dirty = true;
	private StatisticalManagerPortletServiceAsync service = StatisticalManager
			.getService();

	public ResourcesGrid() {
		super(".: Data Sets");
		bind();

		this.setIcon(Images.table());
		this.setScrollMode(Scroll.AUTO);
		this.setLayout(new FitLayout());
		contentPanel.setHeaderVisible(false);
		contentPanel.setLayout(new FitLayout());
		contentPanel.setBodyBorder(false);
		this.add(contentPanel);
		setToolBar();
	}

	private void bind() {
		EventBusProvider.getInstance().addHandler(
				TablesGridGotDirtyEvent.getType(),
				new TablesGridGotDirtyEvent.TablesGridGotDirtyHandler() {
					@Override
					public void onTablesGridGotDirty(
							TablesGridGotDirtyEvent event) {
						dirty = true;
					}
				});
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		// if the operators classification is not loaded, let's load by an rpc
		if (StatisticalManager.getOperatorsClassifications() == null) {
			this.mask(LOADING_OPERATORS_MESSAGE, Constants.maskLoadingStyle);
			service.getOperatorsClassifications(new AsyncCallback<List<OperatorsClassification>>() {

				@Override
				public void onSuccess(List<OperatorsClassification> result) {
					unmask();
					StatisticalManager.setOperatorsClassifications(result);
					loadGrid();
				}

				@Override
				public void onFailure(Throwable caught) {
					unmask();
					MessageBox.alert("Error", ERROR_GET_OPERATORS, null);
				}
			});
		} else
			loadGrid();
	}

	/**
	 * 
	 */
	private void setToolBar() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Label("Tools&nbsp;&nbsp;"));

		Button refreshButton = new Button("Refresh Data Sets",
				Images.refresh(), new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (grid != null) {
							grid.getStore().getLoader().load();
						}
					}
				});

		Button expandButton = new Button("Expand groups", Images.expand(),
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (view != null)
							view.expandAllGroups();
					}
				});

		Button collapseButton = new Button("Collapse groups",
				Images.collapse(), new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (view != null)
							view.collapseAllGroups();
					}
				});

		final Button groupByButton = new Button("Group by provenance",
				Images.groupBy());
		groupByButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (grid != null) {
							GroupingStore<ResourceItem> store = (GroupingStore<ResourceItem>) (grid
									.getStore());
							String previousGrouping = store.getGroupState();
							String newGrouping = (previousGrouping
									.contentEquals(TEMPLATE_COLUMN) ? PROVENANCE_COLUMN
									: TEMPLATE_COLUMN);
							groupByButton.setText("Group by "
									+ previousGrouping);
							store.groupBy(newGrouping);
						}
					}
				});

		toolBar.add(refreshButton);
		toolBar.add(expandButton);
		toolBar.add(collapseButton);
		toolBar.add(groupByButton);
		contentPanel.setTopComponent(toolBar);
	}

	private void loadGrid() {
		// store creation
		RpcProxy<ListLoadResult<ResourceItem>> proxy = new RpcProxy<ListLoadResult<ResourceItem>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<ListLoadResult<ResourceItem>> callback) {
				service.getResourcesItems(callback);
			}
		};

		// loader creation
		loader = new BaseListLoader<ListLoadResult<ResourceItem>>(proxy);
		loader.addLoadListener(new LoadListener() {
			@Override
			public void loaderLoadException(LoadEvent le) {
				Throwable e = le.exception;
				MessageBox.alert("Error", e.getMessage(), null);
				// e.printStackTrace();
			}
		});
		loader.setRemoteSort(false);
		loader.setSortField("name");
		loader.setSortDir(SortDir.ASC);

		GroupingStore<ResourceItem> store = new GroupingStore<ResourceItem>(
				loader);
		store.sort("name", SortDir.ASC);
		store.setSortDir(SortDir.ASC);
		store.setSortField("name");
		store.setDefaultSort("name", SortDir.ASC);
		store.groupBy(TEMPLATE_COLUMN);
		// store.setGroupOnSort(true);
		// store.set

		// RENDERERS
		GridCellRenderer<ResourceItem> renderer = new GridCellRenderer<ResourceItem>() {
			@Override
			public String render(ResourceItem tableItem, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ResourceItem> store, Grid<ResourceItem> grid) {
				if (property.contentEquals(PROVENANCE_COLUMN)) {
					Provenance provenance = tableItem.getProvenance();
					if (provenance == null)
						return null;

					String str = provenance.toString();
					return "<img src='" + GWT.getModuleBaseURL()
							+ "../images/provenances/provenance" + str
							+ "2.png' border='1' alt='Provenance" + str
							+ "' />";

				} else if (property.contentEquals("operator")) {
					Operator op = tableItem.getOperator();
					return (op == null) ? tableItem.getOperatorId() : op
							.getName(); // TODO woth tooltip
				} else if (property.contentEquals("name"))
					return "<span class='dataSpace-grid-tableName'>"
							+ tableItem.getName() + "</span>";
				else if (property.contentEquals("template")) {
					String template = tableItem.getTemplate();
					if (template.contentEquals(Constants.realFileTemplate))
						return Constants.userFileTemplate;
					else
						return template;
				}

				return "";
			}
		};

		// COLUMNS
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column;
		// name
		column = new ColumnConfig("name", "Name", 200);
		column.setRenderer(renderer);
		configs.add(column);

		// description
		column = new ColumnConfig("description", "Description", 280);
		configs.add(column);

		// provenance image
		column = new ColumnConfig(PROVENANCE_COLUMN, "Provenance.", 100);
		column.setResizable(false);
		column.setRenderer(renderer);
		configs.add(column);

		// id
		column = new ColumnConfig("id", "Id", 150);
		configs.add(column);

		// creation date
		column = new ColumnConfig("creationDate", "Creation Date", 83);
		column.setDateTimeFormat(DateTimeFormat
				.getFormat("MM/dd/yyyy'<br/>'hh:mm:ss"));
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setResizable(false);
		configs.add(column);

		// operator
		column = new ColumnConfig("operator", "Operator", 200);
		column.setRenderer(renderer);
		configs.add(column);

		// template
		column = new ColumnConfig(TEMPLATE_COLUMN, "Template", 70);
		column.setRenderer(renderer);
		configs.add(column);

		// button open table
		configs.add(createOpenColumnButton());
		// button delete
		configs.add(createRemoveColumnButton());
		// button export
		configs.add(createExportColumnButton());

		final ColumnModel columnModel = new ColumnModel(configs);

		// GROUPING
		view = new GroupingView();
		view.setEnableGroupingMenu(false);
		view.setShowGroupedColumn(false);
		view.setForceFit(true);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				try {
					String l = data.models.size() == 1 ? "Item" : "Items";
					String field = data.field;
					String group = data.group;

					if (field.contentEquals(TEMPLATE_COLUMN)) {
						String templateTitle;
						if (group.contentEquals(Constants.realFileTemplate)) {
							group = Constants.userFileTemplate;
							templateTitle = "Resource " + group;
						} else
							templateTitle = "Template " + group;

						String imgTag = "<img class='dataSpace-grid-templateImg' src='"
								+ GWT.getModuleBaseURL()
								+ "../images/templateIcons/"
								+ group
								+ ".png' width='38px' height='38px' alt='"
								+ group + "' />";

						return imgTag + "<span>" + templateTitle + " ("
								+ data.models.size() + " " + l + ")</span>";
					} else if (field.contentEquals(PROVENANCE_COLUMN)) {
						String imgTag = "<img src='" + GWT.getModuleBaseURL()
								+ "../images/provenances/provenance" + group
								+ "2.png' border='1' alt='Provenance" + group
								+ "' />";

						return imgTag + "<span>&nbsp;&nbsp; ("
								+ data.models.size() + " " + l + ")</span>";
					} else
						return "";
				} catch (Exception e) {
					// e.printStackTrace();
					return "";
				}
			}
		});

		// GRID
		grid = new Grid<ResourceItem>(store, columnModel);
		grid.setView(view);
		// grid.setAutoWidth(true);
		grid.setId("tablesGrid");

		grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn("name");
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setColumnReordering(true);
		grid.setLoadMask(true);

		grid.addListener(Events.Attach,
				new Listener<GridEvent<ResourceItem>>() {
					@Override
					public void handleEvent(GridEvent<ResourceItem> be) {
						gridAttached();
					}
				});

		grid.addListener(Events.RowDoubleClick,
				new Listener<GridEvent<ResourceItem>>() {
					@Override
					public void handleEvent(GridEvent<ResourceItem> be) {
						ResourceItem resourceItem = be.getModel();
						openResource(resourceItem);
					}
				});
		contentPanel.add(grid);
		contentPanel.layout();
	}

	public void gridAttached() {
		if (dirty && loader != null) {
			loader.load();
			dirty = false;
		}
	}

	private ColumnConfig createRemoveColumnButton() {
		ColumnConfig columnButton = new ColumnConfig();
		columnButton.setId("delete");
		columnButton.setHeader("");
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(new GridCellRenderer<ResourceItem>() {
			@Override
			public Object render(final ResourceItem t, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ResourceItem> store, Grid<ResourceItem> grid) {

				if (t.getProvenance() == Provenance.SYSTEM)
					return null;

				Image img = new Image(ICON_CANCEL);
				img.setTitle("Remove this data set.");
				img.setStyleName("imgCursor");
				img.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String message = "If you remove this certified data set, the computation will be interrupted as well. Do you want to remove \""
								+ t.getName() + "\" ?";
						
						MessageBox mbox=MessageBox.confirm("Confirm removing", message,
								new Listener<MessageBoxEvent>() {
									@Override
									public void handleEvent(MessageBoxEvent be) {
										if (be.getButtonClicked().getText()
												.contentEquals("Yes"))
											removeResource(t);
									}
								});
						List<Component> buttons=mbox.getDialog().getButtonBar().getItems();
						mbox.getDialog().setFocusWidget(buttons.get(0));
					
						
					}
				});
				return img;
			}
		});
		return columnButton;
	}

	private ColumnConfig createOpenColumnButton() {
		ColumnConfig columnButton = new ColumnConfig();
		columnButton.setId("open");
		columnButton.setHeader("");
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(new GridCellRenderer<ResourceItem>() {
			@Override
			public Object render(final ResourceItem resourceItem,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ResourceItem> store,
					Grid<ResourceItem> grid) {
				Image img = new Image(ICON_OPEN);
				img.setTitle(resourceItem.isTable() ? "Open this data set."
						: "Save this file");
				img.setStyleName("imgCursor");
				img.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						openResource(resourceItem);
					}
				});
				return img;
			}
		});
		return columnButton;
	}

	private ColumnConfig createExportColumnButton() {
		ColumnConfig columnButton = new ColumnConfig();
		columnButton.setId("open");
		columnButton.setHeader("");
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(new GridCellRenderer<ResourceItem>() {
			@Override
			public Object render(final ResourceItem resourceItem,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ResourceItem> store,
					Grid<ResourceItem> grid) {
				Image img = new Image(ICON_EXPORT);
				img.setTitle("Export this data set into the Workspace.");
				img.setStyleName("imgCursor");
				img.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						try {
							ResourcesExporter.exportResource(resourceItem);
						} catch (Throwable e) {
							e.printStackTrace();

						}
					}
				});
				return img;
			}
		});
		return columnButton;
	}

	/**
	 * @param resourceItem
	 */
	private void removeResource(final ResourceItem resourceItem) {
		service.removeResource(resourceItem.getId(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Info.display("", "The data set " + resourceItem.getName()
						+ " was correctly removed.");
				// grid.getStore().remove(tableItem);
				loader.load();
				EventBusProvider.getInstance().fireEvent(
						new JobsGridGotDirtyEvent());
			}

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Impossible to remove the data set.",
						null);
			}
		});
	}

	private void openResource(ResourceItem resourceItem) {
		if (resourceItem.isTable())
			openTable(resourceItem);
		else if (resourceItem.isFile()) {
			String smpEncoded = URL.encodeQueryString(resourceItem.getUrl());
			String url = GWT.getModuleBaseURL() + "DownloadService?url="
					+ smpEncoded + "&name=" + resourceItem.getName();
			com.google.gwt.user.client.Window.open(url, "_blank", "");
		}
	}

	/**
	 * @param tableItem
	 */
	protected void openTable(ResourceItem tableItem) {
		TabularData tabularData = StatisticalManager.getTabularData();
		TabularDataGridPanel gridPanel = tabularData.getGridPanel();

		Dialog dialog = new Dialog();
		dialog.setMaximizable(true);
		dialog.setBodyBorder(false);
		dialog.setExpanded(true);
		dialog.setHeadingText("Data Set " + tableItem.getName());
		dialog.setWidth(700);
		dialog.setHeight(500);
		dialog.setHideOnButtonClick(true);
		dialog.setModal(true);
		dialog.add(gridPanel);
		dialog.show();

		tabularData.openTable(tableItem.getId());
		gridPanel.setHeaderVisible(false);
	}
}
