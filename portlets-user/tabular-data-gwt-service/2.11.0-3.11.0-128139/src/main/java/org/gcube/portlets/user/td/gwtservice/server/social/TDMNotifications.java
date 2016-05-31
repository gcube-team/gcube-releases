package org.gcube.portlets.user.td.gwtservice.server.social;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareRule;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTabResource;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTemplate;
import org.gcube.portlets.user.td.widgetcommonevent.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TDMNotification notification sharing TR, templates or rules
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDMNotifications extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(TDMNotifications.class);
	private ASLSession aslSession;
	private NotificationType notificationType;
	private ShareTabResource shareTabularResource;
	private ShareRule shareRule;
	private ShareTemplate shareTemplate;

	/**
	 * Share Tabular Resource
	 * 
	 * @param aslSession
	 * @param shareTabularResource
	 */
	public TDMNotifications(ASLSession aslSession,
			ShareTabResource shareTabularResource) {
		this.aslSession = aslSession;
		this.shareTabularResource = shareTabularResource;
		this.notificationType = NotificationType.TDM_TAB_RESOURCE_SHARE;

	}

	/**
	 * 
	 * @param aslSession
	 * @param shareRule
	 */
	public TDMNotifications(ASLSession aslSession, ShareRule shareRule) {
		this.aslSession = aslSession;
		this.shareRule = shareRule;
		this.notificationType = NotificationType.TDM_RULE_SHARE;

	}

	/**
	 * 
	 * @param aslSession
	 * @param shareTemplate
	 */
	public TDMNotifications(ASLSession aslSession, ShareTemplate shareTemplate) {
		this.aslSession = aslSession;
		this.shareTemplate = shareTemplate;
		this.notificationType = NotificationType.TDM_TEMPLATE_SHARE;

	}

	public void run() {
		switch (notificationType) {
		case TDM_TAB_RESOURCE_SHARE:
			tabularResourceNotify();
			break;
		case TDM_TEMPLATE_SHARE:
			templateNotify();
			break;
		case TDM_RULE_SHARE:
			ruleNotify();
			break;
		default:
			break;
		}

	}

	private void tabularResourceNotify() {
		NotificationsManager nm = new ApplicationNotificationsManager(
				aslSession, Constants.APPLICATION_ID);

		for (Contacts contact : shareTabularResource.getContacts()) {
			if (contact.isGroup()) {
				try {
					List<String> members = WorkspaceUtil
							.getMembersByGroup(contact.getLogin());
					for (String member : members) {
						try {
							nm.notifyTDMTabularResourceSharing(member,
									shareTabularResource.getTabResource()
											.getName(), new String(
											Constants.TABULAR_RESOURCE_ID
													+ "="
													+ shareTabularResource
															.getTabResource()
															.getTrId().getId()));

						} catch (Exception e) {
							logger.error("Error in the notification(Type: "
									+ notificationType + " - "
									+ aslSession.getUsername()
									+ " share tabular resource id="
									+ shareTabularResource.getTabResource()
											.getTrId().getId() + " with "
									+ member + "): " + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					logger.error("Error in the notification(No members found for group "
							+ contact.getLogin()
							+ "): "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}

			} else {
				try {
					nm.notifyTDMTabularResourceSharing(contact.getLogin(),
							shareTabularResource.getTabResource().getName(),
							new String(Constants.TABULAR_RESOURCE_ID
									+ "="
									+ shareTabularResource.getTabResource()
											.getTrId().getId()));

				} catch (Exception e) {
					logger.error("Error in the notification(Type: "
							+ notificationType + " - "
							+ aslSession.getUsername()
							+ " share tabular resource id="
							+ shareTabularResource.getTabResource().getTrId()
									.getId() + " with " + contact.getLogin()
							+ "): " + e.getLocalizedMessage());
					e.printStackTrace();
				}

			}
		}
	}

	private void ruleNotify() {
		NotificationsManager nm = new ApplicationNotificationsManager(
				aslSession, Constants.APPLICATION_ID);

		for (Contacts contact : shareRule.getContacts()) {
			if (contact.isGroup()) {

				try {
					List<String> members = WorkspaceUtil
							.getMembersByGroup(contact.getLogin());
					for (String member : members) {
						try {
							nm.notifyTDMObjectSharing(member, notificationType,
									shareRule.getRuleDescriptionData()
											.getName(), null);

						} catch (Exception e) {
							logger.error("Error in the notification(Type: "
									+ notificationType + " - "
									+ aslSession.getUsername()
									+ " share rule id="
									+ shareRule.getRuleDescriptionData()
											.getId() + " with " + member
									+ "): " + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					logger.error("Error in the notification(No members found for group "
							+ contact.getLogin()
							+ "): "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}

			} else {
				try {
					nm.notifyTDMObjectSharing(contact.getLogin(),
							notificationType, shareRule
									.getRuleDescriptionData().getName(), null);

				} catch (Exception e) {
					logger.error("Error in the notification(Type: "
							+ notificationType + " - "
							+ aslSession.getUsername() + " share rule id="
							+ shareRule.getRuleDescriptionData().getId()
							+ " with " + contact.getLogin() + "): "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}

			}
		}

	}

	private void templateNotify() {
		NotificationsManager nm = new ApplicationNotificationsManager(
				aslSession, Constants.APPLICATION_ID);

		for (Contacts contact : shareTemplate.getContacts()) {
			if (contact.isGroup()) {

				try {
					List<String> members = WorkspaceUtil
							.getMembersByGroup(contact.getLogin());
					for (String member : members) {
						try {
							nm.notifyTDMObjectSharing(member, notificationType,
									shareTemplate.getTemplateData().getName(),
									null);

						} catch (Exception e) {
							logger.error("Error in the notification(Type: "
									+ notificationType + " - "
									+ aslSession.getUsername()
									+ " share template id="
									+ shareTemplate.getTemplateData().getId()
									+ " with " + member + "): "
									+ e.getLocalizedMessage());
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					logger.error("Error in the notification(No members found for group "
							+ contact.getLogin()
							+ "): "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}

			} else {
				try {
					nm.notifyTDMObjectSharing(contact.getLogin(),
							notificationType, shareTemplate.getTemplateData()
									.getName(), null);

				} catch (Exception e) {
					logger.error("Error in the notification(Type: "
							+ notificationType + " - "
							+ aslSession.getUsername() + " share template id="
							+ shareTemplate.getTemplateData().getId()
							+ " with " + contact.getLogin() + "): "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}

			}
		}

	}

}
