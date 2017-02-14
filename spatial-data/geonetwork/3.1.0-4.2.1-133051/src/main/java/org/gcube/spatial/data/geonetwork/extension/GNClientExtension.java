package org.gcube.spatial.data.geonetwork.extension;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.model.Group;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.gcube.spatial.data.geonetwork.utils.GroupUtils;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.jdom.Element;

import it.geosolutions.geonetwork.GN26Client;
import it.geosolutions.geonetwork.GN3Client;
import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo.MetadataInfo;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.HTTPUtils;

public class GNClientExtension implements GNClient {
	
	private GNClient client;
	
	private ServerAccess access;
	
	public GNClientExtension(ServerAccess access) {		
		this.access=access;
		switch(access.getVersion()){
		case DUE : {
			client=access.isLogin()?new GN26Client(access.getGnServiceURL(), access.getUser(), access.getPassword()):new GN26Client(access.getGnServiceURL());
			break;
		}
		default : {
			client=access.isLogin()?new GN3Client(access.getGnServiceURL(), access.getUser(), access.getPassword()):new GN3Client(access.getGnServiceURL());
			break;
		} 
		}
	}

	public void createGroup(String name, String description, String mail)throws GNLibException, GNServerException {
		GNMetadataAdminExtension.createGroup(getConnection(), access.getGnServiceURL(), name, description, mail);
	}
	
	
	public Set<Group> getGroups() throws GNLibException, GNServerException{
		return GroupUtils.parseGroupXMLResponse(GNMetadataAdminExtension.getGroups(getConnection(), access.getGnServiceURL()));
	}
	
	
	
	public Set<User> getUsers() throws GNLibException, GNServerException{
		return UserUtils.parseUserXMLResponse(GNMetadataAdminExtension.getUsers(getConnection(), access.getGnServiceURL()));
	}
	
	
	public void createUser(String name, String password, Profile profile, Collection<Integer> groups) throws GNServerException, GNLibException{
		GNMetadataAdminExtension.createUser(getConnection(), access.getGnServiceURL(), name, password, profile, groups);
	}
	
	public void editUser(User toAdd, Collection<Integer> groups) throws GNServerException,GNLibException{
		Set<Integer> alreadyAddedGroups=getGroupsByUser(toAdd.getId());
		alreadyAddedGroups.addAll(groups);
		GNMetadataAdminExtension.editUser(getConnection(), access.getGnServiceURL(), toAdd, alreadyAddedGroups);
	}
	
	public Set<Integer> getGroupsByUser(Integer userId) throws GNLibException, GNServerException{
		return UserUtils.parseGroupsByUserResponse(GNMetadataAdminExtension.getUserGroupd(getConnection(), access.getGnServiceURL(), userId));
	}
	
	public void assignOwnership(List<Long> toTransferIds,Integer targetUserId,Integer targetGroupId) throws GNServerException, GNLibException{
		try{
			GNMetadataAdminExtension.selectMeta(getConnection(), access.getGnServiceURL(), toTransferIds);
			GNMetadataAdminExtension.assignMassiveOwnership(getConnection(), access.getGnServiceURL(), targetUserId, targetGroupId);
		}finally{
			GNMetadataAdminExtension.clearMetaSelection(getConnection(), access.getGnServiceURL());
		}
	}
	
	public String getPossibleOwnershipTransfer(Integer userId) throws GNServerException, GNLibException{
		return GNMetadataAdminExtension.allowedOwnershipTransfer(getConnection(), access.getGnServiceURL(), userId);
	}
	public String getMetadataOwners() throws GNServerException, GNLibException{
		return GNMetadataAdminExtension.metadataOwners(getConnection(), access.getGnServiceURL());
	}
	
	public void transferOwnership(Integer sourceUserId,Integer sourceGroupId,Integer targetUserId,Integer targetGroupId) throws GNServerException, GNLibException{
		GNMetadataAdminExtension.transferOwnership(getConnection(), access.getGnServiceURL(), sourceUserId, sourceGroupId, targetUserId, targetGroupId);
	}

	
	
	//***************************** OVERRIDES
	
	@Override
	public boolean ping() {
		return client.ping();
	}

	@Override
	public long insertMetadata(GNInsertConfiguration cfg, File metadataFile) throws GNLibException, GNServerException {
		return client.insertMetadata(cfg, metadataFile);
	}

	@Override
	public long insertRequest(File requestFile) throws GNLibException, GNServerException {
		return client.insertRequest(requestFile);
	}

	@Override
	public void setPrivileges(long metadataId, GNPrivConfiguration cfg) throws GNLibException, GNServerException {
		client.setPrivileges(metadataId, cfg);
	}

	@Override
	public GNSearchResponse search(GNSearchRequest searchRequest) throws GNLibException, GNServerException {
		return client.search(searchRequest);
	}

	@Override
	public GNSearchResponse search(File fileRequest) throws GNLibException, GNServerException {
		return client.search(fileRequest);
	}

	@Override
	public Element get(Long id) throws GNLibException, GNServerException {
		return client.get(id);
	}

	@Override
	public Element get(String uuid) throws GNLibException, GNServerException {
		return client.get(uuid);
	}

	@Override
	public void deleteMetadata(long id) throws GNLibException, GNServerException {
		client.deleteMetadata(id);
	}

	@Override
	public void updateMetadata(long id, File metadataFile) throws GNLibException, GNServerException {
		client.updateMetadata(id, metadataFile);	
	}

	@Override
	public void updateMetadata(long id, File metadataFile, String encoding) throws GNLibException, GNServerException {
		client.updateMetadata(id, metadataFile,encoding);
	}

	@Override
	public MetadataInfo getInfo(Long id) throws GNLibException, GNServerException {
		return client.getInfo(id);
	}

	@Override
	public MetadataInfo getInfo(String uuid) throws GNLibException, GNServerException {
		return client.getInfo(uuid);
	}

	@Override
	public HTTPUtils getConnection() throws GNLibException {
		return client.getConnection();
	}
}


