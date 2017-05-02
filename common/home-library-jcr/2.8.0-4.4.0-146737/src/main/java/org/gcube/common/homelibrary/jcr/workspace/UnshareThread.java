package org.gcube.common.homelibrary.jcr.workspace;

import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnshareThread implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(UnshareThread.class);


	private JCRWorkspaceSharedFolder sharedItem;
	private WorkspaceItem itemUnshared;

	public UnshareThread(JCRWorkspaceSharedFolder sharedItem, WorkspaceItem itemUnshared) {
		super();
		this.sharedItem = sharedItem;
		this.itemUnshared = itemUnshared;
	}


	@Override
	public void run() {
		try {			
			itemUnshared = sharedItem.unShare();	
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

}
