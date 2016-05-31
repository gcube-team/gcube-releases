/**
 * 
 */
package org.gcube.data.trees.io;

import static org.gcube.data.trees.data.Nodes.*;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.namespace.QName;

import org.gcube.data.trees.Constants;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.w3c.dom.Element;

/**
 *
 * Static facilities for binding trees to and from other data types.
 * 
 * @author Fabio Simeoni
 * @deprecated since 1.4.0, use {@link XMLBindings} instead
 */
public class Bindings {

	//serialisation constants
	public static final String ROOT_NAME = "root";
	public static final String PREFIX = "t";
	public static final String URI="uri";
	public static final QName ROOT_QNAME = q(Constants.TREE_NS,ROOT_NAME);
	public static final String DEFAULT_NODE_NAME = "node";
	public static final QName NODE_QNAME = q(Constants.TREE_NS,DEFAULT_NODE_NAME);
	public static final String ID_ATTR = "id";
	public static final String STATUS_ATTR = "state";
	public static final String SOURCEID_ATTR = "sourceId";
	
	
	/**
	 * Binds a {@link Tree} to a {@link String}.
	 * @param t the tree
	 * @return the string
	 * @throws Exception if the binding fails
	 */
	public static String toText(Tree t) throws Exception {
		
		return XMLBindings.toString(t);
	}
	
	/**
	 * Binds a {@link Tree} to a {@link Writer}.
	 * @param t the tree
	 * @param stream the writer
	 * @param writeDeclaration (optional) <code>true</code> if the binding is to include the XML declaration (default), 
	 * <code>false</code> otherwise
	 * @throws Exception if the binding fails
	 */
	public static void toWriter(Tree t, Writer stream, boolean ... writeDeclaration) throws Exception {
		
		if (writeDeclaration.length>0 && writeDeclaration[0]==true) {
			WriteOptions options = new WriteOptions();
			options.setWriteXMLDeclaration(true);
			XMLBindings.toStream(t, stream, options);
		}
		else
			XMLBindings.toStream(t, stream);
		
	}
	
	/**
	 * Binds a {@link Reader} to a {@link Tree}
	 * @param reader the reader
	 * @return the tree
	 * @throws Exception if the binding fails
	 */
	public static Tree fromReader(Reader reader) throws Exception {
		
		return XMLBindings.fromStream(reader);
	}
	
	/**
	 * Binds an {@link InputStream} to a {@link Tree}.
	 * @param stream stream
	 * @return the tree
	 * @throws Exception if the binding fails
	 */
	public static Tree fromStream(InputStream stream) throws Exception {
		return XMLBindings.fromStream(stream);
	}
	
	/**
	 * Binds a {@link Reader} to a {@link Node}.
	 * @param r the reader
	 * @return the node
	 * @throws Exception if the binding fails
	 */
	public static Node nodeFromReader(Reader r) throws Exception {
		
		return XMLBindings.nodeFromStream(r);
	}
	
	/**
	 * Binds an {@link InputStream} to a {@link Node}.
	 * @param in the {@link InputStream}
	 * @return the {@link Node}
	 * @throws Exception if the binding fails
	 */
	public static Node nodeFromStream(InputStream in) throws Exception {
		return XMLBindings.nodeFromStream(in);
	}
	
	//dom
	
	/**
	 * Binds a {@link Tree} to an {@link Element}.
	 * @param tree the tree
	 * @return the element
	 * @throws Exception if the binding fails
	 */
	public static Element toElement(Tree tree) throws Exception {
		return XMLBindings.toElement(tree);
	}
	

	/**
	 * Binds an {@link Element} to a {@link Tree}.
	 * @param element the element
	 * @return the tree
	 * @throws Exception if the binding fails
	 */
	public static Tree fromElement(Element element) throws Exception {
		
		return XMLBindings.fromElement(element);
	}

	/**
	 * Binds an {@link Element} to a {@link Node}.
	 * @param element the element
	 * @return the node
	 * @throws Exception if the binding fails
	 */
	public static Node nodeFromElement(Element element) throws Exception {
		
		return XMLBindings.nodeFromElement(element);
	}
	
	/**
	 * Binds a {@link Node} to an {@link Element}.
	 * @param node the node
	 * @param name (optional) a name for the element.
	 * @return the element
	 * @throws Exception if the binding fails
	 */
	public static Element nodeToElement(Node node, QName ... name) throws Exception {
		
		if (name.length>0 && name[0]!=null) {
			WriteOptions options = new WriteOptions();
			options.setRootElement(name[0]);
			return XMLBindings.nodeToElement(node,options);
		}
		else
			return XMLBindings.nodeToElement(node);
	}
	
	
	/**
	 * Binds a {@link Node} to a {@link Writer}.
	 * @param node the node
	 * @param stream the writer
	 * @param name (optional) an element name for the node
	 * @throws Exception if binding fails
	 */
	public static void nodeToWriter(Node node, Writer stream, QName ...name) throws Exception {		
		
		if (name.length>0 && name[0]!=null) {
			WriteOptions options = new WriteOptions();
			options.setRootElement(name[0]);
			XMLBindings.nodeToStream(node,stream,options);
		}
		else
			XMLBindings.nodeToStream(node, stream);
		
	}
	
}
