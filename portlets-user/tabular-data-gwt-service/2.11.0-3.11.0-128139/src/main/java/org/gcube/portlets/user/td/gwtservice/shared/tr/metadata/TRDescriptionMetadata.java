package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;



/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRDescriptionMetadata implements TRMetadata {
	private static final long serialVersionUID = -2663624208642658528L;

	
	String id="DescriptionMetadata";
	String title="Description";
	
	String value;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TRDescriptionMetadata [id=" + id + ", title=" + title
				+ ", value=" + value + "]";
	}
	

}
