package org.gcube.common.homelibrary.jcr.workspace;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnshareThread implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(UnshareThread.class);


	private JCRWorkspaceSharedFolder sharedItem;
	private WorkspaceFolder itemUnshared;
	private String user;

	public UnshareThread(JCRWorkspaceSharedFolder sharedItem, WorkspaceFolder itemUnshared, String user) {
		super();
		this.sharedItem = sharedItem;
		this.itemUnshared = itemUnshared;
		this.user = user;
	}


	@Override
	public void run() {
		try {			
			itemUnshared = sharedItem.unShareNode(user);	
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

}
