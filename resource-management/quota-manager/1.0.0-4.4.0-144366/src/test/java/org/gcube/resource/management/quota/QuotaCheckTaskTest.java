package org.gcube.resource.management.quota;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resource.management.quota.manager.util.DiscoveryConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuotaCheckTaskTest {
	private static Logger log = LoggerFactory.getLogger(DiscoveryConfiguration.class);
	
	@Before
	public void before() throws Exception{
		String token="3acdde42-6883-4564-b3ba-69f6486f6fe0-98187548";
		SecurityTokenProvider.instance.set(token);
		String context="/gcube";
		ScopeProvider.instance.set(context);
			
	}
	
	
	
	
	@Test
	public void QuotaCheckTest() throws Exception{
	
		//String context=ScopeProvider.instance.get();
		//QuotaCheck quotaCheck =new QuotaCheck(context);
		//quotaCheck.getQuotaCheck();
		
	}
	
	@Test
	public void QuotaConfigurationTest() throws Exception{
	
		String context=ScopeProvider.instance.get();
		DiscoveryConfiguration discoveryCheck =new DiscoveryConfiguration(context);
		log.debug(discoveryCheck.toString());

	}
	
	
}
