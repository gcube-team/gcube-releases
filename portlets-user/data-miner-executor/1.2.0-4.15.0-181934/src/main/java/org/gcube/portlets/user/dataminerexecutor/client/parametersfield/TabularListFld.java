/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client.parametersfield;

import java.util.ArrayList;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.TabularListParameter;
import org.gcube.portlets.user.dataminerexecutor.client.DataMinerExecutor;
import org.gcube.portlets.user.dataminerexecutor.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminerexecutor.client.workspace.DownloadWidget;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabularListFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private VerticalLayoutContainer vl;
	private ArrayList<String> list;
	private String value;

	/**
	 * @param parameter
	 *            parameter
	 */
	public TabularListFld(Parameter parameter) {
		super(parameter);

		Log.debug("Create Tabular List field: " + parameter.getName());
		value = parameter.getValue();

		TabularListParameter tabularListParameter = (TabularListParameter) parameter;

		HtmlLayoutContainer descr;

		descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
		descr.addStyleName("workflow-fieldDescription");

		descr = new HtmlLayoutContainer(
				"<p style='margin-left:5px !important;'>" + parameter.getDescription() + "</p>");
		descr.addStyleName("workflow-fieldDescription");

		vl = new VerticalLayoutContainer();
		createListField(tabularListParameter);

		SimpleContainer dataContainer = new SimpleContainer();
		dataContainer.addStyleName("workflow-fieldValue");
		dataContainer.add(vl);

		//
		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("");
		typeDescription.setStylePrimaryName("workflow-parameters-description");

		vField.add(dataContainer, new VerticalLayoutData(-1, -1, new Margins(0)));
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

	private void createListField(TabularListParameter tabularListParameter) {
		String tempValue = new String(value);
		list = new ArrayList<>();

		int pos = tempValue.indexOf(tabularListParameter.getSeparator());
		while (pos > -1) {
			SafeHtmlBuilder safeValue = new SafeHtmlBuilder();
			safeValue.appendEscaped(tempValue.substring(0, pos));
			list.add(safeValue.toSafeHtml().asString());
			tempValue = tempValue.substring(pos + 1, tempValue.length());
			pos = tempValue.indexOf(tabularListParameter.getSeparator());
		}
		if (tempValue != null && !tempValue.isEmpty()) {
			SafeHtmlBuilder safeValue = new SafeHtmlBuilder();
			safeValue.appendEscaped(tempValue);
			list.add(safeValue.toSafeHtml().asString());
		}

		for (int i = 0; i < list.size(); i++) {
			String url = list.get(i);
			TextButton downloadButton = new TextButton("");
			downloadButton.setIcon(DataMinerExecutor.resources.download());
			downloadButton.setTitle(url);
			downloadButton.setItemId(String.valueOf(i));
			downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					downloadRequest(event);
				}

			});
			vl.add(downloadButton, new VerticalLayoutData(-1, -1, new Margins(0)));

		}

	}

	private void downloadRequest(SelectEvent event) {
		TextButton button = (TextButton) event.getSource();
		String id = button.getItemId();
		try {
			int i = Integer.valueOf(id);
			if (i > -1 && i < list.size() && !list.isEmpty()) {
				downloadUrl(list.get(i));
			}
		} catch (NumberFormatException e) {
			Log.error("Invalid id: " + e.getLocalizedMessage(), e);
		}
	}

	private void downloadUrl(String url) {
		if (value != null) {
			DownloadWidget downloadWidget = new DownloadWidget();
			downloadWidget.downloadUrl(value);
		} else {
			UtilsGXT3.info("Attention", "The url is invalid: " + value);
		}

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
