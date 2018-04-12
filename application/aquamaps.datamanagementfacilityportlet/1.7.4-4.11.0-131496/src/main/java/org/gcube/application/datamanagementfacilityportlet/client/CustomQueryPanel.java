package org.gcube.application.datamanagementfacilityportlet.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CustomQueryPanel extends ContentPanel {

	public final Grid<ModelData> grid;
	public PagingLoader<PagingLoadResult<ModelData>> loader;

	private TextField<String> queryField=null;
	private Button setQueryButton=null;
	private Button exportButton=null;
	private String url=DataManagementFacilityConstants.servletUrl.get(Tags.directQueryServlet);
	private HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));
	
	
	public CustomQueryPanel() {

		

		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT); 


		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);  

		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(httpProxy,reader);  

		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {  
			public void handleEvent(LoadEvent be) {
				Log.debug("beforeLoad event..");
				BasePagingLoadConfig m = be.<BasePagingLoadConfig> getConfig();  
				m.set(Tags.START, m.get(Tags.OFFSET));
				m.set("ext", "js");  
				m.set("lightWeight", true);
				m.set(Tags.sort, (m.get("sortField") == null) ? "" : m.get("sortField"));
				m.set(Tags.dir, (m.get("sortDir") == null || (m.get("sortDir") != null && m.<SortDir> get("sortDir").equals(  SortDir.NONE))) ? "" : m.get("sortDir"));  
			}
		});  
		loader.setSortDir(SortDir.DESC); 
		
		loader.setRemoteSort(true);  

		ListStore<ModelData> store = new ListStore<ModelData>(loader);  

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		ColumnModel cm = new ColumnModel(columns);

		//********************* GRID

		grid=new Grid<ModelData>(store,cm);
		grid.addListener(Events.Attach, new Listener<GridEvent<ModelData>>() {  
			public void handleEvent(GridEvent<ModelData> be) {  
//				loader.load(0, 500);  
			}  
		});  
		//		grid.setTrackMouseOver(false);  
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);

		//VIEW	
		ExtendedLiveGridView liveView = new ExtendedLiveGridView();  
		liveView.setEmptyText("No rows available on the server.");  
		grid.setView(liveView);




		grid.setView(liveView);
		grid.mask("Type query to inspect data");
		
		//********************* Toolbar
		ToolBar toolBar = new ToolBar();  
		
		
		queryField=new TextField<String>();
		queryField.setFieldLabel("Query");
		queryField.setAllowBlank(false);
		queryField.setEmptyText("Type query");
		queryField.setWidth(1000);
		toolBar.add(queryField);
		
		
		setQueryButton=new Button("Update",Resources.ICONS.refresh(),new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				grid.mask("Configuring grid..");
				exportButton.disable();
				DataManagementFacility.localService.setLiveQuery(queryField.getValue(), new AsyncCallback<List<String>>() {
					
					public void onSuccess(List<String> arg0) {
						//TODO reconfigure grid
						reconfigureGrid(arg0);
						grid.unmask();
						loader.load();
						exportButton.enable();
					}
					
					public void onFailure(Throwable arg0) {
						grid.unmask();
						Log.error("Unexpected error while submitting request", arg0);
						Info.display("Update query","Unable to parse fields.");
					}
				});
			}
		});
		
		toolBar.add(setQueryButton);
		
		exportButton=new Button("Export",Resources.ICONS.report(),new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {				
				String toExportName="Custom Query";
				
				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(toExportName, true);
				
				  WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
			        	 
			    		@Override
			    		public void onSaving(Item parent, String fileName) {
			    			DataManagementFacilityConstants.sendSaveRequest(
									new SaveRequest(SaveOperationType.CUSTOM_QUERY, "", parent.getId(), fileName));
			    			navigator.hide();
			    		}
			     
			    		@Override
			    		public void onAborted() {
			    			GWT.log("onAborted");
			    		}
			     
			    		@Override
			    		public void onFailed(Throwable throwable) {
			    			GWT.log("onFailed");
			    		}
			     
			           
			    	};
				
				navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		    	
		    	navigator.setZIndex(XDOM.getTopZIndex());
		        navigator.show();
				
//				WorkspaceLightTreeSavePopup popup=new WorkspaceLightTreeSavePopup("Save custom query result", true, toExportName);
//				popup.addStyleName("z_index_1200");
//				//only the basket item can be selected
//				popup.setSelectableTypes(ItemType.FOLDER, ItemType.ROOT);
//				popup.center();
//				
//				popup.addPopupHandler(new PopupHandler() {	
//				
//				public void onPopup(PopupEvent event) {
//					if (!event.isCanceled()){						
//						org.gcube.portlets.widgets.lighttree.client.Item item = event.getSelectedItem();
//						final String name = event.getName();
//						DataManagementFacilityConstants.sendSaveRequest(
//								new SaveRequest(SaveOperationType.CUSTOM_QUERY, "", item.getId(), name));
//
//				}}});
//				
//				
//				
//				popup.addDataLoadHandler(new DataLoadHandler(){
//					public void onDataLoad(DataLoadEvent event) {
//						if (event.isFailed()){
//							System.err.println("LoadingFailure: "+event.getCaught());
//						}
//					}});
//				
//				popup.setText(toExportName);
//				popup.show();
			}		
			
		});
		exportButton.disable();
		toolBar.add(new SeparatorToolItem());
		toolBar.add(exportButton);
		
		//****************** BOTTOM
		

		ToolBar bottomToolbar=new ToolBar();
		 LiveToolItem item = new LiveToolItem();  
		 item.bindGrid(grid);  
		 bottomToolbar.add(item);  
		
		
		//************** Panel settings
		
		
		
		
		
		
		this.setFrame(true);  
		this.setCollapsible(false);
		this.setAnimCollapse(false);  
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());  
		this.add(grid);  
		this.setSize(600, 350);  
		 this.setTopComponent(toolBar);
			this.setBottomComponent(bottomToolbar);
	}
	
	
	private void reconfigureGrid(List<String> fields){
		
		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT); 
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		for(String f:fields){
			type.addField(f);
			columns.add(new ColumnConfig(f,f,100));
		}

		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);  

		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(httpProxy,reader);  

		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {  
			public void handleEvent(LoadEvent be) {
				Log.debug("beforeLoad event..");
				BasePagingLoadConfig m = be.<BasePagingLoadConfig> getConfig();  
				m.set(Tags.START, m.get(Tags.OFFSET));
				m.set("ext", "js");  
				m.set("lightWeight", true);
				m.set(Tags.sort, (m.get("sortField") == null) ? "" : m.get("sortField"));
				m.set(Tags.dir, (m.get("sortDir") == null || (m.get("sortDir") != null && m.<SortDir> get("sortDir").equals(  SortDir.NONE))) ? "" : m.get("sortDir"));  
			}
		});  
		loader.setSortDir(SortDir.DESC); 
		loader.setSortField(fields.get(0));
		loader.setRemoteSort(true);  

		ListStore<ModelData> store = new ListStore<ModelData>(loader);  

		

		ColumnModel cm = new ColumnModel(columns);
		
		grid.reconfigure(store, cm);
	}
}
