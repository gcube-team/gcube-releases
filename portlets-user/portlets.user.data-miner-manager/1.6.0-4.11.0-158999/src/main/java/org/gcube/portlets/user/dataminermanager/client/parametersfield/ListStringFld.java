/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.shared.parameters.ListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ListStringFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private HBoxLayoutContainer horiz;
	private SimpleContainer listContainer;
	private VerticalLayoutContainer vp;
	private List<StringItem> items;
	private ListParameter listParameter;

	/**
	 * 
	 * @param parameter
	 *            parameter
	 */
	public ListStringFld(Parameter parameter) {
		super(parameter);

		listParameter = (ListParameter) parameter;

		listContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		items = new ArrayList<StringItem>();
		listContainer.add(vp, new MarginData(new Margins()));

		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (listParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			/* listContainer.setToolTip(listParameter.getDescription()); */
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'>" + listParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(listContainer, new BoxLayoutData(new Margins(0)));
		horiz.add(descr, new BoxLayoutData(new Margins(0)));

		fieldContainer.add(horiz);
		addField(null);
	}

	protected void addField(StringItem upperItem) {

		ObjectParameter objPar = new ObjectParameter(listParameter.getName(), listParameter.getDescription(),
				listParameter.getType(), null);

		if (upperItem == null) {
			StringItem item = new StringItem(this, objPar, true);
			items.add(item);
			vp.add(item);
		} else {
			// search the position of the upper item
			int pos = items.indexOf(upperItem);
			if (pos > -1) {
				upperItem.showCancelButton();
				upperItem.forceLayout();
				StringItem item = new StringItem(this, objPar, false);
				items.add(pos + 1, item);
				vp.insert(item, pos + 1);// don't use new VerticalLayoutData(1,
											// -1,new Margins(0))
			} else {
				upperItem.forceLayout();
				StringItem item = new StringItem(this, objPar, true);
				items.add(item);
				vp.add(item);// don't use new VerticalLayoutData(-1, -1, new
								// Margins(0))
			}
		}

		forceLayout();

	}

	protected void forceLayout() {
		vp.forceLayout();
		horiz.forceLayout();
		fieldContainer.forceLayout();
	}

	protected void removeField(StringItem item) {
		items.remove(item);

		vp.remove(item);

		if (items.size() == 1) {
			items.get(0).hideCancelButton();
			items.get(0).forceLayout();
		}

		forceLayout();
	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		String separator = listParameter.getSeparator();
		String value = "";
		boolean first = true;
		for (StringItem item : items) {
			String itemValue = item.getValue();
			if (itemValue != null && !itemValue.contentEquals("")) {
				value += (first ? "" : separator) + itemValue;
				first = false;
			}
		}
		return value;
	}

	/**
	 * 
	 */
	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	/**
	 * 
	 */
	@Override
	public boolean isValid() {
		boolean valid = false;
		for (StringItem item : items)
			if (item.isValid()) {
				valid = true;
				break;
			}
		return valid;
	}

}
