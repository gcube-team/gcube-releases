package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters;

import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class TransformationUnitConverter implements IObjectConverter {
	public static final String ELEMENT_resource="Resource";
	public static final String ELEMENT_transformationUnit="TransformationUnit";
	public static final String ELEMENT_transformationPrograms="TransformationPrograms";
	public static final String PRE_CDATA="<![CDATA[";
	public static final String POST_CDATA="]]>";
	
	@Override
	public Object Convert(String serialization) throws Exception {
		if (serialization.trim().length() > PRE_CDATA.length() + POST_CDATA.length())
			serialization = serialization.substring(PRE_CDATA.length(), serialization.length() - POST_CDATA.length())
			.replaceAll("&(?!quot;|apos;|lt;|gt;|amp;)", "&amp;");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(serialization)));

		Element resource = (Element) doc.getElementsByTagName(ELEMENT_resource).item(0);

		Element tu = (Element) resource.getElementsByTagName(ELEMENT_transformationUnit).item(0);
		
		Element tp = (Element) resource.getElementsByTagName(ELEMENT_transformationPrograms).item(0);
		
		TransformationProgram tProg = new TransformationProgram();
		tProg.fromDOM(tp);

		tu.removeChild(tp);
		
		TransformationUnit tUnit = new TransformationUnit();
		tUnit.fromDOM(tu);

		
		tUnit.setTransformationProgram(tProg);
		
		return tUnit;
	}

	@Override
	public String Convert(Object o) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element resource = doc.createElement(ELEMENT_resource);
		((TransformationUnit)o).toDOM(resource);
		
		Element tu = (Element) resource.getElementsByTagName(ELEMENT_transformationUnit).item(0);

		Element tp = doc.createElement(ELEMENT_transformationPrograms);
		((TransformationUnit)o).getTransformationProgram().toDom(tp);
		
		tu.appendChild(tp);
		
		doc.appendChild(resource);
		
		//Creating the xml...
		TransformerFactory tFactory = TransformerFactory.newInstance();
		javax.xml.transform.Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        
        String serialization = new String();
        serialization += PRE_CDATA;
        serialization += sw.getBuffer().toString(); 
        serialization += POST_CDATA;
        return serialization;
	}
}
