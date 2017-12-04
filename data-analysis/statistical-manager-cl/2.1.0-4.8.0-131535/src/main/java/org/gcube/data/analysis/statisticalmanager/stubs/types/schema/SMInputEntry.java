package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMInputEntry {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String key;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String value;
	
	public SMInputEntry() {
    }

	public SMInputEntry(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public void key(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}

	public void value(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMInputEntry [key=");
		builder.append(key);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	
}
