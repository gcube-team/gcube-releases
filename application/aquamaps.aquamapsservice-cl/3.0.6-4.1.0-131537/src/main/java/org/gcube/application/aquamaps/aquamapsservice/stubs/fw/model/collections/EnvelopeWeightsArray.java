package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.EnvelopeWeights;

@XmlRootElement(namespace=aquamapsTypesNS)
public class EnvelopeWeightsArray {

	@XmlElement(namespace=aquamapsTypesNS, name="resourceList")
	private List<EnvelopeWeights> theList=null;
	
	public EnvelopeWeightsArray() {
		theList=new ArrayList<EnvelopeWeights>();
	}
	
	public EnvelopeWeightsArray(Collection<EnvelopeWeights> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<EnvelopeWeights>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<EnvelopeWeights> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<EnvelopeWeights> theList) {
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