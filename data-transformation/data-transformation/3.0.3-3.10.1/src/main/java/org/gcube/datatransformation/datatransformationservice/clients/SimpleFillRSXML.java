//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.FileNotFoundException;
//
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;
//
//public class SimpleFillRSXML {
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		RSXMLWriter writer = RSXMLWriter.getRSXMLWriter();
//		
//		fillRS(writer);
//		writer.close();
//		RSLocator rslocator = createLocator(writer);
//		
//		System.out.println(rslocator.getLocator());
//
//	}
//	
//	public static RSLocator createLocator(RSXMLWriter writer ) throws Exception {
//		RSLocator locator=null; 
//	    locator=writer.getRSLocator(new RSResourceWSRFType("http://dl10.di.uoa.gr:9876/wsrf/services/gcube/searchservice/ResultSet"));
//	    return locator;
//	}
//	
//	public static void fillRS(RSXMLWriter writer) throws FileNotFoundException, Exception{
//		
//		for(int i=1;i<6;i++){
//			ResultElementGeneric blob = new ResultElementGeneric(String.valueOf(i), "colid", "1", "<payload>aaaaaaaaaaaaaaa</payload>");
//			writer.addResults(blob);
//		}
//	}
//
//}
