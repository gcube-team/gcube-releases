package org.gcube.data.oai.tmplugin.binders;

import static org.gcube.data.trees.data.Nodes.e;
import static org.gcube.data.trees.data.Nodes.n;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.axis2.util.XMLUtils;
import org.gcube.common.data.Record;
import org.gcube.data.oai.tmplugin.RepositoryProvider;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.utils.Constants;
import org.gcube.data.oai.tmplugin.utils.Utils;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 Tree model:
item metadata: global information on the item including
title: the title of the record;
collectionID: the collection this item belongs to;
creationTime: the time the item was created;
lastUpdateTime: the most recent time the item has been updated;
provenance: It is characterised by the following information:
*statement: "This item has been created by "+ pluginName +" via OAI-PMH metadata harvesting from the metadata provider "+repositoryName+" at "+baseURL;
*setID: the repository set the object belongs to (optional and repeatable);

metadata (repeatable): the metadata record harvested. It is characterised by the following information:
*schema: the metadata format of the metadata record;
*schemaLocation: the metadata format schema URI;
*record: the manifestation of the metadata record harvested;

content (repeatable): any potential payload shipped with the metadata record. It is characterised by the following information:
*contentType: i.e. whether main or alternative content;
*mimeType: MIME type of the actual content;
*url: URL to the actual content;
 */


public class OAIDCBinder implements Serializable {

	private static final long serialVersionUID = -8037198825787888863L;

	private final static Logger log = LoggerFactory.getLogger(OAIDCBinder.class);
	private final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private final static TransformerFactory transformerFactory = TransformerFactory.newInstance();

	private Request request;

	public OAIDCBinder(Request req) {	
		super();	
		this.request = req;
//		log.info("***OAIDCBinder*** " + request.getRepositoryUrl());
	}

	//backdoor for testing
	public OAIDCBinder(RepositoryProvider p) {

	}

	public Tree bind(Record record) throws Exception {

		try {
			String encodeId = Utils.idEncoder(record.getHeader().getIdentifier());
//			System.out.println("encodeId " + encodeId);
			Tree tree = new Tree(encodeId);
			StringWriter writer = new StringWriter();

			Transformer transformer= transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
		    Element eprElement = XMLUtils.toDOM(record.getMetadataElement().getFirstElement());
////		    Source eprInfoset = new DOMSource(eprElement);
//		        
			transformer.transform(new DOMSource(eprElement), new StreamResult(writer));

			//Adding default metadata
			String metadata = writer.toString();

			//			log.info(metadata);
			Document metadataDocument = factory.newDocumentBuilder().parse(new InputSource(new StringReader(metadata)));
			
			
			//create base information edge
			try { 

				List<Edge> mEdge = createBaseInfoEdge(record, tree, metadataDocument);
				tree.add(mEdge); 
			} catch (Exception te) {
				log.warn("error retrieving the metadata",te);
			}

			// create MetadataEdge
			try { 

				Edge mEdge = createMetadataEdge(metadataDocument, record.getMetadataPrefix(), record.getMetadata().getSchemaLocation());
				tree.add(mEdge); 
			} catch (Exception te) {
				log.warn("error retrieving the metadata",te);
			}

			// create MainContentEdge
			try { 

				Edge mEdge = createMainContentEdge(record, tree, metadataDocument);
				tree.add(mEdge); 
			} catch (Exception te) {
				log.warn("error retrieving the metadata",te);
			}

			// create AlternativeContentEdge
			try { 
				List<Edge> mEdge = createAlternativeContentEdge(tree, metadataDocument);
				tree.add(mEdge); 
			} catch (Exception te) {
				log.warn("error retrieving the metadata",te);
			}
			return tree;

		}
		catch(Exception e) {
//			System.out.println("could not convert record:"+record.getHeader().getIdentifier());
			log.error("could not convert record:"+record.getHeader().getIdentifier(),e);
			throw new Exception("could not convert record",e);
		}
	}



	//create base information edge
	private List<Edge> createBaseInfoEdge(Record record, Tree tree, Document metadataDocument) throws Exception {

//		log.info("createBaseInfoEdge(" + record + "," + tree + "," + metadataDocument+")");
		List<Edge> edgeList= new ArrayList<Edge>();

		try {
			//create titleEdge
			Edge titleEdge = e(Labels.TITLE,"");
			//retrieving the record title
			if (request.getTitleXPath()!=null){

				try{	
					XPathFactory xpathFactory = XPathFactory.newInstance();
					XPath xPath= xpathFactory.newXPath();
					NodeList nl=(NodeList) xPath.evaluate(request.getTitleXPath(),metadataDocument,XPathConstants.NODESET);
					if (nl.item(0)!=null){
						String recordName= nl.item(0).getTextContent();
						titleEdge = e(Labels.TITLE,recordName);
					}
				}catch(Exception e){
					log.warn("cannot calculate the title of "+tree.id(),e);
				}
			}
			edgeList.add(titleEdge);

			// create setEdge
			
		
			Edge setEdge = e(Labels.COLLECTION_ID, "");
			String collID = "";
			try{
				collID = record.getHeader().getSpecList().get(0);
			}catch (Exception e) {
				log.warn("cannot set the collection ID" ,e);
			}
			try {
				setEdge = e(Labels.COLLECTION_ID,collID);				
		
			} catch (Exception e) {
				log.warn("cannot create setEdge" ,e);
			}

			edgeList.add(setEdge);

			Calendar timestamp = null;
			try {
				timestamp = Constants.getDate(record.getHeader().getDatestamp());
			} catch (Exception e1) {
				log.warn("cannot caluculate" ,timestamp);
			}

			// create creationTimeEdge
			Edge creationTimeEdge = e(Labels.CREATION_TIME,timestamp);
			edgeList.add(creationTimeEdge);


			// create lastUpdateTimeEdge		
			Edge lastUpdateTimeEdge = e(Labels.LAST_UPDATE,timestamp);
			edgeList.add(lastUpdateTimeEdge);	


			// create provenanceEdge	
			Edge provenanceEdge =  e(Labels.PROVENANCE, "");

			try {
				List<Edge> provenanceEdgeList = createProvenanceEdge(record);

				provenanceEdge = e(Labels.PROVENANCE,
						n(provenanceEdgeList.toArray(new Edge[0])));
				edgeList.add(provenanceEdge);
			}catch(Exception e){
				log.warn("cannot create  provenance Edge ",e);
			}


		}catch (Exception e) {
			//			log.error("could not convert record:"+record,e);
			throw new Exception("could not create BaseInfo Edge ",e);
		}

		return edgeList;

	}


	//create provenance Edge
	private List<Edge> createProvenanceEdge(Record record) throws Exception {

		String provenance = "This item has been created by the gCube OAI-TM plugin via OAI-PMH metadata harvesting from the metadata provider "+  request.getName() +" at "+ request.getRepositoryUrl();

		List<Edge> provenanceEdgeList = new ArrayList<Edge>();

		provenanceEdgeList.add(e(Labels.STATEMENT,provenance));

		List<String> sets = record.getHeader().getSpecList();
		for (int i=0; i<sets.size(); i++) {

			provenanceEdgeList.add(e(Labels.SET_ID,sets.get(i)));
		}

		provenanceEdgeList.add(e(Labels.RECORD_ID, record.getHeader().getIdentifier()));

		return provenanceEdgeList;
	}

	// create MetadataEdge
	private Edge createMetadataEdge(Document metadataDocument, String schema, String schemaLocation) throws Exception{		
		Edge metadataEdge = createTreeByMetadata(metadataDocument);
		
		if (schema==null)
			schema = "";
		if (schemaLocation==null)
			schemaLocation = "";
		
		return e(Labels.METADATA, new InnerNode(e(Labels.SCHEMA,schema),e(Labels.SCHEMALOCATION,schemaLocation),
				metadataEdge));

	}


	//transform XML metadata to tree
	private Edge createTreeByMetadata(Document doc) {
		Edge metadataEdge = e(Labels.RECORD, "");
		try{
			
//			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			
			Edge edge = printNodeInfo(doc.getDocumentElement(), null);
			metadataEdge = e(Labels.RECORD, new InnerNode(edge));

			//			printNodeInfo(doc.getDocumentElement());
		}catch (Exception e) {
			log.error("error creating TreeByMetadata", e);
		}
		return metadataEdge;
	}


	
	
	/**
	 * Print Node info
	 * @param currentNode
	 * @param edge
	 * @return
	 */

	public Edge printNodeInfo(Node currentNode, Edge edge) {
		//		System.out.println("currentNode: " + currentNode);
		short sNodeType = currentNode.getNodeType();
		String sNodeName = null;
		List<Edge> children = new ArrayList<Edge>();

		if (sNodeType == Node.ELEMENT_NODE) {

			//if nodeType is element, print info

			sNodeName = currentNode.getNodeName();

			//			System.out.println("currentNode.getNodeName(): " + sNodeName);
			String sNodeValue = searchTextInElement(currentNode);		
			if (sNodeValue!=null){
				if (!sNodeValue.trim().equalsIgnoreCase("")) {
					edge = e(sNodeName, sNodeValue);		
					//					System.out.println(sNodeName + " " + sNodeValue);
				}
				else if (!currentNode.hasChildNodes()){
					//					System.out.println(sNodeName +" has no value");
					edge = e(sNodeName, "");	
				}

			}

			int iChildNumber = currentNode.getChildNodes().getLength();

			//			System.out.println("has " + iChildNumber + " children" );
			//if it's not a leafe, continue
			if (currentNode.hasChildNodes()) {		

				NodeList nlChilds = currentNode.getChildNodes();

				for (int iChild = 0; iChild < iChildNumber; iChild++) {
					//					System.out.println("iChildNumber: " +iChildNumber);
					if (nlChilds.item(iChild).getNodeType() == Node.ELEMENT_NODE) {
						//	System.out.println("child: " +nlChilds.item(iChild).toString());

						children.add(printNodeInfo(nlChilds.item(iChild), null));

					}
				}
			}
		}
		//		System.out.println("edges.size(): "+ edges.size());
		if (children.size()>0){
			InnerNode innerNode = new InnerNode(children.toArray(new Edge[0]));			
			edge = e (sNodeName, innerNode);
		}

		NamedNodeMap nnmAttributes = currentNode.getAttributes();

		if (nnmAttributes.getLength() > 0){
			//			System.out.println("******* " + currentNode.getNodeName() + " has attribute");
			setAttributes(nnmAttributes, edge.target());
		}

		//		System.out.println("return " + edge.toString());
		return edge;
	}

	/*
	 * Search the content for a given Node
	 */
	private static String searchTextInElement(Node elementNode) {
		String sText = "";
		if (elementNode.hasChildNodes()) {
			Node nTextChild = elementNode.getChildNodes().item(0);
			sText = nTextChild.getNodeValue();
		}
		return sText;
	}



	private static boolean setAttributes(NamedNodeMap nnm, org.gcube.data.trees.data.Node node) {

		if (nnm != null && nnm.getLength() > 0) {
			for (int iAttr=0; iAttr < nnm.getLength(); iAttr++) {
				String name = nnm.item(iAttr).getNodeName();
				String value = nnm.item(iAttr).getNodeValue();
				//				System.out.println("Attribute: "+ name + " - " + value);

				if (!name.contains("xsi"))
					node.setAttribute(name, value);

			}
			return true;
		}		
		else
			return false;
	}

	// create main ContentEdge (identifier)
	private Edge createMainContentEdge(Record record, Tree tree, Document metadataDocument) throws Exception {

		Edge contentTypeEdge = e(Labels.CONTENT_TYPE,"");
		Edge mimeTypeEdge = e(Labels.MIME_TYPE,"");			
		Edge urlEdge = e(Labels.URL,"");
		try{

			if (request.getContentXPath()!=null){

				try{
					contentTypeEdge=e(Labels.CONTENT_TYPE,"main");

					XPathFactory factory = XPathFactory.newInstance();
					XPath xPath= factory.newXPath();

					NodeList nl=(NodeList) xPath.evaluate(request.getContentXPath(),metadataDocument,XPathConstants.NODESET);

					//					log.trace("time spent to retrieve content "+(System.currentTimeMillis()-startcontent)); 

					if (nl.item(0)!=null){
						String contentUri= nl.item(0).getTextContent();
						String url = Utils.resolver(contentUri);
						try{
							mimeTypeEdge=e(Labels.MIME_TYPE,"text/url");
						}catch(Exception e){
							log.warn("could not retrieve mime type for "+ record.getHeader().getIdentifier());
						}

						urlEdge= e(Labels.URL,url);
						//logger.trace("time spent to add content as edge "+(System.currentTimeMillis()-startrest));

					}
				}catch(Throwable e){
					log.error("could not retrieve content for "+tree.id(),e);
				}

			}
		}catch (Exception e) {
			throw new Exception("could not create MainContent Edge ",e);
		}
		return (e(Labels.CONTENT, new InnerNode(contentTypeEdge, mimeTypeEdge, urlEdge)));
	}



	// create alternative ContentEdge
	private List<Edge> createAlternativeContentEdge(Tree tree, Document metadataDocument) throws Exception {
		//adding alternatives
		List<Edge> edgeList= new ArrayList<Edge>();
		try{
			for (int i=0; i<request.getAlternativesXPath().size(); i++) {

				String path = request.getAlternativesXPath().get(i);
//				System.out.println(path);
//								log.info("***AlternativesXPath*** " + path);
				try{
					edgeList = createAlternatives(i, path, metadataDocument);
				} catch (Exception te) {
					log.warn("could not retrieve alternatives in "+tree.id()+" with path "+path,te);}
			}

		}catch (Exception e) {
			throw new Exception("could not create AlternativeContent Edge ",e);
		}
		return edgeList;
	}


	//	create Alternatives Xpath
	private List<Edge> createAlternatives(int identifier, String path, Document metadataDocument) throws Exception{

		List<Edge> edgeList = null;
		try{

			edgeList = new ArrayList<Edge>();

			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath= factory.newXPath();

			NodeList nl=(NodeList) xPath.evaluate(path, metadataDocument,XPathConstants.NODESET);

//			log.info("found "+nl.getLength()+" element for alternatives ");

			for (int i=0; i<nl.getLength(); i++){

				Edge contentTypeEdge=e(Labels.CONTENT_TYPE,"alternative");
				String alternativeUri= "";
				try{
									
				alternativeUri= nl.item(i).getTextContent();
//				log.info("alternative uri "+alternativeUri);

				Edge mimeTypeEdge=e(Labels.MIME_TYPE,"none");

				URI uri= new URI(alternativeUri);
				URLConnection urlConnection= null;
					urlConnection=uri.toURL().openConnection();
					mimeTypeEdge=e(Labels.MIME_TYPE,urlConnection.getContentType());
					
					edgeList.add(e(Labels.CONTENT,
						n(		contentTypeEdge,
								mimeTypeEdge,							
								e(Labels.URL,alternativeUri)
								)));
					
				
				}catch(Exception e){				
					log.warn("impossible to retrieve alternative, maybe the URI has an illegal characters: " + alternativeUri);
					continue;
				}
					
			}
		}catch (Exception e) {
			throw new Exception("could not create Alternatives ",e);
		}
		return edgeList; 
	}


}