package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters;

import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class ContentTypeConverter implements IObjectConverter {
	public static final String ELEMENT_resource = "Resource";
	public static final String ELEMENT_contentType = "ContentType";

	@Override
	public Object Convert(String serialization) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(serialization)));

		Element resource = (Element) doc.getElementsByTagName(ELEMENT_resource).item(0);

		Element ct = (Element) resource.getElementsByTagName(ELEMENT_contentType).item(0);

		ContentType contentType = new ContentType();
		contentType.fromDOM(ct);

		return contentType;
	}

	@Override
	public String Convert(Object o) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element resource = doc.createElement(ELEMENT_resource);
		((ContentType) o).toDOM(resource);

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
//		ContentTypeConverter conv = new ContentTypeConverter();
//		
//		ContentType ct = new ContentType("image/jpeg", null);
//		ct.addContentTypeParameters(new Parameter("name", "value"));
//
//		System.out.println(conv.Convert(ct));
//		
//		ContentType ct2 = (ContentType) conv.Convert(conv.Convert(ct));
//		
//		System.out.println(conv.Convert(ct2));
//
//	}
}
