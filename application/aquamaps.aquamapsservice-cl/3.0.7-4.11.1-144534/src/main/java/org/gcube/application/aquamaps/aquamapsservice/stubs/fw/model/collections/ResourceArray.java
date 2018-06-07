package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource;

@XmlRootElement(namespace=aquamapsTypesNS)
public class ResourceArray {

	@XmlElement(namespace=aquamapsTypesNS, name="resourceList")
	private List<Resource> theList=null;
	
	public ResourceArray() {
		theList=new ArrayList<Resource>();
	}
	
	public ResourceArray(Collection<Resource> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Resource>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Resource> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Resource> theList) {
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