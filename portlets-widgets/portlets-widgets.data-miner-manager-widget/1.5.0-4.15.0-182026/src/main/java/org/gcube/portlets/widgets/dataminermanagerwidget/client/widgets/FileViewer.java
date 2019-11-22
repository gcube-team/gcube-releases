package org.gcube.portlets.widgets.dataminermanagerwidget.client.widgets;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent.SelectVariableEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets.NetCDFPreviewDialog;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
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
public class FileViewer extends SimpleContainer {

	private FileResource fileResource;

	/**
	 * 
	 * @param computationId
	 *            computation id
	 * @param fileResource
	 *            file resource
	 */
	public FileViewer(ComputationId computationId, FileResource fileResource) {
		super();
		GWT.log("FileViewer: [computationId=" + computationId + ", fileResource=" + fileResource + "]");
		this.fileResource = fileResource;
		// this.computationId = computationId;
		init();
		create();
	}

	private void init() {
		setHeight(56);
	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		final String fileName = fileResource.getName();
		String fileDescription = fileResource.getDescription();
		final String fileUrl = fileResource.getUrl();
		
		if(fileDescription==null||fileDescription.isEmpty()){
			fileDescription="Unknow";
		}
		HtmlLayoutContainer fileNameHtml = new HtmlLayoutContainer("<div class='computation-output-fileName'><p>"
				+ new SafeHtmlBuilder().appendEscaped(fileDescription).toSafeHtml().asString() + "</p></div>");
		lc.add(fileNameHtml, new VerticalLayoutData(-1, -1, new Margins(0)));

		TextButton showFileButton = new TextButton("Show");
		showFileButton.setIcon(DataMinerManagerPanel.resources.pageWhite());
		showFileButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showFileCreate();
			}
		});

		TextButton downloadButton = new TextButton("Download");
		downloadButton.setIcon(DataMinerManagerPanel.resources.download());
		downloadButton.addSelectHandler(new SelectEvent.SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				GWT.log("Download File url: " + fileUrl);
				Window.open(fileUrl, fileName, "");

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
		// HorizontalLayoutData buttonBoxLayoutData = new
		// HorizontalLayoutData(-1, -1, new Margins(2));

		if (fileName != null && !fileName.isEmpty()) {
			String fileNameLowerCase = fileName.toLowerCase();
			if (fileNameLowerCase.endsWith(".html") || fileNameLowerCase.endsWith(".htm")
					|| fileNameLowerCase.endsWith(".pdf")|| fileNameLowerCase.endsWith(".log") || fileNameLowerCase.endsWith(".json")
					|| fileNameLowerCase.endsWith(".txt")) {
				buttonsContainer.add(showFileButton, buttonBoxLayoutData);
			}
		}
		buttonsContainer.add(downloadButton, buttonBoxLayoutData);
		if (fileResource.isNetcdf()) {
			buttonsContainer.add(netcdfButton, buttonBoxLayoutData);
		}

		lc.add(buttonsContainer);

		add(lc);
	}

	private void showFileCreate() {
		if (fileResource != null && fileResource.getUrl() != null && !fileResource.getUrl().isEmpty()) {
			GWT.log("ShowFileCreate");

			ShowFileDialog showFileDialog = new ShowFileDialog(fileResource.getUrl());
			showFileDialog.setZIndex(XDOM.getTopZIndex());
			showFileDialog.show();

		}
	}

	private void showNetCDFFile() {
		if (fileResource != null && fileResource.getUrl() != null && !fileResource.getUrl().isEmpty()
				&& fileResource.isNetcdf()) {
			GWT.log("NetcdfBasicWidgetsManager");

			// Example
			SelectVariableEventHandler handler = new SelectVariableEventHandler() {

				@Override
				public void onResponse(SelectVariableEvent event) {
					GWT.log("SelectVariable Response: " + event);

				}
			};

			NetCDFPreviewDialog netcdfDialog = new NetCDFPreviewDialog(fileResource.getUrl());
			netcdfDialog.addSelectVariableEventHandler(handler);
			netcdfDialog.setZIndex(XDOM.getTopZIndex());

		}
	}

}
