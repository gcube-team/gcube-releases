package org.gcube.application.aquamaps.aquamapsspeciesview.client;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;

public class ExtendedLiveGridView extends LiveGridView {

	
	public ExtendedLiveGridView() {
	    super();
	    scrollOffset = Math.max(19, XDOM.getScrollBarWidth());
	    setEmptyText("No rows available on the server.");
		setForceFit(true);	
		setAdjustForHScroll(true);
		setCacheSize(80);
	}
	
	
}

