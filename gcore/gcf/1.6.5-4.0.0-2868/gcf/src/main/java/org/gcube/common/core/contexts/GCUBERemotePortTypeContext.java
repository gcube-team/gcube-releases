package org.gcube.common.core.contexts;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.axis.client.Stub;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.digester.Digester;
import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.faults.GCUBERetrySameFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManager.AuthMethod;
import org.gcube.common.core.security.GCUBESecurityManager.AuthMode;
import org.gcube.common.core.security.GCUBESecurityManager.DelegationMode;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.scope.api.ScopeProvider;
import org.globus.wsrf.Constants;
import org.globus.wsrf.impl.security.descriptor.GSISecureConvAuthMethod;
import org.globus.wsrf.impl.security.descriptor.GSITransportAuthMethod;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityConfig;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.globus.wsrf.jndi.NamingContext;
import org.globus.wsrf.tools.jndi.JNDIConfigRuleSet;

/**
 * Contexts for remote port-types accessed through a locally available distribution of their stubs.<p>
 * If the distribution is gCube-compliant (e.g. was produced with gCore building facilities), 
 * the contexts expose the configuration of the remote port-type and its service, and in fact use it 
 * to achieve transparent setting of scoping and security for gCube calls to the port-type. <p>
 * Normally, clients do not work directly with the contexts, but request
 * a gCube proxy to the conventional port-type stub, which can offer the transparencies mentioned above.
 * Contexts are instead created implicitly, in the process of creating stub proxies. They are then shared
 * across all future requests for proxies of the same remote port-types.   
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @author Manuele Simi (ISTI-CNR)
 *
 * @param <PORTTYPE> the type of the stub of the remote port-type.
 */
public class GCUBERemotePortTypeContext<PORTTYPE extends Remote> extends GCUBEContext {

	/** Name of the service class call header. */
	public static final String CLASS_HEADER_NAME="serviceClass";
	/** Name of the service name call header. */
	public static final String NAME_HEADER_NAME="serviceName";
	/** Name of the scope call header. */
	public static final String SCOPE_HEADER_NAME="scope";
	
	/** Name of the scope call header. */
	public static final String AUTH_TOKEN_HEADER_NAME="gcube-token";
	
	public static final String CALLED_METHOD_HEADER_NAME="gcube-method";
	
	/** Namespace of scope-related headers */
	public static final String SCOPE_NS="http://gcube-system.org/namespaces/scope";
	
	/** Root Context for remote port-type configurations. */
	protected static String JNDI_STUBS_ROOT_CONTEXT=Constants.JNDI_BASE_NAME+"/stubs/services/";
	/** Standard name of the JNDI configuration file of the remote port-type. */
	protected static String JNDI_FILE_NAME="deploy-jndi-config.xml"; 
	/** Standard name of the WSDD configuration file of the remote port-type. */
	protected static String WSDD_FILE_NAME="deploy-server.wsdd";
	/** Standard name of the build property file of the remote port-type. */
	protected static String BUILD_PROPERTIES_FILE_NAME="build.properties";
	/** Standard name of the build property which identifies the JNDI configuration of the remote port-type. */
	protected static String JNDI_NAME_BUILD_PROPERTY="jndi.name"; 
	/** Name of the scope call header. */
	public static final String CALLER_HEADER_NAME="caller";
	/** Namespace of scope-related headers */
	public static final String CALLER_NS="http://gcube-system.org/namespaces/caller";
	
	/** Cache of remote port-type contexts.*/
	protected static Map<Class<? extends Remote>,GCUBERemotePortTypeContext<?>> contexts = new HashMap<Class<? extends Remote>, GCUBERemotePortTypeContext<?>>();  
	
	/** The class of the stub of the remote port-type.*/
	protected Class<? extends Remote> portTypeClass;
	/** The local resource which models the service of the remote port-type.*/
	protected GCUBEService service=GHNContext.getImplementation(GCUBEService.class);
	/** The JNDI name of the service of the remote port-type.*/
	protected String serviceJNDIName;
	/** The JNDI name of the remote port-type.*/
	protected String portTypeJNDIName;
	/** The deployment descriptor of the remote port-type.*/
	protected WSDDService deploymentDescriptor;
	/** The security descriptor of the remote port-type.*/
	protected ServiceSecurityDescriptor securityDescriptor;
	/** default timeout for proxed calls */
	private static final int CALL_TIMEOUT = 60000;
	/** Creates an instance of the context for a given remote port-type.
	 * @param stub the conventional stub of the remote port-type.
	 * @throws Exception if the context could not be created because the stub was not part of a gCube-compliant distribution.
	 */
	private GCUBERemotePortTypeContext(PORTTYPE stub) throws Exception {
		
		this.portTypeClass = stub.getClass();
		try {
			//may be useful in the future...no need to access JNDI right now.
			//try {this.parseJNDIConfig(this.getConfigResource(JNDI_FILE_NAME));
			//}catch(Exception e) {throw new Exception("invalid JNDI configuration",e);}
			
			InputStream profile = this.getResource(GCUBEServiceContext.PROFILE_FILE_NAME);
			if (profile==null) throw new Exception(GCUBEServiceContext.PROFILE_FILE_NAME+" not found");
			
			service.load(new InputStreamReader(profile));//build service resource	    	
		    	
			//find port-type name from stub endpoint 
			String epr = ((String) ((Stub) stub)._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY));
			String prefix = GHNContext.getContext().getServiceEndpointPrefix();
			this.portTypeJNDIName = epr.substring(epr.lastIndexOf(prefix)+prefix.length());
			
			//fetch deployment descriptor
			WSDDDocument doc = new WSDDDocument(XMLUtils.newDocument(this.getResource(WSDD_FILE_NAME)));
			//if (doc==null) throw new Exception(WSDD_FILE_NAME+ " not found");
		    this.deploymentDescriptor = doc.getDeployment().getWSDDService(new QName(null,this.portTypeJNDIName));
		    if (this.deploymentDescriptor==null) throw new Exception("Service "+this.portTypeJNDIName+ " not found");
		    this.securityDescriptor = loadServiceSecurityDescriptor();
	   }
	   catch(Exception e) {
		   throw new Exception("Invalid stub distribution:",e);
	   }
		

	}
	
	private ServiceSecurityDescriptor loadServiceSecurityDescriptor () throws Exception
	{
		logger.debug("Loading outgoing security descriptor");
	    String securityDescriptorAbsoluteFileName=this.deploymentDescriptor.getParameter(GCUBEPortTypeContext.WSSD_WSDD_NAME);
	    GCUBEDefaultSecurityConfiguration defaultSecurityConfiguration = SecurityContextFactory.getInstance().getSecurityContext().getDefaultServiceSecurityConfiguration();
	    ServiceSecurityDescriptor response = null;
	    
		if ((defaultSecurityConfiguration!= null && defaultSecurityConfiguration.isOutEnabled() && defaultSecurityConfiguration.isOutOverride()) || (defaultSecurityConfiguration != null && defaultSecurityConfiguration.isOutEnabled() && securityDescriptorAbsoluteFileName == null))
		{
			logger.debug("Ingoing override = "+defaultSecurityConfiguration.isOutOverride());
			logger.debug("sec desc file = "+securityDescriptorAbsoluteFileName);
			logger.debug("Loading default ingoing security descriptor");
			response = SecurityContextFactory.getInstance().getSecurityContext().getDefaultOutgoingMessagesSecurityDescriptor();
			logger.debug("Security descriptor = "+response);
		}
		else if (securityDescriptorAbsoluteFileName != null) // && (builder==null || (builder!=null && !builder.inOverride()))
		{
			logger.debug("loading from the file");
		    String securityDescriptor = securityDescriptorAbsoluteFileName.substring(securityDescriptorAbsoluteFileName.lastIndexOf(File.separatorChar)+1);
		    //this is an annoyance: we do not have an absolute path to a file or a resource
		    //and the security API of this version of WSCore does not deal with streams...
		    //we must write a temporary file instead...
		    InputStream stream = this.getResource(securityDescriptor);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(stream)); 
		    File securityDescriptorTempFile= File.createTempFile(this.portTypeJNDIName.replace("/", "."),"securityDescriptor");
		    FileWriter writer = new FileWriter(securityDescriptorTempFile);
		    String line;while ((line=reader.readLine())!=null) writer.write(line);
		    writer.close();reader.close();			    
		    response = new ServiceSecurityDescriptor();
		    response.parse(ServiceSecurityConfig.loadSecurityDescriptor(securityDescriptorTempFile.getAbsolutePath()).getDocumentElement());
		    securityDescriptorTempFile.delete();		
		    logger.debug("security descriptor loaded");
		}
		else // if (securityDescriptorAbsoluteFileName == null && builder==null)
		{
				logger.debug("No security descriptor for this port type");
		}
		
		return response;
	}

	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the context of a given {@link GCUBEServiceContext}.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param context the service context.
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 */
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub,GCUBEServiceContext context) throws Exception {		
		return (PORTTYPE) getProxy(portTypeStub,CALL_TIMEOUT,context);
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the context of a given {@link GCUBEServiceContext}.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param context the service context.
	 * @param callTimeout the timeout per each call
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 * @author Ciro Formisano
	 */
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub, GCUBEServiceContext context, int callTimeout) throws Exception {		
		return (PORTTYPE) getProxy(portTypeStub,callTimeout,context);
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in a given {@link org.gcube.common.core.scope.GCUBEScope GCUBEScope} 
	 * and in the context of a given {@link org.gcube.common.core.security.GCUBESecurityManager GCUBESecurityManager}.
	 * 
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param scope the scope.
	 * @param securityManager the security manager.
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 * @deprecated as to 1.6.0 sets the current scope and redirects to {@link #getProxy(Remote)} ignoring the {@link GCUBESecurityManager} parameter
	 */
	@Deprecated
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub,final GCUBEScope scope, GCUBESecurityManager ... securityManager) throws Exception {		
		ScopeProvider.instance.set(scope.toString());
		return (PORTTYPE) getProxy(portTypeStub);
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in a given {@link org.gcube.common.core.scope.GCUBEScope GCUBEScope} 
	 * and in the context of a given {@link org.gcube.common.core.security.GCUBESecurityManager GCUBESecurityManager}.
	 * Note: the proxy so-created
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param scope the scope.
	 * @param callTimeout the timeout per each call
	 * @param securityManager the security manager.
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 * @deprecated as to 1.6.0 sets the current scope and redirects to {@link #getProxy(Remote, int)}, ignoring the {@link GCUBESecurityManager} parameter
	 */
	@Deprecated
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub,final GCUBEScope scope, int callTimeout, GCUBESecurityManager ... securityManager) throws Exception {		
		ScopeProvider.instance.set(scope.toString());
		return (PORTTYPE) getProxy(portTypeStub,callTimeout);
		
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the context of a given {@link org.gcube.common.core.scope.GCUBEScopeManager GCUBEScopeManager} 
	 * and a given {@link org.gcube.common.core.security.GCUBESecurityManager GCUBESecurityManager}.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param scopeManager the scope manager.
	 * @param securityManager the security manager.
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 * @deprecated as to 1.6.0 redirects to {@link #getProxy(Remote)} ignoring {@link GCUBEScopeManager} and {@link GCUBESecurityManager} parameters
	 */	
	@Deprecated
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub, GCUBEScopeManager scopeManager, GCUBESecurityManager ... securityManager) throws Exception {					
		ScopeProvider.instance.set(scopeManager.getScope().toString());
		return getProxy(portTypeStub);
	}

	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the context of a given {@link org.gcube.common.core.scope.GCUBEScopeManager GCUBEScopeManager} 
	 * and a given {@link org.gcube.common.core.security.GCUBESecurityManager GCUBESecurityManager}.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param scopeManager the scope manager.
	 * @param callTimeout the timeout per each call
	 * @param securityManager the security manager.
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 * @deprecated as to 1.6.0 redirects to {@link #getProxy(Remote, int)} ignoring {@link GCUBEScopeManager} and {@link GCUBESecurityManager} parameters
	 */
	@Deprecated
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub, GCUBEScopeManager scopeManager, int callTimeout, GCUBESecurityManager ... securityManager) throws Exception {
		ScopeProvider.instance.set(scopeManager.getScope().toString());
		return getProxy(portTypeStub,callTimeout);
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the current scope.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param callTimeout the timeout per each call
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 */
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub) throws Exception {

		return getProxy(portTypeStub,CALL_TIMEOUT);
	}
	
	/**
	 * Returns a gCube proxy to a conventional stub of a remote port-type. 
	 * The proxy is expected to be used for gCube calls in the current scope.
	 * @param <PORTTYPE> the type of the port-type stub.
	 * @param portTypeStub the original port-type stub.
	 * @param callTimeout the timeout per each call
	 * @return the gCube proxy to the stub.
	 * @throws Exception if a proxy for the stub could not be returned.
	 */
	@SuppressWarnings("unchecked")
	public static <PORTTYPE extends Remote> PORTTYPE getProxy(PORTTYPE portTypeStub, int callTimeout, GCUBEServiceContext ...context) throws Exception {

		//creates or reuses a stub context for this port-type
		GCUBERemotePortTypeContext<PORTTYPE> stubContext;
		synchronized (contexts)  {//protects check from concurrent threads
			stubContext = (GCUBERemotePortTypeContext) contexts.get(portTypeStub.getClass()); //safe
			if (stubContext==null)  {
				stubContext = new GCUBERemotePortTypeContext<PORTTYPE>(portTypeStub);
				contexts.put(portTypeStub.getClass(),stubContext);
			}
			((Stub) portTypeStub).setTimeout(callTimeout);
		}
		
		GCUBERemotePortTypeContext<?>.GCUBEMethodInterceptor interceptor = 
				(context!=null && context.length!=0)?
						stubContext.new GCUBEMethodInterceptor(portTypeStub,context[0]):
						stubContext.new GCUBEMethodInterceptor(portTypeStub);
			
			return (PORTTYPE) Enhancer.create(portTypeStub.getClass(),interceptor);
	}
	
    /** {@inheritDoc} */
    public InputStream getResource(String resourceName)  {
       	String className = this.portTypeClass.getSimpleName();
	    String classFileName = className + ".class";
	    String classFilePath = this.portTypeClass.getPackage().toString().replace('.', '/') + "/" + className;
	    String pathToClass = this.portTypeClass.getResource(classFileName).toString();
	    String url = pathToClass.toString().substring(0, pathToClass.length() + 2 - ("/" + classFilePath).length())
		+ "/META-INF/"+resourceName;
	    //logger.debug(url);
	    try {return new URL(url).openStream();} catch(Exception e) {return null;}
	    //return this.portTypeClass.getResourceAsStream(resourceName); 
    }
    
    /** 
	 * Returns the value of an arbitrary JNDI property (resource or environment) of the service of the remote port-type.
	 * @param prop the property.
	 * @return the value of the property.
	 */
	public Object getServiceProperty(String prop) {
		try {return this.getJNDIContext().lookup(JNDI_STUBS_ROOT_CONTEXT+this.serviceJNDIName+"/"+prop);}
		catch (Exception e) {return null;}
	}
	
	/** {@inheritDoc} */
	public Object getProperty(String prop, boolean ...required) throws RuntimeException {
		return super.getProperty(JNDI_STUBS_ROOT_CONTEXT+"/"+this.portTypeJNDIName+"/"+prop, required);
	}
	
  /** Returns the {@link org.gcube.common.core.resources.GCUBEService GCUBEService} resource of the service
	 * of the remote port-type. 
	 * @return the resource.
	 */
	public GCUBEService getService() {return service;}

	/** Returns the deployment descriptor of the remote port-type.
	 * @return the deployment descriptor.
	 */
	public WSDDService getDeploymentDescriptor() {return deploymentDescriptor;}

	/** Returns the security descriptor of the remote port-type.
	 * @return the security descriptor.
	 */
	public ServiceSecurityDescriptor getSecurityDescriptor() {return securityDescriptor;}
	
	/**
	 * Loads a JNDI configuration file into the JNDI context reserved to remote port-types. 
	 * @param config the file as a stream.
	 * @throws Exception if the configuration could not be loaded.
	 */
	protected void parseJNDIConfig(InputStream config) throws Exception {
		
		Context stubContext = null;
		//if we have not proxied any stub before, let us prepare the JNDI context now	
		try {stubContext= (Context) this.getJNDIContext().lookup(JNDI_STUBS_ROOT_CONTEXT);} 
		catch(NamingException e) {stubContext = ((Context) this.getJNDIContext().lookup(Constants.JNDI_BASE_NAME)).createSubcontext("stubs");}
		
		Digester digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(false);
        digester.addRuleSet(new JNDIConfigRuleSet("jndiConfig/"));
        digester.push(new NamingContext(stubContext,null));
        digester.parse(config);
        digester.clear();       
		//debugContext((Context) this.getJNDIContext().lookup(JNDI_STUBS_ROOT_CONTEXT));
	}
	
	
	/**
	 * Intercepts and propagates all calls to the original stubs of the remote port-type.
	 *
	 * @param <PORTTYPE> the type of the stub of the remote port-type.
	 */
	 class GCUBEMethodInterceptor implements MethodInterceptor {
				
		final GCUBESecurityManager securityManager;
		final PORTTYPE portType;
		final String identity;
		
		GCUBEMethodInterceptor(PORTTYPE portType, GCUBEServiceContext context) {
			this.portType=portType;
			this.identity = context.getServiceClass().toUpperCase()+":"+context.getName().toUpperCase() + ":" + GHNContext.getContext().getHostnameAndPort();
			this.securityManager=context;
		}
		
		GCUBEMethodInterceptor(PORTTYPE portType) {
			this.identity = GHNContext.getContext().getStatus()==Status.DEPLOYED? //client scenario
												GHNContext.getContext().getHostname():
												GHNContext.getContext().getHostnameAndPort();
			this.portType=portType;
			this.securityManager =SecurityContextFactory.getInstance().getSecurityContext().getCredentialsAdder();
		}
		
		public Object intercept(Object proxy, Method method, Object[] input,MethodProxy methodProxy) throws Throwable {
			
			//out immediately if it's system call
			if (method.getName().equals("finalize") && method.getDeclaringClass() == Object.class) return null;
			
			//sets target service identification headers 
			Stub stub = (Stub) this.portType;
			stub.clearHeaders();
			stub.setHeader(SCOPE_NS, CLASS_HEADER_NAME, getService().getServiceClass());
			stub.setHeader(SCOPE_NS, NAME_HEADER_NAME, getService().getServiceName());
			stub.setHeader(SCOPE_NS, SCOPE_HEADER_NAME,ScopeProvider.instance.get());
			
			((Stub)this.portType).setHeader(CALLER_NS, CALLER_HEADER_NAME, identity);

			//sets security if required
			if (this.securityManager!=null && this.securityManager.isSecurityEnabled() && GCUBERemotePortTypeContext.this.getSecurityDescriptor()!=null) 
			{
				//determine authentication method and mode
				AuthMode authMode=AuthMode.NONE;
				AuthMethod authMethod = AuthMethod.NONE;
				List<?> authMethods = GCUBERemotePortTypeContext.this.getSecurityDescriptor().getAuthMethods(new QName(null,method.getName()));
				
				if (authMethods==null) authMethods = GCUBERemotePortTypeContext.this.getSecurityDescriptor().getDefaultAuthMethods();
				
				if (authMethods!=null) 
				{ 
					for (Object authMethodObject : authMethods) 
					{
						int protType = -1;
						
						if (authMethodObject instanceof GSISecureConvAuthMethod) 
						{	
							logger.debug("Gsi conversation");
							authMethod = AuthMethod.GSI_CONV;
							protType = ((GSISecureConvAuthMethod) authMethodObject).getProtectionType();
						}
						else if (authMethodObject instanceof GSITransportAuthMethod)
						{
							logger.debug("Gsi transport");
							authMethod = AuthMethod.GSI_TRANS;
							protType = ((GSITransportAuthMethod) authMethodObject).getProtectionType();

						}

							
						switch (protType) 
						{
							case 0:authMode=AuthMode.BOTH;break;
							case 1:authMode=AuthMode.INTEGRITY;break;
							case 2:authMode=AuthMode.PRIVACY;break;
						}
				
						//ignore other methods for now
					}
				}
				
				DelegationMode delegationMode=DelegationMode.NONE;
				
				if (authMethod == AuthMethod.GSI_CONV)
				{
					logger.debug("GSI conversation set: setting delegation...");
					//determine delegation mode
					int  runAsType = GCUBERemotePortTypeContext.this.getSecurityDescriptor().getRunAsType(new QName(null,method.getName()));
					
					if (runAsType==-1) runAsType = GCUBERemotePortTypeContext.this.getSecurityDescriptor().getDefaultRunAsType();
					
					if (runAsType==1) delegationMode=DelegationMode.FULL;
					
					logger.debug("delegation set as "+delegationMode);
				}
				
				this.securityManager.setAuthMethod(authMethod);
				this.securityManager.setSecurity(this.portType, authMode, delegationMode);
				
			}
						
			
			try 
			{
				return methodProxy.invoke(portType, input);
			} 
			catch (GCUBEFault e) {
				//if we receive a subclass, nothing to do
				if (e.getClass()!=GCUBEFault.class) throw e;
				//otherwise use message fault to derive subclass
				String type = e.getFaultType();
				if (type==null) {logger.warn ("Fault is untyped could not narrow it");throw e;}
				//logger.debug("converting "+e.getClass()+" subtype "+type);
				GCUBEFault f=e;
				if(type.equals(GCUBERetryEquivalentFault.FAULT_TYPE)) f = new GCUBERetryEquivalentFault(e);
				if(type.equals(GCUBERetrySameFault.FAULT_TYPE)) f= new GCUBERetrySameFault(e);
				if(type.equals(GCUBEUnrecoverableFault.FAULT_TYPE)) f = new GCUBEUnrecoverableFault(e);
				f.setFaultType(e.getFaultType());
				f.setFaultMessage(e.getFaultMessage());
				f.setFaultDetail(e.getFaultDetails());
				throw f;
			}
		}
		
	}
    
}
