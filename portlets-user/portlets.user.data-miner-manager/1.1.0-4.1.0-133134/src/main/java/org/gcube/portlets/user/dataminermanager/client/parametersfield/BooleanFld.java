/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import org.gcube.portlets.user.dataminermanager.shared.StringUtil;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class BooleanFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private CheckBox checkBox = new CheckBox();

	/**
	 * @param parameter
	 */
	public BooleanFld(Parameter parameter) {
		super(parameter);
		fieldContainer = new SimpleContainer();
		HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		ObjectParameter p = (ObjectParameter) parameter;

		if (p.getDefaultValue() != null)
			checkBox.setValue(!p.getDefaultValue().toUpperCase()
					.equals("FALSE"));
		else
			checkBox.setValue(false);
		checkBox.setBoxLabel(StringUtil.getCapitalWords(p.getName()));
	
		HtmlLayoutContainer descr;

		if (p.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			//checkBox.setToolTip(p.getDescription());
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"
					+ p.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(checkBox, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);

	}

	@Override
	public String getValue() {
		return (checkBox.getValue() ? "true" : "false");
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

}
