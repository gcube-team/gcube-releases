package org.gcube.common.scope.impl;

import static org.gcube.common.scope.impl.ScopeBean.Type.*;

import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;

/**
 * A {@link ServiceMap} that forwards requests to {@link ServiceMap}s associated
 * with the current scope.
 * <p>
 * At construction time, it configures itself with all the service maps found
 * in the classpath (excluding URLs available to primordial and extension
 * classloader). Recognises service maps as resources whose names match a
 * {@link ServiceMapScanner#mapConfigPattern}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ScopedServiceMap implements ServiceMap {

	private final Map<String, ServiceMap> maps;


	public ScopedServiceMap() {
		maps = ServiceMapScanner.maps();
	}

	@Override
	public String scope() {
		return currentMap().scope();
	}
	
	@Override
	public String version() {
		return currentMap().version();
	}

	@Override
	public String endpoint(String service) throws IllegalArgumentException,IllegalStateException {

		return currentMap().endpoint(service);
	}
	
	//helper
	private ServiceMap currentMap() {
		
		String currentScope = ScopeProvider.instance.get();
		
		if (currentScope==null)
			throw new IllegalStateException("current scope is undefined");
		
		ScopeBean bean = new ScopeBean(currentScope);
		
		if(bean.is(VRE))
			currentScope = bean.enclosingScope().toString();

		ServiceMap map = maps.get(currentScope);
		
		if (map==null)
			throw new IllegalStateException("a map for "+currentScope+" is undefined");
		
		return map;
	}
	
}
