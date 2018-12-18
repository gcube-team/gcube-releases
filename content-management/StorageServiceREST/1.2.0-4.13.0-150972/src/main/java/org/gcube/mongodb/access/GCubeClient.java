package org.gcube.mongodb.access;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.content.storage.rest.bean.Credentials;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ServerAddress;

/**
 * Client for accessing gcube resources
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class GCubeClient {

	private static final Logger logger = LoggerFactory.getLogger(GCubeClient.class);
	private String username;
	private String password;
	private String[] server;
	private String sc;
	private String sn;
	

	/**
	 * Example of resource:
	 * <Resource version="0.4.x">
	 *     <ID>9b3bfb9c-e4b0-418e-b1f7-657188f48008</ID>
	 *		   <Type>RuntimeResource</Type>
	 *		   <Scopes>
	 *		      <Scope>/gcube/devNext</Scope>
	 *		      <Scope>/gcube</Scope>
	 *		   </Scopes>
	 *		   <Profile>
	 *		      <Category>DataStorage</Category>
	 *		      <Name>SmartStorage</Name>
	 *		      <Description />
	 *		      <Platform>
	 *		         <Name>MongoDB</Name>
	 *		         <Version>3</Version>
	 *		         <MinorVersion>0</MinorVersion>
	 *		         <RevisionVersion>7</RevisionVersion>
	 *		         <BuildVersion>0</BuildVersion>
	 *		      </Platform>
	 *		      <RunTime>
	 *		         <HostedOn>SmartArea</HostedOn>
	 *		         <GHN UniqueID="" />
	 *		         <Status>READY</Status>
	 *		      </RunTime>
	 *		      <AccessPoint>
	 *		         <Description>The Query server</Description>
	 *		         <Interface>
	 *		            <Endpoint EntryName="QueryServer1">n028.smart-applications.area.pi.cnr.it</Endpoint>
	 *		         </Interface>
 	 *		         <AccessData>
	 *		            <Username>test</Username>
	 *		            <Password>hLpav+z35yTus5Fb5dt7Ag==</Password>
	 *		         </AccessData>
	 *		         <Properties>
	 *		            <Property>
	 *		               <Name>db</Name>
	 *		               <Value encrypted="false">test_db</Value>
	 *		            </Property>
	 *		         </Properties>
	 *		      </AccessPoint>
	 *		   </Profile>
	 *		</Resource>
	 * 
	 * 
	 * 
	 * @param serviceClass
	 * @param serviceName
	 */
	public GCubeClient(String serviceClass, String serviceName){
		sc=serviceClass;
		sn=serviceName;
	}
		
	public List<ServiceEndpoint> getStorageEndpoint() {
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+sc+"' and $resource/Profile/Name eq '"+sn+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public List<ServiceEndpoint> getServiceEndpoint(String sc, String sn) {
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+sc+"' and $resource/Profile/Name eq '"+sn+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		return resources;
	}
	
	public String[] getServersAddresses(ServiceEndpoint res) {
		server=new String[res.profile().accessPoints().size()];
		int i=0;
		for (AccessPoint ap:res.profile().accessPoints()) {
//			if (ap.name().equals("server"+(i+1))) {
				server[i] = ap.address();
	// if presents, try to get user and password			
				username = ap.username();	
				logger.info("username found ");
				if(username != null && username.length() > 0){
					try {
						password = StringEncrypter.getEncrypter().decrypt(ap.password());
						if(password != null)
							logger.info("password found");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i++;
		}
		return server;
	}
	
	/**
	 * check if a accessPoint has the dbName set as EntryName
	 * @param res
	 * @param dbName
	 * @return
	 */
	public String getServersAddressesForSpecificAccessPoint(ServiceEndpoint res, String dbName) {
		String server=null;
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals(dbName)) {
				server = ap.address();
	// if presents, try to get user and password			
				username = ap.username();	
				logger.info("username found ");
				if(username != null && username.length() > 0){
					try {
						password = StringEncrypter.getEncrypter().decrypt(ap.password());
						if(password != null)
							logger.info("password found");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				
		}
		return server;
	}
	
	
	public AccessPoint getAccessPoint(ServiceEndpoint res, String dbName) {
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals(dbName)) {
				return ap;
			}
		}
		throw new RuntimeException("AccessPoint Not Found");

	}
	
	public String getUser(AccessPoint ap){
		return ap.username();
	}
	
	public List<ServerAddress> getMongoServers(AccessPoint ap){
		List<ServerAddress> serverList=new ArrayList<ServerAddress>();
		String serverListString= ap.address();
		String [] serverListArray= serverListString.split(";");
		for(int i=0;i<serverListArray.length;i++){
			serverList.add(new ServerAddress(serverListArray[i]));
		}
		return serverList;
	}
	
	public String getPassword(AccessPoint ap) throws Exception{
		return StringEncrypter.getEncrypter().decrypt(ap.password()).toString();
	}
	
	public String getServerAccess(AccessPoint ap){
		return ap.address();
	}
	
	public String getProperty(AccessPoint ap, String name){
		Map<String, Property>map= ap.propertyMap();
		Property type=map.get(name);
		if (type!=null)
			return type.value();
		else
			return null;
	}

	
	public String getResolverHost(ServiceEndpoint serviceEndpoint) {
		return serviceEndpoint.profile().runtime().hostedOn();
		
	}

	public String retrievePropertyValue(ServiceEndpoint res, String name, String scope) {
		Iterator<AccessPoint> it= res.profile().accessPoints().iterator();
		String value=null;
		while(it.hasNext()){
			AccessPoint ap=(AccessPoint)it.next();
			Map<String, Property>map= ap.propertyMap();
			Property type=map.get(name);
			if(type!=null){
				value=type.value();
				if(value!= null) break;
			}

		}
		return value;
	}

	public Credentials getCredentials(String dbName, String collectionFieldName) {
		List<ServiceEndpoint> resources=getStorageEndpoint();
		if((resources!= null) && (resources.size()>0)){
//			String dbName=resources.get(0).profile().
	    	org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint ap = getAccessPoint(resources.get(0), dbName);
	    	if(ap==null)
	    		throw new RuntimeException("DB: "+dbName+" not found on resource "+sc+" "+sn);
	    	List<ServerAddress> server=getMongoServers(ap);
	    	String collection= getProperty(ap, collectionFieldName);
	    	String user=getUser(ap);
	    	String pwd=null;
	    	try {
				pwd=getPassword(ap);
			} catch (Exception e) {
				throw new RuntimeException("decrypting password failed");
			}
	    	Credentials credentials=new Credentials(server, dbName, collection, user,pwd);
			return credentials;
		}else throw new RuntimeException("resource not found on scope: "+ScopeProvider.instance.get());
	}

	/**
	 * Retrieve the db associated to the token. In the first version the token is retrieved by properties file
	 * @param dbIdentifier: the db Field Name serched on properties file
	 * @return the name of the db associated or null
	 */
	public String retrieveDBEnabled(String dbIdentifier) {
		logger.debug("retrieve db info from id: "+dbIdentifier);
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/dbMapping.properties");
		Properties prop=new Properties();
        try{
        	prop.load(inputStream);
        }catch(IOException e){
        	e.printStackTrace();
        }
        String value=(String)prop.get(dbIdentifier);
        if (value != null){
        	return value;
        }else{
        	throw new RuntimeException("There is not mapping between token and backend. This call has been rejected");
        }
	}


}
