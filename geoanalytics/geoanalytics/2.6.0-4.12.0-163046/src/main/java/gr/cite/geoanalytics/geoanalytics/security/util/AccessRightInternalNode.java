package gr.cite.geoanalytics.geoanalytics.security.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AccessRightInternalNode extends AccessRightNode {
	
	private final List<AccessRightNode> children = new ArrayList<AccessRightNode>();
	private final Map<UUID, AccessRightNode> childrenLookup = new HashMap<UUID, AccessRightNode>();
	
	public List<UUID> getChildren() {
		List<UUID> ids = new ArrayList<UUID>();
		for(AccessRightNode c : children)
			ids.add(c.getId());
		return ids;
	}

	public void setChildren(List<AccessRightNode> children) {
		children.clear();
		children.addAll(children);
		childrenLookup.clear();
		for(AccessRightNode c : children)
			childrenLookup.put(c.getId(), c);
	}

	public void addChild(AccessRightNode child) {
		if(!childrenLookup.containsKey(child.getId()))
			children.add(child);
		childrenLookup.put(child.getId(), child);
	}
	
	public AccessRightNode getChild(UUID id) {
		return childrenLookup.get(id);
	}
	
	public AccessRightNode getDescendant(UUID id) {
		AccessRightNode child = childrenLookup.get(id);
		if(child != null)
			return child;
		
		for(AccessRightNode c : children) {
			if(!c.isLeaf()) {
				AccessRightNode desc = ((AccessRightInternalNode)c).getDescendant(id);
				if(desc != null)
					return desc;
			}
		}
		return null;
	}
	
	public void removeChild(UUID id) {
		AccessRightNode child = childrenLookup.get(id);
		if(child != null) {
			children.remove(child);
			childrenLookup.remove(id);
		}
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}
}
