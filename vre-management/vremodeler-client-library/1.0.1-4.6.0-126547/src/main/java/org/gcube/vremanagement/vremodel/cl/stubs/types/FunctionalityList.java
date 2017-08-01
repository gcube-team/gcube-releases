package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class FunctionalityList {

	@XmlElement(namespace=TYPES_NAMESPACE, name="list")
	private List<FunctionalityItem> items;

	/**
	 * @return the items
	 */
	public List<FunctionalityItem> items() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void items(List<FunctionalityItem> items) {
		this.items = items;
	}
	
	
}
