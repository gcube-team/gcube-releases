/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.form;

import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.ui.Widget;


public class DoubleField extends AbstractField {

	private VerticalPanel vp = new VerticalPanel();
	private NumberField numberField = new NumberField();
	
	/**
	 * @param operator
	 */
	public DoubleField(Parameter parameter) {
		super(parameter);
		
		ObjectParameter p = (ObjectParameter)parameter;
		
		numberField.setPropertyEditorType(Double.class);

		if (p.getDefaultValue()!=null)
			numberField.setValue(Double.parseDouble(p.getDefaultValue()));
		
		if (p.getDescription()!=null)
			numberField.setTitle(p.getDescription());
		numberField.setFieldLabel(p.getName());
		
		Html html = new Html("Double Value");
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
