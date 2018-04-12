package org.gcube.portlets.widgets.wsthreddssync.client.event;

import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class PerformDoSyncEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public class PerformDoSyncEvent extends GwtEvent<PerformDoSyncEventHandler> {
	
	/** The type. */
	public static Type<PerformDoSyncEventHandler> TYPE = new Type<PerformDoSyncEventHandler>();
	private WsThreddsSynchFolderConfiguration conf;
	private WsFolder folder;

	
	/**
	 * Instantiates a new perform do sync event.
	 *
	 * @param folder the folder
	 * @param conf the conf
	 */
	public PerformDoSyncEvent(WsFolder folder, WsThreddsSynchFolderConfiguration conf) {
		this.folder = folder;
		this.conf = conf;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PerformDoSyncEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PerformDoSyncEventHandler handler) {
		handler.onPerformDoSync(this);
	}

	
	/**
	 * Gets the conf.
	 *
	 * @return the conf
	 */
	public WsThreddsSynchFolderConfiguration getConf() {
		return conf;

	}
	
	public WsFolder getFolder() {
		return folder;
	}

}
