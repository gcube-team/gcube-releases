/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.Date;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.DateParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.DateField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DateFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private DateField dateField;
	private DateTimeFormat dateFormat;

	/**
	 * @param parameter
	 *            parameter
	 */
	public DateFld(Parameter parameter) {
		super(parameter);

		DateParameter p = (DateParameter) parameter;

		dateField = new DateField();
		dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

		try {
			Date defaultDate = dateFormat.parse(p.getDefaultValue());
			dateField.setValue(defaultDate);
		} catch (Throwable e) {
			Log.error("DateFld invalid default value" + p.getDefaultValue());
		}

		if (p.getDefaultValue() == null || p.getDefaultValue().isEmpty())
			dateField.setAllowBlank(false);

		HtmlLayoutContainer descr;

		if (p.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			// textField.setToolTip(p.getDescription());
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>" + p.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("Date Value");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(dateField, new VerticalLayoutData(-1, -1, new Margins(0)));
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

	@Override
	public String getValue() {
		String dateS = dateFormat.format(dateField.getCurrentValue());
		return dateS;
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	@Override
	public boolean isValid() {
		return dateField.isValid();
	}

}
