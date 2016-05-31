package org.gcube.vremanagement.softwaregateway.impl.is;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.impl.packages.MavenPackage;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.FileUtilsExtended;

/**
 * Manages the interactions with InformationSystem service
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class ISManager {
	
	GCUBEScope scope;
	ISClient isClient;
	ISPublisher isPublisher;
	String[] server;
	protected boolean cacheEnabled=true;
	
	
	protected final GCUBELog logger = new GCUBELog(ISManager.class);
	
	
	public ISManager(GCUBEScope gCubeScope){
		scope =gCubeScope;
	}
	
	/**
	 * If not found  a Element in cache, query the IS in order to harvest the profile matching such gCube coordinates
	 * @param gcubeC
	 * @return maven coordinated if found
	 * @throws ISException
	 */
	public abstract MavenCoordinates getMavenCoordinates(Coordinates gcubeC) throws ISException;
	
	/**
	 * If not found  a Element in cache, query the IS in order to harvest the profile matching such gCube coordinates
	 * @param gcubeC
	 * @return a List of mavenCoordinates if found
	 * @throws ISException
	 */
	public abstract List<MavenPackage> getMavenPackagesCoordinates(GCubeCoordinates gcubeC) throws  ISException;
	
	/**
	 * update the profile in the IS
	 * @param xml
	 */
	public abstract void updateProfile(String xml);
	
	/**
	 * 
	 * @return
	 */
	public abstract List<URL> getMavenConfiguration();
	
	/**
	 * If not found  a Element in cache, query the IS in order to harvest the profile matching such gCube coordinates
	 * @param gcubeC
	 * @return a list of gCube coordinates if found in the IS
	 * @throws ISException
	 */
	public abstract List<GCubePackage> getPluginCoordinates(Coordinates gcubeC) throws ISException;
	
	/**
	 * 
	 * @return a istance of a IS client
	 * @throws Exception
	 */
	ISClient getIsClient() throws Exception{
		if(isClient==null)
			isClient=GHNContext.getImplementation(ISClient.class);
		return isClient;
	}
	
	/**
	 * 
	 * @return a istance of a IS publisher
	 * @throws Exception
	 */
	ISPublisher getIsPublisher() throws Exception{
		if(isPublisher==null)
			isPublisher=GHNContext.getImplementation(ISPublisher.class);
		return isPublisher;
	}

	/**
	 * build a query to the IS for retrieving mavenCoordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	protected MavenCoordinates queryMavenCoordinates(String serviceName, String serviceClass, String serviceVersion, String packageName, String packageVersion) throws ISException{
		logger.trace("queryMavenCoordinates method");
		MavenCoordinates mCoordinates=null;
		GCUBEServiceQuery serviceQuery=null;
		List<MavenPackage> listPackages=new ArrayList<MavenPackage>();
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
				throw new ISException();
		}
		serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion,  serviceQuery);
		try{
			logger.debug("execute query in scope: "+scope.getName());
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				logger.debug("found resource: "+resource.getID());
				listPackages= buildMavenPackage(listPackages, resource, packageName, packageVersion);
				if((listPackages!=null) && (listPackages.size()>1)){
					MavenPackage p=listPackages.get(0);
					mCoordinates=(MavenCoordinates)p.getCoordinates();
				}else{
					if(listPackages.size()==1){
						MavenPackage p=listPackages.get(0);
						mCoordinates=(MavenCoordinates)p.getCoordinates();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("ERROR IN RETRIEVE RESOURCE PROFILE FROM IS "+e.getMessage());
		}
		return mCoordinates;
	}
	
	
	/**
	 * build a query to the IS for retrieving SA mavenCoordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	protected MavenCoordinates querySAMavenCoordinates(String serviceName, String serviceClass, String serviceVersion, String packageName, String packageVersion) throws ISException{
		logger.trace("querySAMavenCoordinates method: sc: "+serviceClass+" sn "+serviceName+" sv: "+serviceVersion+" pn: "+packageName+" pv: "+packageVersion);
		MavenCoordinates mCoordinates=null;
		GCUBEServiceQuery serviceQuery=null;
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
				throw new ISException();
		}
		serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion,  serviceQuery);
		try{
			String gId=null;
			String aid=null;
			String v=null;
			logger.debug("execute query in scope: "+scope.getName());
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				List<Package> listP=resource.getPackages();
				for(Package p : listP){
					logger.debug("found package with pn: "+p.getName()+" pv "+p.getVersion());
					try{
						if((packageName == null) || ((packageName!=null) && (packageName.equalsIgnoreCase(p.getName())))){
							gId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId);
							aid=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId);
							v=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
							logger.info("Maven coordinates found for package: "+p.getName()+" v: "+p.getVersion()+" ==>  g "+gId+" a: "+aid+" v: "+v);
							if((gId!=null) && (aid!=null) && (v!=null)){
								if((packageVersion != null) && (packageVersion.equalsIgnoreCase(v))){
									mCoordinates=new MavenCoordinates(gId, aid, v);
									break;
								}else if(packageVersion == null){
									mCoordinates=new MavenCoordinates(gId, aid, v);
									break;
								}
							}
						}else{
							logger.debug(" the package found not correspond to the package searched ");
							logger.debug("package found: "+p.getName()+" pv "+p.getVersion());
							logger.debug("package searched: "+packageName+" pv "+packageVersion);
							
						}

					}catch(Exception e){
						logger.warn("Bad coordinates found in profile or bad profile. skyp next profile. resource id: "+resource.getID());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("ERROR IN RETRIEVE RESOURCE PROFILE FROM IS "+e.getMessage());
		}
		return mCoordinates;
	}
	

	/**
	 * build a query to the IS for retrieving a List of gCube Coordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	protected List<GCubePackage> queryPluginCoordinates(String serviceName,
			String serviceClass, String serviceVersion, String packageName,
			String packageVersion) throws ISException {
		logger.trace(" queryPluginCoordinates method ");
		List<GCubePackage> listPackages=new ArrayList<GCubePackage>();
		GCUBEServiceQuery serviceQuery=null;
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
				throw new ISException();
		}
		serviceQuery=buildPluginQuery(serviceName, serviceClass, serviceVersion, packageName, packageVersion, serviceQuery);
		try{
			for ( GCUBEService resource : isClient.execute(serviceQuery, scope)){
				logger.debug(" Processing resource: "+resource.getID()+ " desc: "+resource.getDescription());
				listPackages = buildGCubePackage(serviceName, serviceClass, serviceVersion, listPackages, resource);
			}
		}catch(Exception e){
			throw new ISException();
		}
		return listPackages;
	}
	
	/**
	 * build a query to the IS for retrieving a List of Maven Coordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	protected List<MavenPackage> queryMavenPackagesCoordinates(String serviceName, String serviceClass, String serviceVersion, String packageName, String packageVersion) throws ISException{
		logger.trace("queyMavenPackagesCoordinates method");
		GCUBEServiceQuery serviceQuery=null;
		List<MavenPackage> listPackages=new ArrayList<MavenPackage>();
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
			throw new ISException();
		}
		serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion, serviceQuery);
		try{
			for ( GCUBEService resource : isClient.execute(serviceQuery, scope)){
				listPackages = buildMavenPackage(listPackages, resource);
			}
		}catch(Exception e){
			throw new ISException();
		}finally{
			if (listPackages == null)
				throw new ISException();
		}
		return listPackages;
	}
	
	/**
	 * build a query to the IS for retrieving a List of gCube Coordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	protected List<GCubePackage> queryGCubePackagesCoordinates(String serviceName, String serviceClass, String serviceVersion, String packageName, String packageVersion) throws ISException{
		logger.trace("queyGCubePackagesCoordinates method");
		GCUBEServiceQuery serviceQuery=null;
		List<GCubePackage> listPackages=new ArrayList<GCubePackage>();
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
			throw new ISException();
		}
		serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion, serviceQuery);
		try{
			logger.debug("execute query on scope: "+scope.getName());
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				listPackages = buildGCubePackage(serviceName, serviceClass, serviceVersion, listPackages, resource);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new ISException();
		}finally{
			if (listPackages == null){
				logger.error("listPackage is null ");
				throw new ISException();
			}
				
		}
		return listPackages;
	}

	
	
	
	/**
	 * build a query to the IS for retrieving a List of Package
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	public List<Package> queryPackagesCoordinates(String serviceName, String serviceClass, String serviceVersion) throws ISException{
		logger.trace("queyGCubePackagesCoordinates method");
		GCUBEServiceQuery serviceQuery=null;
		List<GCubePackage> listPackages=new ArrayList<GCubePackage>();
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
			throw new ISException();
		}
		serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion, serviceQuery);
		List<Package> list=null;
		try{
			logger.debug("execute query on scope: "+scope.getName());
			String maxVersion="0.0.0";
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				logger.debug("resource found: "+resource.getID()+" with pack: ");
				boolean found = false;
				for(Package p : resource.getPackages()){
					logger.debug(" pn:  "+p.getName()+ " pv: "+p.getVersion());
					boolean newVersion= checkVersion(p.getVersion(), maxVersion);
					logger.debug("found? "+newVersion);
					if (newVersion){
						logger.debug(" maxVersion updated with package "+p.getName()+ " v "+p.getVersion()+" on resource: "+resource.getID());
						maxVersion=p.getVersion();
						found=true;
					}
				}
				if(found){	
					list=new ArrayList<Package>();
					list.addAll(resource.getPackages());
				}
			}
		}catch(Exception e){
			logger.error("package cannot be extracted ");
			throw new ISException();
		}finally{
			if (listPackages == null)
				throw new ISException();
		}
		return list;
	}

	
	/**
	 * build a query to the IS for retrieving a List of Maven Coordinates
	 * @param listPackages
	 * @param resource
	 * @param packageVersion 
	 * @param packageName 
	 * @return
	 * @throws BadCoordinatesException
	 */
	private List<MavenPackage> buildMavenPackage(
			List<MavenPackage> listPackages, GCUBEService resource)
			throws BadCoordinatesException {
		logger.trace("buildMavenPackage method");
		List<Package> list=resource.getPackages();
		if(list!= null && list.size()>0){
			logger.debug("found "+list.size()+" packages");
			String gId=null;
			String aid=null;
			String v=null;
			for(int i=0; i<list.size();i++){
				Package p=list.get(i);
				logger.debug("processing package: "+p.getName()+" v: "+p.getVersion());
				gId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId);
				aid=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId);
				v=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
				logger.info("Maven coordinates found for package: "+p.getName()+" v: "+p.getVersion()+" ==>  g "+gId+" a: "+aid+" v: "+v);
				if((gId!=null) && (aid!=null)){
					Coordinates coordinates=new MavenCoordinates(gId,aid,v);
					listPackages.add(new MavenPackage(coordinates));
				
				}
				gId=null;
				aid=null;
				v=null;
			}					
		}else{
			logger.debug("No packages found");
		}
		return listPackages;
	}
	
	
	
	/**
	 * build a list of packages founded in a GCUBEService object
	 * @param listPackages
	 * @param resource
	 * @param packageVersion 
	 * @param packageName 
	 * @return
	 * @throws BadCoordinatesException
	 */
	private List<MavenPackage> buildMavenPackage(
			List<MavenPackage> listPackages, GCUBEService resource, String packageName, String packageVersion)
			throws BadCoordinatesException {
		logger.trace("buildMavenPackage method with pn: "+packageName+" pv "+packageVersion);
		List<Package> list=resource.getPackages();
		if(list!= null && list.size()>0){
			logger.debug("found "+list.size()+" packages");
			String gId=null;
			String aid=null;
			String v=null;
			for(int i=0; i<list.size();i++){
				Package p=list.get(i);
				logger.debug("processing package: "+p.getName()+" v: "+p.getVersion());
				try{
					gId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId);
					aid=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId);
					v=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
					logger.info("Maven coordinates found for package: "+p.getName()+" v: "+p.getVersion()+" ==>  g "+gId+" a: "+aid+" v: "+v);
					if((p.getName().equalsIgnoreCase(packageName)) && (p.getVersion().equalsIgnoreCase(packageVersion))){
							logger.info("Maven coordinates found for package: "+p.getName()+" v: "+p.getVersion()+" ==>  g "+gId+" a: "+aid+" v: "+v);
							if((gId!=null) && (aid!=null) ){
								if((packageVersion != null) && (packageVersion.equalsIgnoreCase(v))){
									Coordinates coordinates=new MavenCoordinates(gId,aid,v);
									listPackages.add(new MavenPackage(coordinates));
									logger.debug("package added ");
									break;
								}else if(packageVersion == null){
									Coordinates coordinates=new MavenCoordinates(gId,aid,v);
									listPackages.add(new MavenPackage(coordinates));
									logger.debug("package added ");
									break;
								}
							}  
					}else if((packageName==null) && (packageVersion==null)){
			//if there aren't name and version get all packages		
						logger.debug("packageName & package version are null ");
						Coordinates coordinates=new MavenCoordinates(gId,aid,v);
						listPackages.add(new MavenPackage(coordinates));
						logger.debug("package added");
					}
				}catch(Exception e){
					logger.warn("BadCoordinate found "+e.getMessage());
				}
			}					
		}else{
			logger.debug("No packages found");
		}
		return listPackages;
	}
	
	
	
	/**
	 * From a GCUBEService object build a list of GCubePackage  that matches with the gcube Coordinates in input
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param listPackages
	 * @param resource
	 * @return
	 * @throws BadCoordinatesException
	 */
	private List<GCubePackage> buildGCubePackage(String serviceName, String serviceClass, String serviceVersion,
			List<GCubePackage> listPackages, GCUBEService resource)
			throws BadCoordinatesException {
		logger.trace("buildGCubePackage method");
		List<Package> list=resource.getPackages();
		if(list!= null && list.size()>0){
			String pn=null;
			String pv=null;
			logger.debug("Found "+list.size()+" package");
			for(int i=0; i<list.size();i++){
					pn=list.get(i).getName();
					pv=list.get(i).getVersion();
				if((pn!=null) && (pv!=null)){
					Coordinates coordinates=new GCubeCoordinates(serviceName, serviceClass, serviceVersion, pn, pv);
					logger.debug("added Package: pn:"+pn+" pv: "+pv+" to the responselist");
					listPackages.add(new GCubePackage(coordinates));
				}
				pn=null;
				pv=null;
			}					
		}
		return listPackages;
	}

	/**
	 * Remove package that matches with input coordinates from a profile. Public the new profile on IS
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @return
	 * @throws ISException
	 */
	 public List<GCubePackage> updatePackageResource(String serviceName, String serviceClass, String serviceVersion, String packageName, String packageVersion) throws ISException{
			logger.trace("updatePackageResource method");
		 	GCUBEServiceQuery serviceQuery=null;
			List<GCubePackage> listPackages=null;
			try{
				serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
			}catch(Exception e ){
				throw new ISException();
			}
			serviceQuery=buildServiceQuery(serviceName, serviceClass, serviceVersion,  serviceQuery);
			GCUBEService newResource=null;
			logger.debug(" myService: class: "+serviceClass+" name: "+serviceName+" version: "+serviceVersion);
			logger.debug(" myPackage: name: "+packageName+" version: "+packageVersion);
			try{
				for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
						List<Package> list=resource.getPackages();
						List<Package> packageToRemove=new ArrayList<Package>();
						if(list != null){
							int i=-1;
							for(i=0;i<list.size();i++){
								Package p=list.get(i);
								logger.debug("Package found: n: "+p.getName()+" v: "+p.getVersion()+" in scopes: ");
								if ((p.getName().equalsIgnoreCase(packageName)) && (p.getVersion().equalsIgnoreCase(packageVersion))){
									logger.debug(" remove package: "+p.getName()+" "+p.getVersion()+" from sc "+serviceClass+" sn "+serviceName+" sv: "+serviceVersion);
									packageToRemove.add(p);
									break;
								}
							}
							if((packageToRemove!=null) && (packageToRemove.size()>0)){
								if(list.size() == 1){
									resource=setInstanceScopeOnResource(resource);
								}
								publicResourceIS(resource, packageToRemove);
							}
						}
				}
				logger.debug("new resource for public in IS: "+newResource);
				removeLocallyResource(serviceClass, serviceName, packageName, packageVersion);
			}catch(Exception e){
				throw new ISException();
			}
			return listPackages;
	 }

	/**
	 * Update the scopes of the resource in input with the instance scopes
	 * @param resource
	 * @param scopeToRemove
	 * @param allScope
	 */
	private GCUBEService setInstanceScopeOnResource(GCUBEService resource) {
		logger.trace("setInstanceScopeOnResource method");
		Map<String, GCUBEScope> origScopeMap;
		origScopeMap=resource.getScopes();
		Set<String> s=origScopeMap.keySet();
		HashMap<String, GCUBEScope> allScope=new HashMap<String, GCUBEScope>();
		for(String scopeName : s){
			allScope.put(scopeName, origScopeMap.get(scopeName));
		}
		Set <String> resourceScopeSet=allScope.keySet();
// remove scopes from resource		
		for(String scope : resourceScopeSet){
			resource.removeScope(allScope.get(scope));
		}
		
// fill scopes on resource		
		Map<String , GCUBEScope> serviceScopeMap=ServiceContext.getContext().getInstance().getScopes();
		Set<String> serviceScopeSet=serviceScopeMap.keySet();
		for(String scope : serviceScopeSet){
			resource.addScope(serviceScopeMap.get(scope));
		}
		return resource;
	}

	
	/**
	 * Remove a resource from local jetty
	 * @param serviceClass
	 * @param serviceName
	 * @param packageName
	 * @param packageVersion
	 */
	private void removeLocallyResource(String serviceClass, String serviceName,
			String packageName, String packageVersion) {
		logger.trace(" removeLocallyResource(String, String, String, String) method");
		String dir=ServiceContext.getContext().getHttpServerBasePath().getAbsolutePath() +File.separator+ ServiceContext.getContext().getMavenRelativeDir()+File.separator+serviceClass+File.separator+serviceName+File.separator+packageName+File.separator+packageVersion;
		File localResource= new File(dir);
		
		if(localResource.isDirectory()){
			boolean deleted=FileUtilsExtended.recursiveDeleteDirectory(localResource);
			if(deleted)
				logger.debug("directory: "+localResource.getAbsolutePath()+" deleted");
			else
				logger.warn("directory: "+localResource.getAbsolutePath()+" not deleted");
		}
	}

	/**
	 * updates and publics a new resource on the IS
	 * @param newResource
	 * @throws Exception
	 * @throws ISPublisherException
	 */
	public void publicResourceIS(GCUBEService newResource, List<Package> packageToRemove) throws Exception,
			ISPublisherException {
		logger.trace("publicResourceIS method ");
		logger.debug("new resource "+newResource);
		isPublisher=getIsPublisher();
		Map<String, GCUBEScope> resourceScope=newResource.getScopes();
		Set<String> s=resourceScope.keySet();
		if(s.size()>0){
			for(String scopeName : s){
				logger.debug("scope: "+scopeName);
			}
		}else{
			logger.debug("The set of scopes is empty! ");
		}
		//public in the IS
		if((newResource.getPackages().size() - packageToRemove.size()) > 0){
			logger.debug("packages found in new resource: "+newResource.getPackages().size());
			for(String scopeName : s){
				GCUBEScope scopeResFound=resourceScope.get(scopeName);
				List<Package> list=newResource.getPackages();
				for(Package p : packageToRemove){
					logger.debug(" try to remove package "+p.getName()+" v "+p.getVersion());
					try{
						for(int i=0;i<list.size();i++){
							Package pRes=list.get(i);
							if(pRes.getName().equalsIgnoreCase(p.getName()) &&(pRes.getVersion().equalsIgnoreCase(p.getVersion()))){
								list.remove(i);
								break;
							}
						}
						
					}catch(Exception e){
						logger.error("package not removed: "+e.getMessage());
						e.printStackTrace();
					}
				}
				isPublisher.registerGCUBEResource(newResource, scopeResFound, ServiceContext.getContext());
				logger.debug("Package register in scope: "+scopeResFound.getName());
			}
		}else{
			for(String scopeName : s){
				GCUBEScope scope=resourceScope.get(scopeName);
				logger.debug("... from scope: "+scopeName);
				isPublisher.removeGCUBEResource(newResource.getID(), newResource.getType(), scope, ServiceContext.getContext()); //registerGCUBEResource(newResource, scope, ServiceContext.getContext());
				logger.info("Resource Removed succesfully");
			}

		}
	}

	/**
	 * Publish a resource in the InformationSystem service, in the scope specified in input. 
	 * @param resource
	 * @param scope a gCube scope
	 * @return If the resource is not present in IS return the new ID else return null 
	 * @throws Exception
	 * @throws ISPublisherException
	 */
	public String publicResourceIS(GCUBEService resource, GCUBEScope scope) throws Exception,
		ISPublisherException {
		logger.trace(" publicResourceIS method");
		//public in the IS
		String idFound=null;
		String idNew=null;
		if(resource!=null){
			idFound = checkResourceOnIS(resource);
			if(idFound!=null)
				resource.setID(idFound);
			isPublisher=getIsPublisher();
			idNew=isPublisher.registerGCUBEResource(resource, scope, ServiceContext.getContext());
			logger.info("Published resource: sc "+resource.getServiceClass()+" sn: "+resource.getServiceName()+" v "+resource.getVersion()+" in scope: "+scope.getName());
		}
		if(idFound != null)
			return null;
		else
			return idNew;
	}

	
	/**
	 * Check if the resource is present on Information system service
	 * @param resource
	 * @return id if present otherwise null
	 * @throws ISException
	 */
	public String checkResourceOnIS(GCUBEService resource) throws ISException {
		GCUBEServiceQuery serviceQuery=null;
		logger.trace(" checkResourceOnIS method");
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
			logger.error("is Exception");
			throw new ISException();
		}
		serviceQuery=buildServiceQuery( resource.getServiceName(), resource.getServiceClass(), resource.getVersion(), serviceQuery);
		List<GCUBEService> list=null;
		try{
			logger.debug("execute query on scope: "+scope.getName()+" with coordinates: "+resource.getServiceClass()+" "+resource.getServiceName()+"  "+resource.getVersion());
			list=isClient.execute(serviceQuery, scope);
		}catch(Exception e){
			logger.error("is Exception");
			throw new ISException();
		}
		if((list==null) || (list.isEmpty())){
			logger.info("resource not present in IS");
			return null;
		}else{
			String id=list.get(0).getID();
			logger.info("resource present in IS with id: "+id);
			return id;
		}
	}

		
	/**
	 * Query to IS for converts Maven Coordinates in GCube Coordinates
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 * @throws ISException
	 * @throws BadCoordinatesException 
	 */
	public GCubeCoordinates queryGCubeCoordinates(String groupId, String artifactId, String version) throws  ISException, BadCoordinatesException{
		logger.trace("queryGCubeCoordinates method with m coordinates: "+groupId+" "+artifactId+" "+version);
		GCUBEServiceQuery serviceQuery=null;
		try{
			serviceQuery=getIsClient().getQuery(GCUBEServiceQuery.class);
		}catch(Exception e ){
				e.printStackTrace();
				throw new ISException();
		}
		serviceQuery=buildMavenQuery(groupId, artifactId, version, serviceQuery);
		String sn=null;
		String sc=null;
		String sv=null;
		String pn=null;
		String pv=null;
		try{
			logger.debug("query to is");
			for ( GCUBEService resource:isClient.execute(serviceQuery, scope)){
				logger.debug("query executed");
				sn=resource.getServiceName();
				sc= resource.getServiceClass();
				sv=resource.getVersion();
				logger.debug("processing: sn "+sn+" sc "+sc+" v "+sv);
				pn=null;
				pv=null;
				List<Package> list=resource.getPackages();
				for(Package p: list){
					if((p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId).equalsIgnoreCase(groupId)) && (p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId).equalsIgnoreCase(artifactId)) && (p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version).equalsIgnoreCase(version))){
						pn=p.getName();
						pv=p.getVersion();
						logger.info("found package Coordinates: pn: "+pn+" pv "+pv);
						break;
					}
				}
				if((pn!=null) && (pv!=null)){
					break;
				}
			}
		}catch(Exception e){
			logger.error(""+e.getMessage()+" "+sc+sn+sv+pn+pv);
			e.printStackTrace();
			throw new ISException();
		}finally{
			if((pn== null) || (pv==null) || (sn==null) || (sc==null)){
				logger.error("ISEXCEPTION generated impossible convert from maven to gcube sc: "+sc+" sn "+sn+" sv "+sv+" phn "+pn+" pv "+pv);
				throw new ISException();
			}
		}
		logger.debug("queryGCubeCoordinates method end gcube coordinates found: "+sc+" "+sn+" "+sv+" "+pn+" "+pv);
		return new GCubeCoordinates(sn, sc, sv, pn, pv);
	}
	
	/**
	 * Prepare a query for IS. Searches resources with this maven coordinates
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param serviceQuery
	 */
	private GCUBEServiceQuery buildMavenQuery(String groupId, String artifactId,
			String version,	GCUBEServiceQuery serviceQuery) {
		logger.trace("buildMavenQuery method gId: "+groupId+" aid: "+artifactId+" v: "+version);
		serviceQuery.addGenericCondition("count($result//MavenCoordinates[./groupId/string()='"+groupId+"' and ./artifactId/string()='"+artifactId+"' and ./version/string()='"+version+"'])>0");
		return serviceQuery;
	}

	/**
	 * prepare a query for Information System.  Searches resources with this  3 gcube coordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param serviceQuery
	 * @return
	 */
	protected GCUBEServiceQuery buildServiceQuery(String serviceName, String serviceClass,
			String serviceVersion,	GCUBEServiceQuery serviceQuery) {
		logger.trace("buildServiceQuery method with sc "+serviceClass+" sn "+serviceName+ " sv "+serviceVersion);
		serviceQuery.addAtomicConditions(new AtomicCondition("/Profile/Name",serviceName));
		serviceQuery.addAtomicConditions(new AtomicCondition("/Profile/Class",serviceClass));
		serviceQuery.addAtomicConditions(new AtomicCondition("/Profile/Version",serviceVersion));
		return serviceQuery;
	}

	/**
	 * Prepare a query for Information System.  Searches plugin resources with this 5 gcube coordinates
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param serviceQuery
	 * @return
	 */
	private GCUBEServiceQuery buildPluginQuery(String serviceName, String serviceClass,
			String serviceVersion,	String packageName, String packageVersion, GCUBEServiceQuery serviceQuery) {
		logger.trace("buildPluginQuery method");
		serviceQuery.addAtomicConditions(new AtomicCondition("//TargetService/Service/Class",serviceClass));
		serviceQuery.addAtomicConditions(new AtomicCondition("//TargetService/Service/Name",serviceName));
		serviceQuery.addAtomicConditions(new AtomicCondition("//TargetService/Service/Version",serviceVersion));
		serviceQuery.addAtomicConditions(new AtomicCondition("//TargetService/Package",packageName));
		serviceQuery.addAtomicConditions(new AtomicCondition("//TargetService/Version",packageVersion));
		return serviceQuery;
	}

	/**
	 * If v1  > v2 return true else false
	 * @param v1
	 * @param v2
	 * @return
	 */
		private boolean checkVersion(String v1, String v2) {
			boolean v1Win=false;
			logger.debug("v1 "+v1+" v2 "+v2 );
			if(v1.contains("-SNAPSHOT"))
				v1=v1.substring(0, v1.lastIndexOf("-SNAPSHOT"));
			if(v2.contains("-SNAPSHOT"))
				v2=v2.substring(0, v2.lastIndexOf("-SNAPSHOT"));
// added theese checks for new versioning system on gCube release 2.12.0 new version are: 1.2.3-2.12.0
			logger.debug("delete gcube release version from "+v1+" "+v2);
			if(v1.contains("-"))
				v1=v1.substring(0, v1.lastIndexOf("-"));
			if(v2.contains("-"))
				v2=v2.substring(0, v2.lastIndexOf("-"));
			logger.debug("new version "+v1+" "+v2);
			logger.debug("v1: "+v1+ " v2: "+v2);
			String[] v1Array = v1.split("\\.");
			String[] v2Array = v2.split("\\.");
			int length=v1Array.length;
			logger.debug("length1: "+v1Array.length+" length2: "+v2Array.length);
		// get the smallest	
			if(v1Array.length > v2Array.length)
				length=v2Array.length;
			logger.debug("length: "+length);
			for(int i=0;i<length;i++){
				logger.debug("v1 "+v1Array[i]+ "   v2 "+v2Array[i]);
				if(Integer.parseInt(v1Array[i]) > Integer.parseInt(v2Array[i])){
					return true;
				}else if(Integer.parseInt(v1Array[i]) < Integer.parseInt(v2Array[i]))
					return false;
			}
			return v1Win;
		}
}
