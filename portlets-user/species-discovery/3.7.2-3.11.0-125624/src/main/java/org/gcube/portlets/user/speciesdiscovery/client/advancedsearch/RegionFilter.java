package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class RegionFilter extends ColumnContainer implements AdvancedSearchPanelInterface {

	private Button btnResetAllFilters = new Button("Reset Filters");
	private ContentPanel regionFilterPanel = new ContentPanel();
	
	public RegionFilter() {
		init();
		btnResetAllFilters.setStyleName("button-hyperlink");
	}
	

	private void init() {
		
		regionFilterPanel.setHeaderVisible(false);
		regionFilterPanel.setBodyBorder(false);
		
	}


	@Override
	public ContentPanel getPanel() {
		return regionFilterPanel;
	}

	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.REGION.getLabel();
	}

	@Override
	public void resetAdvancedFields() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<DataSourceModel> getAvailablePlugIn() {
		return null;
	}



}
