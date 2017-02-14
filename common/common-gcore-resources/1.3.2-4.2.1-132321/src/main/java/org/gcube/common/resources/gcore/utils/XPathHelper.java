package org.gcube.common.resources.gcore.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple XPath evaluator.
 * 
 * @author Fabio Simeoni
 *
 */
public class XPathHelper {

	/** XPath engine. */;
	private static XPathFactory factory = XPathFactory.newInstance();
	
	private XPath engine = factory.newXPath();
	
	private Map<String,String> namespaces = new HashMap<String,String>();
	
	private NamespaceContext nsContext = new NamespaceContext() {
        
		public String getNamespaceURI(String prefix) {
			return namespaces.get(prefix);
        }
       
        public Iterator<?> getPrefixes(String val) {
        	return null;
        }
       
        public String getPrefix(String uri) {
        	return null;
        }
    };
	
	/** Transformation engine. */
	private static Transformer transformer;

	static {
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private final Node root;

	/**
	 * Creates an instance with a {@link Node} .
	 * @param root the node
	 */
	public XPathHelper(Node root) {
		this.root=root;
		engine.setNamespaceContext(nsContext);
	}
	
	/**
	 * Declares a namespace for XPath evaluation. 
	 * @param prefix the namespace prefix
	 * @param uri the namespace URI
	 */
	public void addNamespace(String prefix,String uri) {
		namespaces.put(prefix,uri);
	}
	
	/**
	 * Evaluates an XPath expression and returns matching values as strings.
	 * @param xpath the expression
	 * @return the list matching values
	 */
	public List<String> evaluate(String xpath) {
		
		List<String> results = new ArrayList<String>();
		
		try {
			
			NodeList set = evaluateForNodes(xpath);
			
			for (int i=0;i<set.getLength();i++) {
				StreamResult sr = new StreamResult(new StringWriter());
				transformer.transform(new DOMSource(set.item(i)),sr);
				results.add(sr.getWriter().toString());
			}
		
			return results;
			
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Evaluates an XPath expression and returns matching values as a {@link NodeList}.
	 * @param xpath the expression
	 * @return the list matching values
	 */
	public NodeList evaluateForNodes(String xpath) {
		
		try {			
			return (NodeList) engine.evaluate(xpath,root, XPathConstants.NODESET);

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
