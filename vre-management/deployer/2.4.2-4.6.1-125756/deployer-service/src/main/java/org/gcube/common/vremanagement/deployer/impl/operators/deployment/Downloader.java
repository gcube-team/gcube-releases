package org.gcube.common.vremanagement.deployer.impl.operators.deployment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.vremanagement.softwaregateway.stubs.AccessPortType;
import org.gcube.vremanagement.softwaregateway.stubs.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.SACoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.service.AccessServiceAddressingLocator;

/**
 * Download manager for gCube Packages
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class Downloader {

	/** Local Ant runner	 */ 
	protected AntRunner run;
	
	/**  local logger */		
	protected final GCUBELog  logger = new GCUBELog(Downloader.class);
	
	/**
	 * Name of the package to download
	 */ 
	public String packagename = "";
	
	/**
	 * Version of the package to download
	 */ 
	protected String packageVersion = "";
	
	/**
	 * File name of the package downloaded  from the Repository service
	 */ 
	//public String packagefile;
	
	public File downloadedFile;
	
	/**
	 * Service ID of the package's service
	 */  
	protected String serviceID;
	
	/**
	 * Service name of the package's service
	 */  
	protected String serviceName = "";

	/**
	 * Service key of the package's service
	 */  
	protected String serviceKey = "";
	
	/** Target port-type name. */
	protected static final String REPOSITORY_ENDPOINT = "gcube/vremanagement/softwaregateway/Access";
	/** Target service name. */
	protected static final String REPOSITORY_NAME = "SoftwareGateway";
	/** Target service class. */
	protected static final String REPOSITORY_CLASS = "VREManagement";
	/**
	 * Service class of the package's service
	 */  
	protected String serviceClass = "";
	
	/**
	 * Service version of the package's service
	 */  
	protected String serviceVersion = "";
	
	/**
	 * The folder where the package tarball is uncompressed
	 */ 
	protected String packagedir;
	
	/**
	 * The folder where the package files are located
	 */
	private String packageFilesDir;
		
	protected boolean update = false;
	
	public static enum PackageType {JAR, SERVICEARCHIVE, UNKNOWN}
	
	protected PackageType packagetype = PackageType.UNKNOWN;
	
	/**
	 * 
	 * @param serviceID the service ID of the package
	 * @param packageName the name of the package
	 * @param update it states if the package is going to be updated or not
	 * @throws DeployException 
	 */
	public Downloader(PackageInfo deployable_package, boolean ... update) throws DeployException, Exception {
		this.packagename = deployable_package.getName();
		this.serviceKey = deployable_package.getServiceClass() + "-" + deployable_package.getServiceName() + "-" +deployable_package.getServiceVersion();
		//this.packagefile = this.packagename; 
		this.packagedir = Configuration.BASEDEPLOYDIR + File.separator + this.serviceKey;
		this.packageFilesDir = this.packagedir + File.separator + this.packagename;
		this.serviceClass = deployable_package.getServiceClass();
		this.serviceName = deployable_package.getServiceName();
		this.serviceVersion =  deployable_package.getServiceVersion();
		this.packageVersion =  deployable_package.getVersion();
				
		//remove the old files if it is an update
		if ((update==null || update.length==0)) 
			this.update = false;		
		else if (update[0]) {
			this.update = true;
			this.removeFiles();
		}	
		this.initAntContext();
	}
	
	private void initAntContext() throws Exception {
		//initialise the ANT context
		try {					
			this.run = new AntRunner();			
			this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
			
		} catch (Exception e) {
			logger.error("Package deployer is unable to initialize the deployment environment for " + this.packagename, e);
			throw new Exception("Package deployer is unable to initialize the deployment environment for " + this.packagename);
		}		
	}
	
	/**
	 * Downloads the package tarball. It tries to download following this priority list:
	 * <ul>
	 * <li> the given scope
	 * <li> the GHN default scope 
	 * <li> all the GHN scopes
	 * </ul> 
	 * 
	 * @param scope the scope where to look for the package
	 * @return the full path of the downloaded file
	 * @throws VOException if an error occurs in the download operation 
	 */
	public String downloadPackage( GCUBEScope ... scope)  throws UnreachablePackageException, Exception {
	
		logger.trace("downloading the package " + this.packagename + "... ");
		
		//check if we have to use the passed Scope
		if ((scope != null) && (scope.length > 0))	{			
			if (scope[0].getType() == GCUBEScope.Type.VRE)
				scope[0] = scope[0].getEnclosingScope();			
			try {
				return this.downloadPackageFromScope(scope[0]);					
			} catch (UnreachablePackageException voe) {
				logger.warn("Unable to download the package from the Scope " + scope[0]);
				if ( (scope[0].getType() == GCUBEScope.Type.VO) || (scope[0].getType() == GCUBEScope.Type.VRE)) {					
					// try with the parent VO					
					return this.downloadPackageFromScope(scope[0].getEnclosingScope());
				}
			}		
		}
		
		// try to download from the default scope
		for (GCUBEScope instancescope: ServiceContext.getContext().getInstance().getScopes().values())
			try {			
				return this.downloadPackageFromScope(instancescope);
			} catch (Exception voe) {		
				logger.warn("Unable to download the package from the scope " + instancescope);
				// try from all the other GHN scopes
				for (GCUBEScope ghnscope : GHNContext.getContext().getGHN().getScopes().values()) {
					try {
						return this.downloadPackageFromScope(ghnscope);
					} catch (Exception upe) {logger.warn("Unable to download the package from the Scope " + ghnscope);}
				}
			}
		//no hope
		throw new UnreachablePackageException();
		
	}
	
	/**
	 * Downloads the package from a local path
	 * @param file the tarball on the local file system
	 * @return the full path of the downloaded file
	 * @throws InvalidPackageArchiveException
	 * @throws IOException 
	 */
	public String downloadPackage(File file) throws DeployException, IOException, InvalidPackageArchiveException {
		String filePathName = Configuration.BASESOURCEDIR + File.separator	+ this.packagename;
		if (!file.exists())
			throw new IOException("The package cannot be downloaded, the local path " + file.getCanonicalPath() + " does not exist");
		//copy the file from file to Configuration.BASEPATCHDIR + File.separator
		logger.trace("Copying " + file.getCanonicalPath() + " to " + filePathName);
		downloadedFile = new File(filePathName);
		copy (file, downloadedFile);

		return filePathName;
	}
	
	private void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);
	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
	    in.close();
	    out.close();
	}
	
	/**
	 * Downloads the patch
	 * @param uri the uri from which the patch has to be downloaded
	 * @return the full path of the downloaded file
	 * @throws UnreachablePackageException it the download fails
	 * @throws InvalidPackageArchiveException 
	 * @throws DeployException 
	 */
	public String downloadPatch(URI uri) throws UnreachablePackageException, DeployException, InvalidPackageArchiveException {
		return this.internalHTTPdownload(Configuration.BASEPATCHDIR, uri);
	}
	
	/**
	 * Downloads the package
	 * @param uri the uri from which the package has to be downloaded
	 * @return the full path of the downloaded file
	 * @throws UnreachablePackageException it the download fails
	 * @throws InvalidPackageArchiveException 
	 * @throws DeployException 
	 */
	public String downloadPackage(URI uri) throws UnreachablePackageException, DeployException, InvalidPackageArchiveException {
		return this.internalHTTPdownload(Configuration.BASESOURCEDIR, uri);
	}
	
	protected String internalHTTPdownload(String baseDir, URI uri) throws UnreachablePackageException, DeployException, InvalidPackageArchiveException {
		logger.trace("Downloading the package " + this.packagename + "... ");
		String filePathName = baseDir + File.separator	+ this.packagename;
		DataInputStream is = null;
		int code = -999;
		try {			
			HttpURLConnection urlC = (HttpURLConnection) new URL(uri.toString()).openConnection();
			urlC.setRequestMethod("GET");
			urlC.setDoInput(true);
			urlC.setDoOutput(true);
			urlC.setDoOutput(true);
			urlC.setUseCaches(false);
			urlC.setInstanceFollowRedirects(true);
			logger.trace("Getting data from " + uri.toString() + "...");
			is = new DataInputStream(urlC.getInputStream());
			code = urlC.getResponseCode();		
		} catch (Exception e) {
			logger.error("", e);
			throw new UnreachablePackageException();
		}
		DataOutputStream o = null;
		try {
			logger.trace("Connection return code: " + code);
			logger.trace("Bytes available out: " + is.available());
			logger.trace("Open URL connection and saving to local cache..");
			o = new DataOutputStream(new FileOutputStream(filePathName));
			logger.trace("Saving package to = " + filePathName);
			while (true) {
				byte b = is.readByte();
				o.writeByte(b);
			}
		} catch (java.io.EOFException ee) {
			// nothing to log, the bytes to read are completed
		} catch (Exception e) {
			logger.error("Unable to download  the package from the URI " + uri, e);
			throw new UnreachablePackageException();
		} finally {
			try {
				o.close();
			} catch (Exception e) {
			}
		}
		downloadedFile = new File(filePathName);
		return filePathName;
	}

	
	
	/**
	 * Downloads the tarball file from the Package Repository service of the given VO
	 * 
	 * @param fileName the file where to place the package once downloaded
	 * @param serviceID the service ID of the package
	 * @param packageName the name of the package
	 * @param voname the name of the VO in which to look for the Package Repository service
	 * @return the full path of the downloaded file
	 * @throws InvalidPackageArchiveException 
	 * @throws DeployException 
	 * @throws UnreachablePackageException 
	 */
	private String downloadPackageFromScope(GCUBEScope scope) throws UnreachablePackageException, DeployException, InvalidPackageArchiveException {

		logger.debug("The Deployer is trying to download the package from Scope " + scope + "...");
		String filePathName = Configuration.BASESOURCEDIR + File.separator	+ this.packagename;
		String packageURI = null;

		// download the package from the package repository service
		for (EndpointReferenceType epr : this.findInstances(scope)) {		
			AccessPortType pt;
			try {
				AccessServiceAddressingLocator locator = new AccessServiceAddressingLocator();
				pt = locator.getAccessPortTypePort(epr);
				pt = GCUBERemotePortTypeContext.getProxy(pt, scope, ServiceContext.getContext());
			} catch (Exception e) {
				logger.error("Unable to contact the Software Gateway service",e);
				throw new UnreachablePackageException();
			}
			StringBuilder messageAsString = new StringBuilder();
			messageAsString.append("downloading ");
			messageAsString.append("ServiceClass=").append(this.serviceClass);			
			messageAsString.append(", ServiceName=").append(this.serviceName);
			messageAsString.append(", ServiceVersion=").append(this.serviceVersion);			
			messageAsString.append(", PackageName=").append(this.packagename);
			messageAsString.append(", PackageVersion=").append(this.packageVersion);
			messageAsString.append(" from ").append(epr.toString());
			logger.debug(messageAsString);
			try {
				SACoordinates coordinates = new SACoordinates();
				coordinates.setPackageName(this.packagename);
				coordinates.setServiceClass(this.serviceClass);
				coordinates.setServiceName(this.serviceName);
				coordinates.setServiceVersion(this.serviceVersion);
				coordinates.setPackageVersion(this.packageVersion);		
				packageURI = pt.getSALocation(coordinates);		
				logger.debug("returned URL for SA = " + packageURI);
				if ((packageURI == null) || (packageURI.compareTo("") == 0))
					logger.error("Unable to retrieve the package '"+ this.packagename + "' from the SoftwareRepository service");
				else {
					break;
				}
				
			} catch (Exception e) {
				logger.warn("Unable to retrieve the SA from the Software Gateway service in scope " + scope,	e);
				logger.debug("Trying to directly access the package");
				try {
					//try to directly get the package
					PackageCoordinates coordinates = new PackageCoordinates();
					coordinates.setPackageName(this.packagename);
					coordinates.setServiceClass(this.serviceClass);
					coordinates.setServiceName(this.serviceName);
					coordinates.setServiceVersion(this.serviceVersion);
					coordinates.setPackageVersion(this.packageVersion);		
					packageURI = pt.getLocation(coordinates);	
					logger.debug("returned URL for package = " + packageURI);
				} catch (Exception e2) {
					logger.warn("Unable to retrieve the package from the Software Gateway service in scope " + scope,	e);
				}
				if ((packageURI == null) || (packageURI.compareTo("") == 0))
					logger.error("Unable to retrieve the package '"+ this.packagename + "' from the SoftwareRepository service");
				else
					break;
				throw new UnreachablePackageException();
			}
		}
		if (packageURI != null) {
			// download package
			DataInputStream is = null;
			HttpURLConnection urlC = null;
			int code = -999;
			try {
				urlC = (HttpURLConnection) new URL(packageURI).openConnection();
				urlC.setRequestMethod("GET");
				urlC.setDoInput(true);
				urlC.setDoOutput(true);
				urlC.setDoOutput(true);
				urlC.setUseCaches(false);
				urlC.setInstanceFollowRedirects(true);
				logger.trace("getting data from returned URL...");
				is = new DataInputStream(urlC.getInputStream());
				code = urlC.getResponseCode();
			} catch (java.net.MalformedURLException e) {
				logger.error("Malformed URL Exception ", e);
			} catch (java.io.FileNotFoundException e) {
				logger.error("File Not FoundException ", e);
			} catch (java.io.IOException e) {
				logger.error("IO Exception ", e);
			}
			DataOutputStream o = null;
			try {
				logger.trace("connection return code: " + code);
				logger.trace("bytes available out: "+ is.available());
				logger.trace("open URL connection and saving to local cache...");
				filePathName += this.getFileExtension(packageURI, urlC);
				File f = new File(filePathName);
				if (f.exists()) {
					f.delete();
				}
				o = new DataOutputStream(new FileOutputStream(filePathName));				
				logger.trace("Saving package to = " + filePathName);
				while (true) {
					byte b = is.readByte();
					o.writeByte(b);
				}
			} catch (java.io.EOFException ee) {
				// nothing to log, the bytes to read are completed
				logger.trace("Package successfully downloaded");
			} catch (Exception e) {
				logger.error("", e);
				throw new UnreachablePackageException();
			} finally {
				try {o.close();} catch (Exception e) {}
			}
		} else {
			logger.error("Unable to download the package from the Software Gateway service beloging the Scope " + scope + ", the service replies with a null value");
			throw new UnreachablePackageException();
		}
		downloadedFile = new File(filePathName);
		return filePathName;
	}
	

	/**
	 * Guesses the extension of the file to download
	 * @param packageURI
	 * @param urlC
	 * @return the extension
	 * @throws InvalidPackageArchiveException
	 */
	private String getFileExtension(String packageURI, HttpURLConnection urlC) throws InvalidPackageArchiveException {
		if ((packageURI.contains("e=tar.gz") 
			|| (packageURI.contains("e=tgz")))) {
			packagetype = PackageType.SERVICEARCHIVE;
			return ".tar.gz";
		} else if (packageURI.contains("e=jar")) {
			packagetype = PackageType.JAR;
			return ".jar";
		} else {
			//try to guess it
			String type = urlC.getContentType().trim();
			logger.trace("Dectected content type: " + type);
			if ((type.contentEquals("application/x-gzip")) 
				|| (type.contentEquals("application/gzip"))
				|| (type.contentEquals("application/x-tar-gz"))) {
				packagetype = PackageType.SERVICEARCHIVE;
				return ".tar.gz";
			} else if (type.contentEquals("application/java-archive")) {
				packagetype = PackageType.JAR;
				return ".jar";
			}
			throw new InvalidPackageArchiveException("Unrecognized package extension");
		}	
	}
	/**
	 * Removes the package files
	 * 
	 * @throws DeployException if the delete operation fails
	 */
	protected void removeFiles() throws DeployException{

		AntRunner local_run; 
		try {
			local_run = new AntRunner();			
			local_run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
			Map<String, String> properties = new HashMap<String, String>();										
			properties.put("folder", Configuration.BASEDEPLOYDIR + File.separator + this.serviceID + File.separator + this.packagename);
			try {				 			
				local_run.setProperties(properties, true);
				local_run.runTarget("deleteFolder");
			} catch (AntInterfaceException aie) {			
				throw new DeployException (aie.getMessage());
			}
		} catch (Exception e) {
			logger.error("Unable to remove the package files for " + this.packagename, e);
			throw new DeployException("Unable to remove the package files for " + this.packagename);
		}
	}
	
	
	/**
	 * Finds instances of the Software Repository service in the current scope(s)
	 * @return the list of scopes and the EPR of the available SR
	 * @throws Exception
	 */
	protected Set<EndpointReferenceType> findInstances(GCUBEScope scopeToCheckIn) {
		Set<EndpointReferenceType> endpoints = new HashSet<EndpointReferenceType>();		
		logger.debug("Looking for SR instances...");
		try {
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERIQuery lookupQuery = client.getQuery(GCUBERIQuery.class);
			lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceName",REPOSITORY_NAME));
			lookupQuery.addAtomicConditions(new AtomicCondition("//ServiceClass",REPOSITORY_CLASS));
			logger.debug("Quering in scope " + scopeToCheckIn);
			List<GCUBERunningInstance> list = client.execute(lookupQuery, scopeToCheckIn);
			logger.debug("Found N." + list.size() + " instances");
			for (GCUBERunningInstance instance : list) {				
				EndpointReferenceType epr = instance.getAccessPoint().getEndpoint(REPOSITORY_ENDPOINT);
				logger.trace("Found EPR " + epr.getAddress().toString());	
				endpoints.add(epr);												
			}		
		} catch (Exception e) {
			logger.error("Unable to query for SR's instances", e);
		}
		
		return endpoints;
	}

	public String getPackagename() {
		return packagename;
	}

	public String getPackagedir() {
		return packagedir;
	}

	public String getPackageFilesDir() {
		return packageFilesDir;
	}
	
	public String getServiceKey() {
		return serviceKey;
	}

	/**
	 * @return the packagetype
	 */
	public PackageType getPackagetype() {
		return packagetype;
	}

	/** 
	 *  
	 * @return true if the operation is an update, false if it is a new deployment
	 */
	public boolean isUpdate() {
		return update;
	}

	/** Unreachable package exception*/
	public static class UnreachablePackageException extends Exception {	private static final long serialVersionUID = 1324255717533638292L;}

}
