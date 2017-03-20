package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class FunctionalityNodes {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<GHNsPerFunctionality> functionalities;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private GHNList selectableGHNs;
	/**
	 * @return the functionalities
	 */
	public List<GHNsPerFunctionality> functionalities() {
		return functionalities;
	}
	/**
	 * @param functionalities the functionalities to set
	 */
	public void functionalities(List<GHNsPerFunctionality> functionalities) {
		this.functionalities = functionalities;
	}
	/**
	 * @return the selectableGHNs
	 */
	public List<GHN> selectableGHNs() {
		if (selectableGHNs!=null && selectableGHNs.ghns()!=null)
			return selectableGHNs.ghns();
		else return Collections.emptyList();
		
	}
	/**
	 * @param selectableGHNs the selectableGHNs to set
	 */
	public void selectableGHNs(List<GHN> ghns) {
		this.selectableGHNs = new GHNList(ghns);
	}
	
}
