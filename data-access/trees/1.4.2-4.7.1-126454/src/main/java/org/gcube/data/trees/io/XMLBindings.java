/**
 * 
 */
package org.gcube.data.trees.io;

import static org.gcube.data.trees.Constants.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Static facilities for converting {@link Tree}s to and from XML representations.
 * 
 * @author Fabio Simeoni
 * 
 */
public class XMLBindings {

	// factory
	private static final DocumentBuilder docFactory;
	private static final TransformerFactory tracFactory = TransformerFactory.newInstance();

	// serialisation constants
	public static final QName ID = new QName(TREE_NS, "id");
	public static final QName STATE = new QName(TREE_NS, "state");
	public static final QName SOURCE = new QName(TREE_NS, "source");

	private static final WriteOptions defaultOptions = new WriteOptions();

	static {

		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		df.setNamespaceAware(true);

		try {
			docFactory = df.newDocumentBuilder();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// /////STRING REPRESENTATIONS

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @return the XML representation of the tree
	 * @throws RuntimeException if the conversion fails
	 */
	public static String toString(Tree tree) {
		return toString(tree, defaultOptions);
	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @return the XML representation of the node
	 * @throws RuntimeException if the conversion fails
	 */
	public static String nodeToString(Node node) {
		return nodeToString(node, defaultOptions);
	}

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @param custom options for the conversion
	 * @return the XML representation of the tree
	 * @throws RuntimeException if the conversion fails
	 */
	public static String toString(Tree tree, WriteOptions options) {

		StreamResult result = new StreamResult(new StringWriter());

		toResult(tree, result, options);

		return result.getWriter().toString();

	}

	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @param custom options for the conversion
	 * @return the XML representation of the node
	 * @throws RuntimeException if the conversion fails
	 */
	public static String nodeToString(Node node, WriteOptions options) {

		StreamResult result = new StreamResult(new StringWriter());

		nodeToResult(node, result, options);

		return result.getWriter().toString();

	}

	/**
	 * Parses a {@link Tree} from its XML representation.
	 * 
	 * @param xml the XML representation
	 * @return the tree parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Tree fromString(String xml) {
		return fromSource(new StreamSource(new StringReader(xml)));
	}

	/**
	 * Parses a {@link Node} from its XML representation.
	 * 
	 * @param xml the XML representation
	 * @return the node parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Node nodeFromString(String xml) {
		return nodeFromSource(new StreamSource(new StringReader(xml)));
	}
	
	// /////STREAM REPRESENTATIONS

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @param stream a stream where to write out the tree representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static void toStream(Tree tree, Writer stream) {
		toStream(tree, stream, defaultOptions);
	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @param stream a stream where to write out the node representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static void nodeToStream(Node node, Writer stream) {
		nodeToStream(node, stream, defaultOptions);
	}

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @param stream a stream where to write out the tree representation
	 * @param custom options for the conversion
	 * @throws RuntimeException if the conversion fails
	 */
	public static void toStream(Tree tree, Writer stream, WriteOptions options) {
		toResult(tree, new StreamResult(stream), options);
	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @param stream a stream where to write out the node representation
	 * @param custom options for the conversion
	 * @throws RuntimeException if the conversion fails
	 */
	public static void nodeToStream(Node node, Writer stream, WriteOptions options) {
		nodeToResult(node, new StreamResult(stream), options);
	}

	/**
	 * Parses a {@link Tree} from its XML representation.
	 * 
	 * @param stream a stream with the XML representation of the tree
	 * @return the tree parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Tree fromStream(Reader stream) {
		return fromSource(new StreamSource(stream));
	}
	
	/**
	 * Parses a {@link Node} from its XML representation.
	 * 
	 * @param stream a stream with the XML representation of the node
	 * @return the node parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Node nodeFromStream(Reader stream) {
		return nodeFromSource(new StreamSource(stream));
	}

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @param stream a stream where to write out the tree representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static void toStream(Tree tree, OutputStream stream) {
		toStream(tree, stream, defaultOptions);
	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @param stream a stream where to write out the node representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static void nodetoStream(Node node, OutputStream stream) {
		nodeToStream(node, stream, defaultOptions);
	}

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * 
	 * @param tree the tree
	 * @param stream a stream where to write out the tree representation
	 * @param custom options for the conversion
	 * @throws RuntimeException if the conversion fails
	 */
	public static void toStream(Tree tree, OutputStream stream, WriteOptions options) {
		toResult(tree, new StreamResult(stream), options);
	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * 
	 * @param node the node
	 * @param stream a stream where to write out the node representation
	 * @param custom options for the conversion
	 * @throws RuntimeException if the conversion fails
	 */
	public static void nodeToStream(Node node, OutputStream stream, WriteOptions options) {
		nodeToResult(node, new StreamResult(stream), options);
	}

	/**
	 * Parses a {@link Tree} from its XML representation.
	 * 
	 * @param stream a stream with the XML representation of the tree
	 * @return the tree parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Tree fromStream(InputStream stream) {
		return fromSource(new StreamSource(stream));
	}
	
	/**
	 * Parses a {@link Node} from its XML representation.
	 * 
	 * @param stream a stream with the XML representation of the node
	 * @return the node parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Node nodeFromStream(InputStream stream) {
		return nodeFromSource(new StreamSource(stream));
	}

	// /////DOM REPRESENTATIONS

	/**
	 * Converts a {@link Tree} to its XML representation.
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the tree is first converted to an in-memory string-based
	 * 
	 * @param tree the tree
	 * @return the root {@link Element} of a DOM model of the XML representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static Element toElement(Tree tree) {
		return toElement(tree, defaultOptions);
	}

	/**
	 * Converts a {@link Tree} to its XML representation. *
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the tree is first converted to an in-memory string-based
	 * 
	 * @param tree the tree
	 * @param custom options for the conversion
	 * @return the root {@link Element} of a DOM model of the XML representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static Element toElement(Tree tree, WriteOptions options) {

		return toElement(toString(tree,options));

	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the node is first converted to an in-memory string-based
	 * 
	 * @param node the node
	 * @param custom options for the conversion
	 * @return the root {@link Element} of a DOM model of the XML representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static Element nodeToElement(Node node, WriteOptions options) {

		return toElement(nodeToString(node,options));

	}
	
	/**
	 * Converts a {@link Node} to its XML representation.
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the node is first converted to an in-memory string-based
	 * 
	 * @param node the node
	 * @return the root {@link Element} of a DOM model of the XML representation
	 * @throws RuntimeException if the conversion fails
	 */
	public static Element nodeToElement(Node node) {

		return toElement(nodeToString(node,defaultOptions));

	}

	
	private static Element toElement(String xml) {

		try {
			DOMResult result = new DOMResult(docFactory.newDocument());
			tracFactory.newTransformer().transform(new StreamSource(new StringReader(xml)), result);
			return Document.class.cast(result.getNode()).getDocumentElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Parses a {@link Tree} from its XML representation.
	 * 
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the representation is first converted to a string.
	 * 
	 * @param element the root {@link Element} of a DOM model of the XML representation
	 * @return the tree parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Tree fromElement(Element element) {

		return fromString(_fromElement(element));
		
	}
	
	/**
	 * Parses a {@link Node} from its XML representation.
	 * 
	 * <p>
	 * Note that this is a derived thus unoptimised conversion, the representation is first converted to a string.
	 * 
	 * @param element the root {@link Element} of a DOM model of the XML representation
	 * @return the node parsed from the representation
	 * @throws RuntimeException if the representation cannot be parsed
	 */
	public static Node nodeFromElement(Element element) {

		return nodeFromString(_fromElement(element));
		
	}
	
	public static String _fromElement(Element element) {

		try {
			DOMSource source = new DOMSource(element);
			StringWriter w = new StringWriter();
			tracFactory.newTransformer().transform(source, new StreamResult(w));
			return w.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// /////GENERIC REPRESENTATIONS

	// we cannot make it public yet, as the jdk stack at the moment does not seem to offer full support for Source and
	// Result
	// implementation

	private static Tree fromSource(Source source) {
		return new XMLReader(source).read();
	}
	
	private static Node nodeFromSource(Source source) {
		return new XMLReader(source).readNode();
	}

	private static void toResult(Tree tree, Result result, WriteOptions options) {

		new XMLWriter(result, options).write(tree);

	}
	
	private static void nodeToResult(Node node, Result result, WriteOptions options) {

		new XMLWriter(result, options).writeNode(node);

	}

}
