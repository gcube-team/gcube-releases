package org.gcube.common.authorization.library.provider;

import org.gcube.common.authorization.library.utils.Caller;

public class AuthorizationProvider {

	public static AuthorizationProvider instance = new AuthorizationProvider();
		
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<Caller> threadAuth =
        new InheritableThreadLocal<Caller>() {
    	    	
    	@Override protected Caller initialValue() {
                return null;
        }
    	
    };
	
	private AuthorizationProvider(){}
    
	public Caller get(){
		Caller info = threadAuth.get();
		return info;
	}
	
	public void set(Caller info){
		threadAuth.set(info);
	}
	
	public void reset(){
		threadAuth.remove();
	}

}
