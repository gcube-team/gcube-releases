package org.gcube.portlets.admin.policydefinition.vaadin.components;

import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.vaadin.containers.PoliciesContainer;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class PolicyTable extends Table{

	private static final long serialVersionUID = 5568483469752658449L;
	
	@Override
	protected String formatPropertyValue(Object rowId, Object colId,
			Property property) {
		if(PoliciesContainer.DATE_RANGE.equalsIgnoreCase((String)colId) && property.getValue() != null){
			String dateRange = (String)property.getValue();
			return PresentationHelper.viewDateRangeString(dateRange);
		}
		if(PoliciesContainer.TIME_RANGE.equalsIgnoreCase((String)colId) && property.getValue() != null){
			String timeRange = (String)property.getValue();
			return PresentationHelper.viewTimeRangeString(timeRange);
		}
		return super.formatPropertyValue(rowId, colId, property);
	}

}
