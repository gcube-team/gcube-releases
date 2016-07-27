package org.gcube.opensearch.opensearchdatasource.processor;

import org.gcube.opensearch.opensearchlibrary.OpenSearchDataSourceConstants;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLProxNode;
import search.library.util.cql.query.tree.GCQLTermNode;

public class OpenSearchGcqlAnnotator {

	public GCQLNodeAnnotation processNode(GCQLNode node) throws Exception {
		//cases for the possible node types
		if(node instanceof GCQLProjectNode)
			return processNode((GCQLProjectNode)node);
		if(node instanceof GCQLAndNode)
			return processNode((GCQLAndNode)node);
		if(node instanceof GCQLNotNode)
			return processNode((GCQLNotNode)node);
		if(node instanceof GCQLOrNode)
			return processNode((GCQLOrNode)node);
		if(node instanceof GCQLProxNode)
			return processNode((GCQLProxNode)node);
		if(node instanceof GCQLTermNode)
			return processNode((GCQLTermNode)node);
		
		throw new Exception("This node class is not supported: " + node.getClass().toString());
	}
	
	public String getFirstEncounteredCollectionId(GCQLNodeAnnotation node) {
		if(node.collectionId != null) return node.collectionId;
		if(node.left != null) {
			String leftCol = getFirstEncounteredCollectionId(node.left);
			if(leftCol != null) return leftCol;
		}
		if(node.right != null) {
			String rightCol = getFirstEncounteredCollectionId(node.right);
			if(rightCol != null) return rightCol;
		}
		return null;
	}
	
	private GCQLNodeAnnotation processNode(GCQLProjectNode node) throws Exception {
		GCQLNodeAnnotation subAnnotation = processNode(node.subtree);
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		an.left = subAnnotation;
		return an;
	}
	
	private GCQLNodeAnnotation processNode(GCQLAndNode node) throws Exception {
		GCQLNodeAnnotation leftAnnotation = processNode(node.left);
		GCQLNodeAnnotation rightAnnotation = processNode(node.right);
		
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		
		if(node.left instanceof GCQLTermNode && node.right instanceof GCQLTermNode) {
			if(leftAnnotation.collectionId != null) { 
				rightAnnotation.collectionId = leftAnnotation.collectionId;
				an.collectionId = leftAnnotation.collectionId;
			}
			else if(rightAnnotation.collectionId != null) {
				leftAnnotation.collectionId = rightAnnotation.collectionId;
				an.collectionId = rightAnnotation.collectionId;
			}
		}else {
			if(leftAnnotation.collectionId == null) {
				an.collectionId = rightAnnotation.collectionId;
				propagateCollectionId(leftAnnotation, rightAnnotation.collectionId);
			}
			else if(rightAnnotation.collectionId == null) {
				an.collectionId = leftAnnotation.collectionId;
				propagateCollectionId(rightAnnotation, leftAnnotation.collectionId);
			}
		}
		an.left = leftAnnotation;
		an.right = rightAnnotation;
		return an;
	}
	
	private GCQLNodeAnnotation processNode(GCQLOrNode node) throws Exception {
		GCQLNodeAnnotation leftAnnotation = processNode(node.left);
		GCQLNodeAnnotation rightAnnotation = processNode(node.right);
		
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		
		if(node.left instanceof GCQLTermNode && node.right instanceof GCQLTermNode) {
			if(leftAnnotation.collectionId != null) { 
				rightAnnotation.collectionId = leftAnnotation.collectionId;
				an.collectionId = leftAnnotation.collectionId;
			}
			else if(rightAnnotation.collectionId != null) {
				leftAnnotation.collectionId = rightAnnotation.collectionId;
				an.collectionId = rightAnnotation.collectionId;
			}
		}else {
			if(leftAnnotation.collectionId == null) propagateCollectionId(leftAnnotation, rightAnnotation.collectionId);
			else if(rightAnnotation.collectionId == null) propagateCollectionId(rightAnnotation, leftAnnotation.collectionId);
		}
		an.left = leftAnnotation;
		an.right = rightAnnotation;
		return an;
	}
	
	private GCQLNodeAnnotation processNode(GCQLTermNode node) throws Exception {
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		
		if(node.getIndex().equals(OpenSearchDataSourceConstants.COLLECTION_FIELD))
			an.collectionId = GcqlProcessor.removeQuotes(node.getTerm());
		
		return an;
	}
	
	private GCQLNodeAnnotation processNode(GCQLProxNode node) throws Exception {
		GCQLNodeAnnotation leftAnnotation = processNode(node.left);
		GCQLNodeAnnotation rightAnnotation = processNode(node.right);
		
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		
		if(node.left instanceof GCQLTermNode && node.right instanceof GCQLTermNode) {
			if(leftAnnotation.collectionId != null) { 
				rightAnnotation.collectionId = leftAnnotation.collectionId;
				an.collectionId = leftAnnotation.collectionId;
			}
			else if(rightAnnotation.collectionId != null) {
				leftAnnotation.collectionId = rightAnnotation.collectionId;
				an.collectionId = rightAnnotation.collectionId;
			}
		}else {
			if(leftAnnotation.collectionId == null) propagateCollectionId(leftAnnotation, rightAnnotation.collectionId);
			else if(rightAnnotation.collectionId == null) propagateCollectionId(rightAnnotation, leftAnnotation.collectionId);
		}
		an.left = leftAnnotation;
		an.right = rightAnnotation;
		return an;
	}
	
	private GCQLNodeAnnotation processNode(GCQLNotNode node) throws Exception {
		GCQLNodeAnnotation leftAnnotation = processNode(node.left);
		GCQLNodeAnnotation rightAnnotation = processNode(node.right);
		
		GCQLNodeAnnotation an = new GCQLNodeAnnotation();
		
		if(node.left instanceof GCQLTermNode && node.right instanceof GCQLTermNode) {
			if(leftAnnotation.collectionId != null) { 
				rightAnnotation.collectionId = leftAnnotation.collectionId;
				an.collectionId = leftAnnotation.collectionId;
			}
			else if(rightAnnotation.collectionId != null) {
				leftAnnotation.collectionId = rightAnnotation.collectionId;
				an.collectionId = rightAnnotation.collectionId;
			}
		}else {
			if(leftAnnotation.collectionId == null) propagateCollectionId(leftAnnotation, rightAnnotation.collectionId);
			else if(rightAnnotation.collectionId == null) propagateCollectionId(rightAnnotation, leftAnnotation.collectionId);
		}
		an.left = leftAnnotation;
		an.right = rightAnnotation;
		return an;
	}
	
	private void propagateCollectionId(GCQLNodeAnnotation annotation, String collectionId) {
		if(annotation.left != null) propagateCollectionId(annotation.left, collectionId);
		if(annotation.right != null) propagateCollectionId(annotation.right, collectionId);
		annotation.collectionId = collectionId;
	}
}
