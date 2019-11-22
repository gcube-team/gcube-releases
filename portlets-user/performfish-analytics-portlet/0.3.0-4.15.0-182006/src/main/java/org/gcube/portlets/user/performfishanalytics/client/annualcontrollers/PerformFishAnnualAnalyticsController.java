/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.annualcontrollers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsServiceAsync;
import org.gcube.portlets.user.performfishanalytics.client.event.CallAlgorithmEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.CallAlgorithmEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadFocusEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadFocusEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadSynopticTableEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadSynopticTableEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedKPIEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedKPIEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.event.SubmitRequestEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SubmitRequestEventHandler;
import org.gcube.portlets.user.performfishanalytics.client.resources.PerformFishResources;
import org.gcube.portlets.user.performfishanalytics.client.view.LoaderIcon;
import org.gcube.portlets.user.performfishanalytics.client.viewannualbinder.AnalyticsAnnualPanelResult;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.AnalyticsPanelResult;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.ShowResult;
import org.gcube.portlets.user.performfishanalytics.client.viewbinder.SubmitRequestPanel;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;


// TODO: Auto-generated Javadoc
/**
 * The Class PerformFishAnalyticsController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 16, 2019
 */
public class PerformFishAnnualAnalyticsController {

	/** The Constant eventBus. */

	public final static HandlerManager eventBus = new HandlerManager(null);
	
	/** The view controller. */
	private PerformFishAnnualAnalyticsViewController viewAnnualController;

	/** The decrypt parameters. */
	private PerformFishInitParameter decryptParameters;
	
	
	private PerformFishResponse thePerformFishResponse;
	
	/** The focus. */
	//This value is read from the first value of column "FARM" contained in 
	//the table AnnualTable_internal.csv returned by PerformFish Service
	private String theFocusValue = null;
	
	private Map<Integer,FlexTable> boxPlotOrderBy = new HashMap<Integer,FlexTable>();
	
	private Map<Integer,FlexTable> synopticOrderBy = new HashMap<Integer,FlexTable>();


	/**
	 * Instantiates a new perform fish analytics controller.
	 */
	public PerformFishAnnualAnalyticsController() {

		registerHandlers();
	}
	
	/**
	 * Sets the inits the parmaters.
	 *
	 * @param result the new inits the parmaters
	 */
	public void setInitParmaters(PerformFishInitParameter result) {
		this.decryptParameters = result;
		viewAnnualController = new PerformFishAnnualAnalyticsViewController();
	}


	/**
	 * Register handlers.
	 */
	private void registerHandlers() {

		eventBus.addHandler(LoadPopulationTypeEvent.TYPE, new LoadPopulationTypeEventHandler() {

			@Override
			public void onLoadPopulationType(
				LoadPopulationTypeEvent loadPopulationEvent) {
				
				viewAnnualController.loadPopulationTypeForLevelAndBatchType(loadPopulationEvent.getPopulationName(), decryptParameters);
				
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

				viewAnnualController.setRootPopulationTypeForKPIs(selectedPopulationTypeEvent.getSelectedPopulationType());
			}
		});

		eventBus.addHandler(SelectedKPIEvent.TYPE, new SelectedKPIEventHandler() {

			@Override
			public void onSelectedKPI(SelectedKPIEvent selectedKPI) {
				GWT.log("Selected KPI: "+selectedKPI);
				viewAnnualController.manageKPI(selectedKPI.getKpi(), selectedKPI.isChecked(), selectedKPI.getSelectedPopulationType());
				eventBus.fireEvent(new PerformFishFieldFormChangedEvent(null));
				
				//viewController.manageAlgorithmsSubmit(selectedKPIsSize);
//				List<String> batchIds = viewController.getSelectedBatchID();
//				if(batchIds==null || batchIds.size()==0) {
//					viewController.enableAllAlgorithmsForSubmit(false);
//				}else
					
				//viewController.manageAlgorithmsSubmit(selectedKPIsSize);
			}
		});
		
		eventBus.addHandler(PerformFishFieldFormChangedEvent.TYPE, new PerformFishFieldFormChangedEventHandler() {

			@Override
			public void onFieldFormChanged(
				PerformFishFieldFormChangedEvent performFishFieldFormChangedEvent) {

				//viewAnnualController.setReloadPerformFishServiceData(true);
				boolean isValidForm = viewAnnualController.validatePerformFishInputFields();
				
				if(isValidForm) {
					//boolean isKPIsSelected = viewAnnualController.validateKPIFields();
					int selectedKPIsSize = viewAnnualController.getSelectedKPIs().size();
					viewAnnualController.manageAlgorithmsSubmit(selectedKPIsSize);
					viewAnnualController.enableSynopticTable(true);
				}else {
					//viewController.enableLoadBatches(false);
					viewAnnualController.enableAllAlgorithmsForSubmit(false);
					viewAnnualController.enableSynopticTable(false);
				}
			}
		});
		
		eventBus.addHandler(LoadFocusEvent.TYPE, new LoadFocusEventHandler() {
			
			@Override
			public void onLoadFocusEvent(LoadFocusEvent loadFocusEvent) {

				final Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
				String farmId = decryptParameters.getParameters().get(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM);
				String batchType =  decryptParameters.getParameters().get(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM);

				//String batchType = viewAnnualController.getForm().getBatchType();
				
				//List<String> listYear = viewAnnualController.getForm().getYear();

				mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM, Arrays.asList(farmId));
				mapParameters.put(PerformFishAnalyticsConstant.PERFORM_FISH_BATCH_TYPE_PARAM, Arrays.asList(batchType));

				PerformFishAnalyticsServiceAsync.Util.getInstance().submitRequestToPerformFishService(mapParameters, new AsyncCallback<PerformFishResponse>() {

					@Override
					public void onFailure(Throwable caught) {
						//modal.hide();
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(PerformFishResponse performFishResponse) {
						thePerformFishResponse = performFishResponse;
						GWT.log("PerformFish Response: "+performFishResponse);
						
						final String pfTableName = PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AnnualTable_internal.getPerformFishTable();

						String fileURL = performFishResponse.getMapParameters().get(pfTableName);

						GWT.log(pfTableName+" is: "+fileURL);

						//Managing the Perform Fish Service Response
						if(fileURL==null){
							viewAnnualController.showAlert("No table found by searching for name: "+PerformFishAnalyticsConstant.BATCHES_TABLE_INTERNAL, AlertType.ERROR);
						}else{


							PerformFishAnalyticsServiceAsync.Util.getInstance().readCSVFile(fileURL, new AsyncCallback<CSVFile>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());

								}

								@Override
								public void onSuccess(CSVFile result) {

									if(result==null){
										viewAnnualController.showAlert("The focus was not found in the table "+pfTableName, AlertType.ERROR);
										return;
									}
									
									int indexOfFARM = result.getHeaderRow().getListValues().indexOf(PerformFishAnalyticsConstant.POPULATION_LEVEL.FARM.name());
									
									GWT.log("The index of column "+PerformFishAnalyticsConstant.POPULATION_LEVEL.FARM.name()+" is "+indexOfFARM);
									
									if(indexOfFARM>-1){
										List<CSVRow> rows = result.getValueRows();

										if(rows==null || rows.isEmpty()){
											viewAnnualController.showAlert("No valid focus was found in the table "+pfTableName, AlertType.ERROR);
											return;
										}

										String focusValue = null;
										//IN THE COLUMN WITH HEADER 'FARM' THE FOCUS VALUE IS THE SAME FOR ALL ROWS
										for (CSVRow row : rows) {
											focusValue = row.getListValues().get(indexOfFARM);
											if(focusValue!=null && !focusValue.isEmpty())
												break;
										}
										
										if(focusValue==null) {
											viewAnnualController.showAlert("No valid focus was found in the table "+pfTableName, AlertType.ERROR);
											return;
										}
										
										theFocusValue = focusValue;
										GWT.log("Loaded the focus value: "+theFocusValue);
									}
								}
							});

						}
					}
				});
				
			}
		});

//
		eventBus.addHandler(SubmitRequestEvent.TYPE, new SubmitRequestEventHandler() {

			@Override
			public void onSubmitRequest(SubmitRequestEvent submitRequestEvent) {

				//boolean isValidBatchId = viewAnnualController.validateBatchIdSelection();

				boolean isValidKPI = viewAnnualController.validateKPIFields();

				List<KPI> selectedKPI = viewAnnualController.getSelectedKPIs();
				viewAnnualController.manageAlgorithmsSubmit(selectedKPI.size());
				
				if(theFocusValue!=null) {
				
					if(isValidKPI){
						submitRequestToDM(submitRequestEvent.getChartType());
					}
				}

				return;
			}
		});
		
		eventBus.addHandler(LoadSynopticTableEvent.TYPE, new LoadSynopticTableEventHandler() {
			
			@Override
			public void onLoadSynopticTable(LoadSynopticTableEvent loadSynopticTableEvent) {

				callAlgorithmSynopticTableFarm();
			}
		});

	}
	
	/**
	 * Call algorithm synoptic table farm.
	 */
	protected void callAlgorithmSynopticTableFarm() {
		
		final DataMinerAlgorithms algorithm = DataMinerAlgorithms.PERFORMFISH_SYNOPTIC_TABLE_FARM;
		final SubmitRequestPanel submitRequestPanel = new SubmitRequestPanel("", 1);
		String tabTitle = "Synoptic Table";
		
		final Tab tab = viewAnnualController.createTab(tabTitle+" #"+(viewAnnualController.currentNumberOfTab()+1),PerformFishResources.INSTANCE.synopticTable().getText(),submitRequestPanel);

		StringBuilder dataInputsFormatter = new StringBuilder();
		Map<String, String> performFishResponseMap = thePerformFishResponse.getMapParameters();
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.LethalIncidentsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AnnualTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntibioticsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntiparasiticTable);
		
		
		String yearsValue = "";
		for (String year : viewAnnualController.getSelectedYears()) {
			yearsValue+=year+"#";
		}
		yearsValue = yearsValue.substring(0, yearsValue.length()-1);
		
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_ALLYEARS_PARAM+"="+ yearsValue +";");
		
		//dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_ALLYEARS_PARAM+"="+ PerformFishAnalyticsConstant.DM_VALUE_ALL +";");
		//dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_ALLYEARS_PARAM+"=2016;");
		
		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final HTMLPanel panelContainer = submitRequestPanel.getContainerPanel();
		final LoaderIcon loaderIcon = new LoaderIcon("Submitting request to "+algorithm.getTitle()+"...");
		loaderIcon.setVisible(true);
		panelContainer.setVisible(true);
		panelContainer.add(loaderIcon);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loaderIcon.setFocus(true);
		}});

		PerformFishAnalyticsServiceAsync.Util.getInstance().callDMServiceToLoadSynopticAnnualTable(thePerformFishResponse, mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				loaderIcon.setVisible(false);
				checkTabSpinner(submitRequestPanel, tab);
				//field_unary_algorithm.setVisible(true);
				GWT.log("I'm displaying: "+dmResponse);
				displayOutputFilesAsStaticEntities(dmResponse, algorithm, null, null, null, panelContainer, false);
			}

			@Override
			public void onFailure(Throwable caught) {
				loaderIcon.setVisible(false);
				checkTabSpinner(submitRequestPanel, tab);
				displayOutputFilesAsStaticEntities(null, algorithm, null, null, null, panelContainer, true);

			}
		});

	}

	/**
	 * Submit request to DM.
	 *
	 * @param dmAlgorithm the dm algorithm
	 */
	private void submitRequestToDM(DataMinerAlgorithms dmAlgorithm) {
		
		switch (dmAlgorithm) {
		
		case BOXPLOT:
			//UNARY
			callAlgorithm(dmAlgorithm, theFocusValue, viewAnnualController.getSelectedKPIs(), null);
			break;

		case SCATTER:
			if(viewAnnualController.getSelectedKPIs().size()==2)
				callAlgorithm(dmAlgorithm, theFocusValue, viewAnnualController.getSelectedKPIs(), null);
			else
				Window.alert("Something seems wrong... You must select exactly two KPIs to execute the "+dmAlgorithm);
			break;

		case CORRELATION:
			//callDataMinerServiceForChartTypeCorrelation(thePerformFishResponse, viewController.getRequestMapParameters());
			callDataMinerServiceForChartTypeCorrelation(theFocusValue, viewAnnualController.getSelectedKPIs(), viewAnnualController.getSelectedKPIs());
			//callAlgorithm(dmAlgorithm, theFocusValue, viewAnnualController.getSelectedKPIs(), null);
			break;

		case SPEEDOMETER:
			//UNARY
			callAlgorithm(dmAlgorithm, theFocusValue, viewAnnualController.getSelectedKPIs(), null);
			break;

		default:
			break;
		}
		
	}

	/**
	 * Call data miner service for chart type correlation.
	 *
	 * @param focusID the focus ID
	 * @param inputKPI the input KPI
	 * @param outputKPI the output KPI
	 */
	private void callDataMinerServiceForChartTypeCorrelation(String focusID, final List<KPI> inputKPI, final List<KPI> outputKPI) {

		DataMinerInputParameters dmInputParameters = new DataMinerInputParameters(thePerformFishResponse, viewAnnualController.getSelectedYears(), inputKPI, outputKPI, DataMinerAlgorithms.CORRELATION, focusID);
		
		GWT.log("Building DM request with input parameters: "+dmInputParameters);

		StringBuilder dataInputsFormatter = new StringBuilder();
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_CHARTTYPE_PARAM+"="+ dmInputParameters.getChartType() +";");
		
		String yearsValue = "";
		for (String year : dmInputParameters.getSelectedYears()) {
			yearsValue+=year+"|";
		}
		yearsValue = yearsValue.substring(0, yearsValue.length()-1);
		
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_YEARS_PARAM+"="+ yearsValue +";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"="+dmInputParameters.getFocusID()+";");
		//dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"=ID;");

		if(dmInputParameters.getInputKPI()!=null && dmInputParameters.getInputKPI().size()>0){
			String kpiCodes = "";
			for (KPI kpi : dmInputParameters.getInputKPI()) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Input KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_INPUT_KPI_PARAM+"="+kpiCodes+";");

		}

		if(dmInputParameters.getOutputKPI()!=null && dmInputParameters.getOutputKPI().size()>0){
			String kpiCodes = "";
			for (KPI kpi : dmInputParameters.getOutputKPI()) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Output KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_OUTPUT_KPI_PARAM+"="+kpiCodes+";");
		}
		
		Map<String, String> performFishResponseMap = dmInputParameters.getPerformFishResponse().getMapParameters();
		
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.LethalIncidentsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AnnualTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntibioticsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntiparasiticTable);
		
		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		final Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final SubmitRequestPanel submitRequestPanel = new SubmitRequestPanel("", 1);
		submitRequestPanel.showLoader(true, "Submitting "+DataMinerAlgorithms.CORRELATION.getName()+" request to DM Service...");
		String tabTitle = DataMinerAlgorithms.CORRELATION.getName().substring(0,1).toUpperCase()+DataMinerAlgorithms.CORRELATION.getName().toLowerCase().substring(1, DataMinerAlgorithms.CORRELATION.getName().length()); //CAMEL CASE
		
		
		final Tab tab = viewAnnualController.createTab(tabTitle+" #"+(viewAnnualController.currentNumberOfTab()+1),PerformFishResources.INSTANCE.farm_CORRELATION().getText(),submitRequestPanel);

		PerformFishAnalyticsServiceAsync.Util.getInstance().callingDataMinerPerformFishAnnualCorrelationAnalysis(dmInputParameters.getPerformFishResponse(), mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				submitRequestPanel.showLoader(false, null);
				checkTabSpinner(submitRequestPanel, tab);
				AnalyticsAnnualPanelResult analyticsPanelResult = new AnalyticsAnnualPanelResult(eventBus);
				//analyticsPanelResult.addSelectedAreas(performFishRequestParameters.get(PerformFishAnalyticsConstant.PERFORM_FISH_AREA_PARAM));
				analyticsPanelResult.addSelectedKPIs(inputKPI);
				
				//TODO COMMENTED NOW
				analyticsPanelResult.addListBatchIds(Arrays.asList(theFocusValue));
				
				analyticsPanelResult.addParameters(PerformFishAnalyticsConstant.DATA_INPUTS, mapParameters, viewAnnualController.getForm().getBatchType());
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
					//analyticsPanelResult.addSelectedAreas(performFishRequestParameters.get(PerformFishAnalyticsConstant.PERFORM_FISH_AREA_PARAM));
					analyticsPanelResult.addSelectedKPIs(viewAnnualController.getSelectedKPIs());
					
					//TODO COMMENTED NOW
					analyticsPanelResult.addListFocusIds(Arrays.asList(theFocusValue));
					
					
					analyticsPanelResult.addParameters(PerformFishAnalyticsConstant.DATA_INPUTS, mapParameters, viewAnnualController.getForm().getBatchType());
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

//		List<String> listBatchID = viewController.getSelectedBatchID();
//
//		if(listBatchID==null || listBatchID.size()==0)
//			Window.alert("Something seems wrong, no selected BatchID, try again");

		SubmitRequestPanel submitRequestPanel = new SubmitRequestPanel("", 1);
		//submitRequestPanel.getElement().addClassName("ext-horizontal-panel");
		String tabTitle = algorithm.getTitle().substring(0,1).toUpperCase()+algorithm.getTitle().toLowerCase().substring(1, algorithm.getTitle().length()); //CAMEL CASE
		
		//TO MANAGE ALGORITHMS DESCRIPTION
		TextResource algDescr = null;
		switch(algorithm) {
			case BOXPLOT:
				algDescr = PerformFishResources.INSTANCE.farm_BOXPLOT();
				break;
			case CORRELATION:
				algDescr = PerformFishResources.INSTANCE.farm_CORRELATION();
				break;
			case DEA_ANALYSIS:
				break;
			case DEA_CHART:
				break;
			case PERFORMFISH_SYNOPTIC_TABLE_FARM:
				break;
			case PERFORMFISH_SYNOPTICTABLE_BATCH:
				break;
			case SCATTER:
				algDescr = PerformFishResources.INSTANCE.farm_SCATTER();
				break;
			case SPEEDOMETER:
				algDescr = PerformFishResources.INSTANCE.farm_SPEEDOMETER();
				break;	
		}
		
		String algDesrTxt = algDescr!=null?algDescr.getText():null;
		
		Tab tab = viewAnnualController.createTab(tabTitle+" #"+(viewAnnualController.currentNumberOfTab()+1),algDesrTxt,submitRequestPanel);

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
				
				DataMinerInputParameters dmInputParams = new DataMinerInputParameters(thePerformFishResponse, viewAnnualController.getSelectedYears(), Arrays.asList(kpi), null, algorithm, focusID);
				callDataMinerServiceForChart(dmInputParams, submitRequestPanel, hp, tab);
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

		default:{
			DataMinerInputParameters dmInputParams = new DataMinerInputParameters(thePerformFishResponse, viewAnnualController.getSelectedYears(), inputKPI, outputKPI, algorithm, focusID);
			callDataMinerServiceForChart(dmInputParams, submitRequestPanel, submitRequestPanel.getContainerPanel(), tab);
			//callDataMinerServiceForChart(thePerformFishResponse, POPULATION_LEVEL.BATCH, inputKPI, outputKPI, algorithm, focusID, submitRequestPanel, submitRequestPanel.getContainerPanel(), tab);
			}
		}
	}


	/**
	 * Call data miner service for chart.
	 *
	 * @param dmInputParameters the dm input parameters
	 * @param requestPanel the request panel
	 * @param panelContainer the panel
	 * @param tab the tab
	 */
	private void callDataMinerServiceForChart(final DataMinerInputParameters dmInputParameters, final SubmitRequestPanel requestPanel, final ComplexPanel panelContainer, final Tab tab) {

		GWT.log("Building DM request with input parameters: "+dmInputParameters);

		StringBuilder dataInputsFormatter = new StringBuilder();
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_CHARTTYPE_PARAM+"="+ dmInputParameters.getChartType() +";");
		
		String yearsValue = "";
		for (String year : dmInputParameters.getSelectedYears()) {
			yearsValue+=year+"|";
		}
		yearsValue = yearsValue.substring(0, yearsValue.length()-1);
		
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_YEARS_PARAM+"="+ yearsValue +";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"="+dmInputParameters.getFocusID()+";");
		//dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"=ID;");

		if(dmInputParameters.getInputKPI()!=null && dmInputParameters.getInputKPI().size()>0){
			String kpiCodes = "";
			for (KPI kpi : dmInputParameters.getInputKPI()) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Input KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_INPUT_KPI_PARAM+"="+kpiCodes+";");

		}

		if(dmInputParameters.getOutputKPI()!=null && dmInputParameters.getOutputKPI().size()>0){
			String kpiCodes = "";
			for (KPI kpi : dmInputParameters.getOutputKPI()) {
				kpiCodes+=kpi.getCode()+"|";
			}
			//remove last |
			kpiCodes = kpiCodes.substring(0, kpiCodes.length()-1);

			GWT.log("Output KPICodes: "+kpiCodes);
			//ADDING KPIs code
			dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_OUTPUT_KPI_PARAM+"="+kpiCodes+";");
		}
		
		Map<String, String> performFishResponseMap = dmInputParameters.getPerformFishResponse().getMapParameters();
		
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.LethalIncidentsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AnnualTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntibioticsTable);
		dataInputsFormatter = appendDMInputTable(performFishResponseMap, dataInputsFormatter, PerformFishAnalyticsConstant.PFSERVICE_TO_DM_MAPPING_TABLE.AntiparasiticTable);
		
		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final LoaderIcon loaderIcon = new LoaderIcon("Submitting request to "+dmInputParameters.getChartType().getTitle()+" Analysis...");
		loaderIcon.setVisible(true);
		panelContainer.setVisible(true);
		panelContainer.add(loaderIcon);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loaderIcon.setFocus(true);
		}});

		PerformFishAnalyticsServiceAsync.Util.getInstance().callingDataMinerPerformFishAnnualAnalysis(mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				loaderIcon.setVisible(false);
				checkTabSpinner(requestPanel, tab);
				//field_unary_algorithm.setVisible(true);
				GWT.log("I'm displaying: "+dmResponse);
				displayOutputFilesAsStaticEntities(dmResponse, dmInputParameters.getChartType(), dmInputParameters.getInputKPI(), dmInputParameters.getOutputKPI(), dmInputParameters.getFocusID(), panelContainer, false);
			}

			@Override
			public void onFailure(Throwable caught) {
				loaderIcon.setVisible(false);
				checkTabSpinner(requestPanel, tab);
				displayOutputFilesAsStaticEntities(null, dmInputParameters.getChartType(), dmInputParameters.getInputKPI(), dmInputParameters.getOutputKPI(), dmInputParameters.getFocusID(), panelContainer, true);

			}
		});
	}
	
	
	/**
	 * Append DM input table.
	 *
	 * @param performFishResponseMap the perform fish response map
	 * @param dataInputsFormatter the data inputs formatter
	 * @param table the table
	 * @return the string builder
	 */
	private StringBuilder appendDMInputTable(Map<String, String> performFishResponseMap, StringBuilder dataInputsFormatter, PFSERVICE_TO_DM_MAPPING_TABLE table) {
		
		String toDMInputTable = performFishResponseMap.get(table.getPerformFishTable());
		
		if(toDMInputTable!=null && !toDMInputTable.isEmpty()) {
			dataInputsFormatter.append(table.getDataMinerTable()+"="+toDMInputTable+";");
		}
		
		return dataInputsFormatter;
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
			viewAnnualController.noSpinner(tab);
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
	private void displayOutputFilesAsStaticEntities(final DataMinerResponse dmResponse, final DataMinerAlgorithms chartType, List<KPI> inputKPIs, List<KPI> outputKPIs, final String focusID, final Panel container, boolean displayError){

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
							//title = chartType.getName()+" - on all batches<br>";
							//title += "Blue dots indicate the selected batch(es): "+focusID;
							title = chartType.getTitle()+" - on all farm data<br>";
							title += "Blue dots indicate the selected farm: "+focusID;
							
							ShowResult showResult = new ShowResult(title);
							showResult.showImage(base64Content);
							displayingOrderedBoxPlot(dmResponse, title, container, showResult);
							return;
						case CORRELATION:
							break;
						case DEA_ANALYSIS:
							break;
						case SCATTER:
							break;
						case SPEEDOMETER:
							title = chartType.getTitle()+"<br>";
							title+= "Selected Farm: "+focusID+"<br>";
							title+= "Normalized with respect to all farm data in the VRE";
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
						String cssTableStyle = "simpletable";
						String title = toTitle;
						switch (chartType) {
						case BOXPLOT:
							if(result.getFileName().contains("_user")){
								title = "My Batch(es)";
								return;
							}else{
								title = "All farm data in the VRE (including mine)";
							}
							title+="<br>";
							title+="Data aggregation is performed by considering farms as base units";
							
							ShowResult showResult = new ShowResult(title);
							showResult.showCSVFile(result, cssTableStyle);
							displayingOrderedBoxPlot(dmResponse, title, container, showResult);
							return;
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
						case PERFORMFISH_SYNOPTIC_TABLE_FARM:
							if(!result.getFileName().contains("legend")) {
								cssTableStyle = "synoptictable-farm";
							}else {
								cssTableStyle = "simpletable-synoptic";
							}
							ShowResult showResultSin = new ShowResult(title);
							showResultSin.showCSVFile(result, cssTableStyle);
							displayingOrderedSynopticTable(dmResponse, result.getFileName(), container, showResultSin);
							
							return;
						}

						ShowResult showResult = new ShowResult(title);
						showResult.showCSVFile(result, cssTableStyle);
						container.add(showResult);
					}
				});
				break;

			default:
				break;
			}

		}

	}
	
	/**
	 * Displaying ordered synoptic table.
	 *
	 * @param dmResponse the dm response
	 * @param fileName the file name
	 * @param container the container
	 * @param showResult the show result
	 */
	private void displayingOrderedSynopticTable(final DataMinerResponse dmResponse, String fileName, final Panel container, ShowResult showResult) {
		int hashcode = dmResponse.hashCode();
		GWT.log("The hascode is: "+hashcode);
		FlexTable flex = synopticOrderBy.get(hashcode);
		
		if(flex==null) {
			GWT.log("The flextable is null");
			flex = new FlexTable();
			synopticOrderBy.put(hashcode, flex);
			container.add(flex);
		}
		
		if(fileName.toLowerCase().contains("legend")) {
			flex.setWidget(0, 0, showResult);
		}else {
			flex.setWidget(flex.getRowCount()+1,0,showResult);
		}
	}
	
	
	/**
	 * Displaying ordered box plot.
	 *
	 * @param dmResponse the dm response
	 * @param title the title
	 * @param container the container
	 * @param showResult the show result
	 */
	private void displayingOrderedBoxPlot(final DataMinerResponse dmResponse, String title, final Panel container, ShowResult showResult) {
		int hashcode = dmResponse.hashCode();
		GWT.log("The hascode is: "+hashcode);
		FlexTable flex = boxPlotOrderBy.get(hashcode);
		
		if(flex==null) {
			GWT.log("The flextable is null");
			flex = new FlexTable();
			boxPlotOrderBy.put(hashcode, flex);
			container.add(flex);
		}
		
		if(title.startsWith("My")) {
			flex.setWidget(0, 0, showResult);
		}else if (title.startsWith("All")){
			flex.setWidget(0, 1, showResult);
		}else {
			flex.setWidget(0, 2, showResult);
		}
	}
}
