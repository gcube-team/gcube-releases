package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;



/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRNameMetadata implements TRMetadata {
	
	private static final long serialVersionUID = 7635332011036656032L;
	
	String id="NameMetadata";
	String title="Name";
	
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
		return "TRNameMetadata [id=" + id + ", title=" + title + ", value="
				+ value + "]";
	}

		 
	 

}
