package org.gcube.common.core.contexts;


import java.io.File;

import javax.xml.namespace.QName;

import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.utils.XMLUtils;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.security.utils.DefaultSecurityDescriptorBuilder;
import org.globus.wsrf.Constants;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityConfig;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.globus.wsrf.utils.AddressingUtils;
import org.xml.sax.InputSource;

/**
 * Partial implementation of contexts for port-types.
 * <p>   
 *  
 *  If useful, the creation of a port-type context may be cached, e.g.
 *  using the pattern illustrated for {@link GCUBEServiceContext}.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBEPortTypeContext extends GCUBEContext {
	
	/** The name of the deployment descriptor. */
	protected static String WSDD_FILE_NAME="deploy-server.wsdd";
	/** The name of the security descriptor parameter in the deployment descriptor.*/
	protected static String WSSD_WSDD_NAME="securityDescriptor";
	
	/** The name of the port-type.*/
	protected String name;
	
	/** The deployment descriptor of the port-type.*/
    protected WSDDService deploymentDescriptor;
    
    /** The security descriptor of the port-type.*/
    protected ServiceSecurityDescriptor securityDescriptor;
    
	/**
	 * A {@link Consumer} for port-type registration to RI scope removal events.
	 * @author Fabio Simeoni (University of Strathclyde)
	 **/
	protected class PTConsumer extends Consumer {

		/**Creates a new instance.*/
		PTConsumer() {this.consumerLogger=logger;}//contextualise generic superclass logger
		/**{@inheritDoc}*/
		@Override protected void onRIInitialised(RILifetimeEvent event) throws Exception {
			  super.onRIInitialised(event);
			  logger.setContext(getServiceContext());
			  //logger.trace("INITIALISING CONTEXT OF PORTTYPE "+getName().toUpperCase());
			  onInitialisation();
		}
		/**{@inheritDoc} */ 
		@Override protected void onRIReady(RILifetimeEvent event) throws Exception {super.onRIReady(event);onReady();}
		/**{@inheritDoc} */
		@Override protected void onRIStateChange(RILifetimeEvent event) throws Exception {super.onRIStateChange(event);onStateChange();}
		/**{@inheritDoc} */
		@Override protected void onRIUpdated(RILifetimeEvent event) throws Exception {super.onRIUpdated(event);onUpdate();}
		/**{@inheritDoc}*/
		@Override protected void onRIFailed(RILifetimeEvent event) throws Exception {super.onRIFailed(event);onFailure();}
	}
    
    public GCUBEPortTypeContext() {
    	if (this.getServiceContext().getStatus()==GCUBEServiceContext.Status.FAILED) return;//any point continuing?
		try {this.getServiceContext().subscribeLifetTime(new PTConsumer());} //register RI consumer
		catch (Exception e) {//..or die trying
			logger.fatal("could not register porttype for RI lifetime event",e);
			getServiceContext().setStatus(GCUBEServiceContext.Status.FAILED);
		}
    }
    
	/**Returns an (unqualified) EPR to the port-type.<br>
	 * 
	 * @return the EPR.
	 * @throws Exception if the EPR could not be derived.
	 */
	public EndpointReferenceType getEPR() throws Exception {
		return AddressingUtils.createEndpointReference(GHNContext.getContext().getBaseURL()+this.getJNDIName(),null);
			
	}


	 /**
	 * Returns a name for the port-type to use in log entries.
	 * By default, it derives a name from the JNDI name of the port-type.
	 * @return the name.
	 * @see #getJNDIName()
	 */	
	public synchronized String getName() {
		if (name==null) name = new File(this.getJNDIName()).getName();//small hack	
		return name;
	}

	/** {@inheritDoc} */
	public Object getProperty(String prop, boolean ...required) throws RuntimeException {
		return super.getProperty(Constants.JNDI_SERVICES_BASE_NAME+this.getJNDIName()+"/"+prop, required);
	}
	
	/** Returns the deployment descriptor of the port-type.
    * @return the deployment descriptor.*/
    public WSDDService getDeploymentDescriptor() {
    	
    	if (this.deploymentDescriptor==null) {//load descriptor lazily
    		try {
    			WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(new InputSource(this.getFile(WSDD_FILE_NAME).getAbsolutePath())));
    		    this.deploymentDescriptor = doc.getDeployment().getWSDDService(new QName(null,this.getJNDIName()));
    		}catch(Exception e){logger.error("could not load deployment descriptor",e);}
    	}
    	return this.deploymentDescriptor;
    	
    }
    
    /** Returns the security descriptor of the port-type.
     * @return the security descriptor.*/
     public ServiceSecurityDescriptor getSecurityDescriptor() {
     	
    	 logger.debug("Getting the security descriptor");
    	 
     	if (this.securityDescriptor==null) {//load descriptor lazily
     		try {
     			
     			logger.debug("Triyng to load the security descriptor...");
     			
     			GCUBEDefaultSecurityConfiguration securityConfiguration = SecurityContextFactory.getInstance().getSecurityContext().getDefaultServiceSecurityConfiguration();
     			String securityDescriptorFileName=this.deploymentDescriptor.getParameter(WSSD_WSDD_NAME);
     			
     			if ((securityConfiguration!= null && securityConfiguration.isInEnabled() && securityConfiguration.isInOverride()) || (securityConfiguration != null  && securityConfiguration.isInEnabled() && securityDescriptorFileName == null))
     			{
     				logger.debug("Ingoing override = "+securityConfiguration.isInOverride());
     				logger.debug("sec desc file = "+securityDescriptorFileName);
 					logger.debug("Loading default ingoing security descriptor");
 					this.securityDescriptor = SecurityContextFactory.getInstance().getSecurityContext().getDefaultIncomingMessagesSecurityDescriptor();
 					logger.debug("Security descriptor = "+this.securityDescriptor);
     			}
     			else if (securityDescriptorFileName != null) // && (builder==null || (builder!=null && !builder.inOverride()))
     			{
     				logger.debug("loading from the file");
         			this.securityDescriptor = new ServiceSecurityDescriptor();
      			    this.securityDescriptor.parse(ServiceSecurityConfig.loadSecurityDescriptor(securityDescriptorFileName).getDocumentElement());
     			}
     			else // if (securityDescriptorFileName == null && builder==null)
     			{
 					logger.debug("No security descriptor for this port type");
 					return null; //no security descriptor for this port-type
     			}

     		}catch(Exception e){logger.error("could not load security descriptor",e);}
     	}
     	return this.securityDescriptor;
     	
     }
    
     /**{@inheritDoc} */
	public File getFile(String path, boolean ... writeMode) {
		return this.getServiceContext().getFile(path,writeMode);//delegates to service context which knows how to resolve it	
	}
	
	/**
	* Gives read or write access to a {@link java.io.File} that will persist across redployments of the Running Instance.
	* @param path the file path.
    * @param writeMode (optional) the access mode, <code>true</code> for write access and <code>false</code> for read access (default).
    * @return the file.
    * @throws IllegalArgumentException if access is in write mode and the path is to a folder.
    * @see GCUBEServiceContext#getPersistentFile(String, boolean...)
    */
	public File getPersistentFile(String path, boolean ... writeMode) throws IllegalArgumentException {
		return this.getServiceContext().getPersistentFile(this.getName()+File.separatorChar+path, writeMode);	
	}
	
	/**
	 * Return the name of the port-type in the JNDI configuration
	 * @return the JNDI name.
	 */
	public abstract String getJNDIName();
	
	/** Returns the namespace of the port-type.
	 *  @return the namespace.*/
	public abstract String getNamespace();
	/**
	 * Returns the name of the service of the port-type.
	 * @return the name.
	 */
	public abstract GCUBEServiceContext getServiceContext();
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////CALLBACKS ADAPTERS
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Invoked when the Running Instance has completed initialisation. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onInitialisation() throws Exception {}
	/** Invoked when the Running Instance is ready to operate. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/ 
	protected void onReady() throws Exception {} 
	/** Invoked when the Runnning Instance is updated. If needed, override in accordance with service semantics.
	* @throws Exception if the callback did not complete successfully.*/
	protected void onUpdate() throws Exception {}; 
	/** Invoked upon a change to the RI's stateful resources, if any. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onStateChange() throws Exception {};
	/** Invoked when the Running Instance fails. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully.*/
	protected void onFailure() throws Exception {} 
	
}