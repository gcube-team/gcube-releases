package org.gcube.common.vremanagement.deployer.impl.resources.deployment;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Software;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;

/**
 * An extension of {@link DeployablePackage} which specialises in library deployment
 *
 * @author Manuele Simi (CNR-ISTI)
 *
 */
class DLibraryPackage extends DeployablePackage {


	private static final long serialVersionUID = -4041391555933447325L;

	protected String baseLibTargetDir = GHNContext.getContext().getLocation() + File.separator + "lib" + File.separator;
	
	protected transient Software packageprofile;
	
	private transient List<String> jars = new ArrayList<String>();		
	
	public DLibraryPackage(Software packageprofile,PackageExtractor extractor) throws Exception {
		super(packageprofile,extractor);
		this.packageprofile = packageprofile;
		this.analysePackage();		
		this.setType(TYPE.LIBRARY);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deployPackage(Set<GCUBEScope> targets) throws DeployException, InvalidPackageArchiveException {
		logger.debug("Deploying the library package " + this.getKey().getPackageName() + " in scope(s) " + targets.toString() );
		URI uri = this.packageprofile.getURI();
		if (uri != null) 
			this.deployRemoteLibrary(uri);
		else  			
			this.deployLibrary(this.packageprofile.getFiles());						 					
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Package getPackageProfile() {		
		return this.packageprofile;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {		
		return true;
	}
		

	/**
	 * Moves the library files to the local GHN /lib folder
	 * 
	 * @param libraryfiles the list of files forming the library
	 * @throws InvalidPackageArchiveException if the Service profile is not valid
	 * @throws Exception if the deploy operation fails 
	 */
	private void deployLibrary(List<String> libraryfiles) throws DeployException, InvalidPackageArchiveException {
		
		//deploy a standard library included in the tar.gz and stored on the PR
		for (String file : libraryfiles) {
			try {					
				this.run = new AntRunner();			
				this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
				
			} catch (Exception e) {
				logger.error("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName(), e);
				throw new DeployException ("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName());
			}
			//this.buildJarNames(file);
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("service.id", this.extractor.getServiceKey());
			properties.put("package.name", this.getKey().getPackageName());
			properties.put("package.file", this.extractor.getDownloadedFile().getName());		
			properties.put("package.source.dir", Configuration.BASESOURCEDIR );
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR );
			properties.put("jar.name", file.trim());
			try {
				this.run.setProperties(properties, true);
				this.run.runTarget("deployLibrary");
			} catch (AntInterfaceException aie) {			
				throw new DeployException (aie.getMessage());
			}
			String[] filetokens = file.split(File.separator);
			this.addFile2Package(new File(baseLibTargetDir + filetokens[filetokens.length-1]));
		}
	}
	
	/**
	 * Downloads and uncompress the library and moves all its Jars to the GLOBUS_LOCATION/lib folder
	 * 
	 * @param URI the URI from which the library has to be downloaded
	 * @throws DeployException if the download operation fails 
	 */
	private void deployRemoteLibrary(URI uri) throws DeployException {
		try {
			String data[] = this.downloadFrom(uri.toURL().toString());
			//initialise the ANT project
			try {					
				this.run = new AntRunner();			
				this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
				
			} catch (Exception e) {
				logger.error("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName(), e);
				throw new DeployException ("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName());
			}
			//uncompress the library in the this.packageDir/this.packagename folder
			Map<String, String> prop = new HashMap<String, String>();			
			prop.put("library.file", data[0]);
			prop.put("base.deploy.dir", this.extractor.getPackagedir());
			prop.put("package.name", this.getKey().getPackageName());
			try {
				this.run.setProperties(prop, true);
				this.run.runTarget("uncompressRemoteLibrary");
			} catch (AntInterfaceException aie) {
				logger.error(aie);
				throw new DeployException (aie.getMessage());
			}
			List<String> myjars = this.getRemoteJarNames(this.extractor.getPackagedir() + File.separator + this.getKey().getPackageName());
			if (myjars.size() == 0)
				logger.warn("no JAR archive found in " + data[0]);
			this.buildRemoteJarNames(myjars);			
			for (String file : myjars) {				
				try {					
					this.run = new AntRunner();			
					this.run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
					
				} catch (Exception e) {
					logger.error("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName(), e);
					throw new DeployException ("Package deployer is unable to initialize the deployment environment for " + this.key.getPackageName());
				}
				
				//deploy
				logger.info("deploying " + file +" as part of an external library") ;
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("jar.name", file.trim());
				//properties.put("service.id", this.serviceID);				
				//properties.put("base.uncompressed.dir", this.packageDir + File.separator + this.packagename);
				try {
					this.run.setProperties(properties, true);
					this.run.runTarget("deployRemoteLibrary");
				} catch (AntInterfaceException aie) {
					logger.error(aie);
					throw new DeployException (aie.getMessage());
				}
				String[] filetokens = file.split(File.separator);
				this.addFile2Package(new File(baseLibTargetDir +filetokens[filetokens.length-1]));
			}
			
		} catch (Exception e) {
			throw new DeployException("Runtime deployment error: " + e.getMessage());
		}	
			
	}
	
	/**
	 * Downloads the external software library from the given URI
	 * 
	 * @return the full path of the downloaded file and the file name
	 * @throws Exception
	 */
	private String[] downloadFrom(String URI) throws Exception {
				
		String[] uri_tokens = URI.split(File.separator);
		if ((uri_tokens == null) || (uri_tokens.length < 1 ))
			throw new Exception("invalid URI from which to download the external library");
		String filename = uri_tokens[uri_tokens.length -1];
		String localpath = this.extractor.getPackagedir() + File.separator + filename; 
		logger.debug("Library Package deployer is trying to download from: " + URI);
		logger.debug("and save it here: " + localpath);
		
        DataInputStream is = null;
        int code = -999;
        try {
                URL url = new URL( URI );
                HttpURLConnection urlC = (HttpURLConnection) url.openConnection();
                urlC.setRequestMethod("GET");
                urlC.setDoInput(true);
                urlC.setDoOutput(true);
                urlC.setDoOutput(true);
                urlC.setUseCaches(false);

                logger.debug("Getting data from returned URL.." );
                is = new DataInputStream ( urlC.getInputStream() );
                code = urlC.getResponseCode();
        } catch ( java.net.MalformedURLException e) {
                logger.error ("Malformed URL Exception ", e);
        } catch ( java.io.FileNotFoundException e) {
                logger.error ("File Not FoundException ",  e);
        } catch ( java.io.IOException e) {
        	logger.error ("IO Exception ", e);
        }
        DataOutputStream o = null;
        try {
                logger.debug ("Connection return code: " + code);
                logger.debug ("Bytes available out:    " + is.available());
                logger.debug ("Open URL connection and saving to local cache.." );		                
                o = new DataOutputStream (new FileOutputStream( localpath ));
                logger.debug ("Saving package to = " + localpath );		                
                while (true){
                    byte b = is.readByte();
                    o.writeByte(b);
                }                       
        } catch (java.io.EOFException ee){
        	//nothing to log, the bytes to read are completed
        } catch (Exception e) {
        	logger.error ("", e );
        }                
        finally {
        	try { o.close(); } catch(Exception e) {}                	 
        }
	
        return new String[]{localpath, filename};
	}

	/**
	 * Extracts the names of a JAR archives from their absolute paths.
	 * It is used when the the deployment is related to an external library
	 * 
	 * @param localJars list of the absolute paths
	 * 
	 * @throws InvalidPackageArchiveException if the names of the JAR archives reported in the 
	 * Service profile are not valid
	 */
	private void buildRemoteJarNames(List<String> localJars) throws InvalidPackageArchiveException {		
		for (String jar : localJars) 
			this.buildJarNames(jar);		
	}

	/**
	 * Extracts the name of a JAR file from its absolute path 
	 * 
	 * @param jarname the absolute path of a JAR archive
	 * @throws InvalidPackageArchiveException if the names of the JAR archives reported in the 
	 * Service profile are not valid
	 */
	private void buildJarNames(String jarname) throws InvalidPackageArchiveException {		
		String[] my_jar_tokes = jarname.split(File.separator);
		if ((my_jar_tokes == null) || (my_jar_tokes.length < 1))
			throw new InvalidPackageArchiveException("invalid Jar name: " + jarname);
		this.jars.add(my_jar_tokes[my_jar_tokes.length - 1]);			
	}
	
	/**
	 * Scans in the given directory to search for a *.jar file
	 * 
	 * @param dir the folder to scan
	 * @return the list of full paths of the JAR files, if any
	 */
	private List<String> getRemoteJarNames(String dir) {
		
		List<String> ret = new ArrayList<String>();
		logger.debug("looking for JAR archives in " + dir + "...");
		File[] list = new File(dir).listFiles();
		if (list == null) {
			logger.warn("the " + dir +" does not contain any file or folder");
			return ret;
		}
		for (File f: list) {
			logger.debug("checking file " + f.getName() + "...");
			if (f.getName().endsWith(".jar") ) {
				logger.debug("External Library Deployer found " + f.getName() + " to deploy");
				ret.add(f.getAbsolutePath());				
			} else if (f.isDirectory()) {				
				ret.addAll(getRemoteJarNames(f.getAbsolutePath()));
			}
		}				
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {		
		for (File libFile : this.getPackageFileList()) {
			logger.warn("Verifying library file "+ libFile.getName() + "...");	
			if (!libFile.exists()) {
				try {
					logger.warn("Library file "+ libFile.getCanonicalPath() + " not correctly deployed");
				} catch (IOException e) {
					throw new InvalidPackageArchiveException("Unable to check library file " + libFile.getName());
				}
				return false;
			}
		}
		return true;
	}

}
