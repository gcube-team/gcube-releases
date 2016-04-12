package org.gcube.common.resources.gcore.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;

import org.gcube.common.resources.gcore.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnyWrapper {

	//this is the element we return to client for modifications
	private Element root = Utils.newDocument().getDocumentElement();
	
	//these are the elements we deserialise
	@XmlAnyElement
	private List<Element> elements;

    public Element root() {
        return root;
    }

    //after deserialisation, we link the elements to the root 
    @SuppressWarnings("unused")
    private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    	if (elements!=null)
    		for (Element e: elements) 
    			root.appendChild(root.getOwnerDocument().adoptNode(e));
    	elements=null;
    }
    
    //before serialisation we copy the child elements of the root
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
    	elements = new ArrayList<Element>();
    	NodeList list = root.getChildNodes();
    	for (int i=0;i<list.getLength();i++) {
    		Node node = list.item(i);
    		if (node.getNodeType()==Node.ELEMENT_NODE)
    			elements.add((Element) node);
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
		AnyWrapper other = (AnyWrapper) obj;
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

    
    

}

