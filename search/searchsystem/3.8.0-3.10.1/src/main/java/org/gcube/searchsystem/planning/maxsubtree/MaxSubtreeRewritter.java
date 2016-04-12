package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.searchsystem.planning.commonvocabulary.Constants;
import org.gcube.searchsystem.planning.commonvocabulary.IndexRelationCommonSemantics;
import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;
import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer.GCQLCondition;

import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.ModifierSet;

public class MaxSubtreeRewritter {

	/**
	 * the logger for this class
	 */
	private Logger logger = LoggerFactory.getLogger(MaxSubtreeRewritter.class.getName());
	
	private GCQLNode root;
	
	private Vector<ModifierSet> projections = new Vector<ModifierSet>();
	
	public MaxSubtreeRewritter(GCQLNode root) {
		this.root = root;
	}
	
	public ArrayList<AndTree> rewrite() throws CQLUnsupportedException{
		
		//first of all check if the root has a projection
		if(root instanceof GCQLProjectNode)
		{
			setProjections(((GCQLProjectNode)root).getProjectIndexes());
			root = ((GCQLProjectNode) root).subtree;
		}
		
		//push the NOT operations to the leaves
		//and push the OR operations to the root
		//in the result an OR operation will 
		//connect trees containing
		//only AND conditions
		logger.trace("Starting push Not Down Or Up");
		
		long before = Calendar.getInstance().getTimeInMillis();
		ArrayList<Set<GCQLCondition>> trees = TreeTransformer.pushNotDownOrUp(root, false);
		long after = Calendar.getInstance().getTimeInMillis(); 
		logger.info("PushNotDownOrUp returned after: " + (after - before) + " millis");
		
		logger.trace("trees after push Not Down Or Up");
		for(Set<GCQLCondition> tree : trees) {
			logger.trace("set of conditions: ");
			for(GCQLCondition cond : tree) {
				logger.trace("Not: " + cond.not + ", term: " + cond.term.toCQL());
			}
		}
		
		//check the validity of the result
		logger.trace("check the validity of the trees");
		
		before = Calendar.getInstance().getTimeInMillis();
		checkValidityTrees(trees);
		after = Calendar.getInstance().getTimeInMillis();
		logger.info("CheckValidityTrees returned after: " + (after - before) + " millis");
		
		ArrayList<AndTree> results = new ArrayList<AndTree>(trees.size());
		
		logger.trace("detecting languages-collections for AndTrees");
		
		before = Calendar.getInstance().getTimeInMillis();
		for(Set<GCQLCondition> tree : trees) {
			
			//for each AND-tree detect the collections
			//and languages it refers to
			AndTree currentResult = detectCollectionLang(tree);
			if(currentResult == null) {
				continue;
			}
			results.add(currentResult);			
		}		
		after = Calendar.getInstance().getTimeInMillis();
		logger.info("DetectCollectionLang returned after: " + (after - before) + " millis");
		
		return results;
	}

	private AndTree detectCollectionLang(Set<GCQLCondition> tree) throws CQLUnsupportedException{
		AndTree result = new AndTree();
		//for each condition check if it refers to a collection or a language
		for(GCQLCondition condition : tree) {
			
			logger.trace("Not: " + condition.not + ", term: " + condition.term.toCQL());
			logger.trace("Modifiers size: " + condition.getTerm().getRelation().getModifiers().size());
			
			if (!IndexRelationCommonSemantics.examineCondition(condition, result)) {
				//this means that the collections and languages in the AndTree
				//are contradicting, and the tree would not provide any result
				logger.trace("Collections and languages are contraddicting for this tree:");
				logger.trace("set of conditions: ");
				for(GCQLCondition cond : tree) {
					logger.trace("Not: " + cond.not + ", term: " + cond.term.toCQL());
				}
				return null;
			}			
		}
		
		return result;
	}

	private void checkValidityTrees(ArrayList<Set<GCQLCondition>> trees) throws CQLUnsupportedException{
		
		//Our final result is an OR of AND sets.
		//If any of the AND sets has only NOT conditions then 
		//part of the results we are asking for, 'says': " I want
		// the universe except for these documents ". Can our 
		//supported CQL syntax give a tree thats is equivalent 
		//to such a result? I think not.. If this happens somehow
		//through an unsupported exception (must investigate
		//why it happened)
		outer: for(Set<GCQLCondition> andConditionSet : trees) {
			for(GCQLCondition cond : andConditionSet) {
				if(cond.not == false) {
					continue outer;
				}
			}
			throw new CQLUnsupportedException("tree: " + root.toCQL() + ", indicates an pure-NOT part. pure-NOT parts are not supported");
		}
		
	}

	public Vector<ModifierSet> getProjections() {
		return projections;
	}
	
	public void setProjections(Vector<ModifierSet> projections) {
		
		//if one of the projections is the wild card then remove everything else and keep the wild card
		boolean foundWild = false;
		for(ModifierSet current : projections) {
			if(current.getBase().equals(MaxSubtreePlanner.WILDCARD)) {
				foundWild = true;
				break;
			}
		}
		
		//if there is a wildcard
		if(foundWild)
		{
			Vector<ModifierSet> sortbyParts = new Vector<ModifierSet>();
			
			boolean inSortBy = false;
			for(ModifierSet current : projections) {
				String proj = current.getBase();
				
				if (proj.equalsIgnoreCase("sortby")){
					inSortBy = true;
				}
				if (inSortBy){
					sortbyParts.add(current);
					if (proj.equalsIgnoreCase("asc") || proj.equalsIgnoreCase("desc"))
						inSortBy = false;
				}
			}
			
			projections.clear();
			projections.add(new ModifierSet(MaxSubtreePlanner.WILDCARD));
			projections.addAll(sortbyParts);
		}
		
		this.projections = projections;
	}

}
