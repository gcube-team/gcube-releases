package gr.uoa.di.madgik.hive.utils;

import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class XMLBeautifier {
	public static String prettyPrintXml(String sourceXml) {
		try {

			sourceXml = sourceXml.replaceAll(">\\W+<", "><");
			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");

			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			// serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount",
			// "2");
			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(sourceXml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());

			serializer.transform(xmlSource, res);

			return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
		} catch (Exception e) {
			return "";
		}
	}
}
