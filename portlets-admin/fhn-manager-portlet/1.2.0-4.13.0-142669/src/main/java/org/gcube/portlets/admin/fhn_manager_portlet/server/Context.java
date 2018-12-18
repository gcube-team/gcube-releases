package org.gcube.portlets.admin.fhn_manager_portlet.server;

import javax.servlet.ServletContext;

import lombok.Data;
import lombok.NonNull;

@Data
public class Context {

	
	
	
	
	
	private static Context singleton=null;
	
	
	public static Context get(){
		return singleton;
	}
	
	
	public static synchronized void load(ServletContext servletContext){
		if(singleton==null)
		singleton=new Context(
				Long.parseLong(servletContext.getInitParameter("cache.nodes.TTL")),
				Long.parseLong(servletContext.getInitParameter("cache.templates.TTL")),
				Long.parseLong(servletContext.getInitParameter("cache.providers.TTL")),
				Long.parseLong(servletContext.getInitParameter("cache.profiles.TTL"))
				);
	}
	
	
	
	
	// CACHE
	@NonNull
	private Long nodesCacheTTL;
	@NonNull
	private Long templatesCacheTTL;
	@NonNull
	private Long providersCacheTTL;
	@NonNull
	private Long serviceProfilesCacheTTL;
	


}
