package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TabExportMetadata implements TabMetadata {
	private static final long serialVersionUID = -2663624208642658528L;
	
	String id="ExportMetadata";
	String title="Export";
	
	private String destinationType;
	private String url;
	private String exportDate;
	
	public String getDestinationType() {
		return destinationType;
	}
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getExportDate() {
		return exportDate;
	}
	public void setExportDate(String exportDate) {
		this.exportDate = exportDate;
	}
	@Override
	public String toString() {
		return "TabExportMetadata [destinationType=" + destinationType
				+ ", url=" + url + ", exportDate=" + exportDate + "]";
	}
	
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	
	
	
}
