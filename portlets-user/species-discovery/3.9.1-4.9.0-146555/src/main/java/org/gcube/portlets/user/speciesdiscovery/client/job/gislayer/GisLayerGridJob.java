package org.gcube.portlets.user.speciesdiscovery.client.job.gislayer;

import java.util.Arrays;
import java.util.HashMap;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReLoadListJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ReSubmitJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SaveJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.window.MessageBoxConfirm;
import org.gcube.portlets.user.speciesdiscovery.shared.DownloadState;
import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;
//import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
//import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


/**
 * The Class GisLayerGridJob.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2017
 */
public class GisLayerGridJob extends ContentPanel{


	private static final String REFRESH_LIST = "Refresh List";
	private static final String RE_SUBMIT = "Re-submit";
	private static final String SAVE = "Save";
	private static final String CANCEL = "Cancel";
	private static final String INFO = "Info";
	private static final String OPEN_GIS_LAYER = "Open with Gis Viewer App";
	private ListStore<BaseModelData> store;
	private ToolBar toolBar = new ToolBar();
	private Grid<BaseModelData> grid;
	private HashMap<String,GisLayerWindowInfoJobsSpecies> listWindowInfo = new HashMap<String, GisLayerWindowInfoJobsSpecies>();
	private EventBus eventBus;

	private Button buttonInfo;
	private Button buttonCancel;
	private Button buttonSave;
	private Button buttonReSubmit;
	private Button buttonOpenGisViewerApp;
	private Button buttonRefreshList;
	private Menu menu = new Menu();

	/**
	 * Instantiates a new gis layer grid job.
	 *
	 * @param eventBus the event bus
	 */
	public GisLayerGridJob(EventBus eventBus) {

		this.eventBus = eventBus;

		ColumnConfig name = new ColumnConfig(JobGisLayerModel.JOBNAME, JobGisLayerModel.JOBNAME, 220);
		ColumnConfig progress = new ColumnConfig(JobGisLayerModel.PROGRESS, JobGisLayerModel.PROGRESS, 150);
		ColumnConfig startDate = new ColumnConfig(JobGisLayerModel.STARTTIME, JobGisLayerModel.STARTTIME, 140);
		ColumnConfig endTime = new ColumnConfig(JobGisLayerModel.ENDTIME, JobGisLayerModel.ENDTIME, 140);
		ColumnConfig layerUUID = new ColumnConfig(JobGisLayerModel.LAYERUUID, JobGisLayerModel.LAYERUUID, 140);
		ColumnConfig gisViewerAppLink = new ColumnConfig(JobGisLayerModel.GISVIEWERAPPLINK, JobGisLayerModel.GISVIEWERAPPLINK, 140);
		ColumnConfig status = new ColumnConfig(JobGisLayerModel.STATUS, JobGisLayerModel.STATUS, 80);
		//final ColumnModel cm = new ColumnModel(Arrays.asList(name, progress, status, layerUUID, gisViewerAppLink, startDate, endTime));
		final ColumnModel cm = new ColumnModel(Arrays.asList(name, progress, status, layerUUID, gisViewerAppLink, startDate, endTime));

		setBodyBorder(false);
		setHeaderVisible(false);
		setLayout(new FitLayout());

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		DateFilter dateFilterStartDate = new DateFilter(JobGisLayerModel.STARTTIME);
		DateFilter dateFilterSubmitDate = new DateFilter(JobGisLayerModel.SUBMITTIME);
		DateFilter dateFilterEnd = new DateFilter(JobGisLayerModel.ENDTIME);

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
	                        && be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof GisLayerJobSpeciesProgressBar) {
	                      ((GisLayerJobSpeciesProgressBar) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);
	                    }
	                  }
	                }
	              });

	            return model.get(property);
	          }
	        };

	    progress.setRenderer(jobSpeciesRenderer);
	    status.setRenderer(downloadStateRenderer);

		grid.getView().setEmptyText("Empty");
		grid.getView().setShowDirtyCells(false);
		grid.getView().setShowInvalidCells(false);
		grid.setBorders(false);
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


	/**
	 * Adds the job into grid.
	 *
	 * @param jobModel the job model
	 * @param jobProgressBar the job progress bar
	 */
	public void addJobIntoGrid(JobGisLayerModel jobModel, GisLayerJobSpeciesProgressBar jobProgressBar) {

		BaseModelData baseModelData = new BaseModelData();

		baseModelData.set(JobGisLayerModel.JOBINDENTIFIER, jobModel.getJobIdentifier());
		baseModelData.set(JobGisLayerModel.JOBNAME, jobModel.getJobName());
		baseModelData.set(JobGisLayerModel.PROGRESS, jobProgressBar);
		baseModelData.set(JobGisLayerModel.STATUS, jobModel.getDownloadState().toString());
		baseModelData.set(JobGisLayerModel.SUBMITTIME, jobModel.getSubmitTime());
		baseModelData.set(JobGisLayerModel.ENDTIME, jobModel.getEndTime());
		baseModelData.set(JobGisLayerModel.STARTTIME, jobModel.getStartTime());
		baseModelData.set(JobGisLayerModel.ELAPSEDTIME, jobModel.getElapsedTime());
		baseModelData.set(JobGisLayerModel.GISVIEWERAPPLINK, jobModel.getGisViewerAppLink());
		baseModelData.set(JobGisLayerModel.LAYERUUID, jobModel.getLayerUUID());
		baseModelData.set(jobModel.getJobIdentifier(), jobModel); //store object JobOccurrencesModel

		store.add(baseModelData);
		updateJobInfo(jobModel);
	}

	/**
	 * Update job info.
	 *
	 * @param jobModel the job model
	 */
	private void updateJobInfo(JobGisLayerModel jobModel){

		GisLayerWindowInfoJobsSpecies win=listWindowInfo.get(jobModel.getJobIdentifier());

		if(win==null){
			win = new GisLayerWindowInfoJobsSpecies();
			win.setWindowTitle("Job " + jobModel.getJobName() + " description:");
		}

		win.updateDescription(jobModel);

		listWindowInfo.put(jobModel.getJobIdentifier(), win);
	}

	/**
	 * Update status.
	 *
	 * @param jobModel the job model
	 * @param jobProgressBar the job progress bar
	 */
	public void updateStatus(JobGisLayerModel jobModel, GisLayerJobSpeciesProgressBar jobProgressBar){

		BaseModelData job  = store.findModel(JobGisLayerModel.JOBINDENTIFIER, jobModel.getJobIdentifier());

		if(job!=null){

			job.set(JobGisLayerModel.PROGRESS, jobProgressBar);
			job.set(JobGisLayerModel.SUBMITTIME, jobModel.getSubmitTime());
			job.set(JobGisLayerModel.STATUS, jobModel.getDownloadState().toString());
			job.set(JobGisLayerModel.ENDTIME, jobModel.getEndTime());
			job.set(JobGisLayerModel.ELAPSEDTIME, jobModel.getElapsedTime());
			job.set(JobGisLayerModel.STARTTIME, jobModel.getStartTime());
			job.set(JobGisLayerModel.GISVIEWERAPPLINK, jobModel.getGisViewerAppLink());
			job.set(JobGisLayerModel.LAYERUUID, jobModel.getLayerUUID());

			updateJobInfo(jobModel);

			store.update(job);
		}

	}


	/**
	 * Removes the gis layer job by identifier.
	 *
	 * @param jobIdentifier the job identifier
	 */
	public void removeGisLayerJobByIdentifier(String jobIdentifier) {
		BaseModelData job = store.findModel(JobGisLayerModel.JOBINDENTIFIER, jobIdentifier);
		if(job!=null){
			store.remove(job);
		}
	}

	/**
	 * View info job.
	 */
	private void viewInfoJob() {

		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();

		if(baseModelData!=null){

			GisLayerWindowInfoJobsSpecies win = listWindowInfo.get(baseModelData.get(JobGisLayerModel.JOBINDENTIFIER));
			win.show();
		}
	}

	/**
	 * Cancel job.
	 */
	private void cancelJob() {

		final BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();

		if(baseModelData!=null){

			String name = baseModelData.get(JobGisLayerModel.JOBNAME);

				MessageBoxConfirm mbc = new MessageBoxConfirm(ConstantsSpeciesDiscovery.CONFIRM_DELETE, ConstantsSpeciesDiscovery.MESSAGE_CONFIRM_DELETE_JOB  + " "+ name+"?");
				mbc.getMessageBoxConfirm().addCallback(new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {

					//IF CANCELLED
					String clickedButton = be.getButtonClicked().getItemId();
					if(clickedButton.equals(Dialog.YES)){

						final String jobIdentifier = baseModelData.get(JobGisLayerModel.JOBINDENTIFIER);

						SpeciesDiscovery.taxonomySearchService.cancelGisLayerJob(jobIdentifier, new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {

								if(result)
									removeGisLayerJobByIdentifier(jobIdentifier);

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

	/**
	 * Save job.
	 */
	private void saveJob() {
		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();

		if(baseModelData!=null){

			String load = baseModelData.get(JobGisLayerModel.STATUS).toString();

			if(load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 ){

				String jobIdentifier = baseModelData.get(JobGisLayerModel.JOBINDENTIFIER);
				JobGisLayerModel jobModel = baseModelData.get(jobIdentifier);
				eventBus.fireEvent(new SaveJobEvent(jobIdentifier, jobModel.getJobName(), SearchResultType.GIS_LAYER_POINT, jobModel));
			}

			else{
				String name = baseModelData.get(JobGisLayerModel.JOBNAME);
				MessageBox.alert(ConstantsSpeciesDiscovery.ALERT, "The job "+ name+ " " +ConstantsSpeciesDiscovery.IS_NOT_COMPLETED, null);
			}
		}

	}

	/**
	 * Resubmit job.
	 */
	private void resubmitJob() {

		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();

		if(baseModelData!=null){

			String load = baseModelData.get(JobTaxonomyModel.STATUS).toString();

			if(load.compareTo(DownloadState.FAILED.toString())==0 || load.compareTo(DownloadState.COMPLETED.toString())==0 || load.compareTo(DownloadState.SAVED.toString())==0 ){

				String jobIdentifier = baseModelData.get(JobGisLayerModel.JOBINDENTIFIER);

				JobGisLayerModel jobModel = (JobGisLayerModel) baseModelData.get(jobIdentifier); //get object JobOccurrencesModel

				eventBus.fireEvent(new ReSubmitJobEvent(SearchResultType.GIS_LAYER_POINT, null, null, jobModel));
			}

			else{
				String name = baseModelData.get(JobTaxonomyModel.JOBNAME);
				MessageBox.alert(INFO, "The job "+ name+ " " + ConstantsSpeciesDiscovery.CAN_NOT_BE_RE_SUBMITTED_UNTIL_IT_HAS_COMPLETED, null);
			}
		}

	}

	/**
	 * Creates the grid tool bar.
	 */
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
				eventBus.fireEvent(new ReLoadListJobEvent(SearchResultType.GIS_LAYER_POINT));

			}
		});


		buttonOpenGisViewerApp =  new Button(OPEN_GIS_LAYER);
		buttonOpenGisViewerApp.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getGlobe16()));
		buttonOpenGisViewerApp.setScale(ButtonScale.MEDIUM);
		buttonOpenGisViewerApp.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				openLayerSelectedWithGisViewerApp();

			}
		});

		toolBar.add(buttonInfo);
		toolBar.add(new SeparatorToolItem());

		toolBar.add(buttonSave);
		toolBar.add(new SeparatorToolItem());

		toolBar.add(buttonCancel);
		toolBar.add(new SeparatorToolItem());

		//toolBar.add(buttonReSubmit);
		//toolBar.add(new SeparatorToolItem());

		toolBar.add(buttonOpenGisViewerApp);
		toolBar.add(new SeparatorToolItem());

		toolBar.add(new FillToolItem());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(buttonRefreshList);

		setTopComponent(toolBar);

		enableButtonOnClick(false);
	}



	/**
	 * Open layer selected with gis viewer app.
	 */
	private void openLayerSelectedWithGisViewerApp(){

		BaseModelData baseModelData = grid.getSelectionModel().getSelectedItem();

		if(baseModelData!=null){
			String url = baseModelData.get(JobGisLayerModel.GISVIEWERAPPLINK);
			if(url==null){
				Window.alert("The task '" +baseModelData.get(JobGisLayerModel.JOBNAME) +"' does not contain a valid GisViewer App link, Is it completed?");
				return;
			}
			Window.open(url, "", "");
		}
	}

	/**
	 * Enable tool bar.
	 *
	 * @param bool the bool
	 */
	public void enableToolBar(boolean bool){
		this.toolBar.setEnabled(bool);
	}

	/**
	 * Reset store.
	 */
	public void resetStore(){
		store.removeAll();
	}

	/**
	 * Enable button on click.
	 *
	 * @param bool the bool
	 */
	public void enableButtonOnClick(boolean bool){
		this.buttonCancel.setEnabled(bool);
		this.buttonInfo.setEnabled(bool);
		this.buttonSave.setEnabled(bool);
		this.buttonReSubmit.setEnabled(bool);
	}

	/**
	 * Creates the menu items on grid.
	 */
	public void createMenuItemsOnGrid(){

		MenuItem menuOpenWithGis = new MenuItem();
		menuOpenWithGis.setText(OPEN_GIS_LAYER);
		menuOpenWithGis.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getGlobe16()));

		menuOpenWithGis.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				openLayerSelectedWithGisViewerApp();
			}

		});

		menu.add(menuOpenWithGis);

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

		//menu.add(menuResubmit);
		grid.setContextMenu(menu);
	}

}
