package org.gcube.common.core.contexts;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;

import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.utils.XMLUtils;
import org.gcube.common.core.contexts.ghn.Builder;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource.InvalidScopeException;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.security.context.impl.DefaultGHNClientSecurityContext;
import org.xml.sax.InputSource;

/**
 * Adapts {@link GHNContext} to the case in which the gHN is not actually running.
 * @author Fabio Simeoni (University of Strathclyde)
 **/
public class GHNClientContext extends GHNContext 
{

	/** In-memory configuration.*/
	private static Map<String, Object> aux_configuration;
	
	private String etcConfigurationPath;
	/**Server WSDD File*/
	public static final String SERVER_WSDD_FILE = "server-config.wsdd";
	
	private WSDDDocument serverWSDDDocument = null;
	
	/**{@inheritDoc}*/
	protected void initialise() throws Exception {
		try {
			this.etcConfigurationPath = this.getLocation()+File.separatorChar+"etc"+File.separatorChar+"globus_wsrf_core";
			this.serverWSDDDocument = loadServerWsddDocument();
			File clientFile = singleton.getFile(GHN_CLIENT_JNDI_RESOURCE);
			configureGHN(clientFile.exists()?clientFile:getFile(GHN_JNDI_RESOURCE));
			configureGHNResource();		
			initializeSecurity ();
			
		}//cannot let this up, or the process will be killed..not suitable here
		catch(Throwable e){singleton.logger.fatal("gHN could not complete initialisation",e);}
	}
	
	private void initializeSecurity ()
	{
		SecurityContextFactory.getInstance().setSecurityContext(new DefaultGHNClientSecurityContext());
	}
	
	/**{@inheritDoc}*/
	protected void configureGHN(File file) throws Exception {
		super.configureGHN(file);
		//retain configuration in local map to avoid JNDI problem with other containers (e.g. tomcat) 
		aux_configuration = new HashMap<String, Object>();
		NamingEnumeration<Binding> bindings = singleton.getJNDIContext().listBindings("/");
		while (bindings.hasMoreElements()) {
			Binding binding = bindings.nextElement();
			aux_configuration.put(binding.getName(),binding.getObject());
		}	
	}
	
	/**{@inheritDoc}*/
	protected void configureGHNResource() throws Exception {
		node = GHNContext.getImplementation(GCUBEHostingNode.class);
		node.setLogger(logger);
		Builder.createGHNResource(this);
		if (node.addScope(this.getStartScopes()).size()==0) throw new InvalidScopeException();
	}
	
	/** {@inheritDoc} */
	@Override public Object getProperty(String prop, boolean... required)	throws RuntimeException {
		if (aux_configuration==null) return super.getProperty(prop, required);
		Object value = aux_configuration.get(prop);
		if (value == null && required!=null && required.length>0 && required[0])
			throw new RuntimeException("configuration property '"+prop+"' does not exist");
		return value;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private WSDDDocument loadServerWsddDocument () throws Exception
	{
		logger.debug("Getting Wsdd Document");
		String fileName = this.etcConfigurationPath+File.separatorChar+SERVER_WSDD_FILE;
		logger.debug("File = "+fileName);
		WSDDDocument response =  new WSDDDocument(XMLUtils.newDocument(new InputSource(new FileInputStream(fileName))));
		logger.debug("Document loaded");
		return response;
	}
	
	public WSDDDocument getServerWsddDocument ()
	{
		return this.serverWSDDDocument;
	}
	
}
