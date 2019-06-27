/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.POPULATION_LEVEL;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsServiceAsync;
import org.gcube.portlets.user.performfishanalytics.client.event.AddedBatchIdEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.AddedBatchIdEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.CallAlgorithmEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.CallAlgorithmEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadBatchesEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadBatchesEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedKPIEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedKPIEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SubmitRequestEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SubmitRequestEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.view.LoaderIcon;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.AnalyticsPanelResult;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.ShowResult;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.SubmitRequestPanel;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


// TODO: Auto-generated Javadoc
/**
 * The Class PerformFishAnalyticsController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 16, 2019
 */
public class PerformFishAnalyticsController {

	/** The Constant eventBus. */

	public final static HandlerManager eventBus = new HandlerManager(null);
	
	/** The view controller. */
	private PerformFishAnalyticsViewController viewController;

	/** The decrypt parameters. */
	private PerformFishInitParameter decryptParameters;


	/**
	 * Instantiates a new perform fish analytics controller.
	 */
	public PerformFishAnalyticsController() {

		registerHandlers();
		viewController = new PerformFishAnalyticsViewController();
	}
	
	/**
	 * Sets the inits the parmaters.
	 *
	 * @param result the new inits the parmaters
	 */
	public void setInitParmaters(PerformFishInitParameter result) {
		this.decryptParameters = result;
	}


	/**
	 * Register handlers.
	 */
	private void registerHandlers() {

		eventBus.addHandler(LoadPopulationTypeEvent.TYPE, new LoadPopulationTypeEventHandler() {

			@Override
			public void onLoadPopulationType(
				LoadPopulationTypeEvent loadPopulationEvent) {
				
				viewController.loadPopulationTypeForLevelAndBatchType(loadPopulationEvent.getPopulationName(), decryptParameters);
				
//				if(loadPopulationEvent.getPopulationName().equals(POPULATION_LEVEL.BATCH.name())) {
//				
//				}else {
//					viewAnnualController.loadPopulationTypeForLevelAndBatchType(loadPopulationEvent.getPopulationName(), decryptParameters);
//				}
			}
		});

		eventBus.addHandler(CallAlgorithmEvent.TYPE, new CallAlgorithmEventHandler() {

			@Override
			public void onCall(CallAlgorithmEvent callAlgorithmEvent) {

				callAlgorithm(callAlgorithmEvent.getAlgorithm(), callAlgorithmEvent.getFocusID(), callAlgorithmEvent.getInputKPI(), callAlgorithmEvent.getOutputKPI());
			}
		});

		eventBus.addHandler(SelectedPopulationTypeEvent.TYPE, new SelectedPopulationTypeEventHandler() {

			@Override
			public void onSelectedPopulationType(
				SelectedPopulationTypeEvent selectedPopulationTypeEvent) {

				viewController.setRootPopulationTypeForKPIs(selectedPopulationTypeEvent.getSelectedPopulationType());
			}
		});

		eventBus.addHandler(SelectedKPIEvent.TYPE, new SelectedKPIEventHandler() {

			@Override
			public void onSelectedKPI(SelectedKPIEvent selectedKPI) {

				viewController.manageKPI(selectedKPI.getKpi(), selectedKPI.isChecked(), selectedKPI.getSelectedPopulationType());
				int selectedKPIsSize = viewController.getSelectedKPIs().size();
				//viewController.manageAlgorithmsSubmit(selectedKPIsSize);
				List<String> batchIds = viewController.getSelectedBatchID();
				if(batchIds==null || batchIds.size()==0) {
					viewController.enableAllAlgorithmsForSubmit(false);
				}else
					viewController.manageAlgorithmsSubmit(selectedKPIsSize);
			}
		});
		
		eventBus.addHandler(LoadBatchesEvent.TYPE, new LoadBatchesEventHandler() {
			
			@Override
			public void onLoadBatches(LoadBatchesEvent loadBatchesEvent) {
				
				viewController.hideErrors();
				
				final Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
				String farmId = decryptParameters.getParameters().get(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM);
				String batchType = viewController.getForm().getBatchType();
				String species = viewController.getForm().getSpecies();
				List<String> listArea = viewController.getForm().getArea();
				List<String> listPeriod = viewController.getForm().getPeriod();
				List<String> listQuarter = viewController.getForm().getQuarter();

				mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM, Arrays.asList(farmId));
				mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM, Arrays.asList(batchType));
				mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_SPECIES_ID_PARAM, Arrays.asList(species));

				if(!listArea.isEmpty()){
					mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_AREA_PARAM, listArea);
				}
				if(!listPeriod.isEmpty()){
					mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_PERIOD_PARAM, listPeriod);
				}
				if(!listQuarter.isEmpty()){
					mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_QUARTER_PARAM, listQuarter);
				}

				final Modal modal = new Modal(true);
				modal.setCloseVisible(false);
				modal.hide(false);
				final VerticalPanel vp = new VerticalPanel();
				LoaderIcon loader = new LoaderIcon("Loading batch(es) from PerformFish service, please wait...");
				vp.add(loader);
				loader.show(true);
				modal.add(vp);
				PerformFishAnalyticsServiceAsync.Util.getInstance().submitRequestToPerformFishService(mapParameters, new AsyncCallback<PerformFishResponse>() {

					@Override
					public void onFailure(Throwable caught) {
						modal.hide();
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(PerformFishResponse performFishResponse) {
						modal.hide();
						viewController.managePerformFishServiceResponse(performFishResponse, mapParameters, POPULATION_LEVEL.BATCH);
					}
				});
				modal.show();
				
			}
		});


		eventBus.addHandler(PerformFishFieldFormChangedEvent.TYPE, new PerformFishFieldFormChangedEventHandler() {

			@Override
			public void onFieldFormChanged(
				PerformFishFieldFormChangedEvent performFishFieldFormChangedEvent) {

				viewController.setReloadPerformFishServiceData(true);
				boolean isValidForm = viewController.validatePerformFishInputFields();
				
				viewController.resetBatchIdStatus();
				
				if(isValidForm) {
					//viewController.resetBatchIdStatus();
					viewController.setBatchIdStatus(ControlGroupType.INFO);
					viewController.enableLoadBatches(true);
					viewController.showAlertForLoadBatches("Please load your batches corresponding to the selected options, by pressing the 'Load Batches' button", AlertType.INFO, false);
					viewController.enableAllAlgorithmsForSubmit(false);
					
				}else {
					viewController.enableLoadBatches(false);
					viewController.enableAllAlgorithmsForSubmit(false);
				}
			}
		});

		eventBus.addHandler(AddedBatchIdEvent.TYPE, new AddedBatchIdEventHandler() {

			@Override
			public void onAddedBatchId(AddedBatchIdEvent checkValidBatchIdEvent) {

				boolean isBatchIdValid = viewController.validateBatchIdSelection();
				if(isBatchIdValid)
					viewController.enableAllAlgorithmsForSubmit(true);
				else
					viewController.enableAllAlgorithmsForSubmit(false);
				
				//viewController.resyncSelectedKPIs();
			}
		});


		eventBus.addHandler(SubmitRequestEvent.TYPE, new SubmitRequestEventHandler() {

			@Override
			public void onSubmitRequest(SubmitRequestEvent submitRequestEvent) {

				boolean isValidBatchId = viewController.validateBatchIdSelection();

				boolean isValidKPI = viewController.validateKPIFields();

				List<KPI> selectedKPI = viewController.getSelectedKPIs();
				viewController.manageAlgorithmsSubmit(selectedKPI.size());

				if(isValidBatchId && isValidKPI){

					switch (submitRequestEvent.getChartType()) {

					case BOXPLOT:
						//UNARY
						callAlgorithm(submitRequestEvent.getChartType(), viewController.getSelectedBatchID().get(0), selectedKPI, null);
						break;

					case SCATTER:
						if(selectedKPI.size()==2)
							callAlgorithm(submitRequestEvent.getChartType(), viewController.getSelectedBatchID().get(0), selectedKPI, null);
						else
							Window.alert("Something seems wrong... You must select exactly two KPIs to execute the "+submitRequestEvent.getChartType());
						break;

					case CORRELATION:
						callDataMinerServiceForChartTypeCorrelation(viewController.getPerformFishResponse(), viewController.getRequestMapParameters());
						break;

					case SPEEDOMETER:
						//UNARY
						callAlgorithm(submitRequestEvent.getChartType(), viewController.getSelectedBatchID().get(0), selectedKPI, selectedKPI);
						break;

					default:
						break;
					}

				}

				return;
			}
		});

	}

	/**
	 * Call data miner service for chart type correlation.
	 *
	 * @param performFishResponse the perform fish response
	 * @param performFishRequestParameters the perform fish request parameters
	 */
	private void callDataMinerServiceForChartTypeCorrelation(final PerformFishResponse performFishResponse, final Map<String, List<String>> performFishRequestParameters) {

		GWT.log("Read perform fish response: "+performFishResponse);

		String batchTableURL = performFishResponse.getMapParameters().get(PerformFishAnalyticsConstant.BATCHES_TABLE);

		if(batchTableURL==null || batchTableURL.isEmpty())
			Window.alert("Something seems wrong. No batches tables matching with parameter "+PerformFishAnalyticsConstant.BATCHES_TABLE+" returned from service");

		final Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();

		StringBuilder dataInputsFormatter = new StringBuilder();

		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_SCALEP_PARAM+"=BATCH;");
		String theBatchType = viewController.getForm().getBatchType();
		theBatchType = theBatchType.replace("_CLOSED_BATCHES", ""); //REMOVING SUFFIX _CLOSED_BATCHES FOR DATAMINER CALL
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_BATCHTYPE_PARAM+"="+theBatchType+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_CHARTTYPE_PARAM+"="+ DataMinerAlgorithms.CORRELATION+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FARMFILE_PARAM+"="+batchTableURL+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"=ID;");

		String kpiCodes = "";
		for (KPI kpi : viewController.getSelectedKPIs()) {
			kpiCodes+=kpi.getCode()+"|";
		}
		//remove last |
		kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);
		dataInputsFormatter.append("inputKPI="+kpiCodes+";");
		//dataInputsFormatter.append("outputKPI=;");

		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final SubmitRequestPanel submitRequestPanel = new SubmitRequestPanel("", 1);
		submitRequestPanel.showLoader(true, "Submitting "+DataMinerAlgorithms.CORRELATION.getName()+" request to DM Service...");
		String tabTitle = DataMinerAlgorithms.CORRELATION.getName().substring(0,1).toUpperCase()+DataMinerAlgorithms.CORRELATION.getName().toLowerCase().substring(1, DataMinerAlgorithms.CORRELATION.getName().length()); //CAMEL CASE
		final Tab tab = viewController.createTab(submitRequestPanel, tabTitle+" #"+(viewController.currentNumberOfTab()+1));
		
		final List<KPI> selectedKPI = new ArrayList<KPI>(viewController.getSelectedKPIs());
		final List<String> batchIDs = new ArrayList<String>(viewController.getListBatchesID());
		PerformFishAnalyticsServiceAsync.Util.getInstance().callingDataMinerPerformFishCorrelationAnalysis(performFishResponse, mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				submitRequestPanel.showLoader(false, null);
				checkTabSpinner(submitRequestPanel, tab);
				AnalyticsPanelResult analyticsPanelResult = new AnalyticsPanelResult();
				analyticsPanelResult.addSelectedAreas(performFishRequestParameters.get(PerformFishAnalyticsConstant.PERFORM_FISH_AREA_PARAM));
				analyticsPanelResult.addSelectedKPIs(selectedKPI);
				analyticsPanelResult.addListFocusIds(batchIDs);
				analyticsPanelResult.addParameters(PerformFishAnalyticsConstant.DATA_INPUTS, mapParameters, viewController.getForm().getBatchType());
				analyticsPanelResult.addResults(dmResponse);
				submitRequestPanel.addWidget(analyticsPanelResult);
				//viewController.geTabPanelView().addResult(resultPanel, "Analysis #"+(viewController.geTabPanelView().countTab()+1));
				//modal.hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.toString());
				submitRequestPanel.showLoader(false, null);
				checkTabSpinner(submitRequestPanel, tab);
				try {
					AnalyticsPanelResult analyticsPanelResult = new AnalyticsPanelResult();
					analyticsPanelResult.addSelectedAreas(performFishRequestParameters.get(PerformFishAnalyticsConstant.PERFORM_FISH_AREA_PARAM));
					analyticsPanelResult.addSelectedKPIs(selectedKPI);
					analyticsPanelResult.addListFocusIds(batchIDs);
					analyticsPanelResult.addParameters(PerformFishAnalyticsConstant.DATA_INPUTS, mapParameters, viewController.getForm().getBatchType());
					submitRequestPanel.addWidget(analyticsPanelResult);
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				Alert error = new Alert(caught.getMessage());
				error.setClose(false);
				error.setType(AlertType.ERROR);
				submitRequestPanel.addWidget(error);
			}
		});
	}


	/**
	 * Creating new TAB and calling DM algorithm.
	 *
	 * @param algorithm the algorithm
	 * @param focusID the focus id
	 * @param inputKPI the input kpi
	 * @param outputKPI the output kpi
	 */
	private void callAlgorithm(DataMinerAlgorithms algorithm, String focusID, List<KPI> inputKPI, final List<KPI> outputKPI){

		List<String> listBatchID = viewController.getSelectedBatchID();

		if(listBatchID==null || listBatchID.size()==0)
			Window.alert("Something seems wrong, no selected BatchID, try again");

		SubmitRequestPanel submitRequestPanel = new SubmitRequestPanel("", 1);
		//submitRequestPanel.getElement().addClassName("ext-horizontal-panel");
		String tabTitle = algorithm.getName().substring(0,1).toUpperCase()+algorithm.getName().toLowerCase().substring(1, algorithm.getName().length()); //CAMEL CASE
		Tab tab = viewController.createTab(submitRequestPanel, tabTitle+" #"+(viewController.currentNumberOfTab()+1));

		switch (algorithm) {
		case BOXPLOT:
		case SPEEDOMETER:

			submitRequestPanel.setTotalRequests(inputKPI.size());
			for (KPI kpi : inputKPI) {
				String title = "KPI: "+kpi.getName();
				HTML toBigTitle = new HTML(title);
				toBigTitle.getElement().addClassName("to-big-title");
				submitRequestPanel.addWidget(toBigTitle);
				HorizontalPanel hp = new HorizontalPanel();
				hp.getElement().addClassName("ext-horizontal-panel");
				callDataMinerServiceForChart(viewController.getPerformFishResponse(), POPULATION_LEVEL.BATCH, Arrays.asList(kpi), Arrays.asList(kpi), algorithm, focusID, submitRequestPanel, hp, tab);
				//resultPanel.add(hp);
				submitRequestPanel.addWidget(hp);
			}
			break;
		case SCATTER:

			if(inputKPI.get(0)==null || inputKPI.get(1)==null){
				submitRequestPanel.setTheTitle("Sorry, something seems wrong, the selected KPIs are not valid. Please try again");
				checkTabSpinner(submitRequestPanel, tab);
				return;
//				Window.alert("Something seems wrong, no selected BatchID, try again");
			}

			String titleScatter = "KPI: "+inputKPI.get(0).getName() +" vs "+inputKPI.get(1).getName();
			submitRequestPanel.setTheTitle(titleScatter);

		default:
			callDataMinerServiceForChart(viewController.getPerformFishResponse(), POPULATION_LEVEL.BATCH, inputKPI, outputKPI, algorithm, focusID, submitRequestPanel, submitRequestPanel.getContainerPanel(), tab);
		}
	}


	/**
	 * Call data miner service for chart.
	 *
	 * @param performFishResponse the perform fish response
	 * @param scalePValue the scale p value
	 * @param inputKPI the input kpi
	 * @param outputKPI the output kpi
	 * @param chartType the chart type
	 * @param focusID the focus id
	 * @param requestPanel the request panel
	 * @param panelContainer the panel
	 * @param tab the tab
	 */
	private void callDataMinerServiceForChart(PerformFishResponse performFishResponse, POPULATION_LEVEL scalePValue, final List<KPI> inputKPI, final List<KPI> outputKPI, final DataMinerAlgorithms chartType, final String focusID, final SubmitRequestPanel requestPanel, final ComplexPanel panelContainer, final Tab tab) {

		GWT.log("Read perform fish response: "+performFishResponse);

		String batchTableURL = performFishResponse.getMapParameters().get(PerformFishAnalyticsConstant.BATCHES_TABLE);

		if(batchTableURL==null || batchTableURL.isEmpty())
			Window.alert("Something seems wrong. No batches tables matching with parameter "+PerformFishAnalyticsConstant.BATCHES_TABLE+" returned from service");


		StringBuilder dataInputsFormatter = new StringBuilder();
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_SCALEP_PARAM+"="+scalePValue.name()+";");
		String theBatchType = viewController.getForm().getBatchType();
		theBatchType = theBatchType.replace("_CLOSED_BATCHES", ""); //REMOVING SUFFIX _CLOSED_BATCHES FOR DATAMINER CALL
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_BATCHTYPE_PARAM+"="+theBatchType+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_CHARTTYPE_PARAM+"="+ chartType +";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FARMFILE_PARAM+"="+batchTableURL+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"="+focusID+";");
		//dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"=ID;");

		if(inputKPI!=null && inputKPI.size()>0){
			String kpiCodes = "";
			for (KPI kpi : inputKPI) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Input KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_INPUT_KPI_PARAM+"="+kpiCodes+";");

		}

		if(outputKPI!=null && outputKPI.size()>0){
			String kpiCodes = "";
			for (KPI kpi : outputKPI) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Output KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_OUTPUT_KPI_PARAM+"="+kpiCodes+";");

		}

		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final LoaderIcon loaderIcon = new LoaderIcon("Submitting request to "+chartType+" Analysis...");
		loaderIcon.setVisible(true);
		panelContainer.setVisible(true);
		panelContainer.add(loaderIcon);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loaderIcon.setFocus(true);
		}});

		PerformFishAnalyticsServiceAsync.Util.getInstance().callingDataMinerPerformFishAnalysis(mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				loaderIcon.setVisible(false);
				checkTabSpinner(requestPanel, tab);
				//field_unary_algorithm.setVisible(true);
				GWT.log("I'm displaying: "+dmResponse);
				displayOutputFilesAsStaticEntities(dmResponse, chartType, inputKPI, outputKPI, focusID, panelContainer, false);
			}

			@Override
			public void onFailure(Throwable caught) {
				loaderIcon.setVisible(false);
				checkTabSpinner(requestPanel, tab);
				displayOutputFilesAsStaticEntities(null, chartType, inputKPI, outputKPI, focusID, panelContainer, true);

			}
		});
	}

	/**
	 * Remove the spinner if all DM responses are returned.
	 *
	 * @param requestPanel the request panel
	 * @param tab the tab
	 */
	private void checkTabSpinner(SubmitRequestPanel requestPanel, Tab tab){
		requestPanel.incrementCompletedRequests();
		int completed = requestPanel.getCompletedRequests();
		int total = requestPanel.getTotalRequests();
		
		if(completed>=total) {
			viewController.noSpinner(tab);
		}
	}

	/**
	 * Display output files as static entities.
	 *
	 * @param dmResponse the dm response
	 * @param chartType the chart type
	 * @param inputKPIs the input kp is
	 * @param outputKPIs the output kp is
	 * @param focusID the focus id
	 * @param container the container
	 * @param displayError the display error
	 */
	private void displayOutputFilesAsStaticEntities(DataMinerResponse dmResponse, final DataMinerAlgorithms chartType, List<KPI> inputKPIs, List<KPI> outputKPIs, final String focusID, final Panel container, boolean displayError){

		String title = displayError?"No results ":"";
		
		if(displayError){
			Alert alert = new Alert(title);
			alert.setType(AlertType.ERROR);
			alert.setClose(false);
			alert.getElement().getStyle().setMargin(10, Unit.PX);
			container.add(alert);
			return;
		}

		final String toTitle = title;

		for (final OutputFile outputFile : dmResponse.getListOutput()) {

			switch (outputFile.getDataType()) {
			case IMAGE:
				PerformFishAnalyticsServiceAsync.Util.getInstance().getImageFile(outputFile, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						//showAlert(caught.getMessage(), AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);
						Window.alert(caught.getMessage());

					}

					@Override
					public void onSuccess(String base64Content) {

						String title = toTitle;
						switch (chartType) {
						case BOXPLOT:
							title = chartType.getName()+" - on all batches<br>";
							title += "Blue dots indicate the selected batch(es): "+focusID;
							break;
						case CORRELATION:
							break;
						case DEA_ANALYSIS:
							break;
						case SCATTER:
							break;
						case SPEEDOMETER:
							title = "Speedometer<br>";
							title+= "Selected BATCH ID: "+focusID+"<br>";
							title+= "Normalized with respect to all batches in the VRE";
							break;
						case DEA_CHART:
							break;
						}

						ShowResult showResult = new ShowResult(title);
						showResult.showImage(base64Content);
						container.add(showResult);

					}
				});
				break;
			case CSV:
				PerformFishAnalyticsServiceAsync.Util.getInstance().getCSVFile(outputFile, true, new AsyncCallback<CSVFile>() {

					@Override
					public void onFailure(Throwable caught) {
						//showAlert(caught.getMessage(), AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(CSVFile result) {
						GWT.log("Displaying: "+result);

						String title = toTitle;
						switch (chartType) {
						case BOXPLOT:
							if(result.getFileName().contains("_user")){
								title = "My Batch(es)";
							}else{
								title = "All batches in the VRE (including mine)";
							}
							break;
						case CORRELATION:
							break;
						case DEA_ANALYSIS:
							break;
						case SCATTER:
							break;
						case SPEEDOMETER:
							break;
						case DEA_CHART:
							break;
						}

						ShowResult showResult = new ShowResult(title);
						showResult.showCSVFile(result);
						container.add(showResult);
					}
				});
				break;

			default:
				break;
			}

		}

	}



}
