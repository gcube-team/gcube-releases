/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;

import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 *
 */
public class JobInfoPanel extends ContentPanel{


	public JobInfoPanel(FlexTableJob table) {
		setBorders(false);
		setHeaderVisible(false);
		setHeight(ConstantsTdTasks.RESULT_PANELS_HEIGHT);
		setStyleAttribute("margin", "5px");
		add(table);
		
	}
	
}
