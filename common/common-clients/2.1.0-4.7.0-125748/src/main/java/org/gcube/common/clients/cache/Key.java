package org.gcube.common.clients.cache;

import org.gcube.common.clients.queries.Query;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Keys for cross-service {@link EndpointCache}s comprised of a service name, a {@link Query}, and a scope.
 * 
 * <p>
 * Keys are <em>value objects</em> with informative string representations.
 * 
 * @author Fabio Simeoni
 *
 */
public final class Key {

	private final String name;
	private final Query<?> query;
	private final String scope;
	
	/**
	 * Creates a {@link Key} with a given service name and {@link Query}
	 * @param name the name
	 * @param query the query
	 * @return the key
	 */
	public static Key key(String name, Query<?> query) {
		return new Key(name, query);
	}
	
	//private
	private Key(String name, Query<?> query) {
		this.name = name;
		this.query = query;
		this.scope = ScopeProvider.instance.get();
	}

	@Override
	public String toString() {
		return "Key [name=" + name + ", query=" + query + ", scope=" + scope + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}


}
