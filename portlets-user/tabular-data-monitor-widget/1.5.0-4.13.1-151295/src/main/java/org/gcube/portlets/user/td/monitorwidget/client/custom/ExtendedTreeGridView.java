package org.gcube.portlets.user.td.monitorwidget.client.custom;

import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 * @param <M> Type
 */
public class ExtendedTreeGridView<M> extends TreeGridView<M> {
	

	
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