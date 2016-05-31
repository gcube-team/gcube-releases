package org.gcube.index;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class SmartfishWebApp extends Application {

	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private Set<Object> singletons = new HashSet<Object>();

	public SmartfishWebApp() {
		this.singletons.add(Index.getInstance());
		this.singletons.add(new DataServerServlet());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return this.classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return this.singletons;
	}
}
