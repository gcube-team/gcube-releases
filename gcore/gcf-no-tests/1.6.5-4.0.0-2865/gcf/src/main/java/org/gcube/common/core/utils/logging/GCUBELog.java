package org.gcube.common.core.utils.logging;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.common.core.contexts.GCUBEContext;

/**
 * A wrapper of logger implementations built on top of <code>org.apache.commons.logging</code> interfaces and configuration.
 * It can be configured to prepend a given string to all messagges, where the prefix is either explicitly provided or
 * is extracted from a {@link org.gcube.common.core.contexts.GCUBEContext}. 
 * 
 * @author Manuele Simi (CNR),Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBELog {

	/** The internal logger. */
    private volatile Log logger;   
    /** The message prefix. */
    private String prefix;
    
    private GCUBEContext ctxt;
            
     /**
     * Creates a new logger for a given object and in a given context.
     * It prepends the name of the context to all log messages.
     * @param obj the object.
     * @param context the service context.
     */
    public GCUBELog(Object obj, GCUBEContext context) {	
		this(obj);
		this.setContext(context);
    }
    
    /**
     * Creates a new logger for a given object and with a given prefix to prepend to all log messages.
     * 
     * @param obj the object.
     * @param prefix the prefix.. 
     */
    public GCUBELog(Object obj, String prefix) {	
		this(obj);	
		this.setPrefix(prefix);
    }

    /**
     * Creates a new logger for a given object.
     * 
     * @param obj the object.
     */
    @SuppressWarnings("unchecked")
	public GCUBELog(Object obj) {
    	try {
    		Class clazz = (Class) obj;
    		this.logger = LogFactory.getLog(clazz);
    		this.setPrefix(clazz.getSimpleName());
    	}
    	catch (ClassCastException e ) {
    		this.logger = LogFactory.getLog(obj.getClass());
    		this.setPrefix(obj.getClass().getSimpleName());
    	}		
    }

    /** @see Log#debug(Object) */
    public void debug(Object arg0) {if (logger.isDebugEnabled()) logger.debug(this.getPrefix() + arg0);}
    /** @see Log#debug(Object, Throwable) */
    public void debug(Object arg0, Throwable arg1) {if (logger.isDebugEnabled()) logger.debug(this.getPrefix() + arg0, arg1);}
    /** @see Log#error(Object) */
    public void error(Object arg0) {if (logger.isErrorEnabled()) logger.error(this.getPrefix() + arg0);}
    /** @see Log#error(Object,Throwable) */
    public void error(Object arg0, Throwable arg1) {if (logger.isErrorEnabled()) logger.error(this.getPrefix() + arg0, arg1);}
    /** @see Log#fatal(Object) */
    public void fatal(Object arg0) {if (logger.isFatalEnabled()) logger.fatal(this.getPrefix() + arg0);}
    /** @see Log#fatal(Object,Throwable) */
    public void fatal(Object arg0, Throwable arg1) {if (logger.isFatalEnabled()) logger.fatal(this.getPrefix() + arg0, arg1);}
    /** @see Log#info(Object) */
    public void info(Object arg0) {if (logger.isInfoEnabled()) logger.info(this.getPrefix() + arg0);}
    /** @see Log#fatal(Object, Throwable) */
    public void info(Object arg0, Throwable arg1) {if (logger.isInfoEnabled()) logger.info(this.getPrefix() + arg0, arg1);}
    /** @see Log#trace(Object) */
    public void trace(Object arg0) {if (logger.isTraceEnabled()) logger.trace(this.getPrefix() + arg0);}
    /** @see Log#fatal(Object, Throwable) */
    public void trace(Object arg0, Throwable arg1) {if (logger.isTraceEnabled()) logger.trace(this.getPrefix() + arg0, arg1);}
    /** @see Log#warn(Object) */
    public void warn(Object arg0) {if (logger.isWarnEnabled()) logger.warn(this.getPrefix() + arg0);}
    /** @see Log#warn(Object, Throwable) */
    public void warn(Object arg0, Throwable arg1) {if (logger.isWarnEnabled()) logger.warn(this.getPrefix() + arg0, arg1);}
    /** @see Log#isDebugEnabled() */
    public boolean isDebugEnabled() {return logger.isDebugEnabled();}
    /** @see Log#isErrorEnabled() */
    public boolean isErrorEnabled() {return logger.isErrorEnabled();}
    /** @see Log#isFatalEnabled()*/
    public boolean isFatalEnabled() {return logger.isFatalEnabled();}
    /** @see Log#isInfoEnabled()*/
    public boolean isInfoEnabled() {return logger.isInfoEnabled();}
    /** @see Log#isTraceEnabled() */
    public boolean isTraceEnabled() {return logger.isTraceEnabled();}
    /** @see Log#isWarnEnabled() */
    public boolean isWarnEnabled() {return logger.isWarnEnabled();}
    /** Returns the log prefix.
     * @return the prefix.*/
    public String getPrefix() {return (ctxt==null?"":"["+ctxt.getTiming()+"s] ")+prefix;}
    /**Sets the log prefix.
     * @param prefix the prefix.*/
    public void setPrefix(String prefix) {this.prefix = prefix + ": ";}
    /** Sets the logging context.
     * @param context the context.*/
    public synchronized void setContext(GCUBEContext context) {
    	this.setPrefix(context.getName());
    	this.ctxt=context;
    }

}
