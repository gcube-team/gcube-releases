package org.gcube.common.authorization.library.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityTokenProvider {

	public static SecurityTokenProvider instance = new SecurityTokenProvider();
	
	private static Logger logger = LoggerFactory.getLogger(SecurityTokenProvider.class);
	
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<String> threadToken =
        new InheritableThreadLocal<String>() {
    	    	
    	@Override protected String initialValue() {
                return null;
        }
    	
    };
	
	private SecurityTokenProvider(){}
    
	public String get(){
		logger.debug("gettin securityToken "+threadToken.get()+" in thread "+Thread.currentThread().getId());
		return threadToken.get();
	}
	
	public void set(String authorizationToken){
		logger.debug("setting securityToken "+authorizationToken+" in thread "+Thread.currentThread().getId());
		threadToken.set(authorizationToken);
	}
	
	public void reset(){
		threadToken.remove();
	}
}
