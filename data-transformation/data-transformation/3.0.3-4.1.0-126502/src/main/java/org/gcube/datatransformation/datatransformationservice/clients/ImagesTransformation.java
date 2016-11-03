//package org.gcube.datatransformation.datatransformationservice.clients;
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
//public class ImagesTransformation {
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = args[0];
//		String scope = args[1];
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
//		Parameter[] tParameters = { tparam1, tparam2 };
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
