/**
 * 
 */
package org.gcube.portlets.user.td.sdmxexportwidget.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
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
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CodelistSelectionPanel extends ContentPanel implements HasSelectionHandlers<Codelist> {

	private static final CodelistProperties properties = GWT.create(CodelistProperties.class);
	
	protected static final ColumnConfig<Codelist, String> nameColumn = new ColumnConfig<Codelist, String>(properties.name(), 50, "Name");
	protected static final ColumnConfig<Codelist, String> agencyIdColumn = new ColumnConfig<Codelist, String>(properties.agencyId(), 50, "Agency Id");
	protected static final ColumnConfig<Codelist, String> versionColumn = new ColumnConfig<Codelist, String>(properties.version(), 50, "Version");
	protected static final ColumnConfig<Codelist, String> descriptionColumn = new ColumnConfig<Codelist, String>(properties.description(), 50, "Description");

	protected Grid<Codelist> grid;
	protected ResourceBundle res;

	private WizardCard parent;
	
	
	public CodelistSelectionPanel(WizardCard parent,ResourceBundle res)
	{
		this.parent=parent;
		this.res=res;
		setHeaderVisible(false);
		//new Resizable(this, Dir.E, Dir.SE, Dir.S);
		buildPanel(properties.key(), Arrays.<ColumnConfig<Codelist, ?>>asList(nameColumn, agencyIdColumn, versionColumn, descriptionColumn), nameColumn);
	}
	
	
	protected void buildPanel(ModelKeyProvider<Codelist> keyProvider, List<ColumnConfig<Codelist, ?>> columns, ColumnConfig<Codelist, ?> autoexpandColumn)
	{

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBar.add(searchField);
		
		TextButton btnReload = new TextButton();
		//btnReload.setText("Reload");
		btnReload.setIcon(res.refresh16());
		btnReload.setToolTip("Reload");
		toolBar.add(btnReload);
		
		
		IdentityValueProvider<Codelist> identity = new IdentityValueProvider<Codelist>();
		final CheckBoxSelectionModel<Codelist> sm = new CheckBoxSelectionModel<Codelist>(identity);
		
		ColumnModel<Codelist> cm = new ColumnModel<Codelist>(columns);

		final ExtendedListStore<Codelist> store = new ExtendedListStore<Codelist>(keyProvider);

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: "+searchField.getCurrentValue());
				store.applyFilters();				
			}
		});
		
		store.addFilter(new StoreFilter<Codelist>() {

			public boolean select(Store<Codelist> store, Codelist parent, Codelist item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null) return true;
				return CodelistSelectionPanel.this.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<Codelist>> proxy = new RpcProxy<ListLoadConfig,  ListLoadResult<Codelist>>() {

			
			public void load(ListLoadConfig loadConfig, final AsyncCallback<ListLoadResult<Codelist>> callback) {
				loadData(loadConfig, callback);
			}	
		};
		final ListLoader<ListLoadConfig, ListLoadResult<Codelist>> loader = new ListLoader<ListLoadConfig, ListLoadResult<Codelist>>(proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, Codelist, ListLoadResult<Codelist>>(store));

		grid = new Grid<Codelist>(store, cm){

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
	
	
	
	protected boolean select(Codelist item, String searchTerm) {
		if (item.getName()!=null && item.getName().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getAgencyId()!=null &&item.getAgencyId().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getVersion()!=null && item.getVersion().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getDescription()!=null && item.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		if (item.getId()!=null &&item.getId().toLowerCase().contains(searchTerm.toLowerCase())) return true;
		return false;
	}

	
	protected void loadData(ListLoadConfig loadConfig, final AsyncCallback<ListLoadResult<Codelist>> callback) {
		TDGWTServiceAsync.INSTANCE.getCodelists(new  AsyncCallback<ArrayList<Codelist>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					parent.getEventBus()
							.fireEvent(
									new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("No codelists retrieved");
				}
				callback.onFailure(caught);
			}

			public void onSuccess(ArrayList<Codelist> result) {
				Log.trace("loaded "+result.size()+" codelists");
				callback.onSuccess(new ListLoadResultBean<Codelist>(result));
			}
		});
	}

	
	public HandlerRegistration addSelectionHandler(SelectionHandler<Codelist> handler) {
		return grid.getSelectionModel().addSelectionHandler(handler);
	}


	public Codelist getSelectedItem() {
		return grid.getSelectionModel().getSelectedItem();
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
