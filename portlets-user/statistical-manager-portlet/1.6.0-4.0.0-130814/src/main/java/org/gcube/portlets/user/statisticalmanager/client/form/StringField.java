/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class StringField extends AbstractField {

	TextField<String> textField = new TextField<String>();
	
	/**
	 * @param parameter
	 */
	public StringField(Parameter parameter) {
		super(parameter);
		
		ObjectParameter p = (ObjectParameter)parameter;
		textField.setValue(p.getDefaultValue());
		if (p.getDescription()!=null)
			textField.setTitle(p.getDescription());
		textField.setFieldLabel(p.getName());
		
		if (p.getDefaultValue()==null)
			textField.setAllowBlank(false);		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return textField.getValue();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return textField;
	}

}
