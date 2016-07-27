package org.gcube.vomanagement.occi;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;

import java.util.HashMap;
import java.util.Map;

public class TemplatesCache {

  private static TemplatesCache instance;

  private Map<String, OSTemplate> osTemplates;
  private Map<String, ResourceTemplate> resourceTemplates;

  private TemplatesCache() {
    this.osTemplates = new HashMap<>();
    this.resourceTemplates = new HashMap<>();
  }

  /**
   * Singleton implementation.
   * @return the singleton instance
   */
  public static synchronized TemplatesCache getInstance() {
    if (instance == null) {
      instance = new TemplatesCache();
    }
    return instance;
  }

  public void cache(String providerId, OSTemplate template) {
    this.osTemplates.put(providerId + template.getId(), template);
  }

  public void cache(String providerId, ResourceTemplate template) {
    this.resourceTemplates.put(providerId + template.getId(), template);
  }

  public OSTemplate getOSTemplate(String providerId, String templateId) {
    return this.osTemplates.get(providerId + templateId);
  }

  public ResourceTemplate getResourceTemplate(String providerId, String tid) {
    return this.resourceTemplates.get(providerId + tid);
  }

}



/*
 * STARTED TO IMPLEMENT LOCAL CACHE
 */

//package org.gcube.vomanagement.occi;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.apache.log4j.net.SyslogAppender;
//import org.ehcache.Cache;
//import org.ehcache.CacheManager;
//import org.ehcache.PersistentCacheManager;
//import org.ehcache.Status;
//import org.ehcache.config.CacheConfiguration;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.CacheManagerBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.EntryUnit;
//import org.ehcache.config.units.MemoryUnit;
//import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
//import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
//import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
//
//public class TemplatesCache {
//
//  private static TemplatesCache instance;
//
// //private Map<String, OSTemplate> osTemplates;
// //private Map<String, ResourceTemplate> resourceTemplates;
//  public Cache<String, OSTemplate> osTemplates;
//  public Cache<String, ResourceTemplate> resourceTemplates;
//
//  	
//  private TemplatesCache() {
////        this.osTemplates = new HashMap<>();
////        this.resourceTemplates = new HashMap<>();
//	  	  
//		/****************************************************************************************/
//	  	  
//	    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("preConfigured",
//				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ResourceTemplate.class)).build();
//	    cacheManager.init();
//	    
//		CacheManager cacheManager2 = CacheManagerBuilder.newCacheManagerBuilder().withCache("preConfigured",
//				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, OSTemplate.class)).build();
//		cacheManager2.init();
//		
//		resourceTemplates = cacheManager.createCache("resourceTemplates",
//				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ResourceTemplate.class).build());
//	    		cacheManager.getCache("resourceTemplates", String.class, ResourceTemplate.class);
//
//		osTemplates = cacheManager2.createCache("osTemplates",
//				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, OSTemplate.class).build());	
//				cacheManager2.getCache("osTemplates", String.class, OSTemplate.class);
//
//		/****************************************************************************************/
//	  	
//	  String storagePath = "resourceTemplatesCache";
//		PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//	            .with(CacheManagerBuilder.persistence(storagePath))
//	            .withCache("resourceTemplates",
//	                    CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ResourceTemplate.class)
//	                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
//	                                    .heap(1000, EntryUnit.ENTRIES)
//	                                    .offheap(10, MemoryUnit.MB)
//	                                    .disk(100, MemoryUnit.MB, true)
//	                            ))
//	                           // .buildConfig(String.class, ResourceTemplate.class))
//	            .build();
//		System.out.println(persistentCacheManager.getStatus());
//		  //System.out.println(persistentCacheManager.getCache("resourceTemplates",String.class,ResourceTemplate.class));
//		  //persistentCacheManager.init();
//		  //persistentCacheManager.close();
//			
//		String storagePath2 = "osTemplatesCache";
//		PersistentCacheManager persistentCacheManager2 = CacheManagerBuilder.newCacheManagerBuilder()
//	            .with(CacheManagerBuilder.persistence(storagePath2))
//	            .withCache("osTemplates",
//	                    CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, OSTemplate.class)
//	                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
//	                                    .heap(1000, EntryUnit.ENTRIES)
//	                                    .offheap(10, MemoryUnit.MB)
//	                                    .disk(100, MemoryUnit.MB, true)
//	                              
//	                            ))
//	                            //.buildConfig(String.class, OSTemplate.class))
//	            .build();
//		//persistentCacheManager2.close();
//		
//		}  
//  
//	/****************************************************************************************/
//  
//  /**
//   * Singleton implementation.
//   * @return the singleton instance
//   */
//  public static synchronized TemplatesCache getInstance() {
//    if (instance == null) {
//      instance = new TemplatesCache();
//    }
//    return instance;
//  }
//
//  public void cache(String providerId, OSTemplate template) {
//    this.osTemplates.put(providerId + template.getId(), template);
//  }
//
//  public void cache(String providerId, ResourceTemplate template) {
//    this.resourceTemplates.put(providerId + template.getId(), template);
//  }
//
//  public OSTemplate getOSTemplate(String providerId, String templateId) {
//    return this.osTemplates.get(providerId + templateId);
//  }
//
//  public ResourceTemplate getResourceTemplate(String providerId, String tid) {
//	  return this.resourceTemplates.get(providerId + tid);
//  }
//
//
//  public static void main(String[] args) {
//	TemplatesCache a = new TemplatesCache();
//	ResourceTemplate b = new ResourceTemplate();
//	ResourceTemplate c = new ResourceTemplate();
//
////b.setId("http://fedcloud.egi.eu/occi/compute/flavour/1.0#small");	
////	c.setId("id");
//////	
////	a.cache("https://carach5.ics.muni.cz:11443", b);
////	a.cache("id", c);
//
//	System.out.println(a.getResourceTemplate("https://carach5.ics.muni.cz:11443", "http://fedcloud.egi.eu/occi/compute/flavour/1.0#small"));
//	System.out.println(a.getResourceTemplate("id", "id"));
//
//  } 
//}


