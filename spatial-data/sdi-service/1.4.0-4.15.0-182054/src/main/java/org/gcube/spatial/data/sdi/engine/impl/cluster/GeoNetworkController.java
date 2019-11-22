package org.gcube.spatial.data.sdi.engine.impl.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.InvalidServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.OutdatedServiceEndpointException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceInteractionException;
import org.gcube.spatial.data.sdi.engine.impl.gn.extension.GeoNetworkClient;
import org.gcube.spatial.data.sdi.engine.impl.gn.extension.GeoNetworkUtils;
import org.gcube.spatial.data.sdi.engine.impl.gn.utils.UserUtils;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.gn.Group;
import org.gcube.spatial.data.sdi.model.gn.User;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkDescriptor;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetworkController extends GeoServiceController<GeoNetworkDescriptor>{


	private static String scopeUserPrefix=null;
	private static String scopePasswordPrefix=null;
	private static String ckanUserPrefix=null;
	private static String ckanPasswordPrefix=null;
	private static String managerUserPrefix=null;
	private static String managerPasswordPrefix=null;
	private static String assignedScopePrefix=null;
	private static String defaultGroupPrefix=null;
	private static String sharedGroupPrefix=null;
	private static String confidentialGroupPrefix=null;
	private static String contextGroupPrefix=null;
	private static String suffixesProperty=null;
	private static String priorityProperty=null;

	static{

		scopeUserPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_SCOPE_USER_PREFIX);
		scopePasswordPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_SCOPE_PASSWORD_PREFIX);
		ckanUserPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_CKAN_USER_PREFIX);
		ckanPasswordPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_CKAN_PASSWORD_PREFIX);
		managerUserPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_MANAGER_USER_PREFIX);
		managerPasswordPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_MANAGER_PASSWORD_PREFIX);
		assignedScopePrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_ASSIGNED_SCOPE_PREFIX);
		defaultGroupPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_DEFAULT_GROUP_PREFIX);
		sharedGroupPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_SHARED_GROUP_PREFIX);
		confidentialGroupPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_CONFIDENTIAL_GROUP_PREFIX);
		contextGroupPrefix=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_CONTEXT_GROUP_PREFIX);
		suffixesProperty=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_SUFFIXES);
		priorityProperty=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_PRIORITY);

	}

	private String suffixes;
	private Integer priority;


	public Integer getPriority() {
		return priority;
	}


	public GeoNetworkController(ServiceEndpoint serviceEndpoint) throws InvalidServiceEndpointException {
		super(serviceEndpoint);		
	}


	@Override
	protected void setServiceEndpoint(ServiceEndpoint toSet) {		
		super.setServiceEndpoint(toSet);
		suffixes=getSEProperty(suffixesProperty, true);
		priority=Integer.parseInt(getSEProperty(priorityProperty, true));
	}


	@Override
	protected GeoNetworkDescriptor getLiveDescriptor(){
		GeoNetworkDescriptor descriptor=new GeoNetworkDescriptor();
		descriptor.setBaseEndpoint(baseURL);
		descriptor.setVersion(version);
		String currentScopeName=ScopeUtils.getCurrentScopeName();

		String suffix=getSuffixByScope(currentScopeName);


		ArrayList<Credentials> availableCredentials=new ArrayList<Credentials>();
		availableCredentials.add(adminAccount);

		Credentials context=new Credentials(getSEProperty(scopeUserPrefix+suffix, true), 
				getSEProperty(scopePasswordPrefix+suffix, true), AccessType.CONTEXT_USER);
		availableCredentials.add(context);

		Credentials ckan=new Credentials(getSEProperty(ckanUserPrefix+suffix, true), 
				getSEProperty(ckanPasswordPrefix+suffix, true), AccessType.CKAN);
		availableCredentials.add(ckan);

		String managerUser=getSEProperty(managerUserPrefix+suffix, false);
		if(managerUser!=null) {
			Credentials manager=new Credentials(managerUser,getSEProperty(managerPasswordPrefix+suffix, true),AccessType.CONTEXT_MANAGER);
			availableCredentials.add(manager);
		}

		descriptor.setAccessibleCredentials(availableCredentials);

		descriptor.setPriority(priority);


		descriptor.setContextGroup(getSEProperty(contextGroupPrefix+suffix, true));
		descriptor.setSharedGroup(getSEProperty(sharedGroupPrefix+suffix, true));
		String confidentialGroup=getSEProperty(confidentialGroupPrefix+suffix, false);
		if(confidentialGroup!=null)
			descriptor.setConfidentialGroup(confidentialGroup);


		descriptor.setDefaultGroup(getSEProperty(defaultGroupPrefix+suffix, true));
		descriptor.setPublicGroup(LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_GROUP_ALL));	

		return descriptor;
	}

	@Override
	protected AccessPoint getTheRightAccessPoint(ServiceEndpoint endpoint) {
		for(AccessPoint declaredPoint:endpoint.profile().accessPoints().asCollection()) {
			if(declaredPoint.name().equals(LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_SE_ENDPOINT_NAME))) {
				return declaredPoint;				
			}
		}		
		return null;
	}


	@Override
	protected void initServiceEndpoint() throws OutdatedServiceEndpointException, ServiceInteractionException {
		String scopeName=ScopeUtils.getCurrentScopeName();

		try {
			if(getSuffixByScope(scopeName)==null) throw new InvalidServiceEndpointException("Scope not present in resource");
		}catch(InvalidServiceEndpointException e) {
			insertScopeInfo(ScopeUtils.getCurrentScope());
		}

	}


	private void insertScopeInfo(String scope) throws OutdatedServiceEndpointException, ServiceInteractionException {

		String scopeName=ScopeUtils.getScopeName(scope);

		log.info("Creating scope {} configuration for GeoNetwork at {} ",scopeName,baseURL);
		//Get GN Client
		log.debug("Instantiating client as admin..");
		GeoNetworkClient gnClient=new GeoNetworkClient(baseURL,version,adminAccount.getPassword(),adminAccount.getUsername());
		log.debug("Getting Users and groups from instance..");
		Set<Group> existingGroups=gnClient.getGroups();
		Set<User> existingUsers=gnClient.getUsers();

		// Get parent scopes users and groups
		// configure parent [mng,ctx] to access [sh]
		// configure siblings [mng,ctx] to access [sh]
		// configure users [mng,ctx] to access siblings [sh] and parent [ctx,sh]

		ArrayList<User> sharedGroupExternalUsers=new ArrayList<User>();
		ArrayList<Integer> externalGroupsToAccess=new ArrayList<Integer>();

		// gathering users and groups from siblings 
		log.debug("Getting Siblings information from SE..");
		for(String siblingScope:ISUtils.getSiblingsScopesInResource(serviceEndpoint, scope)) 
			try {
				getSuffixByScope(ScopeUtils.getScopeName(siblingScope));
				for(String username:getUserNamesByScope(siblingScope, true, true, false))
					sharedGroupExternalUsers.add(UserUtils.getByName(existingUsers, username));

				externalGroupsToAccess.addAll(getGroupIDSByScope(siblingScope, true, false, false));
			}catch(InvalidServiceEndpointException e) {
				log.debug("Sibling scope {} not found in resource. Skipping.",siblingScope);	
			}

		log.debug("Getting Parents information from SE..");
		// gathering users and groups from parents
		for(String parentScope:ScopeUtils.getParentScopes(scope))
			try {
				getSuffixByScope(ScopeUtils.getScopeName(parentScope));
				for(String username:getUserNamesByScope(parentScope, true, true, false))
					sharedGroupExternalUsers.add(UserUtils.getByName(existingUsers, username));

				externalGroupsToAccess.addAll(getGroupIDSByScope(parentScope, true, true, false));			
			}catch(InvalidServiceEndpointException e) {
				log.debug("Parent scope {} not found in resource. Skipping it. ",parentScope);
			}



		// Creating groups

		log.debug("Creating groups..");
		String contactMail=LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_MAIL);
		int passwordLength=Integer.parseInt(LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_PASSWORD_LENGTH, "10"));

		// create user & groups [sh,conf,ctx]
		Group shared=GeoNetworkUtils.generateGroup(existingGroups, "Shared_"+scopeName, "Shared metadata group for "+scopeName, contactMail);
		shared=gnClient.createGroup(shared);
		existingGroups.add(shared);

		Group context=GeoNetworkUtils.generateGroup(existingGroups, "Context_"+scopeName, "Context metadata group for "+scopeName, contactMail);
		context=gnClient.createGroup(context);
		existingGroups.add(context);

		Group confidential=GeoNetworkUtils.generateGroup(existingGroups, "Confidential_"+scopeName, "Confidential metadata group for "+scopeName, contactMail);
		confidential=gnClient.createGroup(confidential);
		existingGroups.add(confidential);

		// Giving access to shared group
		log.debug("Giving access to shared group from external scopes..");
		for(User toUpdate:sharedGroupExternalUsers)
			gnClient.editUser(toUpdate, Collections.singleton(shared.getId()));


		log.debug("Creating users..");
		// CKAN -> sh,ctx
		User ckan=GeoNetworkUtils.generateUser(existingUsers, passwordLength, "CKAN_"+scopeName);		
		ckan.setId(gnClient.createUsers(ckan, Arrays.asList(shared.getId(),context.getId())).getId());
		existingUsers.add(ckan);


		// CTX-USR -> sh,ctx,siblings [sh], parents [sh,ctx] 
		User ctx=GeoNetworkUtils.generateUser(existingUsers, passwordLength, "Ctx_"+scopeName);
		ArrayList<Integer> ctxUserAccessibleGroups=new ArrayList<>();
		ctxUserAccessibleGroups.addAll(externalGroupsToAccess);
		ctxUserAccessibleGroups.add(shared.getId());
		ctxUserAccessibleGroups.add(context.getId());
		ctx.setId(gnClient.createUsers(ctx, ctxUserAccessibleGroups).getId());
		existingUsers.add(ctx);

		// CTX-MANAGER -> sh,ctx,conf siblings [sh], parents [sh,ctx]
		User manager=GeoNetworkUtils.generateUser(existingUsers, passwordLength, "Mng_"+scopeName);
		ctxUserAccessibleGroups.add(confidential.getId());
		manager.setId(gnClient.createUsers(manager, ctxUserAccessibleGroups).getId());
		existingUsers.add(manager);

		// Setting information in Service Endpoint
		log.debug("Inserting configuration in Service Endpoint");		

		String generatedSuffix=generateSuffix(suffixes);

		ArrayList<Property> toUpdateProperties=new ArrayList<>();
		toUpdateProperties.add( new Property().nameAndValue(assignedScopePrefix+generatedSuffix, scopeName));
		toUpdateProperties.add( new Property().nameAndValue(scopeUserPrefix+generatedSuffix, ctx.getUsername()));
		toUpdateProperties.add( new Property().nameAndValue(scopePasswordPrefix+generatedSuffix, ISUtils.encryptString(ctx.getPassword())).encrypted(true));
		toUpdateProperties.add( new Property().nameAndValue(ckanUserPrefix+generatedSuffix, ckan.getUsername()));
		toUpdateProperties.add( new Property().nameAndValue(ckanPasswordPrefix+generatedSuffix, ISUtils.encryptString(ckan.getPassword())).encrypted(true));
		toUpdateProperties.add( new Property().nameAndValue(managerUserPrefix+generatedSuffix, manager.getUsername()));
		toUpdateProperties.add( new Property().nameAndValue(managerPasswordPrefix+generatedSuffix, ISUtils.encryptString(manager.getPassword())).encrypted(true));
		toUpdateProperties.add( new Property().nameAndValue(sharedGroupPrefix+generatedSuffix, shared.getId()+""));
		toUpdateProperties.add( new Property().nameAndValue(defaultGroupPrefix+generatedSuffix, shared.getId()+""));
		toUpdateProperties.add( new Property().nameAndValue(confidentialGroupPrefix+generatedSuffix, confidential.getId()+""));
		toUpdateProperties.add( new Property().nameAndValue(contextGroupPrefix+generatedSuffix, context.getId()+""));



		String suffixesList=(suffixes!=null&&!suffixes.trim().isEmpty()&&suffixes!=",")?suffixes+","+generatedSuffix:generatedSuffix;
		toUpdateProperties.add(new Property().nameAndValue(suffixesProperty, suffixesList));
		accessPoint.properties().addAll(toUpdateProperties);
		throw new OutdatedServiceEndpointException("Created scope configuration for "+scopeName); 
	}


	private String getSuffixByScope(String scopeName) {		
		log.debug("looking for scope {} suffix. Available suffixes are : {} ",scopeName,suffixes);
		if(suffixes!=null)
			for(String suff:suffixes.split(",")) 
				if(suff!=null&&!suff.isEmpty()) {
					String propertyValue=getSEProperty(assignedScopePrefix+suff, false);
					if(propertyValue!=null&&propertyValue.equals(scopeName)) return suff;			
				}
		return null;		
	}


	private static String generateSuffix(String existingSuffixes){
		log.debug("Generating suffix, existing are : "+existingSuffixes);
		String[] suffixArray=existingSuffixes==null?new String[0]:existingSuffixes.split(",");
		int maxIndex=0;
		for(String suff:suffixArray){
			try{
				int actual=Integer.parseInt(suff);
				if(actual>maxIndex) maxIndex=actual;
			}catch(Throwable t){

			}
		}
		String generated=(maxIndex+1)+"";
		log.debug("Generated suffix is : "+generated);
		return generated;
	}


	private HashSet<String> getUserNamesByScope(String scope, boolean getContext, boolean getManager, boolean getCKAN){
		HashSet<String> toReturn=new HashSet<String>();
		String scopeName=ScopeUtils.getScopeName(scope);
		String scopeSuffix=getSuffixByScope(scopeName);			
		if(scopeSuffix!=null) { // context might be not configured
			if(getContext)toReturn.add(getSEProperty(scopeUserPrefix+scopeSuffix, true));
			if(getManager) {
				String scopeManagerUserName=getSEProperty(managerUserPrefix+scopeSuffix, false);
				if(scopeManagerUserName!=null) toReturn.add(scopeManagerUserName);
			}
			if(getCKAN) toReturn.add(getSEProperty(ckanUserPrefix+scopeSuffix, true));
		}
		return toReturn;
	}

	private HashSet<Integer> getGroupIDSByScope(String scope, boolean getShared,boolean getContext,boolean getConfidential){
		HashSet<Integer> toReturn=new HashSet<Integer>();
		String scopeName=ScopeUtils.getScopeName(scope);
		String scopeSuffix=getSuffixByScope(scopeName);			
		if(scopeSuffix!=null) {
			if(getShared)toReturn.add(Integer.parseInt(getSEProperty(sharedGroupPrefix+scopeSuffix,true)));
			if(getContext) toReturn.add(Integer.parseInt(getSEProperty(contextGroupPrefix+scopeSuffix, true)));
			if(getConfidential) {
				String confidentialGroupName=getSEProperty(confidentialGroupPrefix+scopeSuffix,true);
				if(confidentialGroupName!=null) toReturn.add(Integer.parseInt(confidentialGroupName));
			}
		}
		return toReturn;
	}


}
