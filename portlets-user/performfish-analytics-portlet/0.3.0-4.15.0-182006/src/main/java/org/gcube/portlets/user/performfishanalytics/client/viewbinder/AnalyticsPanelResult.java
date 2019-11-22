/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsServiceAsync;
import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.event.CallAlgorithmEvent;
import org.gcube.portlets.user.performfishanalytics.client.view.LoaderIcon;
import org.gcube.portlets.user.performfishanalytics.client.view.util.CorrelationValueToColourUtil;
import org.gcube.portlets.user.performfishanalytics.shared.FileContentType;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class AnalyticsPanelResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 29, 2019
 */
public class AnalyticsPanelResult extends Composite {

	/**
	 *
	 */


	private static AnalyticsPanelResultUiBinder uiBinder =
		GWT.create(AnalyticsPanelResultUiBinder.class);

	/**
	 * The Interface AnalyticsPanelResultUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Jan 30, 2019
	 */
	interface AnalyticsPanelResultUiBinder
		extends UiBinder<Widget, AnalyticsPanelResult> {
	}

	@UiField
	HTMLPanel field_html_panel;

	@UiField
	VerticalPanel field_parameters_container;

	@UiField
	VerticalPanel uib_vp_correlation_results_container;

	@UiField
	VerticalPanel field_unary_algorithm_container;

	@UiField
	VerticalPanel field_binary_algorithm;

	@UiField
	VerticalPanel field_binary_algorithm_container;

	@UiField
	VerticalPanel field_unary_algorithm;

	@UiField
	ControlGroup cg_list_dea_input_kpi;

	@UiField
	ControlGroup cg_list_dea_output_kpi;

	@UiField
	ListBox list_dea_input_kpi;

	@UiField
	ListBox list_dea_output_kpi;

	@UiField
	Label uib_label_focus_id;

//	@UiField
//	ControlGroup cg_focus_id_dea;

	@UiField
	HorizontalPanel uib_vp_deanalanlysis_request_container;

	@UiField
	ControlGroup cg_focus_id_correlation;

//	@UiField
//	ListBox field_list_focus_id_dea;

	@UiField
	VerticalPanel uib_vp_deanalanlysis_algorithm;

	@UiField
	VerticalPanel uib_vp_deanalanlysis_algorithm_container;

	@UiField
	ListBox field_list_focus_id_correlation;

	@UiField
	Button button_dea_analys_request;

	@UiField
	CheckBox uib_check_all_input_kpi;

	@UiField
	CheckBox uib_check_all_output_kpi;

	private Map<String,String> dataInputParameters;

	private DataMinerResponse dmResponse;

	private Map<String,List<KPI>> kpiMapPointers = new HashMap<String, List<KPI>>();

	private List<KPI> selectedKPIs;

	private List<String> selectedAreas;
	
	private Map<String, CSVFile> csvGenerated = new HashMap<String, CSVFile>();

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public AnalyticsPanelResult() {

		initWidget(uiBinder.createAndBindUi(this));

		button_dea_analys_request.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				cg_list_dea_input_kpi.setType(ControlGroupType.NONE);
				cg_list_dea_output_kpi.setType(ControlGroupType.NONE);

				if(list_dea_input_kpi.getSelectedIndex()==-1){
					showAlert("You must select at least one Input KPI", AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);
					cg_list_dea_input_kpi.setType(ControlGroupType.ERROR);
					return;
				}

				if(list_dea_output_kpi.getSelectedIndex()==-1){
					showAlert("You must select at least one Output KPI", AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);
					cg_list_dea_output_kpi.setType(ControlGroupType.ERROR);
					return;
				}

				List<String> inputKPINames = getSelected(list_dea_input_kpi);
				List<String> outputKPINames = getSelected(list_dea_output_kpi);
				//callDeaAnalysis(inputKPINames, outputKPINames, button_dea_analys_request);
			}
		});

		uib_check_all_input_kpi.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_input_kpi.getValue();
				selectAllFields(list_dea_input_kpi, isChecked);
			}
		});

		uib_check_all_output_kpi.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_output_kpi.getValue();
				selectAllFields(list_dea_output_kpi, isChecked);
			}
		});
	}

	/**
	 * Select all fields.
	 *
	 * @param listBox the list box
	 * @param selected the selected
	 */
	private void selectAllFields(ListBox listBox, boolean selected){

		for (int i=0; i<listBox.getItemCount(); i++) {
			listBox.setItemSelected(i, selected);
		}

	}


	/**
	 * Gets the selected.
	 *
	 * @param listBox the list box
	 * @return the selected
	 */
	private List<String> getSelected(ListBox listBox){
		List<String> selected = new ArrayList<String>();
		for (int i=0; i<listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				selected.add(listBox.getItemText(i));
	        }
		}
		return selected;
	}



	/**
	 * Adds the selected areas.
	 *
	 * @param listAreas the list areas
	 */
	public void addSelectedAreas(List<String> listAreas) {

		this.selectedAreas = listAreas;

	}


	/**
	 * Gets the data miner response.
	 *
	 * @return the data miner response
	 */
	public DataMinerResponse getDataMinerResponse(){
		return dmResponse;
	}



	/**
	 * Gets the data input parameters.
	 *
	 * @return the data input parameters
	 */
	public Map<String, String> getDataInputParameters() {

		return dataInputParameters;
	}

	/**
	 * Show alert.
	 *
	 * @param error the error
	 * @param type the type
	 * @param closable the closable
	 * @param panel the panel
	 */
	private void showAlert(String error, AlertType type, boolean closable, ComplexPanel panel){
		Alert alert = new Alert(error);
		alert.setType(type);
		alert.setClose(closable);
		alert.getElement().getStyle().setMargin(10, Unit.PX);
		panel.add(alert);
	}


	/**
	 * Adds the selected kp is.
	 *
	 * @param selectedKPIs the selected kp is
	 */
	public void addSelectedKPIs(List<KPI> selectedKPIs) {

		this.selectedKPIs = selectedKPIs;

	}
	
	/**
	 * Gets the KPI for name.
	 *
	 * @param name the name
	 * @return the KPI for name
	 */
	public KPI getKPIForName(String name){

		GWT.log("Searching KPI name: "+name);
		KPI foundKPI = null;
		String purgedName = name.trim();
		for (KPI kpi : selectedKPIs) {
			String purgedKPIName = kpi.getName().trim();
			if(purgedKPIName.compareToIgnoreCase(purgedName)==0) {
				foundKPI = kpi;
				break;
			}
		}
		GWT.log("FOUND KPI: "+foundKPI);
		return foundKPI;
	}
	
	/**
	 * Gets the KPI for name.
	 *
	 * @param name the name
	 * @return the KPI for name
	 */
	/*public KPI getKPIForName(String name){

		//GWT.log("Selected KPIs: "+selectedKPIs);
		GWT.log("Searching KPI name: "+name);
		KPI foundKPI = null;
		String purgedName = name.replaceAll("\\%", "").trim();
		GWT.log("Searching pureged KPI name: "+purgedName);
		//String purgedName = name.trim();
		for (KPI kpi : selectedKPIs) {
			String purgedKPIName = kpi.getName().replaceAll("\\%", "").trim();
			//String purgedKPIName = kpi.getName().trim();
			GWT.log("The purged KPI name: "+purgedName);
			if(purgedKPIName.compareToIgnoreCase(purgedName)==0) {
				foundKPI = kpi;
				break;
			}
				
		}
		GWT.log("FOUND KPI: "+foundKPI);
		return foundKPI;
	}*/

	/**
	 * Adds the parameters.
	 *
	 * @param keyToGet the key to get
	 * @param parameters the parameters
	 * @param toShowBatchTypeValue label to show batch type value
	 */
	public void addParameters(String keyToGet, Map<String, List<String>> parameters, String toShowBatchTypeValue) {


		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("colgrouptable");

		try{
			List<String> dataInputs = parameters.get(keyToGet);

			if(dataInputs==null || dataInputs.isEmpty())
				return;

			dataInputParameters = new HashMap<String, String>();

			String theDataInputs = dataInputs.get(0);
			String[] splittedParams = theDataInputs.split(";");

			for (String splitParam : splittedParams) {
				try{
					String[] keyvalue = splitParam.split("=");
					dataInputParameters.put(keyvalue[0], keyvalue[1]);
				}catch(Exception e){

				}
			}

			flexTable.setWidget(0, 0, new HTML(dataInputParameters.get(PerformFishAnalyticsConstant.DM_SCALEP_PARAM)));
			flexTable.setWidget(1, 0, new HTML(toShowBatchTypeValue));

			String KPINames = "";
			for (KPI kpi: selectedKPIs) {
				KPINames+=kpi.getName() +", ";
			}
			KPINames = KPINames.substring(0, KPINames.length()-2);
			flexTable.setWidget(2,0, new HTML(KPINames));

			fillDeaListBoxes();

			//CHANGING LABEL TEXT TO SCAPE_PARAM
			uib_label_focus_id.setText(dataInputParameters.get(PerformFishAnalyticsConstant.DM_SCALEP_PARAM) + " ID");

		}catch(Exception e){
			//silent
		}

		field_parameters_container.add(flexTable);
	}


	/**
	 * Fill dea list boxes.
	 */
	private void fillDeaListBoxes(){

		for (KPI kpi: selectedKPIs) {
			list_dea_input_kpi.addItem(kpi.getName());
			list_dea_output_kpi.addItem(kpi.getName());
		}
	}

	
	/**
	 * Adds the list focus ids.
	 *
	 * @param listFocusIDs the list focus I ds
	 */
	public void addListFocusIds(List<String> listFocusIDs){
		for (String batchID : listFocusIDs) {
			field_list_focus_id_correlation.addItem(batchID, batchID);
		}
	}

	/**
	 * Adds the results.
	 *
	 * @param dmResponse the dm response
	 */
	public void addResults(DataMinerResponse dmResponse) {
		this.dmResponse = dmResponse;
		
		for (final OutputFile outputFile : dmResponse.getListOutput()) {
			
			if(outputFile.getDataType().equals(FileContentType.CSV)){
				PerformFishAnalyticsServiceAsync.Util.getInstance().getCSVFile(outputFile, true, new AsyncCallback<CSVFile>() {

					@Override
					public void onFailure(Throwable caught) {
						showAlert(caught.getMessage(), AlertType.ERROR, true, uib_vp_correlation_results_container);

					}

					@Override
					public void onSuccess(CSVFile result) {
						
						csvGenerated.put(result.getFileName(), result);
						fillCorrelationMatrix();
					}
				});
			}
			
		}
	}
	
	private void fillCorrelationMatrix() {
		
		if(csvGenerated.size()<2)
			return;
		
		String corrIndexFilename = null;
		String corrFilename = null;
		for (String fileName : csvGenerated.keySet()) {
			if(fileName.contains("index")) {
				corrIndexFilename = fileName;
			}else {
				corrFilename = fileName;
			}
		}
		
		GWT.log("Correlation Matrix Index File: "+corrIndexFilename);
		GWT.log("Correlation Matrix File: "+corrFilename);
		CSVFile corrIndexCsvFile = csvGenerated.get(corrIndexFilename);
		CSVFile corrCsvFile = csvGenerated.get(corrFilename);
		GWT.log("Correlation Matrix Index CSV: "+corrIndexCsvFile);
		GWT.log("Correlation Matrix CSV: "+corrCsvFile);

		HorizontalPanel hp = new HorizontalPanel();
		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("fixedtable");
		flexTable.getElement().getStyle().setMarginBottom(10, Unit.PX);

		flexTable.setWidget(0, 0,new Label(""));
		
		
		CSVRow headerRow = corrIndexCsvFile.getHeaderRow();
		//HEADER
		for (int i=1; i<headerRow.getListValues().size(); i++) {

			final String headerValue = headerRow.getListValues().get(i);
//							final Button button = new Button(headerValue);
//							button.setType(ButtonType.LINK);
			HTML label = new HTML(headerValue);
			label.getElement().getStyle().setFontSize(16, Unit.PX);

			final int columnIndex = i;
			KPI kpi = getKPIForName(headerValue);
			fillKPIReferenceForIndex(0, columnIndex, Arrays.asList(kpi));
//							button.addClickHandler(new ClickHandler() {
//
//								@Override
//								public void onClick(ClickEvent event) {
//									//calling unary operator
//									callBoxPlotAndSpeedoMeter(0,columnIndex,button);
//
//								}
//							});
			flexTable.setWidget(0, i,label);
		}
		//DATA
		for (int i=0; i<corrIndexCsvFile.getValueRows().size(); i++) {
			CSVRow row = corrIndexCsvFile.getValueRows().get(i);
			CSVRow rowMatrixColor = corrCsvFile.getValueRows().get(i);
			final int rowIndex = i+1; //adding +1 for header row
			for (int j=0; j<row.getListValues().size(); j++) {
				final String rowValue = row.getListValues().get(j);
				final String rowMatrixColorValue = rowMatrixColor.getListValues().get(j);
				final String theColor = CorrelationValueToColourUtil.getRGBColor(rowMatrixColorValue);
				final int columnIndex = j;
				//final Button button = new Button(rowValue);
				final HTML buttonHTML = new HTML(rowValue);
				buttonHTML.addStyleName("my-active-html");
//								button.setType(ButtonType.LINK);
//								button.setSize(ButtonSize.LARGE);

				//only the first column
				if(j==0){
					HTML label = new HTML(rowValue);
					label.getElement().getStyle().setFontSize(16, Unit.PX);
					flexTable.setWidget(rowIndex, j,label);
					//rowValue is a KPI name
					KPI kpi = getKPIForName(rowValue);
					fillKPIReferenceForIndex(rowIndex, columnIndex, Arrays.asList(kpi));
					continue;
					//rowValue is a KPI name
//									KPI kpi = getKPIForName(rowValue);
//									fillKPIReferenceForIndex(rowIndex, columnIndex, Arrays.asList(kpi));
//									button.addClickHandler(new ClickHandler() {
//
//										@Override
//										public void onClick(ClickEvent event) {
//											//calling unary operator
//											callBoxPlotAndSpeedoMeter(rowIndex, columnIndex, button);
//
//										}
//									});
				}
				//diagonal
				else if(rowIndex==j){
					//rowValue should be 1
					//HTML dg = new HTML("1");
					HTML dg = new HTML(rowValue);
					dg.getElement().getStyle().setFontSize(18, Unit.PX);
					flexTable.setWidget(rowIndex, j,dg);
					continue;
				//j > 0
				}else{
					KPI columnKPI = getKPIForName(headerRow.getListValues().get(columnIndex));
					//Here the first index is the KPI name
					KPI rowKPI = getKPIForName(row.getListValues().get(0));
					fillKPIReferenceForIndex(rowIndex, columnIndex, Arrays.asList(columnKPI, rowKPI));
					//button.setText("                       ");
					//button.setIcon(IconType.COMPASS);
					//button.setSize(ButtonSize.LARGE);

					buttonHTML.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							//callScatterAndDeaChart(rowIndex, columnIndex, button);
							callScatter(rowIndex, columnIndex, buttonHTML);
						}
					});
				}

				//button.setType(ButtonType.LINK);
				//APPLYING STYLE ONLY ON INTERNAL VALUES OF CSV
				if(j>0)
					buttonHTML.getElement().getStyle().setColor("#000");

				buttonHTML.addAttachHandler(new AttachEvent.Handler() {

					  @Override
					  public void onAttachOrDetach(AttachEvent event) {
						  if(theColor.startsWith("#")){
							  GWT.log("Setting backgrounf color: "+theColor);
							  buttonHTML.getElement().getParentElement().getStyle().setBackgroundColor(theColor);
						  }
					  }
				});

				flexTable.setWidget(rowIndex, j,buttonHTML);
			}

		}

		hp.add(flexTable);

		final FlexTable flexLegend = new FlexTable();
		flexLegend.setStyleName("simpletable");
		//flexLegend.getElement().getStyle().setBorderWidth(0, Unit.PX);
		Map<String, String> map = CorrelationValueToColourUtil.getMap();
		flexLegend.setWidget(0, 0, new Label("Legend"));
		flexLegend.setWidget(0, 1, new HTML(""));
		int i = 1;
		for (String key : map.keySet()) {
			final String rgbColor = map.get(key);
			final HTML theLegendColor = new HTML("");
			flexLegend.setWidget(i, 0, theLegendColor);
			flexLegend.setWidget(i, 1, new HTML(key));
			theLegendColor.addAttachHandler(new AttachEvent.Handler() {

				  @Override
				  public void onAttachOrDetach(AttachEvent event) {
					  theLegendColor.getElement().getParentElement().getStyle().setBackgroundColor(rgbColor);
				  }
			});

			i++;
		}

		flexLegend.getElement().getStyle().setMarginLeft(15, Unit.PX);
		hp.add(flexLegend);
		uib_vp_correlation_results_container.insert(hp,0);
		
	}

	/**
	 * Fill kpi reference for index.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param listKPI the list kpi
	 */
	private void fillKPIReferenceForIndex(int rowIndex, int columnIndex, List<KPI> listKPI){

		String key = generateKey(rowIndex, columnIndex);
		kpiMapPointers.put(key, listKPI);

//		String kpiNames = "";
//		if(listKPI!=null && listKPI.size()>0){
//			for (KPI kpi : listKPI) {
//
//				if(kpi!=null){
//					kpiNames+=" "+kpi.getName() +";";
//				}else
//					GWT.log("KPI NULL for "+key);
//
//
//			}
//			GWT.log("FILLING kpiMapPointers with key: "+key +" and value: "+kpiNames);
//		}else
//			GWT.log("FILLING kpiMapPointers with key: "+key +" and value: "+listKPI);

	}

	/**
	 * Gets the KPI for indexes.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the KPI for indexes
	 */
	public List<KPI> getKPIForIndexes(int rowIndex, int columnIndex){
		String key = generateKey(rowIndex, columnIndex);
		return kpiMapPointers.get(key);
	}
	
	/**
	 * Generate key.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the string
	 */
	private String generateKey(int rowIndex, int columnIndex) {
		return rowIndex+"-"+columnIndex;
	}


	/**
	 * Check valid focus id.
	 *
	 * @return the FocusID if it is valid, null otherwise.
	 */
	public String checkValidFocusID(){

		cg_focus_id_correlation.setType(ControlGroupType.NONE);

		//CHECK THE FOCUS ID VALUE
		String focusID = field_list_focus_id_correlation.getSelectedItemText();

		if(focusID==null || focusID.isEmpty()){

			String msgError = "Could not execute a valid Analysis.";

			if(selectedAreas==null || selectedAreas.isEmpty()){
				msgError+=" Please select another parameters computation";
			}else{
				msgError+=" Select at least the Area of your FARM";
			}

			showAlert(msgError, AlertType.ERROR, true, uib_vp_correlation_results_container);
			cg_focus_id_correlation.setType(ControlGroupType.ERROR);
			return null;
		}

		return focusID;
	}


//	/**
//	 * Call scatter and dea chart.
//	 *
//	 * @param rowIndex the row index
//	 * @param columnIndex the column index
//	 * @param button the button
//	 */
//	private void callScatterAndDeaChart(int rowIndex, int columnIndex, Button button){
//
//		String focusID = checkValidFocusID();
//
//		if(focusID==null)
//			return;
//
//		GWT.log("Called ScatterAndDeaChart at rowIndex: "+rowIndex +", columnIndex: "+columnIndex);
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.getElement().addClassName("ext-horizontal-panel");
//		HorizontalPanel scatter = new HorizontalPanel();
//		HorizontalPanel deaChart = new HorizontalPanel();
//		hp.add(scatter);
//		hp.add(deaChart);
//		//hp.getElement().addClassName("ext-horizontal-panel");
//		field_binary_algorithm_container.add(hp);
//		List<KPI> selectedKPI = getKPIForIndexes(rowIndex, columnIndex);
//		GWT.log("Selected KPI: "+selectedKPI);
//		field_binary_algorithm.setVisible(true);
//		callDataMinerServiceForChart(dataInputParameters, selectedKPI, null, DataMinerAlgorithms.SCATTER, focusID, scatter);
//		callDataMinerServiceForChart(dataInputParameters, selectedKPI, null, DataMinerAlgorithms.DEA_CHART, focusID, deaChart);
//	}


	/**
	 * Call scatter.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param button the button
	 */
	private void callScatter(int rowIndex, int columnIndex, HTML button){

		String focusID = checkValidFocusID();

		if(focusID==null)
			return;

		GWT.log("Called ScatterChart at rowIndex: "+rowIndex +", columnIndex: "+columnIndex);
		List<KPI> selectedKPI = getKPIForIndexes(rowIndex, columnIndex);
		GWT.log("Selected KPI: "+selectedKPI);
		PerformFishAnalyticsController.eventBus.fireEvent(new CallAlgorithmEvent(DataMinerAlgorithms.SCATTER, focusID, selectedKPI, null));
	}


//	/**
//	 * Call dea analysis.
//	 *
//	 * @param inputKPINames the input kpi names
//	 * @param outputKPINames the output kpi names
//	 * @param button the button
//	 */
//	private void callDeaAnalysis(List<String> inputKPINames, List<String> outputKPINames, Button button){
//
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.getElement().addClassName("ext-horizontal-panel");
//		HorizontalPanel deaAnalysis = new HorizontalPanel();
//		hp.add(deaAnalysis);
//		//hp.getElement().addClassName("ext-horizontal-panel");
//		uib_vp_deanalanlysis_algorithm_container.add(hp);
//
//		List<KPI> inputKPI = new ArrayList<KPI>();
//		for (String kpiName : inputKPINames) {
//			inputKPI.add(getKPIForName(kpiName));
//		}
//
//		List<KPI> outputKPI = new ArrayList<KPI>();
//		for (String kpiName : outputKPINames) {
//			outputKPI.add(getKPIForName(kpiName));
//		}
//
//
//		GWT.log("Calling Dea Analysys... with input: "+inputKPI+" and output: "+outputKPI);
//		uib_vp_deanalanlysis_algorithm.setVisible(true);
//		callDataMinerServiceForChart(dataInputParameters, inputKPI, outputKPI, DataMinerAlgorithms.DEA_ANALYSIS, "ID", deaAnalysis);
//	}


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
		title+=chartType.getTitle();
		title+=inputKPIs.size()>1?" [Input KPIs: ":" [Input KPI: ";

		for (KPI kpi : inputKPIs) {
			title+=" "+kpi.getName()+",";
		}

		title = title.substring(0,title.length()-1)+"]";


		if(outputKPIs!=null && outputKPIs.size()>0){
			title+=inputKPIs.size()>1?" [Output KPIs: ":" [Output KPI: ";
			for (KPI kpi : outputKPIs) {
				title+=" "+kpi.getName()+",";
			}
			title = title.substring(0,title.length()-1)+"]";
		}

		//title+= " Focus "+focusID;

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
						showAlert(caught.getMessage(), AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);

					}

					@Override
					public void onSuccess(String base64Content) {

						String title = toTitle;
						switch (chartType) {
						case BOXPLOT:
							break;
						case SPEEDOMETER:
							title+= " "+uib_label_focus_id.getText()+": "+focusID;
							break;
						default:
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
						showAlert(caught.getMessage(), AlertType.ERROR, true, uib_vp_deanalanlysis_request_container);
					}

					@Override
					public void onSuccess(CSVFile result) {
						GWT.log("Displaying: "+result);
						String cssTableStyle = "simpletable";
						String title = toTitle;
						switch (chartType) {
						case BOXPLOT:
							title+= " Statistics on all data";
							break;
						case SPEEDOMETER:
							title+= " "+uib_label_focus_id.getText()+": "+focusID;
							break;
						default:
							break;
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
	 * Call data miner service for chart.
	 *
	 * @param dataInputParameters the data input parameters
	 * @param inputKPI the input kpi
	 * @param outputKPI the output kpi
	 * @param chartType the chart type
	 * @param focusID the focus id
	 * @param panel the panel
	 */
	private void callDataMinerServiceForChart(Map<String, String> dataInputParameters, final List<KPI> inputKPI, final List<KPI> outputKPI, final DataMinerAlgorithms chartType, final String focusID, final ComplexPanel panel) {

		GWT.log("Call DM wiht Selected KPI: "+inputKPI);
		StringBuilder dataInputsFormatter = new StringBuilder();
		String scalePValue = dataInputParameters.get(PerformFishAnalyticsConstant.DM_SCALEP_PARAM);
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_SCALEP_PARAM+"="+scalePValue+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_BATCHTYPE_PARAM+"="+dataInputParameters.get(PerformFishAnalyticsConstant.DM_BATCHTYPE_PARAM)+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_CHARTTYPE_PARAM+"="+chartType+";");
		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FARMFILE_PARAM+"="+dataInputParameters.get(PerformFishAnalyticsConstant.DM_FARMFILE_PARAM)+";");


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

		dataInputsFormatter.append(PerformFishAnalyticsConstant.DM_FOCUS_PARAM+"="+focusID+";");

		String dataInParameters = dataInputsFormatter.toString();
		GWT.log("Calling DM service with client input parameters: "+dataInParameters);

		Map<String, List<String>> mapParameters = new HashMap<String, List<String>>();
		mapParameters.put(PerformFishAnalyticsConstant.DATA_INPUTS, Arrays.asList(dataInParameters));

		final LoaderIcon loaderIcon = new LoaderIcon("Submitting request to "+chartType.getTitle()+" Analysis...");
		loaderIcon.setVisible(true);
		panel.setVisible(true);
		panel.add(loaderIcon);
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loaderIcon.setFocus(true);
		}});

		PerformFishAnalyticsServiceAsync.Util.getInstance().callingDataMinerPerformFishAnalysis(mapParameters, new AsyncCallback<DataMinerResponse>() {

			@Override
			public void onSuccess(DataMinerResponse dmResponse) {
				loaderIcon.setVisible(false);
				//field_unary_algorithm.setVisible(true);
				GWT.log("I'm displaying: "+dmResponse);
				displayOutputFilesAsStaticEntities(dmResponse, chartType, inputKPI, outputKPI, focusID, panel, false);
			}

			@Override
			public void onFailure(Throwable caught) {
				loaderIcon.setVisible(false);
				displayOutputFilesAsStaticEntities(dmResponse, chartType, inputKPI, outputKPI, focusID, panel, true);

			}
		});
	}
}
