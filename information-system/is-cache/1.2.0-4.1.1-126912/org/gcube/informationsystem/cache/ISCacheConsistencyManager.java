/**
 * 
 */
package org.gcube.informationsystem.cache;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF;

/**
 * Implementation of the {@link ISCacheConsistencyManagerMBean}
 * 
 * @author UoA
 * 
 */
public class ISCacheConsistencyManager implements
		ISCacheConsistencyManagerMBean {

	/*
	 * The set of registered CCMs
	 */
	private Set<String> CCManagerFQNames = null;

	/*
	 * The active CCM
	 */
	private String activeCCM = null;

	/*
	 * URLClassLoader used for remote class loading
	 */
	private URLClassLoader ucl = null;
	
	private SrvRegistry registry = null;

	/**
	 * public constructor
	 * 
	 * @param cache
	 *            the cache object which can either be empty or define some
	 *            default cached data.
	 */
	public ISCacheConsistencyManager(ISCache cache) {
		this.ucl = ((URLClassLoader) ClassLoader.getSystemClassLoader());
		this.setCCManagerFQNames(new HashSet<String>());
		this.registry = cache.getRegistry();
	}

	/**
	 * Register the CCMBean to the JMX server. Its object name is the class
	 * name. Before registering the CCM, first the a new CCM instance is created
	 * and its <i>initialize</i> method is invoked, passing the cache object as
	 * its parameter.
	 * 
	 * @param c
	 *            CCMBean class object
	 * @throws Exception
	 *             in case of error; most probably either error in the
	 *             <i>initiaze</i> method of the CCM instance, or at the MBean
	 *             registration
	 */
	private Object registerCCManager(Class c) throws Exception {
		Object cm = c.newInstance();
		Class[] car = new Class[1];
		car[0] = SrvRegistry.class;
		Object[] args = new Object[1];
		args[0] = this.registry;
		c.getMethod("initialize", car).invoke(cm, args);
		ISCacheMM.registerISMBean("org.gcube:type=" + this.registry.getScope().toString() + ",name=" + c.getName(), cm);
		return cm;
	}

	/**
	 * Unregister the CCMBean from the JMX server.<b>Note</b> that in the
	 * future, there will be a <i>finalization</i> method invocation on the CCM
	 * instance.
	 * 
	 * @param c
	 *            CCMBean class object
	 * @throws Exception
	 *             in case of error; most probably raised at the MBean
	 *             unregistration
	 */
	protected void unregisterCCManager(Class c) throws Exception {
		ISCacheMM.unregisterISMBean("org.gcube:type=" + this.registry.getScope().toString() + ",name=" + c.getName());
	}

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name 
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 * @see ISCacheConsistencyManagerMBean#addCCManager(String)
	 */
	public boolean addCCManager(String CCManagerFQName) throws Exception {
		//ClassLoader.getSystemClassLoader().loadClass(CCManagerFQName);
		try {
			Class.forName(CCManagerFQName).newInstance();
//			Class.forName("org.gcube.commons.is.cache.consistency.manager.poll.PollManager");
		} catch (Exception e) {
			throw new Exception(e);
		}
		return this.getCCManagerFQNames().add(CCManagerFQName);
	}

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name
	 * @param codebase URL of the jar file that contains the specified class  
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 * @see ISCacheConsistencyManagerMBean#addCCManagerDynamic(String, String)
	 */
	public boolean addCCManagerDynamic(String CCManagerFQName, String codebase)
			throws Exception {
		URL[] urls = new URL[this.ucl.getURLs().length + 1];
		for (int i = 0; i < this.ucl.getURLs().length; i++) {
			urls[i] = this.ucl.getURLs()[i];
		}
		urls[this.ucl.getURLs().length] = new URL(codebase);
		this.ucl = URLClassLoader.newInstance(urls);
		this.ucl.loadClass(CCManagerFQName);
		return this.getCCManagerFQNames().add(CCManagerFQName);
	}

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name
	 * @param codebase URL of the jar file that contains the specified class  
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 * @see ISCacheConsistencyManagerMBean#delCCManager(String)
	 */
	public boolean delCCManager(String CCManagerFQName) throws Exception {
		this.unregisterCCManager(ClassLoader.getSystemClassLoader().loadClass(
				CCManagerFQName));
		if(CCManagerFQName.equals(this.getActiveCCM()))
			this.nullifyActiveCCManager();
		return this.getCCManagerFQNames().remove(CCManagerFQName);
	}

	/**
	 * Get all registered Cache Consistency Managers (CCMs).
	 * @return all registered Cache Consistency Managers (CCMs)
	 * @see ISCacheConsistencyManagerMBean#getCCManagers()
	 */
	public String[] getCCManagers() {
		return this.getCCManagerFQNames().toArray(new String[0]);
	}

	/**
	 * Add new Cache Consistency Manager (CCM).
	 * @param CCManagerFQName CCM fully qualified (FQ) class name
	 * @param codebase URL of the jar file that contains the specified class  
	 * @return true if this set did not already contain the specified element; false otherwise
	 * @throws Exception in case of error; most probably due to non-accessibility to the specified class
	 * @see ISCacheConsistencyManagerMBean#setActiveCCManager(String)
	 */
	public void setActiveCCManager(String CCManagerFQName) throws Exception {
		if (this.getCCManagerFQNames().contains(CCManagerFQName) == false)
			throw new Exception("CCManager " + CCManagerFQName
					+ " does not exist.");
		Class c = null;
		if (this.getActiveCCM() != null) {
			/*c = ClassLoader.getSystemClassLoader().loadClass(
					this.getActiveCCM());*/
			c = Class.forName(CCManagerFQName);
			this.unregisterCCManager(c);
		}
		//c = ClassLoader.getSystemClassLoader().loadClass(CCManagerFQName);
		c = Class.forName(CCManagerFQName);
		
		// create the Cache Consistency instance, register it and add it to SrvRegistry
		this.registry.setConsistencyManagerImpl((ConsistencyManagerIF)this.registerCCManager(c));
		
		this.setActiveCCM(CCManagerFQName);
	}
	
	/**
	 * Nullify active CCManager
	 * @throws Exception in case of error
	 */
	public void nullifyActiveCCManager() throws Exception {
		this.registry.setConsistencyManagerImpl(null);
		this.setActiveCCM(null);
	}

	/**
	 * Setter of the {@link #CCManagerFQNames}
	 */
	void setCCManagerFQNames(Set<String> cCManagerFQNames) {
		CCManagerFQNames = cCManagerFQNames;
	}

	/**
	 * Getter of the {@link #CCManagerFQNames}
	 * 
	 * @return the CCManagerFQNames
	 */
	Set<String> getCCManagerFQNames() {
		return CCManagerFQNames;
	}

	/**
	 * Setter of the {@link #activeCCM}
	 */
	private void setActiveCCM(String activeCCM) {
		this.activeCCM = activeCCM;
	}

	/**
	 * Getter of the {@link #activeCCM}
	 * 
	 * @return the activeCCM
	 */
	private String getActiveCCM() {
		return activeCCM;
	}

	/**
	 * Get the active CCM.
	 * @return the active CCM
	 * @throws Exception in case of error
	 * @see ISCacheConsistencyManagerMBean#getActiveCCManager()
	 */
	public String getActiveCCManager() throws Exception {
		return this.getActiveCCM();
	}
}
