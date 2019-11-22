package org.gcube.portlets.user.tdwx.client.config;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 * @param <M>
 */
public class ExtendedLiveGridView<M> extends LiveGridView<M> {
	
	@SuppressWarnings("unused")
	private int viewIndexReload = -1;

	
	@Override
	public void refresh(boolean headerToo) {
		preventScrollToTopOnRefresh = true;
		super.refresh(headerToo);
	}

	public boolean getPreventScrollToTopOnRefresh() {
		return preventScrollToTopOnRefresh;
	}

	public void setPreventScrollToTopOnRefresh(boolean prevent) {
		preventScrollToTopOnRefresh = prevent;
	}

	public ListStore<M> getCacheStore() {
		return cacheStore;
	}

	public void setCacheStore(ListStore<M> list) {
		cacheStore = list;
	}

	
}