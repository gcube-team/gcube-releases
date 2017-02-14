package org.gcube.portlets.user.speciesdiscovery.client.job.occurrence;

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
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
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
public class OccurrenceGridJob extends ContentPanel{
	

	private static final String REFRESH_LIST = "Refresh List";
	private static final String RE_SUBMIT = "Re-submit";
	private static final String SAVE = "Save";
	private static final String SAVEERROR = "Save Errors";
	private static final String CANCEL = "Cancel";
	private static final String INFO = "Info";
	//	public static final String DATE_TIME_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss";
	//	private ListStore<FileModel> store =  ListStoreModel.getInstance().getStore();
	private ListStore<BaseModelData> store;
	private ToolBar toolBar = new ToolBar();
	private Grid<BaseModelData> grid;
	private HashMap<String,OccurrenceWindowInfoJobsSpecies> listWindowInfo = new HashMap<String, OccurrenceWindowInfoJobsSpecies>();
	private EventBus eventBus;

	private Button buttonInfo;
	private Button buttonCancel;
	private Button buttonSave;
	private Button buttonSaveError;
	private Button buttonReSubmit;
	private Button buttonRefreshList;
	private Menu menu = new Menu();
	
	public OccurrenceGridJob(EventBus eventBus) {
		
		this.eventBus = eventBus;
		
		ColumnConfig name = new ColumnConfig(JobOccurrencesModel.JOBNAME, JobOccurrencesModel.JOBNAME, 220);
		ColumnConfig dataSources = new ColumnConfig(JobOccurrencesModel.DATASOURCESASSTRING, JobOccurrencesModel.DATASOURCESASSTRING, 140);
		ColumnConfig progress = new ColumnConfig(JobOccurrencesModel.PROGRESS, JobOccurrencesModel.PROGRESS, 150);
		
		ColumnConfig startDate = new ColumnConfig(JobOccurrencesModel.STARTTIME, JobOccurrencesModel.STARTTIME, 140);
		
		ColumnConfig submitDate = new ColumnConfig(JobOccurrencesModel.SUBMITTIME, JobOccurrencesModel.SUBMITTIME, 140);
//		creationDate.setDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FORMAT));
		
		ColumnConfig endTime = new ColumnConfig(JobOccurrencesModel.ENDTIME, JobOccurrencesModel.ENDTIME, 140);
//		endTime.setDateTimeFormat(DateTimeFormat.getFormat(DATE_TIME_FORMAT));
		
		ColumnConfig elapsedTime = new ColumnConfig(JobOccurrencesModel.ELAPSEDTIME, JobOccurrencesModel.ELAPSEDTIME + " Days, " + ConstantsSpeciesDiscovery.TIMEFORMAT_HH_MM_SS, 100);
//		elapsedTime.setHidden(true);
		
		ColumnConfig fileFormat = new ColumnConfig(JobOccurrencesModel.FILEFORMAT, JobOccurrencesModel.FILEFORMAT, 100);
		
		ColumnConfig csvType = new ColumnConfig(JobOccurrencesModel.CSVTYPE, JobOccurrencesModel.CSVTYPE, 100);
		
		ColumnConfig status = new ColumnConfig(JobOccurrencesModel.STATUS, JobOccurrencesModel.STATUS, 80);
		
		ColumnConfig itemsCount = new ColumnConfig(JobOccurrencesModel.ITEMSNUMBER, JobOccurrencesModel.ITEMSNUMBER, 95);
		
		final ColumnModel cm = new ColumnModel(Arrays.asList(name, dataSources, itemsCount, progress, submitDate, startDate, endTime, elapsedTime, fileFormat, csvType, status));
		
		setBodyBorder(false);
		setHeaderVisible(false);
		setLayout(new FitLayout());
		
		GridFilters filters = new GridFilters();
		filters.setLocal(true);
		
		DateFilter dateFilterStartDate = new DateFilter(JobOccurrencesModel.STARTTIME);
		DateFilter dateFilterSubmitDate = new DateFilter(JobOccurrencesModel.SUBMITTIME);
		DateFilter dateFilterEnd = new DateFilter(JobOccurrencesModel.ENDTIME);
		
		filters.addFilter(dateFilterStartDate);
		filters.addFilter(dateFilterSubmitDate);
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
	                        && be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof OccurrenceJobSpeciesProgressBar) {  
	                      ((OccurrenceJobSpeciesProgressBar) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);  
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
		
//		grid.setAutoExpandColumn(JobOccurrencesModel.JOBNAME);
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

	public void addJobIntoGrid(JobOccurrencesModel jobModel, OccurrenceJobSpeciesProgressBar jobProgressBar) {
		
		BaseModelData baseModelData = new BaseModelData();
		
		baseModelData.set(JobOccurrencesModel.JOBINDENTIFIER, jobModel.getJobIdentifier());
		baseModelData.set(JobOccurrencesModel.JOBNAME, jobModel.getJobName());
		baseModelData.set(JobOccurrencesModel.PROGRESS, jobProgressBar);
		baseModelData.set(JobOccurrencesModel.STATUS, jobModel.getDownloadState().toString());
		
		baseModelData.set(JobOccurrencesModel.SUBMITTIME, jobModel.getSubmitTime());
		baseModelData.set(JobOccurrencesModel.ENDTIME, jobModel.getEndTime());
		
		baseModelData.set(JobOccurrencesModel.FILEFORMAT, jobModel.getFileFormat());
		baseModelData.set(JobOccurrencesModel.CSVTYPE, jobModel.getCsvType());
		
		baseModelData.set(JobOccurrencesModel.STARTTIME, jobModel.getStartTime());
		
		baseModelData.set(JobOccurrencesModel.ELAPSEDTIME, jobModel.getElapsedTime());
		
		baseModelData.set(JobOccurrencesModel.COMPLETEDENTRY, jobModel.getNodeCompleted());
		
		baseModelData.set(JobOccurrencesModel.ITEMSNUMBER, jobModel.getTotalOccurrences());
		
		String dataSources = "";
		for (DataSource dataSource : jobModel.getDataSources()) {
			dataSources+= " "+dataSource.getName() + ",";
		}
		
		//Trim
		dataSources = dataSources.trim();
		
		//Remove last char ,
		dataSources = dataSources.substring(0, dataSources.lastIndexOf(","));
		
		baseModelData.set(JobOccurrencesModel.DATASOURCESASSTRING, dataSources);
		baseModelData.set(JobOccurrencesModel.SCIENTIFICNAME, jobModel.getScientificName());

		baseModelData.set(JobOccurrencesModel.DESCRIPTION, jobModel.getDescription());
		
		baseModelData.set(jobModel.getJobIdentifier(), jobModel); //store object JobOccurrencesModel

		store.add(baseModelData);
		updateJobInfo(jobModel);
	}
	
	private void updateJobInfo(JobOccurrencesModel jobModel){
		
		OccurrenceWindowInfoJobsSpecies win=listWindowInfo.get(jobModel.getJobIdentifier());

		if(win==null){
			win = new OccurrenceWindowInfoJobsSpecies();
			win.setWindowTitle("Job " + jobModel.getJobName() + " description:");
		}
		
		win.updateDescription(jobModel);
			
		listWindowInfo.put(jobModel.getJobIdentifier(), win);
	}
	
	public void updateStatus(JobOccurrencesModel jobModel, OccurrenceJobSpeciesProgressBar jobProgressBar){
		
		BaseModelData job  = store.findModel(JobOccurrencesModel.JOBINDENTIFIER, jobModel.getJobIdentifier());
		
		if(job!=null){
			
			job.set(JobOccurrencesModel.PROGRESS, jobProgressBar);
			job.set(JobOccurrencesModel.SUBMITTIME, jobModel.getSubmitTime());
			job.set(JobOccurrencesModel.STATUS, jobModel.getDownloadState().toString());
			job.set(JobOccurrencesModel.ENDTIME, jobModel.getEndTime());
			job.set(JobOccurrencesModel.ELAPSEDTIME, jobModel.getElapsedTime());
			job.set(JobOccurrencesModel.STARTTIME, jobModel.getStartTime());
			
			updateJobInfo(jobModel);
			
			store.update(job);
		}
		
	}

	
	public void removeSpeciesJobByIdentifier(String jobIdentifier) {
		BaseModelData job = store.findModel(JobOccurrencesModel.JOBINDENTIFIER, jobIdentifier);
		if(job!=null){
			store.remove(job);
		}
	}
	
	private void viewInfoJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
			
			OccurrenceWindowInfoJobsSpecies win = listWindowInfo.get(baseModelData.get(JobOccurrencesModel.JOBINDENTIFIER));
			win.show();
		}
	}
	
	private void cancelJob() {
		
		final BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String name = baseModelData.get(JobOccurrencesModel.JOBNAME);
					
				MessageBoxConfirm mbc = new MessageBoxConfirm(ConstantsSpeciesDiscovery.CONFIRM_DELETE, ConstantsSpeciesDiscovery.MESSAGE_CONFIRM_DELETE_JOB  + " "+ name+"?");
				mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {
			          
				public void handleEvent(MessageBoxEvent be) {
					
					//IF CANCELLED
					String clickedButton = be.getButtonClicked().getItemId();
					if(clickedButton.equals(Dialog.YES)){
						
						final String jobIdentifier = baseModelData.get(JobOccurrencesModel.JOBINDENTIFIER);
						
						SpeciesDiscovery.taxonomySearchService.cancelOccurrenceJob(jobIdentifier, new AsyncCallback<Boolean>() {
							
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
		
			String load = baseModelData.get(JobOccurrencesModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 ){
				
				String jobIdentifier = baseModelData.get(JobOccurrencesModel.JOBINDENTIFIER);
				String scientificName = baseModelData.get(JobOccurrencesModel.SCIENTIFICNAME);
				
				JobOccurrencesModel jobModel = baseModelData.get(jobIdentifier);
				
				List<DataSource> dataSourceList = jobModel.getDataSources();
				
				eventBus.fireEvent(new SaveJobEvent(jobIdentifier, scientificName, dataSourceList, SearchResultType.OCCURRENCE_POINT, jobModel));
			}
			
			else{
				String name = baseModelData.get(JobOccurrencesModel.JOBNAME);
				MessageBox.alert(ConstantsSpeciesDiscovery.ALERT, "The job "+ name+ " " +ConstantsSpeciesDiscovery.IS_NOT_COMPLETED, null);    
			}
		}
		
	}
	
	private void saveErrorJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String load = baseModelData.get(JobOccurrencesModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 || load.compareTo(DownloadState.FAILED.toString())==0){
				
				String jobIdentifier = baseModelData.get(JobOccurrencesModel.JOBINDENTIFIER);
				String scientificName = baseModelData.get(JobOccurrencesModel.SCIENTIFICNAME);
				
				JobOccurrencesModel jobModel = baseModelData.get(jobIdentifier);
				
				List<DataSource> dataSourceList = jobModel.getDataSources();
				
				eventBus.fireEvent(new SaveJobErrorEvent(jobIdentifier, scientificName, dataSourceList, SearchResultType.OCCURRENCE_POINT, jobModel));
			}
			
			else{
				String name = baseModelData.get(JobOccurrencesModel.JOBNAME);
				MessageBox.alert(ConstantsSpeciesDiscovery.ALERT, "The error file "+ name+ " " +ConstantsSpeciesDiscovery.IS_NOT_COMPLETED, null);    
			}
		}
		
	}
	
	
	private void resubmitJob() {
		
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();
		
		if(baseModelData!=null){
		
			String load = baseModelData.get(JobTaxonomyModel.STATUS).toString();
			
			if(load.compareTo(DownloadState.FAILED.toString())==0 || load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 ){
				
				String jobIdentifier = baseModelData.get(JobOccurrencesModel.JOBINDENTIFIER);
				
				JobOccurrencesModel jobModel = (JobOccurrencesModel) baseModelData.get(jobIdentifier); //get object JobOccurrencesModel

				eventBus.fireEvent(new ReSubmitJobEvent(SearchResultType.OCCURRENCE_POINT, jobModel, null));
			}
			
			else{
				String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
				MessageBox.alert(INFO, "The job "+ name+ " " + ConstantsSpeciesDiscovery.CAN_NOT_BE_RE_SUBMITTED_UNTIL_IT_HAS_COMPLETED, null);    
			}
		}
		
	}
	
	private void createGridToolBar(){

		buttonInfo = new Button(INFO);
		buttonInfo.setScale(ButtonScale.MEDIUM);
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
		buttonSave.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				saveJob();
			}
		});
		
		
		buttonSaveError =  new Button(SAVEERROR);
		buttonSaveError.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		buttonSaveError.setScale(ButtonScale.MEDIUM);
		buttonSaveError.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				saveErrorJob();
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
				eventBus.fireEvent(new ReLoadListJobEvent(SearchResultType.OCCURRENCE_POINT));
				
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
	
	public void enableToolBar(boolean bool){
		this.toolBar.setEnabled(bool);
	}
	
	public void resetStore(){
		store.removeAll();
	}
	
	public void enableButtonOnClick(boolean bool){
		this.buttonCancel.setEnabled(bool);
		this.buttonInfo.setEnabled(bool);
		this.buttonSave.setEnabled(bool);
		this.buttonReSubmit.setEnabled(bool);
		this.buttonSaveError.setEnabled(bool);
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
		menuSaveError.setText(SAVEERROR);  
		menuSaveError.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		
		menuSaveError.addSelectionListener(new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {
				saveErrorJob();
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
