package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class GHNArray {
	
	@XmlElement(name="GHNElement", namespace=TYPES_NAMESPACE)
	private List<String> ghns;
	
	public GHNArray(List<String> ghns) {
		super();
		this.ghns = ghns;
	}
	
	protected GHNArray() {
		super();
	}

	/**
	 * @return the ghns
	 */
	public List<String> shns() {
		return ghns;
	}

	/**
	 * @param ghns the ghns to set
	 */
	public void ghns(List<String> ghns) {
		this.ghns = ghns;
	}
	
	
	
}
