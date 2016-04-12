package org.gcube.datatransformation.adaptors.discoverer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gcube.datatransformation.adaptors.common.db.discover.DBPropsDiscoverer;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.discoverer.AdaptorsDiscoverer.Info;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.w3c.dom.Document;

public class ManagerHelper {

	private String scope;
	private DBPropsDiscoverer dbDiscoverer;
	
	public ManagerHelper(String scope){
		this.scope = scope;
		dbDiscoverer = new DBPropsDiscoverer(new RIDiscovererISimpl(), new ResourceHarvester<DBProps>());
	}
	
	public Set<String> getDBRunningInstancesEps(){
		return dbDiscoverer.discoverDBServiceRunningInstances(scope);
	}
	
	public List<Info> getAllDBProps(String endpoint) throws ClientProtocolException, IOException{
		AdaptorsDiscoverer disc = new AdaptorsDiscoverer(scope);
		return disc.queryForDBProps(endpoint);
	}
	
	public String getDBPropsXML(String endpoint, String sourcename, String propsname) throws IOException{
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(endpoint + "/GetResource?sourcename="+sourcename+"&propsname="+propsname);
		httpget.addHeader("gcube-scope", scope);
		HttpResponse response = httpclient.execute(httpget);
		//if it has failed for any reason, return empty
		if(response.getStatusLine().getStatusCode() != 200)
			return "";
		return EntityUtils.toString(response.getEntity());
		
	}
	
	
	public int setDBPropsXML(String endpoint, String dbPropsXML) throws Exception {
		 
		URL obj = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("gcube-scope", scope);
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes("/ReplaceDBHarvesterConfig?dbPropsXML="+dbPropsXML);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) 
			response.append(inputLine);
		in.close();
 
		//print result
		System.out.println(response.toString());
				
		return responseCode; //normally this should be 201, for "successfully done"
 
	}
	
	
//	public static void main (String [] args) throws Exception{
//		ManagerHelper mh = new ManagerHelper("/gcube/devNext");
////		Set<String> runningInstances = mh.getDBRunningInstancesEps();
////		System.out.println(runningInstances);
////		for(String runInst : runningInstances){
////			List<Info> dbPropsSimple = mh.getAllDBProps(runInst);
////			for(Info inf : dbPropsSimple){
////				System.out.println(inf.getPropsname()+"  "+inf.getSourcename());
////				System.out.println(mh.getDBPropsXML(runInst, inf.getSourcename(), inf.getPropsname()));
////			}
////		}
//		
//		
//		String endpoint = "http://dionysus.di.uoa.gr:8080/aslHarvestersHttpDB";
//		String dbPropsXML = "<DBProps><sourcetype>RelationalDB</sourcetype><sourcename>DionysusDB</sourcename><propsname>CountriesTree132</propsname><table><name>country</name><sql>select * from country</sql></table><table><name>city</name><sql>select * from city</sql></table><table><name>countrylanguage</name><sql>select * from countrylanguage</sql></table><table><name>zipcodes</name><sql>select * from zipcodes</sql></table><edge><parent>country</parent><pkeys>code</pkeys><child>city</child><ckeys>countrycode</ckeys></edge><edge><parent>country</parent><pkeys>code</pkeys><child>countrylanguage</child><ckeys>countrycode</ckeys></edge><edge><parent>city</parent><pkeys>id</pkeys><child>zipcodes</child><ckeys>cityid</ckeys></edge></DBProps>";
//		
//		
//		mh.setDBPropsXML(endpoint, dbPropsXML);
//	}
	
	
	
}
