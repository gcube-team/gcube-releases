/**
 * <h4>Introduction</h4>
 * <p>
 * The only artifacts users need to pay attention to are the {@link org.gcube.informationsystem.cache.ISCache} 
 * and {@link org.gcube.informationsystem.cache.ISCacheManager} classes. In the beginning use the {@link org.gcube.informationsystem.cache.ISCacheManager#initialize()}
 * method, which initializes the ISCache environment.
 * Users can access the {@link org.gcube.informationsystem.cache.ISCache} object, using {@link org.gcube.informationsystem.cache.ISCacheManager#getCache()} method. Then 
 * they can invoke the methods defined in the {@link org.gcube.informationsystem.cache.ISCache} class and manage the cache information.
 * There are 2 noteworthy factors regarding the cache architecture.
 * <dl>
 * <dt><b>It's inherently, completely, uniquely, utterly dynamic</b></dt>
 * <dd>cache consistency components may be added/deleted at runtime without affecting the system. 
 * In matter of fact, one can change the consistency component at runtime keeping all of its previous cached information.
 * From the user perspective all the mechanics are completely transparent.</dd>
 * <dt><b>It's JMX-enabled</b></dt>
 * <dd>This means that ISCache has all the advantages of JMX, in terms of management and monitoring</dd>
 * </dl>
 * <h4>Examples of Usage</h4>
 * <h5>Simple Usage</h5>
 * <p>
 * <code>
 * ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));<br>
 * ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));<br>
 * ISCacheManager.delManager(GCUBEScope.getScope("/gcube/testing"));<br>
 * ISCacheManager.addManager(GCUBEScope.getScope("/gcube/devsec"));<br>
 * ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).cache.getEPRsFor("MetadataManagement", "XMLIndexer", "FACTORY") :: String[]<br>
 * ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).cache.getEPRsFor("MetadataManagement", "XMLIndexer", "STATEFULL") :: String[]<br>
 * ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).cache.addFilterCriterion("MetadataManagement", "XMLIndexer", "STATEFULL", "/child::*[local-name()='AccessType']", "GCUBEDaix");<br>
 * ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).cache.getEPRsFor("MetadataManagement", "XMLIndexer", "STATEFULL") :: String[]<br>
 * ISCacheManager.getManager(GCUBEScope.getScope("/gcube/devsec")).cache.getEPRsFor("Personalisation", "UserProfileAccess", "SIMPLE") :: String[]<br>
 * </code><br>
 * <h5>Management & Monitoring</h5>
 * <p>
 * In order to see the current state of ISCache (registered services, corresponding RIs, execution statistics, etc), 
 * you can use JConsole (just type 'jconsole' in your command prompt. ISCache is fully JMX-enabled and thus can be 
 * configured and monitored at runtime.
 */
package org.gcube.informationsystem.cache;