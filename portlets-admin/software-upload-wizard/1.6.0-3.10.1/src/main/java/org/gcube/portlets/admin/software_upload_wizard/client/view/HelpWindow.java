package org.gcube.portlets.admin.software_upload_wizard.client.view;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class HelpWindow extends Window {

	public HelpWindow() {
		setHeaderVisible(true);
		setHeading("Help");
		setSize(300, 600);
		setMaximizable(false);
		setClosable(true);
		setResizable(false);
		setLayout(new FitLayout());
	}

	public void setContent(String htmlContent){
		if (htmlContent==null || htmlContent.isEmpty()) {
			Log.warn("Help window HTML content is empty!");
			htmlContent = "";
		}
		Log.trace("Updating help content with text of lenght: " + htmlContent.length());
		removeAll();
		Html htmlElement = new Html(htmlContent);
		htmlElement.setStyleName("helpPanel");
		add(htmlElement);
		this.layout();
	}
	
}
