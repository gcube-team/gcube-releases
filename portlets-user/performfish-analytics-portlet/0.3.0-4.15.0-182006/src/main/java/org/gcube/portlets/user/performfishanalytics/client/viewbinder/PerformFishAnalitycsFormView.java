
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.client.controllers.PerformFishAnalyticsController;
import org.gcube.portlets.user.performfishanalytics.client.event.LoadPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.PerformFishFieldFormChangedEvent;
import org.gcube.portlets.user.performfishanalytics.client.event.SelectedPopulationTypeEvent;
import org.gcube.portlets.user.performfishanalytics.shared.Area;
import org.gcube.portlets.user.performfishanalytics.shared.Period;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Quarter;
import org.gcube.portlets.user.performfishanalytics.shared.Species;

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
public class PerformFishAnalitycsFormView extends Composite {

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
		extends UiBinder<Widget, PerformFishAnalitycsFormView> {
	}
	/** The pager. */
	// @UiField
	// Pager pager;
	@UiField
	public ListBox field_select_population;
	@UiField
	protected ListBox field_select_population_type;
	@UiField
	ListBox field_select_species;
	@UiField
	ListBox field_select_quarter;
	@UiField
	ListBox field_select_area;
	@UiField
	ListBox field_select_period;
	@UiField
	ControlGroup cg_select_population;
	@UiField
	ControlGroup cg_select_population_type;
	@UiField
	ControlGroup cg_select_species;
	@UiField
	ControlGroup cg_select_quarter;
	@UiField
	ControlGroup cg_select_area;
	@UiField
	ControlGroup cg_select_period;
	@UiField
	VerticalPanel errorPanel;
	@UiField
	CheckBox uib_check_all_period;
	@UiField
	CheckBox uib_check_all_area;
	@UiField
	CheckBox uib_check_all_quarter;
//	@UiField
//	Button uib_button_load_synoptic_table;

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
	public PerformFishAnalitycsFormView() {

		initWidget(uiBinder.createAndBindUi(this));

		field_select_population.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				// String scope = field_select_scope.getSelectedItemText();
				String value = field_select_population.getSelectedValue();
				GWT.log("Selected POPULATION: " + value);
				PerformFishAnalyticsController.eventBus.fireEvent(new LoadPopulationTypeEvent(value, null));
				//PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_population));
			}
		});

		field_select_population_type.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				
				String populatioTypeSelected = field_select_population_type.getSelectedValue();
				GWT.log("Selected population type: " + populatioTypeSelected);
				PopulationType opt = mapPopulationType.get(populatioTypeSelected);
				
//				if(opt.getName().equalsIgnoreCase("GROW_OUT_INDIVIDUAL_CLOSED_BATCHES")) {
//					uib_button_load_synoptic_table.setVisible(true);
//				}else {
//					uib_button_load_synoptic_table.setVisible(false);
//				}
				
				PerformFishAnalyticsController.eventBus.fireEvent(new SelectedPopulationTypeEvent(opt));
				fillSpecies(opt.getListSpecies());
				fillQuarter(opt.getListQuarter());
				fillArea(opt.getListArea());
				fillPeriod(opt.getListPeriod());
				PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_population_type));
			}
		});

		field_select_period.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				hideError(cg_select_period, false);
				PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_period));
			}
		});
		
		field_select_species.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_species));
				
			}
		});

		field_select_quarter.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				hideError(cg_select_quarter, false);
				PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_quarter));
			}
		});

		field_select_area.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				hideError(cg_select_area, false);
				PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(field_select_area));
			}
		});

		uib_check_all_period.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_period.getValue();
				selectAllFields(field_select_period, isChecked);
			}
		});

		uib_check_all_quarter.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_quarter.getValue();
				selectAllFields(field_select_quarter, isChecked);
			}
		});

		uib_check_all_area.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				boolean isChecked = uib_check_all_area.getValue();
				selectAllFields(field_select_area, isChecked);
			}
		});
		
//		uib_button_load_synoptic_table.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
////				boolean isValidForm = vivalidatePerformFishInputFields();
//				PerformFishAnalyticsController.eventBus.fireEvent(new LoadSynopticTableEvent(false));
//			}
//		});
		
	}
	
//	/**
//	 * Sets the enable synoptic table.
//	 *
//	 * @param b the new enable synoptic table
//	 */
//	public void setEnableSynopticTable(boolean b) {
//		uib_button_load_synoptic_table.setEnabled(b);
//	}

	
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

		PerformFishAnalyticsController.eventBus.fireEvent(new PerformFishFieldFormChangedEvent(listBox));

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
	}

	/**
	 * Fill species.
	 *
	 * @param listSpecies
	 *            the list species
	 */
	private void fillSpecies(List<Species> listSpecies) {

		field_select_species.clear();
		if (listSpecies != null && listSpecies.size() > 0) {
			field_select_species.setEnabled(true);
			for (Species species : listSpecies) {
				field_select_species.addItem(species.getName(), species.getId());
			}
		}
		else {
			field_select_species.setEnabled(false);
		}
	}

	/**
	 * Fill area.
	 *
	 * @param listArea
	 *            the list area
	 */
	private void fillArea(List<Area> listArea) {

		field_select_area.clear();
		if (listArea != null && listArea.size() > 0) {
			field_select_area.setEnabled(true);
			for (Area area : listArea) {
				field_select_area.addItem(area.getName(), area.getId());
			}
		}
		else {
			field_select_area.setEnabled(false);
			uib_check_all_area.setEnabled(false);
		}
	}

	/**
	 * Fill period.
	 *
	 * @param listPeriod
	 *            the list period
	 */
	private void fillPeriod(List<Period> listPeriod) {

		field_select_period.clear();
		if (listPeriod != null && listPeriod.size() > 0) {
			field_select_period.setEnabled(true);
			for (Period period : listPeriod) {
				field_select_period.addItem(period.getName(), period.getId());
			}
		}
		else {
			field_select_period.setEnabled(false);
			uib_check_all_period.setEnabled(false);
		}
	}

	/**
	 * Fill quarter.
	 *
	 * @param listQuarter
	 *            the list quarter
	 */
	private void fillQuarter(List<Quarter> listQuarter) {

		field_select_quarter.clear();
		if (listQuarter != null && listQuarter.size() > 0) {
			field_select_quarter.setEnabled(true);
			for (Quarter quarter : listQuarter) {
				field_select_quarter.addItem(quarter.getName(), quarter.getId());
			}
		}
		else {
			field_select_quarter.setEnabled(false);
			uib_check_all_quarter.setEnabled(false);
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
				field_select_population_type.addItem(
					popType.getName(), popType.getId());
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

		cg_select_quarter.setType(ControlGroupType.NONE);
		cg_select_area.setType(ControlGroupType.NONE);
		cg_select_period.setType(ControlGroupType.NONE);
		errorPanel.setVisible(false);
		// cg_remote_path.setType(ControlGroupType.NONE);
		if (field_select_quarter.getItemCount()>0 && field_select_quarter.getSelectedIndex() == -1) {
			cg_select_quarter.setType(ControlGroupType.INFO);
			showAlert("Please select a Quarter", AlertType.INFO);
			return false;
		}
		if (field_select_area.getItemCount()>0 && field_select_area.getSelectedIndex() == -1) {
			cg_select_area.setType(ControlGroupType.INFO);
			showAlert("Please select an Area", AlertType.INFO);
			return false;
		}
		if (field_select_period.getItemCount()>0 && field_select_period.getSelectedIndex() == -1) {
			cg_select_period.setType(ControlGroupType.INFO);
			showAlert("Please select a Period", AlertType.INFO);
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
			cg_select_quarter.setType(ControlGroupType.NONE);
			cg_select_area.setType(ControlGroupType.NONE);
			cg_select_period.setType(ControlGroupType.NONE);
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
	 * Gets the species.
	 *
	 * @return the species
	 */
	public String getSpecies(){
		return field_select_species.getSelectedItemText();
	}

	/**
	 * Gets the quarter.
	 *
	 * @return the quarter
	 */
	public List<String> getQuarter(){
		return getSelected(field_select_quarter);
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public List<String> getArea(){
		return getSelected(field_select_area);
	}

	/**
	 * Gets the period.
	 *
	 * @return the period
	 */
	public List<String> getPeriod(){
		return getSelected(field_select_period);
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
