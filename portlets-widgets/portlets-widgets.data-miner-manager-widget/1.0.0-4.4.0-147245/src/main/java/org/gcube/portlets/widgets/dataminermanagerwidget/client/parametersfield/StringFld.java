/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;


import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class StringFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private TextField textField;

	/**
	 * @param parameter
	 */
	public StringFld(Parameter parameter) {
		super(parameter);
		
		ObjectParameter p = (ObjectParameter) parameter;
		
		textField = new TextField();
		textField.setValue(p.getDefaultValue());

		if (p.getDefaultValue() == null)
			textField.setAllowBlank(false);

		HtmlLayoutContainer descr;

		if (p.getDescription() == null) {
			descr=new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");
		
		} else {
			//textField.setToolTip(p.getDescription());
			descr=new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"+p.getDescription()+"</p>");
			descr.addStyleName("workflow-fieldDescription");
		}
		
		SimpleContainer vContainer=new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(
				"String Value");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(textField, new VerticalLayoutData(-1,-1,new Margins(0)));
		vField.add(typeDescription, new VerticalLayoutData(-1,-1,new Margins(0)));
		vContainer.add(vField);
		
		fieldContainer = new SimpleContainer();
		HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);
		
		
		horiz.add(vContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		fieldContainer.forceLayout();
		
	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		return textField.getValue();
	}

	/**
	 * 
	 */
	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	@Override
	public boolean isValid() {
		return textField.isValid();
	}
	
}
