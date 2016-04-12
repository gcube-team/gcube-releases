package org.gcube.searchsystem.planning.maxsubtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import search.library.util.cql.query.tree.GCQLNode;

public class GeneralTreeNode {

	public enum NodeType {OR, AND, NOT, LEAF};
	
	NodeType type;
	ArrayList<GeneralTreeNode> children = new ArrayList<GeneralTreeNode>();
	
	//in case of a leaf node
	GCQLNode gcql = null;
	Set<String> sources = null;
	HashMap<String, HashSet<String>> colLangs = null;
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("---" + type + "---\n");
		switch (type) {
		case LEAF:
			
			result.append("---" + gcql.toCQL() + "---\n");
			result.append("---" + Arrays.toString(sources.toArray(new String[sources.size()]))+ "---\n");
			for(Entry<String, HashSet<String>> entry : colLangs.entrySet()) {
				result.append("---collection: " + entry.getKey());
				result.append(" - languages: " + Arrays.toString(entry.getValue().toArray(new String[entry.getValue().size()])) + "---\n");
			}
			break;

		default:
			result.append("---children---\n");
			for(GeneralTreeNode node : children)
				result.append(node.toString());
			
			result.append("---children---\n");
			
			break;
		}
		result.append("---" + type + "---\n");
		
		return result.toString();
	}
}
