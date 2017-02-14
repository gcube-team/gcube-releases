package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.indexfeed;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Program creating full text rowsets.
 * </p>
 */
public class GeoRowset_Transformer implements Program{

	private static Logger log = LoggerFactory.getLogger(GeoRowset_Transformer.class);
	
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
		String xsltID=null;
		String indexType=null;
		
		for(Parameter param: programParameters){
			if(param.getName().equalsIgnoreCase("geoxslt")){
				xsltID=param.getValue();
			}
			if(param.getName().equalsIgnoreCase("indexType")){
				indexType=param.getValue();
			}
		}
		
		log.debug("The index type is "+indexType);
		
		String xslt;
		if(xsltID!=null && xsltID.trim().length()>0){
			log.debug("Got XSLT ID: "+xsltID);
			try {
				xslt=XSLTRetriever.getXSLTFromIS(xsltID, DTSScope.getScope());
			} catch (Exception e) {
				log.error("Did not manage to retrieve the XSLT with ID "+xsltID+", aborting transformation...");
				throw new Exception("Did not manage to retrieve the XSLT with ID "+xsltID);
			}
		}else{
			log.error("Program parameters do not contain xslt");
			throw new Exception("Program parameters do not contain xslt");
		}
		
		Templates compiledXSLT = null;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			compiledXSLT = factory.newTemplates(new StreamSource(new StringReader(xslt)));
		} catch (Exception e) {
			log.error("Failed to compile the XSLT: " + xslt, e);
			throw new Exception("Failed to compile the XSLT");
		}
		transformByXSLT(sources, compiledXSLT, targetContentType, sink, indexType);
	}
	
	private void transformByXSLT(List<DataSource> sources, Templates compiledXSLT, ContentType targetContentType, DataSink sink, String indexType) throws Exception{
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
					transformedObject = transformDataElementByXSLT(object, compiledXSLT, targetContentType, indexType);
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
	
	private XPath xpath = XPathFactory.newInstance().newXPath();
	
	private DataElement transformDataElementByXSLT(DataElement sourceDataElement, Templates compiledXSLT, ContentType targetContentType, String indexType) throws Exception {
		StrDataElement transformedElement = StrDataElement.getSinkDataElement(sourceDataElement);
		transformedElement.setContentType(targetContentType);
		transformedElement.setId(sourceDataElement.getId());
		
		StringWriter output = new StringWriter();
		try {
			Transformer t = compiledXSLT.newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			if(sourceDataElement instanceof StrDataElement){
				t.transform(new StreamSource(new StringReader(((StrDataElement)sourceDataElement).getStringContent())), new StreamResult(output));
			}else{
				t.transform(new StreamSource(sourceDataElement.getContent()), new StreamResult(output));
			}
		} catch (Exception e) {
			log.error("Failed to transform element with ID = " + sourceDataElement.getId());
			throw new Exception("Failed to transform element with ID = " + sourceDataElement.getId());
		}
		
		String transformedPayload = output.toString();
		
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
		elRowset.setAttribute("lang", sourceDataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_LANGUAGE));
		elRowset.setAttribute("colID", sourceDataElement.getAttributeValue("ContentCollectionID"));
		
		/* Check the attributes of the ROW element and skip the current record if there are any "NaN" values in them */ 
		NamedNodeMap attrs = n.getAttributes();
		if (attrs==null || attrs.getLength()==0){
			throw new Exception("ROW element has no coordination attrs");
		}
		try {
			if (attrs.getNamedItem("x1").getNodeValue().equals("NaN"))
				return null;
			if (attrs.getNamedItem("x2").getNodeValue().equals("NaN"))
				return null;
			if (attrs.getNamedItem("y1").getNodeValue().equals("NaN"))
				return null;
			if (attrs.getNamedItem("y2").getNodeValue().equals("NaN"))
				return null;
		} catch (Exception e) { /* x1, x2, y1 or y2 does not exist. No error, just continue. */ }
		
		/* Set the 'id' attribute to the target object ID */
		((Element) n).setAttribute("id", sourceDataElement.getAttributeValue("ContentOID"));
		
		transformedPayload = XMLStringParser.XMLDocToString(doc);
		
		transformedElement.setContent(transformedPayload);
		return transformedElement;
	}
}
