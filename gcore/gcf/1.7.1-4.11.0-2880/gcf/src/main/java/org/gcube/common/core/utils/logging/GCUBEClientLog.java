package org.gcube.common.core.utils.logging;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.gcube.common.core.contexts.GHNContext;


public class GCUBEClientLog extends GCUBELog {
	
    /**
     * Creates a new logger for a given object and with a given prefix to prepend to all log messages.
     * 
     * @param obj the object.
     * @param prefix the prefix.. 
     */
    public GCUBEClientLog(Object obj, String prefix) {super(obj,prefix);}
    
    public GCUBEClientLog(Object obj,Properties ... properties) {
    	super(obj);
    	Properties props;
    	try {
	    	if (properties == null || properties.length==0) {
	    		props = new Properties();
	    		//preferred method is via classpath, if correctly set by client
	    		InputStream config = ((obj instanceof Class<?>)?(Class<?>)obj:obj.getClass()).getResourceAsStream("client-log4j.properties");
	    		//preferred method is via classpath, if correctly set by client
	    		if (config==null) config = GCUBEClientLog.class.getResourceAsStream("/client-log4j.properties");
	    		//alternatively from file system, but won't show GHNContext initialisation
	    		if (config==null) config = new FileInputStream(GHNContext.getContext().getFile("../client-log4j.properties"));
	    		props.load(config);
		    }
	    	else props=properties[0];
	     	PropertyConfigurator.configure(props);  
    	}
    	catch(Exception e) {
    		System.out.println("GCUBEClientLog: Could not configure the logger dinamically:");
    		e.printStackTrace();
    	}
    		
    }
}
