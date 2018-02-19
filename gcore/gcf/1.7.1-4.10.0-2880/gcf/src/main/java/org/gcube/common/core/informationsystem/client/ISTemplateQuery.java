package org.gcube.common.core.informationsystem.client;


/**
 * A specialisation of {@link ISQuery} for queries instantiated from fixed templates.
 * 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 * @param <RESULT> the type of the query results.
 */
public interface ISTemplateQuery<RESULT> extends ISQuery<RESULT> {
	
	/**
	 * Adds one or more atomic conditions on the result to the query.
	 * @param conditions the conditions. 
	 */
	public void addAtomicConditions(AtomicCondition ... conditions);
	
	/**
	 * Adds a free-form condition on each result to the query. 
	 * By convention, the <code>$result</code> should be used 
	 * as the variable bound to the result.
	 * @param condition the condition.
	 */
	public void addGenericCondition(String condition);
	
	/**
	 * Clears all conditions on the query
	 */
	public void clearConditions();
	
}
