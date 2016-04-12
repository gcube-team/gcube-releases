package org.gcube.application.datamanagementfacilityportlet.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.forms.SubmissionPopup;
import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.SessionStatistics;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.fields.GroupGenerationRequestFields;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.GroupGenerationRequestPhase;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XTemplate;
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
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
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
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SubmittedRequestsGrid extends ContentPanel {


	public final Grid<ModelData> grid;

	public final PagingLoader<PagingLoadResult<ModelData>> loader;

	private final SubmittedRequestsGrid instance=this;
	final ToggleButton autoRefresh=new ToggleButton("Enable Auto Refresh", Resources.ICONS.refresh());
	
	public SubmittedRequestsGrid() {


		//****************** READER AND STORE Settings
		String url=DataManagementFacilityConstants.servletUrl.get(Tags.submittedReportServlet);
		HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));


		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(GroupGenerationRequestFields.author+"");  
		type.addField(GroupGenerationRequestFields.generationname+"");  
		type.addField(GroupGenerationRequestFields.id+"");  
		type.addField(GroupGenerationRequestFields.description+"");
		type.addField(GroupGenerationRequestFields.phase+"");  


		type.addField(GroupGenerationRequestFields.submissiontime+"");
		type.addField(GroupGenerationRequestFields.starttime+"");
		type.addField(GroupGenerationRequestFields.endtime+"");
		type.addField(GroupGenerationRequestFields.currentphasepercent+"");
		
		type.addField(GroupGenerationRequestFields.sourcehcafids+"");
		type.addField(GroupGenerationRequestFields.sourcehspenids+"");
		type.addField(GroupGenerationRequestFields.sourceoccurrencecellsids+"");
		type.addField(GroupGenerationRequestFields.reportid+"");
		type.addField(GroupGenerationRequestFields.generatedsourcesid+"");
		type.addField(GroupGenerationRequestFields.jobids+"");
		
		type.addField(GroupGenerationRequestFields.submissionbackend+"");
		type.addField(GroupGenerationRequestFields.executionenvironment+"");
		type.addField(GroupGenerationRequestFields.backendurl+"");
		type.addField(GroupGenerationRequestFields.environmentconfiguration+"");
		type.addField(GroupGenerationRequestFields.logic+"");
		type.addField(GroupGenerationRequestFields.numpartitions+"");
		
		type.addField(GroupGenerationRequestFields.algorithms+"");
		
		type.addField(GroupGenerationRequestFields.evaluatedcomputationcount+"");
		type.addField(GroupGenerationRequestFields.togeneratetablescount+"");





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
		loader.setSortField(GroupGenerationRequestFields.submissiontime+"");  
		loader.setRemoteSort(true);  



		ListStore<ModelData> store = new ListStore<ModelData>(loader);  

		//************************* COLUMN CONFIG

		XTemplate tpl = XTemplate.create("<p><b>Description:</b> {"+GroupGenerationRequestFields.description+"}</p><br>"+
				"<p><b>Execution id:</b> {"+GroupGenerationRequestFields.id+"}</p><br>"+
				"<p><b>Algorithms:</b> {"+GroupGenerationRequestFields.algorithms+"}</p><br>"+
				"<p><b>Computational Backend:</b> {"+GroupGenerationRequestFields.executionenvironment+"}</p><br>"+
				"<p><b>Assigned Resources:</b> {"+GroupGenerationRequestFields.numpartitions+"}</p><br>"
				
				//******HSPEC
				+"<tpl if=\""+GroupGenerationRequestFields.logic+"=='"+ClientLogicType.HSPEC+"'\">"+
					"<p><b>Selected HCAF IDs:</b> {"+GroupGenerationRequestFields.sourcehcafids+"}</p><br/>"+
					"<p><b>Selected HSPEN IDs:</b> {"+GroupGenerationRequestFields.sourcehspenids+"}</p><br/>"+
					"<tpl if=\""+GroupGenerationRequestFields.generatedsourcesid+"!=''"+"\"><p><b>Generated Table id(s):</b> {"+GroupGenerationRequestFields.generatedsourcesid+"}</p><br></tpl>"+
					"<tpl if=\""+GroupGenerationRequestFields.jobids+"!=''"+"\"><p><b>Related Image Generation Job id(s):</b> {"+GroupGenerationRequestFields.jobids+"}</p><br></tpl>"+
				"</tpl>"
				//******HSPEN
				+"<tpl if=\""+GroupGenerationRequestFields.logic+"=='"+ClientLogicType.HSPEN+"'\">"+
				"<p><b>Selected HCAF IDs:</b> {"+GroupGenerationRequestFields.sourcehcafids+"}</p><br/>"+
				"<p><b>Selected HSPEN IDs:</b> {"+GroupGenerationRequestFields.sourcehspenids+"}</p><br/>"+
				"<p><b>Selected OCCURRENCECELLS IDs:</b> {"+GroupGenerationRequestFields.sourceoccurrencecellsids+"}</p><br/>"+
				"</tpl>"+
				//*********HSPEC
				"<tpl if=\""+GroupGenerationRequestFields.logic+"=='"+ClientLogicType.HCAF+"'\">"+
				"<p><b>Selected HCAF IDs:</b> {"+GroupGenerationRequestFields.sourcehcafids+"}</p><br/>"+
				"</tpl>");  
		final RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  


		


		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		columns.add(expander);

		ColumnConfig title = new ColumnConfig(GroupGenerationRequestFields.generationname+"", "Title", 100);  
		columns.add(title);  
		
		columns.add(new ColumnConfig(GroupGenerationRequestFields.author+"", "Author", 200));  

		ColumnConfig logicColumn = new ColumnConfig(GroupGenerationRequestFields.logic+"", "Execution type", 200);  
		logicColumn.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					return DataManagementFacilityConstants.resourceNames.get(ClientResourceType.valueOf((String)model.get(property)));
				}catch(Exception e){
					Log.warn("Impossible to parse logic "+e.getMessage());
					return "N/A";
				}
			}
		});
		columns.add(logicColumn);
		
		
		ColumnConfig submissionColumn = new ColumnConfig(GroupGenerationRequestFields.submissiontime+"", "Submission Time", 200);  
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

//		ColumnConfig cloudColumn= new ColumnConfig(GroupGenerationRequestFields.executionenvironment+"","Computational Back End",200);
//		columns.add(cloudColumn);

		ColumnConfig phaseColumn=new ColumnConfig(GroupGenerationRequestFields.phase+"", "Phase", 180);
		phaseColumn.setRenderer(new GridCellRenderer<ModelData>() {

			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					String phaseValue=(String)model.get(GroupGenerationRequestFields.phase+"");
					switch(GroupGenerationRequestPhase.valueOf(phaseValue)){
						case completed : return "<span style='color:green'>Completed</span>";
						case datageneration : return "Generating Table(s)";
						case error : return "<span style='color:red'>Error</span>";
						case mapgeneration : return "Generating maps";
						case pending : return "Pending";
						case uploading : return "Sending uploaded file(s)";
//						case aborted : return "<span style='color:red'>Aborted</span>";
					}
					return "Invalid State";
				}catch(Exception e){
					Log.warn("Impossible to parse phase : "+e.getMessage());
					return "N/A";
				}
			}

		});
		columns.add(phaseColumn);
		
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
		
		ColumnConfig progressColumn=new ColumnConfig(GroupGenerationRequestFields.currentphasepercent+"", "Phase progress", 100);
		progressColumn.setRenderer(percentRenderer);
		columns.add(progressColumn);
		
		
		ColumnConfig hspecColumn= new ColumnConfig(GroupGenerationRequestFields.generatedsourcesid+"","Generated Table(s)",100);
		hspecColumn.setRenderer(new GridCellRenderer<ModelData>() {

			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				Integer generatedCount=0;
				try{
					try{
						String generatedList=(String)model.get(GroupGenerationRequestFields.generatedsourcesid+"");
						if(generatedList!=null&&!generatedList.equals("")){
							generatedCount=generatedList.split(",").length;
						}
					}catch(Exception e){	}
					String toGenerateTotal=((String)model.get(GroupGenerationRequestFields.togeneratetablescount+""));
					return generatedCount+"/"+toGenerateTotal; 
				}catch(Exception e){	
					return "N/A";
				}
			}

		});
		columns.add(hspecColumn);


		
		ColumnConfig completionColumn=new ColumnConfig(GroupGenerationRequestFields.endtime+"","Completion time (Elapsed)",250);
		completionColumn.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					long completion=Long.parseLong((String)model.get(GroupGenerationRequestFields.endtime+""));
					if (completion==0) return "N/A";
					long starttime=Long.parseLong((String)model.get(GroupGenerationRequestFields.starttime+""));
					return DataManagementFacilityConstants.timeFormat.format(new Timestamp(completion))+" ("+(completion-starttime)/(60*1000)+" mins)";
				}catch(Exception e){
//					Log.error("",e);
					return "N/A";
				}
			}
		});
		

		columns.add(completionColumn);
		
		ColumnModel cm = new ColumnModel(columns);









		grid= new Grid<ModelData>(store, cm);  
//		grid.addListener(Events.Attach, new Listener<GridEvent<ModelData>>() {  
//			public void handleEvent(GridEvent<ModelData> be) {  
//				loader.load(0, 500);  
//			}  
//		});  
		//		grid.setTrackMouseOver(false);  
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setAutoExpandColumn(GroupGenerationRequestFields.generationname+"");  

		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		grid.addPlugin(expander);
		
		
		
		
		


		//****************** TOOLBAR 

		ToolBar toolBar = new ToolBar();  

		//		Button refreshButton=new Button("Refresh",new SelectionListener<ButtonEvent>() {  
		//
		//			@Override  
		//			public void componentSelected(ButtonEvent ce) {
		//				Log.debug("Reloading grid..");
		//				  loader.load();
		//			}  
		//
		//		});
		//		toolBar.add(refreshButton);

		Menu menu = new Menu();  
		  
	    MenuItem submitHSPECItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEC),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		try{
	    		SubmissionPopup popup=new SubmissionPopup(ClientLogicType.HSPEC);
				popup.show();
	    		}catch(Exception e){
	    			Log.error("", e);
	    		}
	    	};
		});  
	    menu.add(submitHSPECItem);
	    MenuItem submitHSPENItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		try{
	    		SubmissionPopup popup=new SubmissionPopup(ClientLogicType.HSPEN);
				popup.show();
	    		}catch(Exception e){
	    			Log.error("", e);
	    		}
	    	};
		});  
	    menu.add(submitHSPENItem);

	    MenuItem submitHCAFItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HCAF),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		try{
	    		SubmissionPopup popup=new SubmissionPopup(ClientLogicType.HCAF);
				popup.show();
	    		}catch(Exception e){
	    			Log.error("", e);
	    		}
	    	};
		});  
	    menu.add(submitHCAFItem);
	    
		final Button submitButton=new Button("Submit New Request",Resources.ICONS.cog_add());
		submitButton.setMenu(menu);

		toolBar.add(submitButton);  
		toolBar.add(new SeparatorToolItem());

		final Button openDetails=new Button("Show selected details", Resources.ICONS.report(),new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) {  
				MessageBox.alert("To Implement", "The requested feature is not ready yet",null); 
			}  

		});
		openDetails.disable();
		
		//TODO manage details
//		toolBar.add(openDetails);  
//		toolBar.add(new SeparatorToolItem());

		
		final Button removeExecution=new Button("Remove selected", Resources.ICONS.cog_delete(),new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) { 
				instance.mask("Performing...");
				final String id= grid.getSelectionModel().getSelectedItem().get(GroupGenerationRequestFields.id+"");
				Log.debug("Removing request : "+id);
				DataManagementFacility.localService.removeGeneration(id,false,false, new AsyncCallback<Response>() {
					
					public void onSuccess(Response arg0) {
						if(arg0.getStatus()){
							String v = Format.ellipse(id, 80);
							Info.display("Delete Submission","Successfully deleted execution , ID : {0}", new Params(v));
							instance.unmask();
							loader.load();
						}else{
							String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
							String v = Format.ellipse(message, 80);
							Info.display("Delete Submission","Unable to delete request : {0}", new Params(v));
							instance.unmask();
						}

					}

					public void onFailure(Throwable arg0) {
						Log.error("Unexpected error while submitting request", arg0);
						String message="Please contact support";
						String v = Format.ellipse(message, 80);
						Info.display("HSPEC Group Generation Submission","Unable to submit request : {0}", new Params(v));
						instance.unmask();
					}
				}); 
			}  

		});
		removeExecution.disable();
		
		toolBar.add(removeExecution);  
		toolBar.add(new SeparatorToolItem());
final Button resubmit=new Button("Resubmit", Resources.ICONS.cog_go(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Sending request..");
				String id=grid.getSelectionModel().getSelectedItem().get(GroupGenerationRequestFields.id+"");
				DataManagementFacility.localService.resubmitRequest(id, new AsyncCallback<Response>() {

					public void onSuccess(Response arg0) {
						instance.unmask();
						if(arg0.getStatus()){
							String message=(String) arg0.getAdditionalObjects().get(Tags.responseGroupGeneration);
							String v = Format.ellipse(message, 80);
							Info.display("Generation Submission","Successfully submitted request, ID : {0}", new Params(v));
							instance.unmask();
							DataManagementFacility.get().centerGrid.loader.load();
						}else{
							String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
							String v = Format.ellipse(message, 80);
							Info.display("Generation Submission","Unable to submit request : {0}", new Params(v));
							instance.unmask();
						}

					}

					public void onFailure(Throwable arg0) {
						Log.error("Unexpected error while submitting request", arg0);
						String message="Please contact support";
						String v = Format.ellipse(message, 80);
						Info.display("Generation Submission","Unable to submit request : {0}", new Params(v));
						instance.unmask();
					}
				});
			}
		});
		resubmit.disable();
		toolBar.add(resubmit);
		toolBar.add(new SeparatorToolItem());
		
		final Button openMonitor=new Button("Monitor activity",Resources.ICONS.chart(),new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) {
				try{
					final ModelData model =grid.getSelectionModel().getSelectedItem();
					
					if(model.get(GroupGenerationRequestFields.phase+"").equals(GroupGenerationRequestPhase.datageneration+"")){
						final String reportId=model.get(GroupGenerationRequestFields.reportid+"");
						Log.debug("Report ID is "+reportId);
						instance.mask("Loading session details..");
						DataManagementFacility.localService.getSessionStatistics(new AsyncCallback<SessionStatistics>() {
							public void onFailure(Throwable arg0) {
								Log.error("Unexpected error while loading session statistics ");								
								Info.display("Load session details","Unable to load details: {0}", new Params(arg0.getMessage()));
								instance.unmask();
							}
							
							public void onSuccess(SessionStatistics arg0) {
								String url=DataManagementFacilityConstants.getMonitorUrl()+"?" +
								"liveUrl="+DataManagementFacilityConstants.servletUrl.get(Tags.resourceLoadServlet)+
								"/"+arg0.getScope()+"/"+reportId+"&"+
								"resUrl="+DataManagementFacilityConstants.servletUrl.get(Tags.resourceMapServlet)+
								"/"+arg0.getScope()+"/"+reportId;
								Log.debug("Monitor Url is "+url);
								Window.open(url, "Monitor : "+model.get(GroupGenerationRequestFields.generationname+""), "");
								instance.unmask();
							};
						});
						
						
					}else MessageBox.alert("Monitor Feature", "Live monitor is available only while generating HSPECs.",null);
				}catch(Exception e){
					
				
					Log.error("Unexpected error while opening monitor..",e);
					MessageBox.alert("Monitor Feature", "Sorry, something went wrong.<br> Please, retry later or contact support.",null); 
				}
			}  

		});
		openMonitor.disable();
		toolBar.add(openMonitor);  
		toolBar.add(new SeparatorToolItem());
		
		
		
		
		toolBar.add(autoRefresh);  
		toolBar.add(new SeparatorToolItem());
		
		
		
		grid.getSelectionModel().addListener(Events.SelectionChange,  
		         new Listener<SelectionChangedEvent<ModelData>>() {  
		           public void handleEvent(SelectionChangedEvent<ModelData> be) {  
		             if (be.getSelection().size() > 0){
		            	 resubmit.enable();
		            	 openDetails.enable();
		            	 try{
		            		 GroupGenerationRequestPhase phase=GroupGenerationRequestPhase.valueOf(
		            				 (String)be.getSelectedItem().get(GroupGenerationRequestFields.phase+""));
		            		 ClientLogicType logic=ClientLogicType.valueOf((String)be.getSelectedItem().get(GroupGenerationRequestFields.logic+""));
		            		 if(phase.equals(GroupGenerationRequestPhase.error)||phase.equals(GroupGenerationRequestPhase.completed))
		            			 removeExecution.enable();
		            		 if(logic.equals(ClientLogicType.HCAF)) openMonitor.disable();
		            		 else{ 
		            			 if(phase.equals(GroupGenerationRequestPhase.datageneration))
		            				 openMonitor.enable();
		            			 else openMonitor.disable();
		            		 }
		            	 }catch(Exception e){
		            		 Log.error("Unable to parse selected phase ",e);
		            	 }
		             }else{   
		            	 resubmit.disable();
		            	 removeExecution.disable();
		            	 openMonitor.disable();
		            	 openDetails.disable();
		             }
		           }  
		         });  


		//*************** Bottom Bar

		//**** Uncomment for live view
//
//				ToolBar bottomToolbar=new ToolBar();
//				 LiveToolItem item = new LiveToolItem();  
//				 item.bindGrid(grid);  
//				 bottomToolbar.add(item);  

		////**** Uncomment for buffered view 
		final PagingToolBar bottomToolbar = new PagingToolBar(500);  
		bottomToolbar.bind(loader);


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
