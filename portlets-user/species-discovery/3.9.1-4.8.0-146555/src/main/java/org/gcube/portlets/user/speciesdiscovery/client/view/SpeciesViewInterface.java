package org.gcube.portlets.user.speciesdiscovery.client.view;

import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public interface SpeciesViewInterface {

	public void reload();
	
	public List<ResultRow> getSelectedRows();
	
	public void setBodyStyleAsFiltered(boolean isFiltered);
	
	public ContentPanel getPanel();
	
}
