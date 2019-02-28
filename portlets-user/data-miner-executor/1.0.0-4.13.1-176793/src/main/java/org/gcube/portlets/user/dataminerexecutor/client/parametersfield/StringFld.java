/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

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
//import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StringFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	// private TextField textField;
	private String value;

	/**
	 * @param parameter
	 *            parameter
	 */
	public StringFld(Parameter parameter) {
		super(parameter);

		// ObjectParameter p = (ObjectParameter) parameter;
		Log.debug("Create String field: " + parameter.getName());
		value = parameter.getValue();

		HtmlLayoutContainer descr;

		descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
		descr.addStyleName("workflow-fieldDescription");

		descr = new HtmlLayoutContainer(
				"<p style='margin-left:5px !important;'>" + parameter.getDescription() + "</p>");
		descr.addStyleName("workflow-fieldDescription");
		
		String typology;
		switch (parameter.getTypology()) {
		case COLUMN:
			typology="Column Value";
		case WKT:
			typology="Wkt Value";
		case DATE:
			typology="Date Value";
		case TIME:
			typology="Time Value";
		case ENUM:
			typology="Enum Value";
		case OBJECT:
			typology=getObjectParameterTypology();
			break;
		default:
			typology="Value";
			break;
		}

		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(typology);
		typeDescription.setStylePrimaryName("workflow-parameters-description");

		HtmlLayoutContainer val = new HtmlLayoutContainer(
				"<span style='overflow-wrap: break-word;'>" + parameter.getValue() + "</span>");
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

	
	private String getObjectParameterTypology(){
		ObjectParameter objectParameter = (ObjectParameter) parameter;
		String type = objectParameter.getType();
		if (type.contentEquals(Integer.class.getName())) {
			return "Integer Value";
		} else if (type.contentEquals(String.class.getName())) {
			return "String Value";
		} else if (type.contentEquals(Boolean.class.getName())) {
			return "Boolean Value";
		} else if (type.contentEquals(Double.class.getName())) {
			return "Double Value";
		} else if (type.contentEquals(Float.class.getName())) {
			return "Float Value";
		} else
			return "Value";
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
