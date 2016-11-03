//package org.gcube.datatransformation.test;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.gcube.datatransformation.DataTransformationClient;
//import org.gcube.datatransformation.client.library.beans.Types.ContentType;
//import org.gcube.datatransformation.client.library.beans.Types.FindApplicableTransformationUnits;
//import org.gcube.datatransformation.client.library.beans.Types.FindAvailableTargetContentTypes;
//import org.gcube.datatransformation.client.library.beans.Types.Input;
//import org.gcube.datatransformation.client.library.beans.Types.Output;
//import org.gcube.datatransformation.client.library.beans.Types.Parameter;
//import org.gcube.datatransformation.client.library.beans.Types.QueryTransformationPrograms;
//import org.gcube.datatransformation.client.library.beans.Types.TransformData;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationProgram;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
//import org.gcube.datatransformation.client.library.exceptions.DTSClientException;
//import org.gcube.datatransformation.client.library.exceptions.DTSException;
//import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
//import org.gcube.datatransformation.datatransformationlibrary.utils.JSONConverter;
//
//import com.google.common.collect.Maps;
//import com.google.gson.Gson;
//
//
//public class DTSClient {
//	public static void main(String[] args) throws Exception {
////		testStatistics();
////		testTransformData();
////		testTransformDataWithTransformationProgram();
////		String uri = testTransformDataWithTransformationUnit();
////		System.out.println(uri);
////		testFindApplicableTransformationUnits();
////		testFindAvailableTargetContentTypesResponse();
////		testQueryTransformationPrograms();
//		transformSRUSource();
////		testRSReader(uri);
//		Thread.sleep(1000);
//	}
//
//	private static void testRSReader(String string) throws URISyntaxException, Exception {
//		GRS2Printer.print(new URI(string), 80);
//	}
//
//	static DataTransformationClient dtscli;
//
//	public DTSClient() throws DTSClientException {
//		dtscli = new DataTransformationClient();
//		dtscli.setScope("/gcube/devNext");
////		dtscli.setScope("/d4science.research-infrastructures.eu/FARM");
////		dtscli.setEndpoint("http://meteora.di.uoa.gr:8080/data-transformation-service-3.0.1-SNAPSHOT");
//		dtscli.setEndpoint("http://meteora.di.uoa.gr:8080/data-transformation-service");
////		dtscli.randomClient();
//	}
//
//	public static void testStatistics() throws Exception {
//		System.out.println(new DTSClient().dtscli.statistics());
//	}
//
//	public static void testTransformData() throws Exception {
//		TransformData request = new TransformData();
//		request.input = new Input();
//		request.input.inputType = "FTP";
//		request.input.inputValue = "meteora.di.uoa.gr";
//
//		Parameter inparam1 = new Parameter("directory", "testArea/src");
//		Parameter inparam2 = new Parameter("username", "");
//		Parameter inparam3 = new Parameter("password", "");
//		request.input.inputparameters = Arrays.asList(inparam1, inparam2, inparam3);
//		
//		request.output = new Output();
//		request.output.outputType = "FTP";
//		request.output.outputValue = "meteora.di.uoa.gr";
//
//		Parameter outparam1 = new Parameter("directory", "testArea/sink");
//		Parameter outparam2 = new Parameter("username", "");
//		Parameter outparam3 = new Parameter("password", "");
//		request.output.outputparameters = Arrays.asList(outparam1, outparam2, outparam3);
//		
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "image/png";
//
//		request.createReport = false;
//		
//		System.out.println(new Gson().toJson(new DTSClient().dtscli.transformData(request)));
//	}
//	
//	public static void testTransformDataWithTransformationProgram() throws Exception {
//		TransformDataWithTransformationProgram request = new TransformDataWithTransformationProgram();
//		request.input = new Input();
//		request.input.inputType = "FTP";
//		request.input.inputValue = "meteora.di.uoa.gr";
//
//		Parameter inparam1 = new Parameter("directory", "testArea/src");
//		Parameter inparam2 = new Parameter("username", "");
//		Parameter inparam3 = new Parameter("password", "");
//		request.input.inputparameters = Arrays.asList(inparam1, inparam2, inparam3);
//		
//		request.tpID = "11053d30-d520-11df-b1d3-898875d74937";
//		
//		request.output = new Output();
//		request.output.outputType = "FTP";
//		request.output.outputValue = "meteora.di.uoa.gr";
//
//		Parameter outparam1 = new Parameter("directory", "testArea/sink");
//		Parameter outparam2 = new Parameter("username", "");
//		Parameter outparam3 = new Parameter("password", "");
//		request.output.outputparameters = Arrays.asList(outparam1, outparam2, outparam3);
//		
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "image/png";
//
//		request.createReport = false;
//		
//		System.out.println(new Gson().toJson(new DTSClient().dtscli.transformDataWithTransformationProgram(request)));
//	}
//	
//	public static String testTransformDataWithTransformationUnit() throws Exception {
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		request.tpID = "$FtsRowset_Transformer";
//		request.transformationUnitID = "6";
//
//		/* INPUT */
//		Input input = new Input();
////		input.inputType = "HTTPDataSource";
////		input.inputValue = "http://meteora.di.uoa.gr/trees10.xml";
//		input.inputType = "TMDataSource";
//		input.inputValue = "55b15c13-0ef7-4d7d-90f3-4e993c664b92"; // 82 elements
////		input.inputValue = "e5024936-04ba-4deb-be25-0600ffb0d7eb"; // 0 items
//		request.inputs = Arrays.asList(input);
//
//		/* OUTPUT */
//		request.output = new Output();
//		request.output.outputType = "RS2";
//
//		/* TARGET CONTENT TYPE */
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "text/xml";
//		Parameter param = new Parameter("schemaURI", "http://ftrowset.xsd");
//		
//		request.targetContentType.contentTypeParameters = Arrays.asList(param);
//
//		/* PROGRAM PARAMETERS */
//		Parameter xsltParameter1 = new Parameter("xslt:1", "$BrokerXSLT_oai_dc_anylanguage_to_ftRowset_anylanguage");
//		Parameter xsltParameter4 = new Parameter("finalftsxslt", "$BrokerXSLT_wrapperFT");
//		
//		Parameter indexTypeParameter = new Parameter("indexType", "ft_1.0");
//
//		request.tProgramUnboundParameters = Arrays.asList(xsltParameter1, xsltParameter4, indexTypeParameter);
//
//		request.filterSources = false;
//		request.createReport = false;
//
//		TransformDataWithTransformationUnitResponse resp = new DTSClient().dtscli.transformDataWithTransformationUnit(request);
//		System.out.println(new Gson().toJson(resp));
//		
//		return resp.output;
//	}
//	
//	public static void testFindApplicableTransformationUnits() throws Exception {
//		FindApplicableTransformationUnits request = new FindApplicableTransformationUnits();
//		
//		request.sourceContentType = new ContentType();
//		request.sourceContentType.mimeType = "image/gif";
//
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "image/jpeg";
//
//		request.createAndPublishCompositeTP = false;
//
//		System.out.println(new Gson().toJson(new DTSClient().dtscli.findApplicableTransformationUnits(request)));
//	}
//
//	public static void testFindAvailableTargetContentTypesResponse() throws Exception {
//		FindAvailableTargetContentTypes request = new FindAvailableTargetContentTypes();
//		
//		request.sourceContentType = new ContentType();
//		request.sourceContentType.mimeType = "image/jpeg";
//
//		System.out.println(new Gson().toJson(new DTSClient().dtscli.findAvailableTargetContent(request)));
//	}
////
//	public static void testQueryTransformationPrograms() throws Exception {
//		QueryTransformationPrograms request = new QueryTransformationPrograms();
//		
//		request.queryTransformationPrograms = "GET DESCRIPTION WHERE TRANSFORMATIONPROGRAMID=11053d30-d520-11df-b1d3-898875d74937";
//		
//		System.out.println(new DTSClient().dtscli.queryTransformationPrograms(request).queryTransformationProgramsResponse);
//
//	}
//
////	// HTTP POST request
////	private static void sendPost(String url, Map<String, String> postParams) throws Exception {
////		HttpClient client = new DefaultHttpClient();
////		HttpPost post = new HttpPost(url);
//// 
////		// add header
////		post.setHeader("gcube-scope", "/gcube/devNext");
//// 
////		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
////		for(Entry<String, String> asd : postParams.entrySet())
////			urlParameters.add(new BasicNameValuePair(asd.getKey(), asd.getValue()));
//// 
////		post.setEntity(new UrlEncodedFormEntity(urlParameters));
//// 
////		HttpResponse response = client.execute(post);
////		System.out.println("\nSending 'POST' request to URL : " + url);
////		System.out.println("Post parameters : " + postParams);
////		System.out.println("Response Code : " + 
////                                    response.getStatusLine().getStatusCode());
//// 
////		BufferedReader rd = new BufferedReader(
////                        new InputStreamReader(response.getEntity().getContent()));
//// 
////		StringBuffer result = new StringBuffer();
////		String line = "";
////		while ((line = rd.readLine()) != null) {
////			result.append(line + "\n");
////		}
//// 
////		System.out.println(result.toString());
////	}
////	
////	private static void sendGet(String url) throws Exception {
////		HttpClient client = new DefaultHttpClient();
////		HttpGet httpGet = new HttpGet(url); // create new httpGet object
////
////		httpGet.setHeader("gcube-scope", "/gcube/devNext");
////
////        HttpResponse response = client.execute(httpGet); // execute httpGet
////		
////		System.out.println("\nSending 'GET' request to URL : " + url);
////		StatusLine statusLine = response.getStatusLine();
////		
////		System.out.println("Response Code : " + statusLine.getStatusCode());
////		StringBuffer body = new StringBuffer();
////
////		int statusCode = statusLine.getStatusCode();
////        if (statusCode == HttpStatus.SC_OK) {
////            // System.out.println(statusLine);
////            body.append(statusLine + "\n");
////            HttpEntity e = response.getEntity();
////            String entity = EntityUtils.toString(e);
////            body.append(entity);
////        } else {
////            body.append(statusLine + "\n");
////            // System.out.println(statusLine);
////        }
////
////		// print result
////		System.out.println(body.toString());
////
////	}
//	
//	private static String transformSRUSource() throws Exception {
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		request.tpID = "$XSLT_Transformer";
//		request.transformationUnitID = "0";
//
//		/* INPUT */
//		Input input = new Input();
//		input.inputType = "SRUDataSource";
//		input.inputValue = "http://www.nla.gov.au/apps/srw/search/peopleaustralia?operation=searchRetrieve&query=((((((contributor%20=%20cacikumar)%20or%20(title%20=%20cacikumar)))%20or%20(creator%20=%20cacikumar)))%20or%20(((((contributor%20=%20fish)%20or%20(title%20=%20fish)))%20or%20(creator%20=%20fish))))&maximumRecords=10&recordSchema=info:srw/schema/1/dc-v1.1&version=1.1"; //
//		request.inputs = Arrays.asList(input);
//
//		/* OUTPUT */
//		request.output = new Output();
//		request.output.outputType = "RS2";
//
//		/* TARGET CONTENT TYPE */
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "json/application";
//
//		/* PROGRAM PARAMETERS */
//		Parameter xsltParameter1 = new Parameter("xslt:1", "$BrokerXSLT_FlattenXML");
//		Parameter xsltParameter2 = new Parameter("finalftsxslt", "$BrokerXSLT_XML_to_JSON");
//
//		Parameter xsltParameter3 = new Parameter("delimiter", " &&& ");
//
//		request.tProgramUnboundParameters = Arrays.asList(xsltParameter1, xsltParameter2, xsltParameter3);
//
//		request.filterSources = false;
//		request.createReport = false;
//		
//		DTSClient dtsclient = new DTSClient();
//		String resp = dtsclient.dtscli.transformDataWithTransformationUnit(request, true, true);
//		
//		System.out.println(resp);
//		List<Map<String, String>> maps = dtsclient.dtscli.getMapFromResponse(resp);
//		for (Map<String, String> map : maps) {
//			for (Entry<String, String> e : map.entrySet()) {
//				System.out.println(e.getKey());
//				System.out.println(e.getValue());
//				Map<String, String> imap = JSONConverter.fromJSON(e.getValue(), new HashMap<String, String>().getClass());
//				System.out.println(imap);
//			}
//		}
//		
//		return resp;
//	}
//}
