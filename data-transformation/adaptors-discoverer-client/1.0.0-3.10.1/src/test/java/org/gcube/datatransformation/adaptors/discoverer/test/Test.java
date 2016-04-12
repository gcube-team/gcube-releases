package org.gcube.datatransformation.adaptors.discoverer.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Test {

	public static void main(String[] args) throws IOException {
		
		
	}

	private List<Info> queryForProps(String endpoint) throws IOException{
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(endpoint + "/AvailableResources");
		httpget.addHeader("gcube-scope", "/gcube/devNext");
		HttpResponse response = httpclient.execute(httpget);
		
		String status = response.getStatusLine().toString();
		
		if(response.getStatusLine().getStatusCode() != 200)
			return null;
		
		System.out.println(status);
		
		HttpEntity entity = response.getEntity();
		
		List<Info> output = new ArrayList<Info>();
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(entity.getContent());
			NodeList resources = doc.getElementsByTagName("resource");
			for (int i = 0; i < resources.getLength(); i++) {
				Node nNode = resources.item(i);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("resourceid : " + eElement.getElementsByTagName("resourceid").item(0).getTextContent());
					System.out.println("sourcename : " + eElement.getElementsByTagName("sourcename").item(0).getTextContent());
					System.out.println("propsname : " + eElement.getElementsByTagName("propsname").item(0).getTextContent());
					output.add(new Info(eElement.getElementsByTagName("sourcename").item(0).getTextContent(), 
										eElement.getElementsByTagName("propsname").item(0).getTextContent()));
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	class Info {
		
		private String sourcename;
		private String propsname;
		
		public Info(String sourcename, String propsname){
			this.sourcename = sourcename;
			this.propsname = propsname;
		}
		
		public String getSourcename() {
			return sourcename;
		}
		public void setSourcename(String sourcename) {
			this.sourcename = sourcename;
		}
		public String getPropsname() {
			return propsname;
		}
		public void setPropsname(String propsname) {
			this.propsname = propsname;
		}
		
		
	}
	
}
