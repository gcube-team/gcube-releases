package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMEntry {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private long entryId;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private long computationId;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String key;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String value;

	public SMEntry() {
	}

	public SMEntry(long computationId, long entryId, String key,
			String value) {
		this.entryId = entryId;
		this.computationId = computationId;
		this.key = key;
		this.value = value;
	}

	public void entryId(long entryId) {
		this.entryId = entryId;
	}

	public long entryId() {
		return entryId;
	}

	public void computationId(long computationId) {
		this.computationId = computationId;
	}

	public long computationId() {
		return computationId;
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
		builder.append("SMEntry [entryId=");
		builder.append(entryId);
		builder.append(", computationId=");
		builder.append(computationId);
		builder.append(", key=");
		builder.append(key);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	
}
