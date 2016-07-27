package org.gcube.application.datamanagementfacilityportlet.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.application.datamanagementfacilityportlet.client.forms.SourceEditingPopup;
import org.gcube.application.datamanagementfacilityportlet.client.forms.SubmitAnalysisPopup;
import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.AlgorithmType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientAnalysisType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;
import org.gcube.portal.databook.shared.ImageType;
import org.gcube.portlets.user.csvimportwizard.client.ImportWizard;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardListener;
import org.gcube.portlets.user.csvimportwizard.client.source.local.LocalSource;
import org.gcube.portlets.user.csvimportwizard.ws.client.WorkspaceSource;
import org.gcube.portlets.widgets.applicationnews.client.PostAppNewsDialog;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XDOM;
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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ResourceGrid extends ContentPanel {

	public final Grid<ModelData> grid;
	public final PagingLoader<PagingLoadResult<ModelData>> loader;

	private Button edit;
	private Button drop;
	private Button export;
	private Button importCSV;
	private Button analyze;
	private Button publishTable;
	
	final ToggleButton autoRefresh=new ToggleButton("Enable Auto Refresh", Resources.ICONS.refresh());
	
	private final ResourceGrid instance=this;
	
	final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
		public void handleEvent(MessageBoxEvent ce) {  
			instance.unmask();  
		}  
	};
	
	
	public ResourceGrid() {
		//****************** READER AND STORE Settings
		String url=DataManagementFacilityConstants.servletUrl.get(Tags.resourceServlet);
		HttpProxy httpProxy = new HttpProxy(new RequestBuilder(RequestBuilder.GET, url));
		
		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(ClientResource.ALGORITHM);
		type.addField(ClientResource.AUTHOR);
		type.addField(ClientResource.GENERATION_TIME);
		type.addField(ClientResource.DESCRIPTION);
		type.addField(ClientResource.DISCLAIMER);
		type.addField(ClientResource.PARAMETERS);
		type.addField(ClientResource.PROVENANCE);
		type.addField(ClientResource.SEARCH_ID);
		type.addField(ClientResource.SOURCE_HCAF);
		type.addField(ClientResource.SOURCE_HCAF_TABLE);
		type.addField(ClientResource.SOURCE_HSPEC);
		type.addField(ClientResource.SOURCE_HSPEC_TABLE);
		type.addField(ClientResource.SOURCE_HSPEN);
		type.addField(ClientResource.SOURCE_HSPEN_TABLE);
		type.addField(ClientResource.SOURCE_OCCURRENCE);
		type.addField(ClientResource.SOURCE_OCCURRENCE_TABLE);
		type.addField(ClientResource.STATUS);
		type.addField(ClientResource.TABLE_NAME);
		type.addField(ClientResource.TITLE);
		type.addField(ClientResource.TYPE);
		type.addField(ClientResource.DEFAULT);
		type.addField(ClientResource.ROW_COUNT);
		
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
		loader.setSortField(ClientResource.SEARCH_ID+"");  
		loader.setRemoteSort(true);  

		ListStore<ModelData> store = new ListStore<ModelData>(loader);  
		
		//********************* COLUMNS
		
		XTemplate tpl = XTemplate.create("<p><b>Description:</b> {"+ClientResource.DESCRIPTION+"}</p><br/>"
				+"<p><b>Disclaimer:</b> {"+ClientResource.DISCLAIMER+"}</p><br/>"
				+"<p><b>Provenance:</b> {"+ClientResource.PROVENANCE+"}</p><br/>"
				+"<p><b>Table Name :</b> {"+ClientResource.TABLE_NAME+"}</p><br/>"
				+"<tpl if=\" "+ClientResource.ALGORITHM+" != \'\' \" >" +
						"<p><b>Algorithm :</b> {"+ClientResource.ALGORITHM+"}</p><br/>" +
				"</tpl>"
				// SOURCES
				
				+"<tpl if=\" "+ClientResource.SOURCE_HCAF+" != \'\' \" >" +
							"<p><b>Source HCAF ID:</b> {"+ClientResource.SOURCE_HCAF+"}</p><br/>" +
				"</tpl>"+
				"<tpl if=\""+ClientResource.SOURCE_HSPEN+" != \'\' \" >" +
						"<p><b>Source HSPEN ID:</b> {"+ClientResource.SOURCE_HSPEN+"}</p><br/>" +
				"</tpl>"+
				"<tpl if=\""+ClientResource.SOURCE_OCCURRENCE+" != \'\' \" >" +
						"<p><b>Source OCCURRENCE ID:</b> {"+ClientResource.SOURCE_OCCURRENCE+"}</p><br/>" +
				"</tpl>");  
		final RowExpander expander = new RowExpander();  
		expander.setTemplate(tpl);  

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		columns.add(expander);
		
		ColumnConfig idCol = new ColumnConfig(ClientResource.SEARCH_ID, "ID", 100);
		columns.add(idCol);
		ColumnConfig titleCol = new ColumnConfig(ClientResource.TITLE, "Title", 100);
		TextField<String> titleEditor=new TextField<String>();
		titleEditor.setAllowBlank(false);
		titleCol.setEditor(new CellEditor(titleEditor));
		columns.add(titleCol);
		ColumnConfig authorCol = new ColumnConfig(ClientResource.AUTHOR, "Author", 100);
		columns.add(authorCol);
		ColumnConfig dateCol = new ColumnConfig(ClientResource.GENERATION_TIME, "Generation Time", 100);
		dateCol.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					return DataManagementFacilityConstants.timeFormat.format(new Timestamp(Long.parseLong((String)model.get(property))));
				}catch(Exception e){
					Log.warn("Impossible to parse timestamp "+e.getMessage());
				}
				return "N/A";
			}
		});
		columns.add(dateCol);
		ColumnConfig typeCol = new ColumnConfig(ClientResource.TYPE, "Type", 180);
		columns.add(typeCol);
		ColumnConfig defaultCol = new ColumnConfig(ClientResource.DEFAULT,"DEFAULT",80);
		defaultCol.setRenderer(new GridCellRenderer<ModelData>() {
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				try{
					return (Integer.parseInt((String)model.get(ClientResource.DEFAULT)))==1;
				}catch(Exception e){
					Log.warn("Impossible to parse boolean "+e.getMessage());
				}
				return "N/A";
			}
		});
		columns.add(defaultCol);
		
		
		ColumnConfig statusCol = new ColumnConfig(ClientResource.STATUS,"Status",120);
		columns.add(statusCol);
		
		ColumnConfig rowCol = new ColumnConfig(ClientResource.ROW_COUNT, "Rows", 100);
		columns.add(rowCol);
		
		
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

		
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);  
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelectedItem()!=null){
					edit.enable();
					drop.enable();
					export.enable();
					publishTable.enable();
//					customQuery.enable();
				}
				else {
					edit.disable();
					drop.disable();
					export.disable();
					publishTable.disable();
//					customQuery.disable();
				}
				if(se.getSelection().size()>1) analyze.enable();
				else analyze.disable();
			}
		});
		grid.addPlugin(expander);
		
		
		
	

		 


		//****************** TOOLBAR 

		ToolBar toolBar = new ToolBar();  

		
		toolBar.add(autoRefresh);  
		toolBar.add(new SeparatorToolItem());
		
		edit=new Button("Edit Selected Resource",Resources.ICONS.report(),new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				ModelData model=grid.getSelectionModel().getSelectedItem();
				if(model!=null){					
					String title=(String)model.get(ClientResource.TITLE);
					Integer id=Integer.parseInt((String)model.get(ClientResource.SEARCH_ID));
					String table=(String)model.get(ClientResource.TABLE_NAME);
					String description=(String)model.get(ClientResource.DESCRIPTION); 
					String author=(String)model.get(ClientResource.AUTHOR); 
					String disclaimer=(String)model.get(ClientResource.DISCLAIMER); 
					String provenance=(String)model.get(ClientResource.PROVENANCE); 
					String generationTime=(String)model.get(ClientResource.GENERATION_TIME); 
					String hcafTable=(String)model.get(ClientResource.SOURCE_HCAF_TABLE);
					String hspenTable=(String)model.get(ClientResource.SOURCE_HSPEN_TABLE);
					String hspecTable=(String)model.get(ClientResource.SOURCE_HSPEC_TABLE);
					String occurTable=(String)model.get(ClientResource.SOURCE_OCCURRENCE_TABLE);
					String parameters=(String)model.get(ClientResource.PARAMETERS);
					String status=(String)model.get(ClientResource.STATUS);
					ClientResourceType type=ClientResourceType.HCAF;
					try{
						type=ClientResourceType.valueOf((String)model.get(ClientResource.TYPE));
					}catch(Exception e){
						Log.debug("Unable to load type, value was "+(String)model.get(ClientResource.TYPE));
					}
					AlgorithmType algorithm=AlgorithmType.SuitableRange;
					try{
						algorithm=AlgorithmType.valueOf((String)model.get(ClientResource.ALGORITHM));
					}catch(Exception e){
						Log.debug("Unable to load algorithm, value was "+(String)model.get(ClientResource.ALGORITHM));
					}
					Boolean defaultSource=false;
					try{
						defaultSource=Integer.parseInt((String)model.get(ClientResource.DEFAULT))==1;
					}catch(Exception e){
						Log.debug("Unable to load default, value was "+(String)model.get(ClientResource.DEFAULT));
					}
					Long rowCount=0l;
					try{
						rowCount=Long.parseLong((String)model.get(ClientResource.ROW_COUNT));
					}catch(Exception e){
						Log.debug("Unable to load row count, value was "+(String)model.get(ClientResource.ROW_COUNT));
					}
					
					ClientResource toEdit = new ClientResource(title,id,table,description,author,disclaimer,provenance,generationTime,
							(String)model.get(ClientResource.SOURCE_HCAF),
							(String)model.get(ClientResource.SOURCE_HSPEN),(String)model.get(ClientResource.SOURCE_HSPEC),(String)model.get(ClientResource.SOURCE_OCCURRENCE),
							hcafTable,hspenTable,hspecTable,occurTable,parameters,status,type,algorithm,defaultSource,rowCount);
							
					SourceEditingPopup popup=new SourceEditingPopup(toEdit);
					popup.show();
				}
			}
		});
		edit.disable();
		toolBar.add(edit);
		
		drop=new Button("Remove Source", Resources.ICONS.table_delete(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				final int totalCount=grid.getSelectionModel().getSelectedItems().size();
				MessageBox.confirm("Confirm", "Are you sure you want to do delete "+totalCount+" source"+(totalCount>1?"s":"")+" ?", new Listener<MessageBoxEvent>(){
					public void handleEvent(MessageBoxEvent be) {
						String pressed=be.getButtonClicked().getText();
						if(pressed.equalsIgnoreCase("yes")){
							
							 final MessageBox box = MessageBox.progress("Please wait", "Deleting items...",  
					            "Initializing...");
							List<ModelData> toDelete=grid.getSelectionModel().getSelectedItems();
							final ArrayList<Integer> performedDeletions=new ArrayList<Integer>();
							
							final HashMap<Integer,String> failedDeletions=new HashMap<Integer, String>();
							///TODO count of failed deletions
							
							for(int i=0;i<toDelete.size();i++){
								
								final Integer toDeleteId=Integer.parseInt((String)toDelete.get(i).get(ClientResource.SEARCH_ID));								
								DataManagementFacility.localService.dropTable(toDeleteId, new AsyncCallback<Response>() {

									public void onSuccess(Response arg0) {
										performedDeletions.add(toDeleteId);
										if(!arg0.getStatus()) failedDeletions.put(toDeleteId, arg0.getAdditionalObjects().get(Tags.errorMessage));
										box.updateProgress(performedDeletions.size() / totalCount, (int) performedDeletions.size() + "/"+totalCount+" performed deletions"); 
										
										if(performedDeletions.size()==totalCount){
											box.close();
											Info.display("Delete Resources","Deleted "+(performedDeletions.size()-failedDeletions.size())+" out of "+totalCount);	
											loader.load();
										}

									}

									public void onFailure(Throwable arg0) {
										performedDeletions.add(toDeleteId);
										failedDeletions.put(toDeleteId, arg0.getMessage());
										box.updateProgress(performedDeletions.size() / totalCount, (int) performedDeletions.size() + "/"+totalCount+" performed deletions");
										if(performedDeletions.size()==totalCount){
											box.close();
											Info.display("Delete Resources","Deleted "+(performedDeletions.size()-failedDeletions.size())+" out of "+totalCount);
											loader.load();
										}										
									}
								});
							}
							
							Integer toDeleteId=Integer.parseInt((String)grid.getSelectionModel().getSelectedItem().get(ClientResource.SEARCH_ID));
							
							
						}
					};
				});  
				
			}
		});
		drop.disable();
		toolBar.add(drop);
		
		export=new Button("Export csv", Resources.ICONS.report(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {				
				final Integer toExportId=Integer.parseInt((String)grid.getSelectionModel().getSelectedItem().get(ClientResource.SEARCH_ID));
				String toExportName=(String)grid.getSelectionModel().getSelectedItem().get(ClientResource.TITLE);
				
				final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(toExportName, true);
				
				  WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
			        	 
			    		@Override
			    		public void onSaving(Item parent, String fileName) {
			    			DataManagementFacilityConstants.sendSaveRequest(
									new SaveRequest(SaveOperationType.RESOURCE, toExportId+"", parent.getId(), fileName));
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
				
//				WorkspaceLightTreeSavePopup popup=new WorkspaceLightTreeSavePopup("Export resource to csv", true, toExportName);
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
//						
//						DataManagementFacilityConstants.sendSaveRequest(
//								new SaveRequest(SaveOperationType.RESOURCE, toExportId+"", item.getId(), name));
//
//					}
//					else {
//
//					}
//
//				}});
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
		export.disable();
		toolBar.add(export);
		
		Menu importCSVMenu = new Menu();  
		  
	    MenuItem importHSPECItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEC),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		instance.mask();
	    		DataManagementFacility.localService.setImportRequestType(ClientResourceType.HSPEC, setImportTypeCallBack);
	    	};
		});  
	    MenuItem importHSPENItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEN),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		instance.mask();
	    		DataManagementFacility.localService.setImportRequestType(ClientResourceType.HSPEN, setImportTypeCallBack);
	    	};
		});  
	    MenuItem importHCAFItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HCAF),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		instance.mask();
	    		DataManagementFacility.localService.setImportRequestType(ClientResourceType.HCAF, setImportTypeCallBack);
	    	};
		});  
	    MenuItem importOCCURRENCEItem = new MenuItem(DataManagementFacilityConstants.resourceNames.get(ClientResourceType.OCCURRENCECELLS),new SelectionListener<MenuEvent>() {
	    	public void componentSelected(MenuEvent ce) {
	    		instance.mask();
	    		DataManagementFacility.localService.setImportRequestType(ClientResourceType.OCCURRENCECELLS, setImportTypeCallBack);
	    	};
		});  
		
	    importCSVMenu.add(importHSPECItem);
	    importCSVMenu.add(importHSPENItem);
	    importCSVMenu.add(importHCAFItem);
	    importCSVMenu.add(importOCCURRENCEItem);
		
		importCSV=new Button("Import CSV",Resources.ICONS.report());
		importCSV.setMenu(importCSVMenu);
	
		toolBar.add(importCSV);
		
		toolBar.add(new SeparatorToolItem());
		analyze=new Button("Submit Analysis Process",Resources.ICONS.chart_analysis(),new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {			
				try{
					List<ClientTinyResource> toAnalyze=new ArrayList<ClientTinyResource>();
					List<ClientAnalysisType> toPerformAnalisys=new ArrayList<ClientAnalysisType>();
					HashMap<ClientResourceType,Integer> selectedSourcesType=new HashMap<ClientResourceType, Integer>();
					
					for(ModelData data:grid.getSelectionModel().getSelectedItems()){						
						ClientResourceType type=ClientResourceType.valueOf((String)data.get(ClientResource.TYPE));
						if(type.equals(ClientResourceType.OCCURRENCECELLS)) throw new Exception("Operation not supported for "+type+" resources.");
						toAnalyze.add(new ClientTinyResource(
								Integer.parseInt((String)data.get(ClientResource.SEARCH_ID)),
								type+"",(String) data.get(ClientResource.TITLE),(String)data.get(ClientResource.SOURCE_HCAF)));
						if(selectedSourcesType.containsKey(type)) selectedSourcesType.put(type,selectedSourcesType.get(type)+1);
						else selectedSourcesType.put(type,1);						
					}
					
					
					if(toAnalyze.size()<2) throw new Exception ("Not enough resources selected");
					for(Entry<ClientResourceType,Integer> entry:selectedSourcesType.entrySet()){
						if(entry.getValue()<2) throw new Exception("Analysis requires at least 2 sources of the same type ("+entry.getKey()+")");
						switch(entry.getKey()){
							case HCAF : 	toPerformAnalisys.add(ClientAnalysisType.GEOGRAPHIC_HCAF);
											toPerformAnalisys.add(ClientAnalysisType.HCAF);
											break;
							case HSPEC :  	toPerformAnalisys.add(ClientAnalysisType.GEOGRAPHIC_HSPEC);
											toPerformAnalisys.add(ClientAnalysisType.HSPEC);
											break;
							case HSPEN :  	toPerformAnalisys.add(ClientAnalysisType.HSPEN);
											break;							
						}
					}
					if(selectedSourcesType.containsKey(ClientResourceType.HCAF)&&selectedSourcesType.containsKey(ClientResourceType.HSPEC)) 
						if(selectedSourcesType.get(ClientResourceType.HCAF).equals(selectedSourcesType.get(ClientResourceType.HSPEC)))
							toPerformAnalisys.add(ClientAnalysisType.MIXED);
						else throw new Exception("Combined "+DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HCAF)+
								"/"+DataManagementFacilityConstants.resourceNames.get(ClientResourceType.HSPEC)+"  Analysis requires same amount of sources for both types.");
					 
					SubmitAnalysisPopup popup=new SubmitAnalysisPopup(toPerformAnalisys);
					popup.show();
					popup.grid.getStore().add(toAnalyze);
				}catch(Exception e){
					MessageBox.alert("Invalid Operation", e.getMessage(), l);
				}
			}
		});
		analyze.disable();
		toolBar.add(analyze);
		
		publishTable=new Button("Publish table", Resources.ICONS.feed()	, new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				ModelData selected=grid.getSelectionModel().getSelectedItem();
				
				LinkPreview preview=new LinkPreview();
				preview.setDescription((String) selected.get(ClientResource.DESCRIPTION));
				preview.setImageType(ImageType.JPG);
				preview.setLinkThumbnailUrl(DataManagementFacilityConstants.getImagePreviewUrl());
				preview.setTitle((String) selected.get(ClientResource.TITLE));
				String resourceType=selected.get(ClientResource.TYPE);
				String resourceId=selected.get(ClientResource.SEARCH_ID);
				
				 new PostAppNewsDialog(
						 DataManagementFacility.class+"", 
						 "A new "+resourceType+" Table is available", Tags.TO_OPEN_TABLE+"="+resourceId, preview);
				
			}
		});
		
		publishTable.disable();
		toolBar.add(publishTable);
		//****************** BOTTOM
		

//				ToolBar bottomToolbar=new ToolBar();
//				 LiveToolItem item = new LiveToolItem();  
//				 item.bindGrid(grid);  
//				 bottomToolbar.add(item);  
					//VIEW	
//					ExtendedLiveGridView liveView = new ExtendedLiveGridView();  
//					liveView.setEmptyText("No rows available on the server.");  
//					grid.setView(liveView);


			 final PagingToolBar bottomToolBar = new PagingToolBar(50);  
			    bottomToolBar.bind(loader);  

		
		// ************** Panel settings
		this.setFrame(true);  
		this.setCollapsible(false);
		this.setAnimCollapse(false);  
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());  
		this.add(grid);  
		this.setSize(600, 350);  
		this.setTopComponent(toolBar);
		this.setBottomComponent(bottomToolBar);
		
		
		
		// ************* Refresh time
		
		Timer t = new Timer(){

			@Override
			public void run() {
				if(autoRefresh.isPressed())loader.load();
			}

		};
		t.scheduleRepeating(2*1000);
	}
	
	AsyncCallback<Response> setImportTypeCallBack=new AsyncCallback<Response>() {
		
		public void onSuccess(Response arg0) {
			instance.unmask();
			if(arg0.getStatus()){
				ImportWizard importWizard = new ImportWizard(Tags.CSV_TARGET, LocalSource.INSTANCE, WorkspaceSource.INSTANCE);				
				importWizard.show();				
				importWizard.addListener(new WizardListener() {

					public void failed(Throwable throwable, String reason, String details) {						
						Log.error("FAILED reason: "+reason+" details: "+details+" throwable: "+throwable);
						Info.display("IMPORT ","FAILED "+reason);
					}

					public void completed() {
						Log.debug("COMPLETED");
						Info.display("IMPORT ","Completed");
						loader.load();
					}

					public void aborted() {
						Log.debug("ABORT");
						Info.display("IMPORT ","Completed");
					}
				});				
			}else{
				String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
				String v = Format.ellipse(message, 80);
				Info.display("Set Import Type ","Error : {0}", new Params(v));
			}

		}

		public void onFailure(Throwable arg0) {
			Log.error("Unexpected error while submitting request", arg0);
			String message="Please contact support";
			String v = Format.ellipse(message, 80);
			Info.display("Set Import Type ","Error : {0}", new Params(v));
			instance.unmask();
		}
	};
	
}
