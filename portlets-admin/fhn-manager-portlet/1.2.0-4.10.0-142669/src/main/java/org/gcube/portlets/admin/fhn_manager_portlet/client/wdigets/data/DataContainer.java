package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.user.client.ui.Widget;

public interface DataContainer<T extends Storable> {

	public void setData(List<T> toSet);
	public boolean hasSelection();
	public T getSelected();
	public Widget getTheWidget();
	
	public void setFilters(Map<String,String> toSet);
	
	public void fireRefreshData();
}
