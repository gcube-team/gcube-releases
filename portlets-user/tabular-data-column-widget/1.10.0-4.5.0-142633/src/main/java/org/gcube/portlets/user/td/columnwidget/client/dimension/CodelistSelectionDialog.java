package org.gcube.portlets.user.td.columnwidget.client.dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadConfig;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.CodelistPagingLoadResult;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.Direction;
import org.gcube.portlets.user.td.gwtservice.shared.tr.paging.OrderInfo;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class CodelistSelectionDialog extends Window {
	private static final int WIDTH = 550;
	private static final int HEIGHT = 520;
	private static final int CACHE_SIZE = 200;

	private ArrayList<CodelistSelectionListener> listeners;
	
	private static final TabResourcesProperties properties = GWT
			.create(TabResourcesProperties.class);
	
	private static CodelistSelectionMessages msgs = GWT.create(CodelistSelectionMessages.class);

	private static final ColumnConfig<TabResource, String> nameColumn = new ColumnConfig<TabResource, String>(
			properties.name(), 50, msgs.nameColumnLabel());
	private static final ColumnConfig<TabResource, String> agencyColumn = new ColumnConfig<TabResource, String>(
			properties.agency(), 50, msgs.agencyColumn());
	private static final ColumnConfig<TabResource, String> dateColumn = new ColumnConfig<TabResource, String>(
			properties.date(), 50, msgs.dateColumn());

	private EventBus eventBus;
	private Grid<TabResource> grid;
	private ExtendedLiveGridView liveGridView;
	private PagingLoader<PagingLoadConfig, PagingLoadResult<TabResource>> loader;
	private String filter;
	private boolean firstLoad = true;

	private TabResource selectedTR;

	private ResourceBundle res;
	private TextButton btnSelect;
	private CommonMessages msgsCommon;
	
	public CodelistSelectionDialog(EventBus eventBus) {
		Log.info("Dialog CodelistSelection");
		this.eventBus = eventBus;
		listeners = new ArrayList<CodelistSelectionListener>();
		initMessages();
		initWindow();
		create();

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}


	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		res = ResourceBundle.INSTANCE;
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		setModal(true);
		//setZIndex(Integer.MAX_VALUE);
	}

	protected void create() {

		final FramedPanel panel = new FramedPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);

		VerticalLayoutContainer v = new VerticalLayoutContainer();

		// Search
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(msgs.search()));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		btnReload.setText(msgs.btnReloadText());
		btnReload.setIcon(res.refresh());
		btnReload.setToolTip(msgs.btnReloadToolTip());
		toolBar.add(btnReload);

		IdentityValueProvider<TabResource> identity = new IdentityValueProvider<TabResource>();
		final CheckBoxSelectionModel<TabResource> sm = new CheckBoxSelectionModel<TabResource>(
				identity);

		List<ColumnConfig<TabResource, ?>> columns = Arrays
				.<ColumnConfig<TabResource, ?>> asList(nameColumn,
						agencyColumn, dateColumn);

		ColumnModel<TabResource> cm = new ColumnModel<TabResource>(columns);

		final ListStore<TabResource> store = new ListStore<TabResource>(
				properties.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				filter = searchField.getCurrentValue();
				if (filter != null && !filter.isEmpty()) {
					loader.load(0, liveGridView.getCacheSize());
				}
			}
		});

		RpcProxy<PagingLoadConfig, PagingLoadResult<TabResource>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<TabResource>>() {

			public void load(PagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<TabResource>> callback) {
				loadData(loadConfig, callback);

			}
		};
		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<TabResource>>(
				proxy);

		loader.setRemoteSort(true);

		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, TabResource, PagingLoadResult<TabResource>>(
				store));

		liveGridView = new ExtendedLiveGridView();
		liveGridView.setForceFit(true);
		liveGridView.setEmptyText(msgs.gridEmptyText());
		liveGridView.setCacheSize(CACHE_SIZE);

		grid = new Grid<TabResource>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load(0, liveGridView.getCacheSize());
					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.setView(liveGridView);
		grid.setBorders(false);
		grid.setLoadMask(true);

		// grid.setColumnReordering(true);

		addSelectionHandler();

		SelectHandler sh = new SelectHandler() {
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		};

		btnReload.addSelectHandler(sh);

		ToolBar baseToolBar = new ToolBar();
		baseToolBar.add(new LiveToolItem(grid));
		baseToolBar.addStyleName(ThemeStyles.get().style().borderTop());
		baseToolBar.getElement().getStyle().setProperty("borderBottom", "none");

		v.add(toolBar, new VerticalLayoutData(-1, -1));
		v.add(grid, new VerticalLayoutData(1, 1));
		v.add(baseToolBar, new VerticalLayoutData(1, 25));

		panel.add(v);

		btnSelect = new TextButton(msgs.btnSelectText());
		btnSelect.setToolTip(msgs.btnSelectToolTip());
		btnSelect.disable();
		btnSelect.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				startSelect();
			}
		});

		panel.addButton(btnSelect);

		add(panel);
		forceLayout();

	}

	protected void startSelect() {
		selectedTR = getSelected();
		if (selectedTR != null) {
			TDGWTServiceAsync.INSTANCE.getLastTable(selectedTR.getTrId(),
					new AsyncCallback<TableData>() {

						@Override
						public void onFailure(Throwable caught) {
							if (caught instanceof TDGWTSessionExpiredException) {
								eventBus.fireEvent(new SessionExpiredEvent(
										SessionExpiredType.EXPIREDONSERVER));
							} else {
								if (caught instanceof TDGWTIsLockedException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorLocked(),
											caught.getLocalizedMessage());
								} else {
									if (caught instanceof TDGWTIsFinalException) {
										Log.error(caught.getLocalizedMessage());
										UtilsGXT3.alert(msgsCommon.errorFinal(),
												caught.getLocalizedMessage());
									} else {
										Log.debug(msgsCommon.attention(),
												"This tabular resource does not have a valid table");
										UtilsGXT3
												.alert(msgsCommon.attention(),
														msgs.thisTabularResourceDoesHaveAValidTable());
									}
								}
							}
						}

						@Override
						public void onSuccess(TableData result) {
							Log.debug("Retrieve last table: " + result);
							startFire(result);

						}

					});
		} else {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectACodelist());
		}

	}

	protected void startFire(TableData result) {
		selectedTR.setTrId(result.getTrId());
		fireCompleted(selectedTR);
	}

	protected void loadData(PagingLoadConfig loadConfig,
			final AsyncCallback<PagingLoadResult<TabResource>> callback) {
		if (firstLoad) {
			initLoad(loadConfig, callback);
		} else {
			fastLoad(loadConfig, callback);
		}
	}

	protected void fastLoad(PagingLoadConfig loadConfig,
			final AsyncCallback<PagingLoadResult<TabResource>> callback) {
		List<? extends SortInfo> sortInfo = loadConfig.getSortInfo();
		Iterator<? extends SortInfo> iterator = sortInfo.iterator();
		ArrayList<OrderInfo> listOrderInfo = new ArrayList<OrderInfo>();

		while (iterator.hasNext()) {
			SortInfo info = iterator.next();
			OrderInfo ord = new OrderInfo();
			SortDir sd = info.getSortDir();
			if (sd.compareTo(SortDir.ASC) == 0) {
				ord.setDirection(Direction.ASC);
			} else {
				ord.setDirection(Direction.DESC);
			}
			ord.setField(info.getSortField());
			listOrderInfo.add(ord);
		}

		CodelistPagingLoadConfig codelistPagingLoadConfig = new CodelistPagingLoadConfig(
				loadConfig.getOffset(), loadConfig.getLimit(), listOrderInfo,
				filter);

		TDGWTServiceAsync.INSTANCE.getCodelistsPagingLoader(
				codelistPagingLoadConfig,
				new AsyncCallback<CodelistPagingLoadResult>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error Retrieving Codelist",
											caught.getMessage());
									caught.printStackTrace();
									UtilsGXT3
											.alert(msgs.errorRetrievingCodelistHead(),
													msgs.errorRetrievingCodelist());
								}
							}
						}

					}

					@Override
					public void onSuccess(CodelistPagingLoadResult result) {
						callback.onSuccess(new PagingLoadResultBean<TabResource>(
								result.getLtr(), result.getTotalLenght(),
								result.getOffset()));

					}
				});

	}

	protected void initLoad(final PagingLoadConfig loadConfig,
			final AsyncCallback<PagingLoadResult<TabResource>> callback) {
		TDGWTServiceAsync.INSTANCE
				.setCodelistsPagingLoader(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug(
											"Error in setCodelistsPagingLoader",
											caught.getMessage());
									caught.printStackTrace();
									UtilsGXT3
											.alert(msgs.errorRetrievingCodelistHead(),
													msgs.errorRetrievingCodelistDuringInitializationPhase());
								}
							}
						}
					}

					@Override
					public void onSuccess(Void result) {
						firstLoad = false;
						fastLoad(loadConfig, callback);

					}

				});

	}

	protected HandlerRegistration addSelectionHandler() {
		SelectionHandler<TabResource> hand = new SelectionHandler<TabResource>() {

			public void onSelection(SelectionEvent<TabResource> event) {
				btnSelect.enable();
			}

		};
		return grid.getSelectionModel().addSelectionHandler(hand);
	}

	protected TabResource getSelected() {
		return grid.getSelectionModel().getSelectedItem();
	}

	protected class ExtendedLiveGridView extends LiveGridView<TabResource> {

		// TODO bug in gxt3 3.0.0 fixed in future
		/*
		 * @Override public void refresh(boolean headerToo) {
		 * preventScrollToTopOnRefresh = true; super.refresh(headerToo); }
		 * 
		 * @Override public Point getScrollState() { return new
		 * Point(scroller.getScrollLeft(), liveScroller.getScrollTop()); }
		 * 
		 * @Override public void restoreScroll(Point state) { if (state.getX() <
		 * cm.getTotalWidth()) { scroller.setScrollLeft(state.getX()); } if
		 * (state.getY() < totalCount * getCalculatedRowHeight()) {
		 * liveScroller.setScrollTop(state.getY()); } }
		 */

	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				fireAborted();
				hide();
			}
		});

	}

	public void addListener(CodelistSelectionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CodelistSelectionListener listener) {
		listeners.remove(listener);
	}

	public void fireCompleted(TabResource tabResource) {
		for (CodelistSelectionListener listener : listeners)
			listener.selected(tabResource);
		hide();
	}

	public void fireAborted() {
		for (CodelistSelectionListener listener : listeners)
			listener.aborted();
		hide();
	}

	public void fireFailed(String reason, String details) {
		for (CodelistSelectionListener listener : listeners)
			listener.failed(reason, details);
		hide();
	}

}
