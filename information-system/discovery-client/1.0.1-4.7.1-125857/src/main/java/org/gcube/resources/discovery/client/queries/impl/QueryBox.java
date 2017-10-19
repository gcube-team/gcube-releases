package org.gcube.resources.discovery.client.queries.impl;

import static org.gcube.resources.discovery.client.queries.impl.Utils.*;

import org.gcube.resources.discovery.client.queries.api.Query;

public class QueryBox implements Query {

	private final String expression;
	
	public QueryBox(String expression) {
		notNull("expression",expression);
		this.expression=expression;
	}
	
	public String expression() {
		return expression;
	}
	
	@Override
	public String toString() {
		return super.toString()+"="+expression();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression() == null) ? 0 : expression().hashCode());
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
		QueryBox other = (QueryBox) obj;
		if (expression() == null) {
			if (other.expression() != null)
				return false;
		} else if (!expression().equals(other.expression()))
			return false;
		return true;
	}
	
	
	
}
