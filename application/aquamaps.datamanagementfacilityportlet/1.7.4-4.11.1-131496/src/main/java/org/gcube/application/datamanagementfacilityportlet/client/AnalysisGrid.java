package org.gcube.application.datamanagementfacilityportlet.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.fields.ClientAnalysisFields;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.fields.GroupGenerationRequestFields;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientAnalysisStatus;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalysisGrid extends ContentPanel{

	public final Grid<ModelData> grid;
	public final PagingLoader<PagingLoadResult<ModelData>> loader;

	private final AnalysisGrid instance=this;

	private Button retrieve;
	private Button remove;
	final ToggleButton autoRefresh=new ToggleButton("Enable Auto Refresh", Resources.ICONS.refresh());

	public AnalysisGrid() {
		//****************** READER AND STORE Settings
		String url=DataManagementFacilityConstants.servletUrl.get(Tags.analysisServlet);
		HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));

		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(ClientAnalysisFields.id+"");
		type.addField(ClientAnalysisFields.author+"");
		type.addField(ClientAnalysisFields.currentphasepercent+"");
		type.addField(ClientAnalysisFields.description+"");
		type.addField(ClientAnalysisFields.endtime+"");
		type.addField(ClientAnalysisFields.starttime+"");
		type.addField(ClientAnalysisFields.status+"");
		type.addField(ClientAnalysisFields.submissiontime+"");
		type.addField(ClientAnalysisFields.endtime+"");
		type.addField(ClientAnalysisFields.title+"");
		type.addField(ClientAnalysisFields.type+"");
		type.addField(ClientAnalysisFields.sources+"");

		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);  

		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(httpProxy,reader){
			protected void onLoadFailure(Object loadConfig, Throwable t) {
				super.onLoadFailure(loadConfig, t);
				Log.debug("THE LOAD EVENT HAS BEEN CANCELLED");
				autoRefresh.toggle(false);
				MessageBox.alert("Alert", "Your session seems to be expired, please refresh page.", null);  
			};
		}; 

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
		loader.setSortField(ClientAnalysisFields.submissiontime+"");  
		loader.setRemoteSort(true);  

		//************************* COLUMNS

		ListStore<ModelData> store = new ListStore<ModelData>(loader);

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  

		ColumnConfig idCol = new ColumnConfig(ClientAnalysisFields.id+"", "ID", 100);
		columns.add(idCol);
		ColumnConfig titleCol = new ColumnConfig(ClientAnalysisFields.title+"", "Title", 100);
		columns.add(titleCol);
		ColumnConfig authCol = new ColumnConfig(ClientAnalysisFields.author+"", "Author", 100);
		columns.add(authCol);
		ColumnConfig typeCol = new ColumnConfig(ClientAnalysisFields.type+"", "Type", 100);
		columns.add(typeCol);
		ColumnConfig statusCol = new ColumnConfig(ClientAnalysisFields.status+"", "Status", 100);
		columns.add(statusCol);
		final NumberFormat number = NumberFormat.getFormat("0.00");
		GridCellRenderer<ModelData> percentRenderer=new GridCellRenderer<ModelData>() {  
			public String render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
				double val = Double.parseDouble((String)model.get(GroupGenerationRequestFields.currentphasepercent+""));
				String style = val < 100 ? "blue" : "green";  
				return "<span style='color:" + style + "'>" + number.format(val) + " %</span>";
				}catch(Exception e){
					return "N/A";
				}
			}  
		};
		ColumnConfig percentCol = new ColumnConfig(ClientAnalysisFields.currentphasepercent+"", "Progress", 100);
		percentCol.setRenderer(percentRenderer);
		columns.add(percentCol);
		
		ColumnConfig submissionColumn = new ColumnConfig(ClientAnalysisFields.submissiontime+"", "Submission Time", 200);  
		submissionColumn.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					return DataManagementFacilityConstants.timeFormat.format(new Timestamp(Long.parseLong((String)model.get(property))));
				}catch(Exception e){
					Log.warn("Impossible to parse timestamp "+e.getMessage());
					return "N/A";
				}
			}
		});
		columns.add(submissionColumn); 
		ColumnConfig phaseColumn=new ColumnConfig(ClientAnalysisFields.status+"", "Status", 180);
		phaseColumn.setRenderer(new GridCellRenderer<ModelData>() {

			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					String phaseValue=(String)model.get(ClientAnalysisFields.status+"");
					switch(ClientAnalysisStatus.valueOf(phaseValue)){
						case Completed : return "<span style='color:green'>Completed</span>";
						case Generating : return "Processing Data";
						case Error : return "<span style='color:red'>Error</span>";						
						case Pending : return "Pending";
						default : return phaseValue;
					}
				}catch(Exception e){
					Log.warn("Impossible to parse phase : "+e.getMessage());
					return "N/A";
				}
			}

		});
		columns.add(phaseColumn);
		ColumnConfig completionColumn=new ColumnConfig(ClientAnalysisFields.endtime+"","Completion time (Elapsed)",250);
		completionColumn.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					long completion=Long.parseLong((String)model.get(ClientAnalysisFields.endtime+""));
					if (completion==0) return "N/A";
					long starttime=Long.parseLong((String)model.get(ClientAnalysisFields.starttime+""));
					return DataManagementFacilityConstants.timeFormat.format(new Timestamp(completion))+" ("+(completion-starttime)/(60*1000)+" mins)";
				}catch(Exception e){
//					Log.error("",e);
					return "N/A";
				}
			}
		});
		columns.add(completionColumn);
		ColumnConfig sourcesCol = new ColumnConfig(ClientAnalysisFields.sources+"", "Selected Sources Ids", 180);
		columns.add(sourcesCol);
		ColumnModel cm = new ColumnModel(columns);

		//********************* GRID

		grid=new Grid<ModelData>(store,cm);
//		grid.addListener(Events.Attach, new Listener<GridEvent<ModelData>>() {  
//			public void handleEvent(GridEvent<ModelData> be) {  
//				loader.load(0, 500);  
//			}  
//		});  
		//		grid.setTrackMouseOver(false);  
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setAutoExpandColumn(ClientResource.TITLE+"");  


		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelectedItem()!=null){
					retrieve.enable();
					remove.enable();
				}
				else {
					retrieve.disable();
					remove.disable();
				}
				
			}
		});
		
		
		//VIEW	
		ExtendedLiveGridView liveView = new ExtendedLiveGridView();  
		liveView.setEmptyText("No rows available on the server.");  
		grid.setView(liveView);

		///***************** TOOLBAR

		ToolBar toolBar = new ToolBar();  

		
		toolBar.add(autoRefresh);  
		toolBar.add(new SeparatorToolItem());
		retrieve=new Button("Retrieve Results", Resources.ICONS.report(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				final String toExportId=grid.getSelectionModel().getSelectedItem().get(ClientAnalysisFields.id+"");
				String toExportName=(String)grid.getSelectionModel().getSelectedItem().get(ClientAnalysisFields.title+"");
				
				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(toExportName, true);
				
				  WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
			        	 
			    		@Override
			    		public void onSaving(Item parent, String fileName) {
			    			DataManagementFacilityConstants.sendSaveRequest(
									new SaveRequest(SaveOperationType.ANALYSIS, toExportId, parent.getId(), fileName));
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
				
//				WorkspaceLightTreeSavePopup popup=new WorkspaceLightTreeSavePopup("Save analysis data ", true, toExportName);
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
//								new SaveRequest(SaveOperationType.ANALYSIS, toExportId, item.getId(), name));
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
		retrieve.disable();
		toolBar.add(retrieve);
		
		
		remove=new Button("Remove Analysis", Resources.ICONS.delete(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final String id=grid.getSelectionModel().getSelectedItem().get(ClientAnalysisFields.id+"");
				final String name=grid.getSelectionModel().getSelectedItem().get(ClientAnalysisFields.title+"");
				MessageBox.confirm("Confirm", "Are you sure you want to do delete "+name+" ?", new Listener<MessageBoxEvent>(){
					public void handleEvent(MessageBoxEvent be) {
						String pressed=be.getButtonClicked().getText();
						if(pressed.equalsIgnoreCase("yes")){
							
							DataManagementFacility.localService.removeAnalysis(id, new AsyncCallback<Response>() {
								
								@Override
								public void onSuccess(Response result) {
									if(result.getStatus()){
										Info.display("Remove Analysis","Succesfully Deleted "+name);
										loader.load();
									}else {
										Info.display("Remove Analysis","Something went wrong "+result.getAdditionalObjects().get(Tags.errorMessage));
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Info.display("Remove Analysis","Something went wrong "+caught.getMessage());
								}
							});
						}}
				});
				
			}
		});
		remove.disable();
		toolBar.add(new SeparatorToolItem());
		toolBar.add(remove);
		
		//****************** BOTTOM


		ToolBar bottomToolbar=new ToolBar();
		LiveToolItem item = new LiveToolItem();  
		item.bindGrid(grid);  
		bottomToolbar.add(item); 

		// ************** Panel settings
		this.setFrame(true);  
		this.setCollapsible(false);
		this.setAnimCollapse(false);  
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());  
		this.add(grid);  
		this.setSize(600, 350);  
		this.setTopComponent(toolBar);
		this.setBottomComponent(bottomToolbar);

		// ************* Refresh time

		Timer t = new Timer(){

			@Override
			public void run() {
				if(autoRefresh.isPressed())loader.load();
			}

		};
		t.scheduleRepeating(2*1000);
	}
}
