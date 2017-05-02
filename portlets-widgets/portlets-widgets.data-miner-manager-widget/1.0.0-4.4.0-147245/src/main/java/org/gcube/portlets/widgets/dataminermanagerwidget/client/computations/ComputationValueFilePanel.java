package org.gcube.portlets.widgets.dataminermanagerwidget.client.computations;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFile;
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
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationValueFilePanel extends SimpleContainer {
	private ComputationValueFile computationValueFile;

	public ComputationValueFilePanel(ComputationValueFile computationValueFile) {
		this.computationValueFile = computationValueFile;
		init();
		create();
	}
	
	private void init(){
		setBorders(false);
	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		final String fileName = computationValueFile.getFileName();
		final String fileUrl = computationValueFile.getValue();
		HtmlLayoutContainer fileNameHtml;
		if (fileName != null) {
			fileNameHtml = new HtmlLayoutContainer(
					"<div class='computation-output-fileName'><p>"
							+ new SafeHtmlBuilder().appendEscaped(fileName)
									.toSafeHtml().asString() + "</p></div>");
		} else {
			fileNameHtml = new HtmlLayoutContainer(
					"<div class='computation-output-fileName'><p>"
							+ new SafeHtmlBuilder().appendEscaped("NoName")
									.toSafeHtml().asString() + "</p></div>");
		}

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
