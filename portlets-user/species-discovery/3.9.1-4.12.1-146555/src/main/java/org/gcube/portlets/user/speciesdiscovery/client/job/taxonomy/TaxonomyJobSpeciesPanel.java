package org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TaxonomyJobSpeciesPanel extends ContentPanel {  
	

	/**
	 * This is a singleton
	 */
	private static TaxonomyJobSpeciesPanel instance;
	private static ContentPanel cp;
	private ContentPanel vp;
	private static HashMap<String, TaxonomyJobSpeciesProgressBar> hashProgressBars;
	private static HashMap<String, Boolean> hashLoadCompletedNotify;
	private static HashMap<String, ContentPanel> hashTableContentPanels;
	private static String LASTOPERATION = "Last Operation: ";
	private Html lastOperation = new Html(LASTOPERATION);
	private LayoutContainer lc;
	private static String WINDOWTITLE = "Species Taxonomy Jobs";
	private static final String FAILED = "failed";
	private static final String COMPLETED = "completed";
	private static final String LOADING = "loading";
	private static final String PENDING = "pending";
	private static final String OPERATIONONE = "none";
	private static final String SAVING = "saving";

	private TaxonomyGridJob gridJob;
	private EventBus eventBus;
	
	protected Window speciesJobWindow = new Window();
	
	public static synchronized TaxonomyJobSpeciesPanel getInstance(EventBus eventBus) {
		if (instance == null)
			instance = new TaxonomyJobSpeciesPanel(eventBus);
		return instance;
	}

	private TaxonomyJobSpeciesPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.gridJob = new TaxonomyGridJob(eventBus);

		speciesJobWindow.setSize(1020, 600);
		this.setHeaderVisible(false);
		this.setLayout(new FitLayout());
		createSpeciesJobWindow();
//		createToolBar();
	}
	
	private void createSpeciesJobWindow(){
		
		lc = new LayoutContainer();
		lc.setStyleAttribute("margin", "5px");
		cp = new ContentPanel();
		cp.setBodyBorder(true);
		cp.setStyleAttribute("padding", "5px");
		cp.setLayout(new FitLayout());
		
		cp.add(gridJob);
		
		cp.setHeight(550);
		cp.setHeaderVisible(false);
		hashProgressBars = new HashMap<String, TaxonomyJobSpeciesProgressBar>();
		hashTableContentPanels = new HashMap<String, ContentPanel>();
		hashLoadCompletedNotify = new HashMap<String, Boolean>();
		cp.setScrollMode(Scroll.AUTO);
		
		lastOperation.setHtml(LASTOPERATION + OPERATIONONE);
		
//		cp.add(vp);
		lc.add(lastOperation);
		lc.add(cp);
//		
		speciesJobWindow.setHeading(WINDOWTITLE);
		speciesJobWindow.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getTaxonomy16px()));
		speciesJobWindow.setResizable(false);
		speciesJobWindow.add(lc);
	}
	
	/**
	 * 
	 * @param jobsModel
	 * @return true if almost one progress bar is ongoing
	 */
	public boolean addListJob(List<JobTaxonomyModel> jobsModel){
		
		boolean isLoading = false;
		
		for(final JobTaxonomyModel jobModel : jobsModel){
			
			isLoading = addJob(jobModel);
		}
		
		return isLoading;
		
	}
	
	
	public boolean addJob(final JobTaxonomyModel jobModel){
		
		boolean isLoading = false;
		
//		Log.trace("add job :" +jobModel);
		
		TaxonomyJobSpeciesProgressBar jobsBar = hashProgressBars.get(jobModel.getIdentifier());
		
		if(jobsBar!=null){ //is update

			TaxonomyWindowInfoJobsSpecies win = (TaxonomyWindowInfoJobsSpecies) jobsBar.getData("win");
			
			if(win!=null){
				win.updateListStore(jobModel);
				win.layout();
				jobsBar.setData("win", win);
			}
			
			//IF job is COMPLETED OR FAILED OR COMPLETED WITH ERROR, IS NOT UPDATE
			if(jobsBar.isCompleted()){
				//FOR DEBUG
//				Log.trace("jobsBar " +jobModel.getName() + " completed, return" );
				return true;
			}
		
			
			updateProgressBarView(jobsBar, jobModel);
			
			gridJob.updateStatus(jobModel, jobsBar);

		}
		else{ //create new grid item that contains new progress bar
			
			TaxonomyJobSpeciesProgressBar jobProgressBar = new TaxonomyJobSpeciesProgressBar(jobModel.getIdentifier(), jobModel.getDownloadState().toString());
			
			gridJob.addJobIntoGrid(jobModel, jobProgressBar);
			
			updateProgressBarView(jobProgressBar, jobModel);
			
//			hashTableContentPanels.put(jobModel.getIdentifier(), panelTable); //add contentPanel into hashTableContentPanels
			hashProgressBars.put(jobModel.getIdentifier(), jobProgressBar); //add progressBar into hashProgressBars
			hashLoadCompletedNotify.put(jobModel.getIdentifier(), false); //add false (at load completed event) into hashLoadCompletedNotify
		}

		gridJob.layout();
		cp.layout();

		return isLoading;
	}
	
	
	private boolean updateProgressBarView(TaxonomyJobSpeciesProgressBar jobsBar, JobTaxonomyModel jobModel){
		
		
			switch (jobModel.getDownloadState()) {
			
			case PENDING:{
				jobsBar.setProgressText(PENDING);
				break;
			}
			
			case SAVING:{
				lastOperation.setHtml(LASTOPERATION + jobModel.getName() + " saving");
				jobsBar.getElement().getStyle().setBorderColor("#7093DB");
				jobsBar.progressStart();
				jobsBar.setProgressText(SAVING);
				break;
			}
	
			case COMPLETED:{
				
				lastOperation.setHtml(LASTOPERATION + jobModel.getName() + " completed");
//				notifyInfoCompleted(jobModel.getIdentifier(), jobsBar.getProgressText());
				
				jobsBar.getElement().getStyle().setBorderColor("#000000");
				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(COMPLETED);

				break;
			}
			
			
			case SAVED:{
				
				lastOperation.setHtml(LASTOPERATION + jobModel.getName() + " saved");
//				notifyInfoCompleted(jobModel.getIdentifier(), jobsBar.getProgressText());
				
				jobsBar.getElement().getStyle().setBorderColor("#000000");
				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(COMPLETED);

				break;
			}
	
			case ONGOING:{ 

				jobsBar.setProgressText(LOADING);
				jobsBar.progressStart();
	
				return true;
			}
				
			case ONGOINGWITHFAILURES: {
				
				jobsBar.getElement().getStyle().setBorderColor("#f00");
				String job = jobModel.getFailuresNumbers()==1?"job":"jobs";
				jobsBar.updateText("loading " + jobModel.getName() + " with " + jobModel.getFailuresNumbers() + " " +job +" "+  FAILED);
				
				break;
			}
	
			case FAILED:{ 					
	
				jobsBar.getElement().getStyle().setBorderColor("#f00");
				jobsBar.progressStop();
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				jobsBar.updateText(FAILED);
		
				break;
			}
				
			case COMPLETEDWITHFAILURES:{
	
				jobsBar.getElement().getStyle().setBorderColor("#f00");
				String job = jobModel.getFailuresNumbers()==1?"job":"jobs";
				jobsBar.updateText("job completed with " + jobModel.getFailuresNumbers() + " " +job +" "+  FAILED);
				jobsBar.updateProgress(100);
				jobsBar.setCompleted(true);
				
				break;
			}
		}
			
		return false;
		
	}

	public void removeSpeciesJob(String hashHPKey) {
		
	
		ContentPanel cp = hashTableContentPanels.get(hashHPKey);
		
		vp.remove(cp);
		lastOperation.setHtml(LASTOPERATION + cp.getId() + " deleted");
		hashProgressBars.remove(hashHPKey); //remove progress bar from hash
		hashTableContentPanels.remove(hashHPKey); //remove hp from hash 
		hashLoadCompletedNotify.remove(hashHPKey); //remove notify event

		vp.layout();
		
	}
	
	@SuppressWarnings("unused")
	private void deleteProgressCompleted(List<String> progressIdFound){
		
		List<String> progressIdNotFound = new ArrayList<String>();
		
		for(String key : hashTableContentPanels.keySet()){
			
//		    System.out.println("Key " + key );
			
			if(!progressIdFound.contains(key)){ //if key isn't not found - progress is completed so is removed
				
//				System.out.println("Key is not present " + key );
				 
				TaxonomyJobSpeciesProgressBar bulkPB = hashProgressBars.get(key);
				lastOperation.setHtml(LASTOPERATION + bulkPB.getProgressText() + " uploading completed");
				
//				bulkPB.updateProgress(100);
				progressIdNotFound.add(key);
			}
		}
		
		for(String key : progressIdNotFound){
			
			TaxonomyJobSpeciesProgressBar bulkPB = hashProgressBars.get(key);
			lastOperation.setHtml(LASTOPERATION + bulkPB.getProgressText() + " uploading completed");
			
//			vp.remove(hashHorizontalPanels.get(key)); //remove hp from view
			hashProgressBars.remove(key); //remove progress bar from hash
			hashTableContentPanels.remove(key); //remove hp from hash 
		}
		
		vp.layout();
		cp.layout();
	}

	public Window getSpeciesJobWindow() {
		return speciesJobWindow;
	}
	
	public void resetStructures(){
		
		this.gridJob.resetStore();
		
		hashProgressBars.clear();
		lastOperation.setHtml(LASTOPERATION);
		hashTableContentPanels.clear();
		hashLoadCompletedNotify.clear(); 

	}

	public TaxonomyGridJob getGridJob() {
		return gridJob;
	}
}

