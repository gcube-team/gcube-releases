/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class BooleanFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private SimpleComboBox<String> listBox;

	/**
	 * @param parameter
	 *            parameter
	 */
	public BooleanFld(Parameter parameter) {
		super(parameter);
		fieldContainer = new SimpleContainer();
		HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		ObjectParameter p = (ObjectParameter) parameter;

		listBox = new SimpleComboBox<String>(new StringLabelProvider<>());
		listBox.add("true");
		listBox.add("false");
		listBox.setAllowBlank(false);
		listBox.setForceSelection(true);
		listBox.setEditable(false);
		listBox.setTriggerAction(TriggerAction.ALL);

		if (p.getDefaultValue() != null && !p.getDefaultValue().isEmpty()) {
			Boolean b = Boolean.valueOf(p.getDefaultValue());
			if (b) {
				listBox.setValue("true");
			} else {
				listBox.setValue("false");
			}

		} else {
			listBox.setValue("false");
		}

		HtmlLayoutContainer descr;

		if (p.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			// listBox.setToolTip(p.getDescription());
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>" + p.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}
		horiz.add(listBox, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		fieldContainer.forceLayout();

	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		return listBox.getCurrentValue();
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
		return listBox.isValid();
	}

}
