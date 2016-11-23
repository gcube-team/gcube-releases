//package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.indexfeed;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Templates;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpression;
//import javax.xml.xpath.XPathFactory;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.common.core.informationsystem.ISException;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.ViewReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.MetadataProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.views.MetadataView;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElement;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeMetadata;
//import org.gcube.contentmanagement.storagelayer.storagemanagementservice.stubs.protocol.SMSURLConnection;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
//import org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util.XMLStringParser;
//import org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util.XSLTRetriever;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//
//
///**
// * @author Dimitris Katris, NKUA
// * <p>
// * Program creating full text rowsets.
// * </p>
// */
//public class AggregateFwd_Transformer implements Program{
//
//	
//	class ViewHolder{
//		public String ID;
//		public String lang;
//		public String XSLTname;
//		public String XSLT;
//		public Templates compiledXSLT;
//		Object[][] keyDescs;
//		public ViewReader reader;
//	}
//	
//	private static Logger log = LoggerFactory.getLogger(AggregateFwd_Transformer.class);
//	private Templates finalxslttemplate = null;
//
//	private Map<String, List<ViewHolder>> viewsperlang = new HashMap<String, List<ViewHolder>>(); 
//	private List<ViewHolder> allviews = new ArrayList<ViewHolder>(); 
//	
//	private Map<String, String> paramsviewIDholder = new HashMap<String, String>();
//	private Map<String, String> paramsviewXSLTholder = new HashMap<String, String>();
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.programs.Program#transform(java.util.List, java.util.List, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink)
//	 * @param sources The <tt>DataSources</tt> from which the <tt>Program</tt> will get the <tt>DataElements</tt>.
//	 * @param programParameters The parameters of the <tt>Program</tt> which are primarily set by the <tt>TransformationUnit</tt>.
//	 * @param targetContentType The <tt>ContentType</tt> in which the source data will be transformed.
//	 * @param sink The <tt>DataSink</tt> in which the <tt>Program</tt> will append the transformed <tt>DataElements</tt>.
//	 * @throws Exception If the program is not capable to transform <tt>DataElements</tt>.
//	 */
//	public void transform(List<DataSource> sources, List<Parameter> programParameters, ContentType targetContentType, DataSink sink) throws Exception {
//
//		if(programParameters==null || programParameters.size()==0){
//			log.error("Program parameters not set");
//			throw new Exception("Program parameters not set");
//		}
//		
//		String xsltID=null;
//		
//		for(Parameter param: programParameters){
//			log.debug("Trans FWD parama: "+param.getName());
//			if(param.getName().equalsIgnoreCase("finalfwdxslt")){
//				xsltID=param.getValue();
//				String xslt;
//				if(xsltID!=null && xsltID.trim().length()>0){
//					log.debug("Got final XSLT ID "+xsltID);
//					try {
//						xslt=XSLTRetriever.getXSLTFromIS(xsltID, DTSSManager.getScope());
//					} catch (Exception e) {
//						log.error("Did not manage to retrieve the XSLT with ID "+xsltID+", aborting transformation...");
//						throw new Exception("Did not manage to retrieve the XSLT with ID "+xsltID);
//					}
//				}else{
//					log.error("Program parameters do not contain xslt");
//					throw new Exception("Program parameters do not contain xslt");
//				}
//				
//				try {
//					TransformerFactory factory = TransformerFactory.newInstance();
//					finalxslttemplate = factory.newTemplates(new StreamSource(new StringReader(xslt)));
//				} catch (Exception e) {
//					log.error("Failed to compile the XSLT: " + xslt, e);
//					throw new Exception("Failed to compile the XSLT");
//				}
//			}
//			
//			if(param.getName().startsWith("view:"))
//				parseViewParam(param);
//			
//		}
//		buildViewReadersXSLTs();
//		
//		transformByXSLT(sources, targetContentType, sink, DTSSManager.getScope());
//	}
//	
//	private void buildViewReadersXSLTs() throws ISException, Exception {
//		Iterator<String> views = paramsviewIDholder.keySet().iterator();
//		while (views.hasNext()) {
//			
//			ViewHolder vholder = new ViewHolder();
//			
//			String paramID = views.next();
//			vholder.XSLTname = paramsviewXSLTholder.get(paramID);
//			vholder.ID = paramsviewIDholder.get(paramID);
//			
//			log.debug("About to add: "+ vholder.XSLTname + " , " + vholder.ID);
//			addCompileXSLTandKeys(vholder);
//			
//			MetadataView tView = new MetadataView(DTSSManager.getScope());
//			tView.setId(vholder.ID);
//			List<MetadataView> newView = tView.findSimilar();
//			vholder.reader = newView.get(0).reader();
//			vholder.lang = newView.get(0).language();
//			allviews.add(vholder);
//			
//			if (viewsperlang.containsKey(vholder.lang)){
//				viewsperlang.get(vholder.lang).add(vholder);
//			}else{
//				List<ViewHolder> viewlst = new ArrayList<ViewHolder>();
//				viewlst.add(vholder);
//				viewsperlang.put(vholder.lang, viewlst);
//			}
//		}
//	}
//
//	private void parseViewParam(Parameter param) throws Exception {
//		log.debug("Parse param: "+ param.getName() + " , " + param.getValue());
//		if (param.getValue().equals("-")){
//			log.debug("Skipping: "+ param.getName() + " , " + param.getValue());
//			return;
//		}
//		String key = param.getName();
//		String[] tokens = key.split(":");
//		String paramID = tokens[1];
//		String paramType = tokens[2];
//
//		log.debug("Param type: " + paramType);
//		
//		if (paramType.equalsIgnoreCase("id")){
//			paramsviewIDholder.put(paramID, param.getValue());
//			log.debug("Marked ID "+ param.getValue());
//		}else if (paramType.equalsIgnoreCase("XSLT")){
//			log.debug("Marked xslt "+ param.getValue());
//			paramsviewXSLTholder.put(paramID, param.getValue());
//		}
//	}
//
//	private void addCompileXSLTandKeys(ViewHolder vholder) throws Exception {
//		if(vholder.ID!=null && vholder.ID.trim().length()>0){
//			log.debug("Got XSLT ID "+ vholder.XSLTname +" for collection : "+ vholder.ID);
//			try {
//				vholder.XSLT=XSLTRetriever.getXSLTFromIS(vholder.XSLTname, DTSSManager.getScope());
//			} catch (Exception e) {
//				log.error("Did not manage to retrieve the XSLT with ID " + vholder.ID + ", aborting transformation...");
//				throw new Exception("Did not manage to retrieve the XSLT with ID "+ vholder.ID);
//			}
//		}else{
//			log.error("Program parameters do not contain xslt");
//			throw new Exception("Program parameters do not contain xslt");
//		}
//		
//		TransformerFactory factory = null;
//		Transformer serializer = null;
//		try {
//			factory = TransformerFactory.newInstance();
//			vholder.compiledXSLT = factory.newTemplates(new StreamSource(new StringReader(vholder.XSLT)));
//		} catch (Exception e) {
//			log.error("Failed to compile the XSLT: " + vholder.XSLTname, e);
//			throw e;
//		}
//
//		/* Create a transformer that will be used for serializing the transformed elements */
//		
//		try {
//			serializer = factory.newTransformer();
//			serializer.setOutputProperty("omit-xml-declaration", "yes");
//		} catch (Exception e) {
//			log.error("Failed to create serializer.", e);
//			throw new Exception("Failed to create serializer.", e);
//		}
//
//		/* Retrieve the keys description from the XSLT definition */
//		NodeList keys = null;
//		boolean foundKeysDesc = true;
//		try {
//			keys = (NodeList) xpath.evaluate("//*[local-name()='variable']/self::node()[@name='keys']/key",
//					new InputSource(new StringReader(vholder.XSLT)), XPathConstants.NODESET);
//		} catch (Exception e) {
//			foundKeysDesc = false;
//		}
//
//		if (!foundKeysDesc || keys==null || keys.getLength()==0) {
//			log.error("Unable to locate the 'keys' variable in the given XSLT." +
//					"Make sure the parameter is defined like this:\n" +
//			"<xsl:variable name=\"keys\"> <key><keyName/><keyXPath/></key> ... </xsl:param>");
//			throw new Exception("Unable to locate the 'keys' variable in the given XSLT." +
//					"Make sure the parameter is defined like this:\n" +
//			"<xsl:variable name=\"keys\"> <key><keyName/><keyXPath/></key> ... </xsl:param>");
//		}
//
//		/* Parse the key descriptions */
//		vholder.keyDescs = null;
//		try {
//			vholder.keyDescs = new Object[keys.getLength()][];
//			for (int i=0; i<keys.getLength(); i++) {
//				Node n = keys.item(i);
//				vholder.keyDescs[i] = new Object[2];
//				vholder.keyDescs[i][0] =  ((Element) n).getElementsByTagName("keyName").item(0).getTextContent();
//				vholder.keyDescs[i][1] =  xpath.compile(((Element) n).getElementsByTagName("keyXPath").item(0).getTextContent());
//				log.debug("Xpath: " +((Element) n).getElementsByTagName("keyXPath").item(0).getTextContent());
//			}
//		} catch (Exception e) {
//			log.error("Failed to parse and compile the key descriptions.", e);
//			throw new Exception("Failed to parse and compile the key descriptions.", e);
//		}
//	}
//
//	private void transformByXSLT(List<DataSource> sources, ContentType targetContentType, DataSink sink, GCUBEScope scope) throws Exception{
//		if(sources.size()!=1){
//			throw new Exception("Elm2ElmProgram is only applicable for programs with one Input");
//		}
//		DataSource source = sources.get(0);
//		while(source.hasNext()){
//			log.debug("Source has next...");
//			DataElement object = source.next();
//			if(object!=null){
//				StrDataElement transformedObject = StrDataElement.getSinkDataElement(object);;
//				try {
//					String gcubedocID = object.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID);
//					log.debug("Got next object with ID "+gcubedocID);
//					
//					MetadataProjection mp = Projections.metadata();
//					
//					Iterator<String> langiter = viewsperlang.keySet().iterator();
//					String rowsetsforgdoc = "";
//					
//					while (langiter.hasNext()) {
//						String language = (String) langiter.next();
//						Iterator<ViewHolder> viewsiter = viewsperlang.get(language).iterator();
//
//						String payload = "";
//						while (viewsiter.hasNext()) {
//							ViewHolder viewholder = viewsiter.next();
//							String viewid = (String) viewholder.ID;
//							log.debug("Metadata reader:" + viewid+ " gdocID: "+ gcubedocID);
//							GCubeDocument gdoc = null;
//							try{
//								gdoc = viewholder.reader.get(gcubedocID, mp);
//								if (gdoc.metadata().size() > 1)
//									log.warn("For gdoc "+gcubedocID+" we have more than one metadata docs using view "+viewid+". Using the first.");
//								if (gdoc.metadata().size() == 0){
//									log.warn("For gdoc "+gcubedocID+" we have no metadata docs. Continuing.");
//									continue;
//								}
//								GCubeMetadata metadata = gdoc.metadata().iterator().next();
//								log.debug("Metadata:" + metadata.id());
//								String metaplayload = getContent(metadata, scope);
//								
//								payload += transformDataElementByXSLT(metaplayload, viewholder);
//							}catch(Exception x){
//								log.warn("Failed to retrive/transform document.\n" +
//										"\n Metadata view ID: " + viewid +
//										"\n gDoc ID: " + gcubedocID +". \n Continuing.");
//								continue;
//							}
//						}
//						payload = "<__Agregate_>" + payload + "</__Agregate_>";
//						payload = transformDataElementByXSLT(payload, finalxslttemplate);
//						payload = addFooterFields(payload, object, language);
//
//						rowsetsforgdoc += payload;						
//					}
//
//					transformedObject.setId(gcubedocID);
//					transformedObject.setContentType(targetContentType);
//
//					transformedObject.setContent(rowsetsforgdoc);
////					log.debug("Final payload: "+ rowsetsforgdoc);
//					log.debug("Got transformed object with id: "+transformedObject.getId()+" and content format: "+transformedObject.getContentType().toString()+", appending into the sink");
//					ReportManager.manageRecord(object.getId(), "Data element with id "+object.getId()+
//							"was transformed successfully to "+transformedObject.getContentType().toString(), Status.SUCCESSFUL, Type.TRANSFORMATION);
//				} catch (Exception e) {
//					log.error("Could not transform Data Element, continuing to next...",e);
//					ReportManager.manageRecord(object.getId(), "Data element with id "+object.getId() +
//							"could not be transformed to "+targetContentType.toString(), Status.FAILED, Type.TRANSFORMATION);
//					continue;
//				}
//				sink.append(transformedObject);
//				log.debug("Transformed object with id: "+transformedObject.getId()+", was appended successfully");
//			}else{
//				log.warn("Got null object from the data source");
//			}
//			
//		}
//		log.debug("Source does not have any objects left, closing the sink...");
//		sink.close();
//	}
//	
//
//	private String transformDataElementByXSLT(String metaplayload,
//			ViewHolder viewholder) throws Exception {
//
//		/* Transform the current source element using the given XSLT. The output of the XSLT
//		 * is a forward index rowset whose 'key' is empty, because the actual key is filled in
//		 * by this program later.
//		 */
//		StringWriter output = new StringWriter();
//		Transformer t = viewholder.compiledXSLT.newTransformer();
//		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//		t.transform(new StreamSource(new StringReader(metaplayload)), new StreamResult(output));
//
//		String transformedPayload = output.toString();
//		Document transformedDoc = XMLStringParser.parseXMLString(transformedPayload);
//		DOMSource serializerSource = new DOMSource();
//		serializerSource.setNode(transformedDoc);
//
//		/* One source element may produce more than one output elements. For this reason, use
//		 * the XPath expression defined in each key description to retrieve all the values that
//		 * the current source element contains for each key. Then fill in the 'keys' element of
//		 * the rowset produced above using each different key name and value, and add the resulting
//		 * 'complete' rowset to the output data sink.
//		 */
//		Element elTuple = (Element) xpath.evaluate("/ROWSET/INSERT/TUPLE", transformedDoc, XPathConstants.NODE);
//		Element elValue = (Element) elTuple.getElementsByTagName("VALUE").item(0);
//		for (Object[] keyDesc : viewholder.keyDescs) {
//			String keyName = (String) keyDesc[0];
//			XPathExpression keyXPath = (XPathExpression) keyDesc[1];
//			NodeList keyValueList = null;
//			keyValueList = (NodeList) keyXPath.evaluate(
//					new InputSource(new StringReader(metaplayload)),
//					XPathConstants.NODESET);
//			if (keyValueList!=null && keyValueList.getLength()>0) {
//				for (int z=0; z<keyValueList.getLength(); z++) {
//
//					String keyValue = keyValueList.item(z).getTextContent();
//					log.debug("keyValue "+z+": "+ keyValue);
//					if (keyValue.trim().length() > 0) {
//						/* Create a KEY element, with a KEYNAME and a KEYVALUE child elements. Set
//						 * the key name as the text content of the KEYNAME element, and the current
//						 * value in the KEYVALUE element. */
//						Element elKeyName = transformedDoc.createElement("KEYNAME");
//						elKeyName.setTextContent(keyName);
//						Element elKeyValue = transformedDoc.createElement("KEYVALUE");
//						elKeyValue.setTextContent(keyValue);
//						Element elKey = transformedDoc.createElement("KEY");
//						elKey.appendChild(elKeyName);
//						elKey.appendChild(elKeyValue);
//						elTuple.insertBefore(elKey, elValue);
//						
//						log.debug("Element to add: "+elKey.getTextContent());
//					}
//				}
//			}
//		}
//		transformedPayload = XMLStringParser.XMLDocToString(transformedDoc);
//		return transformedPayload;
//	}
//
//	private String addFooterFields(String transformedPayload,
//			DataElement sourceDataElement, String lang) throws Exception {
//		/* Parse the transformed payload and set the target OID.
//		 * If the payload contains only a ROW element with no
//		 * attributes, this record should be ignored.
//		 */
////		log.debug("Adding footer to: " + transformedPayload + ".");		
//		Document doc = XMLStringParser.parseXMLString(transformedPayload);
//		
//		Element elTuple = (Element) xpath.evaluate("/ROWSET/INSERT/TUPLE", doc, XPathConstants.NODE);
//		Element elValue = (Element) elTuple.getElementsByTagName("VALUE").item(0);
//		
//		{
//			// Add Key for "ObjectID"
//			Element elKeyName = doc.createElement("KEYNAME");
//			elKeyName.setTextContent("ObjectID");
//			Element elKeyValue = doc.createElement("KEYVALUE");
//			elKeyValue.setTextContent(sourceDataElement.getId());
//			Element elKey = doc.createElement("KEY");
//			elKey.appendChild(elKeyName);
//			elKey.appendChild(elKeyValue);
//			elTuple.insertBefore(elKey, elValue);
//		}
//		{
//			// Add Key for "gDocCollectionID"
//			Element elKeyName = doc.createElement("KEYNAME");
//			elKeyName.setTextContent("gDocCollectionID");
//			Element elKeyValue = doc.createElement("KEYVALUE");
//			elKeyValue.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
//			Element elKey = doc.createElement("KEY");
//			elKey.appendChild(elKeyName);
//			elKey.appendChild(elKeyValue);
//			elTuple.insertBefore(elKey, elValue);
//		}		
//		{
//			// Add Key for "gDocCollectionLang"
//			Element elKeyName = doc.createElement("KEYNAME");
//			elKeyName.setTextContent("gDocCollectionLang");
//			Element elKeyValue = doc.createElement("KEYVALUE");
//			elKeyValue.setTextContent(lang);
//			Element elKey = doc.createElement("KEY");
//			elKey.appendChild(elKeyName);
//			elKey.appendChild(elKeyValue);
//			elTuple.insertBefore(elKey, elValue);
//		}		
//
//		{
//			// Add !!!!_Field_!!!! for "ObjectID"
//			Element elfield = doc.createElement("FIELD");
//			elfield.setTextContent(sourceDataElement.getId());
//			elfield.setAttribute("name","ObjectID");
//			elValue.appendChild(elfield);
//		}
//		{
//			// Add !!!!_Field_!!!! for "gDocCollectionID"
//			Element elfield = doc.createElement("FIELD");
//			elfield.setTextContent(sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
//			elfield.setAttribute("name","gDocCollectionID");
//			elValue.appendChild(elfield);
//		}
//		{
//			// Add !!!!_Field_!!!! for "gDocCollectionLang"
//			Element elfield = doc.createElement("FIELD");
//			elfield.setTextContent(lang);
//			elfield.setAttribute("name","gDocCollectionLang");
//			elValue.appendChild(elfield);
//		}
//
//		transformedPayload = XMLStringParser.XMLDocToString(doc);
//		return transformedPayload;
//		
//	}
//
//
//	private XPath xpath = XPathFactory.newInstance().newXPath();
//	
//	private String transformDataElementByXSLT(String xmlpayload, Templates compiledXSLT) throws Exception {
////		StrDataElement transformedElement = StrDataElement.getSinkDataElement(sourceDataElement);
////		transformedElement.setContentType(targetContentType);
////		transformedElement.setId(sourceDataElement.getId());
//		
////		log.debug("Transforming: "+xmlpayload);
//		StringWriter output = new StringWriter();
//		Transformer t = compiledXSLT.newTransformer();
//		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//		t.transform(new StreamSource(new StringReader(xmlpayload)), new StreamResult(output));
//		
//		return output.toString();
//		
//	}
//	
//	
//	protected static String getContent(GCubeElement metadata, GCUBEScope scope) throws Exception 
//	{ 
//		
//		InputStream instream = getContnetStream(metadata, scope);
//		if (instream == null)
//			throw new Exception("Metadata document "+metadata.id()+" with no content.");
//
//		Writer writer = new StringWriter();
//
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(
//                    new InputStreamReader(instream, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//        } finally {
//            instream.close();
//        }
//        return writer.toString();
//	}
//
//	private static InputStream getContnetStream(GCubeElement document, GCUBEScope scope) throws MalformedURLException, IOException {
//		if (document.bytestreamURI() != null){
//			if (document.bytestreamURI().getScheme().equals("sms")){
//				try {
//					return SMSURLConnection.openConnection(document.bytestreamURI(), scope.toString()).getInputStream();
//				} catch (URISyntaxException e) {
//					log.error("Cannot get stream for metadata, "+document.id(),e);
//					return null;
//				}
//			}else{
//				return document.bytestreamURI().toURL().openStream();
//			}
//		}else if (document.bytestream() != null){
//			byte[] content = document.bytestream();
//			ByteArrayInputStream ins = new ByteArrayInputStream(content);
//			return ins;
//		}else
//			return null;
//	}
//}
