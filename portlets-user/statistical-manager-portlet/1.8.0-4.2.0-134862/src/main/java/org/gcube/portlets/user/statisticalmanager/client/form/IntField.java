/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class IntField extends AbstractField {

	private VerticalPanel vp = new VerticalPanel();
	private NumberField numberField = new NumberField();
	
	/**
	 * @param operator
	 */
	public IntField(Parameter parameter) {
		super(parameter);
		
		ObjectParameter p = (ObjectParameter)parameter;		
		
		numberField.setPropertyEditorType(Integer.class);
		
		if (p.getDefaultValue()!=null)
			numberField.setValue(Integer.parseInt(p.getDefaultValue()));
		if (p.getDescription()!=null)
			numberField.setTitle(p.getDescription());
		numberField.setFieldLabel(p.getName());
		numberField.setAllowBlank(false);
		
		Html html = new Html("Integer Value");
		html.addStyleName("workflow-templatesList");
		
		vp.add(numberField);
		vp.add(html);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return numberField.getRawValue();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return vp;
	}

}
