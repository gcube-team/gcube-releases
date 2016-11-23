package org.gcube.data.analysis.tabulardata.commons.utils;

public class AuthorizationProvider {

	public static AuthorizationProvider instance = new AuthorizationProvider();
	
	// Thread local variable containing each thread's ID
    private static final ThreadLocal<AuthorizationToken> threadAuth =
        new ThreadLocal<AuthorizationToken>() {
    	    	
    	@Override protected AuthorizationToken initialValue() {
                return null;
        }
    	
    };
	
	private AuthorizationProvider(){}
    
	public AuthorizationToken get(){
		return threadAuth.get();
	}
	
	public void set(AuthorizationToken authorizationToken){
		threadAuth.set(authorizationToken);
	}

}
