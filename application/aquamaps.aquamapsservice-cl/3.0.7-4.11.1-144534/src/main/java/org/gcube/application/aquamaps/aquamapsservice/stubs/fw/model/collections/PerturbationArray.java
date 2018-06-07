package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Perturbation;


public class PerturbationArray {

	@XmlElement(namespace=aquamapsTypesNS, name="perturbationList")
	private List<Perturbation> theList=null;
	
	public PerturbationArray() {
		theList=new ArrayList<Perturbation>();
	}
	
	public PerturbationArray(Collection<Perturbation> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Perturbation>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Perturbation> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Perturbation> theList) {
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