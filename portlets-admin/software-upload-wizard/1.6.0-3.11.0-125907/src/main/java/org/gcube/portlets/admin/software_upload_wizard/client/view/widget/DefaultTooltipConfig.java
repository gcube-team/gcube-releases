package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class DefaultTooltipConfig extends ToolTipConfig {
	public DefaultTooltipConfig() {
		this.setAnchor("left");
	    this.setCloseable(false);
	    this.setDismissDelay(0);
	    this.setMouseOffset(new int[] {0,0});
	}
}
