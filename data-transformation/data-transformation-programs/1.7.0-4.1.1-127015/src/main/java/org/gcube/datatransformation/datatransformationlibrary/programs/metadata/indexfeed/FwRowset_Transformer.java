package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.indexfeed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util.XMLStringParser;
import org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util.XSLTRetriever;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Creates forward rowsets.
 * </p>
 */
public class FwRowset_Transformer implements Program{

	private static Logger log = LoggerFactory.getLogger(FwRowset_Transformer.class);

	private XPath xpath = XPathFactory.newInstance().newXPath();
	private Transformer serializer = null;
	private DOMSource serializerSource = new DOMSource();

	private static final String DEFAULTLANGUAGE = "";
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Program#transform(java.util.List, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink)
	 * @param sources The <tt>DataSources</tt> from which the <tt>Program</tt> will get the <tt>DataElements</tt>.
	 * @param programParameters The parameters of the <tt>Program</tt> which are primarily set by the <tt>TransformationUnit</tt>.
	 * @param targetContentType The <tt>ContentType</tt> in which the source data will be transformed.
	 * @param sink The <tt>DataSink</tt> in which the <tt>Program</tt> will append the transformed <tt>DataElements</tt>.
	 * @throws Exception If the program is not capable to transform <tt>DataElements</tt>.
	 */
	public void transform(List<DataSource> sources, List<Parameter> programParameters, ContentType targetContentType, DataSink sink) throws Exception {

		if(programParameters==null || programParameters.size()==0){
			log.error("Program parameters do not contain xslt");
			throw new Exception("Program parameters do not contain xslt");
		}

		//Finding xslt and indexType from the program parameters...
		List<String> xsltIDs = new ArrayList<String>();
		String finalxsltID=null;
		
		for(Parameter param: programParameters){
			log.debug("Got parameter: " + param.getName() + " with value: " + param.getValue());
			if(param.getName().equalsIgnoreCase("finalfwdxslt")){
				finalxsltID = param.getValue();
			}
			if(param.getName().matches("xslt(:[0-9][0-9]*)?")){
				if(param.getValue().endsWith("-"))
					log.debug("skipping parameter " + param.getName());
				else
					xsltIDs.add(param.getValue());
			}
		}
		
		if(xsltIDs.isEmpty() || (xsltIDs.size() > 1 && finalxsltID == null)){
			log.error("Program parameters xslts are not set properly");
			throw new Exception("Program parameters xslts are not set properly");
		}
		
		Map<String, Templates> compiledXSLT = new HashMap<String, Templates>();
		Map<String, Map<String, XPathExpression>> keyDescs = new HashMap<String, Map<String,XPathExpression>>();
		for (String xsltID : xsltIDs) {
			String xslt = retrieveXSLT(xsltID);
			
			compiledXSLT.put(xsltID, createTemplates(xslt));
			keyDescs.put(xsltID, createKeyDescs(xslt));
		}
		
		Templates compiledFinalXSLT = null;
		if (finalxsltID != null)
			compiledFinalXSLT = createTemplates(retrieveXSLT(finalxsltID));
		else
			log.debug("finalfwdxslt not set");

		/* Create a transformer that will be used for serializing the transformed elements */

		TransformerFactory factory = null;
		try {
			factory = TransformerFactory.newInstance();
			serializer = factory.newTransformer();
			serializer.setOutputProperty("omit-xml-declaration", "yes");
		} catch (Exception e) {
			log.error("Failed to create serializer.", e);
			throw new Exception("Failed to create serializer.", e);
		}

		transformByXSLT(sources, compiledXSLT, compiledFinalXSLT, targetContentType, sink, keyDescs);
	}

	private void transformByXSLT(List<DataSource> sources, Map<String, Templates> compiledXSLT, Templates compiledFinalXSLT, ContentType targetContentType, DataSink sink, Map<String, Map<String, XPathExpression>> keyDescs) throws Exception{
		if(sources.size()!=1){
			throw new Exception("Elm2ElmProgram is only applicable for programs with one Input");
		}
		DataSource source = sources.get(0);
		while(source.hasNext() && !sink.isClosed()){
			log.debug("Source has next...");
			DataElement object = source.next();
			if(object!=null){
				DataElement transformedObject;
				try {
					log.debug("Got next object with id "+object.getId());
					transformedObject = transformDataElementByXSLTs(object, compiledFinalXSLT, compiledXSLT, targetContentType, keyDescs);
					if(transformedObject==null){
						log.warn("Got null transformed object...");
						throw new Exception();
					}
					transformedObject.setId(object.getId());
					log.debug("Got transformed object with id: "+transformedObject.getId()+" and content format: "+transformedObject.getContentType().toString()+", appending into the sink");
					ReportManager.manageRecord(object.getId(), "Data element with id "+object.getId()+" and content format "+object.getContentType().toString()+" " +
							"was transformed successfully to "+transformedObject.getContentType().toString(), Status.SUCCESSFUL, Type.TRANSFORMATION);
				} catch (Exception e) {
					log.error("Could not transform Data Element, continuing to next...",e);
					ReportManager.manageRecord(object.getId(), "Data element with id "+object.getId()+" and content format "+object.getContentType().toString()+" " +
							"could not be transformed to "+targetContentType.toString(), Status.FAILED, Type.TRANSFORMATION);
					continue;
				}
				sink.append(transformedObject);
				log.debug("Transformed object with id: "+transformedObject.getId()+", was appended successfully");
			}else{
				log.warn("Got null object from the data source");
			}

		}
		if (!source.hasNext())
			log.debug("Source does not have any objects left, closing the sink...");
		else
			log.debug("Sink was closed unexpectedly...");
		sink.close();
	}

	private DataElement transformDataElementByXSLTs(DataElement sourceDataElement, Templates compiledFinalXSLT, Map<String, Templates> compiledXSLTs,
			ContentType targetContentType, Map<String, Map<String, XPathExpression>> keyDescs) throws Exception {

		StrDataElement transformedElement = StrDataElement.getSinkDataElement(sourceDataElement);
		transformedElement.setContentType(targetContentType);
		transformedElement.setId(sourceDataElement.getId());

		String payload = null;
		if(sourceDataElement instanceof StrDataElement){
			payload = ((StrDataElement)sourceDataElement).getStringContent();
		}else{
			payload = isToString((sourceDataElement.getContent()));
		}
		
		StringBuilder strBuilder = new StringBuilder();
		Document transformedDoc = null;
		String transformedPayload = null;

		for (Entry<String, Templates> compiledXSLT : compiledXSLTs.entrySet()){
			StringWriter output = new StringWriter();
			/* Transform the current source element using the given XSLT. The output of the XSLT
			 * is a forward index rowset whose 'key' is empty, because the actual key is filled in
			 * by this program later.
			 */
			try {
				Transformer t = compiledXSLT.getValue().newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.transform(new StreamSource(new StringReader(payload)), new StreamResult(output));
			} catch (Exception e) {
				log.error("Failed to transform element with ID = " + sourceDataElement.getId(), e);
				throw new Exception("Failed to transform element with ID = " + sourceDataElement.getId());
			}
			
			transformedPayload = output.toString();
			transformedDoc = XMLStringParser.parseXMLString(transformedPayload);

			/* One source element may produce more than one output elements. For this reason, use
			 * the XPath expression defined in each key description to retrieve all the values that
			 * the current source element contains for each key. Then fill in the 'keys' element of
			 * the rowset produced above using each different key name and value, and add the resulting
			 * 'complete' rowset to the output data sink.
			 */
			Element elTuple = (Element) xpath.evaluate("/ROWSET/INSERT/TUPLE", transformedDoc, XPathConstants.NODE);
			Element elValue = (Element) elTuple.getElementsByTagName("VALUE").item(0);
			for (Entry<String, XPathExpression> keyDesc : keyDescs.get(compiledXSLT.getKey()).entrySet()) {
				String keyName = keyDesc.getKey();
				XPathExpression keyXPath = keyDesc.getValue();
				NodeList keyValueList = null;
				keyValueList = (NodeList) keyXPath.evaluate(new InputSource(new StringReader(payload)),	XPathConstants.NODESET);
				if (keyValueList!=null && keyValueList.getLength()>0) {
					for (int z=0; z<keyValueList.getLength(); z++) {

						String keyValue = keyValueList.item(z).getTextContent();
						log.debug("keyValue "+z+": "+ keyValue);
						if (keyValue.trim().length() > 0) {
							/* Create a KEY element, with a KEYNAME and a KEYVALUE child elements. Set
							 * the key name as the text content of the KEYNAME element, and the current
							 * value in the KEYVALUE element. */
							Element elKeyName = transformedDoc.createElement("KEYNAME");
							elKeyName.setTextContent(keyName);
							Element elKeyValue = transformedDoc.createElement("KEYVALUE");
							elKeyValue.setTextContent(keyValue);
							Element elKey = transformedDoc.createElement("KEY");
							elKey.appendChild(elKeyName);
							elKey.appendChild(elKeyValue);
							elTuple.insertBefore(elKey, elValue);
							
							log.debug("Element to add: "+elKey.getTextContent());
						}
					}
				}
			}
			transformedPayload = XMLStringParser.XMLDocToString(transformedDoc);
			
			strBuilder.append(transformedPayload);
		}

		if(compiledFinalXSLT != null) {
			transformedPayload = "<__Agregate_>\n" + strBuilder.toString() + "\n</__Agregate_>";;
			
			StringWriter finalOutput = new StringWriter();
			try {
				Transformer t = compiledFinalXSLT.newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				t.transform(new StreamSource(new StringReader(transformedPayload)), new StreamResult(finalOutput));
			} catch (Exception e) {
				log.error("Failed to transform element with ID = " + sourceDataElement.getId());
				throw new Exception("Failed to transform element with ID = " + sourceDataElement.getId());
			}
			transformedPayload = finalOutput.toString();
		}
		
		transformedDoc = XMLStringParser.parseXMLString(transformedPayload);
		serializerSource.setNode(transformedDoc);

		/* One source element may produce more than one output elements. For this reason, use
		 * the XPath expression defined in each key description to retrieve all the values that
		 * the current source element contains for each key. Then fill in the 'keys' element of
		 * the rowset produced above using each different key name and value, and add the resulting
		 * 'complete' rowset to the output data sink.
		 */
		Element elTuple = (Element) xpath.evaluate("/ROWSET/INSERT/TUPLE", transformedDoc, XPathConstants.NODE);
		Element elValue = (Element) elTuple.getElementsByTagName("VALUE").item(0);
		{
			// Add Key for "ObjectID"
			Element elKeyName = transformedDoc.createElement("KEYNAME");
			elKeyName.setTextContent("ObjectID");
			Element elKeyValue = transformedDoc.createElement("KEYVALUE");
			elKeyValue.setTextContent(sourceDataElement.getAttributeValue("ContentOID"));
			Element elKey = transformedDoc.createElement("KEY");
			elKey.appendChild(elKeyName);
			elKey.appendChild(elKeyValue);
			elTuple.insertBefore(elKey, elValue);
		}
		{
			// Add Key for "gDocCollectionID"
			Element elKeyName = transformedDoc.createElement("KEYNAME");
			elKeyName.setTextContent("gDocCollectionID");
			Element elKeyValue = transformedDoc.createElement("KEYVALUE");
			elKeyValue.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
			Element elKey = transformedDoc.createElement("KEY");
			elKey.appendChild(elKeyName);
			elKey.appendChild(elKeyValue);
			elTuple.insertBefore(elKey, elValue);
		}		
		{
			// Add Key for "gDocCollectionLang"
			Element elKeyName = transformedDoc.createElement("KEYNAME");
			elKeyName.setTextContent("gDocCollectionLang");
			Element elKeyValue = transformedDoc.createElement("KEYVALUE");
			elKeyValue.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_LANGUAGE) == null? DEFAULTLANGUAGE : sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_LANGUAGE));
			Element elKey = transformedDoc.createElement("KEY");
			elKey.appendChild(elKeyName);
			elKey.appendChild(elKeyValue);
			elTuple.insertBefore(elKey, elValue);
		}		

		{
			// Add !!!!_Field_!!!! for "ObjectID"
			Element elfield = transformedDoc.createElement("FIELD");
			elfield.setTextContent(sourceDataElement.getAttributeValue("ContentOID"));
			elfield.setAttribute("name","ObjectID");
			elValue.appendChild(elfield);
		}
		{
			// Add !!!!_Field_!!!! for "gDocCollectionID"
			Element elfield = transformedDoc.createElement("FIELD");
			elfield.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
			elfield.setAttribute("name","gDocCollectionID");
			elValue.appendChild(elfield);
		}
		{
			// Add !!!!_Field_!!!! for "gDocCollectionLang"
			Element elfield = transformedDoc.createElement("FIELD");
			elfield.setTextContent(sourceDataElement.getAttributeValue("language") == null? DEFAULTLANGUAGE : sourceDataElement.getAttributeValue("language"));
			elfield.setAttribute("name","gDocCollectionLang");
			elValue.appendChild(elfield);
		}
		
		/* Transform the document to String format */
		StringWriter sw = new StringWriter();
		StreamResult sresult = new StreamResult(sw);
		serializer.transform(serializerSource, sresult);
		String result = sw.getBuffer().toString();

		/* Add the serialized document to the data sink */
		//		destElement = sink.getNewDataElement(sourceElement, result);
		//		if (destElement instanceof ResultSetDataElement) {
		//			((ResultSetDataElement) destElement).setDocumentID(sourceElement.getContentObjectID());
		//			((ResultSetDataElement) destElement).setCollectionID(sourceElement.getMetadataCollectionID());
		//		}
		//		sink.writeNext(destElement);

		transformedElement.setContent(result);
		return transformedElement;
	}
	
	private String retrieveXSLT(String xsltID) throws Exception {
		log.debug("Got XSLT ID: "+xsltID);
		String xslt;
		try {
			xslt=XSLTRetriever.getXSLTFromIS(xsltID, DTSScope.getScope()); 
//			xslt=mockXSLTRetriever(xsltID);
		} catch (Exception e) {
			log.error("Did not manage to retrieve the XSLT with ID "+xsltID+", aborting transformation...");
			throw new Exception("Did not manage to retrieve the XSLT with ID "+xsltID);
		}

		return xslt;
	}

	private Templates createTemplates(String xslt) throws Exception {
		Templates compiledXSLT;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			compiledXSLT = factory.newTemplates(new StreamSource(new StringReader(xslt)));
		} catch (Exception e) {
			log.error("Failed to compile the XSLT: " + xslt, e);
			throw new Exception("Failed to compile the XSLT");
		}
	
		return compiledXSLT;
	}
	
	private Map<String, XPathExpression> createKeyDescs(String xslt) throws Exception {
		Map<String, XPathExpression> keyDescs = new HashMap<String, XPathExpression>();
		/* Retrieve the keys description from the XSLT definition */
		NodeList keys = null;
		boolean foundKeysDesc = true;
		try {
			keys = (NodeList) xpath.evaluate("//*[local-name()='variable']/self::node()[@name='keys']/key", new InputSource(new StringReader(xslt)),
					XPathConstants.NODESET);
		} catch (Exception e) {
			foundKeysDesc = false;
		}

		if (!foundKeysDesc || keys == null || keys.getLength() == 0) {
			log.error("Unable to locate the 'keys' variable in the given XSLT." + "Make sure the parameter is defined like this:\n"
					+ "<xsl:variable name=\"keys\"> <key><keyName/><keyXPath/></key> ... </xsl:param>");
			throw new Exception("Unable to locate the 'keys' variable in the given XSLT." + "Make sure the parameter is defined like this:\n"
					+ "<xsl:variable name=\"keys\"> <key><keyName/><keyXPath/></key> ... </xsl:param>");
		}

		/* Parse the key descriptions */
		try {
			for (int i = 0; i < keys.getLength(); i++) {
				Node n = keys.item(i);
				String keyName = ((Element) n).getElementsByTagName("keyName").item(0).getTextContent();
				XPathExpression keyXPath = xpath.compile(((Element) n).getElementsByTagName("keyXPath").item(0).getTextContent());
				
				keyDescs.put(keyName, keyXPath);
				log.debug("Xpath: " + ((Element) n).getElementsByTagName("keyXPath").item(0).getTextContent());
			}
		} catch (Exception e) {
			log.error("Failed to parse and compile the key descriptions.", e);
			throw new Exception("Failed to parse and compile the key descriptions.", e);
		}
		return keyDescs;
	}

	protected static String isToString(InputStream instream) throws Exception 
	{ 
		Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(
                    new InputStreamReader(instream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            instream.close();
        }
        return writer.toString();
	}
	
//	public static void main(String[] args) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		DTSSManager.setScope("/gcube/devNext");
////		{
////			ScopeProvider.instance.set("/gcube/devNext");
////			DTSSManager.setScope("/gcube/devNext");
////			String input = "74a7190f-4529-4d03-8b23-f4552240cf58";
////	
////			TMDataSource tmsource = new TMDataSource(input, null);
////			DataSink sink2 = new PathDataSink("/home/jgerbe/testArea/tmsource", null);
////	
////			while (tmsource.hasNext()) {
////				DataElement de = tmsource.next();
////				if (de != null)
////					sink2.append(de);
////	
////			}
////			Thread.sleep(1231311231);
////		}
//		
//		PathDataSource ds = new PathDataSource("/home/jgerbe/testArea/tmsource", null);
//
//		FwRowset_Transformer transformer = new FwRowset_Transformer();
//
//		List<DataSource> sources = new ArrayList<DataSource>();
//		sources.add(ds);
//
////		while (ds.hasNext()) {
////			DataElement de = ds.next();
////			if (de != null) {
////				System.out.println(de.getId());;
////				System.out.println(de.getAllAttributes());;
////				System.out.println(de.getContentType());;
////				System.out.println(isToString(de.getContent()));;
////			}
////		}
////		Thread.sleep(1231311231);
//
//		List<Parameter> programParameters = new ArrayList<Parameter>();
//		programParameters.add(new Parameter("xslt", "$BrokerXSLT_DwC_anylanguage_to_fwRowset_anylanguage"));
////		programParameters.add(new Parameter("xslt:2", "/home/jgerbe/Workspace/xml/Test/fwdc.xml"));
////		programParameters.add(new Parameter("xslt:3", "$BrokerXSLT_PROVENANCE_anylanguage_to_ftRowset_anylanguage"));
//		programParameters.add(new Parameter("finalftsxslt", "$BrokerXSLT_wrapperFWD"));
//
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/xml");
//		targetContentType.setContentTypeParameters(Arrays.asList(new Parameter[] { new Parameter("schemaURI", "http://fwrowset.xsd") }));
//
//		DataSink sink = new PathDataSink("/home/jgerbe/testArea/tmsink", null);
//		final String str = sink.getOutput();
//		new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println(str);
//			}
//		}.start();
//
//		transformer.transform(sources, programParameters, targetContentType, sink);
//	}
//
//	public static DataElement getDataElement() {
//		String treeCollectionID = "168b7426-1558-45b7-a639-105355e4b0ec";
//		String id = "ITIS:711074";
//		String payload = mockXSLTRetriever("/home/jgerbe/Workspace/Eclipse/test/plan.xml");
//		StrDataElement de = StrDataElement.getSourceDataElement();
//		de.setId(id);
//		de.setContent(payload);
//
//		de.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, treeCollectionID);
//		de.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);
//
//		de.setContentType(new ContentType("text/xml", null));
//		return de;
//	}
//
//	private static String mockXSLTRetriever(String path) {
//		StringBuilder payload = new StringBuilder();
//		try {
//			FileInputStream fstream = new FileInputStream(path);
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//			String strLine;
//			while ((strLine = br.readLine()) != null) {
//				payload.append(strLine.trim());
//			}
//			in.close();
//		} catch (Exception e) {
//			System.out.println("Could not find file");
//		}
//		return payload.toString();
//	}
	
}
