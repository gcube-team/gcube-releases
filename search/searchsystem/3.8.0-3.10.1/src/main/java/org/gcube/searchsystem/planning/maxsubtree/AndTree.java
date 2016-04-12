package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.gcube.searchsystem.planning.maxsubtree.TreeTransformer.GCQLCondition;

public class AndTree {
		
	//the conditions of the tree
	ArrayList<GCQLCondition> conditions = new ArrayList<GCQLCondition>();
	//collection ID for which the results must derive
	String collection = null;
	//the language of the results
	String language = null;
	//the collections IDs from which the results must NOT derive
	ArrayList<String> notCollections = new ArrayList<String>();
	//the forbidden languages for the results
	ArrayList<String> notLanguages = new ArrayList<String>();
	
	//the sources providing records for the corresponding condition,
	//and the collection-language specified in this AndTree. If there 
	//are no conditions then the sources refer to all the sources that 
	//provide records for this specific collection-language 
	ArrayList<LinkedHashSet<String>> sources =  new ArrayList<LinkedHashSet<String>>();
	
	public ArrayList<LinkedHashSet<String>> getSources() {
		return sources;
	}
	public void setSources(ArrayList<LinkedHashSet<String>> sources) {
		this.sources = sources;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public ArrayList<GCQLCondition> getConditions() {
		return conditions;
	}
	public void setConditions(ArrayList<GCQLCondition> conditions) {
		this.conditions = conditions;
	}
	public ArrayList<String> getNotCollections() {
		return notCollections;
	}
	public void setNotCollections(ArrayList<String> notCollections) {
		this.notCollections = notCollections;
	}
	public ArrayList<String> getNotLanguages() {
		return notLanguages;
	}
	public void setNotLanguages(ArrayList<String> notLanguages) {
		this.notLanguages = notLanguages;
	}	
	
	@Override
	public Object clone() {
		AndTree newTree = new AndTree();
		
		//clone the conditions
		for(GCQLCondition cond : this.conditions) {
			newTree.conditions.add((GCQLCondition)cond.clone());
		}
		newTree.collection = this.collection;
		newTree.language = this.language;
		newTree.notCollections.addAll(this.notCollections);
		newTree.notLanguages.addAll(this.notLanguages);
		for(Set<String> set : this.sources) {
			newTree.sources.add(new LinkedHashSet<String>(set));
		}
		
		return newTree;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collection == null) ? 0 : collection.hashCode());
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ ((notCollections == null) ? 0 : notCollections.hashCode());
		result = prime * result
				+ ((notLanguages == null) ? 0 : notLanguages.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
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
		AndTree other = (AndTree) obj;
		if (collection == null) {
			if (other.collection != null)
				return false;
		} else if (!collection.equals(other.collection))
			return false;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (notCollections == null) {
			if (other.notCollections != null)
				return false;
		} else if (!notCollections.equals(other.notCollections))
			return false;
		if (notLanguages == null) {
			if (other.notLanguages != null)
				return false;
		} else if (!notLanguages.equals(other.notLanguages))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AndTree [conditions=" + conditions + ", collection="
				+ collection + ", language=" + language + ", notCollections="
				+ notCollections + ", notLanguages=" + notLanguages
				+ ", sources=" + sources + "]";
	}

	
	
}
