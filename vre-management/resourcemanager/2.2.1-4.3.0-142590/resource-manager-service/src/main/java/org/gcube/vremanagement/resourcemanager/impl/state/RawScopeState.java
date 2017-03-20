package org.gcube.vremanagement.resourcemanager.impl.state;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.types.MultiKeysMap;

/**
 * 
 * The scope state. 
 * 
 * While {@link ScopeState} incorporates the behavior of a scope state, this  separate and 
 * minimal class contains only the core information to serialize and nothing else.
 * This way, the serialized data are separated from the state  behavior, by minimizing 
 * the need of future changes in the state class.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class RawScopeState {

	/** the scope this state belongs to*/
	protected String scope;
	
	/** the list of resources */
	protected MultiKeysMap<String, String, ScopedResource> resources;
	
	//Open structure for information to store. By using it, we will avoid to broke XSTREAM serialization
	//when we need to add more information to the class
	/** any data belonging the state worthy to be serialized*/
	protected Map<String, Object> data;
		
	protected RawScopeState () {}
	
	/**
	 * Initializes the state
	 * @param scope the scope this state belongs to 
	 */
	protected void initialize(final GCUBEScope scope) {
		resources = new MultiKeysMap<String, String, ScopedResource>();
		data = new HashMap<String, Object>();
		this.scope = scope.toString();
	}

	/**
	 * @return the scope this state belongs to
	 */
	public GCUBEScope getScope() {
		return GCUBEScope.getScope(this.scope);
	}
	
	
}
