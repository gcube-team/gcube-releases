package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(namespace=DM_target_namespace)
public class ExportCSVSettings {

	@XmlElement(namespace=DM_target_namespace)
	private String encoding;
	
	@XmlElement(namespace=DM_target_namespace)
	private String delimiter=",";
	
	@XmlElement(namespace=DM_target_namespace)
	private boolean hasHeader;
	
	@XmlElement(namespace=DM_target_namespace, name="fieldsMask")
	private List<Boolean> fieldMask;
	
	public ExportCSVSettings() {
		// TODO Auto-generated constructor stub
	}

	public ExportCSVSettings(String encoding, String delimiter, boolean hasHeader,
			List<Boolean> fieldMask) {
		super();
		this.encoding = encoding;
		this.delimiter = delimiter;
		this.hasHeader = hasHeader;
		this.fieldMask = fieldMask;
	}

	/**
	 * @return the encoding
	 */
	public String encoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void encoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the delimiter
	 */
	public String delimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void delimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the hasHeader
	 */
	public boolean hasHeader() {
		return hasHeader;
	}

	/**
	 * @param hasHeader the hasHeader to set
	 */
	public void hasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	/**
	 * @return the fieldMask
	 */
	public List<Boolean> fieldMask() {
		return fieldMask;
	}

	/**
	 * @param fieldMask the fieldMask to set
	 */
	public void fieldMask(List<Boolean> fieldMask) {
		this.fieldMask = fieldMask;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExportCSVSettings [encoding=");
		builder.append(encoding);
		builder.append(", delimiter=");
		builder.append(delimiter);
		builder.append(", hasHeader=");
		builder.append(hasHeader);
		builder.append(", fieldMask=");
		builder.append(fieldMask);
		builder.append("]");
		return builder.toString();
	}
	
	
}
