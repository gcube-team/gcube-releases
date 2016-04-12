/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 16, 2013
 *
 */
public class SynonymsFilter implements AdvancedSearchPanelInterface{

	private Button btnResetAllFilters = new Button("Reset Filters");
	private ContentPanel synonymsFilterPanel = new ContentPanel();
	private CheckBoxGroup checkGroup = new CheckBoxGroup();
	
	@Override
	public ContentPanel getPanel() {
		btnResetAllFilters.setStyleName("button-hyperlink");
		return synonymsFilterPanel;
	}

	public SynonymsFilter(){
		init();
	}
	/**
	 * 
	 */
	private void init() {
		synonymsFilterPanel.setHeaderVisible(false);
		synonymsFilterPanel.setBodyBorder(false);
		synonymsFilterPanel.setLayout(new FitLayout());
		
		synonymsFilterPanel.setStyleAttribute("marginLeft", "10px");
		synonymsFilterPanel.setStyleAttribute("marginRight", "10px");
		synonymsFilterPanel.setStyleAttribute("padding", "5px");
		
		Text text = new Text("Expand with synonyms from: ");
		text.setStyleAttribute("margin-left", "5px");
		text.setStyleAttribute("margin-right", "5px");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp.add(text);
		hp.add(checkGroup);
		
		synonymsFilterPanel.add(hp);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelInterface#getName()
	 */
	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.SYNONYMS.getLabel();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelInterface#resetAdvancedFields()
	 */
	@Override
	public void resetAdvancedFields() {

	}

	@Override
	public ArrayList<DataSourceModel> getAvailablePlugIn() {
		return null;
	}
	
	private static Comparator<DataSourceModel> COMPARATOR = new Comparator<DataSourceModel>() {
		// This is where the sorting happens.
		public int compare(DataSourceModel o1, DataSourceModel o2) {
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
					
//		        	System.out.println("\tData Source capability name: " + dsc.getCapability().getName());
		        	
		        	if(dsc.getCapability().getName().compareTo(SpeciesCapability.SYNONYMS.getName())==0){
						
						check = createCheckBox(dsm, dsc.getCapability().getName());

						System.out.println("added check " +  dsm.getName() + " for SYNONYMS");
						
						checkGroup.add(check);
					
					}
				
				}
			}
		}
	}
	
	private CheckBox createCheckBox(DataSourceModel dsm, String property) {

		CheckBox check = new CheckBox();
		check.setBoxLabel(dsm.getName());
		check.setValueAttribute(dsm.getName());
		check.setData("capability", dsm);
		check.setToolTip(new ToolTipConfig(dsm.getDescription()));
		return check;

	}

	/**
	 * 
	 * @return
	 */
	public List<DataSourceModel> getCheckedGroupList() {
		
		List<DataSourceModel> listDS = new ArrayList<DataSourceModel>();
		
		List<CheckBox> values = new ArrayList<CheckBox>();
		
		if(checkGroup.getValues().size()>0)
			values = checkGroup.getValues();
		
		for (CheckBox checkBox : values) {
			if (checkBox.isEnabled())
				listDS.add(new DataSourceModel(checkBox.getValueAttribute(), checkBox.getValueAttribute()));
		}
		
		if(listDS.size()==0)
			return null;
		
		
//		System.out.println("print synonyms ds : "+listDS);

		return listDS;
	}


}
