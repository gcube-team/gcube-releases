package org.gcube.application.datamanagementfacilityportlet.client;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;

public class ExtendedLiveGridView extends LiveGridView {

	
	public ExtendedLiveGridView() {
	    super();
	    scrollOffset = Math.max(19, XDOM.getScrollBarWidth());
	}
	
	
}
