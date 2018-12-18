package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace=TYPES_NAMESPACE)

public class SMEntries {
	@XmlElement(namespace=TYPES_NAMESPACE)
	private SMInputEntry[] list;
	
	
	 public SMEntries() {
	    }

	public SMEntries(SMInputEntry[] list) {
		this.list=list;
	}

	public SMInputEntry[] list() {
		return list;
	}

	/**
	 * @param resource the resource to set
	 */
	public void list(SMInputEntry[]list) {
		this.list = list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMEntries [list=");
		builder.append(Arrays.toString(list));
		builder.append("]");
		return builder.toString();
	}
	
	
}
