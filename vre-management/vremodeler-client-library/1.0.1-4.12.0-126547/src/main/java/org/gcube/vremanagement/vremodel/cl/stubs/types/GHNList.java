package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class GHNList {

	@XmlElement(namespace=TYPES_NAMESPACE, name="list")
	List<GHN> ghns;

	protected GHNList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GHNList(List<GHN> ghns) {
		super();
		this.ghns = ghns;
	}

	/**
	 * @return the ghns
	 */
	public List<GHN> ghns() {
		return ghns;
	}

	/**
	 * @param ghns the ghns to set
	 */
	public void ghns(List<GHN> ghns) {
		this.ghns = ghns;
	}
	
	
	
}
