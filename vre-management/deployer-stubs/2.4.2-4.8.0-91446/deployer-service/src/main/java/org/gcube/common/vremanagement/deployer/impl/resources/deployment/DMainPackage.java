package org.gcube.common.vremanagement.deployer.impl.resources.deployment;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * An extension of {@link DeployablePackage} which specializes in main package deployment
 *
 * @author Manuele Simi (CNR-ISTI)
 *
 */
public class DMainPackage extends DeployablePackage {
 	
	private static final long serialVersionUID = -3346696597160674566L;

	protected transient MainPackage packageprofile;
	
	protected String garID;
	
	protected String garFile;
	
	protected String garname;
		
	private static String JNDIFileName = "jndi-config.xml";			
	
	
	/**
	 * Creates a new main deployable package
	 * 
	 * @param packageprofile the package profile
	 * @param downloader the manager used to download the package from the Software Repository service
	 * @throws Exception if the GAR file is not found or valid
	 */
	public DMainPackage(MainPackage packageprofile,PackageExtractor extractor) throws Exception {
		super(packageprofile,extractor);
		this.packageprofile = packageprofile;
		this.analysePackage();		
		this.garID = this.generateID();
		this.setProperty("gar.name", this.garID);//will be used for undeployment purposes
		this.setType(TYPE.MAINPACKAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deployPackage(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException {
		boolean available = false;
		boolean samepackage = false;

		logger.debug("Deploying the main package " + this.getKey().getPackageName() + " in scope(s) " + targets.toString() );
		if (!this.extractor.isUpdate()) {
			//checks if the RI is already registered in the GHNcontext			
				try {
					GCUBEServiceContext context = GHNContext.getContext().getServiceContext(this.getKey().getServiceClass(), this.getKey().getServiceName());
					logger.trace("Installed RI version is " + context.getInstance().getInstanceVersion());
					logger.trace("Package version is " + this.packageprofile.getVersion());
					if (context.getInstance().getInstanceVersion().compareTo(this.packageprofile.getVersion())== 0) 
						samepackage = true;
					available = true;
				} catch (Exception e) {} finally {
					if (samepackage) {
						logger.warn("unable to deploy " + this.toString() + ": an instance of the service is already deployed on this node");
						throw new PackageAldreadyDeployedException("an instance of the service is already deployed on this node");
					} else if (available) {
						logger.warn("a different version of an instance of " + this.toString() + " is already deployed on this node");
						throw new DeployException("a different version of an instance of the service is already deployed on this node");
			
					}
				}							
		}
		this._deploy();				
		//mark the scope to add after the restart
		try {
			this.setStartScopes(targets);
		} catch (Exception e) {
			logger.error("Could not set start scopes for the package " + e.getMessage());
			throw new DeployException(e);
		}
		//mark the scope to add after the restart
		//this.setTargetsToAdd(targets);
		this.setScopesToAdd(targets);
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return true;
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
	public boolean verify() throws InvalidPackageArchiveException {
		// check if the etc/gar folder has been created and it's not empty
		File targetFolder = new File(getPackageTargetFolder());
		if (!targetFolder.exists())
			return false;
		if ((targetFolder.listFiles() == null) || (targetFolder.listFiles().length == 0))
			return false;		
		return true;
	}
	
	/**
	 * Generates the ID used when deploying the GAR
	 * 
	 * @return the GAR ID
	 * @throws InvalidPackageArchiveException if the GAR name specified in the service profile
	 * is empty
	 */
	private String generateID() throws InvalidPackageArchiveException {
		String[] pathTokes = this.packageprofile.getGarArchive().split(File.separator);
		String temp[] = pathTokes[(pathTokes.length -1)].split(".gar");
		if ((temp == null) || (temp.length < 1))
			 throw new InvalidPackageArchiveException("Invalid GAR name " + this.garname);
		logger.trace("Using the GAR ID: " + temp[0]);
		return temp[0];
	}	
	

	private void _deploy() throws InvalidPackageArchiveException, DeployException {

		if (this.extractor == null)
			throw new DeployException("invalid download manager");
		this.preDeploy();
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("gar.name", this.packageprofile.getGarArchive().trim());
		properties.put("gar.id", this.garID);
		properties.put("package.name", this.getKey().getPackageName());
		properties.put("service.id", this.extractor.getServiceKey());
		properties.put("package.file", this.extractor.getDownloadedFile().getName());
		properties.put("package.source.dir",Configuration.BASESOURCEDIR);
		properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR);
		try {
			this.run.setProperties(properties, true);
			this.run.runTarget("deployWSRFService");
		} catch (AntInterfaceException aie) {			
			throw new DeployException ("Runtime deployment error: " + aie.getMessage());
		}				
		
		//this.postDeploy();

	}

	/**
	 * Adds the <em>startScopes</em> information (if any) in the JNDI file of the the newly deployed MainPackage. As results, when the new instance is up, it will
	 * run in such scopes
	 * 
	 * @param scopes the list of scopes to add
	 * @throws Exception
	 */
	private void setStartScopes(Set<GCUBEScope> scopes) throws Exception {
		
		if ((scopes == null) || (scopes.size() == 0)) 
			return;
		
		//we need to clone the input scopes, since we are going to consume the list
		List<GCUBEScope> clonedScopes = new ArrayList<GCUBEScope>();
		for (GCUBEScope toclone : scopes) clonedScopes.add(toclone);
		
		//build the absolute path on the JNDI file in the service folder
		String JNDIFilePath = this.getPackageTargetFolder() + File.separator + JNDIFileName;
		logger.trace("Compiling JNDI at " + JNDIFilePath);
		File JNDIFile = new File(JNDIFilePath);
		if (!JNDIFile.exists()) 
		      throw new FileNotFoundException ("File does not exist: " + JNDIFile);
		   
		if (!JNDIFile.isFile())
		      throw new IllegalArgumentException("Should not be a directory: " + JNDIFile);
		   
		if (!JNDIFile.canWrite()) 
		      throw new IllegalArgumentException("File cannot be written: " + JNDIFile);
		    
		StringBuilder startScopesAttributeValue = new StringBuilder();		
		startScopesAttributeValue.append(clonedScopes.remove(0));
		for (GCUBEScope scope : clonedScopes) startScopesAttributeValue.append("," + scope);
		logger.trace("Adding scope(s) " + startScopesAttributeValue.toString()	+ " to package: " + this.getKey().toString());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance () ;
		DocumentBuilder parser = factory.newDocumentBuilder ( ) ; 
		Document document = parser.parse (new File(JNDIFilePath)) ;
		NodeList services = document.getElementsByTagName ( "service" ) ; 
		//since we do not know which service's element is the root one, we append the startScopes element to all of them
		//the non-root ones will just ignore it
		for ( int i = 0; i  <  services.getLength(); i++ )   {
			 logger.trace("Adding scope to service element");
			 Element service =  ( Element ) services.item (i) ;
			 Element envElement = document.createElement("environment");
			 envElement.setAttribute("name", "startScopes");
			 envElement.setAttribute("value", startScopesAttributeValue.toString());
			 envElement.setAttribute("type", "java.lang.String");
			 envElement.setAttribute("override", "false");
			 service.appendChild(envElement);
		}
		
		//Output the XML

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(document);
        trans.transform(source, result);
        this.setJNDIContents(JNDIFile, sw.toString());

	}
	 
	
	 /**
	  * Change the contents of JNDI file in its entirety, overwriting any
	  * existing text.	  
	  *
	  * @param JNDIFile the file which can be written to.
	  * @param content the new content
	  * @throws IllegalArgumentException if param does not comply.
	  * @throws FileNotFoundException if the file does not exist.
	  * @throws IOException if problem encountered during write.
	  */
	  private void setJNDIContents(File JNDIFile, String content)
	                                 throws FileNotFoundException, IOException {
	    if (JNDIFile == null) {
	      throw new IllegalArgumentException("File should not be null.");
	    }	    

	    //use buffering
	    java.io.Writer output = new BufferedWriter(new FileWriter(JNDIFile));
	    try {
	      output.write( content );
	    }
	    finally {
	      output.close();
	    }
	  }
		
	  /**
	   * 
	   * @return
	   */
	  private String getPackageTargetFolder() {
		  return GHNContext.getContext().getLocation() + File.separator +"etc" + File.separator + this.garID;
	  }
}
