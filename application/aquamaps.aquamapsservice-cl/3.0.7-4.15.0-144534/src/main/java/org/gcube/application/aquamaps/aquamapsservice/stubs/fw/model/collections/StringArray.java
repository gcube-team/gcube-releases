package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="http://gcube-system.org/namespaces/common/core/types", name="stringArray")
public class StringArray {
	@XmlElement(namespace="http://gcube-system.org/namespaces/common/core/types")
	private List<String> items;
	
	public StringArray() {
		items=new ArrayList<String>();
	}

	public StringArray(Collection<String> items) {		
		if(items!=null)this.items = new ArrayList<String>(items);
	}

	/**
	 * @return the items
	 */
	public List<String> items() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void items(List<String> items) {
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return items+"";
	}
	
	
	
}
