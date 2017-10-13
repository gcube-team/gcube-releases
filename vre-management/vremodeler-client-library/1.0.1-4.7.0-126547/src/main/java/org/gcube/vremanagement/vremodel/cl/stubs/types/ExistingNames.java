package org.gcube.vremanagement.vremodel.cl.stubs.types;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExistingNames {

	@XmlElement(name="names")
	private List<String> names;

	protected ExistingNames() {
		super();
	}

	public ExistingNames(List<String> names) {
		super();
		this.names = names;
	}

	/**
	 * @return the names
	 */
	public List<String> names() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void names(List<String> names) {
		this.names = names;
	}
	
	
}
