package org.gcube.data.trees.io;

import static javax.xml.stream.XMLStreamConstants.*;
import static org.gcube.data.trees.io.XMLBindings.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Node.State;
import org.gcube.data.trees.data.Tree;

/**
 * Parses {@link Tree}s from XML representations.
 * 
 * @author Fabio Simeoni
 *
 */
class XMLReader {

	private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

	
	static {
		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
	}
	
	private final XMLStreamReader reader;
	
	/**
	 * Creates an instance from a {@link Source}.
	 * @param source the source
	 */
	XMLReader(Source source) {
		
		try{
			this.reader=inputFactory.createXMLStreamReader(source);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Constructs a {@link Node} from its XML representation in the underlying {@link Source}.
	 * @return the tree
	 */
	Node readNode() {
		
		try {

			reader.nextTag(); //move to root
			
			return _readNode();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Constructs a {@link Tree} from its XML representation in the underlying {@link Source}.
	 * @return the tree
	 */
	Tree read() {
		
		try {

			reader.nextTag(); //move to root
			
			Node node = _readNode();
			
			if (node instanceof Leaf)
				throw new Exception("trees cannot be rooted in leaves like "+reader.getLocalName());
			
			InnerNode inode = (InnerNode) node;
			
			Map<QName,String> attributes = inode.attributes();
			
			String source = attributes.remove(XMLBindings.SOURCE);
			
			Tree tree = new Tree(inode.id(),inode.state(),attributes,inode.edges().toArray(new Edge[0]));
			
			tree.setSourceId(source);
			
			return tree;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Node _readNode() throws Exception {
		
		Map<QName,String> attributes = readAttributes();
		
		
		String id = attributes.remove(ID);
		String s = attributes.remove(STATE);
		
		State state = null;
		try {
			if (s!=null)
				state = State.valueOf(s);
		}
		catch(IllegalArgumentException e) {
			throw new IllegalStateException("invalid node state "+s);
		}

		
		List<Edge> edges = new ArrayList<Edge>();
		String value=null;
			
		String nil = attributes.remove(new QName("http://www.w3.org/2001/XMLSchema-instance","nil"));
		
		if (nil!=null && nil.equals("true"))
			value="nil";

		boolean leaf = false;
		boolean inner = false;
		
		loop:while (reader.hasNext()) {
			
			int next = reader.next();
			switch (next) {
				case START_ELEMENT:
					if (leaf)
						throw new IllegalArgumentException("invalid tree serialisation: found mixed content");
					inner=true;
					QName edgeLabel = reader.getName();
					edges.add(new Edge(edgeLabel,_readNode()));
					break;
				case CHARACTERS:
					if (!reader.getText().trim().isEmpty()) {
						
						if (inner)
							throw new IllegalArgumentException("invalid tree serialisation: found mixed content");
						
						leaf=true;
						value=reader.getText();
					}
					break;
				case END_ELEMENT:
					break loop;
			}
		}
		
		if (value==null) 
		 return new InnerNode(id,state,attributes,edges.toArray(new Edge[0])); 
		
		if (value.equals("nil"))
				value=null;
		
		return new Leaf(id, state, value, attributes);
			
	}
	
	private Map<QName,String> readAttributes() {
		
		Map<QName,String> attributes = new HashMap<QName, String>();
		
		for (int i=0;i<reader.getAttributeCount();i++) {
			String ns = reader.getAttributeNamespace(i);
			String name = reader.getAttributeLocalName(i);
			String prefix = reader.getAttributePrefix(i);
			
			QName attribute = prefix==null? new QName(ns,name) :new QName(ns,name,prefix);
			
			String attrValue = reader.getAttributeValue(i);
			attributes.put(attribute,attrValue);
		}
		
		return attributes;
	}
	
}
