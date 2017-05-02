package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;


/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabImportMetadata implements TabMetadata {
	private static final long serialVersionUID = -2663624208642658528L;

	String id="ImportMetadata";
	String title="Import";
	
	private String sourceType;
	private String url;
	private String importDate;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	@Override
	public String toString() {
		return "TabImportMetadata [sourceType=" + sourceType + ", url=" + url
				+ ", importDate=" + importDate + "]";
	}

	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

}
