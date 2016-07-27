package org.gcube.datatransformation.adaptors.common;
//package org.gcube.application.framework.harvesting.common;
//
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.rmi.RemoteException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//
//import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.session.SessionManager;
//import org.gcube.application.framework.core.util.GenericResource;
//import org.gcube.application.framework.core.util.RuntimeResource;
//import org.gcube.application.framework.harvesting.common.db.exceptions.SourceIDNotFoundException;
//import org.gcube.application.framework.harvesting.common.db.is.ISResources;
//import org.gcube.application.framework.harvesting.common.db.tools.DBConstants;
//import org.gcube.application.framework.harvesting.common.db.tools.SourcePropsTools;
//import org.gcube.application.framework.harvesting.common.db.xmlobjects.DBProps;
//import org.gcube.application.framework.harvesting.common.db.xmlobjects.DBSource;
//import org.gcube.common.database.DatabaseProvider;
//import org.gcube.common.database.endpoint.DatabaseEndpoint;
//import org.gcube.common.database.endpoint.DatabaseProperty;
//import org.gcube.common.database.engine.DatabaseInstance;
//import org.gcube.common.database.is.ISDatabaseProvider;
//import org.gcube.common.resources.gcore.ServiceEndpoint;
//import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
//import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
////import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
////import org.gcube.portlets.admin.harvestersettingsmanager.shared.DBSourceP;
////import org.gcube.portlets.admin.harvestersettingsmanager.client.rpc.InformationExchanger;
////import org.gcube.portlets.admin.harvestersettingsmanager.server.datasource.ElementGenerator;
//import org.gcube.resources.discovery.client.api.DiscoveryClient;
//import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * @author Nikolas Laskaris laskarisn@di.uoa.gr
// *
// */
//public class ResourceExchanger {
//
//	private static final long serialVersionUID = -8289746397108763902L;
//	
//	/** The logger. */
//	private static final Logger logger = LoggerFactory.getLogger(ResourceExchanger.class);
//	
//	/**
//	 * {@inheritDoc}
//	 */
////	@Override
////	public void init() throws ServletException {
////		super.init();
////		
////	}
//
//	
//	
//	/**
//	 * updates the settings within an existing resource on IS
//	 * 
//	 * @param scope the scope to search within
//	 * @param name the name of the generic resource
//	 * @param typeName the type of the generic resource
//	 * @param body the xml within the body of the generic resource  
//	 * @return the ID of the newly created generic resource, null otherwise
//	 * @throws Exception 
//	 * @throws  
//	 */
//	public boolean updateGenericOnIS(String genResName, String dbName, String typeName,  String body, ASLSession session) throws Exception{
//		
//		GenericResource genRes = new GenericResource(session);
//		List<ISGenericResource> resources = null;
//		try {
//			resources = genRes.getGenericResourcesByType(typeName);
//			for(ISGenericResource resource : resources){
//				if(resource.getName().equals(genResName) && SourcePropsTools.parseSourceProps(resource.getBody()).getSourceName().equals(dbName)){
//					resource.setBody(body);
//					genRes.updateGenericResourceByID(resource);
//				}
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//
//	}
//	
//	
//	public boolean removeGenericFromIS(String genResName, String dbName, String typeName, ASLSession session ) throws Exception{
//		
//		GenericResource genRes = new GenericResource(session);
//		List<ISGenericResource> resources = null;
//		try {
//			resources = genRes.getGenericResourcesByType(typeName);
//			for(ISGenericResource resource : resources)
//				if(resource.getName().equals(genResName) && SourcePropsTools.parseSourceProps(resource.getBody()).getSourceName().equals(dbName))
//					genRes.removeGenericResource(resource);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//		
//	}
//	
//	
//	/**
//	 * stores the settings within a new resource on IS
//	 * 
//	 * @param scope the scope to search within
//	 * @param name the name of the generic resource
//	 * @param typeName the type of the generic resource
//	 * @param body the xml within the body of the generic resource  
//	 * @return the ID of the newly created generic resource, null otherwise
//	 */
//	public String createGenericOnIS(String name, String typeName, String body, ASLSession session){
//		ISGenericResource res = new ISGenericResource("",name,"Holds within body information used by ASL Harvesters",body,typeName);
//		GenericResource genRes = new GenericResource(session);
//		try {
//			return genRes.createGenericResource(res);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * 
//	 * @param scope
//	 * @param name
//	 * @return  the body xml of the given Generic Resource, null otherwise
//	 */
//	public String getBodyByName(String name, ASLSession session){
//		GenericResource genRes = new GenericResource(session);
//		List<ISGenericResource> allRes;
//		try {
//			allRes = genRes.getGenericResourceByName(name);
//			if(allRes.size()==0)
//				return null;
//		} catch (RemoteException e) {
//			return null;
//		}
//		return allRes.get(0).getBody();//prettyFormat(allRes.get(0).getBody());
//	}
//	
//	/**
//	 * Expects to have only one Generic Resource by that type on IS. If more than one, it returns only the first one.
//	 * 
//	 * @param type
//	 * @return the body xml of the given Generic Resource, null otherwise
//	 * @throws TransformerException 
//	 */
//	public String getBodyByType(String type, ASLSession session) throws TransformerException{
//		GenericResource genRes = new GenericResource(session);
//		List<org.gcube.common.resources.gcore.GenericResource> allRes;
//		try {
//			allRes = genRes.getAllGenericResources();
//		} catch (RemoteException e) {
//			return null;
//		}
//		for(org.gcube.common.resources.gcore.GenericResource res : allRes){
//			if(res.profile().type().equalsIgnoreCase(type)){
//				System.out.println("Name: "+res.profile().name()+"\tType: "+res.profile().type());
//				return ElementGenerator.domToXML(res.profile().body());//prettyFormat(ElementGenerator.domToXML(res.profile().body()));
//			}
//		}
//		return null; //in case nothing is found
//	}
//	
//	public String[] getPropsNames(String dbName, ASLSession session) throws Exception{
//		ArrayList<String> output = new ArrayList<String>();
//		GenericResource genRes = new GenericResource(session);
//		List<ISGenericResource> allRes = genRes.getGenericResourcesByType(DBConstants.GENERIC_CATEGORY_NAME);
//		for(ISGenericResource resource : allRes){
//			DBProps props = SourcePropsTools.parseSourceProps(resource.getBody());
//			if(props.getSourceName().equalsIgnoreCase(dbName))
//				output.add(props.getPropsName());
//		}
//		return output.toArray(new String[0]);
//	}
//	
//	public String getDBPropsByName(String dbName, String propsName, ASLSession session) throws Exception{
//		GenericResource genRes = new GenericResource(session);
//		List<ISGenericResource> allRes;
//		try {
//			allRes = genRes.getGenericResourcesByType(DBConstants.GENERIC_CATEGORY_NAME);
//		} catch (RemoteException e) {
//			return null;
//		}
//		String result = new String();
//		for(ISGenericResource res : allRes){
//			DBProps props = SourcePropsTools.parseSourceProps(res.getBody());
//			if(res.getName().equals(propsName) && props.getSourceName().equals(dbName)){
//				System.out.println("propsName: "+res.getName()+"\tdbName: "+props.getSourceName());
//				result = res.getBody();
//			}
//		}
//		return result;
//	}
//	
//	
////	/**
////	 * Get the ASL session
////	 * 
////	 * @return the ASL session
////	 */
////	private ASLSession getASLsession(HttpSession httpSession) {
//////		HttpSession httpSession = this.getThreadLocalRequest().getSession();
////		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
////		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
////		return session;
////	}
//	
//
////	public String prettyFormat(String input) {
////	    try {
////	        Source xmlInput = new StreamSource(new StringReader(input));
////	        StringWriter stringWriter = new StringWriter();
////	        StreamResult xmlOutput = new StreamResult(stringWriter);
////	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
////	        Transformer transformer = transformerFactory.newTransformer(); 
////	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
////	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
////	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
////	        transformer.transform(xmlInput, xmlOutput);
////	        return xmlOutput.getWriter().toString();
////	    } catch (Exception e) {
////	        throw new RuntimeException(e); 
////	    }
////	}
//
//	public String[] getDBTypes(){
//		return DBConstants.getDBTypes();
//	}
//
//	
//	
//	public DBSource[] getDBSourcesInfo(ASLSession session) throws MalformedURLException{		
//		System.out.println("GETTING DBSOURCES INFO");
//		RuntimeResource runResource = new RuntimeResource(session);
//		ArrayList <DBSource> lst = new ArrayList<DBSource>();
//		DatabaseProvider isDBProvider = new ISDatabaseProvider();
//		ScopeProvider.instance.set(session.getScope());
//		for(ServiceEndpoint se : runResource.getRuntimeResourceByCategory(DBConstants.RUNNING_CATEGORY_NAME)){
//			System.out.println("Getting props of : "+se.profile().name());
////			DatabaseInstance db = isDBProvider.get(se.profile().name());
//			/*
//			DatabaseEndpoint endpoint = isDBProvider.get(se.profile().name(),CATEGORY_NAME);
//			System.out.println("endpoint.getId(): "+endpoint.getId());
//			System.out.println("username: "+endpoint.getCredentials().getUsername());
//			System.out.println("password: "+endpoint.getCredentials().getPassword());
//			System.out.println("endpoint.getConnectionString(): "+endpoint.getConnectionString());
//			System.out.println("endpoint.getDescription(): "+endpoint.getDescription());
//			for(DatabaseProperty prop : endpoint.getProperties())
//				System.out.println(prop.getKey()+": "+prop.getValue());
//			*/
//			DBSource dbSource = new DBSource();
//			dbSource.setSourceName(se.profile().name());
//			dbSource.setDBType(se.profile().platform().name());
//			dbSource.setHostName(se.profile().runtime().hostedOn());
////			dbSource.setUserName(se.profile().accessPoints().toArray(new AccessPoint[0])[0].username());
////			dbSource.setPassword(se.profile().accessPoints().toArray(new AccessPoint[0])[0].password());
//			dbSource.setVersionMajor(se.profile().platform().version());
//			dbSource.setVersionMinor(se.profile().platform().minorVersion());
//			lst.add(dbSource);
//		}
////		System.out.println("Found "+lst.size()+" sources of type "+ CATEGORY_NAME);
//		return lst.toArray(new DBSource[0]);
//	}
//	
//	public String createDBSourceInfo(DBSource dbSource, ASLSession session) {
//		
//		ServiceEndpoint se = new ServiceEndpoint();
//		se.newProfile();
//		se.profile().name(dbSource.getSourceName());
//		se.profile().category(DBConstants.RUNNING_CATEGORY_NAME);
//		se.profile().description("For use by ASL harvesters");
//		se.profile().version("1");
//		se.profile().newRuntime();
//		se.profile().runtime().hostedOn(dbSource.getHostName());
//		se.profile().runtime().ghnId("-");
//		se.profile().runtime().status("READY");
//		se.profile().newPlatform();
//		se.profile().platform().name(dbSource.getDBType());
//		se.profile().platform().buildVersion(Short.parseShort("0"));
//		se.profile().platform().revisionVersion(Short.parseShort("0"));
//		se.profile().platform().version((short)dbSource.getVersionMajor());
//		se.profile().platform().minorVersion((short)dbSource.getVersionMinor());
////		AccessPoint ap = new AccessPoint();
////		ap.address("address");
////		ap.description("Database connection credentials");
////		ap.name("database_credentials");
////		ap.credentials(dbInfo.getPassword(), dbInfo.getUserName());
////		se.profile().accessPoints().clear();
////		se.profile().accessPoints().add(ap);
//		RuntimeResource runResource = new RuntimeResource(session);
//		try {
//			runResource.createRuntimeResource(se);
//		} catch (RegistryNotFoundException e) {
////			e.printStackTrace(System.out);
//			return "Could not create the DB source info on IS. ";
//		}
//		return "Created resource on IS";
//	}
//	
//	public String updateDBSourceInfo(DBSource dbInfo, ASLSession session){
//		RuntimeResource runResource = new RuntimeResource(session);
//		for(ServiceEndpoint se : runResource.getRuntimeResourceByCategory(DBConstants.RUNNING_CATEGORY_NAME)){
//			if(!se.profile().name().equals(dbInfo.getSourceName()))//if it's not what we're looking for, bypass it.
//				continue;
//			se.profile().name(dbInfo.getSourceName());
//			se.profile().category(DBConstants.RUNNING_CATEGORY_NAME);
//			se.profile().runtime().hostedOn(dbInfo.getHostName());
//			se.profile().platform().name(dbInfo.getDBType());
//			se.profile().platform().version(Short.parseShort(String.valueOf(dbInfo.getVersionMajor())));
//			se.profile().platform().minorVersion(Short.parseShort(String.valueOf(dbInfo.getVersionMinor())));
//			AccessPoint ap = new AccessPoint();
//			ap.credentials(dbInfo.getPassword(), dbInfo.getUserName());
//			se.profile().accessPoints().clear();
//			se.profile().accessPoints().add(ap);
//			try {
//				runResource.updateRuntimeResource(se);
//			} catch (RemoteException e) {
//				return "Could not update the DB source info on IS";
//			}
//		}
//		return "Updated resource on IS";
//	}
//	
//	public String deleteDBSourceInfo(DBSource dbInfo, ASLSession session) {
//		RuntimeResource runResource = new RuntimeResource(session);
//		for(ServiceEndpoint se : runResource.getRuntimeResourceByCategory(DBConstants.RUNNING_CATEGORY_NAME)){
//			if(!se.profile().name().equals(dbInfo.getSourceName()))//if it's not what we're looking for, bypass it.
//				continue;
//			try {
//				runResource.deleteRuntimeResource(se);
//			} catch (RemoteException e) {
//				return "Could not delete the DB source info from the IS";
//			}
//		}
//		return "Deleted resource from IS";
//	}
//	
//	
//}
