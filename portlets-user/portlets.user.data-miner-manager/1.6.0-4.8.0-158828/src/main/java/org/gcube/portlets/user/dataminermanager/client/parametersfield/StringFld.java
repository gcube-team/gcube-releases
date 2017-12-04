/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.portlets.user.dataminermanager.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StringFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private TextField textField;
	private TextArea textArea;
	private boolean isTextArea;

	/**
	 * @param parameter
	 *            parameter
	 */
	public StringFld(Parameter parameter) {
		super(parameter);

		ObjectParameter p = (ObjectParameter) parameter;

		HtmlLayoutContainer descr;

		if (p.getDescription() == null || p.getDescription().isEmpty()) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");
			isTextArea = false;

		} else {
			// textField.setToolTip(p.getDescription());
			if (p.getDescription().contains("[TEXTAREA]")) {
				String textAreaDescription=p.getDescription();
				Log.debug("textAreaDescription: "+textAreaDescription);
				textAreaDescription=textAreaDescription.replaceFirst("\\[TEXTAREA\\]", "");
				Log.debug("Removed tag: "+textAreaDescription);
				descr = new HtmlLayoutContainer(
						"<p style='margin-left:5px !important;'>" + textAreaDescription + "</p>");
				descr.addStyleName("workflow-fieldDescription");
				isTextArea = true;

			} else {
				descr = new HtmlLayoutContainer(
						"<p style='margin-left:5px !important;'>" + p.getDescription() + "</p>");
				descr.addStyleName("workflow-fieldDescription");
				isTextArea = false;
			}
		}

		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("String Value");
		typeDescription.setStylePrimaryName("workflow-parameters-description");

		if (isTextArea) {
			textArea = new TextArea();
			textArea.setValue(p.getDefaultValue(), true);
			textArea.setStylePrimaryName("dataminer-textarea");
			//textArea.setResizable(TextAreaInputCell.Resizable.VERTICAL);
			textArea.setWidth("360px");
			textArea.setHeight("160px");
			if (p.getDefaultValue() == null)
				textArea.setAllowBlank(false);

			vField.add(textArea, new VerticalLayoutData(-1, -1, new Margins(0)));

		} else {

			textField = new TextField();
			textField.setValue(p.getDefaultValue());

			if (p.getDefaultValue() == null)
				textField.setAllowBlank(false);

			vField.add(textField, new VerticalLayoutData(-1, -1, new Margins(0)));
		}

		vField.add(typeDescription, new VerticalLayoutData(-1, -1, new Margins(0)));
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
		if (isTextArea) {
			return textArea.getCurrentValue();
		} else {
			return textField.getValue();
		}
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
		if (isTextArea) {
			return textArea.isValid();
		} else {
			return textField.isValid();
		}
	}


}
