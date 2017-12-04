package org.gcube.common.resources.gcore.common;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.resources.gcore.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnyMixedWrapper {

	//this is the element we return to client for modifications
	private Element root = Utils.newDocument().getDocumentElement();
	
	//these are the nodes we deserialise
	@XmlAnyElement
	@XmlMixed
	private List<Object> nodes;

    public Element root() {
    	
    	Element response = root;
    	
    	NodeList list = root.getChildNodes();
    	if (list.getLength()==1) {
    		Node child = list.item(0);
    		if (child instanceof Element)
    			response = (Element) child;
    	}
    	
    	return response;
    }
    
    public String asString() {
    	
    	StringBuilder builder = new StringBuilder();
    	
    	NodeList list = root.getChildNodes();
    	
		for (int i=0; i<list.getLength(); i++) {
			Node child = list.item(i);
			if (child instanceof Element)
				builder.append(toString(child));
			else
				builder.append(child.getTextContent());
		}

		return builder.toString();
    }
    
    public void setString(String text) {
    	try {
    		for (Node node : Utils.parse(text))
    			root.appendChild(root.getOwnerDocument().importNode(node,true));
	    }
		catch(Exception e) {
			throw new RuntimeException("cannot add "+text+" to this resource as it violates well-formedness constraints",e);
		}
    }

    //after deserialisation, we link the nodes to the root 
    @SuppressWarnings("unused")
    private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    	if (nodes!=null) {
    		
    		boolean mixedContent=false;
    		
    		for (Object node: nodes)
    			if (node instanceof String && !String.class.cast(node).matches("\\s*")) {//not whitespace?
    				mixedContent=true;
    				break;
    			}
    		
    		for (Object node: nodes) 
    			if (node instanceof Element)
    				root.appendChild(root.getOwnerDocument().adoptNode((Element)node));
    			else
    				if (mixedContent)
    					root.appendChild(root.getOwnerDocument().createTextNode((String) node));
    					
    	}
    	
    	nodes=null; //flush original input
    }
    
    //before serialisation we copy the child nodes of the root
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
    	nodes = new ArrayList<Object>();
    	NodeList list = root.getChildNodes();
    	for (int i=0;i<list.getLength();i++) {
    		Node node = list.item(i);
    		if (node.getNodeType()==Node.ELEMENT_NODE)
    			nodes.add((Element) node);
    		else
    			nodes.add(node.getTextContent());
    	}
    }
    
	@Override
	public String toString() {
		return "[element=" + root + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnyMixedWrapper other = (AnyMixedWrapper) obj;
		if(((root != null) && (other.root != null))){
			root.normalize();
			other.root.normalize();
		}
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.isEqualNode(other.root)){
			return false;
		}
			
		return true;
	}

    private static TransformerFactory tFactory = TransformerFactory.newInstance();
    
	private static String toString(Node tree) {
	
		try {
		String nodeString = null;
		
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(tree);
		transformer.transform( source, result );
		nodeString = sw.getBuffer().toString();
		return nodeString;
		}
		catch(Exception e) {
			throw new RuntimeException("cannot convert DOM to string",e);
		}
	}
    

}

