package org.gcube.portlets.user.td.csvexportwidget.client.grid;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.csvexportwidget.client.CSVExportWizardTDMessages;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

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
	//private static final String GRID_WIDTH  ="524px";
	private static final String GRID_HEIGHT = "310px";
	private static final ColumnDataProperties props = GWT
			.create(ColumnDataProperties.class);
	private final CheckBoxSelectionModel<ColumnData> sm;
	private final Grid<ColumnData> grid;
	
	private CommonMessages msgsCommon;
	private CSVExportWizardTDMessages msgs;
	
	private WizardCard parent;
	

	public ColumnDataGridPanel(WizardCard parent) {
		this.parent=parent;
		Log.debug("ColumnDataGridPanel");
		initMessages();
		setHeadingText(msgs.columnDataGridPanelHead());

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();

		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
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

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.setHeight(GRID_HEIGHT);
		//grid.setWidth(GRID_WIDTH);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setScrollMode(ScrollMode.AUTO);
		con.add(grid, new VerticalLayoutData(-1, -1, new Margins(0)));
		setWidget(con);

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
		msgs = GWT.create(CSVExportWizardTDMessages.class);
	}
	
	public Grid<ColumnData> getGrid() {
		return grid;
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE
				.getColumns(new AsyncCallback<ArrayList<ColumnData>>() {

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
										caught.getLocalizedMessage(), "", caught);
							} else {

								Log.error("No load columns: "
										+ caught.getLocalizedMessage());
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

	public ArrayList<ColumnData> getSelectedItems() {
		return new ArrayList<ColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<ColumnData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

}
