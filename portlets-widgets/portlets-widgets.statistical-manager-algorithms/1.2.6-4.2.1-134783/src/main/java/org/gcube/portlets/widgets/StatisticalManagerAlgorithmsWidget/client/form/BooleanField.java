/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;



import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class BooleanField extends AbstractField {

	CheckBox checkBox = new CheckBox();

	/**
	 * @param parameter
	 */
	public BooleanField(Parameter parameter) {
		super(parameter);

		ObjectParameter p = (ObjectParameter)parameter;
		checkBox.setValue(!p.getDefaultValue().toUpperCase().equals("FALSE"));
		if (p.getDescription()!=null)
			checkBox.setTitle(p.getDescription());
		checkBox.setFieldLabel(p.getName());
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return (checkBox.getValue() ? "true" : "false");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return checkBox;
	}

}
