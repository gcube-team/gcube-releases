/**
 * 
 */
package org.gcube.data.trees.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;


/**
 * A set of static methods that implement a simple EDSL of tree expressions.
 * 
 * @author Fabio Simeoni
 *
 */
public class Nodes {

	///////////////////////////////////////////////////// TYPE CONSTANTS
	
	/** Constant for the {@link Leaf} node type. */
	public static final Class<Leaf> L = Leaf.class;
	
	/** Constant for the {@link InnerNode} type. */
	public static final Class<InnerNode> N = InnerNode.class;
	
	
	//////////////////////////////////////////////////// LABEL GENERATORS
	
	/**
	 * Returns a {@link QName} from a local name.
	 * @param name the name
	 * @return the {@link QName}
	 */
	public static QName q(String name){
		return new QName(name);
	}

	/**
	 * Returns a {@link QName} from a namespace and a local name.
	 * @param ns the namespace
	 * @param name the name
	 * @return the {@link QName}
	 */
	public static QName q(String ns,String name){
		return new QName(ns,name);
	}

	/**
	 * Returns a {@link QName} from a prefix, a namespace and a local name.
	 * @param prefix the prefix
	 * @param ns the namespace
	 * @param name the name
	 * @return the {@link QName}
	 */
	public static QName q(String prefix,String ns,String name){
		return new QName(ns,name,prefix);
	}
	
	//////////////////////////////////////////////////// TREE GENERATORS
	
	/**
	 * Clones a {@link Tree}.
	 * @param root the {@link Tree}
	 * @return the clone
	 */
	public static Tree t(Tree root) {
		return new Tree(root);
	}
	
	/**
	 * Creates a {@link Tree} in a given collection with a given identifier and given {@link Edge}s.
	 * @param collID the collection identifier
	 * @param id the identifier
	 * @param edges the {@link Edge}s
	 * @return the {@link Tree}
	 */
	public static Tree t(String collID,String id, Edge ... edges) {
		return new Tree(collID,id,edges);
	}
	
	/**
	 * Creates a {@link Tree} with a given identifier and given {@link Edge}s.
	 * @param id the identifier
	 * @param edges the {@link Edge}s
	 * @return the {@link Tree}
	 */
	public static Tree t(String id, Edge ... edges) {
		return new Tree(id,edges);
	}

	/**
	 * Creates a {@link Tree} with given {@link Edge}s.
	 * @param edges the {@link Edge}s
	 * @return the {@link Tree}
	 */
	public static Tree t(Edge ... edges) {
		return t(null,edges);
	}


	//////////////////////////////////////////////////// INNER-NODE GENERATORS
	
	/**
	 * Clones an {@link InnerNode}.
	 * @param n the {@link InnerNode}
	 * @return the {@link InnerNode}'s clone
	 */
	public static InnerNode n(InnerNode n) {
		return new InnerNode(n);
	}
	
	/**
	 * Creates an {@link InnerNode} with a given identifier and given {@link Edge}e.
	 * @param id the identifier
	 * @param es the {@link Edge}s
	 * @return the {@link InnerNode}
	 */
	public static InnerNode n(String id, Edge ... es) {
		return new InnerNode(id,es);
	}

	/**
	 * Creates an {@link InnerNode} with given {@link Edge}s.
	 * @param es {@link Edge}s
	 * @return the {@link InnerNode}
	 */
	public static InnerNode n(Edge ... es) {
		return n(null,es);
	}
	
	////////////////////////////////////////////////////EDGE GENERATORS
	
	/**
	 * Clones a given {@link Edge}.
	 * @param e the {@link Edge}
	 * @return the clone
	 */
	public static Edge e(Edge e) {
		return new Edge(e);
	}

	/**
	 * Creates an {@link Edge} to a {@link Node}. 
	 * @param name the {@link Edge} label
	 * @param node the {@link Node}
	 * @return the {@link Edge}
	 */
	public static Edge e(QName name,Node node) {
		return new Edge(name,node);
	} 
	
	/**
	 * Creates an {@link Edge} to a {@link Node}. 
	 * @param name the local name of the {@link Edge} label
	 * @param node the {@link Node}
	 * @return the {@link Edge}
	 */
	public static Edge e(String name,Node node) {
		return e(q(name),node);
	} 
	
	/**
	 * Creates an {@link Edge} to a {@link Leaf}. 
	 * @param name the {@link Edge} label
	 * @param v the value of the {@link Leaf}
	 * @return the {@link Edge}
	 */
	public static Edge e(QName name, Object v) {
		return e(name,l(v));
	} 

	/**
	 * Creates an {@link Edge} to a {@link Leaf}. 
	 * @param name the local name of the {@link Edge} label
	 * @param v the value of the {@link Leaf}
	 * @return the {@link Edge}
	 */
	public static Edge e(String name, Object v) {
		return e(q(name),l(v));
	} 

	//////////////////////////////////////////////////// LEAF GENERATORS

	/**
	 * Clones a {@link Leaf}.
	 * @param l the {@link Leaf}
	 * @return the clone
	 */
	public static Leaf l(Leaf l) {
		return l==null?new Leaf(null,(String)null):new Leaf(l);
	}
	
	/**
	 * Returns a {@link Leaf} with a given identifier and a given value.
	 * @param id the identifier
	 * @param v the value
	 * @return the {@link Leaf}
	 */
	public static Leaf l(String id,Object v) {
		return new Leaf(id,v==null?null:toString(v));
	}
	
	/**
	 * Returns a {@link Leaf} with a given value.
	 * @param v the value
	 * @return the {@link Leaf}
	 */
	public static Leaf l(Object v) {
		return l(null,v);
	}
	
	////////////////////////////////////////////////////ATRIBUTE GENERATORS
	
	/**
	 * An attribute with a name and a value. 
	 * */
	 public static class Attribute {
		public QName name;
		public String value;
	}
	 
	/**
	 * Returns a {@link Node} annotated with one or more attributes.
	 * @param <N> the type of the {@link Node}
	 * @param n the {@link Node}
	 * @param attribute the first attribute
	 * @param attributes the remaining attributes
	 * @return the {@link Node}, annotated
	 */
	public static <N extends Node> N attr(N n,Attribute attribute,Attribute ...attributes) {
		n.setAttribute(attribute.name, attribute.value);
		for (Attribute att: attributes) 
			n.setAttribute(att.name, att.value);
		return n;
	}
	
	
	/**
	 * Returns an {@link Attribute} with a given name and a given value.
	 * @param name the local name of the {@link Attribute}.
	 * @param v the value.
	 * @return the {@link Attribute}.
	 * @throws IllegalArgumentException if the local name is <code>null</code>.
	 */
	public static Attribute a(String name, Object v) throws IllegalArgumentException {
		return a(q(name),v);
	}

	
	/**
	 * Returns an {@link Attribute} with a given name and a given value.
	 * @param name the {@link Attribute} name.
	 * @param v the value.
	 * @return the {@link Attribute}.
	 * @throws IllegalArgumentException if the local name is <code>null</code>.
	 */
	public static Attribute a(QName name, Object v) {
		Attribute a = new Attribute();
		a.name=name;
		a.value=toString(v);
		return a;
	}
	
	
	 
	private static DatatypeFactory typeFactory;
	
	static {
		try {
			typeFactory = DatatypeFactory.newInstance();
		}
		catch(DatatypeConfigurationException e) {
			throw new RuntimeException("could not configure datatype factory",e);
		}
	}
	
	//applies appropriate conversions from objects to strings
	private static String toString(Object v) {
		return
			v instanceof java.util.Date?toDateString((java.util.Date) v):
				v instanceof Calendar?toDateString(((Calendar)v).getTime()):String.valueOf(v);
	}
	
	/**
	 * Transforms a {@link java.util.Date} object into {@link String} representation compliant with the dateTime type of XML Schema 
	 * @param date the date
	 * @return the string
	 */
	public static synchronized String toDateString(java.util.Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return typeFactory.newXMLGregorianCalendar(c).toString();
		
	}
	
	/**
	 * Transforms a {@link String} in the format of the dateTime type of XML Schema into a {@link java.util.Date} object.
	 * @param date the string
	 * @return the date
	 * @throws IllegalArgumentException if the string is not a valid representation of date
	 */
	public static synchronized java.util.Date toDate(String date) throws IllegalArgumentException {
		return typeFactory.newXMLGregorianCalendar(date.trim()).toGregorianCalendar().getTime();
		
	}
	
	/**
	 * Indicates whether a label matches a regular expression.
	 * @param lbl the label.
	 * @param regexp the expression
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 */
	public static boolean matches(QName lbl, QName regexp) {
		return lbl.getNamespaceURI().matches(regexp.getNamespaceURI()) && 
		lbl.getLocalPart().matches(regexp.getLocalPart());
	}
}
