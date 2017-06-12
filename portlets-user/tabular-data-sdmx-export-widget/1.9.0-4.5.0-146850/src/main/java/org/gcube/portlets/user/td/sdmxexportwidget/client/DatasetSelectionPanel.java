/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Dataset;
import org.gcube.portlets.user.td.sdmxexportwidget.client.properties.DatasetProperties;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
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
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DatasetSelectionPanel extends ContentPanel implements HasSelectionHandlers<Dataset> {

	private static final DatasetProperties properties = GWT.create(DatasetProperties.class);
	
	protected static final ColumnConfig<Dataset, String> nameColumn = new ColumnConfig<Dataset, String>(properties.name(), 50, "Name");
	protected static final ColumnConfig<Dataset, String> agencyIdColumn = new ColumnConfig<Dataset, String>(properties.agencyId(), 50, "Agency Id");
	protected static final ColumnConfig<Dataset, String> versionColumn = new ColumnConfig<Dataset, String>(properties.version(), 50, "Version");
	protected static final ColumnConfig<Dataset, String> descriptionColumn = new ColumnConfig<Dataset, String>(properties.description(), 50, "Description");

	protected Grid<Dataset> grid;
	
	protected ResourceBundle res;

	private WizardCard parent;
	
	public DatasetSelectionPanel(WizardCard parent, ResourceBundle res)
	{
		this.parent=parent;
		this.res=res;
		setHeaderVisible(false);
		//new Resizable(this, Dir.E, Dir.SE, Dir.S);
		buildPanel(properties.key(), Arrays.<ColumnConfig<Dataset, ?>>asList(nameColumn, agencyIdColumn, versionColumn, descriptionColumn), nameColumn);
	}
	
	
	protected void buildPanel(ModelKeyProvider<Dataset> keyProvider, List<ColumnConfig<Dataset, ?>> columns, ColumnConfig<Dataset, ?> autoexpandColumn)
	{

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		IdentityValueProvider<Dataset> identity = new IdentityValueProvider<Dataset>();
		final CheckBoxSelectionModel<Dataset> sm = new CheckBoxSelectionModel<Dataset>(identity);

		ColumnModel<Dataset> cm = new ColumnModel<Dataset>(columns);

		final ExtendedListStore<Dataset> store = new ExtendedListStore<Dataset>(keyProvider);

		searchField.addKeyUpHandler(new KeyUpHandler() {

			
			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: "+searchField.getCurrentValue());
				store.applyFilters();				
			}
		});
		
		store.addFilter(new StoreFilter<Dataset>() {

			public boolean select(Store<Dataset> store, Dataset parent, Dataset item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null) return true;
				return DatasetSelectionPanel.this.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<Dataset>> proxy = new RpcProxy<ListLoadConfig,  ListLoadResult<Dataset>>() {

			
			public void load(ListLoadConfig loadConfig, final AsyncCallback<ListLoadResult<Dataset>> callback) {
				loadData(loadConfig, callback);
			}
		};
		final ListLoader<ListLoadConfig, ListLoadResult<Dataset>> loader = new ListLoader<ListLoadConfig, ListLoadResult<Dataset>>(proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, Dataset, ListLoadResult<Dataset>>(store));

		grid = new Grid<Dataset>(store, cm){
			
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
		grid.setSelectionModel(sm);
		grid.getView().setAutoExpandColumn(autoexpandColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);

		grid.setColumnReordering(true);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(grid, new VerticalLayoutData(1, 1));
		setWidget(con);
	}
	
	
	
	protected boolean select(Dataset item, String searchTerm) {
		if (item.getName()!=null && item.getName().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getAgencyId()!=null &&item.getAgencyId().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getId()!=null &&item.getId().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		return false;
	}

	
	protected void loadData(ListLoadConfig loadConfig, final AsyncCallback<ListLoadResult<Dataset>> callback) {
		TDGWTServiceAsync.INSTANCE.getDatasets(new  AsyncCallback<ArrayList<Dataset>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					parent.getEventBus()
							.fireEvent(
									new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("No datasets retrieved");
				}
				
				callback.onFailure(caught);					
			}

			public void onSuccess(ArrayList<Dataset> result) {
				Log.trace("loaded "+result.size()+" datasets");
				callback.onSuccess(new ListLoadResultBean<Dataset>(result));
			}
		});
	}

	
	public HandlerRegistration addSelectionHandler(SelectionHandler<Dataset> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}


	public List<Dataset> getSelectedItems() {
		return grid.getSelectionModel().getSelectedItems();
	}

	protected class ExtendedListStore<M> extends ListStore<M> {

		public ExtendedListStore(ModelKeyProvider<? super M> keyProvider) {
			super(keyProvider);
		}

		public void applyFilters()
		{
			super.applyFilters();
		}

	}
	

}
