package org.gcube.informationsystem.resourceregistry;

import org.gcube.informationsystem.resourceregistry.api.Query;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.glassfish.hk2.api.Factory;

public class QueryManagerFactory implements Factory<Query>{

	@Override
	public void dispose(Query arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Query provide() {
		return new Query() {
			
			@Override
			public String query(String query, int limit, String fetchPlan) throws InvalidQueryException {
				if (query.equals("error"))
					throw new InvalidQueryException("error in query");
				return "result";
			}
		};
	}

}
