package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.window.WindowCredits;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DataSourceFilter implements AdvancedSearchPanelInterface {
	
	private ContentPanel dataPanel = new ContentPanel();
	private Button btnResetAllFilters = new Button("Reset Filters");
	
    private CheckBoxGroup checkGroup = new CheckBoxGroup();
    private CheckBoxGroup checkGroupOccurrences= new CheckBoxGroup();
    private CheckBoxGroup checkGroupClassification = new CheckBoxGroup();
    
//    private ArrayList<DataSourceModel> listDataSourceOccurrences = new ArrayList<DataSourceModel>();
//    private ArrayList<DataSourceModel> listDataSourceClassification = new ArrayList<DataSourceModel>();
    
    private HashMap<String, DataSourceModel> hashMapDataSourceClassification = new HashMap<String, DataSourceModel>();
    private HashMap<String, DataSourceModel> hashMapDataSourceOccurrences = new HashMap<String, DataSourceModel>();
    

	public DataSourceFilter() {
//		checkGroup.setFieldLabel("Sources");
		initDataSourceFilter();
		addListners();
		btnResetAllFilters.setStyleName("button-hyperlink");
	}


	private void initDataSourceFilter() {
		
		dataPanel.setHeaderVisible(false);
		dataPanel.setBodyBorder(false);
		dataPanel.setLayout(new FitLayout());
		
		dataPanel.setStyleAttribute("marginLeft", "10px");
		dataPanel.setStyleAttribute("marginRight", "10px");
		dataPanel.setStyleAttribute("padding", "5px");
		
		VerticalPanel vp = new VerticalPanel();

		HorizontalPanel hp1 = new HorizontalPanel();
		HorizontalPanel hp2 = new HorizontalPanel();
		
//		Html htmlSource = new Html("Occurences Sources: ");
		
		Anchor htmlSource = new Anchor("Occurences Sources: ", true);
		htmlSource.setStyleName("margin-occurrence-link");
		
		htmlSource.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new WindowCredits("Occurrences data sources credits", hashMapDataSourceOccurrences);
				
			}
		});

		
		
//		Html htmlSource2 = new Html("Classification Sources: ");
			
		Anchor htmlSource2 = new Anchor("Classification Sources: ", true);
		
		htmlSource2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new WindowCredits("Classification data sources credits", hashMapDataSourceClassification);
				
			}
		});
		
		htmlSource2.setStyleName("margin-occurrence-link");
		
		hp1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp2.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		hp1.add(htmlSource);
		hp2.add(htmlSource2);

		hp1.add(checkGroupOccurrences);
		hp2.add(checkGroupClassification);

		vp.add(hp1);
		vp.add(hp2);
		
		dataPanel.add(vp);
		
	}
	
	private void addListners(){

		btnResetAllFilters.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				resetAdvancedFields();
				
			}
		});
	}
	
	@Override
	public void resetAdvancedFields() {

		List<Field<?>> listChecks = checkGroup.getAll();
		
		for (Field<?> item : listChecks) {
			CheckBox checkBox = (CheckBox) item;
            checkBox.reset(); 
		}
	}


	@Override
	public ContentPanel getPanel() {
		return dataPanel;
	}


	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.DATASOURCE.getLabel();
	}


	public List<DataSourceModel> getCheckedGroupList() {
		
		List<DataSourceModel> listDS = new ArrayList<DataSourceModel>();
		
		List<CheckBox> values = new ArrayList<CheckBox>();
		
		if(checkGroup.getValues().size()>0)
			values = checkGroup.getValues();
		else{
			List<Field<?>> listChecks = checkGroup.getAll();
			for (Field<?> field : listChecks) {
				values.add((CheckBox) field);
			}
		}
		
		for (CheckBox checkBox : values) {
			if (checkBox.isEnabled())
				listDS.add(new DataSourceModel(checkBox.getValueAttribute(), checkBox.getValueAttribute()));
		}
		
		if(listDS.size()==0)
			return null;

		return listDS;
	}
	
    private static Comparator<DataSourceModel> COMPARATOR = new Comparator<DataSourceModel>()
    {
	// This is where the sorting happens.
        public int compare(DataSourceModel o1, DataSourceModel o2)
        {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };


	public void loadDataSource(List<DataSourceModel> result) {
		
		Collections.sort(result, COMPARATOR);
		
		if(result!=null){
			
			for(DataSourceModel dsm: result){

				System.out.println("Data Source name " + dsm.getName());
		        CheckBox check = null;
		         
		        for (DataSourceCapability dsc : dsm.getListCapabilities()) {
					
		        	System.out.println("\tData Source capability name: " + dsc.getCapability().getName());
		        	
					if(dsc.getCapability().getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){
						
						check = createCheckBox(dsm, SpeciesCapability.RESULTITEM.getName());
						checkGroupOccurrences.add(check);
//						if(dsm.getDataSourceRepositoryInfo()!=null)
//							listDataSourceOccurrences.add(dsm);
						hashMapDataSourceOccurrences.put(dsm.getName(), dsm);
				
						
						System.out.println("\t\t added check " +  dsm.getName() + " to checkGroupOccurences " + dsc.getCapability().getName());
						
						checkGroup.add(check);
					
					}
					
					if(dsc.getCapability().getName().compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0){
						
						check = createCheckBox(dsm, SpeciesCapability.TAXONOMYITEM.getName());
						checkGroupClassification.add(check);
//						if(dsm.getDataSourceRepositoryInfo()!=null)
//							listDataSourceClassification.add(dsm);
						
						hashMapDataSourceClassification.put(dsm.getName(), dsm);
						
						System.out.println("\t\t added check " +  dsm.getName() + " to checkGroupClassification " + dsc.getCapability().getName());
						
						checkGroup.add(check);
					}
				}
			}
			setVisibility();
		}
	}
	
	private void setVisibility() {

		if(checkGroup.getAll().size()==0)
			checkGroup.setVisible(false);
	}
	
	
	public void enableCheckByCapability(SpeciesCapability capability){
		setEnableAllCheck(false, checkGroupClassification);
		setEnableAllCheck(false, checkGroupOccurrences);
		setCheckByCapability(true,capability);
	}
	
	
	private void setCheckByCapability(boolean enable, SpeciesCapability capability){
		
		if(capability.getName().compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0){
			setEnableAllCheck(enable, checkGroupClassification);
//			checkGroupClassification.setEnabled(enable);	
			setValueAllCheck(enable, checkGroupClassification);
		}
		else if(capability.getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){
			setEnableAllCheck(enable, checkGroupOccurrences);
//			checkGroupOccurences.setEnabled(enable);
			setValueAllCheck(enable, checkGroupOccurrences);
		}
	}
	
	public void setValueAllCheck(boolean checkValue, CheckBoxGroup checksGroup){

		List<Field<?>> listChecks = checksGroup.getAll();
		
		for (Field<?> item : listChecks) {
			CheckBox check = (CheckBox) item;
			check.setValue(checkValue);
		}
	}
	
	
	public void setEnableAllCheck(boolean checkValue, CheckBoxGroup checksGroup){
		
		List<Field<?>> listChecks = checksGroup.getAll();
		
		for (Field<?> item : listChecks) {
			CheckBox check = (CheckBox) item;
			check.setEnabled(checkValue);
		}
	}

	private CheckBox createCheckBox(DataSourceModel dsm, String property){
		
		CheckBox check = new CheckBox();
//		check.setId(dsm.getId());
//        check.setBoxLabel(dsm.getName() + " ("+property+")");
        check.setBoxLabel(dsm.getName());
        check.setValueAttribute(dsm.getName());
        check.setData("capability", dsm);
        check.setToolTip(new ToolTipConfig(dsm.getDescription()));
        return check;
		
	}

	
	@Override
	public ArrayList<DataSourceModel> getAvailablePlugIn() {
		return null;
	}


	public HashMap<String, DataSourceModel> getHashMapDataSourceClassification() {
		return hashMapDataSourceClassification;
	}


	public HashMap<String, DataSourceModel> getHashMapDataSourceOccurrences() {
		return hashMapDataSourceOccurrences;
	}
}
