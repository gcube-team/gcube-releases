package org.gcube.portlets.admin.vredeployer.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class AtomicTreeNode extends BaseTreeModel {
	private static final long serialVersionUID = 5094327834701967591L;
	private static int ID = 0;

	private NodeType type;
	

	/**
	 * @deprecated fr serialization only
	 */
	public AtomicTreeNode() {
		set("id", ID++);
	}

	public AtomicTreeNode(final String node) {
		this(node, null);
	}

	/**
	 * The node is used as original node of the element useful to retrieve it from the IS.
	 * The name is instead used for pretty printing (aliasing).
	 * @param node the corresponding IS node
	 * @param name if null the node will be used instead
	 */
	public AtomicTreeNode(final String node, final String name) {
		set("id", ID++);
		set("node", node);
		if (name == null) {
			set("name", node);
		} else {
			set("name", name);
		}
	}

	public AtomicTreeNode(final String node, final String name, final String icon, NodeType type) {
		this(node, name);
		set("icon", icon);
		this.type = type;
	}

	public AtomicTreeNode(final String node, final String name, final AtomicTreeNode[] children) {
		this(node, name);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}

	public AtomicTreeNode(final String node, final String name, final String icon, final AtomicTreeNode[] children) {
		this(node, name, children);
		set("icon", icon);
	}

	public AtomicTreeNode(final String node, final String name, final String icon, final int sortIdx, final AtomicTreeNode[] children) {
		this(node, name, icon, children);
		set("sortIdx", sortIdx);
	}

	public final Integer getId() {
		return (Integer) get("id");
	}

	public final String getName() {
		return (String) get("name");
	}

	public final String getNode() {
		return (String) get("node");
	}

	public final String getLabel() {
		return (String) get("label");
	}

	public final String toString() {
		return getName();
	}

	public final String getSubType() {
		if (this.isLeaf() && this.getParent() != null) {
			return this.getNode();
		}
		return null;
	}
	/**
	 * 
	 * @return
	 */
	public NodeType getType() {
		return type;
	}
}
