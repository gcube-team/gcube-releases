/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import java.util.Date;

import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TimeParameter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TimeField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TimeFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private TimeField timeField;
	private DateTimeFormat timeFormat;

	/**
	 * @param parameter
	 *            parameter
	 */
	public TimeFld(Parameter parameter) {
		super(parameter);

		TimeParameter p = (TimeParameter) parameter;

		timeField = new TimeField();
		timeField.setTriggerAction(TriggerAction.ALL);
		timeField.setFormat(DateTimeFormat.getFormat("HH:mm:ss"));
		timeFormat = DateTimeFormat.getFormat("HH:mm:ss");

		try {
			Date defaultDate = timeFormat.parse(p.getDefaultValue());
			timeField.setValue(defaultDate);
		} catch (Throwable e) {
			Log.error("DateFld invalid default value" + p.getDefaultValue());
		}

		if (p.getDefaultValue() == null || p.getDefaultValue().isEmpty())
			timeField.setAllowBlank(false);

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
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("Time Value");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(timeField, new VerticalLayoutData(-1, -1, new Margins(0)));
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
		String timeS = timeFormat.format(timeField.getCurrentValue());
		return timeS;
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
		return timeField.isValid();
	}

}
