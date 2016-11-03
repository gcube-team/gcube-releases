package org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReLoadListJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReSubmitJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobErrorEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.window.MessageBoxConfirm;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TaxonomyGridJob extends ContentPanel{
	

	/**
	 * 
	 */
	private static final String SAVE_ERROR = "Save Errors";
	private static final String REFRESH_LIST = "Refresh List";
	private static final String RE_SUBMIT = "Re-submit";
	private static final String SAVE = "Save";
	private static final String CANCEL = "Cancel";
	private static final String INFO = "Info";
	//	public static final String DATE_TIME_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss";
	//	private ListStore<FileModel> store =  ListStoreModel.getInstance().getStore();
	private ListStore<BaseModelData> store;
	private ToolBar toolBar = new ToolBar();
	private Grid<BaseModelData> grid;
	private HashMap<String,TaxonomyWindowInfoJobsSpecies> listWindowInfo = new HashMap<String, TaxonomyWindowInfoJobsSpecies>();
	private EventBus eventBus;

	private Button buttonInfo;
	private Button buttonCancel;
	private Button buttonSave;
	private Button buttonReSubmit;
	private Button buttonRefreshList;
	private Button buttonSaveError;
	
	private Menu menu = new Menu();

	
	public TaxonomyGridJob(EventBus eventBus) {
		
		this.eventBus = eventBus;
		
		ColumnConfig name = new ColumnConfig(JobTaxonomyModel.JOBNAME, JobTaxonomyModel.JOBNAME, 220);
		ColumnConfig dataSources = new ColumnConfig(JobTaxonomyModel.DATASOURCE, JobTaxonomyModel.DATASOURCE, 90);
		ColumnConfig progress = new ColumnConfig(JobTaxonomyModel.PROGRESS, JobTaxonomyModel.PROGRESS, 250);
		
		ColumnConfig startDate = new ColumnConfig(JobTaxonomyModel.STARTTIME, JobTaxonomyModel.STARTTIME, 140);
		
		ColumnConfig submitDate = new ColumnConfig(JobTaxonomyModel.SUBMITTIME, JobTaxonomyModel.SUBMITTIME, 140);
//		creationDate.setDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FORMAT));
		
		ColumnConfig endTime = new ColumnConfig(JobTaxonomyModel.ENDTIME, JobTaxonomyModel.ENDTIME, 140);
//		endTime.setDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FORMAT));
		
		ColumnConfig elapsedTime = new ColumnConfig(JobTaxonomyModel.ELAPSEDTIME, JobTaxonomyModel.ELAPSEDTIME + " Days, " + ConstantsSpeciesDiscovery.TIMEFORMAT_HH_MM_SS, 140);
		ColumnConfig status = new ColumnConfig(JobTaxonomyModel.STATUS, JobTaxonomyModel.STATUS, 80);
			
		final ColumnModel cm = new ColumnModel(Arrays.asList(name, dataSources, progress, submitDate, startDate, endTime, elapsedTime, status));
		
		setBodyBorder(false);
		setHeaderVisible(false);
		setLayout(new FitLayout());
		
		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		DateFilter dateFilterStart = new DateFilter(JobTaxonomyModel.STARTTIME);
		DateFilter dateFilterSubmit = new DateFilter(JobTaxonomyModel.SUBMITTIME);
		DateFilter dateFilterEnd = new DateFilter(JobTaxonomyModel.ENDTIME);
	
		filters.addFilter(dateFilterStart);
		filters.addFilter(dateFilterSubmit);
		filters.addFilter(dateFilterEnd);

		store = new ListStore<BaseModelData>();
		
		grid = new Grid<BaseModelData>(store, cm);
		grid.setLoadMask(true); 
		
	    GridCellRenderer<BaseModelData> downloadStateRenderer = new GridCellRenderer<BaseModelData>() {  
			@Override
			public String render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
		          String val = model.get(property).toString();  
		          String color="black";
		          
//		          ONGOING, ONGOINGWITHFAILURES, FAILED, COMPLETED, COMPLETEDWITHFAILURES
		          
		          if(val!=null){
		        		  
//		        		  if(val.compareTo(DownloadState.COMPLETED.toString())==0){
//		        			  color ="#0000EE";
//		        		  }else 
		        		  if(val.compareTo(DownloadState.SAVING.toString())==0){
		        			  color = "#7093DB";
				          }else if(val.compareTo(DownloadState.FAILED.toString())==0){
				        	  color = "#f00";
				          }
		          }
		          else
		        	  val = "";
		          
		          return "<span qtitle='" + cm.getColumnById(property).getHeader() + "' qtip='" + val  + "' style='color:" + color + "'>" + val + "</span>";
		          
			}  
	      };
	      
	      GridCellRenderer<BaseModelData> jobSpeciesRenderer = new GridCellRenderer<BaseModelData>() {  
	    	  
	          public Object render(final BaseModelData model, String property, ColumnData config, final int rowIndex,  
	              final int colIndex, ListStore<BaseModelData> store, Grid<BaseModelData> grid) {  
	   
	              grid.addListener(Events.ColumnResize, new Listener<GridEvent<BaseModelData>>() {  
	      
	                public void handleEvent(GridEvent<BaseModelData> be) {  
	                	
	                  for (int i = 0; i < be.getGrid().getStore().getCount(); i++) {  
	                    if (be.getGrid().getView().getWidget(i, be.getColIndex()) != null  
	                        && be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof TaxonomyJobSpeciesProgressBar) {  
	                      ((TaxonomyJobSpeciesProgressBar) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);  
	                    }  
	                  }  
	                }  
	              });  
	            
	            return model.get(property);  
	          }  
	        }; 
	        
	    progress.setRenderer(jobSpeciesRenderer);
	    status.setRenderer(downloadStateRenderer);
	    
	    
//		grid.getView().setAutoFill(true);
		
		grid.getView().setEmptyText("Empty");
		
		grid.getView().setShowDirtyCells(false);
		grid.getView().setShowInvalidCells(false);
	
//		setAlphanumericStoreSorter(grid);
		
//		grid.setAutoExpandColumn(JobTaxonomyModel.JOBNAME);
		grid.setBorders(false);
//		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.addPlugin(filters);
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BaseModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModelData> se) {
//				System.out.println("selection grid change");
				
				ModelData target = se.getSelectedItem();
				
				if(target!=null)
					enableButtonOnClick(true);
				else
					enableButtonOnClick(false);
				
			}
		});
		
		add(grid);
		
		createGridToolBar();
		createMenuItemsOnGrid();
	}

	public void addJobIntoGrid(JobTaxonomyModel jobModel, TaxonomyJobSpeciesProgressBar jobProgressBar) {
		
		BaseModelData baseModelData = new BaseModelData();
		
		baseModelData.set(JobTaxonomyModel.JOBINDENTIFIER, jobModel.getIdentifier());
		baseModelData.set(JobTaxonomyModel.JOBNAME, jobModel.getName());
		baseModelData.set(JobTaxonomyModel.PROGRESS, jobProgressBar);
		baseModelData.set(JobTaxonomyModel.STATUS, jobModel.getDownloadState().toString());

		baseModelData.set(JobTaxonomyModel.SUBMITTIME, jobModel.getSubmitTime());
		baseModelData.set(JobTaxonomyModel.ENDTIME, jobModel.getEndTime());
		
		baseModelData.set(JobTaxonomyModel.STARTTIME, jobModel.getStartTime());
		baseModelData.set(JobTaxonomyModel.ELAPSEDTIME, jobModel.getElapsedTime());
		
		baseModelData.set(JobTaxonomyModel.DATASOURCE, jobModel.getDataSource());
		baseModelData.set(JobTaxonomyModel.SCIENTIFICNAME, jobModel.getScientificName());
		baseModelData.set(JobTaxonomyModel.RANK, jobModel.getRank());
		
		baseModelData.set(jobModel.getIdentifier(), jobModel);
		
		store.add(baseModelData);
		
		updateJobInfo(jobModel);
		
	}
	
	
	/*
	private void updateStringToDate(BaseModelData baseModel, String property, String dateValue){
		
		Date date = null;
		try {
			if(dateValue!=null && !dateValue.isEmpty())
				date = DateTimeFormat.getFormat(DATE_TIME_FORMAT).parse(dateValue);
			
		} catch (Exception e) {
			Log.trace("error in update date "+dateValue);
			Log.error(e.getMessage());
		}
		
		baseModel.set(property, date);
	}
	*/
	
	private void updateJobInfo(JobTaxonomyModel jobModel){
		
		TaxonomyWindowInfoJobsSpecies win=listWindowInfo.get(jobModel.getIdentifier());

		if(win==null)
			win = new TaxonomyWindowInfoJobsSpecies();
		
		win.setWindowTitle("Job " + jobModel.getName() + " Status");
		win.updateListStore(jobModel);
			
		listWindowInfo.put(jobModel.getIdentifier(), win);
	}
	
	public void updateStatus(JobTaxonomyModel jobModel, TaxonomyJobSpeciesProgressBar jobProgressBar){
		
		BaseModelData job = store.findModel(JobTaxonomyModel.JOBINDENTIFIER, jobModel.getIdentifier());
		
		if(job!=null){
			
			job.set(JobTaxonomyModel.PROGRESS, jobProgressBar);
			
//			updateStringToDate(job, JobSpeciesModel.STARTTIME, jobModel.getStartTime());
//			updateStringToDate(job, JobSpeciesModel.ENDTIME, jobModel.getEndTime());
			
			job.set(JobTaxonomyModel.SUBMITTIME, jobModel.getSubmitTime());
			job.set(JobTaxonomyModel.STATUS, jobModel.getDownloadState().toString());
			job.set(JobTaxonomyModel.ENDTIME, jobModel.getEndTime());
			job.set(JobTaxonomyModel.ELAPSEDTIME, jobModel.getElapsedTime());
			job.set(JobTaxonomyModel.STARTTIME, jobModel.getStartTime());
			job.set(jobModel.getIdentifier(), jobModel); //Store jobModel
			
			updateJobInfo(jobModel);
			
			store.update(job);
			
//			this.layout();
		}
		
	}

	
	public void removeSpeciesJobByIdentifier(String jobIdentifier) {
		BaseModelData job = store.findModel(JobTaxonomyModel.JOBINDENTIFIER, jobIdentifier);
		if(job!=null){
			store.remove(job);
//			this.layout();
		}
	}
	
	private void viewInfoJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
			
			TaxonomyWindowInfoJobsSpecies win = listWindowInfo.get(baseModelData.get(JobTaxonomyModel.JOBINDENTIFIER));
			win.show();
		}
		
	}
	
	private void cancelJob() {
		
		final BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){

			String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
			
			MessageBoxConfirm mbc = new MessageBoxConfirm(ConstantsSpeciesDiscovery.CONFIRM_DELETE, ConstantsSpeciesDiscovery.MESSAGE_CONFIRM_DELETE_JOB  + " "+ name+"?");
			mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {
		          
			public void handleEvent(MessageBoxEvent be) {
				
				//IF CANCELLED
				String clickedButton = be.getButtonClicked().getItemId();
				if(clickedButton.equals(Dialog.YES)){
					
					final String jobIdentifier = baseModelData.get(JobTaxonomyModel.JOBINDENTIFIER);
					
					SpeciesDiscovery.taxonomySearchService.cancelTaxonomyJob(jobIdentifier, new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							
							if(result)
								removeSpeciesJobByIdentifier(jobIdentifier);
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Info.display("Error", "An error occurred during the cancel");
							Log.error("Error during the cancel", caught.getMessage());
							
						}
					});

				}
			}	
			});	 

		}
		
	}
	
	private void saveJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String load = baseModelData.get(JobTaxonomyModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 ){
				
				String jobIdentifier = baseModelData.get(JobTaxonomyModel.JOBINDENTIFIER);
				String scientificName = baseModelData.get(JobTaxonomyModel.SCIENTIFICNAME);
				String dataSource = baseModelData.get(JobTaxonomyModel.DATASOURCE);
				String rank = baseModelData.get(JobTaxonomyModel.RANK);
				
				JobTaxonomyModel jobModel = baseModelData.get(jobIdentifier);
				
				List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList = new ArrayList<org.gcube.portlets.user.speciesdiscovery.shared.DataSource>();
				
				dataSourceList.add(new DataSource(dataSource,dataSource));
				
				eventBus.fireEvent(new SaveJobEvent(jobIdentifier, scientificName, dataSourceList, rank, SearchResultType.TAXONOMY_ITEM, jobModel));
			}
			
			else{
				String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
				MessageBox.alert(ConstantsSpeciesDiscovery.ALERT, "The job "+ name+ " " +ConstantsSpeciesDiscovery.IS_NOT_COMPLETED, null);    
			}
		}
		
	}
	
	private void saveJobErrors() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String load = baseModelData.get(JobTaxonomyModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 || load.compareTo(DownloadState.FAILED.toString())==0){
				
				String jobIdentifier = baseModelData.get(JobTaxonomyModel.JOBINDENTIFIER);
				String scientificName = baseModelData.get(JobTaxonomyModel.SCIENTIFICNAME);
				String dataSource = baseModelData.get(JobTaxonomyModel.DATASOURCE);
				String rank = baseModelData.get(JobTaxonomyModel.RANK);
				
				JobTaxonomyModel jobModel = baseModelData.get(jobIdentifier);
				
				List<org.gcube.portlets.user.speciesdiscovery.shared.DataSource> dataSourceList = new ArrayList<org.gcube.portlets.user.speciesdiscovery.shared.DataSource>();
				
				dataSourceList.add(new DataSource(dataSource,dataSource));
				
				eventBus.fireEvent(new SaveJobErrorEvent(jobIdentifier, scientificName, dataSourceList, rank, SearchResultType.TAXONOMY_ITEM, jobModel));
			}
			
			else{
				String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
				MessageBox.alert(ConstantsSpeciesDiscovery.ALERT, "The error file "+ name+ " " +ConstantsSpeciesDiscovery.IS_NOT_COMPLETED, null);    
			}
		}
		
	}
	
	private void resubmitJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String load = baseModelData.get(JobTaxonomyModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.FAILED.toString())==0 || load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0){

				String jobIdentifier = baseModelData.get(JobTaxonomyModel.JOBINDENTIFIER);
				
				JobTaxonomyModel jobModel = (JobTaxonomyModel) baseModelData.get(jobIdentifier); //get object JobOccurrencesModel
				
				eventBus.fireEvent(new ReSubmitJobEvent(SearchResultType.TAXONOMY_ITEM, null, jobModel));
			}
			
			else{
				String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
				MessageBox.alert(INFO, "The job "+ name+" "+ConstantsSpeciesDiscovery.CAN_NOT_BE_RE_SUBMITTED_UNTIL_IT_HAS_COMPLETED, null);   
			}
		}
		
	}
	
	private void createGridToolBar(){
		buttonInfo = new Button(INFO);
		buttonInfo.setScale(ButtonScale.MEDIUM);
//		buttonInfo.setIconAlign(IconAlign.LEFT);
//		buttonInfo.setArrowAlign(ButtonArrowAlign.RIGHT);
		buttonInfo.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getInfoIcon()));
		buttonInfo.setStyleAttribute("margin-left", "5px");
		buttonInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				viewInfoJob();
			}
		});
		
		buttonCancel =  new Button(CANCEL);
		buttonCancel.setScale(ButtonScale.MEDIUM);
//		buttonCancel.setIconAlign(IconAlign.TOP);
//		buttonCancel.setArrowAlign(ButtonArrowAlign.BOTTOM);
		buttonCancel.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getDelete()));
		
		buttonCancel.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				cancelJob();
			}
		});
		
		buttonSave =  new Button(SAVE);
		buttonSave.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		buttonSave.setScale(ButtonScale.MEDIUM);
//		buttonSave.setIconAlign(IconAlign.TOP);
//		buttonSave.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		buttonSave.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				saveJob();
			}
		});
		
		buttonSaveError =  new Button(SAVE_ERROR);
		buttonSaveError.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		buttonSaveError.setScale(ButtonScale.MEDIUM);
//		buttonSave.setIconAlign(IconAlign.TOP);
//		buttonSave.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		buttonSaveError.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				saveJobErrors();
			}
		});
		
		buttonReSubmit =  new Button(RE_SUBMIT);
		buttonReSubmit.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getReSubmit()));
		buttonReSubmit.setScale(ButtonScale.MEDIUM);
		buttonReSubmit.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				resubmitJob();
			}
		});
		
		buttonRefreshList =  new Button(REFRESH_LIST);
		buttonRefreshList.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getRefresh()));
		buttonRefreshList.setScale(ButtonScale.MEDIUM);
		buttonRefreshList.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {			
//				resetStore();
				eventBus.fireEvent(new ReLoadListJobEvent(SearchResultType.TAXONOMY_ITEM));
				
			}
		});
		

		toolBar.add(buttonInfo);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(buttonSave);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(buttonSaveError);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(buttonCancel);
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(buttonReSubmit);
		toolBar.add(new SeparatorToolItem());
		
		
		toolBar.add(new FillToolItem());
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(buttonRefreshList);
		
		setTopComponent(toolBar);
		
		enableButtonOnClick(false);
	}
	
	public void enableButtonOnClick(boolean bool){
		this.buttonCancel.setEnabled(bool);
		this.buttonInfo.setEnabled(bool);
		this.buttonSave.setEnabled(bool);
		this.buttonReSubmit.setEnabled(bool);
		this.buttonSaveError.setEnabled(bool);
	}

	public void resetStore(){
		store.removeAll();
	}
	
	public void createMenuItemsOnGrid(){
		
		MenuItem menuInfo = new MenuItem(); 
		menuInfo.setText(INFO);  
		menuInfo.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getInfoIcon()));
		menuInfo.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				viewInfoJob();
			}
		});

		menu.add(menuInfo);
		
		MenuItem menuSave= new MenuItem(); 
		menuSave.setText(SAVE);  
		menuSave.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		menuSave.addSelectionListener(new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {
				saveJob();
			}
		});
		
		menu.add(menuSave);
		
		MenuItem menuSaveError = new MenuItem(); 
		menuSaveError.setText(SAVE_ERROR);  
		menuSaveError.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		menuSaveError.addSelectionListener(new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {
				saveJobErrors();
			}
		});
		
		menu.add(menuSaveError);
		
		MenuItem menuCancel= new MenuItem(); 
		menuCancel.setText(CANCEL);  
		menuCancel.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getDelete()));
		menuCancel.addSelectionListener(new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {

				cancelJob();
			}

		});
		
		menu.add(menuCancel);

		MenuItem menuResubmit= new MenuItem(); 
		menuResubmit.setText(RE_SUBMIT);  
		menuResubmit.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getReSubmit()));
		
		menuResubmit.addSelectionListener(new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {
				resubmitJob();
			}

		});
		
		menu.add(menuResubmit);

		grid.setContextMenu(menu);
		
	}
}
