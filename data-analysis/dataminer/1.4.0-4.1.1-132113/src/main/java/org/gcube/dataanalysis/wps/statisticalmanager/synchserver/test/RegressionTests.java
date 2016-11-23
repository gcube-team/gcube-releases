package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.capabilities.GetCapabilitiesChecker;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class RegressionTests {

	public static boolean checkHttpPage(String httplink, String page){
		if (page.contains("ows:ExceptionText")||page.contains("Exception")) {
			System.out.println("Reading Link: " + httplink);
			System.out.println("ERROR:\n" + page);
			return false;
		}
		return true;
	}
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	public static String prepareURL(String executionURL) throws Exception{
		
		String firstPart = executionURL.substring(0,executionURL.indexOf("DataInputs=")+11);
		System.out.println("Execution URL:"+firstPart);
		String secondPart = URLEncoder.encode(executionURL.substring(executionURL.indexOf("DataInputs=")+11),"UTF-8");
		System.out.println("Parameters: "+secondPart);
		executionURL=firstPart+secondPart;
		return executionURL;
	}
	
	public static void callHttps(String httpURLFile) throws Exception{
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(ch.qos.logback.classic.Level.OFF);
		
		BufferedReader br = new BufferedReader(new FileReader(new File(httpURLFile))); 
		String line = br. readLine();
		int counter = 0;
		Date d0 = new Date(System.currentTimeMillis());
		LinkedHashMap<String, String> executionTimes = new LinkedHashMap<String, String>();
		while (line!=null){
			boolean check = true;
			if (line.contains(" - ") && !line.startsWith("#")){
				Date d00 = new Date(System.currentTimeMillis());
				String algorithmName = line.substring(line.indexOf("-")+1).trim();
				String detailsURL = br.readLine();
				if (!detailsURL.startsWith("http://"))
					detailsURL="http://"+detailsURL;
				System.out.println("************************************************************");
				System.out.println("TESTING ALGORITHM : "+algorithmName);
				System.out.println("************************************************************");
				
				String executionURL = br.readLine();
				String firstPart = executionURL.substring(0,executionURL.indexOf("DataInputs=")+11);
				System.out.println("Execution URL:"+firstPart);
				String secondPart = URLEncoder.encode(executionURL.substring(executionURL.indexOf("DataInputs=")+11),"UTF-8");
				System.out.println("Parameters: "+secondPart);
				executionURL=firstPart+secondPart;
				
				
				System.out.println("CHECKING DEFINITION "+algorithmName+" : "+detailsURL);
				String pageCheck = GetCapabilitiesChecker.readPageNoHttpClient(new URL(detailsURL));
				check = checkHttpPage(detailsURL, pageCheck);
				System.out.println("DEFINITION CHECK "+check);
				if (!check)
					break;
				System.out.println("EXECUTING "+algorithmName+" : "+executionURL);
				executionURL = executionURL.replace("test12345.nc","test"+UUID.randomUUID()+".nc");
				pageCheck = GetCapabilitiesChecker.readPageNoHttpClient(new URL(executionURL));
				System.out.println("EXECUTION RESULT "+pageCheck);
				check = checkHttpPage(executionURL, pageCheck);
				System.out.println("EXECUTION CHECK "+check);
				if (!check)
					break;
				Date d11 = new Date(System.currentTimeMillis());
				System.out.println("EXECUTION TIME "+algorithmName+" : "+getDateDiff(d00, d11, TimeUnit.MILLISECONDS)+" s");
				executionTimes.put(algorithmName, ""+getDateDiff(d00, d11, TimeUnit.MILLISECONDS));
				System.out.println("-------------------------------------------------------------\n");
				counter ++;	
			}
			if (!check){
				System.out.println("EXECUTION FAILURE! - BREAK -");
				break;
			}
			line=br.readLine();
			
		}
		
		Date d1 = new Date(System.currentTimeMillis());
		System.out.println("CHECKED "+counter+" PAGES in "+getDateDiff(d0, d1, TimeUnit.MINUTES)+" minutes "+" ("+getDateDiff(d0, d1, TimeUnit.SECONDS)+" s)");
		System.out.println("EXECUTION TIMES SUMMARY:");
		for (String key:executionTimes.keySet()){
			String time = executionTimes.get(key);
			System.out.println(key+","+time+" s");
		} 
		
		br.close();
	}

	@Test
	public void testfewRemoteAlgorithms() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevFew.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testStockRemoteAlgorithms() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevStock.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testRemoteAlgorithms() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevToken.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testSpecificAlgorithm() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevSpAlgorithm.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testLatestRelease() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevTokenV2.0.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testLatestReleaseDevVRE() throws Exception{
		String algorithmsfile = "C:/Users/GP/Desktop/WorkFolder/WPS/TestDevVRETokenV2.0.txt";
		callHttps(algorithmsfile);
	}
	
	@Test
	public void testLoadBalancingDevsec() throws Exception{
		String algorithmsfile = "TestDevTokenLoadBalance.txt";
		callHttps(algorithmsfile);
	}
	
		@Test
	public void testLoadBalancingProduction() throws Exception{
		String algorithmsfile = "TestTokenLoadBalance.txt";
		callHttps(algorithmsfile);
	}
	
		@Test
	public void testPreprod() throws Exception{
			String algorithmsfile = "Test-dataminer-pre.txt";
			callHttps(algorithmsfile);
		}
		
		@Test
	public void testPreprod2() throws Exception{
			String algorithmsfile = "TestPreProd2Token.txt";
			callHttps(algorithmsfile);
		}
		
		@Test
		public void testProd2() throws Exception{
				String algorithmsfile = "TestProd2Token.txt";
				callHttps(algorithmsfile);
			}
		
		@Test
		public void testProd1() throws Exception{
				String algorithmsfile = "TestProd1Token.txt";
				callHttps(algorithmsfile);
			}
	@Test
	public void testUsernames() throws Exception{
		String testURL = "http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token=d7a4076c-e8c1-42fe-81e0-bdecb1e8074a&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL&DataInputs=Matcher_1=LEVENSHTEIN;Matcher_4=NONE;Matcher_5=NONE;Matcher_2=NONE;Matcher_3=NONE;Threshold_1=0.6;Threshold_2=0.6;Accuracy_vs_Speed=MAX_ACCURACY;MaxResults_2=10;MaxResults_1=10;Threshold_3=0.4;Taxa_Authority_File=FISHBASE;Parser_Name=SIMPLE;MaxResults_4=0;Threshold_4=0;MaxResults_3=0;MaxResults_5=0;Threshold_5=0;Use_Stemmed_Genus_and_Species=false;Activate_Preparsing_Processing=true;SpeciesAuthorName=Gadus morhua (Linnaeus, 1758)";
		
		int max = 100;
		for (int i=1;i<=max;i++){
			System.out.println("EXECUTING "+i);
			String pageCheck = GetCapabilitiesChecker.readPageNoHttpClient(new URL(prepareURL(testURL)));
			System.out.println("EXECUTION RESULT "+pageCheck);
		}
		
	}
	
	@Test
	public void testHTTPHeaderCall() throws Exception{
		String testURL = "http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL&DataInputs=Matcher_1=LEVENSHTEIN;Matcher_4=NONE;Matcher_5=NONE;Matcher_2=NONE;Matcher_3=NONE;Threshold_1=0.6;Threshold_2=0.6;Accuracy_vs_Speed=MAX_ACCURACY;MaxResults_2=10;MaxResults_1=10;Threshold_3=0.4;Taxa_Authority_File=FISHBASE;Parser_Name=SIMPLE;MaxResults_4=0;Threshold_4=0;MaxResults_3=0;MaxResults_5=0;Threshold_5=0;Use_Stemmed_Genus_and_Species=false;Activate_Preparsing_Processing=true;SpeciesAuthorName=Gadus morhua (Linnaeus, 1758)";
		System.out.println("EXECUTING");
 		String page = GetCapabilitiesChecker.readPageHTTPHeader(new URL(prepareURL(testURL)),"d7a4076c-e8c1-42fe-81e0-bdecb1e8074a");		
 		System.out.println("EXECUTION RESULT "+page);
	}
	
	@Test
	public void testHTTPCall() throws Exception{
		//prepare the URL
		String testURL = "http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL";
		//add URL encoded parameters
		String parameters = "&DataInputs="+URLEncoder.encode("Matcher_1=LEVENSHTEIN;Matcher_4=NONE;Matcher_5=NONE;Matcher_2=NONE;Matcher_3=NONE;Threshold_1=0.6;Threshold_2=0.6;Accuracy_vs_Speed=MAX_ACCURACY;MaxResults_2=10;MaxResults_1=10;Threshold_3=0.4;Taxa_Authority_File=FISHBASE;Parser_Name=SIMPLE;MaxResults_4=0;Threshold_4=0;MaxResults_3=0;MaxResults_5=0;Threshold_5=0;Use_Stemmed_Genus_and_Species=false;Activate_Preparsing_Processing=true;SpeciesAuthorName=Gadus morhua (Linnaeus, 1758)","UTF-8");
		//build the URL
		testURL+=parameters;
		URL url = new URL(testURL);
		System.out.println("EXECUTING "+testURL);
		
		//build connection
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(true);
		conn.setConnectTimeout(25*60000);
		conn.setReadTimeout(25*60000);
		//set the token
		conn.setRequestProperty("gcube-token", "d7a4076c-e8c1-42fe-81e0-bdecb1e8074a");
		
		//read the output document
		BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
		String inputLine;
		StringBuffer pageBuffer = new StringBuffer();
		while ((inputLine = dis.readLine()) != null) {	
			pageBuffer.append(inputLine + "\r\n");
		}
		String page = pageBuffer.toString();

		//close the stream
		conn.getInputStream().close();
		//print the output
		System.out.println("EXECUTION RESULT "+page);
	}
	
	
	
}
