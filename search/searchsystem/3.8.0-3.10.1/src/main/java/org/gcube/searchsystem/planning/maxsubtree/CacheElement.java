package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.List;

public class CacheElement {
	ArrayList<AndTree> subtrees;
	List<String> projections;
	String indication;
	
	public CacheElement(ArrayList<AndTree> subtrees,
			List<String> proj, String indication) {
		super();
		this.subtrees = subtrees;
		this.projections = proj;
		this.indication = indication;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indication == null) ? 0 : indication.hashCode());
		result = prime * result + ((projections == null) ? 0 : projections.hashCode());
		result = prime * result
				+ ((subtrees == null) ? 0 : subtrees.hashCode());
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
		CacheElement other = (CacheElement) obj;
		if (indication == null) {
			if (other.indication != null)
				return false;
		} else if (!indication.equals(other.indication))
			return false;
		if (projections == null) {
			if (other.projections != null)
				return false;
		} else if (!projections.equals(other.projections))
			return false;
		if (subtrees == null) {
			if (other.subtrees != null)
				return false;
		} else if (!subtrees.equals(other.subtrees))
			return false;
		return true;
	}
	
	
}
