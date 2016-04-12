package org.gcube.security.soa3.configuration;

import java.util.Properties;

public interface GSSConfigurationManager extends ConfigurationManager
{
	public void setServiceProperties (String serviceName, Properties serviceProperties);

}
