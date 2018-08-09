package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.AquaMap;

@XmlRootElement(namespace=aquamapsTypesNS)
public class AquaMapArray {

	@XmlElement(namespace=aquamapsTypesNS, name="aquaMapList")
	private List<AquaMap> theList=null;
	
	public AquaMapArray() {
		theList=new ArrayList<AquaMap>();
	}
	
	public AquaMapArray(Collection<AquaMap> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<AquaMap>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<AquaMap> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<AquaMap> theList) {
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
