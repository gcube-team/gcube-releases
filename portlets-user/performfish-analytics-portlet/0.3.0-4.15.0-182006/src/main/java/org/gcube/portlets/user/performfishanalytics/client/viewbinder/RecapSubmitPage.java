/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadSynopticTableEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SubmitRequestEvent;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class RecapSubmitPage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 21, 2019
 */
public class RecapSubmitPage extends Composite {

	private static RecapSubmitPageUiBinder uiBinder =
		GWT.create(RecapSubmitPageUiBinder.class);

	@UiField
	Button uib_butt_descriptive_statistics;

	@UiField
	Button uib_butt_speedometer;

	@UiField
	Button uib_butt_scatter_plot;

	@UiField
	Button uib_butt_correlation_analysis;

	@UiField
	VerticalPanel recapPanel;

	@UiField
	VerticalPanel errorPanelSubmit;
	
	@UiField
	VerticalPanel synopsisPanel;
	
	@UiField
	Button uib_button_load_synoptic_table;

	private HashMap<String, KPI> mapSelected = new HashMap<String, KPI>();
	private HashMap<String, HTML> mapLabel = new HashMap<String, HTML>();

	private HandlerManager theEventBus;

	private boolean annualAnalysis;

	/**
	 * The Interface RecapSubmitPageUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Jan 22, 2019
	 */
	interface RecapSubmitPageUiBinder extends UiBinder<Widget, RecapSubmitPage> {
	}

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
	 *
	 * @param eventBus the event bus
	 * @param annualAnalysis the annual analysis
	 */
	public RecapSubmitPage(HandlerManager eventBus, final boolean annualAnalysis) {
		this.annualAnalysis = annualAnalysis;
		initWidget(uiBinder.createAndBindUi(this));
		this.theEventBus = eventBus;
		
		recapPanel.getElement().getStyle().setProperty("width", "98%");

		uib_butt_descriptive_statistics.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				DataMinerAlgorithms chartType = DataMinerAlgorithms.valueOf(uib_butt_descriptive_statistics.getName());

				theEventBus.fireEvent(new SubmitRequestEvent(chartType));
			}
		});


		uib_butt_speedometer.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				DataMinerAlgorithms chartType = DataMinerAlgorithms.valueOf(uib_butt_speedometer.getName());

				theEventBus.fireEvent(new SubmitRequestEvent(chartType));
			}
		});

		uib_butt_scatter_plot.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				DataMinerAlgorithms chartType = DataMinerAlgorithms.valueOf(uib_butt_scatter_plot.getName());

				theEventBus.fireEvent(new SubmitRequestEvent(chartType));
			}
		});


		uib_butt_correlation_analysis.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				DataMinerAlgorithms chartType = DataMinerAlgorithms.valueOf(uib_butt_correlation_analysis.getName());

				theEventBus.fireEvent(new SubmitRequestEvent(chartType));
			}
		});
		
		uib_button_load_synoptic_table.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
//				boolean isValidForm = vivalidatePerformFishInputFields();
				theEventBus.fireEvent(new LoadSynopticTableEvent(annualAnalysis));
			
			}
		});

	}
	
	
	/**
	 * Sets the visible load synopsis panel.
	 *
	 * @param b the new visible load synopsis panel
	 */
	public void setVisibleLoadSynopsisPanel(boolean b) {
		synopsisPanel.setVisible(b);
	}
	
	/**
	 * Sets the enable synoptic table.
	 *
	 * @param b the new enable synoptic table
	 */
	public void activeSynopticTable(boolean b) {
		uib_button_load_synoptic_table.setEnabled(b);
	}


	/**
	 * Active box plot.
	 *
	 * @param active the active
	 */
	public void activeBoxPlot(boolean active){
		uib_butt_descriptive_statistics.setEnabled(active);
	}


	/**
	 * Active speedometer.
	 *
	 * @param active the active
	 */
	public void activeSpeedometer(boolean active){
		uib_butt_speedometer.setEnabled(active);
	}

	/**
	 * Active correlation analysis.
	 *
	 * @param active the active
	 */
	public void activeCorrelationAnalysis(boolean active){
		uib_butt_correlation_analysis.setEnabled(active);
	}

	/**
	 * Active scatter plot.
	 *
	 * @param active the active
	 */
	public void activeScatterPlot(boolean active){
		uib_butt_scatter_plot.setEnabled(active);
	}


	/**
	 * Active all algorithms.
	 *
	 * @param active the active
	 */
	public void activeAllAlgorithms(boolean active) {

		activeScatterPlot(active);
		activeSpeedometer(active);
		activeCorrelationAnalysis(active);
		activeBoxPlot(active);

	}


	/**
	 * Manage kpi.
	 *
	 * @param kpi the kpi
	 * @param checked the checked
	 */
	public void manageKPI(KPI kpi, boolean checked){

		if(kpi==null)
			return;

		KPI existingKPI = mapSelected.get(kpi.getId());
		if(existingKPI==null){
			if(checked){
				addSelected(kpi);
			}
		}else{ //already selected. Is is checked or unchecked?
			if(!checked){
				//removing it only if unchecked
				removeSelected(existingKPI);
			}
		}
	}

	/**
	 * Removes the all selected.
	 */
	public void removeAllSelected(){

		Set<String> keySet = mapSelected.keySet();
		for (String key : keySet) {
			KPI kpi = mapSelected.get(key);
			GWT.log("Removing key: "+kpi);
			removeSelected(kpi);
		}

		mapSelected.clear();
	}

	/**
	 * Adds the selected.
	 *
	 * @param kpi the kpi
	 */
	private void addSelected(KPI kpi){
		errorPanelSubmit.clear();
		HTML label = new HTML("* "+kpi.getName());
//		Label label = new Label(kpi.getName());
//		label.setType(LabelType.INFO);
		recapPanel.add(label);
		mapSelected.put(kpi.getId(), kpi);
		mapLabel.put(kpi.getId(), label);
	}
	/**
	 * Removes the selected.
	 *
	 * @param kpi the kpi
	 */
	private void removeSelected(KPI kpi){

		HTML label = mapLabel.get(kpi.getId());
		mapSelected.remove(kpi.getId());

		try{
			recapPanel.remove(label);
		}catch(Exception e){

		}

		//mapSelected.remove(kpi.getId());
	}


	/**
	 * Gets the selected kp is.
	 *
	 * @return the selected kp is
	 */
	public List<KPI> getSelectedKPIs(){

		errorPanelSubmit.clear();

		List<KPI> selectedKPI = new ArrayList<KPI>();
		Set<String> keySet = mapSelected.keySet();
		for (String key : keySet) {
			KPI kpi = mapSelected.get(key);
			selectedKPI.add(kpi);
		}

		return selectedKPI;
	}

	/**
	 * Sets the error.
	 *
	 * @param txt the new error
	 */
	public void setError(String txt) {

		Alert msg = new Alert(txt);
		msg.setAnimation(true);
		msg.setClose(false);
		msg.setType(AlertType.ERROR);
		errorPanelSubmit.add(msg);
	}
	
	/**
	 * Checks if is annual analysis.
	 *
	 * @return true, if is annual analysis
	 */
	public boolean isAnnualAnalysis() {
		return annualAnalysis;
	}

}
