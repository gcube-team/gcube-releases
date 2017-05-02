package org.gcube.vremanagement.softwaregateway.impl.packages;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManagerFactory;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;


public class GCubePackage extends Package {
	
	Coordinates coordinates;
	protected final GCUBELog logger = new GCUBELog(GCubePackage.class);
	
	public GCubePackage(Coordinates coordinates) throws BadCoordinatesException{
		setCoordinates(coordinates);
	}

/**
 * Retrieve location of Package	
 */
	@Override
	public String getLocation() throws ServiceNotAvaiableFault {
		logger.trace("GCubePackage, getLocation method");
		String url=null;
		initialize();
		try{
	// retrieve or convert in maven Coordinates		
			MavenCoordinates mavenC = getMavenCoordinates();
	// Call to RepositoryManager with maven coordinates
			RepositoryManagerFactory rmf= new RepositoryManagerFactory();		
			rm= rmf.getRepositoryManager(is.getMavenServerListFromRR(scope), true);
			url=(String)rm.get(mavenC, "jar", "servicearchive");			
			logger.debug(" url of jar founded: "+url);
		}catch(Exception e){
			throw new ServiceNotAvaiableFault(e.getMessage());
		}finally{
			if(url==null)
				throw new ServiceNotAvaiableFault("url is null ");
		}
		return url;
	}
	
	/**
	 * get javadoc location of the package
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public String getDocLocation() throws ServiceNotAvaiableFault {
		logger.trace("GCubePackage, getDocLocation method");
		String url=null;
		initialize();
		try{
	// retrieve or convert in maven Coordinates		
			MavenCoordinates mavenC = getMavenCoordinates();
	// Call to RepositoryManager with maven coordinates
			RepositoryManagerFactory rmf= new RepositoryManagerFactory();		
			rm= rmf.getRepositoryManager(is.getMavenServerListFromRR(scope), true);
			url=(String)rm.get(mavenC, "jar", "javadoc");			
			logger.debug(" url of javadoc founded: "+url);
		}catch(Exception e){
			throw new ServiceNotAvaiableFault(e.getMessage());
		}finally{
			if(url==null)
				throw new ServiceNotAvaiableFault("url is null ");
		}
		return url;
	}
	

/**
 * Retrieve location of softwareArchive identifies by coordinates	
 */
	@Override
	public String getSALocation() throws ServiceNotAvaiableFault {
		logger.trace(" getSALocation method");
		String url=null;
		initialize();
		logger.debug("Create a temporary directory: "+ coordinates.getServiceClass()+" in directory: "+ServiceContext.getContext().getTmp());
		File tmpParentDirectory = new File(ServiceContext.getContext().getTmp(), coordinates.getServiceClass());
		if(!tmpParentDirectory.exists())
			tmpParentDirectory.mkdir();
		File tmpTargetDirectory= new File(tmpParentDirectory, coordinates.getServiceName()+"_"+coordinates.getServiceVersion());
		try{
			MavenCoordinates mavenC = getSAMavenCoordinates();
			List<MavenCoordinates> mcList=new ArrayList<MavenCoordinates>();
			mcList.add(mavenC);
	// Call to RepositoryManager with maven coordinates to retrieve the url
			RepositoryManagerFactory rmf= new RepositoryManagerFactory();		
			rm= rmf.getRepositoryManager(is.getMavenServerListFromRR(scope), true);
			url = rm.getSALocation(tmpTargetDirectory, mcList, coordinates);
		}catch(Exception e){
			throw new ServiceNotAvaiableFault(e.getMessage());
		}finally{
			if(url==null)
				throw new ServiceNotAvaiableFault("url is null ");
		}
		return url;
	}

	
	/**
	 * Return an XML document that contains resolved and missing dependencies
	 * ex:
	 * 
	 * 
	 */
	@Override
	public String getDependencies() throws ServiceNotAvaiableFault {
		logger.trace("GCubePackage, getDependencies method");
		initialize();
		String result=null;
		try{
			result=findDeps();
		}catch(Exception e){
			logger.error("Failed to retrive software deps for " + this.coordinates.getServiceName(), e);
			throw new ServiceNotAvaiableFault();
		}
		if(result == null) {
			logger.error("Failed to retrive software deps for " + this.coordinates.getServiceName() + ": repository returned null results");
			throw new ServiceNotAvaiableFault("Failed to retrive software deps for " + this.coordinates.getServiceName() + ": repository returned null results");
		}
		
		return result;
	}

	/**
	 * This method retrieves all packages that matches with the coordinates
	 */
	@Override
	public List<GCubePackage> getPackages() throws ServiceNotAvaiableFault {
		logger.trace("GCubePackage, getPackages method");
		initialize();
		List<GCubePackage> coordList=null;
		try{
			coordList=is.getGCubePackagesCoordinates(coordinates);
		}catch(ISException e){
			throw new ServiceNotAvaiableFault("Coordinates not found");
		}finally{
			if(coordList == null)
				throw new ServiceNotAvaiableFault("Coordinates not found");
		}
		return coordList;
	}

	/**
	 * this method retrieves all plugin coordinates matches with the coordinates 
	 */
	@Override
	public List<GCubePackage> getPlugins() throws ServiceNotAvaiableFault {
		logger.trace(" getPlugins method ");
		initialize();
		List<GCubePackage> coordList=null;
		try{
			coordList=is.getPluginCoordinates(coordinates);
		}catch(ISException e){
			throw new ServiceNotAvaiableFault();
		}
		return coordList;
	}

	/**
	 * Register a profile in IS.
	 * This method isn't implemented for this class.
	 * The real implementation is in the Register class
	 *	 
	 */
	@Override
	public String register() {
		return null;
	}
	/**
	 * Unregister a profile in IS.
	 * This method isn't implemented for this class.
	 * The real implementation is in the Register class
	 *	 
	 */
	@Override
	public void unregister() throws ServiceNotAvaiableFault {
		logger.trace("GCubePackage unregister method");	
		initialize();
		// get list of gcubePackage	
		try {
			is.updatePackageResource(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion(), coordinates.getPackageName(), coordinates.getPackageVersion());
		} catch (ISException e) {
			throw new ServiceNotAvaiableFault();
		}
	}
	/**
	 * Return the current coordinates
	 * 
	 */
	public Coordinates getCoordinates() {
		return coordinates;
	}
	/**
	 * Set the coordinates
	 * @param coordinates
	 * @throws BadCoordinatesException
	 */
	private void setCoordinates(Coordinates coordinates) throws BadCoordinatesException {
		logger.trace("GCubePackage setCoordinates method");
		this.coordinates = coordinates;
		logger.debug("GCubePackage Coordinates ok");
	}

	/**
	 * query to IS maven coordinates, if not found, try to convert in gCube coordinate follow specific description
	 * @param gcubeC
	 * @return
	 * @throws BadCoordinatesException
	 */
	private MavenCoordinates getMavenCoordinates()
			throws BadCoordinatesException {
		logger.trace("getMavenCoordinates method: with maven Coordinates: g "+coordinates.getGroupId()+" a "+coordinates.getArtifactId()+" v "+coordinates.getVersion());
   // try to get coordinates from IS
		MavenCoordinates mavenC=null;		
		if((coordinates.getGroupId()== null) ||(coordinates.getArtifactId()==null) || (coordinates.getVersion()==null)){
			try{	
				mavenC = is.getMavenCoordinates(coordinates);
			}catch(ISException e){
				logger.error("IS EXCEPTION CATCHED");
			}
	   //if the is return is null, then converts coordinates from gCubeCoordinates to MavenCoordinates		
			if(mavenC==null){
				logger.debug(" no maven coordinates founded in profile. Try to convert from gCube to Maven");
				try{
					mavenC = (MavenCoordinates)coordinates.convert();
				}catch(BadCoordinatesException e){
					throw new BadCoordinatesException(" convert from gcube to maven");
				}
			}else{
				logger.debug("founded maven Coordinates in profile: gid: "+mavenC.getGroupId()+" aid: "+mavenC.getArtifactId()+" v: "+mavenC.getVersion());
			}
		}else{
			logger.debug("maven coordinates already setted ");
			mavenC=new MavenCoordinates(coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getVersion());
		}
		return mavenC;
	}

	
	/**
	 * query to IS the SA maven coordinates, if not found, try to convert in gCube coordinate follow specific description
	 * @param gcubeC
	 * @return
	 * @throws BadCoordinatesException
	 */
	private MavenCoordinates getSAMavenCoordinates()
			throws BadCoordinatesException {
		logger.trace("getMavenCoordinates method: with maven Coordinates: g "+coordinates.getGroupId()+" a "+coordinates.getArtifactId()+" v "+coordinates.getVersion());
   // try to get coordinates from IS
		MavenCoordinates mavenC=null;	
		if((coordinates.getGroupId()== null) ||(coordinates.getArtifactId()==null) || (coordinates.getVersion()==null)){
			try{	
				mavenC = is.getSAMavenCoordinates(coordinates);
			}catch(ISException e){
				logger.error("IS EXCEPTION CATCHED");
			}
	   //if the is return is null, then converts coordinates from gCubeCoordinates to MavenCoordinates		
			if(mavenC==null){
				logger.debug(" no maven coordinates founded in profile. Try to convert from gCube to Maven");
				try{
					mavenC = (MavenCoordinates)coordinates.convert();
				}catch(BadCoordinatesException e){
					throw new BadCoordinatesException(" convert from gcube to maven");
				}
			}else{
				logger.debug("founded maven Coordinates in profile: gid: "+mavenC.getGroupId()+" aid: "+mavenC.getArtifactId()+" v: "+mavenC.getVersion());
			}
		}else{
			logger.debug("maven coordinates already setted ");
			mavenC=new MavenCoordinates(coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getVersion());
		}
		return mavenC;
	}

	
	/**
	 * Extract dependencies from maven repositories
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceVersion
	 * @param packageName
	 * @param packageVersion
	 * @throws Exception 
	 * @throws BadCoordinatesException
	 */
	private String findDeps()
			throws ServiceNotAvaiableFault {
		logger.trace("findDeps method");
		String url=null;
		List<MavenCoordinates> mavenList = convertGcubeToMaven();
		logger.info("founded "+mavenList.size()+" packages ");
		// Call to RepositoryManager with maven coordinates
		logger.debug("try to retrieve Repository Manager server list");
		RepositoryManagerFactory rmf= new RepositoryManagerFactory();
		try{
			rm= rmf.getRepositoryManager(is.getMavenServerListFromRR(scope), true);
		}catch(Exception e){
			logger.error("Unable to retrieve Repository Manager server list for " + this.coordinates.getServiceName(), e);
			throw new ServiceNotAvaiableFault("Unable to retrieve Repository Manager server list for " + this.coordinates.getServiceName());
		}
		String result=null;
		String resultMerged=null;
		ArrayList<String> resultList=new ArrayList<String>();
		for(MavenCoordinates mavenC: mavenList){
			try{
				logger.debug(" try to retrieve pom's url for coordinates gid= "+mavenC.getGroupId()+" aid: "+mavenC.getArtifactId()+" v: "+mavenC.getVersion());
				url=rm.get(mavenC, "pom", null);
				logger.debug(" pom url is : "+url);
			}catch(Exception e){
				logger.info("url is null for package "+mavenC.getGroupId()+" "+mavenC.getArtifactId()+" "+mavenC.getVersion());
				break;
			}
			logger.debug("pom file founded, try to extract Dependencies...");
			result=rm.extractDepsFromMavenEmb(url.toString());
			resultList.add(result);
		}
		resultMerged=mergeResult(resultList);
		return resultMerged.toString();
	}

	
	private String mergeResult(ArrayList<String> resultList) {
		logger.trace("mergeResult method");
		StringBuffer resolvedDep=new StringBuffer();
		StringBuffer missingDep=new StringBuffer();
		for(String result : resultList){
			try{
				logger.debug("process: \n"+result);
				if((result.contains("<ResolvedDependencies>")) && (result.contains("</ResolvedDependencies>"))){
					String temp=result.substring(result.lastIndexOf("<ResolvedDependencies>"), result.lastIndexOf("</ResolvedDependencies>"));
					temp=temp.substring(22);
					logger.debug("resolved deps:\n"+temp);
					resolvedDep.append(temp);
				}
				if((result.contains("<MissingDependencies>")) && (result.contains("</MissingDependencies>"))){
					String temp2=result.substring(result.lastIndexOf("<MissingDependencies>"), result.lastIndexOf("</MissingDependencies>"));
					temp2=temp2.substring(21);
					logger.debug("missing deps:\n"+temp2);
					missingDep.append(temp2);
				}
			}catch(Exception e){
				logger.error("exception: "+e.getMessage());
				e.printStackTrace();
			}
		}
		String result="<DependencyResolutionReport>\n\t<ResolvedDependencies>\n"+resolvedDep.toString()+"</ResolvedDependencies>\n"+"<MissingDependencies>\n"+missingDep.toString()+"</MissingDependencies>\n</DependencyResolutionReport>";
		return result;
	}

	/**
	 * Converts GCube coordinate to maven coordinates
	 * @return
	 * @throws BadCoordinatesException
	 */
	private List<MavenCoordinates> convertGcubeToMaven() throws BadCoordinatesException{
		logger.trace("convertGcubeToMaven method");
		// retrieve maven coordinates
		List<MavenCoordinates> mavenList=new ArrayList<MavenCoordinates>();
		if((coordinates.getPackageName() == null) && (coordinates.getPackageVersion()==null)){
			// then we want the packages coordinates
			logger.debug("Package coordinates are null. Try to extract all package coordinates");
			List<org.gcube.common.core.resources.service.Package> packs=null;
			try {
				packs = is.queryPackagesCoordinates(coordinates.getServiceName(), coordinates.getServiceClass(), coordinates.getServiceVersion());
			} catch (ISException e) {
				e.printStackTrace();
			}
			for(org.gcube.common.core.resources.service.Package p : packs) {				
				logger.debug("processing pack: "+p.getName()+" "+p.getVersion());
				try{
					String gId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.groupId);
					String aId=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.artifactId);
					String v=p.getMavenCoordinate(org.gcube.common.core.resources.service.Package.MavenCoordinate.version);
					MavenCoordinates mc=new MavenCoordinates(gId, aId, v);
					logger.debug("package added: "+gId+" "+aId+" "+v);
					mavenList.add(mc);
				}catch(BadCoordinatesException e){
					logger.warn("package  not added, bad coordinate founded ");
				}
			}
		}else{
	//else we want the artifact coordinates
			MavenCoordinates mavenC = getMavenCoordinates();
			logger.info("Maven Coordinates founded are: gid: "+mavenC.getGroupId()+" aid "+mavenC.getArtifactId()+" v "+mavenC.getVersion());
			mavenList.add(mavenC);
		}
		return mavenList;
	}
}
