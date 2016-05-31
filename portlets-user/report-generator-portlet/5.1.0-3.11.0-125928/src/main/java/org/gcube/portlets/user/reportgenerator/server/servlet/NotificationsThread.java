package org.gcube.portlets.user.reportgenerator.server.servlet;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NotificationsThread implements Runnable {
	private static final Logger _log = LoggerFactory.getLogger(NotificationsThread.class);
	
	private static final String WORKSPACE_PORTLET_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl";


	private NotificationsManager nm;
	private List<String> userIdsToBeNotified;
	private WorkspaceItem subjectItem;
	private WorkspaceFolder sharedFolder;
	
	
	public NotificationsThread(ASLSession session, List<String> userIdsToBeNotified, WorkspaceItem item, WorkspaceFolder sharedFolder)  {
		nm = new ApplicationNotificationsManager(session, WORKSPACE_PORTLET_ID);
		this.userIdsToBeNotified = userIdsToBeNotified;
		subjectItem = item;		
		this.sharedFolder = sharedFolder;
	}

	@Override
	public void run() {
		for (String userId : userIdsToBeNotified) {
			try {
			//	boolean notifResult = nm.notifyUpdatedItem(userId, subjectItem, sharedFolder);
				//_log.trace("Update Notification sent to " + userId + " result="+notifResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
}
