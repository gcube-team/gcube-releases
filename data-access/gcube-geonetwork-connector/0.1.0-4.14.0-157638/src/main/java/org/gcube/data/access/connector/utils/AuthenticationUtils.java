package org.gcube.data.access.connector.utils;

public class AuthenticationUtils {
	
	public final static String AUTHORIZATION = "Authorization";
	public final static String BASIC = "Basic";
	public final static String WHITESPACE = " ";
	
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	
	public final static String GCUBE_QUERY_STRING = "gcube-token";
	
	public final static String SDI = "SDI";
	public final static String SDI_SERVICE = "sdi-service";
	public final static String READY = "ready";
	
	//cache parameters
	public final static String TOKEN_CACHE = "token"; 
	public final static int MAX_ITEMS_CACHE = 5; //max items in the cache object (LRUMap removes the least recently used entry if an entry is added when full)
	public final static int TIME_TO_LIVE = 100; //in seconds
	public final static int TIMER_INTERVAL = 100; //in seconds

}
