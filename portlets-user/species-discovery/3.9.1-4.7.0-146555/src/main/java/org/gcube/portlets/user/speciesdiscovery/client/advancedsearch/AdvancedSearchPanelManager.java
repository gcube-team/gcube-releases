package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class AdvancedSearchPanelManager extends ContentPanel implements AdvancedSearchInterface{

	private static AdvancedSearchPanelManager instance;
	private BoundsFilter boundsFilter;
	private DateFilter dateFilter;
	private RegionFilter regionFilter;
	private DataSourceFilter dataSourceFilter;
	private List<String> listAdvacedPanels = new ArrayList<String>();
	private CardLayout cardLayout = new CardLayout();
//	private ClassificationFilter classificationFilter;
    private HashMap<String,DataSourceModel> listDataSources;
	private SpeciesCapability currentSelectedCapability;
	private SynonymsFilter synonymsFilter;
	private UnfoldFilter unfoldFilter;

	public static synchronized AdvancedSearchPanelManager getInstance() {
		if (instance == null)
			instance = new AdvancedSearchPanelManager();
		return instance;
	}

	private AdvancedSearchPanelManager(){

		boundsFilter = new BoundsFilter();
		dateFilter = new DateFilter();
		regionFilter = new RegionFilter();
		dataSourceFilter = new DataSourceFilter();
		synonymsFilter = new SynonymsFilter();
		unfoldFilter = new UnfoldFilter();
//		classificationFilter = new ClassificationFilter();

		init();
//		listAdvacedPanels.add(classificationFilter.getName());
		listAdvacedPanels.add(dataSourceFilter.getName());
		listAdvacedPanels.add(boundsFilter.getName());
		listAdvacedPanels.add(dateFilter.getName());
		listAdvacedPanels.add(synonymsFilter.getName());
		listAdvacedPanels.add(unfoldFilter.getName());
//		listAdvacedPanels.add(regionFilter.getName());

	}

	private void init() {

		setLayout(cardLayout);
		setHeaderVisible(false);
		setSize(925, 95);
		setBodyBorder(false);

		add(boundsFilter.getPanel());
		add(dateFilter.getPanel());
		add(dataSourceFilter.getPanel());
		add(regionFilter.getPanel());
		add(synonymsFilter.getPanel());
		add(unfoldFilter.getPanel());
		cardLayout.setActiveItem(boundsFilter.getPanel());
	}


	public List<String> getListAdvancedSearchPanels(){
		return listAdvacedPanels;
	}

	public void setActivePanel(String name){
		if(name.equals(AdvancedSearchPanelEnum.BOUNDS.getLabel()))
			cardLayout.setActiveItem(boundsFilter.getPanel());
		else if(name.equals(AdvancedSearchPanelEnum.DATE.getLabel()))
			cardLayout.setActiveItem(dateFilter.getPanel());
		else if(name.equals(AdvancedSearchPanelEnum.DATASOURCE.getLabel()))
			cardLayout.setActiveItem(dataSourceFilter.getPanel());
		else if(name.equals(AdvancedSearchPanelEnum.REGION.getLabel()))
			cardLayout.setActiveItem(regionFilter.getPanel());
		else if(name.equals(AdvancedSearchPanelEnum.SYNONYMS.getLabel()))
			cardLayout.setActiveItem(synonymsFilter.getPanel());
		else if(name.equals(AdvancedSearchPanelEnum.UNFOLD.getLabel()))
			cardLayout.setActiveItem(unfoldFilter.getPanel());
//		else if(name.equals(AdvancedSearchPanelEnum.CLASSIFICATION.getLabel()))
//			cardLayout.setActiveItem(classificationFilter.getPanel());
	}


	public ContentPanel getPanel(){
		return this;
	}


	public NumberField getUpperBoundLatitudeField() {
		return boundsFilter.getUpperBoundLatitudeField();
	}

	public NumberField getUpperBoundLongitudeField() {
		return boundsFilter.getUpperBoundLongitudeField();
	}

	public NumberField getLowerBoundLatitudeField() {
		return boundsFilter.getLowerBoundLatitudeField();
	}

	public NumberField getLowerBoundLongitudeField() {
		return boundsFilter.getLowerBoundLongitudeField();
	}

	public DateField getFromDate() {
		return dateFilter.getFromDate();
	}

	public DateField getToDate() {
		return dateFilter.getToDate();
	}

	public void loadDataSource(List<DataSourceModel> result) {
		dataSourceFilter.loadDataSource(result);
		synonymsFilter.loadDataSource(result);
		unfoldFilter.loadDataSource(result);

		listDataSources = new HashMap<String, DataSourceModel>();

		//fill hash
		for(DataSourceModel dsm: result){
			listDataSources.put(dsm.getId(), dsm);
		}

		setAvailableInfoAdvancedFilters(result);

	}

	private void setAvailableInfoAdvancedFilters(List<DataSourceModel> result) {

		for(DataSourceModel dsm: result){
			for (DataSourceCapability dsc : dsm.getListCapabilities()) {

				ArrayList<SpeciesCapability> properties = dsc.getListFilters();

				for (SpeciesCapability capabilityEnum : properties) {

//					System.out.println("capability " + capabilityEnum.toString());

					if(capabilityEnum.equals(SpeciesCapability.FROMDATE))
						dateFilter.addAvailablePlugInfo(dsm, SpeciesCapability.FROMDATE);
					else if(capabilityEnum.equals(SpeciesCapability.LOWERBOUND))
						boundsFilter.addAvailablePlugInfo(dsm, SpeciesCapability.LOWERBOUND);
					else if(capabilityEnum.equals(SpeciesCapability.TODATE))
						dateFilter.addAvailablePlugInfo(dsm, SpeciesCapability.TODATE);
					else if(capabilityEnum.equals(SpeciesCapability.UPPERBOUND))
						boundsFilter.addAvailablePlugInfo(dsm, SpeciesCapability.UPPERBOUND);
//					else if(capabilityEnum.equals(SpeciesCapability.SYNONYMS))
//						synonymsFilter.addAvailablePlugInfo(dsm, SpeciesCapability.SYNONYMS);
//					else if(capabilityEnum.equals(SpeciesCapability.UNFOLD))
//						unfoldFilter.addAvailablePlugInfo(dsm, SpeciesCapability.UNFOLD);
				}
			}
		}


	}

	public void setCurrentCapability(SpeciesCapability capability){

		this.currentSelectedCapability = capability;

		this.capabilityChange();

	}

	public void setVisibleAllPanel(boolean bool){

		boundsFilter.getPanel().setVisible(bool);
		dateFilter.getPanel().setVisible(bool);
		dataSourceFilter.getPanel().setVisible(bool);
		regionFilter.getPanel().setVisible(bool);
		synonymsFilter.getPanel().setVisible(bool);
		unfoldFilter.getPanel().setVisible(bool);
//		classificationFilter.getPanel().setVisible(bool);

	}

	private void capabilityChange() {

//		dataSourceFilter.disableAllCheck();
		dataSourceFilter.enableCheckByCapability(this.currentSelectedCapability);

	}

	public List<DataSourceModel> getCheckedDataSources() {
		return dataSourceFilter.getCheckedGroupList();

	}

	public List<DataSourceModel> getCheckedDataSourceForSynonyms() {
		return synonymsFilter.getCheckedGroupList();

	}

	public HashMap<String, DataSourceModel> findDataSourceByCapability(SpeciesCapability capability) {

		if(capability.getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){
			return dataSourceFilter.getHashMapDataSourceOccurrences();
		}
		else if(capability.getName().compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0){

			return dataSourceFilter.getHashMapDataSourceClassification();
		}

		return null;
	}


	public DataSourceModel findDataSourceByCapabilityAndName(SpeciesCapability capability, String dataSourceName) {

		HashMap<String, DataSourceModel> hashDataSource = findDataSourceByCapability(capability);

		if(hashDataSource!=null)
			return hashDataSource.get(dataSourceName);

		return null;
	}



	public SpeciesCapability getCurrentSelectedCapability() {
		return currentSelectedCapability;
	}

	public List<DataSourceModel> getCheckedDataSourceForUnfold() {
		return unfoldFilter.getCheckedGroupList();
	}

	public void disableFilterForSearchType(SearchType type){

		switch (type) {

			case BY_COMMON_NAME:
				unfoldFilter.activeChecks(false);
				break;

			default:
				unfoldFilter.activeChecks(true);
				break;
		}

	}

}
