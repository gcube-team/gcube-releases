package org.gcube.common.core.contexts;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

import org.apache.naming.SynchronizedContext;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Base implementation for <em>contexts</em>, i.e. centralised information managers and 
 * utility providers for key implementation components (e.g. services, porttypes, nodes).
 * From this implementation, contexts inherit facilities for handling component configuration.
 * Configuration may be accessed from files in the file system or the classpath and then stored in local
 * state or in a JNDI naming service (typically that of the gCore container). 
 * Storage may occur at any point of a context's lifetime, but it is always transient. Its 
 * persistence is largely responsibility of subclasses, though this implementation offers 
 * some transparencies for the management of file backups.
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (ISTI-CNR)
 *
 */
public abstract class GCUBEContext {
	
	/** Object logger. */
	protected final GCUBELog logger = new GCUBELog(this);
	
	/** The initial JNDI context of the naming service. */
	private Context JNDIcontext;

	/**
	 * Creates and initialises an instance.
	 */
	@SuppressWarnings("unchecked")
	public GCUBEContext() {
		try {
			Hashtable env = new Hashtable();
			env.put(SynchronizedContext.SYNCHRONIZED, "true");
			env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			this.JNDIcontext=new InitialContext(env);
		}
		catch (Exception e) {
			logger.fatal("Could not initialise JNDI context",e);			
		}
		this.logger.setContext(this);//instructs logger to use the context nome in logs entries.
	}
	
	
	/**
	 * Returns the initial JNDI context of the naming service.
	 * @return the naming context.
	 */
	protected Context getJNDIContext() {return this.JNDIcontext;}
	
	/**
	 * Sets the initial JNDI context of the naming service.
	 * @param jndiContext the naming context.
	 */
	protected void setJNDIContext(Context jndiContext) {this.JNDIcontext=jndiContext;}

	/** 
	 * Resolve a configuration property against the naming service. Resolution is relative
	 * to the root JNDI context of the naming service and the JNDI name associated with the context.
	 * @param prop the property.
	 * @param required (optional) <code>true</code> if the property is required, <code>false</code> or omitted if property is optional.
	 * The only implication is a different handling of failure and logging. Failure in resolving optional properties results in a runtime
	 * exception and fatal log entry, whereas failure in resolving optional properties results in a <code>null</code> result and warning 
	 * in the log.
	 * @return the value of the property, or <code>null</code> if the property was optional and could not be resolved.
	 * @throws RuntimeException if the property is required but cannot be resolved.
	 */
	public Object getProperty(String prop, boolean ...required) throws RuntimeException {

		try {
			return this.getJNDIContext().lookup(prop);
		}
		catch (Exception e) {
			String msg="Configuration property '"+prop+"' does not exist or could not be processed";
			if (required.length>0 && required[0]) throw new RuntimeException(msg,e);
			//check exception type to avoid verbose logging for optional properties
			if (!NameNotFoundException.class.isAssignableFrom(e.getClass())) logger.warn(msg,e);
			return null;
		}
	}
	
	
	/**
	 * Returns the name of the context as used in log entries.
	 * By default, it returns the simple name of the context implementation.
	 * Override to specialize the log (e.g. with the name of the component associated
	 * with the context).
	 * @return the name.
	 */	
    public String getName() {return this.getClass().getSimpleName();}
    
	
    /**Returns a classpath resource given its path. 
     * By default, a relative path is resolved with respect to the context implementation. 
     * @return the resource, or <code>null</code> if the path could not be resolved.*/
    public InputStream getResource(String resourceName)  {
       	return this.getClass().getResourceAsStream(resourceName); 
    }
    
	/**
	* Gives read or write access to a {@link java.io.File}.
	* Write access induces backups and read access relies on backups to recover from failures.
    * @param path the file path.
    * @param writeMode (optional) the access mode, <code>true</code> for write access and <code>false</code> for read access (default).
    * @return the file.
    * @throws IllegalArgumentException if access is in write mode and the path is to a folder.
    */
    public File getFile(String path, boolean ... writeMode) throws IllegalArgumentException  {
    
    	boolean toWrite = (writeMode==null || writeMode.length==0)? false :writeMode[0];
    	
    	//this is the single-point of physical dependency for the container	
        File file = new File(path);

        
        if (toWrite) {//write

        	if (file.isDirectory()) throw new IllegalArgumentException("Path is to a folder");
        	
        	//create parent folder if it does not exist yet
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            
        	BufferedReader reader = null;
        	BufferedWriter writer=null;
	        try {
	        	if (file.exists()) {//create backup
	        		reader = new BufferedReader(new FileReader(file));
	        		writer = new BufferedWriter(new FileWriter(new File(file.getAbsolutePath()+".backup")));
	        		String line; while ((line=reader.readLine())!=null) writer.write(line);
	        	}
	        }
	        catch (Exception e) {logger.warn("Could not create backup of "+file.getAbsolutePath(),e);}
	        finally {
	        	try{if (reader!=null) reader.close(); if (writer!=null) writer.close();}catch(Exception shrug){}}
	        	
        } 
        else {
        	if (!file.exists() || file.length()==0) {
        		File backup = new File(file.getAbsolutePath()+".backup");
        		if (backup.exists()) backup.renameTo(file);
            }
        }
        return file;
    }
    
    /**
     * Convenience method for testing. Shows the content of the naming service, recursively and 
     * starting from a given context. 
     * @param ctxt the starting context.
     * @param indent used internally to format debug information in line with the context hierarchy.
     * @throws Exception if the content could not be shown.
     */
    public static void debugContext(Context ctxt, String ... indent)  throws Exception{
    	GCUBELog classLogger = new GCUBELog(GCUBEContext.class);
		String spaces = (indent==null || indent.length==0)? "":indent[0]+"   "; 
		NamingEnumeration<Binding> bindings = ctxt.listBindings("/");
	    while (bindings.hasMoreElements()) {
	    	Binding binding = bindings.nextElement();
	    	Object value = binding.getObject();
	    	if (Context.class.isAssignableFrom(value.getClass())) {
	    		classLogger.debug(spaces+binding.getName()+"->");
	    		debugContext((Context) value, spaces);
	    	}
	    	else classLogger.debug(spaces+binding.getName()+"("+binding.getClassName()+")->"+value);
	    }
	}
    
    public static void printContext(Context context) {
    	GCUBELog classLogger = new GCUBELog(GCUBEContext.class);
    	classLogger.debug(context);    	    	
		try {
			for (Object key : context.getEnvironment().keySet()) {
				classLogger.debug("Key: " + key);
				classLogger.debug("Value: " + context.getEnvironment().get(key));
			}
			debugContext(context);
		} catch (Exception e) {
			classLogger.error("Failed to get the JNDI env", e);
		}

		

	}

    
	/** Timers, indexed by thread */
	protected Map<Thread,Long> timers = Collections.synchronizedMap(new WeakHashMap<Thread,Long>());
	
	/** Returns the timing for the current thread.
	 * @return the timing.*/
	public float getTiming() {
		Thread t = Thread.currentThread();
		long now = Calendar.getInstance().getTimeInMillis(); 
		synchronized (timers) {if (timers.get(t)==null) timers.put(t,now);}	
		return (now-timers.get(t))/1000f;
	
	}
	
	/** Reset the timer for the current thread. */
	public void resetTimer() {timers.remove(Thread.currentThread());}

}
