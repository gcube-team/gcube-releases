package org.gcube.search.sru.consumer.client.factory;

import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.gcube.search.sru.consumer.client.SruConsumerClient;
import org.gcube.search.sru.consumer.client.exception.SruConsumerClientException;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;


public class SruConsumerClientTest {

	public static void main(String[] args) throws SruConsumerClientException, InterruptedException, URISyntaxException {
		
		final String scope = "/gcube/devNext";
		final String endpoint = "http://localhost:8080/sru-consumer-service";
//		final String resourceID = "1c37aaa3-70e4-439a-8b33-709ff201155c";
		
//		SruConsumerFactoryClient factory = new SruConsumerFactoryClient.Builder()
//			.endpoint(endpoint)
//			.scope(scope)
//			.skipInitialize(true)
//			.build();
////
//		
////		SruConsumerResource resource = new SruConsumerFactoryClient.ResourceBuilder()
////				.schema("http")
////				.host("highwire.stanford.edu")
////				.port(80)
////				.collectionID("highwireCollection")
////				.maxRecords(10l)
////				.servlet("cgi/sru")
////				.version("1.1")
////				.presentables(Lists.newArrayList("id", "contributor" , "title", "description"))
////				.mapping(ImmutableMap.<String, String>builder()
////						.put("contributor", "//*[local-name() = 'contributor']")
////						.put("title", "//*[local-name() = 'title']")
////						.put("description", "//*[local-name() = 'description']")
////						.build())
////				.recordIDField("id")
////				.build();
//		
		SruConsumerResource resource = new SruConsumerFactoryClient.ResourceBuilder()
			.schema("http")
			.host("www.nla.gov.au")
			.port(80)
			.servlet("apps/srw/search/peopleaustralia")
			.collectionID("60c02e75-ca32-473c-883a-71c0337e1d3e")
			.maxRecords(10l)
			.version("1.1")
			.defaultRecordSchema("info:srw/schema/1/dc-v1.1")
			.presentables(Lists.newArrayList("id", "contributor" , "title", "creator"))
			.searchables(Lists.newArrayList("id", "contributor" , "title", "creator"))
			.mapping(ImmutableMap.<String, String>builder()
					.put("contributor", "//*[local-name() = 'contributor']")
					.put("title", "//*[local-name() = 'title']")
					.put("creator", "//*[local-name() = 'creator']")
					.build())
			.recordIDField("id")
			.build();
//		
		
		
		//String resourceID = factory.createResource(resource, scope);
		
//		String resourceID = "d7d7f58d-990a-4993-9997-c3a95fb6fdab";
		
//		System.out.println("resourceID : " + resourceID);
		
//		Thread.sleep(5*1000);
		
		
//		SruConsumerResource resource2 = SruConsumerFactoryClient.ResourceBuilder
//					.createResourceFromExplain("http", "www.nla.gov.au", 80, "apps/srw/search/peopleaustralia", "nlaCollection");
//		
//		
//		String resourceID2 = factory.createResource(resource2, scope);
		
//		System.out.println("resourceID2 : " + resourceID2);
		
		SruConsumerClient client = new SruConsumerClient.Builder()
			//.endpoint(endpoint)
			.scope(scope)
			//.resourceID(resourceID)
			//.skipInitialize(true)
			.build();
		
		
		String queryString = "name = \"cacikumar\"";
		
		System.out.println("------------------------");
		System.out.println("Query");
		System.out.println(client.query(queryString, 10l, false));
		System.out.println("End Of Query");
		System.out.println("------------------------");
		
		System.out.println("------------------------");
		System.out.println("queryAndRead");
		System.out.println(client.queryAndRead(queryString, 10l, false));
		System.out.println("End Of queryAndRead");
		System.out.println("------------------------");
		
		
		System.out.println("------------------------");
		System.out.println("queryAndReadClientSide");
		System.out.println(client.queryAndReadClientSide(queryString, 10l, false));
		System.out.println("End Of queryAndReadClientSide");
		System.out.println("------------------------");
		
		
		System.out.println("------------------------");
		System.out.println("explain");
		System.out.println(client.explain());
		System.out.println("End Of explain");
		System.out.println("------------------------");
		
		Thread.sleep(5*1000);
		
	}
	
	public static void main2(String[] args) throws XPathExpressionException {
		String xml = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<recordData>\n" + 
				"   <dc xmlns=\"info:srw/schema/1/dc-schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + 
				"      <format>text/html</format>\n" + 
				"      <format>text/xml</format>\n" + 
				"      <type>Collection</type>\n" + 
				"      <type>InteractiveResource</type>\n" + 
				"      <identifier>http://nla.gov.au/nla.party-1477896</identifier>\n" + 
				"      <creator>National Library of Australia Party Infrastructure</creator>\n" + 
				"      <publisher>National Library of Australia Party Infrastructure</publisher>\n" + 
				"      <date>2011-06-10T01:42:12Z</date>\n" + 
				"      <date>2011-06-10T01:42:12Z</date>\n" + 
				"      <language>eng</language>\n" + 
				"      <title>Kala Sashikumar</title>\n" + 
				"      <title>Sashikumar, Kala</title>\n" + 
				"      <title>Cacikumār, Kalā</title>\n" + 
				"      <title>Kalā Cacikumār</title>\n" + 
				"      <title>Saśikumāra, Kalā</title>\n" + 
				"      <title>Kalā Saśikumāra</title>\n" + 
				"      <relation>http://nla.gov.au/anbd.aut-an47037874</relation>\n" + 
				"      <contributor>Libraries Australia</contributor>\n" + 
				"   </dc>\n" + 
				"</recordData>";
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		

		XPathExpression expr = xpath.compile("//*[local-name() = 'title']");
		NodeList nodes = (NodeList)expr.evaluate(new InputSource(
				new StringReader(xml)), XPathConstants.NODESET);
		
		List<String> vals = Lists.newArrayList();
		for (int i = 0 ; i < nodes.getLength() ; i++){
			vals.add(nodes.item(i).getTextContent());
		}
		
		String value = Joiner.on(", ").skipNulls().join(vals);
		System.out.println(value);
		
	}
}
