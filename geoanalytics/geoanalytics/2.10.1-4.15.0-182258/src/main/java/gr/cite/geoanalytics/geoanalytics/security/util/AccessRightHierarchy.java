package gr.cite.geoanalytics.geoanalytics.security.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AccessRightHierarchy {
	
	private final List<AccessRightNode> top = new ArrayList<AccessRightNode>();
	private final Map<UUID, AccessRightNode> lookup = new HashMap<UUID, AccessRightNode>();
	
	public List<AccessRightNode> getTop() {
		return new ArrayList<AccessRightNode>(top);
	}
	
	public void setTop(List<AccessRightNode> topNodes) {
		top.clear();
		top.addAll(topNodes);
		lookup.clear();
		for(AccessRightNode t : top)
			lookup.put(t.getId(), t);
	}
	
	public void addTop(AccessRightNode topNode) {
		if(!lookup.containsKey(topNode.getId()))
			top.add(topNode);
		lookup.put(topNode.getId(), topNode);
	}
	
	public AccessRightNode lookup(UUID id) {
		AccessRightNode result = lookup.get(id);
		if(result != null)
			return result;
		
		for(AccessRightNode topNode : top) {
			if(!topNode.isLeaf()) {
				AccessRightNode desc = ((AccessRightInternalNode)topNode).getDescendant(id);
				if(desc != null)
					return desc;
			}
		}
		return null;
	}
	
}
