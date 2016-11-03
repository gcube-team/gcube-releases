//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
//
//public class SimplePersistRS {
//	public static void main(String[] args) throws Exception {
//		RSLocator rslocator = new RSLocator(
//				"<ns1:ResultSetResourceReference xsi:type=\"ns2:EndpointReferenceType\" xmlns:ns1=\"http://gcube.org/namespaces/searchservice/ResultSetService\" " +
//				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ns2=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\">"
//						+ "<ns2:Address xsi:type=\"ns2:AttributedURI\">http://dl10.di.uoa.gr:9876/wsrf/services/gcube/searchservice/ResultSet</ns2:Address>"
//						+ "<ns2:ReferenceProperties xsi:type=\"ns2:ReferencePropertiesType\">"
//						+ "<ns1:ResourceKey>cbae9520-87f0-11dd-a926-b919a00e6e16</ns1:ResourceKey>"
//						+ "</ns2:ReferenceProperties>"
//						+ "<ns2:ReferenceParameters xsi:type=\"ns2:ReferenceParametersType\"/>"
//						+ "</ns1:ResultSetResourceReference>");
//		persistRS(rslocator.getLocator());
//	}
//	public static void persistRS(String resultRS){
//		RSBLOBIterator iterator;
//		
//		try{
//			iterator = RSBLOBReader.getRSBLOBReader(new RSLocator(resultRS)).makeLocal(new RSResourceLocalType()).getRSIterator();
////			RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(new RSLocator(resultRS));
////			reader.makeLocal(new w)
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
