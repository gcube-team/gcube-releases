package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Contains nodes which have the same mimetype.
 * </p>
 */
public class NodesCollection {

	private HashMap<String, ArrayList<TNode>> nodesbyMT = new HashMap<String, ArrayList<TNode>>();
	
	/**
	 * Adds a content type to this node collection.
	 * 
	 * @param contentType The content type of this node.
	 * @return The node of the graph.
	 */
	public TNode add(ContentType contentType){
		TNode newnode = new TNode(contentType);
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT == null){
			nodesofMT = new ArrayList<TNode>();
			nodesbyMT.put(contentType.getMimeType().toLowerCase(), nodesofMT);
		}
		nodesofMT.add(newnode);
		return newnode;
	}
	
	/**
	 * Checks if the content type exists in this node collection.
	 * 
	 * @param contentType The content type to be checked.
	 * @return true if the content type already exists in the collection.
	 */
	public boolean exists(ContentType contentType){
		return (get(contentType)!=null);
	}
	
	/**
	 * Returns the node of a content type if it exists in the collection.
	 * 
	 * @param contentType The content type parameters.
	 * @return The node of the collection.
	 */
	public TNode get(ContentType contentType){
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT==null)
			return null;
		for(int i=0;i<nodesofMT.size();i++){
			if(Parameter.equals(nodesofMT.get(i).getContentTypeParameters(), contentType.getContentTypeParameters()))
				return nodesofMT.get(i);
		}
		return null;
	}
	
	/**
	 * Returns nodes that have exact support with the content type filled with unbound parameters.
	 * 
	 * @param contentType The content type.
	 * @return The nodes that have exact support with the content type.
	 */
	public ArrayList<TNodeWithUnbound> getExactlySupportedWithUnbound(ContentType contentType){
		ArrayList<TNodeWithUnbound> exactsupported = new ArrayList<TNodeWithUnbound>();
		TNode node = get(contentType);
		if(node!=null)exactsupported.add(new TNodeWithUnbound(node,null));
		return exactsupported;
	}
	
	/**
	 * Returns nodes that have exact support with the content type.
	 * 
	 * @param contentType The content type.
	 * @return The nodes that have exact support with the content type.
	 */
	public TNode getExactlySupported(ContentType contentType){
		return get(contentType);
	}
	
	/**
	 * Returns nodes that have exact support with the content type filled with unbound parameters.
	 * 
	 * @param contentType The content type.
	 * @return The nodes that have generic support with the content type.
	 */
	public ArrayList<TNodeWithUnbound> getAnyThatSupportWithUnbound(ContentType contentType){
		ArrayList<TNodeWithUnbound> supported = new ArrayList<TNodeWithUnbound>();
		
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT==null)
			return null;
		for(int i=0;i<nodesofMT.size();i++){
			List<Parameter> unbound;
			if((unbound = ContentType.supportAndFillUnbound(nodesofMT.get(i).getContentTypeParameters(), contentType.getContentTypeParameters()))!=null){
				TNodeWithUnbound twunbound = new TNodeWithUnbound(nodesofMT.get(i), unbound);
				twunbound.setRefsToSource();
				supported.add(twunbound);
			}
		}
		return supported;
	}
	
	/**
	 * Returns nodes that have generic support with the <tt>contentType</tt>.
	 * 
	 * @param contentType The <tt>ContentType</tt> to be checked.
	 * @return The nodes that have generic support with the content type.
	 */
	public ArrayList<TNode> getAnyThatSupport(ContentType contentType){
		ArrayList<TNode> supported = new ArrayList<TNode>();
		
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT==null)
			return supported;
		for(int i=0;i<nodesofMT.size();i++){
			if(ContentType.support(nodesofMT.get(i).getContentTypeParameters(), contentType.getContentTypeParameters())){
				supported.add(nodesofMT.get(i));
			}
		}
		return supported;
	}

	
	/**
	 * Returns nodes which are supported by the content type.
	 * 
	 * @param contentType The content type.
	 * @return The nodes which are supported by the content type.
	 */
	public ArrayList<TNodeWithUnbound> getAnySupportedByWithUnbound(ContentType contentType){
		ArrayList<TNodeWithUnbound> supported = new ArrayList<TNodeWithUnbound>();
		
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT==null)
			return null;
		for(int i=0;i<nodesofMT.size();i++){
			List<Parameter> unbound;
			if((unbound = ContentType.supportAndFillUnbound(contentType.getContentTypeParameters(), nodesofMT.get(i).getContentTypeParameters()))!=null){
				TNodeWithUnbound twunbound = new TNodeWithUnbound(nodesofMT.get(i), unbound);
				twunbound.setRefsToTarget();
				supported.add(twunbound);
			}
		}
		return supported;
	}
	
	/**
	 * Returns nodes which are generically supported by the content type.
	 * 
	 * @param contentType The content type.
	 * @return The nodes which are generically supported by the content type.
	 */
	public ArrayList<TNodeWithUnbound> getGenericallySupported(ContentType contentType){
		ArrayList<TNodeWithUnbound> gensupported = new ArrayList<TNodeWithUnbound>();
		
		ArrayList<TNode> nodesofMT = nodesbyMT.get(contentType.getMimeType().toLowerCase());
		if(nodesofMT==null){
			return null;
		}
		for(int i=0;i<nodesofMT.size();i++){
			if(ContentType.gensupport(nodesofMT.get(i).getContentTypeParameters(), contentType.getContentTypeParameters()))
				gensupported.add(new TNodeWithUnbound(nodesofMT.get(i),null));
		}
		return gensupported;
	}
	
	protected void clear(){
		nodesbyMT.clear();
	}
}
