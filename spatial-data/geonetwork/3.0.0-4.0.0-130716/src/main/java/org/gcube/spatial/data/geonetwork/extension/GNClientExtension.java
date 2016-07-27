package org.gcube.spatial.data.geonetwork.extension;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.model.Group;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.gcube.spatial.data.geonetwork.utils.GroupUtils;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;

public class GNClientExtension extends GNClient {

	private String gnServiceURL;
	
	public GNClientExtension(String serviceURL) {
		super(serviceURL);
		this.gnServiceURL=serviceURL;
	}

	public void createGroup(String name, String description, String mail)throws GNLibException, GNServerException {
		GNMetadataAdminExtension.createGroup(getConnection(), gnServiceURL, name, description, mail);
	}
	
	
	public Set<Group> getGroups() throws GNLibException, GNServerException{
		return GroupUtils.parseGroupXMLResponse(GNMetadataAdminExtension.getGroups(getConnection(), gnServiceURL));
	}
	
	
	
	public Set<User> getUsers() throws GNLibException, GNServerException{
		return UserUtils.parseUserXMLResponse(GNMetadataAdminExtension.getUsers(getConnection(), gnServiceURL));
	}
	
	
	public void createUser(String name, String password, Profile profile, Collection<Integer> groups) throws GNServerException, GNLibException{
		GNMetadataAdminExtension.createUser(getConnection(), gnServiceURL, name, password, profile, groups);
	}
	
	public void editUser(User toAdd, Collection<Integer> groups) throws GNServerException,GNLibException{
		Set<Integer> alreadyAddedGroups=getGroupsByUser(toAdd.getId());
		alreadyAddedGroups.addAll(groups);
		GNMetadataAdminExtension.editUser(getConnection(), gnServiceURL, toAdd, alreadyAddedGroups);
	}
	
	public Set<Integer> getGroupsByUser(Integer userId) throws GNLibException, GNServerException{
		return UserUtils.parseGroupsByUserResponse(GNMetadataAdminExtension.getUserGroupd(getConnection(), gnServiceURL, userId));
	}
	
	public void assignOwnership(List<Long> toTransferIds,Integer targetUserId,Integer targetGroupId) throws GNServerException{
		try{
			GNMetadataAdminExtension.selectMeta(getConnection(), gnServiceURL, toTransferIds);
			GNMetadataAdminExtension.assignMassiveOwnership(getConnection(), gnServiceURL, targetUserId, targetGroupId);
		}finally{
			GNMetadataAdminExtension.clearMetaSelection(getConnection(), gnServiceURL);
		}
	}
	
	public String getPossibleOwnershipTransfer(Integer userId) throws GNServerException{
		return GNMetadataAdminExtension.allowedOwnershipTransfer(getConnection(), gnServiceURL, userId);
	}
	public String getMetadataOwners() throws GNServerException{
		return GNMetadataAdminExtension.metadataOwners(getConnection(), gnServiceURL);
	}
	
	public void transferOwnership(Integer sourceUserId,Integer sourceGroupId,Integer targetUserId,Integer targetGroupId) throws GNServerException{
		GNMetadataAdminExtension.transferOwnership(getConnection(), gnServiceURL, sourceUserId, sourceGroupId, targetUserId, targetGroupId);
	}
}


