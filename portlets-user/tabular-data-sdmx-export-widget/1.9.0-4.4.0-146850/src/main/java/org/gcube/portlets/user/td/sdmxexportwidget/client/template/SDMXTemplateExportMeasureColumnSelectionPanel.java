/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateColumnData;
import org.gcube.portlets.user.td.sdmxexportwidget.client.properties.TemplateColumnDataProperties;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SDMXTemplateExportMeasureColumnSelectionPanel extends ContentPanel
		implements HasSelectionHandlers<TemplateColumnData> {

	private static final TemplateColumnDataProperties properties = GWT
			.create(TemplateColumnDataProperties.class);

	private static final ColumnConfig<TemplateColumnData, String> nameColumn = new ColumnConfig<TemplateColumnData, String>(
			properties.label(), 50, "Name");

	private Grid<TemplateColumnData> grid;

	private WizardCard parent;
	private ResourceBundle res;
	private SDMXTemplateExportSession sdmxTemplateExportSession;

	public SDMXTemplateExportMeasureColumnSelectionPanel(WizardCard parent,
			ResourceBundle res,
			SDMXTemplateExportSession sdmxTemplateExportSession) {
		this.parent = parent;
		this.res = res;
		this.sdmxTemplateExportSession = sdmxTemplateExportSession;

		setHeaderVisible(false);
		buildPanel(
				properties.id(),
				Arrays.<ColumnConfig<TemplateColumnData, ?>> asList(nameColumn),
				nameColumn);
	}

	private void buildPanel(ModelKeyProvider<TemplateColumnData> keyProvider,
			List<ColumnConfig<TemplateColumnData, ?>> columns,
			ColumnConfig<TemplateColumnData, ?> autoexpandColumn) {

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(res.refresh16());
		btnReload.setToolTip("Reload");
		toolBar.add(btnReload);

		IdentityValueProvider<TemplateColumnData> identity = new IdentityValueProvider<TemplateColumnData>();
		final CheckBoxSelectionModel<TemplateColumnData> sm = new CheckBoxSelectionModel<TemplateColumnData>(
				identity);

		ColumnModel<TemplateColumnData> cm = new ColumnModel<TemplateColumnData>(
				columns);

		final ExtendedListStore<TemplateColumnData> store = new ExtendedListStore<TemplateColumnData>(
				keyProvider);

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<TemplateColumnData>() {

			public boolean select(Store<TemplateColumnData> store,
					TemplateColumnData parent, TemplateColumnData item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return SDMXTemplateExportMeasureColumnSelectionPanel.this
						.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<TemplateColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<TemplateColumnData>>() {

			public void load(
					ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<TemplateColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		final ListLoader<ListLoadConfig, ListLoadResult<TemplateColumnData>> loader = new ListLoader<ListLoadConfig, ListLoadResult<TemplateColumnData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, TemplateColumnData, ListLoadResult<TemplateColumnData>>(
				store));

		grid = new Grid<TemplateColumnData>(store, cm) {
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
		grid.getView().setAutoExpandColumn(autoexpandColumn);
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

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(grid, new VerticalLayoutData(1, 1));
		setWidget(con);
	}

	protected boolean select(TemplateColumnData item, String searchTerm) {
		if (item.getLabel() != null
				&& item.getLabel().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;

		return false;
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TemplateColumnData>> callback) {

		String templateId=String.valueOf(sdmxTemplateExportSession
		.getTemplateData().getId());
		
		TDGWTServiceAsync.INSTANCE.getTemplateColumnsData(templateId,
				new AsyncCallback<ArrayList<TemplateColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("No Template Columns Data retrieved");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<TemplateColumnData> result) {
						Log.trace("loaded " + result.size() + " Template Columns Data");
						List<TemplateColumnData> measureColumn = new ArrayList<>();
						for (TemplateColumnData column : result) {
							if (column != null
									&& column.getTypeCode() != null
									&& column.getTypeCode().compareTo(
											ColumnTypeCode.MEASURE) == 0) {
								measureColumn.add(column);
							}
						}

						callback.onSuccess(new ListLoadResultBean<TemplateColumnData>(
								measureColumn));
					}
				});
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<TemplateColumnData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

	/*
	 * public List<TemplateColumnData> getSelectedItems() { return
	 * grid.getSelectionModel().getSelectedItems(); }
	 */

	public TemplateColumnData getSelectedItem() {
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

}
