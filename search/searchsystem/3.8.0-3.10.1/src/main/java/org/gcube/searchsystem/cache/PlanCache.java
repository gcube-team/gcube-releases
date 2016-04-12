package org.gcube.searchsystem.cache;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;
import search.library.util.cql.query.tree.GCQLNode;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class PlanCache {
	
	private Cache cache = null;

	public PlanCache(Cache cache) {
		this.cache = cache;
	}

	/**
	 * returns a cached plan for a query
	 * @param cqlQuery - the query in string format
	 * @param head - the query, parsed 
	 * @return the plan or null if there is no cached plan
	 */
	public PlanNode searchForCachedPlans(String cqlQuery, GCQLNode head) {
		
		//TODO: explore possibilities for a more sophisticated search 
		//in queries--cached--plans set. Take into account the fact that
		//this search operation should be very fast.
		Element e = cache.get(cqlQuery);
		if(e == null) {
			return null;
		} else {
			return (PlanNode) e.getObjectValue();
		}
		
	}

	public void addPlan(String cqlQuery, GCQLNode head, PlanNode newPlan) {
		Element e = new Element(cqlQuery, newPlan);
		cache.put(e);
	}
	
	public void clear() {
		cache.removeAll();
	}

}
