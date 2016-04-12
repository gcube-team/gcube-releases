package org.gcube.portlets.admin.software_upload_wizard.server.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlFormatter {

	public static String prettyFormat(String input, int indent,
			boolean withHeader) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			if (!withHeader)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount",
					String.valueOf(indent));
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	public static String prettyFormat(String input, boolean withHeader) {
		return prettyFormat(input, 4, withHeader);
	}
}
