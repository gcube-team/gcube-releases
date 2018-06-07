package org.gcube.portlets.user.td.tablewidget.client.validation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata.TabValidationsMetadata;
import org.gcube.portlets.user.td.tablewidget.client.properties.ValidationsProperties;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ValidationsTablePanel extends FramedPanel {
	protected String WIDTH = "298px";
	protected String HEIGHT = "520px";

	protected TRId trId;
	protected TableData table;
	protected String headingTitle;
	protected VerticalLayoutContainer vl;
	protected EventBus eventBus;

	protected ListStore<Validations> storeValidations;
	protected ListLoader<ListLoadConfig, ListLoadResult<Validations>> loader;
	protected Grid<Validations> grid;
	private boolean drawed;

	public ValidationsTablePanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		retrieveCurrentTR();

	}

	public ValidationsTablePanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		draw();
	}

	protected void draw() {
		drawed = true;
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		ValidationsProperties props = GWT.create(ValidationsProperties.class);

		ColumnConfig<Validations, String> descriptionCol = new ColumnConfig<Validations, String>(
				props.description(), 168, "Description");

		descriptionCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='" + SafeHtmlUtils.htmlEscape(value) + "'>" + SafeHtmlUtils.htmlEscape(value)
						+ "</span>");

			}

		});

		ColumnConfig<Validations, Boolean> validCol = new ColumnConfig<Validations, Boolean>(
				props.valid(), 28, "Valid");
		validCol.setCell(new AbstractCell<Boolean>() {

			@Override
			public void render(Context context, Boolean value,
					SafeHtmlBuilder sb) {

				if (value) {
					sb.appendHtmlConstant("<img src='"
							+ ResourceBundle.INSTANCE.ok().getSafeUri()
									.asString() + "' alt='true'>");

				} else {
					sb.appendHtmlConstant("<img src='"
							+ ResourceBundle.INSTANCE.exit().getSafeUri()
									.asString() + "' alt='true'>");

				}

			}
		});

		List<ColumnConfig<Validations, ?>> l = new ArrayList<ColumnConfig<Validations, ?>>();
		l.add(descriptionCol);
		l.add(validCol);

		ColumnModel<Validations> cm = new ColumnModel<Validations>(l);

		storeValidations = new ListStore<Validations>(props.id());

		RpcProxy<ListLoadConfig, ListLoadResult<Validations>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<Validations>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<Validations>> callback) {
				loadData(loadConfig, callback);
			}

		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<Validations>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, Validations, ListLoadResult<Validations>>(
				storeValidations) {
		});

		grid = new Grid<Validations>(storeValidations, cm) {
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

		grid.setLoader(loader);
		grid.setSize("200px", "300px");
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(descriptionCol);
		grid.getView().setEmptyText("No validation");

		add(grid, new MarginData(0));

		onResize();

	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<Validations>> callback) {

		TDGWTServiceAsync.INSTANCE.getTableValidationsMetadata(trId,
				new AsyncCallback<TabValidationsMetadata>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Load validations metadata failure: "
										+ caught.getLocalizedMessage());
								UtilsGXT3
										.alert("Error retrieving validations metadata",
												"Error retrieving validations metadata");
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(TabValidationsMetadata result) {
						Log.debug("loaded " + result.getId());
						if (result.getValidations() != null) {
							Log.debug("Validations Retrieved: "
									+ result.getValidations().size());
							callback.onSuccess(new ListLoadResultBean<Validations>(
									result.getValidations()));
						} else {
							Log.debug("No validations");
							ArrayList<Validations> empty = new ArrayList<Validations>();
							callback.onSuccess(new ListLoadResultBean<Validations>(
									empty));
						}

					}

				});

	}

	public void update() {
		retrieveCurrentTR();
		loader.load();
	}

	public void update(TRId trId) {
		this.trId = trId;
		loader.load();
	}

	protected void retrieveCurrentTR() {
		TDGWTServiceAsync.INSTANCE.getCurrentTRId(new AsyncCallback<TRId>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked",
								caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving current TRId: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert("Error",
								"Error retrieving current tabular resource id");
					}
				}
			}

			public void onSuccess(TRId result) {
				Log.debug("retrieved " + result);
				trId = result;
				if (!drawed) {
					draw();
				}

			}

		});
	}

}
