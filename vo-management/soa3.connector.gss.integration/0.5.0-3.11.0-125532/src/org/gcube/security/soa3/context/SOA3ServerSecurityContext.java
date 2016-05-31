package org.gcube.security.soa3.context;

import java.io.File;
import java.security.Provider;
import java.security.Security;
import java.util.Set;

import javax.security.auth.Subject;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.context.SecurityContext;
import org.gcube.common.core.security.impl.GCUBECredentialAdder;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.ietf.jgss.GSSCredential;

public class SOA3ServerSecurityContext implements SecurityContext
{
	private GCUBELog logger;
	
	public SOA3ServerSecurityContext ()
	{
		this.logger = new GCUBELog(this);
		String pathToDefaultSecurityConfiguration = GHNContext.getContext().getLocation()+File.separatorChar+ContainerConfig.getConfig().getOption(DEFAULT_SECURITY_CONFIGURATION);
		this.logger.debug("Security configuration path: "+pathToDefaultSecurityConfiguration);
		
		if (this.logger.isDebugEnabled())
		{
			Provider[] provs = Security.getProviders();
			
			for (Provider p : provs)
			{
				this.logger.debug(p.getName());
				Set<Provider.Service> services = p.getServices();
				
				if (services!=null && !services.isEmpty())
				{
					this.logger.debug("Services:");
					
					for (Provider.Service s : services)
					{
						this.logger.debug(s.getAlgorithm());
					}
					
					this.logger.debug("********************");
					
				}
			}
		}
		

		
		
		
	}

	@Override
	public GCUBEDefaultSecurityConfiguration getDefaultServiceSecurityConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceSecurityDescriptor getDefaultIncomingMessagesSecurityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceSecurityDescriptor getDefaultOutgoingMessagesSecurityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Subject getDefaultSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GSSCredential getDefaultCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCUBESecurityManager getDefaultSecurityManager() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GCUBECredentialAdder getCredentialsAdder() {
		// TODO Auto-generated method stub
		return null;
	}

}
