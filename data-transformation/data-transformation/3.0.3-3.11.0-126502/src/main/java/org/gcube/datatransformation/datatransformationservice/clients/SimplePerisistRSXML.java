//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBGeneric;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//
//public class SimplePerisistRSXML {
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		RSLocator rslocator = new RSLocator(
//				"<ns1:ResultSetResourceReference xsi:type=\"ns2:EndpointReferenceType\" xmlns:ns1=\"http://gcube.org/namespaces/searchservice/ResultSetService\" " +
//				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ns2=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\">"
//						+ "<ns2:Address xsi:type=\"ns2:AttributedURI\">http://dl17.di.uoa.gr:9876/wsrf/services/gcube/searchservice/ResultSet</ns2:Address>"
//						+ "<ns2:ReferenceProperties xsi:type=\"ns2:ReferencePropertiesType\">"
//						+ "<ns1:ResourceKey>82bbd610-b6ef-11dd-a38e-c91add5d2556</ns1:ResourceKey>"
//						+ "</ns2:ReferenceProperties>"
//						+ "<ns2:ReferenceParameters xsi:type=\"ns2:ReferenceParametersType\"/>"
//						+ "</ns1:ResultSetResourceReference>", GCUBEScope.getScope("/gcube/devsec"));
//		persistRS(rslocator.getLocator());
//
//	}
//	public static void persistRS(String resultRS){
//		RSXMLIterator iterator;
//		
//		try{
//			iterator = RSXMLReader.getRSXMLReader(new RSLocator(resultRS)).getRSIterator();
//			int cnt=0;
//			while(iterator.hasNext()){
//				ResultElementGeneric element = (ResultElementGeneric)iterator.next(ResultElementGeneric.class);
//				
//				if(element!=null){
//					System.out.println("ID: "+(element.getRecordAttributes(ResultElementBLOBGeneric.RECORD_ID_NAME)[0]).getAttrValue());
//					System.out.println("FromID: "+(element.getRecordAttributes(ResultElementBLOBGeneric.RECORD_COLLECTION_NAME)[0]).getAttrValue());
//					System.out.println("Payload: "+element.getPayload());
//				} else {
//					System.out.println("To "+cnt+" blob pou epistrefei o iterator einai null. PROSEKSE kai auto...");
//				}
//				cnt++;
//			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//}
