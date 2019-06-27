/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.POPULATION_LEVEL;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsServiceAsync;
import org.gcube.portlets.user.performfishanalytics.client.view.BaseDockLayoutPanel;
import org.gcube.portlets.user.performfishanalytics.client.view.BodyPanel;
import org.gcube.portlets.user.performfishanalytics.client.view.CustomTreeModel;
import org.gcube.portlets.user.performfishanalytics.client.view.HeaderPanel;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.BatchIDAndListKPIView;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.PerformFishAnalitycsFormView;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.PortletTitle;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.RecapSubmitPage;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.TabPanelView;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class PerformFishAnalyticsViewController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 16, 2019
 */
public class PerformFishAnalyticsViewController {

	/** The Constant PERFORM_FISH_ANALYTICS_DIV. */
	public static final String PERFORM_FISH_ANALYTICS_DIV = "perform-fish-analytics";
	
	/** The base dock layout panel. */
	private BaseDockLayoutPanel baseDockLayoutPanel;
	
	/** The header page. */
	private HeaderPanel headerPage;
	
	/** The body page. */
	private BodyPanel bodyPage;
	
	/** The batch ID and list KPI. */
	private BatchIDAndListKPIView batchIDAndListKPI;
	
	/** The form. */
	private PerformFishAnalitycsFormView form;
	
	/** The selected population. */
	private Population selectedPopulation;
	
	/** The custom tree model. */
	private CustomTreeModel customTreeModel;
	
	/** The tree. */
	private CellTree tree;
	
	/** The west panel. */
	private VerticalPanel westPanel;
	
	/** The est panel. */
	private VerticalPanel estPanel;
	
	/** The recap page. */
	private RecapSubmitPage recapPage;
	
	/** The tab panel. */
	private TabPanelView tabPanel;
	
	/** The root panel. */
	private VerticalPanel rootPanel;

	/** The reload perform fish service data. */
	private boolean reloadPerformFishServiceData = true;

	/**
	 * Instantiates a new perform fish analytics view controller.
	 */
	public PerformFishAnalyticsViewController() {
		initLayout();
		form = new PerformFishAnalitycsFormView();
		//customTreeModel = new CustomTreeModel();
		customTreeModel = new CustomTreeModel(PerformFishAnalyticsController.eventBus);
		batchIDAndListKPI = new BatchIDAndListKPIView();
		tree = new CellTree(customTreeModel, null);
		//tree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		westPanel.add(form);
		//estPanel.add(recapPage);
		//bodyPage.add(form);
		//tree.setWidth("600px");
		//tree.setSize("600px", "800px");
		batchIDAndListKPI.add(tree);
		bodyPage.addWidget(batchIDAndListKPI);
		bodyPage.addWidget(recapPage);
	}

	/**
	 * Inits the layout.
	 */
	private void initLayout(){
		rootPanel = new VerticalPanel();
		headerPage = new HeaderPanel();
		bodyPage = new BodyPanel();

		baseDockLayoutPanel = new BaseDockLayoutPanel(Unit.PX);
		headerPage.showLoading(true, "Loading Data....");
		westPanel = new VerticalPanel();
		estPanel = new VerticalPanel();
		recapPage = new RecapSubmitPage(PerformFishAnalyticsController.eventBus);
		tabPanel = new TabPanelView();

		headerPage.add(new PortletTitle("PerformFISH Statistical Analysis"));

		//baseDockLayoutPanel.addNorth(headerPage, 60);
		baseDockLayoutPanel.addWest(westPanel, 500);
		//baseDockLayoutPanel.addEast(estPanel, 250);
		baseDockLayoutPanel.add(bodyPage);

		tabPanel.getTabCreateRequestPanel().add(baseDockLayoutPanel);

		rootPanel.add(headerPage);
		rootPanel.add(tabPanel);
		RootPanel.get(PERFORM_FISH_ANALYTICS_DIV).add(rootPanel);
	}


	/**
	 * Creates the tab.
	 *
	 * @param w the w
	 * @param tabTitle the tab title
	 * @return the tab
	 */
	public Tab createTab(Widget w, String tabTitle){
		return tabPanel.addAsTab(w, tabTitle, true);
	}


	/**
	 * No spinner.
	 *
	 * @param tab the tab
	 */
	public void noSpinner(Tab tab){
		tabPanel.setNoSpinner(tab);
	}


	/**
	 * Current number of tab.
	 *
	 * @return the int
	 */
	public int currentNumberOfTab(){

		return tabPanel.countTab();
	}


	/**
	 * Gets the base panel.
	 *
	 * @return the basePanel
	 */
	public BaseDockLayoutPanel getBasePanel() {

		return baseDockLayoutPanel;
	}


	/**
	 * Load population type for level and batch type.
	 *
	 * @param populationName the population name
	 * @param decodedParameters the decoded parameters
	 */
	public void loadPopulationTypeForLevelAndBatchType(final String populationName, final PerformFishInitParameter decodedParameters){

		PerformFishAnalyticsServiceAsync.Util.getInstance().getListPopulationType(populationName, new AsyncCallback<List<PopulationType>>() {

			@Override
			public void onSuccess(List<PopulationType> result) {

				GWT.log("Loaded list of "+PopulationType.class.getSimpleName()+ ": "+result);
				headerPage.showLoading(false);

				if(result.size()>0){
					String passedBatchType = decodedParameters.getParameters().get(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM);
					for (PopulationType populationType : result) {

						if(populationType.getName().compareToIgnoreCase(passedBatchType)==0){
							GWT.log("Found the passed batch type: "+populationType);
							selectedPopulation = result.get(0).getPopulation();
							form.addPopulationTypes(populationName, Arrays.asList(populationType), selectedPopulation);
						}
					}
				}else{
					Window.alert("No batch type found for level: "+populationName);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				headerPage.showLoading(false);
				String error = "Error on getting Population Type for population: "+populationName;
				GWT.log(caught.getLocalizedMessage());
				Window.alert(error);
			}
		});

	}


	/**
	 * Sets the root population type for kp is.
	 *
	 * @param rootPopulationType the new root population type for kp is
	 */
	public void setRootPopulationTypeForKPIs(final PopulationType rootPopulationType) {
		GWT.log("Set root: "+rootPopulationType);
		//customTreeModel.addChildrenListKPI(rootPopulationType.getListKPI());

		PerformFishAnalyticsServiceAsync.Util.getInstance().getPopulationTypeWithListKPI(rootPopulationType.getId(), new AsyncCallback<PopulationType>() {

			@Override
			public void onFailure(Throwable caught) {

				Window.alert("Error on loading KPIs for population type: "+rootPopulationType.getName());

			}

			@Override
			public void onSuccess(PopulationType result) {

				TreeNode rootNode = tree.getRootTreeNode();
				customTreeModel.setNewBatchType(result);
			    // Open the first playlist by default.
			   
			    //firstPlaylist.setChildOpen(0, true);
			    
				expandAllTreeNode(rootNode);

			    //IT SHOULD BE AN EVENT
			    recapPage.removeAllSelected();

			}
		});

	}
	
	/**
	 * Expand all tree node.
	 *
	 * @param rootNode the root node
	 */
	private void expandAllTreeNode(TreeNode rootNode) {
		
		if(rootNode==null || rootNode.getChildCount()==0)
			return;
		
		 for (int i=0; i<rootNode.getChildCount(); i++) {
		   TreeNode childNode = rootNode.setChildOpen(i, true);
		   expandAllTreeNode(childNode);
		   //GWT.log("Opened child: "+childNode);
		 }
	}


	/**
	 * Manage kpi.
	 *
	 * @param kpi the kpi
	 * @param checked the checked
	 * @param selectedPopulationType the selected population type
	 */
	public void manageKPI(KPI kpi, boolean checked, PopulationType selectedPopulationType) {
		recapPage.manageKPI(kpi, checked);
	}


	/**
	 * Manage algorithms submit.
	 *
	 * @param selectedKPIsSize the selected kp is size
	 */
	public void manageAlgorithmsSubmit(int selectedKPIsSize) {

		recapPage.activeAllAlgorithms(false);

		if(selectedKPIsSize>0){
			if(selectedKPIsSize==1){
				recapPage.activeBoxPlot(true);
				recapPage.activeSpeedometer(true);
			}else if(selectedKPIsSize==2){
				recapPage.activeAllAlgorithms(true);
			}else{
				//IS GREATER THAN 2
				recapPage.activeAllAlgorithms(true);
				recapPage.activeScatterPlot(false);
			}

		}
	}


	/**
	 * Enable all algorithms for submit.
	 *
	 * @param bool the bool
	 */
	public void enableAllAlgorithmsForSubmit(boolean bool){
		recapPage.activeAllAlgorithms(bool);
	}


	/**
	 * Manage perform fish service response.
	 *
	 * @param performFishResponse the perform fish response
	 * @param mapParameters the map parameters
	 * @param populationLevel the population level
	 */
	public void managePerformFishServiceResponse(
		PerformFishResponse performFishResponse,
		Map<String, List<String>> mapParameters, POPULATION_LEVEL populationLevel) {

		batchIDAndListKPI.managePerformFishServiceResponse(performFishResponse, mapParameters, populationLevel);

	}


	/**
	 * Gets the perform fish response.
	 *
	 * @return the perform fish response
	 */
	public PerformFishResponse getPerformFishResponse() {

		return batchIDAndListKPI.getPerformFishResponse();
	}


	/**
	 * Gets the map parameters.
	 *
	 * @return the map parameters
	 */
	public Map<String, List<String>> getRequestMapParameters() {

		return batchIDAndListKPI.getMapParameters();
	}


	/**
	 * Validate kpi fields.
	 *
	 * @return true, if successful
	 */
	public boolean validateKPIFields() {

		bodyPage.hideError();
		List<KPI> selectedKPIs = recapPage.getSelectedKPIs();
		if(selectedKPIs.size()<1){
			//bodyPage.showAlert("Please select at least 1 KPI.", AlertType.ERROR);
			bodyPage.showAlert("Please select at least 1 KPI. Expand the KPI's levels and than select at least 1 KPI by checkbox", AlertType.ERROR);
			//recapPage.setError("You must select a KPI");
			return false;
		}

		if(selectedKPIs.size()>10){
			bodyPage.showAlert("Please select 10 KPI at most", AlertType.ERROR);
			//recapPage.setError("You must select a KPI");
			return false;
		}

		return true;

	}
	
	
	/**
	 * Reset batch id status.
	 */
	public void resetBatchIdStatus() {
		batchIDAndListKPI.resetBatchIdStatus();
	}
	

	/**
	 * Sets the batch id status.
	 *
	 * @param status the new batch id status
	 */
	public void setBatchIdStatus(ControlGroupType status) {
		ControlGroup cg_batch_id = batchIDAndListKPI.getControlGroupBatchID();
		cg_batch_id.setType(status);
	}
	
	/**
	 * Validate batch id selection.
	 *
	 * @return true, if successful
	 */
	public boolean validateBatchIdSelection() {

		ControlGroup cg_batch_id = batchIDAndListKPI.getControlGroupBatchID();
		ListBox uib_list_batch_id = batchIDAndListKPI.getListBoxBatchId();

		cg_batch_id.setType(ControlGroupType.NONE);

		//CHECK THE FOCUS ID VALUE
		String batchID = uib_list_batch_id.getSelectedItemText();

		if(batchID==null || batchID.isEmpty()){

			/*String msgError = "Could not execute a valid Analysis.";

			List<String> selectedAreas = form.getArea();

			if(selectedAreas==null || selectedAreas.isEmpty()){
				msgError+=" Please select another parameters computation";
			}else{
				msgError+=" Select at least the Area of your FARM";
			}

			cg_batch_id.setType(ControlGroupType.ERROR);
			batchIDAndListKPI.showError(msgError, true);
			return false;*/

			String msgError = "Could not find valid data. Please change your selection (select other parameters for Quarter and/or Area and/or Period)";
			cg_batch_id.setType(ControlGroupType.WARNING);
			batchIDAndListKPI.showAlert(msgError, AlertType.WARNING, false);
			batchIDAndListKPI.enableButtonLoadBatches(false);
			return false;
		}


		//batchIDAndListKPI.showSelectionOK("Batch ID selection is valid", closable);
		return true;
	}


	/**
	 * Validate perform fish input fields.
	 *
	 * @return true, if successful
	 */
	public boolean validatePerformFishInputFields() {
		return form.validateForm();

	}
	
	
	/**
	 * Enable load batches.
	 *
	 * @param enable the enable
	 * @return true, if successful
	 */
	public void enableLoadBatches(boolean enable) {
		batchIDAndListKPI.enableButtonLoadBatches(enable);
	}

	/**
	 * Resync selected kp is.
	 */
	public void resyncSelectedKPIs() {

		//customTreeModel.resync(getSelectedKPIs());

	}

	/**
	 * Gets the form.
	 *
	 * @return the form
	 */
	public PerformFishAnalitycsFormView getForm() {

		return form;
	}

	/**
	 * Hide errors.
	 */
	public void hideErrors(){
		bodyPage.hideError();
		form.hideError(null, true);
	}


	/**
	 * Gets the selected kp is.
	 *
	 * @return the selected kp is
	 */
	public List<KPI> getSelectedKPIs(){

		return recapPage.getSelectedKPIs();
	}


	/**
	 * Checks if is reload perform fish service data.
	 *
	 * @return true, if is reload perform fish service data
	 */
	public boolean isReloadPerformFishServiceData() {

		return reloadPerformFishServiceData;
	}


	/**
	 * Sets the reload perform fish service data.
	 *
	 * @param reloadPerformFishServiceData the new reload perform fish service data
	 */
	public void setReloadPerformFishServiceData(boolean reloadPerformFishServiceData) {

		this.reloadPerformFishServiceData = reloadPerformFishServiceData;
	}



	/**
	 * Gets the selected batch id.
	 *
	 * @return the selected batch id
	 */
	public List<String> getSelectedBatchID() {

		return batchIDAndListKPI.getSelectedBatchID();

	}


	/**
	 * Gets the list batches id.
	 *
	 * @return the list batches id
	 */
	public List<String> getListBatchesID() {

		return batchIDAndListKPI.getAllBatchesID();
	}
	
	
	/**
	 * Show alert for load batches.
	 *
	 * @param msg the msg
	 * @param type the type
	 * @param closable the closable
	 */
	public void showAlertForLoadBatches(String msg, AlertType type, boolean closable){
		batchIDAndListKPI.showAlert(msg, type, closable);
	}




}
