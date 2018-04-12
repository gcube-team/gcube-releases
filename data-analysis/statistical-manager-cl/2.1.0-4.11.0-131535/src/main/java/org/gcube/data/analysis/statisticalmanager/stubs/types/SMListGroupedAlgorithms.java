package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMListGroupedAlgorithms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4000169036422198147L;
	@XmlElement(name = "list")
	private List<SMGroupedAlgorithms> thelist;

	public SMListGroupedAlgorithms() {
			this.thelist = new ArrayList();
	}

	public SMListGroupedAlgorithms(List<SMGroupedAlgorithms> list) {
			thelist = new ArrayList<SMGroupedAlgorithms>();
	}

	public List<SMGroupedAlgorithms> thelist() {

		return thelist;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void thelist(List<SMGroupedAlgorithms> list) {

		this.thelist = new ArrayList<SMGroupedAlgorithms>(list);
	}

}
