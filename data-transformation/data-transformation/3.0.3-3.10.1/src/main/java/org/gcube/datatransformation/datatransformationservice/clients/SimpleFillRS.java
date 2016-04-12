//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.ByteArrayInputStream;
//import java.io.FileNotFoundException;
//
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
//
//public class SimpleFillRS {
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter();
//		
//		fillRS(writer);
//		writer.close();
//		RSLocator rslocator = createLocator(writer);
//		
//		System.out.println(rslocator.getLocator());
//
//	}
//	public static RSLocator createLocator(RSBLOBWriter writer ) throws Exception {
//		RSLocator locator=null; 
//	    locator=writer.getRSLocator(new RSResourceWSRFType("http://dl17.di.uoa.gr:9876/wsrf/services/gcube/searchservice/ResultSet"));
//	    return locator;
//	}
//	
//	public static void fillRS(RSBLOBWriter writer) throws FileNotFoundException, Exception{
//		byte [] barray = new byte[1024];
//		for(int j=0;j<1024;j++)
//			barray[j]='a';
//		
//		for(int i=1;i<6;i++){
//			ByteArrayInputStream instream = new ByteArrayInputStream(barray);
//			ResultElementBLOBGeneric blob = new ResultElementBLOBGeneric(String.valueOf(i), "colid", null, instream);
//			writer.addResults(blob);
//		}
//	}
//}
