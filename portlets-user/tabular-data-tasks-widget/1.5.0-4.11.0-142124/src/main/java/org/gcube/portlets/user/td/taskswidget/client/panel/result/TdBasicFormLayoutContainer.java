/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 *
 */
public class TdBasicFormLayoutContainer extends LayoutContainer{

	
	FormData formData = new FormData("-20");
	VerticalPanel vp = new VerticalPanel();
	FormPanel form = new FormPanel();
	FormLayout layout = new FormLayout();
	FieldSet fieldSet = new FieldSet();
	
	boolean panelIsFull = false;

	/**
	 * 
	 */
	public TdBasicFormLayoutContainer() {
		vp.setSpacing(5);
		form.setWidth(350);
		form.setHeaderVisible(false);
		form.setFrame(true);
		form.setLayout(new FlowLayout());
		
		layout.setLabelWidth(75);
		fieldSet.setLayout(layout);
		
		vp.add(form);
		add(vp);
	}
	
	
	public boolean panelIsFull(){
		return panelIsFull;
	}
}
