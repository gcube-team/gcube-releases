/**
 * 
 */
package org.gcube.portlets.user.dataminerexecutor.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminerexecutor.client.DataMinerExecutor;
import org.gcube.portlets.user.dataminerexecutor.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminerexecutor.client.workspace.DownloadWidget;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.button.TextButton;
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
public class TabularFld extends AbstractFld {

	private SimpleContainer fieldContainer;
	private String value;

	/**
	 * @param parameter
	 *            parameter
	 */
	public TabularFld(Parameter parameter) {
		super(parameter);

		Log.debug("Create Tabular field: " + parameter.getName());
		value = parameter.getValue();

		// Description
		HtmlLayoutContainer descr;

		descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
		descr.addStyleName("workflow-fieldDescription");

		descr = new HtmlLayoutContainer(
				"<p style='margin-left:5px !important;'>" + parameter.getDescription() + "</p>");
		descr.addStyleName("workflow-fieldDescription");

		// Value
		TextButton downloadButton = new TextButton("");
		downloadButton.setIcon(DataMinerExecutor.resources.download());
		downloadButton.setTitle(value);
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				downloadUrl();
			}
		});

		SimpleContainer dataContainer = new SimpleContainer();
		dataContainer.addStyleName("workflow-fieldValue");
		dataContainer.add(downloadButton);

		//
		SimpleContainer vContainer = new SimpleContainer();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();

		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer("Table Value");
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

	private void downloadUrl() {
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
