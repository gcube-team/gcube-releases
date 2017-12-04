package org.gcube.spatial.data.sdi.engine.impl.gn.extension;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.model.Group;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.GroupUtils;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceInteractionException;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.model.service.Version;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class GeoNetworkClient {

	private ServerAccess access;
	private GNClientExtension theClient=null;
	private GeoNetworkDescriptor descriptor;
	
	
	public GeoNetworkClient(String baseURL, Version version, String password, String user, GeoNetworkDescriptor descriptor) {
		this(baseURL,version,password,user);
		theClient=new GNClientExtension(access);		
	}
	public GeoNetworkClient(String baseURL, Version version, String password, String user) {
		super();
		this.access=new ServerAccess(baseURL,version,password,user);
	}
	
	//************************************** GROUPS AND USERS
	
	
	public Group createGroup(Group group)throws ServiceInteractionException{
		try {
			theClient.createGroup(group.getName(), group.getDescription(), group.getMail(),group.getId());
			long submitTime=System.currentTimeMillis();

			long timeout=LocalConfiguration.getTTL(LocalConfiguration.GEONETWORK_UPDATE_TIMEOUT);
			long wait=LocalConfiguration.getTTL(LocalConfiguration.GEONETWORK_UPDATE_WAIT);

			log.debug("Waiting for created group to be available, timeout is {} ",timeout);
			//wait for update to be available
			Group created=null;
			do{
				try{Thread.sleep(wait);}catch(InterruptedException e){}
				created=GroupUtils.getByName(theClient.getGroups(), group.getName());			
			}while(created==null && (System.currentTimeMillis()-submitTime>=timeout));		

			if(created==null) {
				log.error("GN Update timeout {}ms reached. Group {} not created.",timeout,group);
				throw new ServiceInteractionException("Reached timeout while creating group "+group.getName());
			}
			return created;
		}catch(ServiceInteractionException e) {
			throw e;
		}catch(Throwable t) {
			throw new ServiceInteractionException("Unable to create group. ",t);
		}
	}

	public Set<Group> getGroups() throws ServiceInteractionException {
		try {
			return theClient.getGroups();
		} catch (Exception e) {
			throw new ServiceInteractionException("Unable to get Groups from "+access,e);
		} 
	}


	public Set<User> getUsers() throws ServiceInteractionException{
		try {
			return theClient.getUsers();
		} catch (Exception e) {
			throw new ServiceInteractionException("Unable to get Users from "+access,e);
		}
	}


	public User createUsers(User user, Collection<Integer> groups) throws ServiceInteractionException {
		try{
			theClient.createUser(user.getUsername(), user.getPassword(), user.getProfile(), groups);

			long submitTime=System.currentTimeMillis();

			long timeout=LocalConfiguration.getTTL(LocalConfiguration.GEONETWORK_UPDATE_TIMEOUT);
			long wait=LocalConfiguration.getTTL(LocalConfiguration.GEONETWORK_UPDATE_WAIT);
			log.debug("Waiting for created group to be available, timeout is {} ",timeout);
			//wait for update to be available
			User created=null;
			do{
				try{Thread.sleep(wait);}catch(InterruptedException e){}
				created=UserUtils.getByName(theClient.getUsers(), user.getUsername());			
			}while(created==null && (System.currentTimeMillis()-submitTime>=timeout));		
			if(created==null) {
				log.error("GN Update timeout {}ms reached. User {} not created.",timeout,user.getUsername());
				throw new ServiceInteractionException("Reached timeout while creating user "+user.getUsername());
			}
			return created;
		}catch(ServiceInteractionException e) {
			throw e;
		}catch(Throwable t) {
			throw new ServiceInteractionException("Unable to create User. ",t);
		}

	}

	public void editUser(User toEdit, Collection<Integer> toAddGroups) throws ServiceInteractionException{
		try{
			Set<Integer> alreadyAddedGroups=getGroupsByUser(toEdit.getId());
			alreadyAddedGroups.addAll(toAddGroups);
			GNMetadataAdminExtension.editUser(theClient.getConnection(), access, toEdit, alreadyAddedGroups);
		}catch(Throwable t) {
			throw new ServiceInteractionException("Unable to create User. ",t);
		}
	}
	public Set<Integer> getGroupsByUser(Integer userId) throws ServiceInteractionException{
		try{
			return UserUtils.parseGroupsByUserResponse(GNMetadataAdminExtension.getUserGroupd(theClient.getConnection(), access, userId));
		}catch(Throwable t) {
			throw new ServiceInteractionException(t);
		}
	}
	
	
	//******************************* METADATA INSERTION 
	
		
	public long insertMetadata(String category, String styleSheet,boolean validate, int group, boolean makePublic, File metadataFile) throws GNLibException, GNServerException {
		GNInsertConfiguration configuration=new GNInsertConfiguration();
		configuration.setCategory(category);
		configuration.setStyleSheet(styleSheet);
		configuration.setValidate(validate);
		configuration.setGroup(group+"");
		log.debug("Inserting with {} ",configuration);
		long toReturnId=theClient.insertMetadata(configuration, metadataFile);
		GNPrivConfiguration privileges=(makePublic?getPrivileges(group,
				Integer.parseInt(descriptor.getPublicGroup())):getPrivileges(group));
		
		log.debug("Setting privileges {} on {} ",privileges,toReturnId);
		
		theClient.setPrivileges(toReturnId, privileges);
		return toReturnId;		
	}

	private static final GNPrivConfiguration getPrivileges(Integer...groups ) {
		GNPrivConfiguration toReturn=new GNPrivConfiguration();
		for(Integer group:groups)
			toReturn.addPrivileges(group, EnumSet.of(GNPriv.DOWNLOAD,GNPriv.DYNAMIC,GNPriv.EDITING,GNPriv.FEATURED,GNPriv.NOTIFY,GNPriv.VIEW));
		return toReturn;
	}
	
	
	public void updateMeta(long toUpdateMetaId,File metadataFile) throws GNLibException, GNServerException{
		log.debug("Updating metadata by ID "+toUpdateMetaId);
		theClient.updateMetadata(toUpdateMetaId, metadataFile);
	}
	
	
	//********************************* SEARCH 
	public GNSearchResponse query(GNSearchRequest request) throws GNLibException, GNServerException{
		return theClient.search(request);
	}
	
}
