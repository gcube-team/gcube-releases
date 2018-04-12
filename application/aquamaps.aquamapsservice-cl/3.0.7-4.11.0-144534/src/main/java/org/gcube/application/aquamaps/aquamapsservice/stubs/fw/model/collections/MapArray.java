package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Map;

@XmlRootElement(namespace=aquamapsTypesNS)
public class MapArray {

	@XmlElement(namespace=aquamapsTypesNS, name="mapList")
	private List<Map> theList=null;
	
	public MapArray() {
		theList=new ArrayList<Map>();
	}
	
	public MapArray(Collection<Map> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Map>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Map> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Map> theList) {
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