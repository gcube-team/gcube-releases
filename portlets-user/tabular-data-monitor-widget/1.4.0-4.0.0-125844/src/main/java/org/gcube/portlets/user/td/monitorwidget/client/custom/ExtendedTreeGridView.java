package org.gcube.portlets.user.td.monitorwidget.client.custom;

import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * @param <M>
 */
public class ExtendedTreeGridView<M> extends TreeGridView<M> {
	// TODO bug in gxt3 3.0.0 fixed in future

	
	@Override
	public void refresh(boolean headerToo) {
		preventScrollToTopOnRefresh = true;
		super.refresh(headerToo);
	}
	
	public boolean getPreventScrollToTopOnRefresh(){
		return preventScrollToTopOnRefresh;
	}
	
	
	public void setPreventScrollToTopOnRefresh(boolean prevent ){
		preventScrollToTopOnRefresh=prevent;
	}
	
	
	
}