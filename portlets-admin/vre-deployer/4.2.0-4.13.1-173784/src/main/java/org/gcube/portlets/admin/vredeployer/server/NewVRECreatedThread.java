package org.gcube.portlets.admin.vredeployer.server;

import java.util.List;


import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.model.impl.entity.ContextImpl;
import org.gcube.informationsystem.model.reference.entity.Context;
import org.gcube.informationsystem.resourceregistry.context.ResourceRegistryContextClient;
import org.gcube.informationsystem.resourceregistry.context.ResourceRegistryContextClientFactory;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class NewVRECreatedThread implements Runnable {
	private static Logger _log = LoggerFactory.getLogger(NewVRECreatedThread.class);

	final String SUBJECT = "New VRE Created notification";
	private GroupManager gm;
	
	private String creatorUserName;
	private String creatorFullName;
	private String vreScope;
	private String vreName;
	private long currentScopeGroupId;

	public NewVRECreatedThread(String creatorUserName,	String creatorFullName, String vreScope, String vreName, long currentScopeGroupId) {
		super();
		this.creatorUserName = creatorUserName;
		this.creatorFullName = creatorFullName;
		this.vreScope = vreScope;
		this.vreName = vreName;
		this.currentScopeGroupId = currentScopeGroupId;
		gm = new LiferayGroupManager();
	}

	@Override
	public void run() {
		handleVRECreatedNotificationEmail(creatorUserName, creatorFullName, vreScope);
		handleVRECreatedContextAddonResourceRegistry(vreScope, vreName, currentScopeGroupId);
	}

	private void handleVRECreatedContextAddonResourceRegistry(String vreScope, String vreName, long currentScopeGroupId) {
		try {
			final GCubeGroup voGroup = gm.getGroup(currentScopeGroupId);

			String currentScope = gm.getInfrastructureScope(currentScopeGroupId);
			_log.debug("handleVRECreatedContextAddonResourceRegistry, VO Scope= " + currentScope);
			ScopeProvider.instance.set(currentScope);
			ResourceRegistryContextClient rrClient = ResourceRegistryContextClientFactory.create();
			Context context = new ContextImpl(vreName);

			List<Context> contexts = rrClient.all();
			Context parent = null;
			for(Context c : contexts){
				if(c.getName().compareTo(voGroup.getGroupName()) == 0){
					parent = c;
					break;
				}
			}
			_log.debug("setting parent as= " + parent.getName());
			context.setParent(parent);
			_log.debug("creating context: " + context.toString());
			rrClient.create(context);
			_log.debug("created context OK");
		} catch (Exception e) {
			_log.error("an error occurred wehn creating new context on Resource Registry" , e);
		}
	}


	private void handleVRECreatedNotificationEmail(String newUserUserName,	String newUserFullName, String vreScope) {
		UserManager um = new LiferayUserManager();
		RoleManager rm = new LiferayRoleManager();
		try {
			String rootVoName = PortalContext.getConfiguration().getInfrastructureName();
			long groupId = gm.getGroupIdFromInfrastructureScope("/"+rootVoName);
			long infraManagerRoleId = -1;
			try {
				infraManagerRoleId = rm.getRoleIdByName(GatewayRolesNames.INFRASTRUCTURE_MANAGER.getRoleName());
			}
			catch (RoleRetrievalFault e) {
				_log.warn("There is no (Site) Role " + infraManagerRoleId + " in this portal. Will not notify about newly VRE creations.");
				return;
			}
			_log.trace("Root is: " + rootVoName + " Scanning roles ....");	

			List<GCubeUser> managers = um.listUsersByGroupAndRole(groupId, infraManagerRoleId); 
			if (managers == null || managers.isEmpty()) {
				_log.warn("There are no users with (Site) Role " + infraManagerRoleId + " on " + rootVoName + " in this portal.  Will not notify about newly VRE creations.");
			}
			else {
				for (GCubeUser manager : managers) {
					sendNotification(manager, newUserUserName, newUserFullName, vreScope);
					_log.info("sent email to manager: " + manager.getEmail());	
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void sendNotification(GCubeUser manager, String creatorUserName, String creaotrFullName, String vreScope) {
		EmailNotification toSend = new EmailNotification(manager.getEmail(), SUBJECT, 
				getHTMLEmail(manager.getFirstName(), creatorUserName, creaotrFullName, vreScope), null);
		toSend.sendEmail();
	}

	private static String getHTMLEmail(String userFirstName, String creatorUsername, String creatorFullName, String vreScope) {
		String sender = creatorFullName + " ("+creatorUsername+") ";

		StringBuilder body = new StringBuilder();

		body.append("<body><br />")
		.append("<div style=\"color:#000; font-size:13px; font-family:'lucida grande',tahoma,verdana,arial,sans-serif;\">")
		.append("Dear ").append(userFirstName).append(",")  //dear <user>
		.append("<p>").append(sender).append(" ").append("as VRE Manager allowed the creation of the following VRE: ") // has done something
		.append(vreScope)
		.append("</div><br />")
		.append("<p>Please note: the actual deploy may have been triggered by another VRE Manager</p>")
		.append("<p>You received this email because you are an Infrastructure Manager in this portal</p>")
		.append("</div></p>")
		.append("</body>");

		return body.toString();
	}

}
