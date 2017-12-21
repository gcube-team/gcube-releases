package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;


/**
 * A source record for this grsf record: source type (i.e. fishsource, ram, firms), name and identifier
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SourceRecord implements Serializable{

	private static final long serialVersionUID = -5144710283443577518L;
	private String name;
	private String url; // within the catalogue
	
	public SourceRecord() {
		super();
	}

	/**
	 * @param name
	 * @param url
	 */
	public SourceRecord(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "SourceRecord [name=" + name + ", url=" + url + "]";
	}
}