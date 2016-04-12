package gr.uoa.di.madgik.workflow.adaptor.hive.utils.converters;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class MapConverter implements IObjectConverter {
	public static final String MAP = "map";
	public static final String ENTRY = "entry";
	public static final String ENTRYNAME = "key";
	public static final String ENTRYVALUE = "value";

	@Override
	public Object Convert(String serialization) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(serialization)));

		Element pars = (Element) doc.getElementsByTagName(MAP).item(0);

		Map<String, String> map = new HashMap<String, String>();

		Element e;
		int cnt = 0;
		while ((e = (Element) pars.getElementsByTagName(ENTRY).item(cnt++)) != null) {

			String name = XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(ENTRYNAME).item(0).getTextContent());
			String value = null;
			if (name.startsWith("CDATA:")) {
				value = ((CharacterData) e.getElementsByTagName(ENTRYVALUE).item(0).getFirstChild()).getData();
			} else
				value = XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(ENTRYVALUE).item(0).getTextContent());

			map.put(name, value);
		}

		return map;
	}

	@Override
	public String Convert(Object o) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) o;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element resource = doc.createElement(MAP);

		for (Entry<String, String> entry : map.entrySet()) {
			Element par = doc.createElement(ENTRY);
			resource.appendChild(par);

			Element parName = doc.createElement(ENTRYNAME);
			String key = entry.getKey();
			parName.setTextContent(key);
			par.appendChild(parName);

			Element parValue = doc.createElement(ENTRYVALUE);
			String value = entry.getValue();
			if (entry.getKey().startsWith("CDATA:")) {
				parValue.appendChild(doc.createCDATASection(value));
			} else {
				parValue.setTextContent(value);
			}
			par.appendChild(parValue);
		}

		doc.appendChild(resource);

		// Creating the xml...
		TransformerFactory tFactory = TransformerFactory.newInstance();
		javax.xml.transform.Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		return sw.getBuffer().toString();
	}

//	public static void main(String[] args) throws Exception {
//		Map<String, String> map = new HashMap<String, String>();
//
//		map.put("key1", "value1");
////		map.put("key2", "value2");
////		map.put("key3", "value3");
////		map.put("key4", "value4");
//		map.put("CData:key5", "value5");
//
//		System.out.println(new MapConverter().Convert(map).toString());
//		
//		System.out.println(new MapConverter().Convert(new MapConverter().Convert(map).toString()).equals(map));
//	}
}
