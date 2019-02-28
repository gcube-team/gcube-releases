package org.gcube.portlets.user.dataminermanager.client.widgets;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.SelectVariableEvent.SelectVariableEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets.NetCDFPreviewDialog;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
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
		GWT.log("FileViewer: [computationId="+computationId+", fileResource="+fileResource+"]");
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
		final String fileUrl = fileResource.getUrl();
		HtmlLayoutContainer fileNameHtml = new HtmlLayoutContainer("<div class='computation-output-fileName'><p>"
				+ new SafeHtmlBuilder().appendEscaped(fileName).toSafeHtml().asString() + "</p></div>");
		lc.add(fileNameHtml, new VerticalLayoutData(-1, -1, new Margins(0)));
		TextButton downloadBtn = new TextButton("Download File");
		downloadBtn.setIcon(DataMinerManager.resources.download());
		downloadBtn.addSelectHandler(new SelectEvent.SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				GWT.log("Download File url: "+fileUrl);
				Window.open(fileUrl, fileName, "");

			}
		});
				
		TextButton netcdfButton = new TextButton("");
		netcdfButton.setIcon(DataMinerManager.resources.netcdf());
		netcdfButton.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showNetCDFFile();
			}
		});

		lc.add(downloadBtn, new VerticalLayoutData(-1, -1, new Margins(0)));
		if (fileResource.isNetcdf()) {
			lc.add(netcdfButton, new VerticalLayoutData(-1, -1, new Margins(0)));
		}
		add(lc);
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
