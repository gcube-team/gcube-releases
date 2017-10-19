/**
 * 
 */
package org.gcube.data.trees.generators;

import static java.lang.Math.*;
import static java.util.UUID.*;
import static org.apache.commons.lang.RandomStringUtils.*;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A {@link AbstractTreeTemplate} for {@link Tree}s with given structural properties.
 * 
 * @see StructuralTemplate.STBuilder
 * 
 * @author Fabio Simeoni
 *
 */
public class StructuralTemplate extends AbstractTreeTemplate {

	private static Logger logger = LoggerFactory.getLogger(StructuralTemplate.class);
	
	private static final int defaultWidth=5;
	private static final int defaultDepth=2;
	private static final int defaultValueSize=3000;
	
	Integer width=defaultWidth;
	Integer depth=defaultDepth;
	Integer value=defaultValueSize;
	int attributes=0;
	
	private boolean generateIDs=false;
	private Map<Integer,List<String>> labels;
	private String sourceId;

	protected StructuralTemplate(){}
	
	/**
	 * {@inheritDoc}
	 */
	public Tree generate() {
		
		if (labels==null)
			generateLabels();
		
		InnerNode node = inner(0);
		Tree tree = new Tree(node.id(), node.attributes(), node.edges().toArray(new Edge[0]));
		
		if (sourceId!=null)
			tree.setSourceId(sourceId);
		
		return tree;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Tree template");
		b.append("\n======================");
		b.append("\nwidth="+width);
		b.append("\ndepth="+depth);
		b.append("\ntotal payload="+round(pow(width,depth)*value/1024)+"kb");
		b.append("\n");
		return b.toString();
	}
	
	/**
	 * Builds {@link StructuralTemplate}s.
	 * 
	 * @author Fabio Simeoni
	 *
	 */
	public static class STBuilder {

		private StructuralTemplate template = new StructuralTemplate();
		
		/**
		 * Sets a source identifier on the template.
		 * @param id the source identifier
		 * @return the template
		 */
		public STBuilder inSource(String id) {
			template.sourceId=id;
			return this;
		}
		
		/**
		 * Sets the width on the template.
		 * @param w the width
		 * @return this builder
		 */
		public STBuilder wide(int w) {
			template.width=w;
			return this;
		}
		
		/**
		 * Sets the depth on the template.
		 * @param depth the depth
		 * @return this builder
		 */
		public STBuilder deep(int depth) {
			template.depth=depth;
			return this;
		}
		
		/**
		 * Sets an approximate size in kilobyte for the tree
		 * @param size the size
		 * @return this builder
		 */
		public StructuralTemplate totalling(int size) {

			double bytes = size*1024;
			
			double w = template.width==null?defaultWidth:template.width.doubleValue();
			double d = template.depth==null?defaultDepth:template.depth.doubleValue();
			
			int val = (int) rint(bytes/pow(w,d));
			if (template.value!=null)
				logger.warn("leaf value is reset to "+val);
				
			template.value = val;
			
			logger.trace("inferring value as "+template.value);
			
			return this.build();
		}

		/**
		 * Sets the maximum number of attributes on the template.
		 * @param max the maximum number of attributes
		 * @return this builder
		 */
		public STBuilder withAttributes(int max) {
			template.attributes=max;
			return this;
		}

		/**
		 * Sets the maximum size of leaf values on the template.
		 * @param max the maximum size of leaf values
		 * @return this builder
		 */
		public STBuilder withValuesOf(int max) {
			template.value=max*1024;
			return this;
		}
		
		/**
		 * Sets the generation of node identifiers on the template.
		 * @return this builder
		 */
		public STBuilder withIds() {
			template.generateIDs=true;
			return this;
		}
		
		/**
		 * Builds the template.
		 * @return the template
		 */
		public StructuralTemplate build() {
			return template;
		}
	}
	
	
	
	///helpers
	
	private void generateLabels() {

		int lblcount=0;
		labels = new HashMap<Integer, List<String>>();
		for (int i=0; i<this.depth;i++) {
			List<String> lbls = new ArrayList<String>();
			labels.put(i,lbls);
			for (int j=0; j<this.width;j++) {
				String lbl = null; 
				do {
					lbl= randomAlphabetic(random(5,10));
				}
				while (lbl.contains("{") || lbl.contains("}")); //discards labels that cannot be parsed as QNames
				lbls.add(lbl);
			}
			lblcount = lblcount+lbls.size();
		}
		
		logger.trace("using "+lblcount+" different labels");

	}

	private InnerNode inner(int depth) {
		
		Edge[] edges = edges(depth);
		InnerNode node = new InnerNode(newId(),edges);
		
		Map<QName,String> attributes = attributes(depth);
		for (Map.Entry<QName,String> entry : attributes.entrySet())
			node.setAttribute(entry.getKey(), entry.getValue());
		
		//logger.trace("generated node "+(node.id()==null?"":node.id()+" ")+"@"+depth+" @width "+node.edges().size());
		return node;
		
	}
	
	private String newId() {
		return generateIDs?randomUUID().toString():null;
	}
	
	private Leaf leaf(int depth) {
		Leaf l = new Leaf(newId(),randomAlphabetic(value));
		return l;
	}
	
	private Node node(int depth) {
		if (depth==this.depth-1)
			return leaf(depth);
		else
			return inner(depth+1);
	}
	
	private String label(int depth) {
		List<String> lbls = labels.get(depth);  
		return lbls.get(random(0,lbls.size()-1));
	}	
	
	private Edge edge(int depth) {
		String label = label(depth);
		Edge e = new Edge(label,node(depth));
		return e;
	}
	
	
	private Edge[] edges(int depth) {
		Edge[] edges = new Edge[this.width];
		for (int i=0;i<edges.length;i++)
			edges[i] = edge(depth);
		return edges;
	}
	
	private Map<QName,String> attributes(int depth) {
		Map<QName,String> attrs = new HashMap<QName, String>();
		for (int i=0;i<this.attributes;i++)
			attrs.put(new QName(label(depth)),label(depth));
		return attrs;
	}
	
	private int random(int min, int max) {
		return min + (int)(Math.random() * ((max -min) + 1));
	}
}
