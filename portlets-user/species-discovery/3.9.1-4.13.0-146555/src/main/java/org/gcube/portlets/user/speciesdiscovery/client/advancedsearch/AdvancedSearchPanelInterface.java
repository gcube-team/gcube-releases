package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;

import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface AdvancedSearchPanelInterface {
	public ContentPanel getPanel();
	public String getName();
	public void resetAdvancedFields();
	public ArrayList<DataSourceModel> getAvailablePlugIn();
}
