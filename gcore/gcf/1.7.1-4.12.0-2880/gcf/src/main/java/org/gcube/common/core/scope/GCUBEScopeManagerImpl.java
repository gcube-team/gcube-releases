package org.gcube.common.core.scope;

import java.rmi.Remote;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.axis.client.Stub;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Default implementation of {@link GCUBEScopeManager}. 
 *
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 * @deprecated As to 1.6.0, use {@link ScopeProvider#instance} instead
 *
 */
@Deprecated
public class GCUBEScopeManagerImpl implements GCUBEScopeManager  {

	/** Object logger. */
	protected GCUBELog logger = new GCUBELog(this); //object logger
	
	/** Cache of scope information indexed by thread id. */
	protected Map<Thread,GCUBEScope>  scopes = Collections.synchronizedMap(new WeakHashMap<Thread,GCUBEScope>());
	
	/** The name of the manager. **/
	protected String name = this.getClass().getSimpleName();
	
	/**
	 * Creates a new instance.
	 */
	public GCUBEScopeManagerImpl(){}
	
	/**
	 * Creates a new instance with a given logger.
	 * @param logger the logger.
	 */
	public GCUBEScopeManagerImpl(GCUBELog logger) {
		this.logger=logger;
	}
	
	/**
	 * Returns the name with which the manager should log events.
	 * If it is not overridden, the manager logs for itself.
	 * @return the name.
	 */
	protected String getName() {return this.name;}
	
	/**
	 * Sets the name with which the manager should log events
	 * @param name the name
	 */
	protected void setName(String name) {
		this.name = name;logger.setPrefix(name);
	}
	
	/**
	 * {@inheritDoc}
	 * */
	public void setScope(GCUBEScope scope)  {
		//new provider holds the current scope
		ScopeProvider.instance.set(scope==null?null:scope.toString());
	}
	
	//TODO:ONLY FOR BINARY COMPATIBILITY: MUST ELIMINATE SOONER OR LATER
	public void setScope(Thread thread, GCUBEScope scope) throws IllegalScopeException {
		this.setScope(thread, new GCUBEScope[]{scope});
	}
	
	/**{@inheritDoc}*/
	public void setScope(Thread thread, GCUBEScope ... scope) {
		
		GCUBEScope s = scope.length==0?getScope():scope[0];
		
		if (s!=null)
			logger.trace("Setting scope "+s+" in "+thread);
		
		//this manager holds thread-assignments
		scopes.put(thread,s);
	}
	
	/**{@inheritDoc}*/
	public GCUBEScope getScope() {
		
		//first check this manager has it (was explicit thread-assignment)
		GCUBEScope scope = this.scopes.get(Thread.currentThread());
		
		//then check current scope with new provider
		if (scope==null) {
			String newScope = ScopeProvider.instance.get();
			if (newScope!=null)
				scope = GCUBEScope.getScope(newScope);
		}
		
		return scope;
	}
	
	
	/**{@inheritDoc}*/
	public synchronized void prepareCall(Remote remote, String clazz, String name, GCUBEScope ... scope) {
		
		String s = null;
		if (scope==null || scope.length==0) {
			s = (getScope()==null)?null:scope[0].toString();
			logger.trace("Preparing call to service "+clazz+","+name+" in current scope "+s);

		}
		else {
			s= (scope[0]==null)?null:scope[0].toString();
			logger.trace("Preparing call to service "+clazz+","+name+" in scope "+s);			
		}
	
		
		Stub stub = (Stub) remote;
		stub.clearHeaders();
		stub.setHeader(SCOPE_NS, CLASS_HEADER_NAME, clazz);
		stub.setHeader(SCOPE_NS, NAME_HEADER_NAME, name);
		stub.setHeader(SCOPE_NS, SCOPE_HEADER_NAME,s);

	}
}
