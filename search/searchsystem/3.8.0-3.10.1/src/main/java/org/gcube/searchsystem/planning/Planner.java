package org.gcube.searchsystem.planning;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import org.gcube.searchsystem.planning.exception.CQLTreeSyntaxException;
import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;

import search.library.util.cql.query.tree.GCQLNode;

/**
 * Classes that implement this interface, create a plan that will be used as input 
 * by the workflow layer  
 * @author vasilis verroios - DI NKUA
 *
 */
public interface Planner {

	/**
	 * Receives the root of a CQL tree, creates a plan for the workflow layer,
	 * and returns the root of this plan 
	 * @param root - the CQL tree root
	 * @return the root of the plan for the workflow layer
	 * @throws CQLTreeSyntaxException - when the input tree is not in a valid state
	 * (this case is possible when a chain of proprocessors is applied sequentially, 
	 * before planning)
	 */
	public PlanNode plan(GCQLNode root) throws CQLTreeSyntaxException, CQLUnsupportedException;
}
