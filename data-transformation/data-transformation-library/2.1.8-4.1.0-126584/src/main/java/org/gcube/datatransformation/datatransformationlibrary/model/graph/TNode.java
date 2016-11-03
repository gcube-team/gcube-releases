package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Node of the transformationUnit's graph.
 * </p>
 */
public class TNode extends ContentType{
	
	private ArrayList <TEdge> links = new ArrayList<TEdge>();
	
	/**
	 * Creates a new <tt>TNode</tt>.
	 */
	public TNode(){
		super();
	}
	
	/**
	 * Instantiates a <tt>TNode</tt> from a content type.
	 * @param contentType The content type of the TNode.
	 */
	public TNode(ContentType contentType){
		super();
		this.setMimeType(contentType.getMimeType());
		this.setContentTypeParameters(contentType.getContentTypeParameters());
	}
	
	/**
	 * Creates a new <tt>TNode</tt>.
	 * @param mimeType The mimetype of the node.
	 * @param contentTypeParameters The content type parameters of the node.
	 */
	public TNode(String mimeType, List<Parameter> contentTypeParameters) {
		super(mimeType, contentTypeParameters);
		this.links=new ArrayList<TEdge>();
	}
	
	/**
	 * Returns the edges of this node to other nodes of the graph.
	 * 
	 * @return The edges of this node.
	 */
	public ArrayList<TEdge> getEdges() {
		return links;
	}
	
	/**
	 * Adds a new edge to this node.
	 * 
	 * @param edge The edge to be added.
	 * @return If the edge was added successfully.
	 */
	public boolean addEdge(TEdge edge) {
		return links.add(edge);
	}
	
	/**
	 * Returns true if there are edges from this node to another one.
	 * @return true if there are edges from this node to another one.
	 */
	public boolean hasEdges() {
		return links.isEmpty();
	}
	
	/**
	 * Returns the amount of the edges of this node. 
	 * @return The amount of the edges of this node.
	 */
	public int sizeOfEdges() {
		return links.size();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.ContentType#equals(java.lang.Object)
	 * @param obj The object to be checked. 
	 * @return True if the two nodes are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
