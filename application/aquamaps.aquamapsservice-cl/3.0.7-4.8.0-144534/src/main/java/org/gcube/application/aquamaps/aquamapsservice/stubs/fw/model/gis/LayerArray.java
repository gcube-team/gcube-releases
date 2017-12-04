package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;


import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=gisTypesNS)
public class LayerArray {

	@XmlElement(namespace=gisTypesNS, name="name")
	private List<LayerInfoType> theList=null;
	
	public LayerArray() {
		theList=new ArrayList<LayerInfoType>();
	}
	
	public LayerArray(Collection<LayerInfoType> initialCollection) {
		theList=new ArrayList<LayerInfoType>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<LayerInfoType> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<LayerInfoType> theList) {
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
