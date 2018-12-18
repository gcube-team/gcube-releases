package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
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


/**
 * 
 * @author Giancarlo Panichi
 *   
 *
 */
public class TabResourcesSelectionPanel extends ContentPanel implements
		HasSelectionHandlers<TabResource> {
	private static final DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm");

	private Grid<TabResource> grid;
	private ResourceBundle res;
	private Menu contextMenu;

	private TabResource removableTR;

	private WizardCard parent;

	private CodelistMappingMessages msgs;
	private CommonMessages msgsCommon;

	public TabResourcesSelectionPanel(WizardCard parent, ResourceBundle res) {
		this.res = res;
		this.parent = parent;
		Log.debug("TabResourcesSelectionPanel");
		initMessages();
		init();
		
		try {
			createContextMenu();
			createPanel();

		} catch (Throwable e) {
			Log.debug("Error building panel:" + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void initMessages(){
		msgs = GWT.create(CodelistMappingMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void init() {
		setHeaderVisible(false);
		// new Resizable(this, Dir.E, Dir.SE, Dir.S);

	}
	

	protected void createPanel() {

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(msgsCommon.toolItemSearchLabel()));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(res.refresh16());
		btnReload.setToolTip(msgsCommon.toolItemReloadLabel());
		toolBar.add(btnReload);

		IdentityValueProvider<TabResource> identity = new IdentityValueProvider<TabResource>();
		final CheckBoxSelectionModel<TabResource> sm = new CheckBoxSelectionModel<TabResource>(
				identity);

		TabResourceProperties properties = GWT
				.create(TabResourceProperties.class);

		ColumnConfig<TabResource, String> nameColumn = new ColumnConfig<TabResource, String>(
				properties.name(), 90, msgs.nameColumn());
		ColumnConfig<TabResource, String> typeColumn = new ColumnConfig<TabResource, String>(
				properties.tabResourceType(), 30, msgs.typeColumn());
		ColumnConfig<TabResource, String> tableTypeNameColumn = new ColumnConfig<TabResource, String>(
				properties.tableTypeName(), 30, msgs.tableTypeColumn());
		ColumnConfig<TabResource, String> agencyColumn = new ColumnConfig<TabResource, String>(
				properties.agency(), 60, msgs.agencyColumn());
		ColumnConfig<TabResource, String> ownerColumn = new ColumnConfig<TabResource, String>(
				properties.ownerLogin(), 70, msgs.ownerColumn());
		ColumnConfig<TabResource, Date> creationDateColumn = new ColumnConfig<TabResource, Date>(
				properties.date(), 50, msgs.creationDateColumn());

		creationDateColumn.setCell(new DateCell(sdf));

		List<ColumnConfig<TabResource, ?>> columns = new ArrayList<ColumnConfig<TabResource, ?>>();
		columns.add(nameColumn);
		columns.add(typeColumn);
		columns.add(tableTypeNameColumn);
		columns.add(agencyColumn);
		columns.add(ownerColumn);
		columns.add(creationDateColumn);

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
		MenuItem deleteTRItem = new MenuItem(msgs.deleteItem());
		deleteTRItem.setId("DeleteTR");
		deleteTRItem
				.setIcon(org.gcube.portlets.user.td.codelistmappingimportwidget.client.dataresource.ResourceBundle.INSTANCE
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
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								parent.showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									parent.showErrorAndHide(msgsCommon.errorFinal(),
											caught.getLocalizedMessage(), "",
											caught);
								} else {
									Log.debug("Error retrieving tabular resource: "
											+ caught.getLocalizedMessage());
									parent.showErrorAndHide(
											msgsCommon.error(),
											msgs.errorRetrievingTabularResourceFixed(),
											caught.getLocalizedMessage(),
											caught);
								}
							}
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
		final ConfirmMessageBox mb = new ConfirmMessageBox(msgs.delete(),
				msgs.wouldYouLikeToDeleteThisTabularResource());
		

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

		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteTabularResource() {
		Log.debug("Delete TR:" + removableTR);
		TDGWTServiceAsync.INSTANCE.removeTabularResource(removableTR.getTrId(),
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						UtilsGXT3.alert(
								msgsCommon.error(),
								msgs.errorOnDeleteTabularResourceFixed()
										+ caught.getLocalizedMessage());
					}

					public void onSuccess(Void result) {
						gridReload();
					}

				});
	}

}
