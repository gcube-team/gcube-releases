package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;

@XmlRootElement()
public class SMFiles  {
	@XmlElement()
	private List<SMFile> list;

	public SMFiles() {
		if (list == null)
			list = new ArrayList<SMFile>();
	}

	public SMFiles(List<SMFile> list) {
		if (list == null)
			this.list = new ArrayList<SMFile>(list);
	}

	public List<SMFile> list() {
		return list;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void list(List<SMFile> list) {
		if (list == null)
			this.list = new ArrayList<SMFile>(list);
	}
}
