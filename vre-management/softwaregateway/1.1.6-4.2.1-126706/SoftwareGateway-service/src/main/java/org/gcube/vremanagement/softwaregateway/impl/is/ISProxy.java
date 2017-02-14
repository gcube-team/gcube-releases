package org.gcube.vremanagement.softwaregateway.impl.is;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
//import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.impl.packages.MavenPackage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * 
 * Extends the ISManager
 * @author Roberto Cirillo (ISTI - CNR)
 *
 *
 */
public class ISProxy extends ISManager {
	
	private ISCache cache;
	private static HashMap serverCache;
	protected final GCUBELog logger = new GCUBELog(ISProxy.class);
//	private static ISProxy singleton;
	
	public ISProxy(GCUBEScope gCubeScope, boolean cacheEnabled) {
		super(gCubeScope);
		logger.trace("creating IS proxy...");
		this.cacheEnabled=cacheEnabled;
		if(cacheEnabled) {
			logger.trace("cache is enabled");
			cache=ISCache.getInstance();
		}
		serverCache=new HashMap();
	}
	
	
	@Override
	public MavenCoordinates getMavenCoordinates(Coordinates coordinates) throws ISException{
		logger.trace("getMavenCoordinates method ");
//check the cache	
		MavenCoordinates mCoordinates=null;//cache.getMavenCoordinates(gcubeC);
// query to IS		
		if(mCoordinates==null){
			mCoordinates = queryMavenCoordinates(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion(), coordinates.getPackageName(), coordinates.getPackageVersion());
			if((mCoordinates!=null) && (cacheEnabled)){
				cache.put(coordinates, mCoordinates);
			}
		}
		return mCoordinates;
	}
	
	
	public MavenCoordinates getSAMavenCoordinates(Coordinates coordinates) throws ISException{
		logger.trace("getSAMavenCoordinates method ");
//check the cache	
		MavenCoordinates mCoordinates=null;//cache.getMavenCoordinates(gcubeC);
// query to IS		
		if(mCoordinates==null){
			mCoordinates = querySAMavenCoordinates(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion(), coordinates.getPackageName(), coordinates.getPackageVersion());
		}
		return mCoordinates;
	}
	
	
	public GCubeCoordinates getGcubeCoordinates(MavenCoordinates mavenC) throws ISException, BadCoordinatesException{
		GCubeCoordinates gCoordinates=null;
	//check the cache	
		if(cacheEnabled)
			gCoordinates=cache.getGcubeCoordinates(mavenC);
		else
			logger.info("Cache not enabled");
	// query to IS		
		if(gCoordinates==null){
			gCoordinates = queryGCubeCoordinates(mavenC.getGroupId(), mavenC.getArtifactId(), mavenC.getVersion());
			if(gCoordinates!=null)
				logger.info("gcube coordinates found: sc "+gCoordinates.getServiceClass()+" sn "+gCoordinates.getServiceName()+" sv "+gCoordinates.getPackageName()+" ");
			if((gCoordinates!=null) && (cacheEnabled)){
				logger.info("CACHE ELEMENT INSERTED");
				cache.put(mavenC, gCoordinates);
			}
		}
		return gCoordinates;
	}

	
	public void updateProfile(String xml){

	}
	
	public List<URL> getMavenConfiguration(){
		List<URL> list=null;
		return list;
	}

	/**
	 * Retrieves a maven server list from a generic resource 
	 * @param scope
	 * @return
	 * @throws Exception
	 * 
	 */
	@Deprecated
	public String[] getMavenServerListFromGR(GCUBEScope scope) throws Exception {
		logger.trace("getMavenServerList method call for retrieve server list from scope: "+scope.getName());
		GCUBEGenericResourceQuery query = getIsClient().getQuery(GCUBEGenericResourceQuery.class);
		query.addAtomicConditions(new AtomicCondition("/Profile/Name","SoftwareGateway"));
		for (GCUBEGenericResource resource:isClient.execute(query, scope)){
			String body=resource.getBody();
			server=parseXmlFile(new InputSource(new StringReader(body)));
		}
		if(server != null){
			logger.debug("server found: "+server.length);
			for(String s : server){
				logger.debug("server: "+s);
			}
			logger.info("number of servers : "+server.length);
		}
	// if not found check in the enclosing scope	
		if(server == null || (server.length==0)){
			logger.info("server not found try in enclosing scope if it is a VRE scope");
			Type scopeType=scope.getType();
			logger.info("scope type: "+scopeType.VRE+ " equals to  "+Type.VRE);
			if(scopeType.VRE == Type.VRE ){
				logger.info("VRE scope try enclosing scope");
				GCUBEScope newScope=scope.getEnclosingScope();
				logger.info("enclosing scope found: "+newScope);
				if(newScope!=null)
					server=getMavenServerListFromGR(newScope);
				else
					return server;
			}	

		}
		return server;
	}


	/**
	 * Retrieves a maven server list from one ore more runtime resource 
	 * @param scope
	 * @return
	 * @throws ServiceNotAvaiableFault 
	 * @throws Exception
	 */
	public String[] getMavenServerListFromRR(GCUBEScope scope) throws ServiceNotAvaiableFault{
		List<String> serverList=new ArrayList<String>();
		String host=null;
		logger.trace("getMavenServerList method call for retrieve server list from scope: "+scope.getName());
		if((serverCache == null)  || (serverCache.get(scope.getName()) == null)){
			logger.debug("server cache empty for this scope ");
			GCUBERuntimeResourceQuery query;
			List<GCUBERuntimeResource> resources=null;
			try {
				query = getIsClient().getQuery(GCUBERuntimeResourceQuery.class);
//			} catch (ISUnsupportedQueryException | InstantiationException
//					| IllegalAccessException e ) {
//				logger.error("Problem on getQuery method. Impossible to retrieve GCUBERuntimeResourceQuery ");
//				throw new ServiceNotAvaiableFault(e.getMessage());
			} catch (Exception e) {
				logger.error("Problem on getQuery method. Impossible to retrieve GCUBERuntimeResourceQuery ");
				throw new ServiceNotAvaiableFault(e.getMessage());
			}
			query.clearConditions();
			query.addAtomicConditions(new AtomicCondition("/Profile/Name", "MavenRepository"));
			try {
				logger.debug("start query execution in scope "+scope.getName());
				resources=isClient.execute(query, scope);
			} catch (ISException e) {
				logger.error("ISException thrown when executing query");
				throw new ServiceNotAvaiableFault(e.getMessage());
			} catch (Exception e) {
				logger.error("ISException thrown when executing query");
				throw new ServiceNotAvaiableFault(e.getMessage());
			}
			if(resources != null){
				logger.debug("resources found: "+resources.size());
				for (GCUBERuntimeResource resource:resources){
					if((host == null) || (host!=null) && (!host.equals(resource.getHostedOn()))){
						host=resource.getHostedOn();
						logger.debug("host found "+host);
						String url="http://"+host+"/nexus";
						serverList.add(url);
						
					}
				}

			}else
				logger.debug("no resources found on IS ");
			// put in cache
			if(!serverList.isEmpty()){
				logger.debug("caching maven server");
				serverCache.put(scope.getName(), serverList);
			}
		}else{
			if(serverCache==null) serverCache=new HashMap();
			serverList=(List)serverCache.get(scope.getName());
			logger.debug("retrieve from cache: "+serverList);
		}
		if((serverList != null) && (!serverList.isEmpty())){
			server=new String[serverList.size()];
			int i=0;
			for(String s : serverList){
				server[i]=serverList.get(i);
				i++;
			}
		}else{
			server=null;
		}
		if(server != null){
			logger.debug("server found: "+server.length);
			for(String s : server){
				logger.debug("server: "+s);
			}
			logger.info("number of servers : "+server.length);
		}
	// if not found check in the enclosing scope	
		if(server == null || (server.length==0)){
			logger.info("server not found on scope:"+scope.getName()+" try on enclosing scope ");
			Type scopeType=scope.getType();
			if(scopeType.VRE == Type.VRE ){
				GCUBEScope newScope=scope.getEnclosingScope();
				logger.debug("enclosing scope found: "+newScope);
				if(newScope!=null)
					server=getMavenServerListFromRR(newScope);
				else
					return server;
			}	
		}
		return server;
	}

	
	/**
	 * Parse a generic Resource that contains the server list
	 * @param body
	 * @return
	 */
	private String[] parseXmlFile(InputSource body){
		String[] list=null;
		try{
		//get the factory
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		  DocumentBuilder db = dbf.newDocumentBuilder();
		  Document doc = db.parse(body);
		  doc.getDocumentElement().normalize();
		  NodeList nodeLst = doc.getElementsByTagName("server_list");
		  int i=0;
		  list=new String[nodeLst.getLength()];
		  for (int s = 0; s < nodeLst.getLength(); s++) {
		    Node fstNode = nodeLst.item(s);
		    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		      Element fstElmnt = (Element) fstNode;
		      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("server");
		      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
		      String ip=fstNmElmnt.getAttribute("url");
		      list[i]=ip;
		      i++;
		    }
		  }
		} catch (Exception e) {
		   e.printStackTrace();
		}
		return list;
	}

	
	/**
	 * Return a list of packages maven that are related to gcube  coordinates in input
	 * @param coordinates
	 * @return
	 * @throws ISException
	 */
	public List<MavenPackage> getMavenPackagesCoordinates(GCubeCoordinates gcubeC) throws ISException {
		List<MavenPackage> coordList=null;
//check the cache	
		if(cacheEnabled)
			coordList=cache.getMavenPackagesCoordinates(gcubeC);
// query to IS		
		if(coordList==null){
			coordList = queryMavenPackagesCoordinates(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());//MavenCoordinates(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());
			if((coordList!=null) && (cacheEnabled)){
				cache.put(gcubeC, coordList);
			}
		}
		return coordList;

	}
	
	
	/**
	 * Return a list of gcube packages that are related to coordinates in input
	 * @param coordinates
	 * @return
	 * @throws ISException
	 */
	public List<GCubePackage> getGCubePackagesCoordinates(Coordinates coordinates) throws ISException {
		logger.trace("getGCUbePackagesCoordinates method");
		List<GCubePackage> coordList=null;
   //check the cache	
		if(cacheEnabled)
			coordList=cache.getGCubePackagesCoordinates(coordinates);
   // query to IS		
		if(coordList==null){
			coordList = queryGCubePackagesCoordinates(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion(), coordinates.getPackageName(), coordinates.getPackageVersion());//MavenCoordinates(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());
			if((coordList!=null) && (cacheEnabled)){
				cache.put(coordinates, coordList);
			}
		}
		return coordList;
	}


	
/**
 * Retrieve A list of coordinates that are plugin of the coordinates in input
 * @param service coordinates 
 *  
 */
	public List<GCubePackage> getPluginCoordinates(	Coordinates coordinates) throws ISException {
		logger.trace(" getPlugin method");
		List<GCubePackage> coordList=null;
   //check the cache	
		if(cacheEnabled)
			coordList=cache.getPluginCoordinates(coordinates);
   // query to IS		
		if(coordList==null){
			coordList = queryPluginCoordinates(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion(), coordinates.getPackageName(), coordinates.getPackageVersion());//MavenCoordinates(gcubeC.getServiceName(), gcubeC.getServiceClass(), gcubeC.getServiceVersion(), gcubeC.getPackageName(), gcubeC.getPackageVersion());
			if((coordList!=null) && (cacheEnabled)){
				cache.put(coordinates, coordList);
			}
		}
		return coordList;
	}

}
