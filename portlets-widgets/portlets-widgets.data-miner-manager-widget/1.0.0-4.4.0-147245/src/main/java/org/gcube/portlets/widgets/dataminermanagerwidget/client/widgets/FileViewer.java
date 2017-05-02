package org.gcube.portlets.widgets.dataminermanagerwidget.client.widgets;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.FileResource;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FileViewer extends SimpleContainer {

	private FileResource fileResource;
	

	/**
	 * @param mapImages
	 */
	public FileViewer(ComputationId computationId,
			FileResource fileResource) {
		super();
		this.fileResource = fileResource;
		//this.computationId = computationId;
		init();
		create();
	}
	
	private void init(){
		setHeight(56);
	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		final String fileName = fileResource.getName();
		final String fileUrl = fileResource.getUrl();
		HtmlLayoutContainer fileNameHtml = new HtmlLayoutContainer(
				"<div class='computation-output-fileName'><p>"
						+ new SafeHtmlBuilder().appendEscaped(fileName)
								.toSafeHtml().asString() + "</p></div>");
		lc.add(fileNameHtml, new VerticalLayoutData(-1, -1, new Margins(0)));
		TextButton downloadBtn = new TextButton("Download File");
		downloadBtn.setIcon(DataMinerManagerPanel.resources.download());
		downloadBtn.addSelectHandler(new SelectEvent.SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				com.google.gwt.user.client.Window.open(fileUrl, fileName, "");

			}
		});

		lc.add(downloadBtn, new VerticalLayoutData(-1, -1, new Margins(0)));
		add(lc);
	}
		
		
}
