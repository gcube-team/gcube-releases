/**
 * 
 */
package org.gcube.data.tm.utils;

import static org.gcube.data.trees.io.XMLBindings.*;
import static org.gcube.data.trees.patterns.Patterns.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.MessageElement;
import org.gcube.data.tm.stubs.AnyHolder;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Library-wide utilities.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Utils {

	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	/**
	 * Checks that the input is not <code>null</code>
	 * 
	 * @param name the name of the value to report in error messages
	 * @param value the value
	 * @throws IllegalArgumentException if the value is <code>null</code>
	 */
	private static void notNull(String name, Object value) throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException(name + " is null");
	}

	/**
	 * Checks that one or more values are not <code>null</code>.
	 * 
	 * @param values the values
	 * @throws IllegalArgumentException if any of the input values is <code>null</code>
	 */
	public static void notNull(Object... values) throws IllegalArgumentException {

		for (Object v : values)
			notNull(v.getClass().getSimpleName(), v);
	}

	/**
	 * Converts a {@link Pattern} into an {@link AnyHolder} accepted by the service.
	 * 
	 * @param p the pattern
	 * @return the holder
	 * */
	public static AnyHolder toHolder(Pattern p) {

		if (p == null)
			return null;

		try {
			Document filterNode = factory.newDocumentBuilder().newDocument();
			Patterns.getMarshaller().marshal(p, filterNode);
			return toHolder(filterNode.getDocumentElement());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Converts a {@link AnyHolder} returned by the service into a {@link Pattern}.
	 * 
	 * @param h the holder
	 * @return the pattern
	 * @throws Exception if the conversion fails
	 */
	public static Pattern toPattern(AnyHolder h) throws Exception {

		return h == null ? null : (Pattern) getUnMarshaller().unmarshal(h.get_any()[0].getAsDOM());

	}

	/**
	 * Transforms a {@link Tree} into a {@link AnyHolder} accepted by the service.
	 * 
	 * @param t the tree
	 * @return the holder
	 * @throws Exception if the conversion fails
	 */
	public static AnyHolder toAnyHolder(Tree t) {

		try {
			return t == null ? null : toHolder(XMLBindings.toElement(t));
		}
		catch(Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

	/**
	 * Converts an {@link Element} into a {@link AnyHolder} accepted by the service.
	 * 
	 * @param e the element
	 * @return the holder
	 */
	public static AnyHolder toHolder(Element e) {

		return e == null ? null : new AnyHolder(new MessageElement[] { new MessageElement(e) });

	}

	/**
	 * Transforms a {@link Node} into a {@link AnyHolder} accepted by the service.
	 * 
	 * @param n the node
	 * @return the holder
	 * @throws Exception if the conversion fails
	 */
	public static AnyHolder toHolder(Node n) throws Exception {

		return n == null ? null : toHolder(XMLBindings.nodeToElement(n));

	}

	/**
	 * Converts an {@link AnyHolder} into an {@link Element}.
	 * 
	 * @param d the holder
	 * @return the element
	 */
	public static Element toElement(AnyHolder d) {
		return d == null ? null : d.get_any()[0];
	}

	/**
	 * Converts an {@link AnyHolder} into an {@link Element}.
	 * 
	 * @param h the holder
	 * @return the element
	 * @throws Exception if the conversion fails
	 */
	public static Tree toTree(AnyHolder h) throws Exception {
		return fromElement(toElement(h));
	}
	

}
