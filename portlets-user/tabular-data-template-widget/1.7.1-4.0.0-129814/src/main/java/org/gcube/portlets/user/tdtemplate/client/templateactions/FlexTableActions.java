/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 1, 2015
 */
public class FlexTableActions extends FlexTable{

	
	/**
	 * 
	 */
	public static final String NO_DATA = "No Data";
	private List<TabularDataActionDescription> actions;

	/**
	 * 
	 */
	public FlexTableActions(List<TabularDataActionDescription> tabularDataActionDescription) {
		this.actions = tabularDataActionDescription;
		addStyleName("FlexTableTemplateHistory");
		fillTableActions();
	}
	
	public void fillTableActions(){
		
		setWidget(0, 0, new HTML("#"));
//		setWidget(0, 1, new HTML("Id"));
		setWidget(0, 1, new HTML("Operation"));
		setWidget(0, 2, new HTML("Description"));
		HTMLTable.RowFormatter rf = getRowFormatter();
		rf.setStyleName(0, "FlexTableTemplateHistory-header-row");
		
		if(actions==null || actions.size()==0){
			setWidget(1, 0, new HTML(NO_DATA));
//			setWidget(1, 1, new HTML(NO_DATA));
			setWidget(1, 1, new HTML(NO_DATA));
			setWidget(1, 2, new HTML(NO_DATA));
			rf.setStyleName(1, "FlexTableTemplateHistory-other-rows");
		}
		
		for (int i = 0; i < actions.size(); i++) {
			
			int indexOffset = i+1;
			TabularDataActionDescription action = actions.get(i);

			setWidget(indexOffset, 0, new HTML(indexOffset+""));
			setWidget(indexOffset, 1,  new HTML(action.getLabel()));
			setWidget(indexOffset, 2,  new HTML(action.getDescription()));
//			setWidget(indexOffset, 1,  new HTML(action.getId()));

			rf.setStyleName(indexOffset, "FlexTableTemplateHistory-other-rows");
			
//			getCellFormatter().getElement(indexOffset, i).setAttribute("height", "40px");

		}
	}
}
