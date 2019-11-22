package org.gcube.spatial.data.geonetwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.configuration.ConfigurationManager;
import org.gcube.spatial.data.geonetwork.configuration.ScopeConfigurationUtils;
import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.gcube.spatial.data.geonetwork.extension.GNClientExtension;
import org.gcube.spatial.data.geonetwork.extension.ServerAccess;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Group;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.GeoNetworkException;
import org.gcube.spatial.data.geonetwork.model.faults.InvalidInsertConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;
import org.gcube.spatial.data.geonetwork.utils.GroupUtils;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.geonetwork.utils.UserUtils;
import org.geotoolkit.xml.XML;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.opengis.metadata.Metadata;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.gn3.GN3MetadataGetInfo.MetadataInfo;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetwork implements GeoNetworkAdministration {

	private static XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());			

	public static GeoNetworkAdministration get() throws Exception{
		return new GeoNetwork(ConfigurationManager.get());
	}

	public static GeoNetworkAdministration get(Configuration config) throws MissingServiceEndpointException, GNLibException, GNServerException, AuthorizationException, MissingConfigurationException{
		return new GeoNetwork(config);
	}

	private Configuration config;


	private GeoNetwork(Configuration config) throws MissingServiceEndpointException, GNLibException, GNServerException, AuthorizationException, MissingConfigurationException{
		this.config=config;
		log.debug("Checking scope configuration..");
		try{
			ScopeConfiguration scopeConfig=config.getScopeConfiguration();
		}catch(MissingConfigurationException e){
			log.trace("Configuration not found for current scope "+ScopeUtils.getCurrentScope()+"acquiring one..");
			acquireConfiguration();
		}
	}

	@Override
	public Configuration getConfiguration(){
		return config;
	}
	//************** READ ONLY METHODS, LOGIN OPTIONAL

	@Override
	public void login(LoginLevel lvl) throws AuthorizationException, MissingServiceEndpointException, MissingConfigurationException {
		String user=null;
		String password=null;				
		switch(lvl){
		case ADMIN :{ user=config.getAdminAccount().getUser();
		password=config.getAdminAccount().getPassword();
		break;
		}
		case CKAN : {
			ScopeConfiguration scopeConfiguration=config.getScopeConfiguration();
			Account account=scopeConfiguration.getAccounts().get(Account.Type.CKAN);
			user=account.getUser();
			password=account.getPassword();
			break;
		}
		default: {
			ScopeConfiguration scopeConfiguration=config.getScopeConfiguration();
			Account account=scopeConfiguration.getAccounts().get(Account.Type.SCOPE);
			user=account.getUser();
			password=account.getPassword();
			break;
		}		
		}
		setAccess(new ServerAccess(config.getGeoNetworkEndpoint(),config.getGeoNetworkVersion(),true,password,user,lvl));
		
		getClient();
	}


	private ServerAccess getAccess(ScopeConfiguration scopeConfig,LoginLevel lvl) throws MissingServiceEndpointException{
		String user=null;
		String password=null;				
		switch(lvl){
		case ADMIN :{ user=config.getAdminAccount().getUser();
		password=config.getAdminAccount().getPassword();
		break;
		}
		case CKAN : {
			ScopeConfiguration scopeConfiguration=scopeConfig;
			Account account=scopeConfiguration.getAccounts().get(Account.Type.CKAN);
			user=account.getUser();
			password=account.getPassword();
			break;
		}
		default: {
			ScopeConfiguration scopeConfiguration=scopeConfig;
			Account account=scopeConfiguration.getAccounts().get(Account.Type.SCOPE);
			user=account.getUser();
			password=account.getPassword();
			break;
		}		
		}
		return new ServerAccess(config.getGeoNetworkEndpoint(),config.getGeoNetworkVersion(),true,password,user,lvl);
	}


	@Override
	public void logout() throws MissingServiceEndpointException {
		setAccess(new ServerAccess(config.getGeoNetworkEndpoint(),config.getGeoNetworkVersion()));		
	}


	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#query(it.geosolutions.geonetwork.util.GNSearchRequest)
	 */
	@Override
	public GNSearchResponse query(GNSearchRequest request) throws GNLibException, GNServerException, MissingServiceEndpointException{
		return getClient().search(request);
	}

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#query(java.io.File)
	 */
	@Override
	public GNSearchResponse query(File fileRequest) throws GNLibException, GNServerException, MissingServiceEndpointException{
		return getClient().search(fileRequest);
	}

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#getById(long)
	 */
	@Override
	public Metadata getById(long id) throws GNLibException, GNServerException, JAXBException, MissingServiceEndpointException{
		String xml=out.outputString(getClient().get(id));
		return (Metadata) XML.unmarshal(xml);		
	}

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#getById(java.lang.String)
	 */
	@Override
	public Metadata getById(String UUID) throws GNLibException, GNServerException, JAXBException, MissingServiceEndpointException{
		return (Metadata) XML.unmarshal(getByIdAsRawString(UUID));
	}

	@Override
	public String getByIdAsRawString(String UUID) throws GNLibException,
	GNServerException, JAXBException, MissingServiceEndpointException {
		return out.outputString(getClient().get(UUID));
	}

	//************** WRITE METHODS, LOGIN REQUIRED

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#setPrivileges(long, it.geosolutions.geonetwork.util.GNPrivConfiguration)
	 */
	@Override
	public void setPrivileges(long metadataId,GNPrivConfiguration cfg) throws GNLibException, GNServerException, MissingServiceEndpointException{
		GNClient client=getClient();		
		client.setPrivileges(metadataId, cfg);
	}


	private void setPrivileges(long metadataId,ScopeConfiguration toUseConfig,LoginLevel toUseLevel) throws MissingConfigurationException, MissingServiceEndpointException, GNLibException, GNServerException{
		GNPrivConfiguration cfg=null;

		switch(toUseLevel){
		case ADMIN : break;
		case CKAN : break;
		case DEFAULT : {
			cfg=new GNPrivConfiguration();
			cfg.addPrivileges(toUseConfig.getDefaultGroup(), EnumSet.of(GNPriv.VIEW,GNPriv.FEATURED));
			break;
		}
		case PRIVATE :{
			cfg=new GNPrivConfiguration();
			cfg.addPrivileges(toUseConfig.getPrivateGroup(), EnumSet.of(GNPriv.VIEW,GNPriv.FEATURED));
			break; 
		}case SCOPE : {
			cfg=new GNPrivConfiguration();
			cfg.addPrivileges(toUseConfig.getPublicGroup(), EnumSet.of(GNPriv.VIEW,GNPriv.FEATURED));
			break;
		}
		}
		if(cfg!=null){
			log.debug("Setting privileges... ");
			setPrivileges(metadataId, cfg);
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, java.io.File)
	 */
	@Override
	public long insertMetadata(GNInsertConfiguration configuration,File metadataFile) throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException, InvalidInsertConfigurationException, AuthorizationException{
		checkPublishingConfiguration(configuration,config.getScopeConfiguration());
		GNClient client=getClient();		
		long toReturn=client.insertMetadata(configuration, metadataFile);
		setPrivileges(toReturn,config.getScopeConfiguration(),getCurrentLoginLevel());
		return toReturn;
	}
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(it.geosolutions.geonetwork.util.GNInsertConfiguration, org.opengis.metadata.Metadata)
	 */
	@Override
	public long insertMetadata(GNInsertConfiguration configuration,Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException, MissingServiceEndpointException, MissingConfigurationException, InvalidInsertConfigurationException, AuthorizationException{	
		return insertMetadata(configuration, meta2File(meta,registeredXMLAdapters));
	}

	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(java.io.File)
	 */
	@Override
	public long insertMetadata(File requestFile) throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException{
		GNClient client=getClient();		
		long toReturn= client.insertRequest(requestFile);
		setPrivileges(toReturn,config.getScopeConfiguration(),getCurrentLoginLevel());
		return toReturn;
	}
	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#insertMetadata(org.opengis.metadata.Metadata)
	 */
	@Override
	public long insertMetadata(Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException, MissingServiceEndpointException, MissingConfigurationException{
		return insertMetadata(meta2File(meta,registeredXMLAdapters));
	}


	//**************** INSERT AND PROMOTE

	@Override
	public long insertAndPromoteMetadata(File requestFile)
			throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException {
		LoginLevel currentLevel=getCurrentLoginLevel();

		// get parent configuration set access to GN
		ScopeConfiguration parentConfig=getParentConfiguration();
		ServerAccess parentAccess=getAccess(parentConfig, LoginLevel.DEFAULT);
		setAccess(parentAccess);
		GNClient client=getClient();		

		// actually publish as promoted access
		long toReturn= client.insertRequest(requestFile);
		setPrivileges(toReturn,parentConfig,LoginLevel.DEFAULT);

		// restore logged level
		try{
			login(currentLevel);
		}catch(AuthorizationException e){
			throw new GNLibException("Unabel to restore level. This should not happen.",e);
		}

		return toReturn;
	}


	@Override
	public long insertAndPromoteMetadata(GNInsertConfiguration configuration, File metadataFile)
			throws GNLibException, GNServerException, MissingServiceEndpointException, MissingConfigurationException,
			InvalidInsertConfigurationException, AuthorizationException {

		LoginLevel currentLevel=getCurrentLoginLevel();

		// get parent configuration set access to GN
		ScopeConfiguration parentConfig=getParentConfiguration();
		ServerAccess parentAccess=getAccess(parentConfig, LoginLevel.DEFAULT);
		setAccess(parentAccess);

		GNInsertConfiguration toUseConfig=null;

		try{
			toUseConfig=getUserConfiguration(configuration.getCategory(), configuration.getStyleSheet(),parentConfig,LoginLevel.DEFAULT);
		}catch(GeoNetworkException e){
			throw new GNLibException("Unabel to get publish configuration ",e);
		}


		checkPublishingConfiguration(toUseConfig,parentConfig);
		GNClient client=getClient();		


		// actually publish		
		long toReturn=client.insertMetadata(toUseConfig, metadataFile);
		setPrivileges(toReturn,parentConfig,LoginLevel.DEFAULT);

		// restore logged level
		try{
			login(currentLevel);
		}catch(AuthorizationException e){
			throw new GNLibException("Unabel to restore level. This should not happen.",e);
		}
		return toReturn;
	}

	@Override
	public long insertAndPromoteMetadata(GNInsertConfiguration configuration, Metadata meta)
			throws GNLibException, GNServerException, IOException, JAXBException, MissingServiceEndpointException,
			MissingConfigurationException, InvalidInsertConfigurationException, AuthorizationException {
		return insertAndPromoteMetadata(configuration, meta2File(meta,registeredXMLAdapters));
	}

	@Override
	public long insertAndPromoteMetadata(Metadata meta) throws GNLibException, GNServerException, IOException,
	JAXBException, MissingServiceEndpointException, MissingConfigurationException {
		return insertAndPromoteMetadata(meta2File(meta, registeredXMLAdapters));
	}


	private ScopeConfiguration getParentConfiguration() throws MissingConfigurationException, MissingServiceEndpointException{
		ArrayList<String> parent=ScopeUtils.getParentScopes();
		return ScopeConfigurationUtils.getByScope(config.getExistingConfigurations(),parent.get(parent.size()-1));
	}


	@Override
	public MetadataInfo getInfo(Long id) throws GNLibException, GNServerException, MissingServiceEndpointException {
		GNClient client=getClient();
		return client.getInfo(id);
	}

	@Override
	public MetadataInfo getInfo(String uuid) throws GNLibException, GNServerException, MissingServiceEndpointException {
		GNClient client=getClient();
		return client.getInfo(uuid);
	}


	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#updateMetadata(long, java.io.File)
	 */
	@Override
	public void updateMetadata(long id,File metadataFile) throws GNLibException, GNServerException, MissingServiceEndpointException{
		GNClient client=getClient();
		client.updateMetadata(id, metadataFile);
	}


	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#updateMetadata(long, org.opengis.metadata.Metadata)
	 */
	@Override
	public void updateMetadata(long id,Metadata meta) throws GNLibException, GNServerException, IOException, JAXBException, MissingServiceEndpointException{
		updateMetadata(id, meta2File(meta,registeredXMLAdapters));
	}




	/* (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkReader#deleteMetadata(long)
	 */
	@Override
	public void deleteMetadata(long id) throws GNLibException, GNServerException, MissingServiceEndpointException{
		GNClient client=getClient();
		client.deleteMetadata(id);
	}

	@Override
	public GNInsertConfiguration getCurrentUserConfiguration(String category,String styleSheet) throws AuthorizationException, GeoNetworkException {
		if(getCurrentLoginLevel()==null) throw new AuthorizationException("Client is not authenticated, please use Login before publishing");
		return getUserConfiguration(category, styleSheet, config.getScopeConfiguration(), getCurrentLoginLevel());
	}


	public GNInsertConfiguration getUserConfiguration(String category,String styleSheet, ScopeConfiguration configuration,LoginLevel lvl) throws AuthorizationException, GeoNetworkException {
		Integer configuredGroup=null;
		switch(getCurrentLoginLevel()){
		case CKAN : throw new AuthorizationException("Current logged level "+getCurrentLoginLevel()+"is read-only");
		case ADMIN :  throw new GeoNetworkException("Current logged level is Admin, unable to determine publihing configuration");
		case DEFAULT : configuredGroup=configuration.getDefaultGroup();
		break;
		case PRIVATE : configuredGroup=configuration.getPrivateGroup();
		break;
		case SCOPE : configuredGroup=configuration.getPublicGroup();
		break;		
		}


		boolean validate=false;
		try{
			validate=Boolean.parseBoolean(new RuntimeParameters().getProps().getProperty(RuntimeParameters.defaultValidation));
		}catch(Throwable t) {
			log.warn("Unable to read parameter "+RuntimeParameters.defaultValidation,t);
		}
		
		return new GNInsertConfiguration(configuredGroup+"", category, styleSheet, validate);		 
	}

	//******************************* ADMIN ********************************* //

	@Override
	public Group createGroup(String name, String description, String mail,Integer id)
			throws GNLibException, GNServerException, MissingServiceEndpointException {
		GNClientExtension client=getClient();
		client.createGroup(name, description, mail,id);
		long submitTime=System.currentTimeMillis();

		long timeout=500l;
		long wait=100l;
		try{
			timeout=Long.parseLong(new RuntimeParameters().getProps().getProperty(RuntimeParameters.geonetworkUpdateTimeout));
			wait=Long.parseLong(new RuntimeParameters().getProps().getProperty(RuntimeParameters.geonetworkUpdateWait));
		}catch(Exception e){
			throw new RuntimeException("Unable to read GN polling parameters from local configuration",e);
		}
		log.debug("Waiting for created group to be available, timeout is {} ",timeout);
		//wait for update to be available
		Group created=null;
		do{
			try{Thread.sleep(wait);}catch(InterruptedException e){}
			created=GroupUtils.getByName(getClient().getGroups(), name);			
		}while(created==null && (System.currentTimeMillis()-submitTime>=timeout));		
		if(created==null) {
			log.error("GN Update timeout {}ms reached. Group [ID : {}, name : {} ] not created.",timeout,id,name);
			throw new GNServerException("Reached timeout while creating group with id "+id);
		}
		return created;
	}

	@Override
	public Set<Group> getGroups() throws GNLibException, GNServerException, MissingServiceEndpointException {
		return getClient().getGroups();
	}

	@Override
	public String getAvailableOwnershipTransfer(Integer userId)
			throws GNServerException, MissingServiceEndpointException, GNLibException {
		return getClient().getPossibleOwnershipTransfer(userId);
	}

	@Override
	public String getMetadataOwners() throws GNServerException,
	MissingServiceEndpointException, GNLibException {		
		return getClient().getMetadataOwners();
	}

	private ScopeConfiguration acquireConfiguration() throws MissingServiceEndpointException, GNLibException, GNServerException, AuthorizationException, MissingConfigurationException{
		ScopeConfiguration acquired=null;
		try{
			acquired=config.acquireConfiguration();
		}catch(MissingConfigurationException e){
			acquired=createCurrentScopeConfiguration();
			config.createScopeConfiguration(acquired);
		}
		updateRightsOnAcquiredConfiguration(acquired);
		return acquired;
	}

	@Override
	public Set<User> getUsers() throws GNLibException, GNServerException, MissingServiceEndpointException {
		return getClient().getUsers();
	}

	@Override
	public User createUsers(String username,String password,Profile profile, Collection<Integer> groups) throws GNLibException, GNServerException, MissingServiceEndpointException {
		getClient().createUser(username, password, profile, groups);
		
		long submitTime=System.currentTimeMillis();

		long timeout=500l;
		long wait=100l;
		try{
			timeout=Long.parseLong(new RuntimeParameters().getProps().getProperty(RuntimeParameters.geonetworkUpdateTimeout));
			wait=Long.parseLong(new RuntimeParameters().getProps().getProperty(RuntimeParameters.geonetworkUpdateWait));
		}catch(Exception e){
			throw new RuntimeException("Unable to read GN polling parameters from local configuration",e);
		}
		log.debug("Waiting for created group to be available, timeout is {} ",timeout);
		//wait for update to be available
		User created=null;
		do{
			try{Thread.sleep(wait);}catch(InterruptedException e){}
			created=UserUtils.getByName(getClient().getUsers(), username);			
		}while(created==null && (System.currentTimeMillis()-submitTime>=timeout));		
		if(created==null) {
			log.error("GN Update timeout {}ms reached. User {} not created.",timeout,username);
			throw new GNServerException("Reached timeout while creating user "+username);
		}
		return created;
		
	}

	@Override
	public void assignOwnership(List<Long> toTransferIds,Integer targetUserId, Integer targetGroupId) throws AuthorizationException, GNServerException, MissingServiceEndpointException, GNLibException {
		if(getCurrentLoginLevel()==null||(!getCurrentLoginLevel().equals(LoginLevel.ADMIN))) throw new AuthorizationException("You need to login as Admin to massively transfer ownership");
		getClient().assignOwnership(toTransferIds, targetUserId, targetGroupId);
	}

	@Override
	public void transferOwnership(Integer sourceUserId, Integer sourceGroupId,
			Integer targetUserId, Integer targetGroupId)
					throws GNServerException, MissingServiceEndpointException, GNLibException {
		getClient().transferOwnership(sourceUserId, sourceGroupId, targetUserId, targetGroupId);		
	}

	private ScopeConfiguration createCurrentScopeConfiguration() throws GNLibException, GNServerException, MissingServiceEndpointException {
		try{
			String currentScopeName=ScopeUtils.getCurrentScopeName();
			log.debug("Generating configuration for scope "+currentScopeName);		
			this.login(LoginLevel.ADMIN);

			RuntimeParameters props=new RuntimeParameters();
			Integer nameLength=Integer.parseInt(props.getProps().getProperty(RuntimeParameters.GNUniqueNameLength));
			Integer passwordLength=Integer.parseInt(props.getProps().getProperty(RuntimeParameters.GNPasswordLength));

			// Get existing information
			//		Set<ScopeConfiguration> parentConfigurations=config.getParentScopesConfiguration();
			//		log.debug("Got parentScope configurations : "+parentConfigurations);

			//		Set<User> existingUsers=this.getUsers();


			//Generate privateGroup
			Group privateGroup=GroupUtils.generateRandomGroup(this.getGroups(), nameLength);
			log.debug("Creating private group {} ",privateGroup);
			this.createGroup(privateGroup.getName(), "Private group for scope "+currentScopeName, "none", privateGroup.getId());

			//Generate public group

			Group publicGroup=GroupUtils.generateRandomGroup(this.getGroups(), nameLength);
			log.debug("Creating public group {} ",publicGroup);
			this.createGroup(publicGroup.getName(), "Public group for scope "+currentScopeName, "none", publicGroup.getId());

			//Reload info
			Set<Group> existingGroups=this.getGroups();
			privateGroup=GroupUtils.getByName(existingGroups, privateGroup.getName());
			log.debug("Resulting private group : "+privateGroup);		
			publicGroup=GroupUtils.getByName(existingGroups, publicGroup.getName());
			log.debug("Resulting publicGroup : "+publicGroup);

			// Create ckan user
			User ckanUser=UserUtils.generateRandomUser(this.getUsers(), nameLength, passwordLength);
			log.debug("Creating ckan user..");
			Set<Integer> scopeAccessibleGroups=new HashSet<>();
			scopeAccessibleGroups.add(publicGroup.getId());
			scopeAccessibleGroups.add(privateGroup.getId());		
			this.createUsers(ckanUser.getUsername(), ckanUser.getPassword(), Profile.Reviewer, scopeAccessibleGroups);

			// Create scope user
			User scopeUser=UserUtils.generateRandomUser(this.getUsers(), nameLength, passwordLength);
			log.debug("Creating scope user..");		
			//		//Scope has read rights on parent groups
			//		for(ScopeConfiguration conf:parentConfigurations){
			//			scopeAccessibleGroups.add(conf.getPrivateGroup());
			//			scopeAccessibleGroups.add(conf.getPublicGroup());
			//		}
			this.createUsers(scopeUser.getUsername(), scopeUser.getPassword(), Profile.Reviewer, scopeAccessibleGroups);

			logout();

			HashMap<Account.Type,Account> accounts=new HashMap<>();
			accounts.put(Account.Type.CKAN, new Account(ckanUser.getUsername(), ckanUser.getPassword(), Account.Type.CKAN));
			accounts.put(Account.Type.SCOPE, new Account(scopeUser.getUsername(), scopeUser.getPassword(), Account.Type.SCOPE));
			return new ScopeConfiguration(currentScopeName, publicGroup.getId(),privateGroup.getId(), accounts, publicGroup.getId());


		}catch(Exception e){
			throw new GNLibException("Unable to create scope configuration", e);
		}
	}

	/**
	 * Update scopeConfig.scopeUser to access parent scopes groups and parent Users to access scopeConfig.publicGroup
	 * 
	 * @param scopeConfig
	 * @throws MissingServiceEndpointException
	 * @throws AuthorizationException
	 * @throws GNServerException 
	 * @throws GNLibException 
	 * @throws MissingConfigurationException 
	 */


	private void updateRightsOnAcquiredConfiguration(ScopeConfiguration scopeConfig) throws MissingServiceEndpointException, AuthorizationException, GNLibException, GNServerException, MissingConfigurationException{
		Set<ScopeConfiguration> parentConfigurations=config.getParentScopesConfiguration();
		this.login(LoginLevel.ADMIN);
		GNClientExtension client=getClient();
		Set<Integer> parentVisibleGroups=new HashSet<>();
		for(ScopeConfiguration conf:parentConfigurations){
			parentVisibleGroups.add(conf.getPrivateGroup());
			parentVisibleGroups.add(conf.getPublicGroup());
		}
		Set<User> existingUsers=this.getUsers();
		User scopeUser=UserUtils.getByName(existingUsers, scopeConfig.getAccounts().get(Account.Type.SCOPE).getUser());
		log.debug("Updating scope user..");		
		client.editUser(scopeUser, parentVisibleGroups);
		for(ScopeConfiguration conf:parentConfigurations){
			log.debug("Updating parent user for scope "+conf.getAssignedScope());
			User toUpdateUser=UserUtils.getByName(existingUsers, conf.getAccounts().get(Account.Type.SCOPE).getUser());			 
			client.editUser(toUpdateUser, Collections.singleton(scopeConfig.getPublicGroup()));
		}		
		logout();
	}


	/*
	 * (non-Javadoc)
	 * @see org.gcube.spatial.data.geonetwork.GeoNetworkPublisher#registerXMLAdapter(org.gcube.spatial.data.geonetwork.configuration.XMLAdapter)
	 */

	@Override
	public void registerXMLAdapter(XMLAdapter adapter) {
		registeredXMLAdapters.add(adapter);
	}




	//************* PRIVATE

	private GNClientExtension theClient=null;

	private ServerAccess currentAccess=null;

	private void setAccess(ServerAccess access){
		this.currentAccess=access;
		theClient=null;
	}

	private ServerAccess getCurrentAccess() throws MissingServiceEndpointException{
		if(currentAccess==null) currentAccess=new ServerAccess(config.getGeoNetworkEndpoint(),config.getGeoNetworkVersion());
		return currentAccess;
	}

	private LoginLevel getCurrentLoginLevel() throws MissingServiceEndpointException{
		return getCurrentAccess().getLoggedLevel();
	}

	private synchronized GNClientExtension getClient() throws MissingServiceEndpointException{
		if(theClient==null)			
			theClient = new GNClientExtension(getCurrentAccess());		
		return theClient;
	}


	private void checkPublishingConfiguration(GNInsertConfiguration configuration,ScopeConfiguration toUseConfig) throws AuthorizationException, InvalidInsertConfigurationException, MissingConfigurationException, MissingServiceEndpointException{
		if(getCurrentLoginLevel()==null) throw new AuthorizationException("Client is not authenticated, please use Login before publishing");
		boolean mandatoryValidation=true; 
		try{
			mandatoryValidation=Boolean.parseBoolean(new RuntimeParameters().getProps().getProperty(RuntimeParameters.mandatoryValidation, "true"));
		}catch(Exception e){
			log.warn("Invalid local configuration, unable to parse mandatory validation option.",e);
		}
		log.debug("Mandatory validation : {} ",mandatoryValidation);
		
		if(!configuration.getValidate()&&mandatoryValidation) throw new InvalidInsertConfigurationException("Validate option is mandatory");

		// CHECK GROUP
		Integer targetGroup=Integer.parseInt(configuration.getGroup());
		Integer configuredGroup=null;


		switch(getCurrentLoginLevel()){
		case CKAN : throw new AuthorizationException("Current logged level "+getCurrentLoginLevel()+"is read-only");
		case ADMIN : break; 
		case DEFAULT : configuredGroup=toUseConfig.getDefaultGroup();
		break;
		case PRIVATE : configuredGroup=toUseConfig.getPrivateGroup();
		break;
		case SCOPE : configuredGroup=toUseConfig.getPublicGroup();
		break;		
		}
		if (configuredGroup!=null&&(!targetGroup.equals(configuredGroup))) 
			throw new InvalidInsertConfigurationException(String.format("Invalid logged level %s and specified group %d, expected %d", getCurrentLoginLevel(),targetGroup,configuredGroup));
	}


	private List<XMLAdapter> registeredXMLAdapters=new ArrayList<XMLAdapter>();


	private static File meta2File(Metadata meta,List<XMLAdapter> adapters) throws IOException, JAXBException{
		File temp=File.createTempFile("meta", ".xml");
		FileWriter writer=new FileWriter(temp);
		String marshalled=XML.marshal(meta);
		for(XMLAdapter adapter:adapters)
			marshalled=adapter.adaptXML(marshalled);
		writer.write(marshalled);
		writer.close();
		return temp;
	}



}
