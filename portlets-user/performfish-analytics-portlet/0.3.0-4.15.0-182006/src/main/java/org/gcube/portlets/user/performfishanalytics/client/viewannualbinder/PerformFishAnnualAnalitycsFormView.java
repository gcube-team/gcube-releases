
package org.gcube.portlets.user.performfishanalytics.client.viewannualbinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.annualcontrollers.PerformFishAnnualAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadFocusEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Year;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class PerformFishAnalitycsFormView.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class PerformFishAnnualAnalitycsFormView extends Composite {

	/** The ui binder. */
	private static PerformFishAnalitycsFormViewUiBinder uiBinder =
		GWT.create(PerformFishAnalitycsFormViewUiBinder.class);

	/**
	 * The Interface CreateFolderConfigurationToThreddsSyncUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 14,
	 *         2018
	 */
	interface PerformFishAnalitycsFormViewUiBinder
		extends UiBinder<Widget, PerformFishAnnualAnalitycsFormView> {
	}
	/** The pager. */
	// @UiField
	// Pager pager;
	@UiField
	public ListBox field_select_population;
	@UiField
	protected ListBox field_select_population_type;
	@UiField
	ListBox field_select_year;
	@UiField
	ControlGroup cg_select_population;
	@UiField
	ControlGroup cg_select_population_type;
	@UiField
	ControlGroup cg_select_year;
	@UiField
	VerticalPanel errorPanel;
	@UiField
	CheckBox uib_check_all_year;

	// @UiField
	// Fieldset fieldset_add_catalogue_bean;
	/** The folder id. */
	private String folderId;
	private Map<String, List<PopulationType>> mapPopulation =
		new HashMap<String, List<PopulationType>>();
	private Map<String, PopulationType> mapPopulationType =
		new HashMap<String, PopulationType>();


	/**
	 * Instantiates a new perform fish analitycs form view.
	 */
	public PerformFishAnnualAnalitycsFormView() {

		initWidget(uiBinder.createAndBindUi(this));

		field_select_population.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				// String scope = field_select_scope.getSelectedItemText();
				String value = field_select_population.getSelectedValue();
				GWT.log("Selected POPULATION: " + value);
				PerformFishAnnualAnalyticsController.eventBus.fireEvent(new LoadPopulationTypeEvent(value, null));
				//PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_population));
			}
		});

		field_select_population_type.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				String populatioTypeSelected = field_select_population_type.getSelectedValue();
				GWT.log("Selected population type: " + populatioTypeSelected);
				PopulationType opt = mapPopulationType.get(populatioTypeSelected);
				PerformFishAnnualAnalyticsController.eventBus.fireEvent(new SelectedPopulationTypeEvent(opt));
				fillYear(opt.getListYears());
				PerformFishAnnualAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_population_type));
			}
		});

		field_select_year.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				hideError(cg_select_year, false);
				PerformFishAnnualAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_year));
			}
		});


		uib_check_all_year.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_year.getValue();
				selectAllFields(field_select_year, isChecked);
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

		PerformFishAnnualAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(listBox));

	}

	/**
	 * Fill population type.
	 *
	 * @param populationName
	 *            the population name
	 * @param result
	 *            the result
	 * @param population
	 *            the population
	 */
	public void addPopulationTypes(
		String populationName, List<PopulationType> result,
		Population population) {

		mapPopulation.put(populationName, result);
		fillPopulationType(populationName, result, population);
		
		//TODO  CAN WE MOVE THIS CALL IN ANOTHER POSITION?
		PerformFishAnnualAnalyticsController.eventBus.fireEvent(new LoadFocusEvent());
	}

	/**
	 * Fill year.
	 *
	 * @param listYear the list quarter
	 */
	private void fillYear(List<Year> listYear) {

		field_select_year.clear();
		if (listYear != null && listYear.size() > 0) {
			field_select_year.setEnabled(true);
			for (Year year : listYear) {
				field_select_year.addItem(year.getName(), year.getId());
			}
		}
		else {
			field_select_year.setEnabled(false);
			uib_check_all_year.setEnabled(false);
		}
	}

	/**
	 * Fill population type.
	 *
	 * @param populationName
	 *            the population name
	 * @param result
	 *            the result
	 * @param population
	 *            the population
	 */
	private void fillPopulationType(
		String populationName, List<PopulationType> result,
		Population population) {

		field_select_population_type.clear();
		if (result != null && result.size() > 0) {
			field_select_population_type.setEnabled(true);
			mapPopulationType.clear();
			for (PopulationType popType : result) {
				
				//TODO HARD-CABLED DUE TO MOCKUP PRESENTED BY GP
				String batchTypeName = popType.getName();
				if(popType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_AGGREGATED_CLOSED_BATCHES.name())) {
					batchTypeName = "Farm Data";
				}
				
				field_select_population_type.addItem(batchTypeName, popType.getId());
				mapPopulationType.put(popType.getId(), popType);
				// if(thCatalogueBean.isDefault()){
				// field_select_catalogue_name.setSelectedValue(thCatalogueBean.getName());
				// }
			}
			// field_select_population_type.setSelectedValue(thCatalogueBean.getName());
			field_select_population_type.setSelectedValue(result.get(0).getName());
			DomEvent.fireNativeEvent(
				Document.get().createChangeEvent(),
				field_select_population_type);
			field_select_population.addItem(
				population.getName(), population.getId());
		}
	}


	/**
	 * Validate form.
	 *
	 * @return true, if successful
	 */
	public boolean validateForm() {

		cg_select_year.setType(ControlGroupType.NONE);
		errorPanel.setVisible(false);
		// cg_remote_path.setType(ControlGroupType.NONE);
		if (field_select_year.getItemCount()>0 && field_select_year.getSelectedIndex() == -1) {
			cg_select_year.setType(ControlGroupType.INFO);
			showAlert("Please select a Year", AlertType.INFO);
			return false;
		}
		return true;
	}

	
	/**
	 * Show alert.
	 *
	 * @param txt the txt
	 * @param type the type
	 */
	public void showAlert(String txt, AlertType type) {
		errorPanel.clear();
		errorPanel.setVisible(true);
		Alert msg = new Alert(txt);
		msg.setAnimation(true);
		msg.setClose(false);
		msg.setType(type);
		errorPanel.add(msg);

	}

	/**
	 * Hide error.
	 *
	 * @param cgroup the cgroup
	 * @param forceHide the force hide
	 */
	public void hideError(ControlGroup cgroup, boolean forceHide){

		if(forceHide){
			cg_select_year.setType(ControlGroupType.NONE);
			errorPanel.setVisible(false);
		}

		if(cgroup!=null){
			cgroup.setType(ControlGroupType.NONE);
		}

		errorPanel.setVisible(false);
	}


	/**
	 * Gets the batch type.
	 *
	 * @return the batch type
	 */
	public String getBatchType(){

		return field_select_population_type.getSelectedItemText();

	}

	/**
	 * Gets the sel level.
	 *
	 * @return the sel level
	 */
	public String getLevel(){
		return field_select_population.getSelectedItemText();
	}


	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public List<String> getYear(){
		return getSelected(field_select_year);
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


}
