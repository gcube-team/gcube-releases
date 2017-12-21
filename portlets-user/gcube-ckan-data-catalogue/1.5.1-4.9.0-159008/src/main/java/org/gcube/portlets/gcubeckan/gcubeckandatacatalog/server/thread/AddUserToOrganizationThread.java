package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.thread;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Add the user to the other organizations present in the local instance of ckan.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddUserToOrganizationThread extends Thread {

	private static final Log logger = LogFactoryUtil.getLog(AddUserToOrganizationThread.class);
	private DataCatalogue instance;
	private String username;
	private Map<String, String> orgAndCapacity;

	/**
	 * @param instance
	 * @param username
	 * @param orgAndCapacity
	 */
	public AddUserToOrganizationThread(DataCatalogue instance, String username,
			Map<String, String> orgAndCapacity) {
		super();
		this.instance = instance;
		this.username = username;
		this.orgAndCapacity = orgAndCapacity;
	}

	@Override
	public void run() {

		logger.debug("Thread for role association started. Organizations and roles are in the map: " + orgAndCapacity);
		Set<Entry<String, String>> entrySet = orgAndCapacity.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if(instance.getOrganizationByName(entry.getKey()) != null){
				instance.checkRoleIntoOrganization(username, entry.getKey(), RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()));
				instance.assignRolesOtherOrganization(username, entry.getKey(), RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()));
			}
			else if(instance.getGroupByName(entry.getKey()) != null){
				RolesCkanGroupOrOrg roleInGroup = RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()); 
				roleInGroup = roleInGroup.equals(RolesCkanGroupOrOrg.EDITOR) ? RolesCkanGroupOrOrg.MEMBER : roleInGroup;
				instance.checkRoleIntoGroup(username, entry.getKey(), roleInGroup);
			}
		}
		logger.debug("Thread for role association ended");

	}
}
