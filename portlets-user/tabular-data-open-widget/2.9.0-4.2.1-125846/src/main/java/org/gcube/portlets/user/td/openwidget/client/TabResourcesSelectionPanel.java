/**
 * 
 */
package org.gcube.portlets.user.td.openwidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.openwidget.client.resources.ResourceBundleTDOpen;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
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
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
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
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabResourcesSelectionPanel extends ContentPanel implements
		HasSelectionHandlers<TabResource> {

	private static final DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm");

	interface NameTooltipTemplates extends XTemplates {
		@XTemplate("<span qtip=\"{desc}\" qtitle=\"{value}\">{value}</span>")
		SafeHtml format(String value, String desc);

	}
	
	private CommonMessages msgsCommon;
	private TDOpenMessages msgs;
	
	private Grid<TabResource> grid;
	private ResourceBundle res;
	private Menu contextMenu;

	private TabResource removableTR;

	private TabResourcesSelectionCard parent;
	
	

	public TabResourcesSelectionPanel(TabResourcesSelectionCard parent,
			ResourceBundle res) {
		this.res = res;
		this.parent = parent;
		Log.debug("TabResourcesSelectionPanel");
		
		initMessages();
		initWindow();
		
		try {
			createContextMenu();
		} catch (Throwable e) {
			Log.debug("Error In CreateContextMenu:" + e.getMessage());
			e.printStackTrace();
		}
		try {
			buildPanel();

		} catch (Throwable e) {
			Log.debug("Error building panel:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected void initMessages(){
		msgs = GWT.create(TDOpenMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void initWindow() {
		setHeaderVisible(false);
	}

	protected void buildPanel() {

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

		ColumnConfig<TabResource, String> nameColumn = new ColumnConfig<TabResource, String>(
				properties.name(), 90, msgs.tabResourcesSelectionPanelColumnNameLabel());
		ColumnConfig<TabResource, String> typeColumn = new ColumnConfig<TabResource, String>(
				properties.tabResourceType(), 30, msgs.tabResourcesSelectionPanelColumnTypeLabel());
		ColumnConfig<TabResource, String> tableTypeNameColumn = new ColumnConfig<TabResource, String>(
				properties.tableTypeName(), 30, msgs.tabResourcesSelectionPanelColumnTableTypeLabel());
		ColumnConfig<TabResource, Boolean> lockedColumn = new ColumnConfig<TabResource, Boolean>(
				properties.locked(), 20, msgs.tabResourcesSelectionPanelColumnLockLabel());
		ColumnConfig<TabResource, String> agencyColumn = new ColumnConfig<TabResource, String>(
				properties.agency(), 60, msgs.tabResourcesSelectionPanelColumnAgencyLabel());
		ColumnConfig<TabResource, String> ownerColumn = new ColumnConfig<TabResource, String>(
				properties.ownerLogin(), 70, msgs.tabResourcesSelectionPanelColumnOwnerLabel());
		ColumnConfig<TabResource, Date> dateColumn = new ColumnConfig<TabResource, Date>(
				properties.date(), 50, msgs.tabResourcesSelectionPanelColumnCreationDateLabel());

		dateColumn.setCell(new DateCell(sdf));

		lockedColumn.setCell(new AbstractCell<Boolean>() {

			@Override
			public void render(Context context, Boolean value,
					SafeHtmlBuilder sb) {

				if (value) {
					sb.appendHtmlConstant("<img style='margin:auto;padding:auto;display:block;' src='"
							+ ResourceBundleTDOpen.INSTANCE.lock().getSafeUri()
									.asString() + "' alt='true'>");

				} else {
					sb.appendHtmlConstant("<img style='margin:auto;padding:auto;display:block;' src='"
							+ ResourceBundleTDOpen.INSTANCE.lockOpen()
									.getSafeUri().asString() + "' alt='true'>");
				}

			}
		});

		List<ColumnConfig<TabResource, ?>> columns = new ArrayList<ColumnConfig<TabResource, ?>>();
		columns.add(nameColumn);
		columns.add(typeColumn);
		columns.add(tableTypeNameColumn);
		columns.add(lockedColumn);
		columns.add(ownerColumn);
		columns.add(agencyColumn);
		columns.add(dateColumn);

		ColumnModel<TabResource> cm = new ColumnModel<TabResource>(columns);

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

		grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {

			@Override
			public void onRowDoubleClick(RowDoubleClickEvent event) {
				int rowIndex = event.getRowIndex();
				requestOpen(rowIndex);
			}

		});

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

	/**
	 * 
	 * @param rowIndex
	 */
	protected void requestOpen(int rowIndex) {
		TabResource tabResource = grid.getStore().get(rowIndex);
		if (tabResource != null) {
			parent.getTdOpenSession().setSelectedTabResource(tabResource);
			parent.retrieveLastTable();
		}

	}

	protected void createContextMenu() {
		contextMenu = new Menu();
		MenuItem deleteTRItem = new MenuItem(msgs.tabResourcesSelectionPanelContextMenuDelete());
		deleteTRItem.setId("DeleteTR");
		deleteTRItem.setIcon(ResourceBundleTDOpen.INSTANCE.delete());
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

		MenuItem infoItem = new MenuItem();
		infoItem.setText(msgs.tabResourcesSelectionPanelContextMenuInfo());
		infoItem.setIcon(ResourceBundleTDOpen.INSTANCE.information());
		infoItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (grid != null) {
					TabResource tabResource = getSelectedItem();
					requestInfo(tabResource);
				}

			}
		});

		contextMenu.add(infoItem);

	}

	protected void openInfoDialog(TabResource tabResource) {
		final TabResourceInfoDialog tabResourceInfoDialog = new TabResourceInfoDialog(
				tabResource, parent.getEventBus());
		tabResourceInfoDialog.show();
	}

	protected void requestInfo(TabResource tabResource) {
		if (tabResource.isLocked()) {
			UtilsGXT3.alert(msgsCommon.attention(),
					msgs.attentionTabularResourceIsLockedNoInfoAvailable());
			return;
		}

		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(
				tabResource.getTrId(), new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						openInfoDialog(result);
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error("Tabular Resource is Locked: "
										+ caught.getLocalizedMessage());
								parent.showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);

							} else {
								Log.error("Error: "
										+ caught.getLocalizedMessage());
								parent.showErrorAndHide(msgsCommon.error(),
										caught.getLocalizedMessage(), "",
										caught);

							}

						}
					}

				});
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
							Log.error("Error retrieving tabular resource: "
									+ caught.getLocalizedMessage());
							parent.showErrorAndHide(msgsCommon.error(),
									caught.getLocalizedMessage(), "", caught);
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<TabResource> result) {
						Log.debug("loaded " + result.size()
								+ " TabularResources");
						ArrayList<TabResource> avaibles = new ArrayList<TabResource>();
						for (TabResource tResource : result) {
							// if (!tResource.isLocked()) {
							avaibles.add(tResource);
							// }
						}

						/*
						 * for(TabResource tr:result){ Log.debug("TR:"+tr); }
						 */
						callback.onSuccess(new ListLoadResultBean<TabResource>(
								avaibles));
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
		grid.getSelectionModel().deselectAll();
		grid.getLoader().load();
	}

	protected void deleteTR(TabResource tabResource) {
		removableTR = tabResource;
		final ConfirmMessageBox mb = new ConfirmMessageBox(msgs.deleteHead(),
				msgs.questionWouldYouLikeToDeleteThisTabularResource());
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

		// TODO
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
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.error("Error on delete TabResource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(Void result) {
						Log.debug("Remove tabular resource success");
						Log.debug("Check current tr for close: " + parent.trId);
						if (parent.trId != null
								&& parent.trId.getId() != null
								&& parent.trId.getId().compareTo(
										removableTR.getTrId().getId()) == 0) {
							Log.debug("Fire Close Event on current TR");
							parent.getEventBus().fireEvent(
									new RibbonEvent(RibbonType.CLOSE));
						} else {
							Log.debug("No tr opened");
						}
						gridReload();
					}

				});
	}

}
