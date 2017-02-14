package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(name ="SMFile",namespace=TYPES_NAMESPACE)
public class SMFile extends SMResource  {



	@XmlElement( namespace = TYPES_NAMESPACE)
	private String url;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String mimeType;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String remoteName;

	public SMFile() {
		super();

	}

	public SMFile(String mimeType, String remoteName, String url) {

		this.url = url;
		this.mimeType = mimeType;
		this.remoteName = remoteName;
	}

	public String url() {
		return url;
	}

	public void url(String value) {
		this.url = value;
	}

	public String mimeType() {
		return mimeType;
	}

	public void mimeType(String value) {
		this.mimeType = value;
	}

	public String remoteName() {
		return remoteName;
	}

	public void remoteName(String value) {
		this.remoteName = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMFile [url=");
		builder.append(url);
		builder.append(", mimeType=");
		builder.append(mimeType);
		builder.append(", remoteName=");
		builder.append(remoteName);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	

	
	
}
