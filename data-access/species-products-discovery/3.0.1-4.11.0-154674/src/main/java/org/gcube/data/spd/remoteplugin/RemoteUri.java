package org.gcube.data.spd.remoteplugin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="RemoteUri")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteUri {

	@XmlElement(name="id")
	private String endpointId;
	
	@XmlElement(name="uri")
	private String uri;
	
	public RemoteUri(){}
	
	public RemoteUri(String endpointId, String uri) {
		this.endpointId = endpointId;
		this.uri = uri;
	}

	public String getEndpointId() {
		return endpointId;
	}

	public String getUri() {
		return uri;
	}
		
}
