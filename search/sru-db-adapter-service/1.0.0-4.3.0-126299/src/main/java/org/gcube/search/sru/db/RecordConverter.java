package org.gcube.search.sru.db;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class RecordConverter {
	static final Logger logger = LoggerFactory.getLogger(RecordConverter.class);

	public String convertRecordsToSru(Long resultsCnt,
			List<Map<String, String>> rs, boolean splitLists) throws TransformerException,
			ParserConfigurationException {
		return convertRecordsToSRU(resultsCnt, rs, splitLists);
	}

	static final List<String> DC_FIELDS = Lists.newArrayList("title", "creator",
			"subject", "description", "publisher", "contributor", "date",
			"type", "format", "identifier", "source", "language", "relation",
			"coverage", "rights");

	static String convertRecordsToSRU(Long resultsCnt,
			List<Map<String, String>> rs, boolean splitLists) throws TransformerException,
			ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		doc.setXmlStandalone(true);
		Element searchRetrieveResponse = doc
				.createElement("searchRetrieveResponse");
		searchRetrieveResponse.setAttribute("xmlns:zs",
				"http://www.loc.gov/zing/srw/");
		doc.appendChild(searchRetrieveResponse);

		Element el = doc.createElement("zs:version");
		el.appendChild(doc.createTextNode("1.1"));
		searchRetrieveResponse.appendChild(el);

		Element records = doc.createElement("zs:records");
		searchRetrieveResponse.appendChild(records);

		el = doc.createElement("zs:numberOfRecords");
		el.appendChild(doc.createTextNode(String.valueOf(resultsCnt)));
		searchRetrieveResponse.appendChild(el);

		int recPosition = 1;

		for (Map<String, String> rec : rs) {
			Element record = doc.createElement("zs:record");
			records.appendChild(record);

			el = doc.createElement("zs:recordSchema");
			el.appendChild(doc.createTextNode("info:srw/schema/1/dc-v1.1"));
			record.appendChild(el);

			el = doc.createElement("zs:recordPacking");
			el.appendChild(doc.createTextNode("xml"));
			record.appendChild(el);

			Element recordData = doc.createElement("zs:recordData");
			// el.setAttribute("xmlns:srw_rss", "rss");
			// el.appendChild(doc.createTextNode("xml"));
			record.appendChild(recordData);

			Element dc = doc.createElement("srw_dc:dc");
			dc.setAttribute("xmlns:srw_dc", "info:srw/schema/1/dc-schema");
			dc.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			dc.setAttribute("xmlns", "http://purl.org/dc/elements/1.1/");
			dc.setAttribute(
					"xsi:schemaLocation",
					"info:srw/schema/1/dc-schema http://www.loc.gov/standards/sru/resources/dc-schema.xsd");

			// el.appendChild(doc.createTextNode("xml"));
			recordData.appendChild(dc);

			// channel.appendChild(row);

			for (Entry<String, String> col : rec.entrySet()) {
				String columnName = col.getKey();
				String value = col.getValue();

				columnName = StringEscapeUtils.escapeXml(columnName);
				columnName = columnName.toLowerCase();

				if (!DC_FIELDS.contains(columnName)) {
					logger.info("field : " + columnName + " not in dc");
					continue;
				}

				if (value == null || value.trim().length() == 0) {
					logger.info("field : " + columnName + " has no value");
					continue;
				}
				
				
				if (value.startsWith("[") && value.endsWith("]") && splitLists){
					List<String> values = Splitter.on(", ").trimResults().splitToList(value.substring(1, value.length()-1));
					
					for (String singleVal : values){
						Element node = doc.createElement(columnName);
						
						logger.debug("nodeToString : "
								+ XMLConverter.nodeToString(node));
						
						String xml = StringEscapeUtils.escapeXml(singleVal);
						node.appendChild(doc.createTextNode(xml));
						
						dc.appendChild(node);
					}
					
				} else {
					
					Element node = doc.createElement(columnName);
					
					logger.debug("nodeToString : "
							+ XMLConverter.nodeToString(node));
					
					String xml = StringEscapeUtils.escapeXml(value);
					node.appendChild(doc.createTextNode(xml));
					
					dc.appendChild(node);
				}
				

			}

			Element recordPosition = doc.createElement("zs:recordPosition");
			recordPosition.appendChild(doc.createTextNode(String
					.valueOf(recPosition)));
			// el.appendChild(doc.createTextNode("xml"));
			record.appendChild(recordPosition);
			recPosition++;
		}

		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);

		return sw.toString();
	}

	static String convertRecordsToRSS(Long resultsCnt,
			List<Map<String, String>> rs) throws TransformerException,
			ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		doc.setXmlStandalone(true);
		Element searchRetrieveResponse = doc
				.createElement("searchRetrieveResponse");
		searchRetrieveResponse.setAttribute("xmlns:zs",
				"http://www.loc.gov/zing/srw/");
		doc.appendChild(searchRetrieveResponse);

		Element el = doc.createElement("zs:version");
		el.appendChild(doc.createTextNode("1.1"));
		searchRetrieveResponse.appendChild(el);

		Element records = doc.createElement("zs:records");
		searchRetrieveResponse.appendChild(records);

		// Element el = doc.createElement(DB_TITLE_PROP);
		// el.appendChild(doc.createTextNode(DB_TITLE));
		// channel.appendChild(el);
		//
		// el = doc.createElement(DB_LINK_PROP);
		// el.appendChild(doc.createTextNode(DB_LINK));
		// channel.appendChild(el);
		//
		// el = doc.createElement(DB_DESCRIPTION_PROP);
		// el.appendChild(doc.createTextNode(DB_DESCRIPTION));
		// channel.appendChild(el);
		//
		// el = doc.createElement(DB_PUBDATE_PROP);
		// el.appendChild(doc.createTextNode(DB_PUBDATE));
		// channel.appendChild(el);
		//
		// el = doc.createElement(DB_LASTBUILDDATE_PROP);
		// el.appendChild(doc.createTextNode(DB_LASTBUILDDATE));
		// channel.appendChild(el);
		//
		// el = doc.createElement(DB_TTL_PROP);
		// el.appendChild(doc.createTextNode(DB_TTL));
		// channel.appendChild(el);

		// Element

		el = doc.createElement("zs:numberOfRecords");
		el.appendChild(doc.createTextNode(String.valueOf(resultsCnt)));
		searchRetrieveResponse.appendChild(el);

		int recPosition = 1;

		for (Map<String, String> rec : rs) {
			Element record = doc.createElement("zs:record");
			records.appendChild(record);

			el = doc.createElement("zs:recordSchema");
			el.appendChild(doc.createTextNode("rss"));
			record.appendChild(el);

			el = doc.createElement("zs:recordPacking");
			el.appendChild(doc.createTextNode("xml"));
			record.appendChild(el);

			Element recordData = doc.createElement("zs:recordData");
			// el.setAttribute("xmlns:srw_rss", "rss");
			// el.appendChild(doc.createTextNode("xml"));
			record.appendChild(recordData);

			Element srw = doc.createElement("srw_rss:rss");
			srw.setAttribute("xmlns:srw_rss", "rss");
			srw.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			srw.setAttribute("xmlns", "rss");

			// el.appendChild(doc.createTextNode("xml"));
			recordData.appendChild(srw);

			// channel.appendChild(row);

			for (Entry<String, String> col : rec.entrySet()) {
				String columnName = col.getKey();
				String value = col.getValue();

				String xmlColumn = StringEscapeUtils.escapeXml(columnName);

				// xmlColumn = "ColName_" + xmlColumn + "_ColName";

				Element node = doc.createElement(xmlColumn);

				logger.debug("nodeToString : "
						+ XMLConverter.nodeToString(node));

				if (value == null)
					node.appendChild(doc.createTextNode(""));
				else {
					String xml = StringEscapeUtils.escapeXml(value.toString());
					node.appendChild(doc.createTextNode(xml));
				}
				srw.appendChild(node);
			}

			Element recordPosition = doc.createElement("zs:recordPosition");
			recordPosition.appendChild(doc.createTextNode(String
					.valueOf(recPosition)));
			// el.appendChild(doc.createTextNode("xml"));
			record.appendChild(recordPosition);
			recPosition++;
		}

		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);

		return sw.toString();
	}
}
