package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLRelation;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.Modifier;

public class TreeTransformer {
	
	/**
	 * the logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(TreeTransformer.class.getName());
	
	private TreeTransformer() {
		
	}

	public static ArrayList<Set<GCQLCondition>> pushNotDownOrUp(GCQLNode node, boolean revert) throws CQLUnsupportedException{
		//cases for the possible node types
		if(node instanceof GCQLTermNode)
			return pushNotDownOrUp((GCQLTermNode)node, revert);
		if(node instanceof GCQLAndNode)
			return pushNotDownOrUp((GCQLAndNode)node, revert);
		if(node instanceof GCQLOrNode)
			return pushNotDownOrUp((GCQLOrNode)node, revert);
		if(node instanceof GCQLNotNode)
			return pushNotDownOrUp((GCQLNotNode)node, revert);
		
		throw new CQLUnsupportedException("reached a node of unsupported type: " + node.getClass().getName());
	}
	
	private static ArrayList<Set<GCQLCondition>> pushNotDownOrUp(GCQLTermNode node, boolean revert) throws CQLUnsupportedException{
		
		logger.trace("Not: " + revert + ", TermNode: " + node.toCQL());
		
		GCQLCondition condition =  new GCQLCondition(node, revert);
		HashSet<GCQLCondition> array =  new HashSet<TreeTransformer.GCQLCondition>();
		array.add(condition);
		ArrayList<Set<GCQLCondition>> result =  new ArrayList<Set<GCQLCondition>>();
		result.add(array);
		return result;
	}
	
	private static ArrayList<Set<GCQLCondition>> pushNotDownOrUp(GCQLAndNode node, boolean revert) throws CQLUnsupportedException{
		
		//first process the two subtrees, providing the revert flag
		ArrayList<Set<GCQLCondition>> left = pushNotDownOrUp(node.left, revert);
		ArrayList<Set<GCQLCondition>> right = pushNotDownOrUp(node.right, revert);
		
		//if revert is true
		if(revert) {
			//AND becomes OR and we concatenate the condition arrays
			logger.trace("AndNode becomes Or");
			return concatenate(left, right);
		} else {
			//we match the condition arrays
			logger.trace("AndNode remains And");
			return match(left, right);
		}
				
	}
	
	private static ArrayList<Set<GCQLCondition>> pushNotDownOrUp(GCQLOrNode node, boolean revert) throws CQLUnsupportedException{
		
		//first process the two subtrees, providing the revert flag
		ArrayList<Set<GCQLCondition>> left = pushNotDownOrUp(node.left, revert);
		ArrayList<Set<GCQLCondition>> right = pushNotDownOrUp(node.right, revert);
		
		//if revert is true
		if(revert) {
			//OR becomes AND and we match the condition arrays
			logger.trace("OrNode becomes And");
			return match(left, right);
		} else {
			//we concatenate the condition arrays
			logger.trace("AndNode becomes Or");
			return concatenate(left, right);
		}
	}
	
	private static ArrayList<Set<GCQLCondition>> pushNotDownOrUp(GCQLNotNode node, boolean revert) throws CQLUnsupportedException{
		
		//first process the two subtrees, providing the revert flag
		ArrayList<Set<GCQLCondition>> left = pushNotDownOrUp(node.left, revert);
		//revert the flag on right
		ArrayList<Set<GCQLCondition>> right = pushNotDownOrUp(node.right, !revert);
		
		//if revert is true
		if(revert) {
			//AND(-NOT) becomes OR and we concatenate the condition arrays
			logger.trace("AndNotNode becomes Or");
			return concatenate(left, right);
		} else {
			//we match the condition arrays
			logger.trace("AndNotNode remains AndNot");
			return match(left, right);
		}
	}
	
	private static ArrayList<Set<GCQLCondition>> match(
			ArrayList<Set<GCQLCondition>> left,
			ArrayList<Set<GCQLCondition>> right) {
		
		ArrayList<Set<GCQLCondition>> result = new ArrayList<Set<GCQLCondition>>();
		
		//each outer set matches with every inner set
		//(conditions in each set are connected with 'AND')
		for(Set<GCQLCondition> outer : left) {
			for(Set<GCQLCondition> inner : right) {
			
				Set<GCQLCondition> resultInner = new HashSet<GCQLCondition>();
				resultInner.addAll(cloneSet(outer));
				resultInner.addAll(cloneSet(inner));
				
				result.add(resultInner);
			}			
		}
		
		return result;
	}

	private static Set<GCQLCondition> cloneSet(
			Set<GCQLCondition> input) {
		HashSet<GCQLCondition> result = new HashSet<GCQLCondition>(input.size());
		for(GCQLCondition each : input)
			result.add((GCQLCondition)each.clone());
		
		return result;
	}

	private static ArrayList<Set<GCQLCondition>> concatenate(
			ArrayList<Set<GCQLCondition>> left,
			ArrayList<Set<GCQLCondition>> right) {
		
		left.addAll(right);
		return left;
		
	}

	public static class GCQLCondition implements Cloneable{
		
		boolean not;
		GCQLTermNode term;
		
		GCQLCondition(GCQLTermNode term, boolean not) {
			this.term = term;
			this.not = not;
		}
		
		public GCQLTermNode getTerm() {
			return term;
		}
		public void setTerm(GCQLTermNode term) {
			this.term = term;
		}
		public boolean isNot() {
			return not;
		}
		public void setNot(boolean not) {
			this.not = not;
		}
		
		
		
		@Override
		public String toString() {
			return "GCQLCondition [not=" + not + ", term=" + term.toCQL() + "]";
		}

		@Override
		public Object clone(){
			return new GCQLCondition(cloneTerm(this.term), this.not);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (not ? 1231 : 1237);
			int code = term.getIndex().hashCode() + term.getTerm().hashCode() + term.getRelation().getBase().hashCode();
			result = prime * result + ((term == null) ? 0 : code);
			return result;
		}
		
		/**
		 *  @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			//check the not
			GCQLCondition other = (GCQLCondition) obj;
			if(other.not != this.not)
				return false;
			if(!this.term.getIndex().equals(other.term.getIndex()))
				return false;
			if(!this.term.getRelation().getBase().equals(other.term.getRelation().getBase()))
				return false;
			if(this.term.getRelation().getModifiers().size() != other.term.getRelation().getModifiers().size())
				return false;
			int size = this.term.getRelation().getModifiers().size();
			for(int i=0; i<size; i++) {
				Modifier mod1 = this.term.getRelation().getModifiers().get(i);
				Modifier mod2 = other.term.getRelation().getModifiers().get(i);
				
				if(!mod1.getType().equals(mod2.getType()))
					return false;
				if(!mod1.getValue().equals(mod2.getValue()))
					return false;
			}
			
			return true;				
		}
		
		public static GCQLTermNode cloneTerm(GCQLTermNode term) {
			GCQLTermNode termNode = new GCQLTermNode();
			
			termNode.setIndex(term.getIndex());
			termNode.setTerm(term.getTerm());
			
			GCQLRelation newRelation = new GCQLRelation();
			newRelation.setBase(term.getRelation().getBase());
			for(Modifier mod : term.getRelation().getModifiers()) {
				Modifier newModifier = new Modifier(mod.getType(), mod.getComparison(), mod.getValue());
				newRelation.getModifiers().add(newModifier);
			}
			termNode.setRelation(newRelation);
			
			return termNode;
		}
		
	}
	

}
