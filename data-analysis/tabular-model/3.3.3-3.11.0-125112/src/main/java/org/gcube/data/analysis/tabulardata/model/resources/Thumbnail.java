package org.gcube.data.analysis.tabulardata.model.resources;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Thumbnail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3097080363495354080L;
	
	private URI uri;
	private String mimeType;
	
	@SuppressWarnings("unused")
	private Thumbnail(){}
	
	public Thumbnail(URI uri, String mimeType) {
		super();
		this.uri = uri;
		this.mimeType = mimeType;
	}

	public URI getUri() {
		return uri;
	}

	public String getMimeType() {
		return mimeType;
	}
		
}
