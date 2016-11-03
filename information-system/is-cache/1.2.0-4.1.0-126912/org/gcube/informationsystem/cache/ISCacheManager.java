package org.gcube.informationsystem.cache;

import java.util.Hashtable;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.cache.consistency.manager.poll.PollManager;
import org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * ISCacheManager class is the initiator and accessor to the ISCache workflow.
 * It initiates the cache object, cache consistency manager MBean and registers
 * them to the JMX server.
 * 
 * @author UoA
 * 
 */
public class ISCacheManager {

	/**
	 * CCM Mbean. see {@link ISCacheConsistencyManager}
	 */
	private ISCacheConsistencyManager ccm;

	private ISCache cache;

	private GCUBEScope scope;

	static Hashtable<String, ISCacheManager> managers = new Hashtable<String, ISCacheManager>();
	
	private static GCUBELog log = new GCUBELog(ISCacheManager.class);

	public static void initialize() throws Exception {
		ISCacheMM.init();
		ISCacheRegistry r = new ISCacheRegistry();
		String obName = "org.gcube:type=ISCacheRegistry";
		ISCacheMM.registerISMBean(obName, r);
	}
	
	public static boolean addManager(GCUBEScope scope) throws Exception {
		log.info("Creating new CacheManager in scope: " + scope.getName() + "\t" + scope.toString());
		if (ISCacheManager.managers.get(scope.toString()) == null) {
			if (ISCacheManager.managers.put(scope.toString(),
					new ISCacheManager(scope)) == null)
				return true;
			return false;
		}
		return false;
	}
	
	public static ISCacheManager getManager(GCUBEScope scope) throws Exception {
		return ISCacheManager.managers.get(scope.toString());
	}

	public static boolean delManager(GCUBEScope scope) throws Exception {
		log.info("Deleting CacheManager in scope: " + scope.getName() + "\t" + scope.toString());
		ISCacheManager.managers.get(scope.toString()).unpublicISCache();
		ISCacheManager.managers.get(scope.toString())
				.unpublicISCacheConsistency();
		ISCacheManager.managers.get(scope.toString()).ccm
				.unregisterCCManager(Class.forName(ISCacheManager.managers
						.get(scope.toString()).ccm.getActiveCCManager()));
		if (ISCacheManager.managers.remove(scope.toString()) == null)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ISCacheManager) {

		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.scope.toString().hashCode();
	}

	/**
	 * register the {@link #cache}.
	 * 
	 * @throws Exception
	 *             in case of registration error.
	 */
	private void publicISCache() throws Exception {
		ISCacheMM.init();
		cache = new ISCache(this.scope);
		String obName = "org.gcube:type="
				+ this.cache.getRegistry().getScope().toString()
				+ ",name=ISCache";
		ISCacheMM.registerISMBean(obName, getCache());
	}

	void unpublicISCache() throws Exception {
		String obName = "org.gcube:type="
				+ this.cache.getRegistry().getScope().toString()
				+ ",name=ISCache";
		ISCacheMM.unregisterISMBean(obName);
	}

	/**
	 * register the CCM Mbean.
	 * 
	 * @throws Exception
	 *             in case of registration error
	 */
	private void publicISCacheConsistency() throws Exception {
		ISCacheMM.init();
		String obName = "org.gcube:type="
				+ this.cache.getRegistry().getScope().toString()
				+ ",name=ISCacheCA";
		ccm = new ISCacheConsistencyManager(this.cache);
		publicStdConsistencyManager(ccm);
		ISCacheMM.registerISMBean(obName, ccm);
	}

	void unpublicISCacheConsistency() throws Exception {
		String obName = "org.gcube:type="
				+ this.cache.getRegistry().getScope().toString()
				+ ",name=ISCacheCA";
		ISCacheMM.unregisterISMBean(obName);
	}

	/**
	 * Register the default CCM.
	 * 
	 * @see PollManagerMBean
	 * @see PollManager
	 * @param cc
	 *            the CCM MBean instance
	 * @throws Exception
	 *             in case of registration error
	 */
	private void publicStdConsistencyManager(ISCacheConsistencyManager cc)
			throws Exception {
		String CCManagerFQName = "org.gcube.informationsystem.cache.consistency.manager.poll.PollManager";
		cc.addCCManager(CCManagerFQName);
		cc.setActiveCCManager(CCManagerFQName);
	}

	public ISCacheManager(GCUBEScope scope) throws Exception {
		this.scope = scope;
		this.publicISCache();
		this.publicISCacheConsistency();
	}

	/**
	 * Test main method
	 * 
	 * @param args
	 *            main arguments
	 * @throws Exception
	 *             in case of error
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 0)
			printUsageAndExit();
		/*
		 * Srv srv1 = new Srv("MetadataManagement", "XMLIndexer"); Srv srv2 =
		 * new Srv("Personalisation", "UserProfileAccess"); Srv srv3 = new
		 * Srv("ContentManagement", "ContentManagementService"); Srv[] srvs = {
		 * srv1, srv2, srv3 }; ISCacheManager.prepareEnvironment(srvs);
		 */

		// ISCacheManager.prepareEnvironment();
		ISCacheManager.initialize();
//		ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));
//		ISCacheManager.addManager(GCUBEScope.getScope("/gcube/testing"));
//		ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));

//		Thread.sleep(20000);

//		ISCacheManager.delManager(GCUBEScope.getScope("/gcube/testing"));
//		ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));

//		ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).getCache().addFilterCriterion("MetadataManagement", "XMLIndexer", "STATEFULL", "/child::*[local-name()='AccessType']", "GCUBEDaix");
//		printEPRs(ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).getCache().getEPRsFor("MetadataManagement", "XMLIndexer", "FACTORY"));
//		printEPRs(ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).getCache().getEPRsFor("MetadataManagement", "XMLIndexer", "STATEFULL"));
//		printEPRs(ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).getCache().getEPRsFor("Personalisation", "UserProfileAccess", "SIMPLE"));
		
//		Thread.sleep(20000);
//		ISCacheManager.delManager(GCUBEScope.getScope("/gcube/devsec"));

		/*
		 * ISCacheManager.getCache().addSrvFilterCriteria("MetadataManagement",
		 * "XMLIndexer", "STATEFULL", "/child::*[local-name()='AccessType']",
		 * "GCUBEDaix");
		 */
		Thread.sleep(Long.MAX_VALUE);
	}

	private static void printEPRs(EndpointReference[] eprs) {
		for(int i=0;i<eprs.length;i++)
			System.out.println("::::::::" + eprs[i].toString());
	}
	
	/**
	 * printUsageAndExit
	 * 
	 */
	private static void printUsageAndExit() {
		System.err.println("Wrong number of arguments.\n" + "Usage: java\n"
				+ "Aborting...\n");
		System.exit(1);
	}

	/**
	 * @return the cache
	 */
	public ISCache getCache() {
		return cache;
	}
	
	/**
	 * @return the cache
	 */
	public ISCacheConsistencyManager getCCM() {
		return ccm;
	}

}
