package org.gcube.application.framework.contentmanagement.datatransformation.util;


import java.util.ArrayList;

import org.gcube.application.framework.contentmanagement.datatransformation.util.DataTransformationUtils;
import org.gcube.application.framework.contentmanagement.exceptions.OCRException;
import org.gcube.application.framework.contentmanagement.exceptions.ReadingRSException;
import org.gcube.application.framework.contentmanagement.exceptions.ServiceEPRRetrievalException;
import org.gcube.application.framework.contentmanagement.exceptions.TransformationException;
import org.gcube.application.framework.contentmanagement.util.DocumentInfos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDTSClient {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(TestDTSClient.class);

	/**
	 * @param args
	 * @throws OCRException 
	 * @throws ServiceEPRRetrievalException 
	 */
	public static void main(String[] args) {
//		try {
//			ArrayList<String>colid = new ArrayList<String>();
//			colid.add("");
//			String resultsEpr = DataTransformationUtils.transformPDFDocumentsToText("dl10.di.uoa.gr:8181/documentLocations.txt", colid, "testpdfcol", "gcube/devNext");
//			ArrayList<String>wrong = new ArrayList<String>();
//			ArrayList<DocumentInfos> failures = DataTransformationUtils.getReports(resultsEpr, wrong);
//			performOCRtoPDF_HTTPInput(failures, colid.get(0), "/gcube/devNext");
//		} catch (ServiceEPRRetrievalException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (TransformationException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		catch (ReadingRSException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (OCRException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}

	}
	
	public static ArrayList<String> performOCRtoPDF_HTTPInput(ArrayList<DocumentInfos> documents, String outputCollectionId, String scope) throws ServiceEPRRetrievalException, OCRException {
		// Perform OCR
		
		/*ArrayList<String> HocrOutputSSIDs = new ArrayList<String>();
		String ocrEPR = getOCRServiceEPR("/gcube/devNext");
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
			stub = GCUBERemotePortTypeContext.getProxy(stub, GCUBEScope.getScope(scope));
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
			input.setResourceKey(documents.get(i).getDocumentId().trim());
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
					stub2 = GCUBERemotePortTypeContext.getProxy(stub2, GCUBEScope.getScope(scope));
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
				do {
					try {
						status = stub2.status(new VOID());
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
					
				} while (status.isCompleted() == false);
				
				numDocs++;
				logger.info("Number of documents transformed: " + numDocs);
				HocrOutputSSIDs.add(status.getHocrOutputSSID());
				String error = status.getJoberrSSID();
				logger.info("The JOBERRSSID is: " + error);
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
			cmWriter = new DocumentWriter(outputCollectionId, GCUBEScope.getScope(scope));
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
						cmReader = new DocumentReader(colID, GCUBEScope.getScope(scope));
						cmWriter = new DocumentWriter(outputCollectionId, GCUBEScope.getScope(scope));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
						throw new ContentReaderCreationException(e);
					}
					
					// Read the document from OCR collection
					doc = cmReader.get(oid, document());
					
					// Write it to output collection
					String myNewDocumentId = cmWriter.add(doc);
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

/*
private static String getOCRServiceEPR(String scope) {
	EndpointReference[] ocrService = null;
	logger.info("Looking for an OCRService epr");
	try {
		ocrService = RIsManager.getInstance().getISCache(GCUBEScope.getScope(scope)).getEPRsFor("Execution", "OCRService",SrvType.FACTORY.name());
	} catch (Exception e) {
		logger.error("Exception:", e);
	}
	
	
//	if (ocrService != null && ocrService.length > 0) {
//		logger.info("The ocrService epr is: " + ocrService[0]);
//		return ocrService[0].getAddress().toString();
//	}
	
	logger.info("The ocrService epr is NULL");
	
	// TODO: Change bellow!!
	//return null;
	return "http://dl20.di.uoa.gr:8485/wsrf/services/gcube/execution/ocrservice/OCRServiceFactory";
}
*/
}
