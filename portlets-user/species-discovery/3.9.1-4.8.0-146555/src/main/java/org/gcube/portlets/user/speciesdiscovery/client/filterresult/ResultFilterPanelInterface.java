package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

import java.util.HashMap;

import com.extjs.gxt.ui.client.widget.ContentPanel;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface ResultFilterPanelInterface {
	public ContentPanel getPanel();
	public void setHeaderTitle();
	public String getName();
	public void loadDataSource(HashMap<String, Integer> result);
	
	public final String DEFAULTMARGINLEFT = "5px"; 
	public final String DEFAULTMARGIN = "5px";
}
