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

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Converter for class {@link Input} used by {@link DataTypeConvertable}
 * objects. Actually, it is a serializer/deserializer.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class InputConverter implements IObjectConverter {
	static String INPUTSOURCE = "input";
	static String INPUTTYPE = "inputType";
	static String INPUTVALUE = "inputValue";
	static String INPUTPARAMETERS = "inputParameters";
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
			Input input = new Input();

			Element xmlDocEl = (Element) xmlDoc.getElementsByTagName(INPUTSOURCE).item(0);

			input.setInputType(XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getElementsByTagName(INPUTTYPE).item(0).getTextContent()));
			CDATASection cdata = (CDATASection) xmlDocEl.getElementsByTagName(INPUTVALUE).item(0).getFirstChild();
			input.setInputValue(cdata != null? cdata.getTextContent() : "");

			Element e;
			int cnt = 0;
			Element pars = (Element) xmlDocEl.getElementsByTagName(INPUTPARAMETERS).item(0);
			List<Parameter> list = new ArrayList<Parameter>();
			while ((e = (Element) pars.getElementsByTagName(PARAMETER).item(cnt++)) != null) {
				Parameter par = new Parameter();
				par.setName(XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(PARAMETERNAME).item(0).getTextContent()));
				par.setValue(XMLUtils.UndoReplaceSpecialCharachters(e.getElementsByTagName(PARAMETERVALUE).item(0).getTextContent()));
				list.add(par);
			}

			if (!list.isEmpty())
				input.setInputParameters(list.toArray(new Parameter[list.size()]));

			return input;
		} catch (Exception ex) {
			throw new Exception(serialization, ex);
		}
	}

	@Override
	public String Convert(Object o) throws Exception {
		try {
			Input input = (Input) o;

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element resource = doc.createElement(INPUTSOURCE);
			doc.appendChild(resource);

			Element type = doc.createElement(INPUTTYPE);
			type.setTextContent(input.getInputType());
			resource.appendChild(type);

			Element value = doc.createElement(INPUTVALUE);
			if (input.getInputValue() != null) {
				CDATASection cdata = doc.createCDATASection(input.getInputValue());
				value.appendChild(cdata);
			}
			resource.appendChild(value);

			Element parameters = doc.createElement(INPUTPARAMETERS);
			resource.appendChild(parameters);

			if (input.getInputParameters() != null)
				for (Parameter parameter : input.getInputParameters()) {
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
			throw new Exception("Could not serialize Input");
		}
	}

//	public static void main(String args[]) {
//		Input input = new Input("HTTPDataSource",
//				"http://dionysus.di.uoa.gr:8081/TreeHarvester/HarvestTrees?treeCollectionID=c9076f3f-be8d-43e2-9f02-de35e6d8f72c&scope=/gcube/devNext", new Parameter[] {
//						new Parameter("username", "giannis"), new Parameter("password", "ftpsketo"), new Parameter("directory", "src") });
//
//		InputConverter conv = new InputConverter();
//		try {
//			String str = conv.Convert(input);
//			System.out.println(str);
//			Input cpy = (Input) conv.Convert(str);
//			System.out.println(conv.Convert(cpy));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}