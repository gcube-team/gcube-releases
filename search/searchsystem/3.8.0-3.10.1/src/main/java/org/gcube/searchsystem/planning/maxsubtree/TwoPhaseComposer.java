package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Vector;

import org.gcube.searchsystem.planning.commonvocabulary.IndexRelationCommonSemantics;
import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;
import org.gcube.searchsystem.planning.maxsubtree.GeneralTreeNode.NodeType;
import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer.GCQLCondition;
import search.library.util.cql.query.tree.ModifierSet;

public class TwoPhaseComposer {

	/**
	 * the logger for this class
	 */
	private Logger logger = LoggerFactory.getLogger(TwoPhaseComposer.class.getName());
	
	private ArrayList<AndTree> subtrees;
	private Vector<ModifierSet> projections;
	
	public TwoPhaseComposer(ArrayList<AndTree> subtrees, Vector<ModifierSet> projections) {
		this.subtrees = subtrees;
		this.projections = projections;
	}

	public GeneralTreeNode compose() throws CQLUnsupportedException{
		
		//find the subtrees, in which each factor is met
		HashMap<GCQLCondition, Set<Integer>> factorTrees = createFactorMap(this.subtrees);
		Set<Integer> treesLeft =  new HashSet<Integer>();
		for(int i=0; i< this.subtrees.size(); i++) {
			treesLeft.add(new Integer(i));
		}
		
		return allInOnePhase(treesLeft, subtrees, factorTrees);
	}
	
	private GeneralTreeNode allInOnePhase(Set<Integer> treesLeft,
			ArrayList<AndTree> currentSubtrees,
			HashMap<GCQLCondition, Set<Integer>> factorTrees) throws CQLUnsupportedException{
		
		logger.trace("AllInOnePhase called for trees: " + Arrays.toString(treesLeft.toArray(new Integer[treesLeft.size()])));
		
		for(AndTree tree : currentSubtrees) {
			logger.trace("Tree for collection: " + tree.collection + ", language: " + tree.language);
			for(int i=0; i<tree.conditions.size(); i++) {
				GCQLCondition condition = tree.conditions.get(i);
				logger.trace("Not: " + condition.not + ", term: " + condition.term.toCQL());
				logger.trace("Sources: " + Arrays.toString(tree.sources.get(i).toArray(new String[tree.sources.get(i).size()])));
			}
		}
		
		ArrayList<Integer> treesToBeExamined = new ArrayList<Integer>(treesLeft);
		ArrayList<Integer> treesForFactorization = new ArrayList<Integer>();
		
		//these lists will contain 
		//a)the trees that are grouped together for being answered
		//by One source
		//b)the respective sources
		ArrayList<ArrayList<Integer>> groupedTrees = new ArrayList<ArrayList<Integer>>();
		ArrayList<Set<String>> groupedSources = new ArrayList<Set<String>>();
		
		//examine all the trees
		while(treesToBeExamined.size() > 0) {
			Integer currentTree = treesToBeExamined.get(0);
			//the tree to be examined is removed from the head of the list
			treesToBeExamined.remove(0);
			HashSet<String> currentSources = getSourcesSatisfyingTree(currentSubtrees.get(currentTree));
			//if no source can answer the whole tree
			if(currentSources == null) {
				treesForFactorization.add(currentTree);
				continue;
			}
			ArrayList<Integer> currentGroupedTrees = new ArrayList<Integer>();
			currentGroupedTrees.add(currentTree);
			
			Iterator<Integer> treesIter = treesToBeExamined.iterator();
			//examine which trees can be answered by the same sources with the current tree
			while(treesIter.hasNext()) {
				Integer tmpTree = treesIter.next();
				HashSet<String> tmpSources = getSourcesSatisfyingTree(currentSubtrees.get(tmpTree));
				//if no source can answer the whole tree
				if(tmpSources == null) {
					treesForFactorization.add(tmpTree);
					treesIter.remove();
					continue;
				}
				
				//find out which are the common sources
				tmpSources.retainAll(currentSources);
				if(tmpSources.size() == 0) {
					//if there is no common source the tree will be examined later again
					continue;
				} else {
					//else this tree is put to this group and won't be examined again
					currentGroupedTrees.add(tmpTree);
					treesIter.remove();
					currentSources = tmpSources;
				}
			}
			//add the grouped trees and the respective sources in the set of overall groups
			groupedTrees.add(currentGroupedTrees);
			groupedSources.add(currentSources);
		}
		
		logger.trace("trees for factorization: " + Arrays.toString(treesForFactorization.toArray(new Integer[treesForFactorization.size()])));
		
		//An OR node will connect the groups
		GeneralTreeNode orNode = null;
		GeneralTreeNode leaf = null;
		if(groupedTrees.size() > 1) {
			orNode = new GeneralTreeNode();
			orNode.type = NodeType.OR;
		}
		//for each group create a leaf node
		for(int i=0; i<groupedTrees.size(); i++) {
			
			ArrayList<Integer> treeGroup = groupedTrees.get(i);
			Set<String> sourceGroup = groupedSources.get(i);
			
			logger.trace("group: " + Arrays.toString(treeGroup.toArray(new Integer[treeGroup.size()])));
			logger.trace("sources for this group: " + Arrays.toString(sourceGroup.toArray(new String[sourceGroup.size()])));
			
			//create a leaf node
			leaf = new GeneralTreeNode();
			leaf.type = NodeType.LEAF;
			leaf.sources = sourceGroup;
			//collect the AndTrees
			ArrayList<AndTree> andTrees = new ArrayList<AndTree>();
			for(Integer treeNum : treeGroup) {
				andTrees.add(currentSubtrees.get(treeNum));
			}
			leaf.colLangs = new HashMap<String, HashSet<String>>();
			leaf.gcql = IndexRelationCommonSemantics.createGCQLNodeFromAndTrees(andTrees, leaf.colLangs);
			
			logger.trace("CQL: " + leaf.gcql);
			
			//if there are more than one leaves, store it in the children of the OR node
			if(orNode != null) {
				orNode.children.add(leaf);
			}
		}
		
		//if there are trees for factorization
		GeneralTreeNode factorNode = null;
		if(treesForFactorization.size() > 0) {
			//create the new current subtrees for running the FactorizationPhase
			ArrayList<AndTree> newCurrentSubtrees = new ArrayList<AndTree>();
			for(int i=0; i<this.subtrees.size(); i++) {
				newCurrentSubtrees.add(new AndTree());
			}
			for(Integer treeSeqNum : treesForFactorization) {
				newCurrentSubtrees.remove(treeSeqNum.intValue());
				//createAndTree needs no factors to remove from the existing AndTree
				newCurrentSubtrees.add(treeSeqNum, createAndTree(new ArrayList<GCQLCondition>(), currentSubtrees.get(treeSeqNum)));
			}
			//apply factorization phase
			factorNode = factorizationPhase(new HashSet<Integer>(treesForFactorization), createFactorMap(newCurrentSubtrees), newCurrentSubtrees);
			//if there are no leaves
			if(orNode == null && leaf == null) {
				//return the output of the factorization
				return factorNode;
			} else if(orNode == null && leaf != null) {
				//if there is only one leaf node
				orNode = new GeneralTreeNode();
				orNode.type = NodeType.OR;
				orNode.children.add(leaf);
				orNode.children.add(factorNode);
			} else if(orNode != null) {
				//if there are more than one leaves
				orNode.children.add(factorNode);
			}
		}
		
		//if factorization is applied the method will already have returned
		//if there is only one leaf node
		if(orNode == null) {
			if(leaf == null) {
				throw new CQLUnsupportedException("While applying the AllInOnePhase we ended up with no output");
			}
			
			logger.trace(leaf.toString());
			
			return leaf;
		} else {
			
			logger.trace(orNode.toString());
			
			return orNode;
		}
	}

	private HashSet<String> getSourcesSatisfyingTree(AndTree andTree) {
		HashSet<String> result = null;
		//find the common subset of sources for all the conditions
		for(Set<String> sources : andTree.sources) {
			if(result == null) {
				result = new HashSet<String>(sources);
			} else {
				result.retainAll(sources);
				//if no sources are left after this condition
				if(result.size() == 0) {
					return null;
				}
			}
		}
		return result;
	}

	private HashMap<GCQLCondition, Set<Integer>> createFactorMap(ArrayList<AndTree> currentSubtrees) {
		HashMap<GCQLCondition, Set<Integer>> factorTrees = new HashMap<TreeTransformer.GCQLCondition, Set<Integer>>();
		int i = 0;
		for(AndTree tree : currentSubtrees) {
			for(GCQLCondition factor : tree.getConditions()) {
				Set<Integer> referencedIn = factorTrees.get(factor);
				if(referencedIn == null) {
					referencedIn = new HashSet<Integer>();
					factorTrees.put(factor, referencedIn);
				}
				referencedIn.add(new Integer(i));				
			}
			i++;
		}
		return factorTrees;
	}

	/**
	 * This method receives trees that can NOT be answered by a unique source, and tries to find common condition factors
	 * in them, in order to gather conditions that can be answered by unique sources.
	 * @param treesLeft - the trees left (can NOT be answered by a unique source)
	 * @param factorTrees - the factor correspondence for the trees left
	 */
	private GeneralTreeNode factorizationPhase(Set<Integer> treesLeft, HashMap<GCQLCondition, Set<Integer>> factorTrees, ArrayList<AndTree> currentSubtrees) throws CQLUnsupportedException{
		
		//flag indicating when the following loop is applied for the first time
		boolean firstStep = true;
		
		//this node is used during the loop for the part of the query answered in each step
		GeneralTreeNode matchedFactorsNode = null;
		
		GeneralTreeNode generalOrNode = null;
		
		//while there are more trees for factorization in the first level
		while(true) {

			logger.trace("FactorizationPhase applied for trees: " + Arrays.toString(treesLeft.toArray(new Integer[treesLeft.size()])));
			
			for(AndTree tree : currentSubtrees) {
				logger.trace("Tree for collection: " + tree.collection + ", language: " + tree.language);
				for(int i=0; i<tree.conditions.size(); i++) {
					GCQLCondition condition = tree.conditions.get(i);
					logger.trace("Not: " + condition.not + ", term: " + condition.term.toCQL());
					logger.trace("Sources: " + Arrays.toString(tree.sources.get(i).toArray(new String[tree.sources.get(i).size()])));
				}
			}
			
			//find the factor referenced to the most trees(of the ones left)
			Set<Integer> maxSet = null;
			for(Entry<GCQLCondition, Set<Integer>> current : factorTrees.entrySet()) {
				if(maxSet == null || current.getValue().size() > maxSet.size()) {
					maxSet = current.getValue();
				}
			}
			
			logger.trace("MaxSet factor: " + Arrays.toString(maxSet.toArray(new Integer[maxSet.size()])));
			
			Set<Integer> newMaxSet = (Set<Integer>)((HashSet<Integer>)maxSet).clone();
			//the size the of the maxSet
			int maxSize = newMaxSet.size();
			//create the tree correspondence for the maxSet and
			//find the factors that are referenced in all the trees of the maxSet
			ArrayList<GCQLCondition> maxFactors = new ArrayList<TreeTransformer.GCQLCondition>();
			HashMap<GCQLCondition, Set<Integer>> currentFactorTrees = new HashMap<TreeTransformer.GCQLCondition, Set<Integer>>();
			for(Entry<GCQLCondition, Set<Integer>> current : factorTrees.entrySet()) {
				Set<Integer> newSet = new HashSet<Integer>(current.getValue());
				newSet.retainAll(newMaxSet);
				//if there are trees, from the ones left, that reference the current factor
				if(newSet.size() > 0) {
					currentFactorTrees.put(current.getKey(), newSet);
					if(newSet.size() == maxSize) {
						maxFactors.add(current.getKey());
					}
				}			
			}
			//for each of the maxFactors find the "shortest list" of sources that can answer it.
			//Each list refers to the sources that need to be combined with OR, to provide the results
			ArrayList<ArrayList<Set<String>>> minimalSourceSets = new ArrayList<ArrayList<Set<String>>>();
			for(GCQLCondition condition : maxFactors) {
				
				logger.trace("Factor: " + condition.getTerm().toCQL() + ", Not: " + condition.not);
				ArrayList<Set<String>> sourceSets = findShortestListOfSources(condition, currentFactorTrees.get(condition));
				logger.trace("Sources: ");
				for(Set<String> set : sourceSets) {
					logger.trace(Arrays.toString(set.toArray(new String[set.size()])));
				}
				
				minimalSourceSets.add(sourceSets);
			}
			//get the GeneralTreeNode that can provide the records for the max factors
			matchedFactorsNode = matchFactorSources(maxFactors, newMaxSet, minimalSourceSets);
			
			logger.trace(matchedFactorsNode.toString());
			
			//create the new current subtrees for running the AllInOnePhase
			ArrayList<AndTree> newCurrentSubtrees = new ArrayList<AndTree>();
			for(int i=0; i<this.subtrees.size(); i++) {
				newCurrentSubtrees.add(new AndTree());
			}
			//use a flag to see if there is at least one AndTree with conditions
			boolean atLeastOne = false;
			HashSet<Integer> treeSetForAllInOne = new HashSet<Integer>(newMaxSet);
			for(Integer treeSeqNum : newMaxSet) {
				AndTree newTree = createAndTree(maxFactors, currentSubtrees.get(treeSeqNum));
				//if the new tree has conditions
				if(newTree.conditions.size() > 0) {
					newCurrentSubtrees.remove(treeSeqNum.intValue());
					newCurrentSubtrees.add(treeSeqNum, newTree);
					atLeastOne = true;
				} else {
					treeSetForAllInOne.remove(treeSeqNum.intValue());
				}
			}
			
			//if there is any point in running the allInOnePhase
			if(atLeastOne) {
				
				//apply the all in one phase and get the result back
				GeneralTreeNode generalNode = allInOnePhase(treeSetForAllInOne, newCurrentSubtrees, createFactorMap(newCurrentSubtrees));
				
				if(matchedFactorsNode.type.equals(NodeType.AND)) {
					//append this node to the and node for the max Factor trees
					matchedFactorsNode.children.add(generalNode);
				} else {
					//create a new and node as a root for the two nodes
					GeneralTreeNode generalAndNode = new GeneralTreeNode();
					generalAndNode.type = NodeType.AND;
					generalAndNode.children.add(matchedFactorsNode);
					generalAndNode.children.add(generalNode);
					//keep the reference in matchedFactorsNode
					matchedFactorsNode = generalAndNode;
				}		
			}
			
			
			//check if there are trees not referenced in the maxFactor's set
			treesLeft.removeAll(newMaxSet);
			if(treesLeft.size() > 0) {
				
				//create the factor tree correspondence for the trees left
				Iterator<Entry<GCQLCondition, Set<Integer>>> iter = factorTrees.entrySet().iterator();
				while( iter.hasNext() ) {
					Entry<GCQLCondition, Set<Integer>> current = iter.next();
					current.getValue().removeAll(newMaxSet);
					if(current.getValue().size() == 0) {
						iter.remove();
					}
				}
				newCurrentSubtrees = new ArrayList<AndTree>();
				for(int i=0; i<this.subtrees.size(); i++) {
					newCurrentSubtrees.add(new AndTree());
				}
				for(Integer treeSeqNum : treesLeft) {
					newCurrentSubtrees.remove(treeSeqNum.intValue());
					newCurrentSubtrees.add(treeSeqNum, createAndTree(new ArrayList<GCQLCondition>(), currentSubtrees.get(treeSeqNum)));
				}
				//since the trees left are also trees that can NOT be answered by a unique source
				//we apply Factorization Phase again through this loop
				//treesLeft and factorTrees have already the updated values
				//update also the currentSubtrees
				currentSubtrees = newCurrentSubtrees;
				
				if(firstStep) {
					//create an OR node and add the outcome of this step's factorization as a child
					generalOrNode =  new GeneralTreeNode();
					generalOrNode.type = NodeType.OR;
					generalOrNode.children.add(matchedFactorsNode);
					firstStep = false;
				} else {
					generalOrNode.children.add(matchedFactorsNode);
				}
				
			} else {
				
				if(firstStep) {
					//if this is the first step of the loop return the current output(not connected with an OR node)
					return matchedFactorsNode;
				} else {
					
					generalOrNode.children.add(matchedFactorsNode);
					return generalOrNode;
				}
			}
		
		}
		
		//IMPORTANT: This implementation of factorization phaze applies factorization for one level only. This means
		//that if after the first level of factorization we have: (A AND (C OR D)) OR (B AND (C OR D)) this will not reach after 
		//a second level of factorization to (A OR B) AND (C OR D). More levels of factorization are not needed in our use cases,
		//so currently in order to avoid the extra cost we provide this lightweight implementation. If multilevel factorization
		//can be useful, at this point functionality must be added for detecting identical parts, and applying the multilevel 
		//factorization. Note that there may be a much more interesting way to apply factorization than the simple way applied here, 
		//and this is a part of this planner to be examined. 
	}

	private GeneralTreeNode matchFactorSources(
			ArrayList<GCQLCondition> maxFactors, Set<Integer> newMaxSet,
			ArrayList<ArrayList<Set<String>>> minimalSourceSets) {
		
		//use a clone for the list of factors
		ArrayList<GCQLCondition> factors = (ArrayList<GCQLCondition>)maxFactors.clone();
		
		//create a HashMap for the sources of each factor
		HashMap<GCQLCondition, ArrayList<Set<String>>> factorSourcesMap = 
			new HashMap<GCQLCondition, ArrayList<Set<String>>>();
		for(int i=0; i<maxFactors.size(); i++) {
			factorSourcesMap.put(maxFactors.get(i), minimalSourceSets.get(i));
		}
		
		//match the factors
		ArrayList<ArrayList<GCQLCondition>> matchedFactors = new ArrayList<ArrayList<GCQLCondition>>();
		ArrayList<ArrayList<Set<String>>> matchedSources = new ArrayList<ArrayList<Set<String>>>();
		//while there is no other matching that can be applied
		while(factors.size() > 0) {
						
			ArrayList<GCQLCondition> currentMatching = new ArrayList<GCQLCondition>();
			currentMatching.add(factors.get(0));
			matchedFactors.add(currentMatching);
			ArrayList<Set<String>> currentSourceMatching = factorSourcesMap.get(factors.get(0));
			//remove from the list the factor to be examined
			factors.remove(0);
			
			//examine the factors on the right
			Iterator<GCQLCondition> factorIter = factors.iterator();
			nextFactor: while(factorIter.hasNext()) {
				
				//get the list of sources set for this factor
				GCQLCondition currentFactor = factorIter.next();
				ArrayList<Set<String>> sourcesList = factorSourcesMap.get(currentFactor);
				
				//if the sources lists have the same size
				if(sourcesList.size() == currentSourceMatching.size()) {
					ArrayList<Set<String>> tmpList = new ArrayList<Set<String>>();
					for(int k=0; k<sourcesList.size(); k++) {
						HashSet<String> tmpSet = new HashSet<String>(sourcesList.get(k));
						tmpSet.retainAll(currentSourceMatching.get(k));
						if(tmpSet.size() == 0)
							continue nextFactor;
						tmpList.add(tmpSet);
					}
					//if we reach this point it means that we have a matching
					currentSourceMatching = tmpList;
					currentMatching.add(currentFactor);
					factorIter.remove();
				}
			}
			
			//add the source matching for the matched factors
			matchedSources.add(currentSourceMatching);
		}
		
		//create the tree to answer the query defined by the maxFactors
		GeneralTreeNode root = null;
		if(matchedFactors.size() > 1) {
			root = new GeneralTreeNode();
			root.type = NodeType.AND;
		}
		
		//for all the matched sources
		for(int i=0; i<matchedFactors.size(); i++) {
			ArrayList<GCQLCondition> mFactors = matchedFactors.get(i);
			ArrayList<Set<String>> mSources = matchedSources.get(i);
			
			//if there is only one Set of sources for the matched factors
			//the orNode won't be used
			GeneralTreeNode orNode = null;
			for(int j=0; j<mSources.size(); j++) {
				HashMap<String, HashSet<String>> colLangs = getColLangsForFactorSources(mFactors, mSources.get(j), newMaxSet);
				GeneralTreeNode leaf = new GeneralTreeNode();
				leaf.type = NodeType.LEAF;
				leaf.sources = mSources.get(j);
				leaf.colLangs = colLangs;
				leaf.gcql = IndexRelationCommonSemantics.createGCQLNodeFromMatchedFactors(mFactors, colLangs);
				if(mSources.size() == 1) {
					//if there is only one leaf for answering this part of the query
					if(root == null) {
						return leaf;
					}
					root.children.add(leaf);
				} else {
					if(orNode == null) {
						orNode = new GeneralTreeNode();
						orNode.type = NodeType.OR;
					}
					orNode.children.add(leaf);
				}				
			}
			if(orNode != null) {
				//if there is only one orNode answering this part of the query
				if(root == null) {
					return orNode;
				}
				root.children.add(orNode);
			}
		}
		
		return root;
		
	}

	private HashMap<String, HashSet<String>> getColLangsForFactorSources(
			ArrayList<GCQLCondition> matchedFactors, Set<String> sourceSet,
			Set<Integer> referencedIn) {
		
		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		HashSet<GCQLCondition> factors = new HashSet<GCQLCondition>(matchedFactors);
		
		//examine the Andtrees to find out the languages and collections
		nextTree: for(Integer treeSeqNum : referencedIn) {
			AndTree currentTree = this.subtrees.get(treeSeqNum);
			HashSet<String> sources = new HashSet<String>(sourceSet);
			for(int i=0; i<currentTree.conditions.size(); i++) {
				//if this condition is one of the factors
				if(factors.contains(currentTree.conditions.get(i))){
					sources.retainAll(currentTree.sources.get(i));
					if(sources.size() == 0) {
						continue nextTree;
					}
				}
			}
			//if we reach this point it means that for the current tree
			//all the conditions that refer to the matched factors, have
			//a common subset of sources with the set of alternative sources 
			//currently examined. So these alternatives can answer the matched 
			//factors for this collection-language. In our environment all the 
			//alternative choices for the source, are replicas. If this isn't the case,
			//the question is if this examined set of alternatives is the 
			//minimal one(and if the above process to find out the 
			//collection-languages is correct in general).
			HashSet<String> langs = result.get(currentTree.collection);
			if(langs == null) {
				langs = new HashSet<String>();
				result.put(currentTree.collection, langs);
			}
			langs.add(currentTree.language);
		}
		
		return result;
	}

	private AndTree createAndTree(ArrayList<GCQLCondition> maxFactors,
			AndTree andTree) {
		AndTree newTree = new AndTree();
		
		newTree.collection = andTree.collection;
		newTree.language = andTree.language;
		
		//add the conditions and the corresponding sources except for the max factors
		for(int i=0; i<andTree.conditions.size(); i++) {
			GCQLCondition condition = andTree.conditions.get(i);
			boolean found = false;
			for(GCQLCondition maxFactor : maxFactors) {
				if(maxFactor.equals(condition)) {
					found = true;
					break;
				}
			}
			if(!found) {
				newTree.conditions.add(condition);
				newTree.sources.add(new LinkedHashSet<String>(andTree.sources.get(i)));
			}
		}
		
		return newTree;
	}

	private ArrayList<Set<String>> findShortestListOfSources(
			GCQLCondition factor, Set<Integer> referencedIn) throws CQLUnsupportedException{
		ArrayList<Set<String>> result = new ArrayList<Set<String>>();
		
		for(Integer treeSeqNum : referencedIn) {
			AndTree currentTree = this.subtrees.get(treeSeqNum);
			//find in which position the factor is referred
			int pos = -1;
			for(int i=0; i<currentTree.conditions.size(); i++) {
				if(currentTree.conditions.get(i).equals(factor)) {
					pos = i;
					break;
				}
			}
			if(pos == -1) {
				throw new CQLUnsupportedException("This should not happen. There is a bug here!");
			}
			//examine if any of the Sets already created until now has a common intersection with this set
			boolean found = false;
			for(Set<String> sourceSet : result) {
				HashSet<String> tmpSet = new HashSet<String>(sourceSet);
				tmpSet.retainAll(currentTree.sources.get(pos));
				//if there is such a set
				if(tmpSet.size() > 0) {
					sourceSet.retainAll(currentTree.sources.get(pos));
					found = true;
					break;
				}
			}
			//if no set has a common intersection, add a new one
			if(!found) {
				result.add(new HashSet<String>(currentTree.sources.get(pos)));
			}
		}
		
		return result;
	}

	
}
