//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.Input;
//import org.gcube.datatransformation.datatransformationservice.stubs.Output;
//import org.gcube.datatransformation.datatransformationservice.stubs.Parameter;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformData;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataResponse;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class DriverImagesThumbs {
//	public static void main(String[] args) throws Exception {
//		
//		String dtsEndpoint = null;
//		String scope = null;
//		String objectUrl = null;
//		
//		if (args.length == 0){
//			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//			System.out.print("Scope: ");
//			scope = in.readLine();
//			System.out.print("DTS host: ");
//			String dtshost = in.readLine();
//			System.out.print("DTS port: ");
//			String dtsport = in.readLine();			
//			dtsEndpoint ="http://"+dtshost+":"+dtsport+"/wsrf/services/gcube/datatransformation/DataTransformationService";
//		}else{
//			dtsEndpoint = args[0];
//			scope = args[1];
//			objectUrl = args[2];
//		}
//
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator()
//				.getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope
//				.getScope(scope));
//
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("image/png");
//
//		Parameter tparam1 = new Parameter();
//		tparam1.setName("width");
//		tparam1.setValue("160");
//
//		Parameter tparam2 = new Parameter();
//		tparam2.setName("height");
//		tparam2.setValue("100");
//
//		Parameter tparam3 = new Parameter();
//		tparam3.setName("background");
//		tparam3.setValue("transparent");
//
//		Parameter tparam4 = new Parameter();
//		tparam4.setName("gravity");
//		tparam4.setValue("center");
//
////		Parameter tparam5 = new Parameter();
////		tparam5.setName("define");
////		tparam5.setValue("jpeg:size=320x200");
//		 
//
//		Parameter tparam5 = new Parameter();
//		tparam5.setName("auto-orient");
//		tparam5.setValue("true");
//
//		Parameter tparam6 = new Parameter();
//		tparam6.setName("keep-aspect");
//		tparam6.setValue("true");
//
//		Parameter tparam7 = new Parameter();
//		tparam7.setName("unsharp");
//		tparam7.setValue("0x.5");
//
//		Parameter tparam8 = new Parameter();
//		tparam8.setName("background");
//		tparam8.setValue("transparent");
//
//		Parameter tparam9 = new Parameter();
//		tparam9.setName("extent");
//		tparam9.setValue("160x100");
//		
//		
//		Parameter[] tParameters = { tparam1, tparam2, tparam3, tparam4 , tparam5,
//				tparam6, tparam7, tparam8, tparam9};
//		targetContentType.setParameters(tParameters);
//
//		/* INPUT */
//		TransformData request = new TransformData();
//		Input input = new Input();
//		input.setInputType("URIList");
//		input.setInputValue(args[2]);
//		request.setInput(input);
//
//		/* OUTPUT */
//		Output output = new Output();
//		output.setOutputType("FTP");
//		output.setOutputValue("dl07.di.uoa.gr");
//		Parameter param1 = new Parameter("username", "dimitris");
//		Parameter param2 = new Parameter("password", args[3]);
//		Parameter param3 = new Parameter("port", "21");
//		Parameter param4 = new Parameter("directory", "/home/dimitris/sink");
//		output.setOutputparameters(new Parameter[] { param1, param2, param3,
//				param4 });
//		request.setOutput(output);
//
//		TransformData td = new TransformData(true, input, output,
//				targetContentType);
//		TransformDataResponse tdr = dts.transformData(td);
//
//		System.out.println(tdr.getOutput());
//		readRS(tdr.getReportEPR());
//
//	}
//
//	public static void readRS(String locator) throws Exception {
//		try {
//			RSLocator rsLocator = new RSLocator(locator);
//
//			System.out.println(rsLocator.getRSResourceType());
//			System.out.println(rsLocator.getURI());
//			RSXMLReader reader = RSXMLReader.getRSXMLReader(rsLocator)
//					.makeLocalPatiently(new RSResourceLocalType(), 3600000);
//			System.out.println("done make local");
//			int count = 0;
//			int recs = 0;
//			while (true) {
//				count += 1;
//			//	reader.getNextPart(3600000);
//				recs += reader.getNumberOfResults();
//				int partcount = reader.getNumberOfResults();
//				int partsize = 0;
//				System.out.println(count);
//				for (int i = 0; i < partcount; i += 1) {
//					int recordcount = reader.getResults(
//							ResultElementGeneric.class, i).RS_toXML()
//							.getBytes().length;
//					System.out.println(reader.getResults(
//							ResultElementGeneric.class, i).RS_toXML());
//					partsize += recordcount;
//					// System.out.println(recordcount);
//					// System.out.println(partsize);
//				}
//				// System.out.println(partsize);
//				// System.out.println("count " +
//				// reader.executeQueryOnDocument("/Body").getBytes().length);
//				// System.out.println("----------------------------------------------------------");
//				if (reader.isLast())
//					break;
//				reader.getNextPart();
//			}
//			System.out.println("parts " + count);
//			System.out.println("records " + recs);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
