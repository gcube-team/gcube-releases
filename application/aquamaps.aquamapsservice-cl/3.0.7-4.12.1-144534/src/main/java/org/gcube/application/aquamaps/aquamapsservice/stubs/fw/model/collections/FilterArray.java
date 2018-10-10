package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter;

@XmlRootElement(namespace=aquamapsTypesNS)
public class FilterArray {

	@XmlElement(namespace=aquamapsTypesNS, name="filterList")
	private List<Filter> theList=null;
	
	public FilterArray() {
		theList=new ArrayList<Filter>();
	}
	
	public FilterArray(Collection<Filter> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Filter>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Filter> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Filter> theList) {
		this.theList = theList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return theList+"";
	}
	
	
}