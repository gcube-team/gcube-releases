package org.gcube.data.tml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownPathException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.w3c.dom.Document;

public class TestUtils {
	
	private static DocumentBuilder payloadBuilder; 
	
	static {
		DocumentBuilderFactory.newInstance();
		try {
			payloadBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Document newDocument() {
		return payloadBuilder.newDocument();
	}

	public static String SOURCE_ID = "source";
	public static String TREE_ID = "treeid";
	//public static String ROOT_URI = PROTOCOL+"://"+SOURCE_ID+"/"+TREE_ID;
	
	public static UnknownTreeException UNKNOWN = new UnknownTreeException();
	public static UnknownPathException UNKNOWNPATH=new UnknownPathException();
	public static InvalidTreeException INVALID = new InvalidTreeException();
	public static DiscoveryException UNFOUND = new DiscoveryException(new Exception());

}
