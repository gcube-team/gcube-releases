package org.gcube.portlets.user.templates.client.model;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeNode implements IsSerializable {

	private String label;
	private String id;
	private String path;
	private int type;
	private boolean isRoot;
	private List<TreeNode> children;

	public TreeNode() {
		super();
	}

	public TreeNode(String label, String id, String path, List<TreeNode> children, int type, boolean isRoot) {
		super();
		this.label = label;
		this.path = path;
		this.id = id;
		this.children = children;
		this.type = type;
		this.isRoot = isRoot;
	} 

	public List<TreeNode> getChildren() {
		return children;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public int getType() {
		return type;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
