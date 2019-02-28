/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ListStringFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	// private TextField textField;
	private String value;

	/**
	 * @param parameter
	 *            parameter
	 */
	public ListStringFld(Parameter parameter) {
		super(parameter);

		// ObjectParameter p = (ObjectParameter) parameter;
		Log.debug("Create String List field: " + parameter.getName());
		value = parameter.getValue();

		ListParameter listParameter = (ListParameter) parameter;

		StringBuilder columnListHtml = new StringBuilder();
		String tempValue = new String(value);
		int pos = tempValue.indexOf(listParameter.getSeparator());
		while (pos > -1) {
			SafeHtmlBuilder safeValue = new SafeHtmlBuilder();
			safeValue.appendEscaped(tempValue.substring(0, pos));
			columnListHtml.append("<span style='display:block;overflow-wrap: break-word;'>"
					+ safeValue.toSafeHtml().asString() + "</span>");
			tempValue = tempValue.substring(pos + 1, tempValue.length());
			pos = tempValue.indexOf(listParameter.getSeparator());
		}
		if (tempValue != null && !tempValue.isEmpty()) {
			SafeHtmlBuilder safeValue = new SafeHtmlBuilder();
			safeValue.appendEscaped(tempValue);
			columnListHtml.append("<span style='display:block;overflow-wrap: break-word;'>"
					+ safeValue.toSafeHtml().asString() + "</span>");
		} else {
			columnListHtml.append("<span style='display:block;overflow-wrap: break-word;'></span>");
		}

		HtmlLayoutContainer descr;

		descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
		descr.addStyleName("workflow-fieldDescription");

		descr = new HtmlLayoutContainer(
				"<p style='margin-left:5px !important;'>" + parameter.getDescription() + "</p>");
		descr.addStyleName("workflow-fieldDescription");

		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("");
		typeDescription.setStylePrimaryName("workflow-parameters-description");

		HtmlLayoutContainer val = new HtmlLayoutContainer(columnListHtml.toString());
		val.addStyleName("workflow-fieldValue");

		vField.add(val, new VerticalLayoutData(-1, -1, new Margins(0)));

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
		return value;
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
		if (value != null && !value.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
