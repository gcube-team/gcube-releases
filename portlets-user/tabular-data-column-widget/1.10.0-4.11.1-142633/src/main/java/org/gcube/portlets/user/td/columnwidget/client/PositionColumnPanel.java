package org.gcube.portlets.user.td.columnwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ChangeColumnsPositionSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

/**
 * Change columns position
 * 
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class PositionColumnPanel extends FramedPanel {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";
	private EventBus eventBus;
	private PositionColumnDialog parent;
	private TRId trId;

	private TextButton applyBtn;

	private ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader;
	private Grid<ColumnData> grid;
	private GridSelectionModel<ColumnData> sm;

	private ChangeColumnsPositionSession changeColumnsPositionSession;

	private ListStore<ColumnData> store;
	
	private PositionColumnMessages msgs;
	private CommonMessages msgsCommon;
	
	public PositionColumnPanel(TRId trId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		Log.debug("ReorderColumnsPanel(): [" + trId.toString() + "]");
		initMessages();
		init();
		build();
	}
	
	protected void initMessages(){
		msgs = GWT.create(PositionColumnMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void build() {
		ColumnDataProperties props = GWT.create(ColumnDataProperties.class);

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());
		labelCol.setHeader(msgs.labelColHeader());

		//IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();
		sm = new GridSelectionModel<ColumnData>();

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(labelCol);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		store = new ListStore<ColumnData>(props.id());

		store.addStoreDataChangeHandler(new StoreDataChangeHandler<ColumnData>() {

			@Override
			public void onDataChange(StoreDataChangeEvent<ColumnData> event) {
				/*List<ColumnData> cols = event.getSource().getAll();
				Log.debug("Columns:" + cols.size());
				for (ColumnData c : cols) {
					if (c.getName().compareTo(columnName) == 0) {
						sm.select(c, false);
						sm.refresh();
						break;
					}
				}*/

			}
		});

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(
				store) {
		});

		grid = new Grid<ColumnData>(store, cm) {
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

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.setHeight(360);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);
		
		GridDragSource<ColumnData> ds=new GridDragSource<ColumnData>(grid);
		ds.addDragStartHandler(new DndDragStartHandler() {
			
			
			@Override
			public void onDragStart(DndDragStartEvent event) {
				@SuppressWarnings("unchecked")
				ArrayList<ColumnData> draggingSelection = (ArrayList<ColumnData>) event.getData();
				Log.debug("Start Drag: "+draggingSelection);
				
			}
		});
	    GridDropTarget<ColumnData> dt = new GridDropTarget<ColumnData>(grid);
	    dt.setFeedback(Feedback.BOTH);
	    dt.setAllowSelfAsSource(true);
		

		// Apply Button
		applyBtn = new TextButton(msgs.applyBtnText());
		applyBtn.setIcon(ResourceBundle.INSTANCE.columnReorder());
		applyBtn.setIconAlign(IconAlign.RIGHT);
		applyBtn.setToolTip(msgs.applyBtnToolTip());

		SelectHandler deleteHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onReorderColumns();

			}
		};
		applyBtn.addSelectHandler(deleteHandler);

		HTML tipForReorganization = new HTML("<p>"+msgs.tipForReorganization()+"</p>");
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(applyBtn, new BoxLayoutData(new Margins(2, 5, 2, 5)));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(tipForReorganization, new VerticalLayoutData(-1, -1, new Margins(2, 1, 5,
				1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected ArrayList<ColumnData> getReorderedColumns() {
		return new ArrayList<ColumnData>(grid.getStore().getAll());

	}

	public void update(TRId trId) {
		this.trId = trId;
		loader.load();
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

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
									Log.error("load columns failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingColumnsHead(),
											msgs.errorRetrievingColumns());
								}
							}
						}
						callback.onFailure(caught);

					}

					public void onSuccess(ArrayList<ColumnData> result) {
						try {
							Log.debug("loaded " + result.size() + " ColumnData");
							if(result.size()<=1){
								applyBtn.disable();
							}
							
							callback.onSuccess(new ListLoadResultBean<ColumnData>(
									result));

						} catch (UmbrellaException e) {
							Log.debug("Umbrella exception "
									+ e.getLocalizedMessage());
						} catch (com.google.web.bindery.event.shared.UmbrellaException e) {
							Log.debug("Umbrella exception "
									+ e.getLocalizedMessage());
						}

					}

				});

	}

	protected void onReorderColumns() {
		ArrayList<ColumnData> columns = getReorderedColumns();
		if (columns == null || columns.size() < 1) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.attentionNoColumnChange());
			return;
		} else {
			callReorderColumn(columns);
		}
	}

	private void callReorderColumn(ArrayList<ColumnData> columns) {
		
		changeColumnsPositionSession = new ChangeColumnsPositionSession(trId, columns);
		
		Log.debug("ChangeColumnsSession: "+changeColumnsPositionSession);
	
		TDGWTServiceAsync.INSTANCE.startChangeColumnsPosition(changeColumnsPositionSession,
				new AsyncCallback<Void>() {

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
									Log.debug("Error changing the position of the columns: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgsCommon.error(),
													msgs.errorChangingPositionOfColumns()+caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(Void result) {
						UtilsGXT3
						.info(msgsCommon.success(), msgs.positionUpdated());
						syncOpComplete();
						
						//openMonitorDialog(taskId);
					}

				});

	}
	
	protected void syncOpComplete(){
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.CHANGEPOSITIONCOLUMNS, trId, why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}
	
	
	public void close() {
		if (parent != null) {
			parent.close();
		}
	}

	

}
