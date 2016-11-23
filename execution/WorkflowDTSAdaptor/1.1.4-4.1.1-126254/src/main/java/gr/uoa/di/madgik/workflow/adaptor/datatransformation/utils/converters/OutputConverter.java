package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.converters;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Converter for class {@link Output} used by {@link DataTypeConvertable}
 * objects. Actually, it is a serializer/deserializer.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class OutputConverter implements IObjectConverter {
	static String OUTPUTSOURCE = "output";
	static String OUTPUTTYPE = "outputType";
	static String OUTPUTVALUE = "outputValue";
	static String OUTPUTPARAMETERS = "outputParameters";
	static String PARAMETER = "parameter";
	static String PARAMETERNAME = "parameterName";
	static String PARAMETERVALUE = "parameterValue";
	
	@Override
	public Object Convert(String serialization) throws Exception {
		if (serialization == null || serialization.trim().length() == 0)
			throw new Exception("Cannot convert null or empty value (" + serialization + ")");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document xmlDoc = db.parse(new InputSource(new StringReader(serialization)));

		try {
			Output output = new Output();

			Element xmlDocEl = (Element) xmlDoc.getElementsByTagName(OUTPUTSOURCE).item(0);

			output.setOutputType(XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getElementsByTagName(OUTPUTTYPE).item(0).getTextContent()));
			output.setOutputValue(XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getElementsByTagName(OUTPUTVALUE).item(0).getTextContent()));

			Element e;
			int cnt = 0;
			Element pars = (Element) xmlDocEl.getElementsByTagName(OUTPUTPARAMETERS).item(0);
			List<Parameter> list = new ArrayList<Parameter>();
			while ((e = (Element) pars.getElementsByTagName(PARAMETER).item(cnt++)) != null) {
				Parameter par = new Parameter();
				par.setName(XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(PARAMETERNAME).item(0).getTextContent()));
				par.setValue(XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(PARAMETERVALUE).item(0).getTextContent()));
				list.add(par);
			}

			if (!list.isEmpty())
				output.setOutputparameters(list.toArray(new Parameter[list.size()]));

			return output;
		} catch (Exception ex) {
			throw new Exception(serialization, ex);
		}
	}

	@Override
	public String Convert(Object o) throws Exception {
		try {
			Output output = (Output) o;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element resource = doc.createElement(OUTPUTSOURCE);
			doc.appendChild(resource);

			Element type = doc.createElement(OUTPUTTYPE);
			type.setTextContent(output.getOutputType());
			resource.appendChild(type);

			Element value = doc.createElement(OUTPUTVALUE);
			value.setTextContent(output.getOutputValue());
			resource.appendChild(value);

			Element parameters = doc.createElement(OUTPUTPARAMETERS);
			resource.appendChild(parameters);
			
			if(output.getOutputparameters() != null)
				for(Parameter parameter : output.getOutputparameters()){
					Element par = doc.createElement(PARAMETER);
					parameters.appendChild(par);
					
					Element parName = doc.createElement(PARAMETERNAME);
					parName.setTextContent(parameter.getName());
					par.appendChild(parName);
	
					Element parValue = doc.createElement(PARAMETERVALUE);
					parValue.setTextContent(parameter.getValue());
					par.appendChild(parValue);
				}

			// Creating the xml...
			TransformerFactory tFactory = TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty("omit-xml-declaration", "yes");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			return sw.getBuffer().toString();

		} catch (Exception e) {
			throw new Exception("Could not serialize Output");
		}
	}
	
//	public static void main(String args[]) {
//    	Output output = new Output("FTP", "meteora.di.uoa.gr", new Parameter[]{ new Parameter("username", "giannis"), new Parameter("password", "ftpsketo"), new Parameter("directory", "src")});
//    	
//    	OutputConverter conv = new OutputConverter();
//    	
//    	try {
//    		String str = conv.Convert(output);
//			System.out.println(str);
//			Output cpy = (Output) conv.Convert(str);
//			System.out.println(conv.Convert(cpy));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		String outputType = "Local";
//		String outputValue = "home/jgerbe/dest";
//		Output output = new Output(outputType, outputValue, null);
//
//    	OutputConverter conv = new OutputConverter();
//    	
//    	try {
//    		String str = conv.Convert(output);
//			System.out.println(str);
//			Output cpy = (Output) conv.Convert(str);
//			System.out.println(conv.Convert(cpy));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		
//    	
//	}
}