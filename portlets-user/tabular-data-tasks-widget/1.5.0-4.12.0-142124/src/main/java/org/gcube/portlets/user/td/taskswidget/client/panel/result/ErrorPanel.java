/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import com.extjs.gxt.ui.client.widget.form.TextArea;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 *
 */
public class ErrorPanel extends TdBasicFormLayoutContainer{
	
	private TextArea errorArea = new TextArea();
	
	public ErrorPanel(String error) {
		initPanel();
		upateFormFields(error);
		panelIsFull = true;
	}
	

	private void initPanel() {
		
		super.fieldSet.setHeading("Error on Result");
//		fieldSet.setCheckboxToggle(true);

		errorArea.setFieldLabel("Error");
		errorArea.setReadOnly(true);
		fieldSet.add(errorArea, formData);
		
	

		form.add(fieldSet);
		
	}

	public void upateFormFields(String error){
		errorArea.setValue(error);
	}


	@Override
	public boolean panelIsFull() {
		return panelIsFull;
	}

}
