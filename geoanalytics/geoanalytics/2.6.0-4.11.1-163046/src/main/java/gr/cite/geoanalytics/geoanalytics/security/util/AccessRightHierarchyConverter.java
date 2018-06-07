package gr.cite.geoanalytics.geoanalytics.security.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.definitions.security.AccessRightDefinition;
import gr.cite.geoanalytics.dataaccess.definitions.security.AccessRightDefinitions;
import gr.cite.geoanalytics.dataaccess.definitions.security.AccessRightStructure;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightLeafNode.AccessRightClass;
import gr.cite.geoanalytics.geoanalytics.security.util.AccessRightLeafNode.AccessRightType;


public class AccessRightHierarchyConverter {
	
	private AccessRightHierarchyConverter() { }
	
	public static AccessRightHierarchy createHierarchy(AccessRightDefinitions definitions) {
	
		AccessRightHierarchy hierarchy = new AccessRightHierarchy();
		
		Map<UUID, AccessRightDefinition> defs  = new HashMap<UUID, AccessRightDefinition>();
		Map<UUID, AccessRightStructure> structs = new HashMap<UUID, AccessRightStructure>();
		Map<UUID, AccessRightNode> nodes = new HashMap<UUID, AccessRightNode>();
		
		for(AccessRightDefinition def : definitions.getDefinitions())
			defs.put(def.getId(), def);
		for(AccessRightStructure struct : definitions.getHierarchy())
			structs.put(struct.getId(), struct);
		
		List<AccessRightNode> leaves = new ArrayList<AccessRightNode>();
		List<AccessRightNode> top  = new ArrayList<AccessRightNode>();
		for(AccessRightDefinition def : definitions.getDefinitions()) {
			AccessRightLeafNode nd = new AccessRightLeafNode();
			nd.setId(def.getId());
			nd.setName(def.getName());
			nd.setDescription(def.getDescription());
			nd.setRightClass(AccessRightClass.valueOf(def.getRightClass().toString()));
			nd.setRightType(AccessRightType.valueOf(def.getType().toString()));
			nodes.put(def.getId(), nd);
			if(def.getParentId() != null)
				doCreateHierarchy(nd, def.getParentId(), defs, structs, nodes);
		}
		for(AccessRightNode node : nodes.values()) {
			if(node.getParent() == null)
				top.add(node);
		}
		hierarchy.setTop(top);
		return hierarchy;
	}
	
	private static void doCreateHierarchy(AccessRightNode node, UUID parentId, Map<UUID, AccessRightDefinition> defs, Map<UUID, AccessRightStructure> structs, Map<UUID, AccessRightNode> nodes) {
		AccessRightInternalNode parent = (AccessRightInternalNode)nodes.get(parentId);
		if(parent != null) {
			parent.addChild(node);
			node.setParent(parent);
			return;
		}
		
		AccessRightStructure struct = structs.get(parentId);
		parent = new AccessRightInternalNode();
		parent.setId(parentId);
		parent.addChild(node);
		node.setParent(parent);
		parent.setDescription(struct.getDescription());
		parent.setName(struct.getName());
		nodes.put(parent.getId(), parent);
		
		if(struct.getParentId() != null)
			doCreateHierarchy(parent, struct.getParentId(), defs, structs, nodes);

	}

}
