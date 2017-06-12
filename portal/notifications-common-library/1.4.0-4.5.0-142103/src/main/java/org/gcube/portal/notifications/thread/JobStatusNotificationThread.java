package org.gcube.portal.notifications.thread;

import java.util.List;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.databook.shared.RunningJob;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A job status notification thread.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class JobStatusNotificationThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(JobStatusNotificationThread.class);
	private RunningJob jobDescriptor;
	private List<GenericItemBean> recipients;
	private NotificationsManager nm;

	/**
	 * @param jobDescriptor
	 * @param applicationQualifier
	 * @param recipients
	 * @param nm
	 */
	public JobStatusNotificationThread(RunningJob jobDescriptor, List<GenericItemBean> recipients,
			NotificationsManager nm) {
		super();
		this.jobDescriptor = jobDescriptor;
		this.recipients = recipients;
		this.nm = nm;
	}


	@Override
	public void run() {

		logger.debug("Starting job notification thread. Recipients of this notification are " + recipients);

		for (GenericItemBean recipient : recipients) {
			try{
				String userIdToNotify = recipient.getName();
				nm.notifyJobStatus(userIdToNotify, jobDescriptor);
			}catch(Exception e){
				logger.error("Failed to send notification", e);
			}
		}
		logger.debug("Notification job thread ended");

	}

}
