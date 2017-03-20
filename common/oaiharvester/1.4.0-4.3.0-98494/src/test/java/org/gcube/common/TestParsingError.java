package org.gcube.common;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.http.client.ClientProtocolException;

public class TestParsingError {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		OMElement documentElement = null;
		try {
			documentElement = Utils.getReaderFromHttpGet("http://www.bioline.org.br/oai", "ListRecords&resumptionToken=;oai_dc;;;16200");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Iterator<OMElement> getRecords = null;
		Iterator<OMElement> getListRecords = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListRecords"));		
		if (getListRecords.hasNext()){
			getRecords = getListRecords.next().getChildElements();
		}

		while (getRecords.hasNext()){
			OMElement elem = null;
			try{
				elem = getRecords.next();

				System.out.println(elem);
			}
			catch(OMException ex) {
				
					try {
					elem.serializeAndConsume(System.out);
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		}

	}
}
