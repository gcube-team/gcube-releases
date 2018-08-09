package org.gcube.portlets.docxgenerator.treemodel;

import java.util.ArrayList;
import java.util.List;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;

public class TreeNode<T extends BasicComponent> {

	private List<TreeNode<T>> children = new ArrayList<TreeNode<T>>();
	
	private  T value;
	private TreeNode<T> parent;
	
	public TreeNode(T value) {
		this.value = value;
	}
	
	public void addChild(TreeNode<T> node) {
		node.parent = this;
		children.add(node);
	}
	
	public List<TreeNode<T>> getChildren(){
		return children;
	}
	
	public TreeNode<T> getParent() {
		return parent;
	}
	
	public void removeChild(TreeNode<T> node){
		children.remove(node);
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	
	public static <T extends BasicComponent> void purgeTree(TreeNode<T> root) {
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			TreeNode<T> child = root.getChildren().get(i);
			purgeTree(child);

			switch (child.getValue().getType()) {
			case HEADING_1:
			case HEADING_2:
			case HEADING_3:
			case HEADING_4:
			case HEADING_5:
				if (!child.hasValidContent(child.getChildren())){
					root.getChildren().remove(i--);
				}
				break;

			default:
				break;
			}
		}
	}
	
	public boolean hasValidContent(List<TreeNode<T>> children) {
		
		for (TreeNode<T> node : children) {
			switch (node.getValue().getType()) {
			case HEADING_1:
			case HEADING_2:
			case HEADING_3:
			case HEADING_4:
			case HEADING_5:
			case BODY: 
			case BODY_NOT_FORMATTED: 
			case FLEX_TABLE: 
			case DYNA_IMAGE:
			case ATTRIBUTE:
			case ATTRIBUTE_MULTI:
			case ATTRIBUTE_UNIQUE:	
				return true;
			}
		}
		return false;
	}
	
	
}
