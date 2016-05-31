package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public class InfoPanel extends ContentPanel {
	public InfoPanel() {
		this.setHeaderVisible(false);
		this.setBodyBorder(false);
		this.setFrame(false);
		this.setBodyStyleName("infoPanel");
	}

	public void setText(String text) {
		this.removeAll();
		this.addText(text);
		this.layout();
	}
}
