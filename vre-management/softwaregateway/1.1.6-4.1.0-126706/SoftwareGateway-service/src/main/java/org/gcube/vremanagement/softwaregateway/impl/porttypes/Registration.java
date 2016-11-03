package org.gcube.vremanagement.softwaregateway.impl.porttypes;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.softwaregateway.answer.AnswerBuild;
import org.gcube.vremanagement.softwaregateway.answer.ReportObject;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISCache;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.cache.NexusCache;
import org.gcube.vremanagement.softwaregateway.stubs.LocationCoordinates;


public class Registration  extends GCUBEPortType {

	/**
	 * Register a profile in the InformationSystem
	 * @param profile
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public String register(String profile) throws ServiceNotAvaiableFault{
		logger.trace("register method ");
// retrieve scopes
		Map<String, GCUBEScope> list=ServiceContext.getContext().getInstance().getScopes();
		logger.debug("Register resource with following start scopes:");
		Set<String> setScopes=list.keySet();
		for(String scope : setScopes){
			logger.debug("scope: "+scope);
		}	
//		GCUBEService resource = loadAndCheck(profile);
// MOD		
		GCUBEService resource = load(profile);
		checkResourceCoordinates(resource);
		boolean sv= checkServiceVersion(resource);
// END MOD		
		for(String scope : setScopes){
			logger.debug(" added scope: "+scope+" to resource "+resource.getID()+" with description:"+resource.getDescription());
			resource.addScope(list.get(scope));
		}
		Set<String> s=resource.getScopes().keySet();
		String idNewResource=null;
		if(s.size() == 0)
			logger.debug("The scope set is empty! The resource not will be pubblicated");
		else{
			idNewResource = checkOnAllScopes(resource, s);
		}
		List<ReportObject> listReport=new ArrayList<ReportObject>();
	// if the serviceVersion is bad then the report is a error report	
		if(!sv){
			listReport=buildResourceReport(resource, listReport, list, "ERROR");
		}else{
			// if the resource is founded on is then i will do an update operation else a new operation			
			if(idNewResource != null)
				listReport=buildResourceReport(resource, listReport, list, "UPDATE");
			else
				listReport=buildResourceReport(resource, listReport, list, "NEW");
		}
//if there isn't error in the listReport i public the resource on IS else i don't public the resource on IS
		boolean errorFound=false;
		for(ReportObject r : listReport){
			if(r.status.equalsIgnoreCase("ERROR")){
				errorFound=true;
				break;
			}
		}
		if (!errorFound){
			publicOnAllScopes(resource, s);
		}else{
//				DELETE OPERATION IS NEEDED ???????
		}
		AnswerBuild answer= new AnswerBuild();
		String report=answer.constructReportAnswer(listReport);
		logger.info("Report:");
		logger.info(report);
		return report;

	}

	
	protected GCUBEService load(String profile)
			throws ServiceNotAvaiableFault {
		GCUBEService resource=null;
		try {
			resource = GHNContext.getImplementation(GCUBEService.class);
			logger.debug("load resource: "+profile);
			resource.load(new StringReader(profile.trim()));
//			if(!resource.getVersion().equalsIgnoreCase("1.0.0")){
//				resource.setVersion("1.0.0");
//			}
		}catch(Exception e){
			throw new ServiceNotAvaiableFault("load resource exception "+e.getMessage());
		}
//		checkResourceCoordinates(resource);
		return resource;
	}

	protected boolean checkServiceVersion(GCUBEService resource){
		if((!resource.getVersion().equalsIgnoreCase("1.0.0")) && (!resource.getVersion().equalsIgnoreCase("1.00.00")) && (!resource.getVersion().equalsIgnoreCase("1.0.00")) && (!resource.getVersion().equalsIgnoreCase("1.00.0"))){
//			throw new IllegalArgumentException(" the resource have a bad serviceVersion: "+resource.getVersion()+" the service version must be 1.0.0 ");
			return false;
		}else{
			return true;
		}
	}
	
	protected GCUBEService loadAndCheck(String profile)
			throws ServiceNotAvaiableFault {
		GCUBEService resource=null;
		try {
			resource = GHNContext.getImplementation(GCUBEService.class);
			logger.debug("load resource: "+profile);
			resource.load(new StringReader(profile.trim()));
//			if(!resource.getVersion().equalsIgnoreCase("1.0.0")){
//				resource.setVersion("1.0.0");
//			}
		}catch(Exception e){
			throw new ServiceNotAvaiableFault("load resource exception "+e.getMessage());
		}
		if((!resource.getVersion().equalsIgnoreCase("1.0.0")) && (!resource.getVersion().equalsIgnoreCase("1.00.00")) && (!resource.getVersion().equalsIgnoreCase("1.0.00")) && (!resource.getVersion().equalsIgnoreCase("1.00.0"))){
			
			throw new IllegalArgumentException(" the resource have a bad serviceVersion: "+resource.getVersion()+" the service version must be 1.0.0 ");
		}

	// disabling cache disabling method removeAllPAckagesFromCache
//			removeAllPAckagesFromCache(resource);
			checkResourceCoordinates(resource);
		return resource;
	}

	/**
	 * @param resource
	 * @param s
	 * @throws ServiceNotAvaiableFault
	 */
	private void publicOnAllScopes(GCUBEService resource, Set<String> s)
			throws ServiceNotAvaiableFault {
		for(String scopeName : s){
			GCUBEScope scope=GCUBEScope.getScope(scopeName);
			ISProxy is=new ISProxy(scope, false);
			try{
				logger.debug("public resource in scope: "+scopeName);
				is.publicResourceIS(resource, scope);
			} catch (Exception e) {
				throw new ServiceNotAvaiableFault("problem to public resource in IS");
			}

		}
	}

	/**
	 * @param resource
	 * @param s
	 * @param idNewResource
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	private String checkOnAllScopes(GCUBEService resource, Set<String> s) throws ServiceNotAvaiableFault {
		String idNewResource=null;
		for(String scopeName : s){
			GCUBEScope scope=GCUBEScope.getScope(scopeName);
			ISProxy is=new ISProxy(scope, false);
			try{
				logger.debug("public resource in scope: "+scopeName);
				idNewResource=is.checkResourceOnIS(resource);
			} catch (Exception e) {
				throw new ServiceNotAvaiableFault("problem to public resource in IS");
			}

		}
		return idNewResource;
	}

	protected void checkResourceCoordinates(GCUBEService resource) throws ServiceNotAvaiableFault {
		List<Package> listP=resource.getPackages();
		for(Package  p : listP){
			String v=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
			if(!p.getVersion().equals(v))
				throw new ServiceNotAvaiableFault("The package version not corresponds to maven coordinate version");
		}
	}

	/**
	 * @param resource
	 */
	protected List<ReportObject> buildResourceReport(GCUBEService resource, List<ReportObject> listReport, Map<String, GCUBEScope> listScopes, String operation) {
		logger.trace("buildResourceReport method");
		List<org.gcube.common.core.resources.service.Package> listPackage= resource.getPackages();
		if(listPackage!=null)
			logger.debug("packages founded: "+listPackage.size());
		String groupID=null;
		String artifactId=null;
		String version=null;
		for(org.gcube.common.core.resources.service.Package p : listPackage){
			groupID=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId);
			artifactId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId);
			version=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
			logger.debug("buildReport:package:"+p.getName()+" v "+p.getVersion()+" maven coordinate founded: g"+groupID+"  a: "+artifactId+" v: "+version);
			ReportObject report =null;
	// if the operation is equal to error there is a problem on resource identification eg: the serviceVersion is not equal to 1.0.0		
			if(operation.equalsIgnoreCase("ERROR")){
				report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), "", "", "INVALID VERSION", "ERROR", resource.getID());
				listReport.add(report);
			}else{
				String artifactUrl=null;
				String javadocUrl=null;
				GCubePackage gcubePackage=null;
				boolean jarUrl=true;
				try{
					Coordinates coordinates= new GCubeCoordinates(resource.getServiceName(), resource.getServiceClass(), resource.getVersion(), p.getName(), p.getVersion());
					coordinates.setGroupId(groupID);
					coordinates.setArtifactId(artifactId);
					coordinates.setVersion(version);
					gcubePackage=new GCubePackage(coordinates);
				
					artifactUrl=gcubePackage.getLocation();
					
				}catch(ServiceNotAvaiableFault e){
					jarUrl=false;
				}
				try {
					javadocUrl=gcubePackage.getDocLocation();
				} catch (ServiceNotAvaiableFault e) {
					logger.error("javadoc not found ");
					if(jarUrl)
						report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), artifactUrl, javadocUrl, "WARN", operation, resource.getID());
					else
						report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), artifactUrl, javadocUrl, "ERROR", operation, resource.getID());
					listReport.add(report);
				} 
				if(report== null){
					if(artifactUrl != null && javadocUrl!=null)
						report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), artifactUrl, javadocUrl, "SUCCESS", operation, resource.getID());
					else if(artifactUrl == null)
						report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), artifactUrl, javadocUrl, "ERROR", operation, resource.getID());
					else
						report = new ReportObject(groupID, artifactId, version, Long.toString(System.currentTimeMillis()), artifactUrl, javadocUrl, "WARN", operation, resource.getID());
					listReport.add(report);
				}
				report=null;
				groupID=null;
				artifactId=null;
				version=null;
			}
		}
		return listReport;
	}

	/**
	 * @param resource
	 * @throws BadCoordinatesException
	 */
	private void removeAllPAckagesFromCache(GCUBEService resource)
			throws BadCoordinatesException {
		List<org.gcube.common.core.resources.service.Package> packageList=resource.getPackages();
		NexusCache cacheNExus=NexusCache.getInstance(null);
		ISCache cacheIS=ISCache.getInstance();
		for(org.gcube.common.core.resources.service.Package p: packageList){
			String pn=p.getName();
			String pv=p.getVersion();
			logger.debug("CHECK PACKAGE FROM RESOURCE: "+resource.getID()+" pn "+pn+" pv: "+pv);
			Coordinates coordinates=new GCubeCoordinates(resource.getServiceName(), resource.getServiceClass(), resource.getVersion(), pn, pv);
			GCubeCoordinates gcubeC=new GCubeCoordinates(resource.getServiceName(), resource.getServiceClass(), resource.getVersion(), pn, pv);
			cacheIS.remove(gcubeC);
			String cacheString=cacheNExus.buildGCubeCoordinatesCacheInputString(coordinates, "jar", null);
			cacheNExus.remove(cacheString);
			cacheString=cacheNExus.buildGCubeCoordinatesCacheInputString(coordinates, "pom", null);
			cacheNExus.remove(cacheString);
			cacheString=cacheNExus.buildGCubeCoordinatesCacheInputString(coordinates, "tar.gz", "servicearchive");
			cacheNExus.remove(cacheString);
			removeLocalSAURL(coordinates);
		}
	}

	/**
	 * Unregister a package in the Information System
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @throws ServiceNotAvaiableFault
	 */
	public void unregister(LocationCoordinates coord) throws ServiceNotAvaiableFault{
		logger.trace("unregister() invoked with coordinates: "+coord.getServiceClass()+" "+coord.getServiceName()+"  "+coord.getServiceVersion()+"  "+coord.getPackageName()+"  "+coord.getPackageVersion());
		try{
			GCubeCoordinates coordinates= new GCubeCoordinates(coord.getServiceName(), coord.getServiceClass(), coord.getServiceVersion(), coord.getPackageName(), coord.getPackageVersion());
			GCubePackage p=new GCubePackage(coordinates);
			p.unregister();
		}catch(Exception e){
			e.printStackTrace();
			throw new ServiceNotAvaiableFault(e.getMessage());
		}
	}

	/**
	 * Return the current context
	 */
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	
	/**
	 * remove a local SA archive ,
	 * @param coordinates coordinates that identifies  the SA
	 * 
	 */
	public void removeLocalSAURL(Coordinates coordinates) {		
		String relativePath=File.separator+coordinates.getServiceClass()+File.separator+coordinates.getServiceName()+File.separator+coordinates.getPackageName()+File.separator+coordinates.getPackageVersion();
		File urlDirectory = new File(ServiceContext.getContext().getHttpServerBasePath().getAbsolutePath() +File.separator+ ServiceContext.getContext().getMavenRelativeDir()+relativePath);
		if(urlDirectory.exists()){
			try {
				FileUtils.deleteDirectory(urlDirectory);
			} catch (IOException e) {
				logger.info(" Impossible to delete directory : "+urlDirectory.getAbsolutePath());
			}
		}
	}

	
}
