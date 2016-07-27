package org.gcube.vremanagement.resourcemanager.impl.state;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;

/**
 * List of nodes belonging a scope
 * @author manuele simi (CNR)
 *
 */
public class VirtualNodeList {

	Map<String, VirtualNode> listByName;
	Map<String, VirtualNode> listByID;

	GCUBEScope scope;
	private final String stateTag = "NODES";
	
	public VirtualNodeList(GCUBEScope scope) {
		this.scope = scope;
		this.listByName = new HashMap<String, VirtualNode>();
		this.listByID = new HashMap<String, VirtualNode>();

	}

	@SuppressWarnings("unchecked")
	void loadFromState(RawScopeState rawState) {
		if (rawState.data.containsKey(stateTag)) {
			this.listByName = (Map<String, VirtualNode>) rawState.data.get(stateTag);
			//rebuild also the list by ID
			for (VirtualNode node : this.listByName.values())
				this.listByID.put(node.getID(), node);
		}
	}
	
	void storeToState(RawScopeState rawState) {
		rawState.data.put(stateTag, this.listByName);
	}
	
	/**
	 * Adds the node to the list
	 * @param node the node to add
	 */
	void addNode(VirtualNode node) {
		this.listByName.put(node.getName(), node);
		this.listByID.put(node.getID(), node);
	}
	
	/**
	 * Gets a node given its name
	 * @param name the name of the node
	 * @return the node
	 * @throws NoGHNFoundException
	 */
	public VirtualNode getNode(String name) throws NoGHNFoundException {
		if (!this.listByName.containsKey(name)) {
			VirtualNode node = VirtualNode.fromName(name, this.scope);
			if (GHNContext.getContext().getName().equalsIgnoreCase(node.getName()))
				throw new NoGHNFoundException ("cannot virtualize the GHN (name=" + node.getName() + ") on which Resource Manager is running");
			this.addNode(node);
		}
		return this.listByName.get(name);
	}
	
	/**
	 * Gets a node given its id
	 * @param id the id of the node
	 * @return the node
	 * @throws NoGHNFoundException
	 */
	VirtualNode getNodeById(String id) throws NoGHNFoundException {
		if (!this.listByID.containsKey(id)) {
			VirtualNode node = VirtualNode.fromID(id, this.scope);
			if (GHNContext.getContext().getName().equalsIgnoreCase(node.getName()))
				throw new NoGHNFoundException ("cannot virtualize the GHN (name=" + node.getName() + ") on which Resource Manager is running");
			this.addNode(node);
			return node;
		}
		return this.listByID.get(id);
	}
	
}
