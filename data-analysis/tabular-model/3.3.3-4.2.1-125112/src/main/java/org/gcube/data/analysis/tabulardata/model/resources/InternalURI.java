package org.gcube.data.analysis.tabulardata.model.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InternalURI extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3230438212164027113L;
	
	private String fileId;
	
	private URI uri;
	
	private String mimeType;
	
	private Thumbnail thumbnail;
	
	@SuppressWarnings("unused")
	private InternalURI(){}
	
	public InternalURI(URI uri){
		this.uri = uri;
	}
		
	public InternalURI(URI uri, String mimeType){
		this(uri);
		this.mimeType = mimeType;
	}
	
	public InternalURI(URI uri, String mimeType, Thumbnail thumbnail){
		this(uri, mimeType);
		this.thumbnail = thumbnail;
	}
	
	public URI getUri() {
		if (uri!=null) return uri;
		else
			try {
				return new URI(fileId);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
	}

	@Override
	public String getStringValue() {
		if (uri!=null) return uri.toString();
		else return fileId;
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return this.getClass();
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public Thumbnail getThumbnail() {
		return thumbnail;
	}
}
