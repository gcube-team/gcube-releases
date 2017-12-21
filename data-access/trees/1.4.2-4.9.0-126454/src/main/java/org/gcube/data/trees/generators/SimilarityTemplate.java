/**
 * 
 */
package org.gcube.data.trees.generators;

import static java.lang.Math.*;
import static java.util.UUID.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;



/**
 * 
 * A {@link TreeTemplate} for {@link Tree}s that are similar to a {@link Tree}, the <em>prototype</em>, to a required degree.
 * 
 * @author Fabio Simeoni
 * 
 * @see TemplateFactory
 *
 */
public class SimilarityTemplate extends AbstractTreeTemplate {

	private final Tree proto;
	private final double similarity;
	

	protected SimilarityTemplate(Tree proto,double similarity) {
		this.proto=proto;
		this.similarity=similarity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Tree generate() {
		return new Tree(newId(proto),newattributes(proto),newedges(proto));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("tree prototype");
		b.append("\n======================");
		b.append(proto);
		b.append("\n");
		return b.toString();
	}
	
	
	/// helpers
	
	private String newId(Node proto) {
		return proto.id()==null?null:randomUUID().toString();
	}
	
	private Edge[] newedges(InnerNode proto) {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge e : proto.edges())
			if (random()<similarity)
				edges.add(newedge(e));
		
		return edges.toArray(new Edge[0]);
	}
	
	private Edge newedge(Edge proto) {
		Node target = proto.target();
		return  new Edge(proto.label(),	target instanceof InnerNode?
										generate((InnerNode)target):
										generate((Leaf)target));
	}
	
	 private Map<QName,String> newattributes(Node proto) {
		 Map<QName,String> attributes = new HashMap<QName,String>();
		for (Map.Entry<QName,String> attr : proto.attributes().entrySet())
			if (random()<similarity)
				attributes.put(attr.getKey(),shuffle(attr.getValue(), (similarity+((1-similarity)/2))));
		
		return attributes;
	}
	
	private Leaf generate(Leaf proto) {
		return new Leaf(newId(proto),null,shuffle(proto.value(),similarity),newattributes(proto));
	}
	
	private InnerNode generate(InnerNode proto) {
		return new InnerNode(newId(proto),newattributes(proto),newedges(proto));
	}
	
	private String shuffle(String value, double similarity){
			
		if (value.length()<=1)
			 return value;

		int split=value.length()/2;

		String temp1=shuffle(value.substring(0,split),similarity);
		String temp2=shuffle(value.substring(split),similarity);

		if (Math.random() < similarity) 
		    return temp1 + temp2;
		else 
			return temp2 + temp1;
	}
}
