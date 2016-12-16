/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.EnumParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author ceras
 *
 */
public class EnumField extends AbstractField {

	SimpleComboBox<String> listBox;

	/**
	 * @param parameter
	 */
	public EnumField(Parameter parameter) {
		super(parameter);

		EnumParameter p = (EnumParameter)parameter;
		
		listBox = new SimpleComboBox<String>();
		listBox.add(p.getValues());
		listBox.setAllowBlank(false);
		listBox.setForceSelection(true);
		listBox.setEditable(false);
		listBox.setTriggerAction(TriggerAction.ALL);
		if (p.getDescription()!=null)
			listBox.setTitle(p.getDescription());
		listBox.setFieldLabel(p.getName());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return listBox.getValue().getValue();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return listBox;
	}

//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#isValid()
//	 */
//	@Override
//	public boolean isValid() {
//		return false;
//	}

}
