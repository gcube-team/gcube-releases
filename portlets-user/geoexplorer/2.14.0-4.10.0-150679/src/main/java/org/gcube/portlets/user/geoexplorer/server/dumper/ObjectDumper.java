/*    
    The content of objects can become very complex and hard to inspect during runtime.
    This package dumps all readable properties of a (complex) object recursively 
    to a html table. The output can be viewed in a web browser.

    Copyright (C) 2008 Erwin Rohde

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gcube.portlets.user.geoexplorer.server.dumper;

import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ObjectDumper {

	/**
	 * 
	 */
	protected static final String ISO_8859_1 = "ISO-8859-1";
	protected static final String UTF_8 = "UTF-8";
	
	Document document;
	Writer writer;
	private Object object;
	private Properties config = new Properties();

	// elements
	public static final String TABLE = "table";
	public static final String TR = "tr";
	public static final String TH = "th";
	public static final String TD = "td";
	public static final String SPAN = "span";
	public static final String DIV = "div";
	public static final String BR = "br";

	// css classes
	public static final String CLASS_NAME = "name";
	public static final String CLASS_INDEX = "index";
	public static final String CLASS_SPACER = "spacer";
	public static final String CLASS_NULL = "null";
	public static final String CLASS_NOTDUMPED = "notdumped";
	public static final String CLASS_STRING = "string";
	public static final String CLASS_QUOTE = "quote";
	public static final String CLASS_PRIMITIVE = "primitive";
	public static final String CLASS_ENUM = "enum";
	public static final String CLASS_WARNING = "warning";
	public static final String CLASS_ERROR = "error";

	
	public static final String MAX_RECURSION_LEVEL = "maxRecursionLevel";

	public static final String SIMPLE_CLASS_NAMES = "simpleClassNames";

	public static final String ONLY_HTML_CONTENT = "onlyHtmlContent";

	public static final String STYLESHEET_HREF = "stylesheetHref";

	public static final String MAX_ITEMS = "maxItems";
	
	public static final String DUMP_PACKAGES = "dumpPackages";


	private void loadDefaultConfig(){
		config.put(MAX_RECURSION_LEVEL, "10");
		config.put(DUMP_PACKAGES, "");
		config.put(MAX_ITEMS, "10");
		config.put(STYLESHEET_HREF, "dumpmetadata.css");
		config.put(ONLY_HTML_CONTENT, "true");
		config.put(SIMPLE_CLASS_NAMES, "true");
	}
	
	/**
	 * Constructor. Sets the writer, and loads the default configuration
	 * 
	 * @param writer
	 *            The outout is written to this writer. i.e. Use a StringWriter
	 *            to output to screen or a FileWriter to write to a file.
	 */
	public ObjectDumper(Writer writer) {
		this.writer = writer;
		loadDefaultConfig();
	}

	/**
	 * Load properties from file. The default configuration is set in the
	 * constructor.
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadConfig(String fileName) throws FileNotFoundException,IOException {
		config.load(new FileInputStream(fileName));
	}

	/**
	 * Load properties from input stream. The default configuration is set in
	 * the constructor.
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadConfig(FileInputStream is) throws FileNotFoundException,IOException {
		config.load(is);
	}

	/**
	 * Set up the DOM document. It is used to build the html document.
	 * 
	 * @return the document
	 * @throws ParserConfigurationException
	 */
	private Document setUpDocument() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder;
		documentBuilder = builderFactory.newDocumentBuilder();

		DOMImplementation implementation = documentBuilder
				.getDOMImplementation();

		document = implementation.createDocument(null, null, null);
		return document;
	}

	/**
	 * Start document as a complete html page, including html, head and body
	 * tag. Or don't use these tags at all, and append the output from
	 * writeObjectDump immediately to the document. This is used to incorporate
	 * the {@link Document} into an existing html page.
	 * 
	 * @throws ParserConfigurationException
	 */
	private void startObjectDump() throws ParserConfigurationException {

		setUpDocument();

		if (!Boolean.parseBoolean((String) config.get(ONLY_HTML_CONTENT))) {
			Element html = document.createElement("html");
			document.appendChild(html);
			Element head = document.createElement("head");
			html.appendChild(head);
			Element link = document.createElement("link");
			link.setAttribute("rel", "stylesheet");
			link.setAttribute("href", config.getProperty(STYLESHEET_HREF));
			head.appendChild(link);
			Element body = document.createElement("body");
			html.appendChild(body);
			Element content = dumpObject(object, 0);
			body.appendChild(content);
		} else {
			Element content = dumpObject(object, 0);
			document.appendChild(content);
		}
	}

	/**
	 * This method starts it all. Write resulting formatted XML document to
	 * writer. Closes writer.
	 * 
	 * @param object
	 *            The object to dump
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public Writer writeObjectDump(Object object) throws TransformerException, IOException, ParserConfigurationException {

		this.object = object;

		startObjectDump();

		// transform the Document into a String
		DOMSource domSource = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8);
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult streamResult = new StreamResult(writer);
		transformer.transform(domSource, streamResult);
		writer.close();

		return writer;
	}

	/**
	 * Returns the given object as a html element. Can be a span, a table etc.
	 * 
	 * @param object
	 *            The object to dump
	 * @param level
	 *            The level of recursion.
	 * @return The object as html element
	 */
	private Element dumpObject(Object object, int level) {

		Element element = null;

		// NULL
		if (object == null) {
			element = handleNull();
		}
		// STRING
		else if (object instanceof String) {
			element = handleString(object);
		}
		// NUMBER
		else if (object instanceof Number || object instanceof Boolean
				|| object.getClass().isPrimitive()) {
			element = handleNumber(object);
		}
		// ARRAY
		else if (object.getClass().isArray()) {
			element = handleArray(object, ++level);
		}
		// ENUM
		else if (object.getClass().isEnum()) {
			element = handleEnum(object);
		}
		// COLLECTION
		else if (object instanceof Collection) {
			element = handleCollection(object, ++level);
		}
		// MAP
		else if (object instanceof Map) {
			element = handleMap(object, ++level);
		}
		// OBJECT
		else {
			element = handleObject(object, ++level);
		}

		return element;
	}

	/**
	 * Creates a html element to represent null
	 * 
	 * @return Null as html element
	 */
	private Element handleNull() {
		Element span = createElement(SPAN, CLASS_WARNING);
		//COMMENTED BY FRANCESCO M.
//		span.setTextContent("<null>");
		span.setTextContent(" ");
		return span;
	}

	/**
	 * Creates a html element to represent an empty object, i.e. an empty array,
	 * collection or map.
	 * 
	 * @return Empty as html element
	 */
	private Element handleEmpty() {
		Element span = createElement(SPAN, CLASS_WARNING);
		//COMMENTED BY FRANCESCO M.
//		span.setTextContent("<empty>");
		span.setTextContent(" ");
		return span;
	}

	/**
	 * Creates a html element to represent a class whos package is not in the
	 * dumpPackages list.
	 * 
	 * @return Not dumped as element
	 */
	private Element handleNotDumped() {
		Element span = createElement(SPAN, CLASS_WARNING);
		span.setTextContent("<not dumped>");
		return span;
	}

	/**
	 * Creates An element to show that the maximum resursion level has been
	 * reached
	 * 
	 * @return An element
	 */
	private Element handleMaxRecursionLevelReached() {
		Element span = createElement(SPAN, CLASS_WARNING);
		span.setTextContent("<etc...>");
		return span;
	}

	/**
	 * Creates a html table with two columns. The left column shows the index,
	 * the right column the value.
	 * 
	 * @param object
	 *            An array
	 * @param level
	 *            The level of recursion
	 * @return A html table representing the array
	 */
	private Element handleArray(Object object, int level) {

		int maxRecursionLevel = Integer.parseInt((String) config
				.get(MAX_RECURSION_LEVEL));
		if (level > maxRecursionLevel) {
			return handleMaxRecursionLevelReached();
		}

		Object[] array = null;

		// Object to object array
		if (object instanceof Object[]) {
			array = (Object[]) object;
		}
		// Convert primitive array to Object array
		else if (object instanceof boolean[]) {
			array = ArrayUtils.toObject((boolean[]) object);
		} else if (object instanceof byte[]) {
			array = ArrayUtils.toObject((byte[]) object);
		} else if (object instanceof char[]) {
			array = ArrayUtils.toObject((char[]) object);
		} else if (object instanceof short[]) {
			array = ArrayUtils.toObject((short[]) object);
		} else if (object instanceof int[]) {
			array = ArrayUtils.toObject((int[]) object);
		} else if (object instanceof long[]) {
			array = ArrayUtils.toObject((long[]) object);
		} else if (object instanceof float[]) {
			array = ArrayUtils.toObject((float[]) object);
		} else if (object instanceof double[]) {
			array = ArrayUtils.toObject((double[]) object);
		}

		// Empty array
		if (array.length == 0) {
			return handleEmpty();
		}

		// Table element
		Element table = createElement(TABLE);
		
		table.setIdAttribute("one-column-emphasis", false);
		
		Element colgroup = createElement("colgroup");
		Element col = createElement("col", "oce-first");
		colgroup.appendChild(col);
		table.appendChild(colgroup);

		Element tbody = createElement("tbody");
		
	
		
		// Table row element
		Element tr = createElement(TR);
		tbody.appendChild(tr);
		// Table column element
		Element td = createElement(TH);
		td.setAttribute("colspan", "2");
		td.setTextContent("array");
		tr.appendChild(td);

		int maxItems = Integer.parseInt((String) config.get(MAX_ITEMS));
		int maxLength = (array.length > maxItems ? maxItems : array.length);
		int i = 0;
		for (i = 0; i < maxLength; i++) {
			// Row
			tr = createElement(TR);
			// Index column
			td = createElement(TD, CLASS_INDEX);
			td.setTextContent("" + i);
			tr.appendChild(td);

			// Value column
			td = createElement(TD);
			Element element = dumpObject(array[i], level);
			safeAppendChild(td, element);
			tr.appendChild(td);
			table.appendChild(tr);
		}

		// Too many items to display
		if (array.length > maxItems) {
			tr = createElement(TR);
			td = createElement(TD, CLASS_WARNING);
			td.setAttribute("colspan", "2");
			td.setTextContent("<" + i + " of " + array.length
					+ " items displayed>");
			tr.appendChild(td);
			tbody.appendChild(tr);
		}
		
		table.appendChild(tbody);

		return table;
	}

	/**
	 * Iterates over the items inside of a collection and returns a div element
	 * with all the items inside.
	 * 
	 * @param object
	 *            A collection
	 * @param level
	 *            The level of recursion
	 * @return An element representing this collection
	 */
	@SuppressWarnings("unchecked")
	private Element handleCollection(Object object, int level) {

		int maxRecursionLevel = Integer.parseInt((String) config
				.get(MAX_RECURSION_LEVEL));
		if (level > maxRecursionLevel) {
			return handleMaxRecursionLevelReached();
		}

		Collection<Object> collection = (Collection<Object>) object;
		int i = 0;
		int maxItems = Integer.parseInt((String) config.get(MAX_ITEMS));

		// Empty collection
		if (collection.size() == 0) {
			return handleEmpty();
		}

		// Div element
		Element div = createElement(DIV);

		Iterator iter = null;
		for (iter = collection.iterator(); iter.hasNext() && i < maxItems;) {
			Object nextObject = iter.next();

			Element element = dumpObject(nextObject, level);
			safeAppendChild(div, element);
			div.appendChild(createElement(BR));
			i++;
		}

		// Too many items to display
		if (iter.hasNext() && i >= maxItems) {
			Element span = createElement(SPAN, CLASS_WARNING);
			span.setTextContent("<" + i + " of " + collection.size()
					+ " items displayed>");
			div.appendChild(span);
		}

		return div;
	}

	/**
	 * Iterates over the items inside of a map and returns a div element with
	 * all the items inside as Map.Entry objects.
	 * 
	 * @param object
	 *            A map
	 * @param level
	 *            The level of recursion
	 * @return An element representing this map
	 */
	@SuppressWarnings("unchecked")
	private Element handleMap(Object object, int level) {

		int maxRecursionLevel = Integer.parseInt((String) config
				.get(MAX_RECURSION_LEVEL));
		if (level > maxRecursionLevel) {
			return handleMaxRecursionLevelReached();
		}

		Map<Object, Object> map = (Map<Object, Object>) object;
		int i = 0;
		int maxItems = Integer.parseInt((String) config.get(MAX_ITEMS));

		// Empty map
		if (map.size() == 0) {
			return handleEmpty();
		}

		// Div element
		Element div = createElement(DIV);

		Iterator iter = null;
		for (iter = map.entrySet().iterator(); iter.hasNext() && i < maxItems;) {
			Map.Entry nextObject = (Map.Entry) iter.next();

			Element element = dumpObject(nextObject, level);
			safeAppendChild(div, element);
			div.appendChild(createElement(BR));
			i++;
		}

		// Too many items to display
		if (iter.hasNext() && i >= maxItems) {
			Element span = createElement(SPAN, CLASS_WARNING);
			span.setTextContent("<" + i + " of " + map.size()
					+ " items displayed>");
			div.appendChild(span);
		}

		return div;
	}

	/**
	 * Creates a span element. The text inside this span is the given string.
	 * 
	 * @param object
	 *            The string
	 * @return A span element
	 */
	private Element handleString(Object object) {

		Element div = createElement(DIV);
		// Opening quotation mark
		Element span = createElement(SPAN, CLASS_QUOTE);
		
		//COMMENTED BY FRANCESCO M.
//		span.setTextContent("\"");
		span.setTextContent("");
		div.appendChild(span);
		// String
		span = createElement(SPAN, CLASS_STRING);
		span.setTextContent("" + object);
		div.appendChild(span);
		// Closing quotation mark
		span = createElement(SPAN, CLASS_QUOTE);
		//COMMENTED BY FRANCESCO M.
//		span.setTextContent("\"");
		span.setTextContent("");
		div.appendChild(span);

		return div;
	}

	/**
	 * Creates a span element. The text inside the span represents the given
	 * number.
	 * 
	 * @param object
	 *            The number
	 * @return A span element
	 */
	private Element handleNumber(Object object) {

		Element span = createElement(SPAN, CLASS_PRIMITIVE);
		span.setTextContent("" + object);
		return span;
	}

	private Element handleEnum(Object object) {
		Element span = createElement(SPAN, CLASS_ENUM);
		span.setTextContent(((Enum) object).toString());
		return span;
	}

	/**
	 * Creat a table element with two columns. The left column represents the
	 * name of a property/attribute. Iterates over all properties, and gets an
	 * element for each property. Stores this element in the right column of the
	 * table.
	 * 
	 * 
	 * @param object
	 *            The object
	 * @param level
	 *            The level of recursion
	 * @return An element representing this object
	 */
	private Element handleObject(Object object, int level) {

		int maxRecursionLevel = Integer.parseInt((String) config
				.get(MAX_RECURSION_LEVEL));
		if (level > maxRecursionLevel) {
			return handleMaxRecursionLevelReached();
		}

		// Package in handled packages?
		String objectPackage = object.getClass().getPackage().getName();
		boolean packageFound = false;
		for (String pack : ((String) config.get(DUMP_PACKAGES)).split(",")) {
			if (objectPackage.startsWith(pack.trim())) {
				packageFound = true;
			}
		}
		if (!packageFound) {
			return handleNotDumped();
		}

		// Get all properties of object
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(object);

		// Table element
		Element table = createElement(TABLE);
		

		table.setAttribute("id", "one-column-emphasis");
		
		
		Element colgroup = createElement("colgroup");
		Element col = createElement("col", "oce-first");
		colgroup.appendChild(col);
		table.appendChild(colgroup);
		
		Element tbody = createElement("tbody");
		
		
		// Table row element
		Element tr = createElement(TR);
		tbody.appendChild(tr);
		// Table column element
		Element td = createElement(TH);
		td.setAttribute("colspan", "2");
		td.setTextContent(getTableHeader(object));
		tr.appendChild(td);

		for (PropertyDescriptor descriptor : descriptors) {
			// Name of property
			String property = descriptor.getName();

			// Property has getter (readable), ignore "class" property
			if (PropertyUtils.isReadable(object, property)
					&& !property.equals("class")) {

				// Row
				tr = createElement(TR);
				// Index column
//				td = createElement(TD, CLASS_NAME);
				td = createElement(TD);
				td.setTextContent(property);
				tr.appendChild(td);

				// Value column
				td = createElement(TD);
				Element element = null;
				try {
					element = dumpObject(
							PropertyUtils.getProperty(object, property), level);
				} catch (IllegalAccessException e) {
					element = createElement(SPAN, CLASS_ERROR);
					element.setTextContent("No access to property " + property);
				} catch (InvocationTargetException e) {
					element = createElement(SPAN, CLASS_ERROR);
					element.setTextContent("Reading property " + property
							+ " raises an exception");
				} catch (NoSuchMethodException e) {
					element = createElement(SPAN, CLASS_ERROR);
					element.setTextContent("No such property: " + property);
				}
				safeAppendChild(td, element);
				tr.appendChild(td);
				tbody.appendChild(tr);
			}
		}
		
		table.appendChild(tbody);
		return table;
	}

	/**
	 * Append a child element to the parent. Handles null childs silently.
	 * 
	 * @param parent
	 *            The parent element
	 * @param child
	 *            The child element
	 */
	private void safeAppendChild(Element parent, Element child) {
		if (child != null) {
			parent.appendChild(child);
		}
	}

	/**
	 * Returns the content of the table header for objects, thus the (simple)
	 * class name.
	 * 
	 * @param object
	 *            The object
	 * @return The (simple) class name
	 */
	private String getTableHeader(Object object) {
		boolean simpleNames = Boolean.parseBoolean((String) config
				.get(SIMPLE_CLASS_NAMES));
		Class<? extends Object> clazz = object.getClass();
		return (simpleNames ? clazz.getSimpleName() : clazz.getName());
	}

	/**
	 * Helper method to create an element in document.
	 * 
	 * @param tagName
	 *            Tag name
	 * @return The created element
	 */
	private Element createElement(String tagName) {
		return document.createElement(tagName);
	}

	/**
	 * Helper method to create an element in document with the given class
	 * attribute. This is used to be able to style the html output.
	 * 
	 * @param tagName
	 *            Tag name
	 * @param cssClass
	 *            CSS class name
	 * @return The created element
	 */
	private Element createElement(String tagName, String cssClass) {
		Element element = document.createElement(tagName);
		element.setAttribute("class", cssClass);
		return element;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Properties getConfig() {
		return config;
	}

	public void setConfig(Properties config) {
		this.config = config;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
}