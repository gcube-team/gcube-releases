package org.gcube.portlets.user.td.codelistmappingimportwidget.client.grid;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.codelistmappingimportwidget.client.CodelistMappingMessages;
import org.gcube.portlets.user.td.codelistmappingimportwidget.client.ColumnSelectionCard;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnDataGridPanel extends ContentPanel implements
		HasSelectionHandlers<ColumnData> {
	private static final int GRIDHEIGHT = 360;
	private static final ColumnDataProperties props = GWT
			.create(ColumnDataProperties.class);

	private CheckBoxSelectionModel<ColumnData> sm;
	private Grid<ColumnData> grid;

	private CodelistMappingMessages msgs;
	private CommonMessages msgsCommon;
		
	private TRId trId;
	private ColumnSelectionCard parent;


	public ColumnDataGridPanel(ColumnSelectionCard parent, TRId trId) {
		Log.debug("ColumnDataGridPanel");
		if (trId == null) {
			Log.error("ColumnDataGridPanel: TRId is null");
		}
		Log.debug("ColumnDataGridPanel: " + trId.toString());
		this.parent = parent;
		this.trId = trId;
		initMessages();
		initPanel();
		createPanel();

	}
	
	protected void initMessages(){
		msgs = GWT.create(CodelistMappingMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void initPanel(){
		setHeaderVisible(false);
		// setHeadingText("Columns");
	}

	protected void createPanel() {
		ColumnConfig<ColumnData, String> labelColumn = new ColumnConfig<ColumnData, String>(
				props.label(), 120, msgs.labelColumn());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();

		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();

		l.add(labelColumn);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		ListStore<ColumnData> store = new ListStore<ColumnData>(props.id());

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		final ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
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

		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.setHeight(GRIDHEIGHT);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setScrollMode(ScrollMode.AUTO);
		con.add(grid, new VerticalLayoutData(-1, -1, new Margins(0)));
		setWidget(con);
	}
	
	

	public Grid<ColumnData> getGrid() {
		return grid;
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

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
								Log.error("No load columns: "
										+ caught.getLocalizedMessage());
								parent.showErrorAndHide(msgsCommon.error(),
										msgs.errorNoLoadColumnsFixed(),
										caught.getLocalizedMessage(), caught);

							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " columns");
						callback.onSuccess(new ListLoadResultBean<ColumnData>(
								result));
						sm.selectAll();
						forceLayout();
					}
				});
	}

	public ColumnData getSelectedItem() {
		return grid.getSelectionModel().getSelectedItem();

	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<ColumnData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

}
