package org.gcube.smartgears.utils;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnerMethodName {

	public static InnerMethodName instance = new InnerMethodName();
	
	private static Logger logger = LoggerFactory.getLogger(CalledMethodProvider.class);
	
	// Thread local variable containing each thread's ID
    private static final InheritableThreadLocal<String> threadMethod =
        new InheritableThreadLocal<String>() {
    	    	
    	@Override protected String initialValue() {
                return "UNKNOWN";
        }
    	
    };
	
	private InnerMethodName(){}
    
	public String get(){
		String calledMethod = threadMethod.get();
		logger.trace("getting InnerMethodName as "+calledMethod+" in thread "+Thread.currentThread().getId() );
		return calledMethod;
	}
	
	public void set(String calledMethod){
		if (calledMethod==null) return;
		threadMethod.set(calledMethod);
		logger.trace("setting InnerMethodName as "+calledMethod+" in thread "+Thread.currentThread().getId() );
	}
	
	public void reset(){
		threadMethod.remove();
	}
}
