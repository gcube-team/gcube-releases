package org.gcube.data.tml.proxies;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

@XmlRootElement
public class Binding {

	@XmlElement
	String sourceID;
	
	@XmlElement
	W3CEndpointReference readerEndpoint;
	
	@XmlElement
	W3CEndpointReference writerEndpoint;
	
	public Binding() {
	}
	
	Binding(String sourceId,W3CEndpointReference readerRef,W3CEndpointReference writerRef) {
		this.sourceID=sourceId;
		this.readerEndpoint=readerRef;
		this.writerEndpoint=writerRef;
	}
	
	public String source() {
		return sourceID;
	}
	
	public W3CEndpointReference readerRef() {
		return readerEndpoint; 
	}
	
	public W3CEndpointReference writerRef() {
		return writerEndpoint;
	}
	
	@Override
	public String toString() {
		return "source:"+source()+
				(readerRef()==null?"":"\nreader:"+readerRef())+
				(writerRef()==null?"":"\nwriter:"+writerRef());
	}
	
}
