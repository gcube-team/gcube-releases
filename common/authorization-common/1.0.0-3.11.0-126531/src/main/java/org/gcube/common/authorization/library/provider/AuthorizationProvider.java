package org.gcube.common.authorization.library.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationProvider {

	public static AuthorizationProvider instance = new AuthorizationProvider();
	
	private static Logger logger = LoggerFactory.getLogger(AuthorizationProvider.class);
	
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<UserInfo> threadAuth =
        new InheritableThreadLocal<UserInfo>() {
    	    	
    	@Override protected UserInfo initialValue() {
                return null;
        }
    	
    };
	
	private AuthorizationProvider(){}
    
	public UserInfo get(){
		UserInfo info = threadAuth.get();
		logger.trace("getting "+info+" in thread "+Thread.currentThread().getId() );
		return info;
	}
	
	public void set(UserInfo authorizationToken){
		threadAuth.set(authorizationToken);
		logger.trace("setting "+authorizationToken+" in thread "+Thread.currentThread().getId() );
	}
	
	public void reset(){
		threadAuth.remove();
	}

}
