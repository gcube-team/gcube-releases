//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//
////import org.gcube.common.core.scope.GCUBEScope;
////import org.gcube.common.core.scope.GCUBEScopeManager;
////import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
//
//public class SimpleRSClient {
//
//	public static void main(String[] args) throws Exception {
////		GCUBEScopeManager manager = new GCUBEScopeManagerImpl();
////		manager.setScope(GCUBEScope.getScope(args[0]));
//		
//		/* Creating the RS */
//		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter();
//			
//		fillRS(writer);
//		writer.close();
//		RSLocator rslocator = createLocator(writer);
//		
//		persistRS(rslocator.getLocator());
//	}
//	
//	public static void persistRS(String resultRS){
//		RSBLOBIterator iterator;
//		try{
//			iterator = RSBLOBReader.getRSBLOBReader(new RSLocator(resultRS)).getRSIterator();
//			int cnt=0;
//			while(iterator.hasNext()){
//				ResultElementBLOBGeneric blob = (ResultElementBLOBGeneric)iterator.next(ResultElementBLOBGeneric.class);
//				
//				if(blob!=null){
//					System.out.println("ID: "+(blob.getRecordAttributes(ResultElementBLOBGeneric.RECORD_ID_NAME)[0]).getAttrValue());
//					System.out.println("FromID: "+(blob.getRecordAttributes(ResultElementBLOBGeneric.RECORD_COLLECTION_NAME)[0]).getAttrValue());
//					InputStream istream = blob.getContentOfBLOB();
//					streamToFile(istream, "file"+cnt+".txt");
//				} else {
//					System.out.println("To "+cnt+" blob pou epistrefei o iterator einai null. PROSEKSE kai auto...");
//				}
//				cnt++;
//			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	
//	public static RSLocator createLocator(RSBLOBWriter writer ) throws Exception {
//		RSLocator locator=null; 
//	    locator=writer.getRSLocator(new RSResourceWSRFType("http://dl10.di.uoa.gr:9876/wsrf/services/gcube/searchservice/ResultSet"));
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
//	
//	public static void streamToFile(InputStream instream, String filename) throws Exception{
//		OutputStream out=null;
//		try{
//			out=new FileOutputStream(new File(filename));
//			byte[] buf = new byte[4096];
//			int len;
//			int sum=0;
//			while ((len = instream.read(buf)) >= 0) {
//				sum+=len;
//				out.write(buf, 0, len);
//			}
//			instream.close();
//			instream=null;
//			out.close();
//			out=null;
//			
//		}catch(Exception e){
//			if(instream!=null) instream.close();
//			if(out!=null) out.close();
//			System.out.println("Could not persist stream. Throwing Exception");
//			e.printStackTrace();
//		}
//	}
//}
