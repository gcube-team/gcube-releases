/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.sdmxexportwidget.client.properties.TemplateDataProperties;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
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
public class SDMXTmplateExportTemplateSelectionPanel extends ContentPanel
		implements HasSelectionHandlers<TemplateData> {

	private static final DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm");

	private Grid<TemplateData> grid;
	private ResourceBundle res;

	private WizardCard parent;

	public SDMXTmplateExportTemplateSelectionPanel(WizardCard parent,
			ResourceBundle res) {
		this.parent = parent;
		this.res = res;
		setHeaderVisible(false);
		create();
	}

	protected void create() {

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(res.refresh16());
		btnReload.setToolTip("Reload");
		toolBar.add(btnReload);

		IdentityValueProvider<TemplateData> identity = new IdentityValueProvider<TemplateData>();
		final CheckBoxSelectionModel<TemplateData> sm = new CheckBoxSelectionModel<TemplateData>(
				identity);

		TemplateDataProperties properties = GWT
				.create(TemplateDataProperties.class);

		ColumnConfig<TemplateData, String> nameColumn = new ColumnConfig<TemplateData, String>(
				properties.name(), 50, "Name");
		ColumnConfig<TemplateData, String> categoryColumn = new ColumnConfig<TemplateData, String>(
				properties.category(), 50, "Category");
		ColumnConfig<TemplateData, String> ownerLoginColumn = new ColumnConfig<TemplateData, String>(
				properties.ownerLogin(), 50, "Owner");
		ColumnConfig<TemplateData, String> agencyColumn = new ColumnConfig<TemplateData, String>(
				properties.agency(), 50, "Agency");
		ColumnConfig<TemplateData, String> descriptionColumn = new ColumnConfig<TemplateData, String>(
				properties.description(), 50, "Description");
		ColumnConfig<TemplateData, Date> creationDateColumn = new ColumnConfig<TemplateData, Date>(
				properties.creationDate(), 50, "Creation Date");
		creationDateColumn.setCell(new DateCell(sdf));

		List<ColumnConfig<TemplateData, ?>> l = new ArrayList<ColumnConfig<TemplateData, ?>>();
		l.add(nameColumn);
		l.add(categoryColumn);
		l.add(ownerLoginColumn);
		l.add(agencyColumn);
		l.add(descriptionColumn);
		l.add(creationDateColumn);

		ColumnModel<TemplateData> cm = new ColumnModel<TemplateData>(l);

		final ExtendedListStore<TemplateData> store = new ExtendedListStore<TemplateData>(
				properties.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<TemplateData>() {

			public boolean select(Store<TemplateData> store,
					TemplateData parent, TemplateData item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return SDMXTmplateExportTemplateSelectionPanel.this.select(
						item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<TemplateData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<TemplateData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<TemplateData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		final ListLoader<ListLoadConfig, ListLoadResult<TemplateData>> loader = new ListLoader<ListLoadConfig, ListLoadResult<TemplateData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, TemplateData, ListLoadResult<TemplateData>>(
				store));

		grid = new Grid<TemplateData>(store, cm) {

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

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(grid, new VerticalLayoutData(1, 1));
		setWidget(con);
	}

	protected boolean select(TemplateData item, String searchTerm) {
		if (item.getName() != null
				&& item.getName().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getCategory() != null
				&& item.getCategory().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getOwnerLogin() != null
				&& item.getOwnerLogin().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getDescription() != null
				&& item.getDescription().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		return false;
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TemplateData>> callback) {
		TDGWTServiceAsync.INSTANCE
				.getTemplatesForDSDExport(new AsyncCallback<ArrayList<TemplateData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("No templates retrieved");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<TemplateData> result) {
						Log.trace("loaded " + result.size() + " templates");
						callback.onSuccess(new ListLoadResultBean<TemplateData>(
								result));
					}
				});
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<TemplateData> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}

	public TemplateData getSelectedItem() {
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
