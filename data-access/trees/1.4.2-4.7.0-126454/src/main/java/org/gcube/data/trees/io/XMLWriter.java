package org.gcube.data.trees.io;

import static org.gcube.data.trees.io.XMLBindings.*;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;

/**
 * Converts {@link Tree}s to XML representations.
 * 
 * @author Fabio Simeoni
 *
 */
class XMLWriter {

	private static final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	
	static {
		outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, new Boolean(true));
	}
	
	private final XMLStreamWriter writer;
	private final WriteOptions options;
	
	/**
	 * Creates an instance with a {@link Result} and {@link WriteOptions}.
	 * @param result the result
	 * @param options the options
	 */
	XMLWriter(Result result,WriteOptions options) {
		try{
			this.writer=outputFactory.createXMLStreamWriter(result);
			this.options=options;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Converts a {@link Tree} to its XML representation and writes it to the underlying {@link Result}.
	 * @param tree the tree
	 */
	void write(Tree tree) {

		try {
			
			writer.writeStartDocument();
			
			//register optional namespace prefixes
			for (Map.Entry<String,String> decl : options.prefixes().entrySet())
				writer.setPrefix(decl.getKey(), decl.getValue());
			
			//write root element
			QName root = options.rootElement();
			
			if (root.getPrefix().isEmpty())
				writer.writeStartElement(root.getNamespaceURI(),root.getLocalPart());
			else
				writer.writeStartElement(root.getPrefix(),root.getLocalPart(),root.getNamespaceURI());
			
			writeAttributes(tree);
	
			if (tree.sourceId() != null)
				writer.writeAttribute(SOURCE.getNamespaceURI(), SOURCE.getLocalPart(), tree.sourceId());
	
			for (Edge e : tree.edges())
				if (e.target() instanceof InnerNode)
					writeInnerNode(e.label(), (InnerNode) e.target());
				else {
					Leaf leaf = (Leaf) e.target();
					if (leaf.value() == null)
						writeEmptyLeaf(e.label(), leaf);
					else
						writeLeaf(e.label(), leaf);
				}
	
			writer.writeEndElement();
			
			writer.writeEndDocument();
			
			writer.flush();
			writer.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Converts a {@link Node} to its XML representation and writes it to the underlying {@link Result}.
	 * @param node the node
	 */
	void writeNode(Node node) {

		try {
			
			writer.writeStartDocument();
			
			if (node instanceof Leaf)
			 writeLeaf(options.rootElement(), (Leaf) node);
			else
			 writeInnerNode(options.rootElement(), (InnerNode) node);
			
			writer.writeEndDocument();
			
			writer.flush();
			writer.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void writeAttributes(Node node) throws Exception {

		
		for (Map.Entry<QName, String> attr : node.attributes().entrySet()) {
			if (attr.getValue() == null)
				continue;
			String prefix = attr.getKey().getPrefix();
			String ns = attr.getKey().getNamespaceURI();
			String name = attr.getKey().getLocalPart();
			String value = attr.getValue();
			if (!prefix.isEmpty())
				writer.writeAttribute(prefix, ns, name, value);
			else if (!ns.isEmpty()) {
				writer.writeAttribute(ns, name, value);
			} else
				writer.writeAttribute(name, value);
		}

		if (node.id() != null)
			writer.writeAttribute(ID.getNamespaceURI(), ID.getLocalPart(), node.id());

		if (node.state() != null)
			writer.writeAttribute(STATE.getNamespaceURI(), STATE.getLocalPart(), node.state().name());

	}

	private void writeInnerNode(QName label, InnerNode node) throws Exception {

		if (!label.getPrefix().isEmpty())
			writer.writeStartElement(label.getPrefix(), label.getLocalPart(), label.getNamespaceURI());
		else if (!label.getNamespaceURI().isEmpty()) {
			writer.writeStartElement(label.getNamespaceURI(), label.getLocalPart());
		} else
			writer.writeStartElement(label.getLocalPart());

		writeAttributes(node);

		for (Edge e : node.edges())
			if (e.target() instanceof InnerNode)
				writeInnerNode(e.label(), (InnerNode) e.target());
			else
				writeLeaf(e.label(), (Leaf) e.target());

		writer.writeEndElement();
	}

	private void writeLeaf(QName label, Leaf leaf) throws Exception {

		if (!label.getPrefix().isEmpty())
			writer.writeStartElement(label.getPrefix(), label.getLocalPart(), label.getNamespaceURI());
		else if (!label.getNamespaceURI().isEmpty()) {
			writer.writeStartElement(label.getNamespaceURI(), label.getLocalPart());
		} else
			writer.writeStartElement(label.getLocalPart());

		writeAttributes(leaf);

		writer.writeCharacters(leaf.value());

		writer.writeEndElement();

	}

	private  void writeEmptyLeaf(QName label, Node leaf) throws Exception {

		if (!label.getPrefix().isEmpty())
			writer.writeEmptyElement(label.getPrefix(), label.getLocalPart(), label.getNamespaceURI());
		else if (!label.getNamespaceURI().isEmpty()) {
			writer.writeEmptyElement(label.getNamespaceURI(), label.getLocalPart());
		} else
			writer.writeEmptyElement(label.getLocalPart());

		writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "true");

		writeAttributes(leaf);

	}
}
