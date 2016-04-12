package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class TabResourcesSelectionPanel extends ContentPanel implements
		HasSelectionHandlers<TabResource> {
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");	
	private Grid<TabResource> grid;
	private ResourceBundle res;
	private Menu contextMenu;

	private TabResource removableTR;

	private WizardCard parent;

	public TabResourcesSelectionPanel(WizardCard parent, ResourceBundle res) {
		this.res = res;
		this.parent = parent;
		Log.debug("TabResourcesSelectionPanel");
		init();
		try {
			createContextMenu();	
			buildPanel();

		} catch (Throwable e) {
			Log.debug("Error building panel:" + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void init() {
		setHeaderVisible(false);
		// new Resizable(this, Dir.E, Dir.SE, Dir.S);

	}

	protected void buildPanel() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(res.refresh16());
		btnReload.setToolTip("Reload");
		toolBar.add(btnReload);

		IdentityValueProvider<TabResource> identity = new IdentityValueProvider<TabResource>();
		final CheckBoxSelectionModel<TabResource> sm = new CheckBoxSelectionModel<TabResource>(
				identity);

		
		TabResourceProperties properties = GWT
				.create(TabResourceProperties.class);

		ColumnConfig<TabResource, String> nameColumn = new ColumnConfig<TabResource, String>(
				properties.name(), 90, "Name");
		ColumnConfig<TabResource, String> typeColumn = new ColumnConfig<TabResource, String>(
				properties.tabResourceType(), 30, "Type");
		ColumnConfig<TabResource, String> tableTypeNameColumn = new ColumnConfig<TabResource, String>(
				properties.tableTypeName(), 30, "Table Type");
		ColumnConfig<TabResource, String> agencyColumn = new ColumnConfig<TabResource, String>(
				properties.agency(), 60, "Agency");
		ColumnConfig<TabResource, String> ownerColumn = new ColumnConfig<TabResource, String>(
				properties.ownerLogin(), 70, "Owner");
		ColumnConfig<TabResource, Date> dateColumn = new ColumnConfig<TabResource, Date>(
				properties.date(), 50, "Creation Date");
		
		dateColumn.setCell(new DateCell(sdf));
		
		List<ColumnConfig<TabResource, ?>> columns=new ArrayList<ColumnConfig<TabResource,?>>();
		columns.add(nameColumn);
		columns.add(typeColumn);
		columns.add(tableTypeNameColumn);
		columns.add(agencyColumn);
		columns.add(ownerColumn);
		columns.add(dateColumn);
		
		
		ColumnModel<TabResource> cm = new ColumnModel<TabResource>(columns);

		final ExtendedListStore<TabResource> store = new ExtendedListStore<TabResource>(
				properties.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<TabResource>() {

			public boolean select(Store<TabResource> store, TabResource parent,
					TabResource item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return TabResourcesSelectionPanel.this.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<TabResource>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<TabResource>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<TabResource>> callback) {
				loadData(loadConfig, callback);
			}
		};
		final ListLoader<ListLoadConfig, ListLoadResult<TabResource>> loader = new ListLoader<ListLoadConfig, ListLoadResult<TabResource>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, TabResource, ListLoadResult<TabResource>>(
				store));

		grid = new Grid<TabResource>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load();
					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.getView().setAutoExpandColumn(nameColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);

		SelectHandler sh = new SelectHandler() {
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		};

		btnReload.addSelectHandler(sh);

		if (contextMenu != null) {
			grid.setContextMenu(contextMenu);
		} else
			grid.setContextMenu(null);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(grid, new VerticalLayoutData(1, 1));
		setWidget(con);
	}

	protected void createContextMenu() {
		contextMenu = new Menu();
		MenuItem deleteTRItem = new MenuItem("Delete");
		deleteTRItem.setId("DeleteTR");
		deleteTRItem
				.setIcon(org.gcube.portlets.user.td.replacebyexternalwidget.client.resources.ReplaceByExternalResourceBundle.INSTANCE
						.delete());
		deleteTRItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (grid != null) {
					TabResource tabResource = getSelectedItem();
					deleteTR(tabResource);
				}

			}
		});
		contextMenu.add(deleteTRItem);

	}

	protected boolean select(TabResource item, String searchTerm) {
		if (item.getName() != null
				&& item.getName().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getTableTypeName() != null
				&& item.getTableTypeName().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getAgency() != null
				&& item.getAgency().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getOwnerLogin() != null
				&& item.getOwnerLogin().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		return false;
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TabResource>> callback) {
		TDGWTServiceAsync.INSTANCE
				.getTabularResources(new AsyncCallback<ArrayList<TabResource>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.debug("Error retrieving tabular resource: "
									+ caught.getLocalizedMessage());
							UtilsGXT3
									.alert("Error!",
											"Error retrieving tabular resources on server!");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<TabResource> result) {
						Log.debug("loaded " + result.size()
								+ " TabularResources");
						callback.onSuccess(new ListLoadResultBean<TabResource>(
								result));
					}
				});
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<TabResource> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

	public TabResource getSelectedItem() {
		return grid.getSelectionModel().getSelectedItem();
	}

	protected class ExtendedListStore<M> extends ListStore<M> {

		public ExtendedListStore(ModelKeyProvider<? super M> keyProvider) {
			super(keyProvider);
		}

		public void applyFilters() {
			super.applyFilters();
		}

	}

	public void gridReload() {
		grid.getLoader().load();
	}

	protected void deleteTR(TabResource tabResource) {
		removableTR = tabResource;
		final ConfirmMessageBox mb = new ConfirmMessageBox("Delete",
				"Would you like to delete this tabular resource?");

		// Next in GXT 3.1.1

		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					break;
				case YES:
					callDeleteTabularResource();
					break;
				default:
					break;

				}

			}
		});
		/*
		 * GXT 3.0.1 mb.addHideHandler(new HideHandler() { public void
		 * onHide(HideEvent event) { if (mb.getHideButton() ==
		 * mb.getButtonById(PredefinedButton.YES .name())) {
		 * callDeleteTabularResource(); } else if (mb.getHideButton() == mb
		 * .getButtonById(PredefinedButton.NO.name())) { // perform NO action }
		 * } });
		 */
		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteTabularResource() {
		Log.debug("Delete TR:" + removableTR);
		TDGWTServiceAsync.INSTANCE.removeTabularResource(removableTR.getTrId(),
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error on delete TabResource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Error on delete TabResource: "
												+ caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(Void result) {
						gridReload();
					}

				});
	}
}
