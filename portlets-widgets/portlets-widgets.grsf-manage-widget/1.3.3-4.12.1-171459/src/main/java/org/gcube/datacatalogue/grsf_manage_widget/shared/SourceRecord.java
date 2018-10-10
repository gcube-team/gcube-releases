package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * A source record for this grsf record: source type (i.e. fishsource, ram, firms), name and identifier
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SourceRecord extends GenericRecord{

	private static final long serialVersionUID = -5144710283443577518L;
	private String name; // one of RAM, FIRMS, FishSource

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
		setUrl(url);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SourceRecord [name=" + name + "]";
	}

}