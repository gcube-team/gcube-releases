package org.gcube.common.homelibrary.jcr.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkAsReadThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(MarkAsReadThread.class);
	
	
	private JCRWorkspace workspace;
	private String itemID;
	private boolean isRead;

	public MarkAsReadThread(JCRWorkspace workspace, String itemID, boolean isRead){
		super();
		this.workspace = workspace;
		this.itemID = itemID;
		this.isRead = isRead;
	}
	
	
	@Override
	public void run() {
		try {			
			workspace.getItem(itemID).markAsRead(isRead);	
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
}
