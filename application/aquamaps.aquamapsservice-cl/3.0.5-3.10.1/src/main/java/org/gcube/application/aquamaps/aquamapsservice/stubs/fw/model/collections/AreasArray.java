package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Area;

@XmlRootElement(namespace=aquamapsTypesNS)
public class AreasArray {

	@XmlElement(namespace=aquamapsTypesNS, name="areasList")
	private List<Area> theList=null;
	
	public AreasArray() {
		theList=new ArrayList<Area>();
	}
	
	public AreasArray(Collection<Area> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Area>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Area> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Area> theList) {
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
