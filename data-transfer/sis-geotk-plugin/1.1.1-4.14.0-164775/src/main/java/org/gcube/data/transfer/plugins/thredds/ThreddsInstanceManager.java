package org.gcube.data.transfer.plugins.thredds;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.data.transfer.plugin.model.DataTransferContext;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.xml.sax.SAXException;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ThreddsInstanceManager {

	protected static ThreddsInstanceManager instance=null;

	protected static final ConcurrentSkipListSet<String> checkedTokens=new ConcurrentSkipListSet<>();


	@Synchronized
	public static ThreddsInstanceManager get(DataTransferContext ctx) {
		if(instance==null)
			instance= new ThreddsInstanceManager(ctx);
		return instance;
	}

	// ***************** INSTANCE LOGIC


	protected ThreddsInfo cachedInfo=null;
	protected ExecutorService executor=Executors.newSingleThreadExecutor();
	//loaded at construction time
	protected DataTransferContext ctx=null;
	protected ApplicationConfiguration threddsConfig=null;
	protected String threddsAdminUser;
	protected String threddsAdminPassword;
	protected String threddsPersistenceLocation;
	protected String threddsVersionString;
	protected String currentHostname;
	protected String currentGHNId;

	protected ThreddsInstanceManager(DataTransferContext context) {
		log.warn("Instance Creation. Should happen only once. Loading information from context..");
		this.ctx=context;
		currentHostname=ctx.getCtx().container().configuration().hostname();
		currentGHNId=ctx.getCtx().container().id();
		String tomcatSecurityPath=System.getenv("WEB_CONTAINER_HOME")+"/conf/tomcat-users.xml";

		log.info("Loading security from {} ",tomcatSecurityPath);
		try{
			TomcatSecurityHandler tomcatHandler=new TomcatSecurityHandler(tomcatSecurityPath);
			threddsAdminUser=tomcatHandler.getThreddsAdminUser();
			threddsAdminPassword=tomcatHandler.getThreddsAdminPassword();
		}catch(Exception e) {
			throw new RuntimeException("Unable to parse security file "+tomcatSecurityPath,e);
		}
		log.info("Looking for Thredds Application Configuration.. ");


		//Use Future 
		Future<ApplicationConfiguration> future=executor.submit(new ApplicationConfigurationRetriever(ctx));
		try {
			threddsConfig=future.get();
			if(threddsConfig==null) throw new Exception("Returned Application Configuration is null");
			threddsPersistenceLocation=threddsConfig.persistence().location();
			threddsVersionString=threddsConfig.version();
		}catch(Exception e) {			
			throw new RuntimeException("Unable to find Application Configuration for thredds.",e);
		}

	}

	/**
	 * 
	 * 
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws Exception
	 */
	public synchronized ThreddsInfo getInfo() throws SAXException, IOException {
		if(cachedInfo==null) {
			log.info("Loading ThreddsInfo..");

			String threddsContentRoot=getContentRoot();


			log.debug("Found content root at {} ",threddsContentRoot);

			//Host
			ThreddsInfo info=new ThreddsInfo();
			info.setHostname(currentHostname);
			info.setGhnId(currentGHNId);
			info.setLocalBasePath(threddsPersistenceLocation);
			info.setInstanceBaseUrl("http://"+info.getHostname()+"/thredds");


			String mainCatalogPath=threddsContentRoot+"/catalog.xml";

			log.info("Loading catalog information from {} ",mainCatalogPath);

			XMLCatalogHandler handler=new XMLCatalogHandler(new File(mainCatalogPath));
			info.setCatalog(handler.getCatalogDescriptor());


			//tomcat security
			info.setAdminPassword(threddsAdminPassword);
			info.setAdminUser(threddsAdminUser);

			//version
			String[] splittedVersion=threddsVersionString.split("\\.");
			info.setVersion(Integer.parseInt(splittedVersion[0]));
			info.setMinor(Integer.parseInt(splittedVersion[1]));
			info.setRevision(Integer.parseInt(splittedVersion[2]));						
			log.info("Loaded ThreddsInfo is {} ",info);	
			cachedInfo=info;
		}
		return cachedInfo;
	}

	public synchronized void clearCache() {
		log.debug("Clearing cache..");
		cachedInfo=null;
	}

	public String getCurrentHostname() {
		return currentHostname;
	}

	public String getMainCatalogFile(){
		return getContentRoot()+"/catalog.xml";
	}

	public String getContentRoot() {
		return threddsPersistenceLocation;
	}


	public XMLCatalogHandler mainCatalogHandler() throws SAXException, IOException, Exception {
		return new XMLCatalogHandler(new File(getMainCatalogFile()));
	}

	@Synchronized
	public void updatePublishedInfo() throws Exception {
		String token=SecurityTokenProvider.instance.get();
		log.info("Checking IS with token {} ",token);
		if(!checkedTokens.contains(token)) {
			checkedTokens.add(token);
			getInfo();
			String currentHostname=cachedInfo.getHostname();

			log.info("Checking IS Information, host is {}",currentHostname);

			List<ServiceEndpoint> currentEndpoints=queryForServiceEndpoints(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_CATEGORY),
					LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_PLATFORM));

			ServiceEndpoint toCheck=null;

			//Checking by host
			log.debug("Found {} Service Endpoints, checking by hostname {} ",currentEndpoints.size());
			for(ServiceEndpoint se:currentEndpoints) {
				String host=se.profile().runtime().hostedOn();
				try{
					if(isSameHost(host, currentHostname)) {toCheck=se;
					break;}
				}catch(Throwable t) {
					log.warn("Unable to check Host {} ",host,t);
				}			
			}

			if(toCheck==null) {
				log.info("ServiceEndpoint not found, going to create one..");
				// CREATE NEW
				ServiceEndpoint newSE=getNewServiceEndpoint();
				updateAndWait(newSE, true);

			}else {
				// Check found
				boolean updateSE=true;			
				String adminAPName=LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_REMOTE_MANAGEMENT_ACCESS);
				log.debug("Looking for Access Point {} ",adminAPName);
				Group<AccessPoint> existentAP=toCheck.profile().accessPoints();


				boolean addAccessPoint=true;
				for(AccessPoint ap:existentAP)
					if(ap.name().equals(adminAPName)) {
						addAccessPoint=false;					
						// FOUND AP
						String pwd=decryptString(ap.password());
						if(ap.username().equalsIgnoreCase(threddsAdminUser)&&pwd.equalsIgnoreCase(threddsAdminPassword)) {
							log.info("ServiceEndopint is up to date.");
							updateSE=false;
						}else {
							// AP is not up to date
							ap.credentials(threddsAdminPassword, threddsAdminUser);
						}
					}

				if(updateSE) {
					log.debug("Need to update SE... ");
					if(addAccessPoint) {
						log.debug("Access point {} not found. Adding it.. ",adminAPName);
						existentAP.add(getNewAccessPoint());
					}
					ServiceEndpoint updated=updateAndWait(toCheck,false);
					log.info("Updated {} ",updated);				
				}

			}
		}else log.info("Skipping token {}, already checked.",token);

	}

	private static String registerServiceEndpoint(ServiceEndpoint toRegister) {
		RegistryPublisher rp=RegistryPublisherFactory.create();
		Resource r=rp.create(toRegister);
		return r.id();
	}

	public static ServiceEndpoint update(ServiceEndpoint toUpdate) {
		RegistryPublisher rp=RegistryPublisherFactory.create();
		return rp.update(toUpdate);		
	}

	private ServiceEndpoint getNewServiceEndpoint() {		
		ServiceEndpoint toReturn=new ServiceEndpoint();

		Profile profile=toReturn.newProfile();
		profile.category(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_CATEGORY));
		profile.name("Thredds on "+cachedInfo.getHostname());
		profile.description("Thredds on "+cachedInfo.getHostname());

		// TODO Gather info on version
		Platform platform=profile.newPlatform();
		platform.version((short)cachedInfo.getVersion());
		platform.minorVersion((short)cachedInfo.getMinor());
		platform.revisionVersion((short)cachedInfo.getRevision());
		platform.buildVersion((short)cachedInfo.getBuild());
		platform.name(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_PLATFORM));

		org.gcube.common.resources.gcore.ServiceEndpoint.Runtime runtime=profile.newRuntime();
		runtime.ghnId(cachedInfo.getGhnId());
		runtime.hostedOn(cachedInfo.getHostname());		
		runtime.status("READY");

		profile.accessPoints().add(getNewAccessPoint());
		return toReturn;

	}

	private static ServiceEndpoint updateAndWait(ServiceEndpoint toUpdate,boolean isNew) {		
		boolean equals=true;
		boolean timeoutReached=false;
		long timeout=LocalConfiguration.getTTL(LocalConfiguration.IS_REGISTRATION_TIMEOUT);
		log.info("Going to register {}. Timeout is {} ",toUpdate.id(),timeout);
		String toUpdateString=marshal(toUpdate);
		log.debug("Serialized resource is {} ",toUpdateString);
		if(isNew) registerServiceEndpoint(toUpdate);
		else update(toUpdate);
		long updateTime=System.currentTimeMillis();
		String updatedString=null;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			List<String> byIdResults=queryById(toUpdate.id());
			if(byIdResults.isEmpty()) {
				equals=false;
			}else {
				updatedString=byIdResults.get(0);
				equals=toUpdateString.equals(updatedString);				
			}
			timeoutReached=(System.currentTimeMillis()-updateTime)>timeout;
		}while(equals&&(!timeoutReached));
		if(timeoutReached) log.warn("Timeout reached. Check if {} is updated ",toUpdate.id());
		return querySEById(toUpdate.id());
	}


	public static List<String> queryById(String id) {
		DiscoveryClient<String> client = client();
		String queryString ="declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
				"for $profiles in collection('/db/Profiles')//Document/Data/ic:Profile/Resource "+
				"where $profiles/ID/text() eq '"+id+"'"+				
				" return $profiles";
		return client.submit(new QueryBox(queryString));		
	}


	public static ServiceEndpoint querySEById(String id) {
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/ID/text() eq '"+id+"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query).get(0);
	}


	public static String marshal(Resource res) {
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		Resources.marshal(res, stream);
		return stream.toString();
	}

	private AccessPoint getNewAccessPoint() {
		AccessPoint toReturn=new AccessPoint();
		toReturn.credentials(encrypt(threddsAdminPassword), threddsAdminUser);
		toReturn.description("Thredds Remote Management credentials");
		toReturn.name(LocalConfiguration.getProperty(LocalConfiguration.THREDDS_SE_REMOTE_MANAGEMENT_ACCESS));
		toReturn.address("https://"+getCurrentHostname()+"/thredds/admin/debug?catalogs/reinit");
		return toReturn;
	}


	static String decryptString(String toDecrypt){
		try{
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}catch(Exception e) {
			throw new RuntimeException("Unable to decrypt.",e);
		}
	}


	static String encrypt(String toEncrypt) {
		try{
			return StringEncrypter.getEncrypter().encrypt(toEncrypt);
		}catch(Exception e) {
			throw new RuntimeException("Unable to Encrypt.",e);
		}
	}

	static List<ServiceEndpoint> queryForServiceEndpoints(String category, String platformName){
		log.debug("Querying for Service Endpoints [category : {} , platformName : {}]",category,platformName);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'");

		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}

	static boolean isSameHost(String toTestHost,String toLookForHost) throws UnknownHostException {
		log.debug("Checking same hosts {},{}",toTestHost,toLookForHost);
		if(toTestHost.equalsIgnoreCase(toLookForHost)) return true;
		else {
			InetAddress[] toTestHostIPs=InetAddress.getAllByName(toTestHost);
			InetAddress[] toLookForHostIPs=InetAddress.getAllByName(toLookForHost);
			log.debug("Checking IPs. ToTestIPs {}, ToLookForIPs {} ",toTestHostIPs,toLookForHostIPs);
			for(InetAddress toTestIP:toTestHostIPs) {
				for(InetAddress toLookForIP:toLookForHostIPs)
					if(toTestIP.equals(toLookForIP)) return true;
			}
		}
		log.debug("HOSTS are different.");
		return false;
	}
}
