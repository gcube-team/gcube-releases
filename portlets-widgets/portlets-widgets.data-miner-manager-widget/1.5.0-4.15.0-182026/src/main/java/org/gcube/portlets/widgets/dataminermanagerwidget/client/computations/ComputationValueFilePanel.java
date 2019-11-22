package org.gcube.portlets.widgets.dataminermanagerwidget.client.computations;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFile;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.widgets.ShowFileDialog;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent.SelectVariableEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets.NetCDFPreviewDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
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
public class ComputationValueFilePanel extends SimpleContainer {
	private ComputationValueFile computationValueFile;

	public ComputationValueFilePanel(ComputationValueFile computationValueFile) {
		this.computationValueFile = computationValueFile;
		init();
		create();
	}

	private void init() {
		setBorders(false);
	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		final String fileName = computationValueFile.getFileName();
		final String fileUrl = computationValueFile.getValue();
		HtmlLayoutContainer fileNameHtml;
		if (fileName != null) {
			fileNameHtml = new HtmlLayoutContainer("<div class='computation-output-fileName'><p>"
					+ new SafeHtmlBuilder().appendEscaped(fileName).toSafeHtml().asString() + "</p></div>");
		} else {
			fileNameHtml = new HtmlLayoutContainer("<div class='computation-output-fileName'><p>"
					+ new SafeHtmlBuilder().appendEscaped("NoName").toSafeHtml().asString() + "</p></div>");
		}

		lc.add(fileNameHtml, new VerticalLayoutData(-1, -1, new Margins(0)));

		TextButton showFileButton = new TextButton("Show");
		showFileButton.setIcon(DataMinerManagerPanel.resources.pageWhite());
		showFileButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showFileCreate();
			}
		});

		TextButton downloadBtn = new TextButton("Download");
		downloadBtn.setIcon(DataMinerManagerPanel.resources.download());
		downloadBtn.addSelectHandler(new SelectEvent.SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				com.google.gwt.user.client.Window.open(fileUrl, fileName, "");

			}
		});

		TextButton netcdfButton = new TextButton("");
		netcdfButton.setIcon(DataMinerManagerPanel.resources.netcdf());
		netcdfButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showNetCDFFile();
			}
		});

		HBoxLayoutContainer buttonsContainer = new HBoxLayoutContainer();
		buttonsContainer.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		BoxLayoutData buttonBoxLayoutData = new BoxLayoutData(new Margins(2, 2, 2, 2));

		if (fileName != null && !fileName.isEmpty()) {
			String fileNameLowerCase = fileName.toLowerCase();
			if (fileNameLowerCase.endsWith(".html") || fileNameLowerCase.endsWith(".htm")
					|| fileNameLowerCase.endsWith(".pdf") || fileNameLowerCase.endsWith(".log")
					|| fileNameLowerCase.endsWith(".json") || fileNameLowerCase.endsWith(".txt")) {
				buttonsContainer.add(showFileButton, buttonBoxLayoutData);
			}
		}
		// c.add(previewButton, new VerticalLayoutData(-1, -1, new Margins(0)));
		buttonsContainer.add(downloadBtn, buttonBoxLayoutData);
		if (computationValueFile.isNetcdf()) {
			buttonsContainer.add(netcdfButton, buttonBoxLayoutData);
		}

		lc.add(buttonsContainer);
		add(lc);
	}

	private void showFileCreate() {
		if (computationValueFile != null && computationValueFile.getValue() != null
				&& !computationValueFile.getValue().isEmpty()) {
			GWT.log("ShowFileCreate");

			ShowFileDialog showFileDialog = new ShowFileDialog(computationValueFile.getValue());
			showFileDialog.setZIndex(XDOM.getTopZIndex());
			showFileDialog.show();

		}
	}

	private void showNetCDFFile() {
		if (computationValueFile != null && computationValueFile.getValue() != null
				&& !computationValueFile.getValue().isEmpty() && computationValueFile.isNetcdf()) {
			GWT.log("NetcdfBasicWidgetsManager");

			SelectVariableEventHandler handler = new SelectVariableEventHandler() {

				@Override
				public void onResponse(SelectVariableEvent event) {
					GWT.log("SelectVariable Response: " + event);

				}
			};

			NetCDFPreviewDialog netcdfDialog = new NetCDFPreviewDialog(computationValueFile.getValue());
			netcdfDialog.addSelectVariableEventHandler(handler);
			netcdfDialog.setZIndex(XDOM.getTopZIndex());

		}
	}

}
