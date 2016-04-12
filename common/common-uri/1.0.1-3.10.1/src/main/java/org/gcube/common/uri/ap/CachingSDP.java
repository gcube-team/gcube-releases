package org.gcube.common.uri.ap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ScopedAuthorityProvider} that caches authorities by the associated scopes.
 * <p>
 * The provider delegates the identification of the authority to an existing {@link ScopedAuthorityProvider}.
 * 
 * @author Fabio Simeoni
 *
 */
public class CachingSDP implements ScopedAuthorityProvider {

	private static Logger log = LoggerFactory.getLogger(CachingSDP.class);
	
	private static final int DEFAULT_SIZE = 10;
	
	private final Map<String,String> authorities;
	
	private final ScopedAuthorityProvider target;
	
	/**
	 * Creates an instance for a given {@link ScopedAuthorityProvider}.
	 * 
	 * @param cached the provider.
	 */
	public CachingSDP(ScopedAuthorityProvider cached) {
		this(cached,DEFAULT_SIZE);
	}
	
	@SuppressWarnings("serial")
	public CachingSDP(ScopedAuthorityProvider cached, final int size) {
		
		this.target=cached;
		
		this.authorities = Collections.synchronizedMap(
    			new LinkedHashMap<String, String>() {
			    	protected boolean removeEldestEntry(Map.Entry<String,String> eldest) {
			    	       return size() > size;
			    	    }
    			}
    		);
	}
	
	@Override
	public String authorityIn(String scope) {
		
		String authority = authorities.get(scope);
		
		if (authority==null)
			//target may take indefinitely long, sync only clients for same scope
			synchronized (scope) { 
				authority = target.authorityIn(scope);
				log.info("cached authority {} for scope {}",authority,scope);
			}
		
		
		authorities.put(scope,authority);

		return authority;
	}
	
	
}
