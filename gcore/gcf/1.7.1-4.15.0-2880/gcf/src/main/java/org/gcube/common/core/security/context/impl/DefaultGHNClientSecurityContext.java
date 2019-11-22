package org.gcube.common.core.security.context.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.gcube.common.core.contexts.GHNClientContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.security.GCUBEClientSecurityManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.utils.ClientCredentialsBean;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.jaas.JaasGssUtil;
import org.globus.wsrf.impl.security.descriptor.CredentialParamsParser;
import org.ietf.jgss.GSSCredential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * 
 * A context that manages all the security related operations
 * 
 * @author Ciro Formisano
 *
 */
public class DefaultGHNClientSecurityContext extends DefaultGHNSecurityContext 
{
	private GCUBELog logger;

	
	public DefaultGHNClientSecurityContext() 
	{
		super ();
		this.logger = new GCUBELog(this);
		init();
	}
	
	/**
	 * 
	 * Inits the SecurityContext
	 * 
	 */
	private void init ()
	{
		String pathToDefaultSecurityConfiguration = null;

		try 
		{
			logger.debug("Loading client mode default security configuration");
			WSDDDocument document = ((GHNClientContext) GHNContext.getContext()).getServerWsddDocument();
			pathToDefaultSecurityConfiguration = (String) document.getDeployment().getGlobalOptions().get(DEFAULT_SECURITY_CONFIGURATION);
			logger.debug(pathToDefaultSecurityConfiguration);
		} catch (ConfigurationException e)
		{
			logger.error("Server.wsdd configuration error",e);
			
		}
		super.init(pathToDefaultSecurityConfiguration);
	
	}
	

	

	
	 
	 /* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultSubject()
	 */
	 @Override
	public Subject getDefaultSubject ()
	 {
		 if (defaultCredentials == null)
		 {
			 this.defaultCredentials = getDefaultCredentials();
		 }
		 
		 if (defaultSubject == null && defaultCredentials != null)
		 {
			 try 
			 {
			 
			 this.defaultSubject = JaasGssUtil.createSubject(defaultCredentials);
			 
			 } catch (Exception e)
			 {
				 logger.error("Error in loading the subject ",e);
			 }
		 }
		
		 return this.defaultSubject;
	 }
			
	 
	 /* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultrCredentials()
	 */
	 	@Override
	 	public GSSCredential getDefaultCredentials ()
	 	{
	 		if (this.defaultCredentials == null) 
	 		{

	 			try
	 			{	
					logger.debug("Loading client mode container credentials");
					WSDDDocument document = ((GHNClientContext) GHNContext.getContext()).getServerWsddDocument();
					String secDescPath = GHNContext.getContext().getLocation()+File.separatorChar+document.getDeployment().getGlobalConfiguration().getParameter("containerSecDesc");
					logger.debug(secDescPath);
					BufferedReader scanner = new BufferedReader(new FileReader(secDescPath));
					StringBuilder builder = new StringBuilder();
					String line = null;
					
					while ((line = scanner.readLine() ) != null)
					{
						builder.append(line);
					}
				
					logger.debug("Sec desc "+builder.toString());
					
					Element rootElement = stringToElement(builder.toString());

					NodeList credList = rootElement.getElementsByTagName(CredentialParamsParser.CREDENTIAL_NAME);
					Element credElement = null;
					
					if (credList == null || credList.getLength() ==0) credList = rootElement.getElementsByTagName(CredentialParamsParser.PROXY_FILE_NAME);
					
					if (credList != null && credList.getLength() >0)
					{
						credElement = (Element) credList.item(0);
						ClientCredentialsBean bean = new ClientCredentialsBean();
						CredentialParamsParser parser = new CredentialParamsParser(bean);
						parser.parse(credElement);
						String proxy = bean.getProxyFile();
						logger.debug("Proxy file = "+proxy);
						
						if (proxy != null)
						{
							defaultCredentials = ProxyUtil.loadProxyCredentials(proxy);
						}
						else if (bean.getCertFile() != null && bean.getKeyFile()!= null)
						{
							String certFile = bean.getCertFile();
							String keyFile = bean.getKeyFile();
							logger.debug("Cert file = "+certFile);
							logger.debug("Key file = "+keyFile);
							GlobusCredential globusCredentials =  new GlobusCredential(certFile, keyFile);
							defaultCredentials = new GlobusGSSCredentialImpl(globusCredentials, GSSCredential.INITIATE_AND_ACCEPT);
						}
					}
		 			
	 			}
	 			catch (Exception e)
	 			{
	 				this.logger.error("Unable to load container credentials, some operations could be not available", e);
	 			}
	 		}
	 		
	 		return defaultCredentials;
	 	}
			

		
		/* (non-Javadoc)
		 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultSecurityManager()
		 */
		@Override
		public GCUBESecurityManager getDefaultSecurityManager () throws Exception
		{
			logger.debug("Generate default security manager");
			logger.debug("Client mode: generating a Client security manager");
			Class<? extends GCUBESecurityManager> securityManagerClass = GCUBEClientSecurityManager.class;
			return GHNContext.getImplementation(securityManagerClass);
		}
	 

		/**
		 * 
		 * @param nodeAsString
		 * @return
		 * @throws Exception
		 */
		private  Element stringToElement(String nodeAsString) throws Exception 
		{
			Document xml = string2Document(nodeAsString);
			Element element = xml.getDocumentElement();
			return element;

		}
		
		/**
		 * 
		 * @param xmlString
		 * @return
		 */
		private  Document string2Document(String xmlString) 
		{
			Document doc = null;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();

				doc = builder.parse(new InputSource(new StringReader(xmlString)));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return doc;

		}
		
}
