/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.parametersfield;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TabularListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TabularParameter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabularListFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private HBoxLayoutContainer horiz;
	private SimpleContainer listContainer;
	private List<TabItem> items;
	private VerticalLayoutContainer vp;
	private TabularListParameter tabularListParameter;

	/**
	 * @param parameter
	 */
	public TabularListFld(Parameter parameter) {
		super(parameter);
		Log.debug("TabularListField");
		try {
			tabularListParameter = (TabularListParameter) parameter;
			createField();
		} catch (Throwable e) {
			Log.error("TabularListField: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void createField() {
		vp = new VerticalLayoutContainer();
		items = new ArrayList<>();

		listContainer = new SimpleContainer();
		listContainer.add(vp, new MarginData(new Margins(0)));

		/*
		 * List<String> templates = tabularListParameter.getTemplates(); String
		 * list = ""; boolean firstTemplate = true; for (String template :
		 * templates) { list += (firstTemplate ? "" : ", ") +
		 * Format.ellipse(template,50); firstTemplate = false; }
		 * HtmlLayoutContainer templatesList = new
		 * HtmlLayoutContainer("<p>Suitable Data Set Templates: <br>" +
		 * list+"</p>");
		 * templatesList.addStyleName("workflow-parameters-description");
		 */

		fieldContainer = new SimpleContainer();
		// fieldContainer.setResize(true);
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		HtmlLayoutContainer descr;

		if (tabularListParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");
		} else {
			/* listContainer.setToolTip(listParameter.getDescription()); */
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"
					+ tabularListParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(listContainer);
		horiz.add(descr);

		fieldContainer.add(horiz, new MarginData(new Margins(0)));
		fieldContainer.forceLayout();
		addField(null);
	}

	protected void addField(TabItem upperItem) {
		try {

			TabularParameter tabPar = new TabularParameter(
					tabularListParameter.getName(),
					tabularListParameter.getDescription(), null,
					tabularListParameter.getTemplates(),
					tabularListParameter.getDefaultMimeType(),
					tabularListParameter.getSupportedMimeTypes());

			if (upperItem == null) {
				TabItem item = new TabItem(this, tabPar, true);
				items.add(item);
				vp.add(item);// don't use new VerticalLayoutData(1, -1, new
								// Margins(0))
			} else {
				// search the position of the upper item
				int pos = items.indexOf(upperItem);
				if (pos > -1) {
					upperItem.showCancelButton();
					upperItem.forceLayout();
					TabItem item = new TabItem(this, tabPar, false);
					items.add(pos + 1, item);
					vp.insert(item, pos + 1);// don't use new
												// VerticalLayoutData(-1, -1,new
												// Margins(0))
				} else {
					upperItem.forceLayout();
					TabItem item = new TabItem(this, tabPar, true);
					items.add(item);
					vp.add(item);// don't use new VerticalLayoutData(-1, -1, new
									// Margins(0))
				}
			}

			forceLayout();

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param item
	 */
	protected void removeField(TabItem item) {
		items.remove(item);

		if (items.size() == 1) {
			items.get(0).hideCancelButton();
			items.get(0).forceLayout();
		}
		vp.remove(item);
		forceLayout();
	}

	/**
	 * 
	 */
	protected void forceLayout() {
		vp.forceLayout();
		listContainer.forceLayout();
		horiz.forceLayout();
		fieldContainer.forceLayout();
	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		String separator = tabularListParameter.getSeparator();
		// String separator="";
		String value = "";
		boolean first = true;
		for (TabItem item : items) {
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

	@Override
	public boolean isValid() {
		boolean valid = false;
		for (TabItem item : items)
			if (item.isValid()) {
				valid = true;
				break;
			}
		return valid;
	}

}
