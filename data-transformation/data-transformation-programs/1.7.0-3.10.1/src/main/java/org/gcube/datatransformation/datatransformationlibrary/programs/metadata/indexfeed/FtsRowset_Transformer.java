package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.indexfeed;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
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

import com.google.common.xml.XmlEscapers;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program creating full text rowsets.
 * </p>
 */
public class FtsRowset_Transformer implements Program{

	private static Logger log = LoggerFactory.getLogger(FtsRowset_Transformer.class);
	
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
		String indexType=null;
		Boolean base64 = false;
		
		for(Parameter param: programParameters){
			if(param.getName().equalsIgnoreCase("finalftsxslt") && !param.getValue().equals("-")){
				finalxsltID = param.getValue();
			}
			if(param.getName().matches("xslt(:[0-9][0-9]*)?")){
				if(param.getValue().endsWith("-"))
					log.debug("skipping parameter " + param.getName());
				else
					xsltIDs.add(param.getValue());
			}
			if(param.getName().equalsIgnoreCase("indexType")){
				indexType=param.getValue();
			}
			if(param.getName().equalsIgnoreCase("base64")){
				base64=Boolean.valueOf(param.getValue());
			}
		}
		
		log.debug("The index type is "+indexType);
		
		if((xsltIDs.isEmpty() || (xsltIDs.size() > 1 && finalxsltID == null)) && !base64){
			log.error("Program parameters xslts are not set properly. xsltIDs: " + xsltIDs + " finalxsltID: " + finalxsltID + " toBase64: " + base64);
			throw new Exception("Program parameters xslts are not set properly. xsltIDs: " + xsltIDs + " finalxsltID: " + finalxsltID + " toBase64: " + base64);
		}
		
		List<Templates> compiledXSLT = new ArrayList<Templates>();
		for (String xsltID : xsltIDs)
			compiledXSLT.add(createXSLTTemplates(xsltID));
		
		Templates compiledFinalXSLT = null;
		if (finalxsltID != null)
			compiledFinalXSLT = createXSLTTemplates(finalxsltID);
		
		transformSources(sources, compiledXSLT, compiledFinalXSLT, targetContentType, sink, indexType, base64);
	}
	
	private Templates createXSLTTemplates(String xsltID) throws Exception {
		log.debug("Got XSLT ID: "+xsltID);
		String xslt;
		try {
			xslt=XSLTRetriever.getXSLTFromIS(xsltID, DTSScope.getScope());
//			xslt=mockXSLTRetriever(xsltID);
		} catch (Exception e) {
			log.error("Did not manage to retrieve the XSLT with ID "+xsltID+", aborting transformation...");
			throw new Exception("Did not manage to retrieve the XSLT with ID "+xsltID);
		}

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
	
	private void transformSources(List<DataSource> sources, List<Templates> compiledXSLTs, Templates compiledFinalXSLT, ContentType targetContentType, DataSink sink, String indexType, Boolean base64) throws Exception{
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
					transformedObject = transformDataElement(object, compiledFinalXSLT, compiledXSLTs, targetContentType, indexType, base64);
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
				log.debug("Trying to append transformed object with id: "+transformedObject.getId());
				sink.append(transformedObject);
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
	
	private XPath xpath = XPathFactory.newInstance().newXPath();
	
	private DataElement transformDataElement(DataElement sourceDataElement, Templates compiledFinalXSLT, List<Templates> compiledXSLTs, ContentType targetContentType, String indexType, Boolean base64) throws Exception {
		StrDataElement transformedElement = StrDataElement.getSinkDataElement(sourceDataElement);
		transformedElement.setContentType(targetContentType);
		transformedElement.setId(sourceDataElement.getId());
		
		String transformedPayload = null;
		if (base64)
			transformedPayload = transformToBase64(sourceDataElement);
		else
			transformedPayload = transformByXLST(sourceDataElement, compiledXSLTs, compiledFinalXSLT);
		
		
		if(compiledFinalXSLT != null) {
			transformedPayload = "<__Agregate_>" + transformedPayload + "</__Agregate_>";
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

		/* Parse the transformed payload and set the target OID.
		 * If the payload contains only a ROW element with no
		 * attributes, this record should be ignored.
		 */
		Document doc = XMLStringParser.parseXMLString(transformedPayload);
		Node n = (Node) xpath.evaluate("//ROW", doc, XPathConstants.NODE);
		if (n == null) {
			log.warn("Couldn't find ROW element in record " + String.valueOf(sourceDataElement.getId()) + ".");
			throw new Exception("Couldn't find ROW element in record " + String.valueOf(sourceDataElement.getId()) + ".");
		}
		
		/* Set the "indexType" attribute to the ROWSET element */
		Element elRowset = (Element) xpath.evaluate("//ROWSET", doc, XPathConstants.NODE);
		elRowset.setAttribute("idxType", indexType);
		
		String lang = "";
		if(sourceDataElement.getAttributeValue("language")!=null){
			lang = sourceDataElement.getAttributeValue("language");
		}

		elRowset.setAttribute("lang", lang);
		
		log.debug("Get attribute ContentCollectionID "+sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
		elRowset.setAttribute("colID", sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
		
		
		/* Create a FIELD element to hold the content OID */
		Element elID = doc.createElement("FIELD");
		elID.setAttribute("name", "ObjectID");					
		elID.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID));
		
		/* Append the FIELD element to the ROW element */
		Element elRow = (Element) xpath.evaluate("//ROW", doc, XPathConstants.NODE);
		elRow.appendChild(elID);
		transformedPayload = XMLStringParser.XMLDocToString(doc);
		transformedElement.setContent(transformedPayload);
		return transformedElement;
	}
	
	private String transformToBase64(DataElement sourceDataElement) throws Exception {
		StringWriter sw = new StringWriter();
		sw.append("<ROWSET>");
		sw.append("<ROW>");

		for (Entry<String, String> attr: sourceDataElement.getAllAttributes().entrySet()) {
			String name = attr.getKey();
			
			if (name.equals(DataHandlerDefinitions.ATTR_COLLECTION_ID) || name.equals(DataHandlerDefinitions.ATTR_CONTENT_OID))
			continue;
			
			//special case for acl
			if (name.matches("sid\\d+"))
				name = "sid";
			
			sw.append("<FIELD name=\"" + XmlEscapers.xmlAttributeEscaper().escape(name) + "\">");
			sw.append(XmlEscapers.xmlContentEscaper().escape(attr.getValue()));
			sw.append("</FIELD>");
		}
		
		sw.append("<FIELD name=\"file\">");
		if(sourceDataElement instanceof StrDataElement)
			sw.append(Base64.encodeBase64String(((StrDataElement)sourceDataElement).getStringContent().getBytes()));
		else
			sw.append(Base64.encodeBase64String(IOUtils.toByteArray(sourceDataElement.getContent())));
		sw.append("</FIELD>");
		
		sw.append("</ROW>");
		sw.append("</ROWSET>");
		
		return sw.toString();
	}
	
	private String transformByXLST(DataElement sourceDataElement, List<Templates> compiledXSLTs, Templates compiledFinalXSLT) throws Exception {
		StringWriter output = new StringWriter();
		for (Templates compiledXSLT : compiledXSLTs){
			try {
				Transformer t = compiledXSLT.newTransformer();
				t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				if(sourceDataElement instanceof StrDataElement){
					t.transform(new StreamSource(new StringReader(((StrDataElement)sourceDataElement).getStringContent())), new StreamResult(output));
				}else{
					t.transform(new StreamSource(sourceDataElement.getContent()), new StreamResult(output));
				}
			} catch (Exception e) {
				log.error("Failed to transform element with ID = " + sourceDataElement.getId(), e);
				throw new Exception("Failed to transform element with ID = " + sourceDataElement.getId());
			}
		}
		return output.toString();
	}

//	public static void main(String[] args) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		
//		ScopeProvider.instance.set("/gcube/devNext");
//		DTSSManager.setScope(GCUBEScope.getScope("/gcube/devNext"));
//		 String input = "41f428fd-3218-40d2-a098-8e07c6754262";
//		 
////		 TMDataSource tmsource = new TMDataSource(input, null);
////		 DataSink sink2 = new PathDataSink("/home/jgerbe/testArea/tmsource", null);
////
////		 while (tmsource.hasNext()) {
////			 DataElement de = tmsource.next();
////			 if (de != null)
////				 sink2.append(de);
////			 
////		 }
////		 Thread.sleep(1231311231);
//		 
//		 PathDataSource ds = new PathDataSource("/home/jgerbe/testArea/tmsource", null);
//		 
//		 FtsRowset_Transformer transformer = new FtsRowset_Transformer();
//		 
//		 List<DataSource> sources = new ArrayList<DataSource>();
//		 sources.add(ds);
//		 
//		 List<Parameter> programParameters = new ArrayList<Parameter>();
////		 programParameters.add(new Parameter("xslt", "bb099010-f2c8-11dd-99ef-cbe8b682b1c1"));
////		 programParameters.add(new Parameter("finalftsxslt", "821167b0-8b78-11e0-a9c6-9c00829f1447"));
//		 programParameters.add(new Parameter("xslt:1", "$BrokerXSLT_DwC-A_anylanguage_to_ftRowset_anylanguage"));
//		 programParameters.add(new Parameter("xslt:2", "$BrokerXSLT_TAXONOMY_anylanguage_to_ftRowset_anylanguage"));
//		 programParameters.add(new Parameter("xslt:3", "$BrokerXSLT_PROVENANCE_anylanguage_to_ftRowset_anylanguage"));
//		 programParameters.add(new Parameter("finalftsxslt", "$BrokerXSLT_wrapperFT"));
//		 programParameters.add(new Parameter("indexType", "ft_SPD_1.0"));
//		 
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/xml");
//		targetContentType.setContentTypeParameters(Arrays.asList(new Parameter[] {new Parameter("schemaURI", "http://ftrowset.xsd")}));
//		 
//		 DataSink sink = new PathDataSink("/home/jgerbe/testArea/tmsink", null);
//		 final String str = sink.getOutput();
//		 new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println(str);
//			}
//		}.start();
//		
//		 transformer.transform(sources, programParameters, targetContentType, sink);
//	}
//	public static DataElement getDataElement() {
//		String treeCollectionID ="168b7426-1558-45b7-a639-105355e4b0ec";
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
//	private static String mockXSLTRetriever(String path) {
//		StringBuilder payload = new StringBuilder();
//		try{
//			FileInputStream fstream = new FileInputStream(path);
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			
//			String strLine;
//			while ((strLine = br.readLine()) != null)   {
//				payload.append(strLine.trim());
//			}
//			in.close();
//		}catch (Exception e){System.out.println("Could not find file");}
//		return payload.toString();
//	}
}
