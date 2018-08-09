package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class GHNsPerFunctionality {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private int id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String description;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private GHNList ghns;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<RunningInstanceMessage> missingServices;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<RunningInstanceMessage> foundServices;
			
	protected GHNsPerFunctionality() {
		super();
	}

	public GHNsPerFunctionality(int id, String description, GHNList ghns,
			List<RunningInstanceMessage> missingServices,
			List<RunningInstanceMessage> foundServices) {
		super();
		this.id = id;
		this.description = description;
		this.ghns = ghns;
		this.missingServices = missingServices;
		this.foundServices = foundServices;
	}

	/**
	 * @return the id
	 */
	public int id() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void id(int id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String description() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void description(String description) {
		this.description = description;
	}

	/**
	 * @return the ghns
	 */
	public List<GHN> ghns() {
		if (ghns!=null && ghns.ghns()!=null)
			return ghns.ghns();
		else return Collections.emptyList();
	}

	/**
	 * @param ghns the ghns to set
	 */
	public void ghns(GHNList ghns) {
		this.ghns = ghns;
	}

	/**
	 * @return the missingServices
	 */
	public List<RunningInstanceMessage> missingServices() {
		return missingServices;
	}

	/**
	 * @param missingServices the missingServices to set
	 */
	public void missingServices(List<RunningInstanceMessage> missingServices) {
		this.missingServices = missingServices;
	}

	/**
	 * @return the foundServices
	 */
	public List<RunningInstanceMessage> foundServices() {
		return foundServices;
	}

	/**
	 * @param foundServices the foundServices to set
	 */
	public void foundServices(List<RunningInstanceMessage> foundServices) {
		this.foundServices = foundServices;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GHNsPerFunctionality [id=" + id + ", description="
				+ description + ", ghns=" + ghns + ", missingServices="
				+ missingServices + ", foundServices=" + foundServices + "]";
	}
	
	
	
}
