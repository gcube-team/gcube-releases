package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 *NOT USED
 */
public class TypeFilter implements ResultFilterPanelInterface{

	private ContentPanel typeFilterPanel = new ContentPanel();
	
	public TypeFilter() {
		setHeaderTitle();
	}
	
	@Override
	public ContentPanel getPanel() {
		return typeFilterPanel;
	}

	@Override
	public String getName() {
		return ResultFilterPanelEnum.TYPE.getLabel();
	}

	@Override
	public void setHeaderTitle() {
		typeFilterPanel.setHeading(this.getName());
		
	}

	@Override
	public void loadDataSource(HashMap<String, Integer> result) {
		// TODO Auto-generated method stub
		return;
		
	}

}
