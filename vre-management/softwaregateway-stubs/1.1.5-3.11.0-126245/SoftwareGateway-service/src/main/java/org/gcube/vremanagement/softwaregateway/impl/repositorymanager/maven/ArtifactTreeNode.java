package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class ArtifactTreeNode {
	
	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(ArtifactTreeNode.class);
	
	private ArtifactCoordinates item;
	private ArtifactTreeNode parent;
	private int deep;
	private List<ArtifactTreeNode> sons = new ArrayList<ArtifactTreeNode>();
	
	/**
	 * Constructor
	 * @param parent parent node
	 * @param item value
	 * @param deep deep
	 * @throws Exception if fails
	 */
	public ArtifactTreeNode(ArtifactTreeNode parent, ArtifactCoordinates item, int deep) throws Exception {
		if(item!=null){
			this.item = item;
		}else{
			IllegalArgumentException e = new IllegalArgumentException();
			logger.debug(e);
			throw e;
		}
		
		this.parent = parent;
		this.deep = deep;
	}
	
	/**
	 * Add a son to this node
	 * @param son to add 
	 * @throws Exception if fails
	 */
	public void addSon(ArtifactTreeNode son) throws Exception {
		if(!sons.add(son)){
			Exception e = new Exception("Unable to add the son " + son.toString() + " to the list");
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * Remove a son from this node
	 * @param son to remove
	 * @throws Exception if fails
	 */
	public void removeSon(ArtifactTreeNode son) throws Exception {
		if(!sons.remove(son)){
			Exception e = new Exception("Unable to remove the son " + son.toString() + " from list");
			logger.error(e);
			throw e;
		}
	}

	/**
	 * Get the Artifact
	 * @return the Artifact
	 */
	public ArtifactCoordinates getItem() {
		return item;
	}
	
	/**
	 * @return deep
	 */
	public int getDeep() {
		return deep;
	}

	/**
	 * @return parent TreeNode;
	 */
	public ArtifactTreeNode getParent() {
		return parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString(){
		return (parent==null?"ROOT Node\n":"") + "Value=" + item.toString() + "\nDeep = " + deep + "\nNumber of child = " + sons.size();
	}
	
	/**
	 * @return XML tree representation
	 */
	public String printTree(){
		final String indent  = "\t";
		StringBuilder deepIndent = new StringBuilder();
				
		StringBuilder sb = new StringBuilder();
		if(parent==null){
			sb.append("<Root>\n");
			for(ArtifactTreeNode item : sons){
				sb.append(item.printTree());
			}
		}else{
			for (int i = 1; i < (deep*2)-1; i++) {
				deepIndent.append(indent);
			}
			sb.append(deepIndent).append("<Node>\n");
			
			sb.append(deepIndent).append(indent).append("<Value>").append(item.toString()).append("</Value>\n");
			
			if(sons.size()>0){
				sb.append(deepIndent).append(indent).append("<Children>\n");
				for(ArtifactTreeNode item : sons){
					sb.append(item.printTree());
				}
				sb.append(deepIndent).append(indent).append("</Children>\n");
			}else{
				sb.append(deepIndent).append(indent).append("<Children/>\n");
			}
		
		}
		
		if(parent==null){
			sb.append("</Root>\n");
		}else{
			sb.append(deepIndent).append("</Node>\n");
		}
		
		return sb.toString();
	}

	/**
	 * @param dep dependency List fileld with dependecy information
	 * @throws Exception if fails
	 */
	public void listDependency(List<String> dep) throws Exception {
		if(parent!=null){
			String xml = item.toXML("Dependency");
			dep.add(xml);
			logger.debug("Dependency xml specification\n" + xml + "\n\n");
		}
		
		for (ArtifactTreeNode treeNode : sons) {
			treeNode.listDependency(dep);
		}
	}
	
}
