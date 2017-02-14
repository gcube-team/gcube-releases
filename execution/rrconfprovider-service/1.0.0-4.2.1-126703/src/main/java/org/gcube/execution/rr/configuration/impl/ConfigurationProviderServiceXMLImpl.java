package org.gcube.execution.rr.configuration.impl;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.naming.SynchronizedContext;
import org.gcube.execution.rr.configuration.ConfigurationProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ConfigurationProviderServiceXMLImpl implements ConfigurationProvider {
	public List<String> getGHNContextStartScopes() {
		try {
			return getStartScopes();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public List<String> getGHNContextScopes() {
		try {
			return getStartScopes();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getStartScopes() throws Exception {
		String filename = System.getenv("GLOBUS_LOCATION") + "/config/GHNConfig.xml";
		Document doc = loadXMLFromFile(filename);
		doc.getDocumentElement().normalize();
		
		List<String> scopes = getStartScopes(doc);
		//System.out.println("startScopes : " + scopes);
		return scopes;
	}
	
	
	
	
	static List<String> getStartScopes(Document doc) {
		String infrastructure = getInfrastructure(doc);
		List<String> scopes = getScopes(doc);
		
		List<String> startScopes = new ArrayList<String>();
		
		startScopes.add("/" + infrastructure);
		
		for (String sc : scopes)
			startScopes.add("/" + infrastructure + "/" + sc);
		
		return startScopes;
	}
	
	static String getInfrastructure(Document doc) {
		return getElement(doc, "environment", "infrastructure");
	}

	static List<String> getScopes(Document doc) {
		String scopesValue = getElement(doc, "environment", "startScopes");
		if (scopesValue == null)
			return null;
		String scopesArr[] = scopesValue.split(",");

		List<String> scopes = new ArrayList<String>();
		for (String sc : scopesArr)
			scopes.add(sc.trim());
		return scopes;
	}

	static String getElement(Document doc, String tag, String attributeName) {
		NodeList nl = doc.getElementsByTagName(tag);
		Node n;

		for (int i = 0; i < nl.getLength(); i++) {
			n = nl.item(i);

			NamedNodeMap nnm = n.getAttributes();

			String nodeName = nnm.getNamedItem("name").getNodeValue();

			if (nodeName.equalsIgnoreCase(attributeName)) {
				String nodeValue = nnm.getNamedItem("value").getNodeValue();
				return nodeValue;
			}
		}
		return null;
	}

	public static Document loadXMLFromFile(String filename) throws Exception {
		long before = System.currentTimeMillis();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new FileReader(filename));
		Document doc = builder.parse(is);

		long after = System.currentTimeMillis();

		return doc;
	}


	public boolean isClientMode() {
		//return GHNContext.getContext().isClientMode();
		
		//test JNDI to detect client mode and set status to DOWN in case
		try {getContext().lookup("java:comp/env/status");}
		catch(Exception e) {return true;}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	static  InitialContext getContext() {
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(SynchronizedContext.SYNCHRONIZED, "true");
			env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			return new InitialContext(env);
		} catch (Exception e) {
			
		}
		return null;
	}
	
	

	
	
	
	

}
