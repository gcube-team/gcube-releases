package org.gcube.common.authorization.library.provider;


public class SecurityTokenProvider {

	public static SecurityTokenProvider instance = new SecurityTokenProvider();
	
	//private static Logger logger = LoggerFactory.getLogger(SecurityTokenProvider.class);
	
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<String> threadToken =
        new InheritableThreadLocal<String>() {
    	    	
    	@Override protected String initialValue() {
                return null;
        }
    	
    };
	
	private SecurityTokenProvider(){}
    
	public String get(){
		return threadToken.get();
	}
	
	public void set(String authorizationToken){
		threadToken.set(authorizationToken);
	}
	
	public void reset(){
		threadToken.remove();
	}
}
