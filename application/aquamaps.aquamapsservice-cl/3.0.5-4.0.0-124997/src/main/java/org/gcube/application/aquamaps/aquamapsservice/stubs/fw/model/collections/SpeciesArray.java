package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Species;

@XmlRootElement(namespace=aquamapsTypesNS)
public class SpeciesArray {

	@XmlElement(namespace=aquamapsTypesNS, name="speciesList")
	private List<Species> theList=null;
	
	public SpeciesArray() {
		theList=new ArrayList<Species>();
	}
	
	public SpeciesArray(Collection<Species> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Species>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Species> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Species> theList) {
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