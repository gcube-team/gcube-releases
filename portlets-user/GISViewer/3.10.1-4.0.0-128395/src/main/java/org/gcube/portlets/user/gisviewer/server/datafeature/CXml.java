/**
 * 
 */
package org.gcube.portlets.user.gisviewer.server.datafeature;

/**
 * @author ceras
 *
 */
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Class CXml - Ceras XML
 * With Ceras XML, XML manipulation is easy!
 * @author Ceras
 *
 */
public class CXml {

	private enum Type { DOCUMENT, ELEMENT, ELEMENTS, NULL };
	
	private Type type;
	private Document doc;
	private Element element;
	private String text=null;
	private List<Element> elements = new ArrayList<Element>();
	private List<CXml> children = new ArrayList<CXml>();

	public CXml(Document doc, String tagName) {
		this.doc = doc;
		this.element = doc.createElement(tagName);
		this.type = Type.DOCUMENT;
	}

	public CXml(Document doc, String tagName, String text) {
		this.doc = doc;
		this.element = doc.createElement(tagName);
		this.text(text);
		this.type = Type.DOCUMENT;
	}

	/**
	 * @param doc
	 */
	public CXml(Document doc) {
		this.doc = doc;
		this.element = doc.getDocumentElement();
		this.type = Type.DOCUMENT;
	}
	
	public CXml(Element e) {
		this.element = e;
		
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData)
	       this.text = ((CharacterData) child).getData();
		
		this.type = Type.ELEMENT;
	}
	
	public CXml(List<Element> elements) {
		this.elements = elements;
		this.type = Type.ELEMENTS;
	}
	
	/**
	 * 
	 */
	public CXml() {
		this.type = Type.NULL;
	}

	/**
	 * @param xml
	 */
	public CXml(String xml) {
		this(CXml.getDocument(xml));
	}

	public String tagName() {
		try {
			return this.element.getTagName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void attr(String name, String value) {
		Attr attr = doc.createAttribute(name);
		attr.setValue(value);
		element.getAttributes().setNamedItem(attr);
	}

	public void attr(String name, int value) {
		attr(name, ""+value);
	}

	public String attr(String name) {
		if (type==Type.DOCUMENT || type==Type.ELEMENT)
			return element.getAttribute(name);
		else
			return null;
	}

	// alias di attr(String name)
	public String getAttr(String name) {
		return attr(name);
	}

	public void text(String text) {
		if (text!=null) {
			removeChildren();
			element.appendChild(doc.createTextNode(text));
			this.text = text;
		}
	}

	// alias di text(String text)
	public void setText(String text) {
		text(text);
	}

	public String text() {
		return text;
	}
	
	// alias di text()
	public String getText() {
		return text();
	}

	public void append(CXml cXml) {
		element.appendChild(cXml.element);
		children.add(cXml);
	}

	public void appendTo(CXml cXml) {
		cXml.element.appendChild(element);
		cXml.children.add(this);
	}

	public void removeChildren() {
		NodeList nodeList = element.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			element.removeChild(node);
		}
		children.clear();
	}

	public String toString() {
		doc.appendChild(element);

		Transformer transformer;
		String xmlString = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

			xmlString = result.getWriter().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlString;
	}
	
	public void each(CXmlManager manager) {
		if (type==Type.DOCUMENT || type==Type.ELEMENT)
			manager.manage(0, this);
		else if (type==Type.ELEMENTS) {
			int i=0;
			for (Element e : elements) {
				CXml cXml = new CXml(e);
				manager.manage(i++, cXml);
			}
		}
	}

	public static Document getNewDocument() {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
	public static Document getDocument(String xml) {
		Document doc = null;
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = docBuilder.parse(is);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * @param string
	 * @return
	 */
	public CXml find(String tagName) {
		if (type==Type.DOCUMENT || type==Type.ELEMENT) {
			NodeList nodeList = element.getElementsByTagName(tagName);
			
			if (nodeList.getLength()==1)
				return new CXml((Element)nodeList.item(0));
			else if (nodeList.getLength()>1) {
				List<Element> elements = new ArrayList<Element>();
				for (int i=0; i<nodeList.getLength(); i++)
					elements.add((Element)nodeList.item(i));
				return new CXml(elements);
			} else
				return new CXml(); // null cxml
		}
		
		return new CXml(); // null cxml
	}
	
	/**
	 * @param string
	 * @return
	 */
	public CXml findFirst(String tagName) {
		if (type==Type.DOCUMENT || type==Type.ELEMENT) {
			NodeList nodeList = element.getElementsByTagName(tagName);
			if (nodeList.getLength()>0)
				return new CXml((Element)nodeList.item(0));
		}
		
		return new CXml(); // null cxml
	}
	
	/**
	 * @param string
	 * @return all children CXml nodes
	 */
	public CXml children() {
		List<Element> elements = new ArrayList<Element>();
		if (type==Type.DOCUMENT || type==Type.ELEMENT) {
			NodeList nodeList = element.getChildNodes();
			for (int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element e = (Element)node;
					elements.add(e);
				}
			}
			return new CXml(elements);
		}
		
		return new CXml(); // null cxml
	}

	/**
	 * @param string
	 * @return
	 */
	public CXml children(String tagName) {
		List<Element> elements = new ArrayList<Element>();
		if (type==Type.DOCUMENT || type==Type.ELEMENT) {
			NodeList nodeList = element.getChildNodes();
			for (int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element e = (Element)node;
					if  (e.getTagName().equalsIgnoreCase(tagName))
						elements.add(e);
				}
			}
			return new CXml(elements);
		}
		
		return new CXml(); // null cxml
	}
	
	
	public CXml child(String tagName) {
		if (type==Type.DOCUMENT || type==Type.ELEMENT) {
			NodeList nodeList = element.getChildNodes();
			int l = nodeList.getLength();
			for (int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					Element e = (Element)node;
					if  (e.getTagName().equalsIgnoreCase(tagName))
						return new CXml(e);
				}
			}			
		}
		return new CXml(); // null cxml
	}

	public boolean isNull() {
		return (type==Type.NULL);
	}	
	
	public boolean isDocument() {
		return (type==Type.DOCUMENT);
	}	
	
	public boolean isElement() {
		return (type==Type.DOCUMENT || type==type.ELEMENT);
	}	
	
	public boolean isElements() {
		return (type==Type.ELEMENTS);
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TEST CREATE
//		Document doc = CXml.getNewDocument();
//		
//		CXml root = new CXml(doc, "ciccio");
//		root.attr("id", "41");
//		root.append(new CXml(doc, "work", "team director"));
//		root.append(new CXml(doc, "work", "analist programmer"));
//		
//		String str = root.toString();
//		str = str.replaceAll("work", "Work");
//		
//		System.out.println(str);
		
		

		// TEST READ
		String xml = "" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<people>" +
			"	<member id='21'>" +
			"		<name>ciccio</name>" +
			"		<surname>ceras</surname>" +
			"		<category>senior programmer</category>" +
			"	</member>" +
			"	<member id='16'>" +
			"		<name>pinco</name>" +
			"		<surname>pallino</surname>" +
			"		<category>analist programmer</category>" +
			"	</member>" +
			"</people>";
		
		
		// get root element
		CXml rootPeople = new CXml(xml);

		// iterate members with "each" method		
		rootPeople.children("member").each(new CXmlManager() {
			
			public void manage(int i, CXml member) {
				System.out.println(
					"MEMBER "+member.attr("id") + ": " +
					member.child("name").text + " " +
					member.child("surname").text + " (" + 
					member.child("category").text +")"
				);
			}
			
		});

		/* 
		OUTPUT:
		
		MEMBER 21: ciccio ceras (senior programmer)
		MEMBER 16: pinco pallino (analist programmer)
		*/
		
		
		
		
		/*
		<ciccio id="41">
			<work>team director</work>
			<work>analist programmer</work>
		</ciccio>
		 */
		/*
		try {			
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Node root = doc.createElement("ciccio");
		Node el1 = doc.createElement("work");
		Node el2 = doc.createElement("work");
		root.appendChild(el1);
		root.appendChild(el2);
		el1.appendChild(doc.createTextNode("analista programmatore"));
		el2.appendChild(doc.createTextNode("team director"));
		Attr rootId = doc.createAttribute("id");
		rootId.setValue("42");
		root.getAttributes().setNamedItem(rootId);
		
		doc.appendChild(root);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		//initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}