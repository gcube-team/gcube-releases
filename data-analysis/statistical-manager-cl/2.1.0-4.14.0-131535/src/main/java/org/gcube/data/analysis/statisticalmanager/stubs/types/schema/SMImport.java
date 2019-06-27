package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMImport extends SMOperation {
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String fileName;

	public SMImport() {
		super();
	}

	public SMImport(String fileName) {

		this.fileName = fileName;
	}

	/**
	 * Gets the value of the fileName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String fileName() {

		return fileName;
	}

	/**
	 * Sets the value of the fileName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void fileName(String value) {

		this.fileName = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMImport [fileName=");
		builder.append(fileName);
		builder.append("]");
		return builder.toString();
	}
	
	
}
