package org.gcube.common.scope.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scans the classpath for a {@link ScopeProvider} implementation.
 * <p>
 * Returns a shared {@link DefaultScopeProvider} by default.
 * 
 * @author Fabio Simeoni
 * @see ScopeProvider
 * @see ScopeProvider#instance
 */
public class ScopeProviderScanner {

	private static Logger log = LoggerFactory.getLogger(ScopeProviderScanner.class);
	
	/**
	 * Returns the configured provider. 
	 * @return the provider
	 */
	public static ScopeProvider provider() {
		try {

			ScopeProvider impl = null;
			
			ServiceLoader<ScopeProvider> loader = ServiceLoader.load(ScopeProvider.class);
			
			Iterator<ScopeProvider> iterator = loader.iterator();
			List<ScopeProvider> impls = new ArrayList<ScopeProvider>();

			 while(iterator.hasNext())
				 impls.add(iterator.next());
					
			 if (impls.size()==0) {
				 impl = new DefaultScopeProvider();
			 }
			 else if (impls.size()>1)
				 throw new Exception("mis-configured environment: detected multiple default providers "+impls);
			 else
				 impl=impls.get(0);
				 
			 log.info("using scope provider "+impl);
			 
			 return impl;
			   
			
		} catch (Exception e) {
			throw new RuntimeException("could not configure scope provider", e);
		}
	}


	
}
