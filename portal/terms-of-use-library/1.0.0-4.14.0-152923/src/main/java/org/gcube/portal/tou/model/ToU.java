package org.gcube.portal.tou.model;

/**
 * A ToU with Title and Content
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ToU {

	private String title;
	private String content;
	private long id;
	private double version;

	/**
	 * 
	 * @param title
	 * @param content (it can be html)
	 * @param articleId
	 * @param version
	 */
	public ToU(String title, String content, long touId, double version) {
		super();
		this.title = title;
		this.content = content;
		this.id = touId;
		this.version = version;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "ToU [title=" + title + ", content=" + content + ", id=" + id
				+ ", version=" + version + "]";
	}
}
