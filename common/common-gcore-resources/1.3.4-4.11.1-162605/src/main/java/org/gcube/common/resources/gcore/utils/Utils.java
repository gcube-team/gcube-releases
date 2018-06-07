package org.gcube.common.resources.gcore.utils;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Library-wide implementation utilities.
 * 
 * 
 */
public class Utils {

	static Document emptyDocument;
	static SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	static DocumentBuilder builder; 
	
	static {
		DocumentBuilderFactory.newInstance();
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			emptyDocument=builder.newDocument();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<Node> parse(String text) throws Exception {
		String wrapped = "<doc>"+text+"</doc>";
		List<Node> nodes = new ArrayList<Node>();
		Document document = builder.parse(new InputSource(new StringReader(wrapped)));
		NodeList children = document.getDocumentElement().getChildNodes();
		for (int i=0;i<children.getLength();i++)
			nodes.add(children.item(i));
		return nodes;
	}
	
	public static Document newDocument() {
		Document document = builder.newDocument();
		document.appendChild(document.createElement("doc"));
		return document;
	}

	
	public static String toString(Calendar c) {
		return dateFormat.format(c.getTime());
	}
	
	public static void notNull(String name, Object value) throws IllegalArgumentException {
		if (value==null)
			throw new IllegalArgumentException("parameter "+ name+" is null");
	}
}
