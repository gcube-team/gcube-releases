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
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathFactory;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.common.core.informationsystem.ISException;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.cms.CMSURLConnection;
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
//
//
//
///**
// * @author Dimitris Katris, NKUA
// * <p>
// * Program creating full text rowsets.
// * </p>
// */
//public class AggregateFts_Transformer implements Program{
//
//	
//	class ViewHolder{
//		public String ID;
//		public String lang;
//		public String XSLT;
//		public Templates compiledXSLT;
//		public ViewReader reader;
//	}
//	
//	private static Logger log = LoggerFactory.getLogger(AggregateFts_Transformer.class);
//	private Templates finalxslttemplate = null;
//
//	private Map<String, List<ViewHolder>> viewsperlang = new HashMap<String, List<ViewHolder>>(); 
//	private List<ViewHolder> allviews = new ArrayList<ViewHolder>(); 
//	
//	private Map<String, String> paramsviewIDholder = new HashMap<String, String>();
//	private Map<String, String> paramsviewXSLTholder = new HashMap<String, String>();
//	
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
//		//Finding xslt and indexType from the program parameters...
//		String xsltID=null;
//		String indexType=null;
//		
//		for(Parameter param: programParameters){
//			log.debug("Trans parama: "+param.getName());
//			if(param.getName().equalsIgnoreCase("finalftsxslt")){
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
//			if(param.getName().equalsIgnoreCase("indexType")){
//				indexType=param.getValue();
//			}
//			
//			if(param.getName().startsWith("view:"))
//				parseViewParam(param);
//			
//		}
//		buildViewReadersXSLTs();
//		log.debug("The index type is "+indexType);
//		
//		transformByXSLT(sources, targetContentType, sink, indexType, DTSSManager.getScope());
//	}
//	
//	private void buildViewReadersXSLTs() throws ISException, Exception {
//		Iterator<String> views = paramsviewIDholder.keySet().iterator();
//		while (views.hasNext()) {
//			
//			ViewHolder vholder = new ViewHolder();
//			
//			String paramID = views.next();
//			vholder.XSLT = paramsviewXSLTholder.get(paramID);
//			vholder.ID = paramsviewIDholder.get(paramID);
//			
//			log.debug("About to add: "+ vholder.XSLT + " , " + vholder.ID);
//			vholder.compiledXSLT = addCompileXSLT(vholder.XSLT, vholder.ID);
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
//	private Templates addCompileXSLT(String xsltID, String viewID) throws Exception {
//		String xslt;
//		if(xsltID!=null && xsltID.trim().length()>0){
//			log.debug("Got XSLT ID "+xsltID +" for collection : "+viewID);
//			try {
//				xslt=XSLTRetriever.getXSLTFromIS(xsltID, DTSSManager.getScope());
//			} catch (Exception e) {
//				log.error("Did not manage to retrieve the XSLT with ID "+xsltID+", aborting transformation...");
//				throw new Exception("Did not manage to retrieve the XSLT with ID "+xsltID);
//			}
//		}else{
//			log.error("Program parameters do not contain xslt");
//			throw new Exception("Program parameters do not contain xslt");
//		}
//		
//		Templates compiledXSLT = null;
//		try {
//			TransformerFactory factory = TransformerFactory.newInstance();
//			compiledXSLT = factory.newTemplates(new StreamSource(new StringReader(xslt)));
//			return compiledXSLT;
//		} catch (Exception e) {
//			log.error("Failed to compile the XSLT: " + xslt, e);
//			throw e;
//		}
//		
//	}
//
//	private void transformByXSLT(List<DataSource> sources, ContentType targetContentType, DataSink sink, String indexType, GCUBEScope scope) throws Exception{
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
//							log.debug("Metadata reader: " + viewid + " gdocID: " + gcubedocID);
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
//								payload += transformDataElementByXSLT(metaplayload, viewholder.compiledXSLT);
//							}catch(Exception x){
//								log.warn("Failed to retrive/transform document.\n" +
//										"\n Metadata view ID: " + viewid +
//										"\n gDoc ID: " + gcubedocID +". \n Continuing.");
//								continue;
//							}
//						}
//						payload = "<__Agregate_>" + payload + "</__Agregate_>";
//						payload = transformDataElementByXSLT(payload, finalxslttemplate);
//						payload = addFooterFields(payload, indexType, object, language);
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
//	private String addFooterFields(String transformedPayload, String indexType,
//			DataElement sourceDataElement, String lang) throws Exception {
//		/* Parse the transformed payload and set the target OID.
//		 * If the payload contains only a ROW element with no
//		 * attributes, this record should be ignored.
//		 */
//		log.debug("Adding footer to: " + transformedPayload + ".");		
//		Document doc = XMLStringParser.parseXMLString(transformedPayload);
//		Node n = (Node) xpath.evaluate("//ROW", doc, XPathConstants.NODE);
//		if (n == null) {
//			log.warn("Couldn't find ROW element in record " + String.valueOf(sourceDataElement.getId()) + ".");
//			throw new Exception("Couldn't find ROW element in record " + String.valueOf(sourceDataElement.getId()) + ".");
//		}
//		
//		/* Set the "indexType" attribute to the ROWSET element */
//		Element elRowset = (Element) xpath.evaluate("//ROWSET", doc, XPathConstants.NODE);
//		elRowset.setAttribute("idxType", indexType);
//
//		
//		log.debug("Get attribute ContentCollectionID "+sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
//		elRowset.setAttribute("colID", sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_COLLECTION_ID));
//
//		elRowset.setAttribute("lang", lang);
//
//		
//		/* Append a "lang" attribute to all rowset fields */
//		NodeList fieldsList = (NodeList) xpath.evaluate("//FIELD", doc, XPathConstants.NODESET);
//		for (int z=0; z<fieldsList.getLength(); z++) {
//			((Element) fieldsList.item(z)).setAttribute("lang", lang);
//		}
//		
//
//		
//		/* Create a FIELD element to hold the content OID */
//		Element elID = doc.createElement("FIELD");
//		elID.setAttribute("name", "ObjectID");					
//		elID.setTextContent(sourceDataElement.getId());
//		
//		/* Append the FIELD element to the ROW element */
//		Element elRow = (Element) xpath.evaluate("//ROW", doc, XPathConstants.NODE);
//		elRow.appendChild(elID);
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
//		log.debug("Transforming: "+xmlpayload);
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
