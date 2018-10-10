package gr.uoa.di.madgik.commons.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Utility class to parse and traverse an XML DOM tree
 *
 * @author gpapanikos
 */
public class XMLUtils
{

	public static void Serialize(String FileName, String XML) throws Exception
	{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(XML)));
			
			TransformerFactory tf=TransformerFactory.newInstance();
			Transformer tr=tf.newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD,"xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(new File(FileName))));
//		import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//		import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//		OutputFormat format = new OutputFormat(doc);
//		format.setIndenting(true);
//		XMLSerializer serializer = new XMLSerializer(new FileOutputStream(new File(FileName)), format);
//		serializer.serialize(doc);
	}
	
	public static String Serialize(Node node,boolean omitDeclaration) throws Exception
	{
		if (node == null)
		{
			throw new Exception("Cannot serialize null element");
		}
		StringWriter writer =null;
		writer = new StringWriter();
		
		Transformer tr=TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, (omitDeclaration ? "yes" : "no"));
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD,"xml");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
		tr.transform(new DOMSource(node), new StreamResult(writer));
		writer.flush();
		return writer.toString();
	}
	
	public static String Serialize(Node node) throws Exception
	{
		return XMLUtils.Serialize(node, false);
		
//		import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//		import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//		OutputFormat format = new OutputFormat();
//		format.setIndenting(true);
//		XMLSerializer serializer = new XMLSerializer(writer, format);
//		serializer.serialize((Element) node);
//		writer.flush();
//		return writer.toString();
	}

	/**
	 * Parses the provided XML string
	 *
	 * @param XML the xml to parse
	 * @return the {@link Document} constructed
	 * @throws java.lang.Exception the deserialization could not be performed
	 */
	public static Document Deserialize(String XML) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(XML)));
		return doc;
	}

	public static Document Deserialize(File XML) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new FileReader(XML)));
		return doc;
	}

	/**
	 * Checks if the provided attribute exists in the supplied node
	 *
	 * @param node the Node that should hold the attribute
	 * @param attributeName the name of the attribute to check for
	 * @return whether or not the attribute exists
	 * @throws java.lang.Exception The operation could not be performed
	 */
	public static Boolean AttributeExists(Element node, String attributeName) throws Exception
	{
		String val = node.getAttribute(attributeName);
		if (val.trim().length() == 0)
		{
			return false;
		}
		return true;
	}

	/**
	 * Retrieves an attribute's value
	 *
	 * @param node The node the attribute should exist in
	 * @param attributeName the name of the attribute
	 * @return the value of the attribute
	 * @throws java.lang.Exception The attribute does not exist
	 */
	public static String GetAttribute(Element node, String attributeName) throws Exception
	{
		String val = node.getAttribute(attributeName);
		if (val.trim().length() == 0)
		{
			throw new Exception("Specified attribute not present");
		}
		return val;
	}

	public static String[] GetAttributes(Element node, boolean CollapseNamespase) throws Exception
	{
		NamedNodeMap map = node.getAttributes();
		String[] attrs = new String[map.getLength()];
		for (int i = 0; i < map.getLength(); i += 1)
		{
			if (CollapseNamespase)
			{
				attrs[i] = map.item(i).getLocalName();
			} else
			{
				attrs[i] = map.item(i).getNodeName();
			}
		}
		return attrs;
	}
	
	public static String SerializeChild(Element parent) throws Exception
	{
		NodeList childer=parent.getChildNodes();
		for(int i=0;i<childer.getLength();i+=1)
		{
			if((childer.item(i) instanceof Element)) return XMLUtils.Serialize(childer.item(i),true);
		}
		return null;
	}

	/**
	 * Retrieves a single child element of the provided node that has the provided name. If more
	 * than one elements have the same name, only the first one is returned
	 *
	 * @param Parent the node to check its children
	 * @param name the name of the child node to retrieve
	 * @return the child element or null if it doesn't exist
	 * @throws java.lang.Exception the Operation could not be performed
	 */
	public static Element GetChildElementWithName(Node Parent, String name) throws Exception
	{
		if (Parent == null)
		{
			return null;
		}
		NodeList children = Parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i += 1)
		{
			if ((children.item(i) instanceof Element) && (((Element) children.item(i)).getNodeName().equals(name)))
			{
				return (Element) children.item(i);
			}
		}
		return null;
	}

	public static Element GetChildElementWithNameAndNamespace(Node Parent,String name,String ns) throws Exception
	{
		if(Parent==null) return null;
		NodeList children = Parent.getChildNodes();
		for(int i=0;i<children.getLength();i+=1)
		{
			if((children.item(i) instanceof Element) && (((Element)children.item(i)).getLocalName().equals(name)) && (((Element)children.item(i)).getNamespaceURI().equals(ns)))
			{
				return (Element)children.item(i);
			}
		}
		return null;
	}

	/**
	 * Retrieves all child elements of the provided node that have the provided name
	 *
	 * @param Parent the node to check its children
	 * @param name the name of the child node to retrieve
	 * @return the child element or null if it doesn't exist
	 * @throws java.lang.Exception the Operation could not be performed
	 */
	public static List<Element> GetChildElementsWithName(Node Parent, String name) throws Exception
	{
		List<Element> elems = new ArrayList<Element>();
		if (Parent == null)
		{
			return elems;
		}
		NodeList children = Parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i += 1)
		{
			if ((children.item(i) instanceof Element) && (((Element) children.item(i)).getNodeName().equals(name)))
			{
				elems.add((Element) children.item(i));
			}
		}
		return elems;
	}

	public static List<Element> GetChildElementsWithNameAndNamespace(Node Parent,String name,String ns) throws Exception
	{
		List<Element> elems=new ArrayList<Element>();
		if(Parent==null) return elems;
		NodeList children = Parent.getChildNodes();
		for(int i=0;i<children.getLength();i+=1)
		{
			if((children.item(i) instanceof Element) && (((Element)children.item(i)).getLocalName().equals(name)) && (((Element)children.item(i)).getNamespaceURI().equals(ns)))
			{
				elems.add((Element)children.item(i));
			}
		}
		return elems;
	}
	
	public static String GetChildTextOrFirstNonEmptyChildElementAsText(Node Parent) throws Exception
	{
		String ret= XMLUtils.GetChildText(Parent);
		if(ret!=null) return ret;
		NodeList lst= Parent.getChildNodes();
		for(int i=0;i<lst.getLength();i+=1)
		{
			if(lst!=null && (lst instanceof Element))
			{
				return XMLUtils.Serialize(lst.item(i));
			}
		}
		return null;
	}
	
	public static String GetChildCDataText(Node Parent) throws Exception
	{
		if(Parent==null) return null;
		NodeList children = Parent.getChildNodes();
		if(children==null) return null;
		String ret=null;
		for(int i=0;i<children.getLength();i+=1)
		{
			if((children.item(i) instanceof CDATASection))
			{
				ret=((CDATASection)children.item(i)).getNodeValue();
				break;
			}
		}
		return ret;
	}

	/**
	 * Retrieves the text payload of the first available Text node that is a member of the provided
	 * node children
	 *
	 * @param Parent The node whose child text node's payload should be retrieved
	 * @return The text or null if no text node could be found
	 * @throws java.lang.Exception the Operation could not be performed
	 */
	public static String GetChildText(Node Parent) throws Exception
	{
		if(Parent==null) return null;
		NodeList children = Parent.getChildNodes();
		if(children==null) return null;
		String ret=null;
		for(int i=0;i<children.getLength();i+=1)
		{
			if((children.item(i) instanceof Text))
			{
				ret=((Text)children.item(i)).getWholeText();
				break;
			}
		}
		return ret;
	}

	public static Object Query(Document doc, String query, QName type) throws Exception
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		return xpath.evaluate(query, doc, type);
	}
	
	public static String Transform(String Source, String xslt) throws Exception 
	{
		StringWriter writer =null;
		writer = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(xslt)));
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new StreamSource(new StringReader(Source)),new StreamResult(writer));
		writer.flush();
		return writer.toString();

	}

	/**
	 * Replaces special characters with xml valid escape sequences.
	 *
	 * @param XML The xml to escape
	 * @return the escaped xml
	 */
	public static String DoReplaceSpecialCharachters(String XML)
	{
		if (XML == null)
		{
			return null;
		}
		String tmp;
		tmp = XML.replaceAll("&", "&amp;");
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");
		tmp = tmp.replaceAll("\"", "&quot;");
		tmp = tmp.replaceAll("'", "&apos;");
		return tmp;
	}

	/**
	 * Replaces valid xml escape sequences to their original form
	 *
	 * @param XML the escaped xml
	 * @return the original xml
	 */
	public static String UndoReplaceSpecialCharachters(String XML)
	{
		if (XML == null)
		{
			return null;
		}
		String tmp;
		tmp = XML.replaceAll("&lt;", "<");
		tmp = tmp.replaceAll("&gt;", ">");
		tmp = tmp.replaceAll("&quot;", "\"");
		tmp = tmp.replaceAll("&apos;", "'");
		tmp = tmp.replaceAll("&amp;", "&");
		return tmp;
	}
}

