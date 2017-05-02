package org.gcube.portlets.user.homelibrary.jcr.manager;

import org.gcube.common.homelibary.model.util.MemoryCache;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.jcr.home.JCRHome;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * @author Crunchify.com
 */

public class CrunchifyInMemoryCacheTest {

	public static void main(String[] args) throws InterruptedException, InternalErrorException, HomeNotFoundException, UserNotFoundException {
		ScopeProvider.instance.set("/gcube");
		CrunchifyInMemoryCacheTest crunchifyCache = new CrunchifyInMemoryCacheTest();

		System.out.println("\n\n==========Test1: crunchifyTestAddRemoveObjects ==========");
		crunchifyCache.crunchifyTestAddRemoveObjects();
		System.out.println("\n\n==========Test2: crunchifyTestExpiredCacheObjects ==========");
		crunchifyCache.crunchifyTestExpiredCacheObjects();
		System.out.println("\n\n==========Test3: crunchifyTestObjectsCleanupTime ==========");
		crunchifyCache.crunchifyTestObjectsCleanupTime();
	}

	private void crunchifyTestAddRemoveObjects() throws InternalErrorException, HomeNotFoundException, UserNotFoundException {

		// Test with timeToLiveInSeconds = 200 seconds
		// timerIntervalInSeconds = 500 seconds
		// maxItems = 6
		//        CrunchifyInMemoryCache<String, Home> cache = new CrunchifyInMemoryCache<String, Home>(200, 500, 6);



		String user = "valentina.marioli";
		JCRHome home = (JCRHome) HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);

		MemoryCache<String, Home> cache = home.getHomeManager().getCache();
		System.out.println("Cache.size(): " + cache.size());
		
		String user1 = "roberto.cirillo";
		Home home1 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user1);



		String user2 = "massimiliano.assante";
		Home home2 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user2);		



		System.out.println("3 Cache Object Added.. cache.size(): " + cache.size());
		cache.remove(user);

		System.out.println("One object removed.. cache.size(): " + cache.size());

		String user3 = "francesco.mangiacrapa";
		Home home3 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user3);
		cache.put(user3, home3);


		System.out.println("Two objects Added but reached maxItems.. cache.size(): " + cache.size());
		cache.cleanup();

	}

	private void crunchifyTestExpiredCacheObjects() throws InterruptedException, InternalErrorException, HomeNotFoundException, UserNotFoundException {

		// Test with timeToLiveInSeconds = 1 second
		// timerIntervalInSeconds = 1 second
		// maxItems = 10
		//        CrunchifyInMemoryCache<String, Home> cache = new CrunchifyInMemoryCache<String, Home>(1, 1, 10);

		
		
		String user = "valentina.marioli";
		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);
		
		MemoryCache<String, Home> cache = home.getHomeManager().getCache();
		System.out.println("Cache.size(): " + cache.size());
	
		String user1 = "roberto.cirillo";
		Home home1 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user1);

	
		// Adding 3 seconds sleep.. Both above objects will be removed from
		// Cache because of timeToLiveInSeconds value
		Thread.sleep(3000);

		System.out.println("Two objects are added but reached timeToLive. cache.size(): " + cache.size());
		cache.cleanup();

	}

	private void crunchifyTestObjectsCleanupTime() throws InterruptedException, InternalErrorException, HomeNotFoundException, UserNotFoundException {

		// Test with timeToLiveInSeconds = 100 seconds
		// timerIntervalInSeconds = 100 seconds
		// maxItems = 5

		//        CrunchifyInMemoryCache<String, Home> cache = new CrunchifyInMemoryCache<String, Home>(5, 5, 2);
		//        cache.cleanup();
	
		//        for (int i = 0; i < size; i++) {

		String user = "valentina.marioli";
		Home home = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user);


		MemoryCache<String, Home> cache = home.getHomeManager().getCache();
		System.out.println("Cache.size(): " + cache.size());
		
		String user1 = "roberto.cirillo";
		Home home1 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user1);



		String user2 = "massimiliano.assante";
		Home home2 = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(user2);		


		//        }

		Thread.sleep(200);

		cache.cleanup();

		System.out.println("Cache.size(): " + cache.size());
	}
}
