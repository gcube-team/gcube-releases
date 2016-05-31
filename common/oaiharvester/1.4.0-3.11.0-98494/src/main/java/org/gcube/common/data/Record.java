package org.gcube.common.data;

import java.io.Serializable;

import org.apache.axiom.om.OMElement;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;
	private Header header;
	private Metadata metadata;
	private OMElement metadataElement;
	private String metadataPrefix;
	private String metadataNamespaceURI;
	private Boolean isDeleted = false;

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	/**
	 * @return the isDeleted
	 */
	public Boolean IsDeleted() {
		return isDeleted;
	}
	/**
	 * @param metadataNamespaceURI the metadataNamespaceURI to set
	 */
	public void setMetadataNamespaceURI(String metadataNamespaceURI) {
		this.metadataNamespaceURI = metadataNamespaceURI;
	}
	/**
	 * @return the metadataNamespaceURI
	 */
	public String getMetadataNamespaceURI() {
		return metadataNamespaceURI;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the metadataPrefix
	 */
	public String getMetadataPrefix() {
		return metadataPrefix;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	/**
	 * @param metadataElement the metadataElement to set
	 */
	public void setMetadataElement(OMElement metadataElement) {
		this.metadataElement = metadataElement;
	}
	/**
	 * @param metadataPrefix the metadataPrefix to set
	 */
	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}
	/**
	 * @return the metadataElement
	 */
	public OMElement getMetadataElement() {
		return metadataElement;
	}

}
