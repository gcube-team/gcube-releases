package org.gcube.application.framework.contentmanagement.datatransformation.util;

import java.util.ArrayList;

import org.gcube.application.framework.contentmanagement.exceptions.OCRException;
import org.gcube.application.framework.contentmanagement.exceptions.ReadingRSException;
import org.gcube.application.framework.contentmanagement.exceptions.ServiceEPRRetrievalException;
import org.gcube.application.framework.contentmanagement.util.DocumentInfos;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.util.ServiceType;
import org.gcube.application.framework.core.util.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTransformationUtils {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataTransformationUtils.class);
	
	
	/**
	 * Transforms a list of PDF documents to Text documents, using DTS. It returns an RSLocator of the resultset containing the reports for the transformations. 
	 * @param listLocation - the location of the file containing the document URIs
	 * @param collectionId	- the output collection id requested (empty if a new collection is about to be created)
	 * @param collectionName - the name of the output collection id requested
	 * @param scope
	 * @return returns the rsLocator of the resultset, containing the reports from the transformation
	 * @throws ServiceEPRRetrievalException 
	 * @throws TransformationException 
	 */ 
	
//	public static String transformPDFDocumentsToText(String listLocation, ArrayList<String> collectionId, String collectionName, String scope) throws ServiceEPRRetrievalException, TransformationException {
//		TransformData request = new TransformData();
//		
//		//INPUT
//		Input input = new Input();
//		input.setInputType("URIList");
//		input.setInputValue(listLocation);
//		logger.info("The URI Location IS: " + listLocation);
//		request.setInput(input);
//
//		// OUTPUT
//		Output output = new Output();
//		output.setOutputType("Collection");
//		output.setOutputValue(collectionId.get(0));
//		Parameter outputParam = new Parameter();
//		outputParam.setName("CollectionName");
//		outputParam.setValue(collectionName);
//		Parameter outputParam2 = new Parameter();
//		outputParam2.setName("CollectionDesc");
//		outputParam2.setValue("Collection of Text documents, created from PDFs.");
//		Parameter outputParam3 = new Parameter();
//		outputParam3.setName("isUserCollection");
//		outputParam3.setValue("false");
//		Parameter [] outputParams = {outputParam, outputParam2, outputParam3};
//		output.setOutputparameters(outputParams);
//		request.setOutput(output);
//
//		// TARGET CONTENT TYPE 
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/plain");
//		Parameter [] contentTypeParameters = {};
//		targetContentType.setParameters(contentTypeParameters);
//		request.setTargetContentType(targetContentType);
//		
////		request.setTPID(transformationProgramID);
////		request.setTransformationUnitID(transformationUnitID);
//
//		request.setCreateReport(true);
//		
//		DataTransformationServicePortType dts = null;
//		try {
//			dts = getDataTransformationServicePortType(scope);
//		} catch (Exception e) {
//			throw new ServiceEPRRetrievalException(e);
//		}
//		String rslocator = null;
//		String colId = null;
//		TransformDataResponse response = new TransformDataResponse(); //= null;
//		try{ 
//			//TODO: USE CLIENT LIBRARIES FOR BELOW CALL
////			response = dts.transformData(request);
//		}
//		catch (Exception e){
//			logger.error(e.toString());
//		}
//		rslocator = response.getReportEPR();
//		colId = response.getOutput();
//		collectionId.clear();
//		collectionId.add(0, colId);
//		
//		logger.info("Output: "+rslocator);
//	
//		return rslocator;
//	}
	
	
	/**
	 * It parses the reports contained in the resultset, coming from DTS and returns the list of the document URIs that failed to be transformed.
	 * @param rsLocator - the RSLocator containing the reports from DTS
	 * @param allDocuments - list of all the documents that participated in the transformation attempt
	 * @param collectionId - empty list that needs to be filled with the id of the Collection Output
	 * @return the documents that failed to be transformed
	 * @throws ReadingRSException 
	 */
//	public static ArrayList<DocumentInfos> getListOfFailuresFromReport(String rsLocator, ArrayList<DocumentInfos> allDocuments, ArrayList<String> collectionId) throws ReadingRSException {
//		RSXMLReader reader = getRSClient(rsLocator);
//		RSXMLIterator iterator = null;
//		ArrayList<DocumentInfos> listOfFailures = new ArrayList<DocumentInfos>();
//		try {
//			iterator = reader.getRSIterator();
//		} catch (Exception e) {
//			throw new ReadingRSException(e);
//		}
//		
//		while (iterator.hasNext()) {
//			ResultElementBase res = iterator.next(ResultElementGeneric.class);
//			try {
//				String report = res.RS_toXML();
//				//Retrieve info about the record:
//				RecordAttribute[] tmp = res.getRecordAttributes("DocID");
//				String sourceId = tmp[0].getAttrValue();
//				
//				
//				// Add also the output collection id
//				tmp = res.getRecordAttributes("CollID");
//				String colId = tmp[0].getAttrValue();
//				collectionId.add(colId);
//				
//				// Get the Status Report
//				boolean success = getStatusReport(report, sourceId);
//				if (!success) {
//					DocumentInfos di = new DocumentInfos();
//					di = findDocumentInfosFromDocumentURI(allDocuments, sourceId);
//					listOfFailures.add(di);
//				}
//				logger.info("The REPORT for the document: " + sourceId + "is:"  + report);
//			} catch (Exception e) {
//				throw new ReadingRSException(e);
//			}
//		}
//		
//		return listOfFailures;
//	}
	
	
//	public static ArrayList<DocumentInfos> getReports(String rsLocator, ArrayList<String> collectionId) throws ReadingRSException {
//		RSXMLReader reader = getRSClient(rsLocator);
//		RSXMLIterator iterator = null;
//		ArrayList<DocumentInfos> listOfFailures = new ArrayList<DocumentInfos>();
//		try {
//			iterator = reader.getRSIterator();
//		} catch (Exception e) {
//			throw new ReadingRSException(e);
//		}
//		
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//		logger.info("ITERATE RESULTSET");
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//		int i = 0; 
//		while (iterator.hasNext()) {
//			i++;
//			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			logger.info("BEGINNING");
//			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			long startTime = System.currentTimeMillis();
//			ResultElementBase res = iterator.next(ResultElementGeneric.class);
//			long endTime = System.currentTimeMillis();
//			
//			long diff = endTime - startTime;
//			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			logger.info("THE DIFFERENCE IS: " + diff + " " + i);
//			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			try {
//				String report = res.RS_toXML();
//				//Retrieve info about the record:
//				RecordAttribute[] tmp = res.getRecordAttributes("DocID");
//				String sourceId = tmp[0].getAttrValue();
//				
//				
//				// Add also the output collection id
//				tmp = res.getRecordAttributes("CollID");
//				String colId = tmp[0].getAttrValue();
//				collectionId.add(colId);
//				
//				// Get the Status Report
//				boolean success = getStatusReport(report, sourceId);
//				if (!success) {
//					DocumentInfos di = new DocumentInfos();
//					di.setPdfURI(sourceId);
//					UUID uuid = UUID.randomUUID();
//					String randomUUIDString = uuid.toString();
//					di.setDocumentId(randomUUIDString);
//					//di = findDocumentInfosFromDocumentURI(allDocuments, sourceId);
//					listOfFailures.add(di);
//				}
//				logger.info("The REPORT for the document: " + sourceId + "is:"  + report);
//			} catch (Exception e) {
//				logger.error("Exception:", e);
//				throw new ReadingRSException(e);
//			}
//		}
//		
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//		logger.info("FINISHED");
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//		
//		return listOfFailures;
//	}
	
	
	private static DocumentInfos findDocumentInfosFromDocumentURI(ArrayList<DocumentInfos> allDocuments, String documentURI) {
		for (int i = 0; i < allDocuments.size(); i++) {
			logger.info("Comparing: " + allDocuments.get(i).getPdfURI() + " with " + documentURI);
			if (allDocuments.get(i).getPdfURI().equals(documentURI)) {
				return allDocuments.get(i);
			}
		}
		return null;
	}
	
	private static boolean getStatusReport(String report, String sourceId) {
		// TODO: could be done with dom parsing..
		logger.info("Getting report for document: " + sourceId);
		if (report.contains("<STATUS>FAILED</STATUS>"))
			return false;
		else
			return true;
	}

//	protected static RSXMLReader getRSClient(String epr) throws ReadingRSException {
//		RSXMLReader client = null;
//
//		try {
//			/* edo tora ta pernoume local ta apotelesmata */
//			client = RSXMLReader.getRSXMLReader(new RSLocator(epr)).makeLocal(new RSResourceLocalType());
//		} catch (Exception e) {
//			throw new ReadingRSException(e);
//		}
//		return client;
//	}
	
	
//	private static DataTransformationServicePortType getDataTransformationServicePortType(String scope) throws Exception {
//		
////		EndpointReference[] dtsURIs = null;
//		String dtsAddress;
//		try {
//			dtsAddress = ServiceUtils.getEprAddressOfService("DataTransformation", "DataTransformationService", ServiceType.SIMPLE.name(), scope);
////			dtsURIs = RIsManager.getInstance().getISCache(GCUBEScope.getScope(scope)).getEPRsFor("DataTransformation", "DataTransformationService", SrvType.SIMPLE.name());
//		} catch (Exception e) {
//			throw new Exception(e);
//		}
//		
////		String dtsAddress;
////		Random random = new Random();
////		dtsAddress = dtsURIs[random.nextInt(dtsURIs.length)].getAddress().toString();
//		DataTransformationServicePortType dts;
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new Address(dtsAddress));
//		DataTransformationServiceAddressingLocator dtsLocator = new DataTransformationServiceAddressingLocator();
//		dts = dtsLocator.getDataTransformationServicePortTypePort(endpoint);
////		try {
////			dts = ServiceContextManager.applySecurity(dts, GCUBEScope.getScope(scope), ApplicationCredentials.getInstance().getCredential(scope));
////		} catch (MalformedScopeExpressionException e) {
////			throw new ServiceEPRRetrievalException(e);
////		} catch (Exception e) {
////			throw new ServiceEPRRetrievalException(e);		}
//		return dts;
//	}
	
	
	
	/**
	 * Transforms a list of PDF documents to text, using OCR Service. It returns a list of the CM URIs of the output documents.
	 * It also copies the generated output to the collection given as a parameter.
	 * @param documents - the list of documents to be transformed
	 * @param outpuCollectionId - the collection to which the output will be inserted
	 * @param session
	 * @return - list of CM URIs of transformed documents
	 * @throws ServiceEPRRetrievalException 
	 * @throws OCRException 
	 */
	public static ArrayList<String> performOCRtoPDF_HTTPInput(ArrayList<DocumentInfos> documents, String outputCollectionId, ASLSession session) throws ServiceEPRRetrievalException, OCRException {
		// Perform OCR
		
		/*ArrayList<String> HocrOutputSSIDs = new ArrayList<String>();
		String ocrEPR = getOCRServiceEPR(session);
		EndpointReferenceType factoryEPR = new EndpointReferenceType();
		try {
			factoryEPR.setAddress(new Address(ocrEPR));
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
			throw new ServiceEPRRetrievalException(e);
		}
		OCRServiceFactoryPortType stub = null;
		try {
			stub = new OCRServiceFactoryServiceAddressingLocator().getOCRServiceFactoryPortTypePort(factoryEPR);
			stub = GCUBERemotePortTypeContext.getProxy(stub, session.getScope());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
			throw new ServiceEPRRetrievalException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
			throw new ServiceEPRRetrievalException(e);
		}
		int numDocs = 0;
		for (int i = 0; i < documents.size(); i++) {
			InputResource input = new InputResource();
			logger.info("Printing...");
			if (documents.get(i) == null)
				logger.info("IT IS NULL " + documents.size());
			logger.info("The document id is: " + documents.get(i).getDocumentId() + " " + documents.get(i).getPdfURI());
			String documentId = documents.get(i).getDocumentId().trim();
			logger.info("Setting as name: " + documentId);
			String dirtyDID = documentId.replaceAll("/", "__");
			logger.info("Setting as name: " + dirtyDID);
			input.setResourceKey(dirtyDID);
			input.setResourceAccess("Reference");
			input.setResourceReference(documents.get(i).getPdfURI().trim());
			Submit sub = new Submit();
			sub.setInputResource(input);
			EndpointReferenceType resourceEPR = null;
				
			try {
				resourceEPR = stub.submit(sub).getEndpointReference();
			} catch (GCUBEUnrecoverableFault e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
				throw new ServiceEPRRetrievalException(e);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
				throw new ServiceEPRRetrievalException(e);
			}
			
			// take the status
			//try {
				OCRServicePortType stub2;
				try {
					stub2 = new OCRServiceAddressingLocator().getOCRServicePortTypePort(resourceEPR);
					stub2 = GCUBERemotePortTypeContext.getProxy(stub2, session.getScope());
				} catch (ServiceException e) {
					logger.error("Exception:", e);
					throw new ServiceEPRRetrievalException(e);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
					throw new ServiceEPRRetrievalException(e);
				}
				//stub2 = GCUBERemotePortTypeContext.getProxy(stub2, session.getScope());
				StatusResponseType status = null;
				// Wait for 2.5 hours
				int timeCounter = 0;
				do {
					try {
						status = stub2.status(new VOID());
						timeCounter += 10000;
						Thread.sleep(10000);
					} catch (GCUBEUnrecoverableFault e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
					}
					
				} while (status.isCompleted() == false && timeCounter < 9000000);
				
				
				if (status.isCompleted()) {
					numDocs++;
					logger.info("Number of documents transformed: " + numDocs);
					HocrOutputSSIDs.add(status.getHocrOutputSSID());
					String error = status.getJoberrSSID();
					logger.info("The JOBERRSSID is: " + error);
				} else {
					logger.info("Omitted transformation of document: " + documents.get(i).getPdfURI());
				}
//			} catch (ServiceException e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception:", e);
//				throw new ServiceEPRRetrievalException(e);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception:", e);
//				throw new ServiceEPRRetrievalException(e);
//			}
		}
		
		
		// Copy every document to the output collection given
		DocumentWriter cmWriter = null;
		DocumentReader cmReader = null;
		GCubeDocument doc = null;
		try {
			boolean first = true;
			cmWriter = new DocumentWriter(outputCollectionId, session.getScope());
			logger.info(String.valueOf(HocrOutputSSIDs.size()));
			for (int i = 0; i < HocrOutputSSIDs.size(); i++) {
				if (HocrOutputSSIDs.get(i) == null)
					logger.info("CRY");
				else
					logger.info("CRY NOT");
				URI theUri = new URI(HocrOutputSSIDs.get(i).trim());
				String oid = URIs.documentID(theUri);
				String colID = URIs.collectionID(theUri);
				if (first) {
					try {
						cmReader = new DocumentReader(colID, session.getScope());
						cmWriter = new DocumentWriter(outputCollectionId, session.getScope());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
						//throw new ContentReaderCreationException(e);
					}
					
					// Read the document from OCR collection
					doc = cmReader.get(oid, document());
					
					GCubeDocument newDoc = new GCubeDocument();
					if (doc.bytestream() != null)
						newDoc.setBytestream(doc.bytestream());
					if (doc.bytestreamURI() != null)
						newDoc.setBytestreamURI(doc.bytestreamURI());
					if (doc.length() != null)
						newDoc.setLenght(doc.length());
					if (doc.mimeType() != null)
						newDoc.setMimeType(doc.mimeType());
					if (doc.name() != null)
						newDoc.setName(doc.name());
					if (doc.type() != null)
						newDoc.setType(doc.type());
					// Write it to output collection
					String myNewDocumentId = cmWriter.add(newDoc);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
			throw new OCRException(e);
		}
		
		return HocrOutputSSIDs;*/
		return new ArrayList<String>();
	}
	
	private static String getOCRServiceEPR(ASLSession session) {
//		EndpointReference[] ocrService = null;
		String ocrService = null;
		logger.info("Looking for an OCRService epr");
		try {
			ocrService = ServiceUtils.getEprAddressOfService("Execution", "OCRService", ServiceType.FACTORY.name(), session.getScope());
//			ocrService = RIsManager.getInstance().getISCache(session.getScope()).getEPRsFor("Execution", "OCRService",SrvType.FACTORY.name());
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		
		if(ocrService==null || ocrService.isEmpty()){
			logger.info("The ocrService epr is NULL");
			return null;
		}
		else{
			logger.info("The ocrService epr is: " + ocrService);
			return ocrService;
		}
		
	}


}
