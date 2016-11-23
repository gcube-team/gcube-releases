package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.indexfeed;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program creating full text rowsets from plain text files.
 * </p>
 */
public class TextToFtsRowset_Transformer extends Elm2ElmProgram{
	
	private static Logger log = LoggerFactory.getLogger(TextToFtsRowset_Transformer.class);
	
	private static String indexType = "ft_content_1.0";
	
	/**
	 * A simple test.
	 * 
	 * @param args The arguments
	 * @throws Exception If the test could not be performed.
	 */
	public static void main(String[] args) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream("<lala>dsfsdfsa ads adf df ads f d \n lala</lala>".getBytes());
		DataElementImpl sourceDataElement = DataElementImpl.getSourceDataElement();
		sourceDataElement.setContent(input);
		sourceDataElement.setAttribute("ContentOID", "5490-5342-5342-8765");
		System.out.println(stringFromInputStream(new TextToFtsRowset_Transformer().transformDataElement(sourceDataElement, null, null).getContent()));
	}
	
	private static String stringFromInputStream(InputStream input) throws Exception {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = input.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    input.close();
	    return out.toString();
	}
	
	static ContentType contentType = new ContentType();
	static{
		contentType.setMimeType("text/xml");
		Parameter schema = new Parameter("schema", "ftrs");
		Parameter schemaURI = new Parameter("schemaURI", "http://ftrowset.xsd");
		contentType.addContentTypeParameters(schema, schemaURI);
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Elm2ElmProgram#transformDataElement(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceDataElement The source <tt>DataElement</tt>.
	 * @param programParameters The parameters of the {@link Program}.
	 * @param targetContentType The <tt>ContentType</tt> in which the <tt>DataElement</tt> will be transformed.
	 * @return The transformed <tt>DataElement</tt>.
	 * @throws Exception If the <tt>Program</tt> is not capable to transform <tt>DataElements</tt>.
	 */
	@Override
	public DataElement transformDataElement(DataElement sourceDataElement, List<Parameter> programParameters, ContentType targetContentType) throws Exception {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			Document doc = docBuilder.newDocument();
			Element rowSetElement = doc.createElement("ROWSET");
			Element rowElement = doc.createElement("ROW");
			Element fieldElement = doc.createElement("FIELD");
			Element fieldOIDElement = doc.createElement("FIELD");
			
			rowSetElement.setAttribute("idxType", indexType);
			
			String lang = "en";
			if(sourceDataElement.getAttributeValue("language")!=null){
				lang = sourceDataElement.getAttributeValue("language");
			}
			
			fieldElement.setAttribute("lang", lang);
			
			fieldElement.setAttribute("name", "content");
			
			if(sourceDataElement instanceof StrDataElement){
				fieldElement.setTextContent(((StrDataElement)sourceDataElement).getStringContent());
			}else{
				fieldElement.setTextContent(stringFromInputStream(sourceDataElement.getContent()));
			}
			
			fieldOIDElement.setAttribute("name", "ObjectID");
			
			fieldOIDElement.setTextContent(sourceDataElement.getAttributeValue("ContentOID"));
			
			rowElement.appendChild(fieldElement);
			rowElement.appendChild(fieldOIDElement);
			rowSetElement.appendChild(rowElement);
			doc.appendChild(rowSetElement);
			
			
			StrDataElement dataElement = StrDataElement.getSinkDataElement(sourceDataElement);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

			dataElement.setContent(output);
			
			dataElement.setContentType(contentType);
			
			return dataElement;
		} catch (Exception e) {
			log.error("COuld not transform text to xml...", e);
			throw new Exception("COuld not transform text to xml...", e);
		}
	}

}
