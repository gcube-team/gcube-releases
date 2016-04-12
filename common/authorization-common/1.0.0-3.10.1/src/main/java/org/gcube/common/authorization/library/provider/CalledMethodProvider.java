package org.gcube.common.authorization.library.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalledMethodProvider {

public static CalledMethodProvider instance = new CalledMethodProvider();
	
	private static Logger logger = LoggerFactory.getLogger(CalledMethodProvider.class);
	
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<String> threadMethod =
        new InheritableThreadLocal<String>() {
    	    	
    	@Override protected String initialValue() {
                return "UNKNOWN";
        }
    	
    };
	
	private CalledMethodProvider(){}
    
	public String get(){
		String calledMethod = threadMethod.get();
		logger.trace("getting calledMethod as "+calledMethod+" in thread "+Thread.currentThread().getId() );
		return calledMethod;
	}
	
	public void set(String calledMethod){
		if (calledMethod==null) return;
		threadMethod.set(calledMethod);
		logger.trace("setting calledMethod as "+calledMethod+" in thread "+Thread.currentThread().getId() );
	}
	
	public void reset(){
		threadMethod.remove();
	}
}
