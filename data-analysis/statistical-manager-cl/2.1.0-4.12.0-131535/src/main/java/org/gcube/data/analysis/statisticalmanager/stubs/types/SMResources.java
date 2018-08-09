package org.gcube.data.analysis.statisticalmanager.stubs.types;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
@XmlRootElement()
public class SMResources {
	@XmlElement(name="list")
	private List<SMResource> list;

	public SMResources() {
		super();
			this.list = new ArrayList<SMResource>();

	}

	public SMResources(ArrayList<SMResource> list) {


		if (list != null)
			this.list = new ArrayList<SMResource>(list);
	}

	public List<SMResource> list() {
		return list;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void list(List<SMResource> list) {
		this.list = new ArrayList<SMResource>(list);
	}
}
