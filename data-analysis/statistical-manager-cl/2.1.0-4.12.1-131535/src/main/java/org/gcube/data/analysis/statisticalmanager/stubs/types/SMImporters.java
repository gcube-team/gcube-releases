package org.gcube.data.analysis.statisticalmanager.stubs.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;

public class SMImporters {
	@XmlElement(name = "list")
	private List<SMImport> theList;

	public SMImporters() {
		if (this.theList == null) {
			this.theList = new ArrayList<SMImport>();
		}
	}

	public SMImporters(List<SMImport> list) {


			this.theList = new ArrayList<SMImport>(list);

	}

	public List<SMImport> theList() {
		return theList;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void theList(List<SMImport> list) {
		if (list != null)
			this.theList = new ArrayList<SMImport>(list);
	}
}
