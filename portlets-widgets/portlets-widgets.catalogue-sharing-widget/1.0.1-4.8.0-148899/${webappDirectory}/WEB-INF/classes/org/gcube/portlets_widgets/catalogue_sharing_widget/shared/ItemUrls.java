package org.gcube.portlets_widgets.catalogue_sharing_widget.shared;

import java.io.Serializable;

/**
 * Short and long object url.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ItemUrls implements Serializable {

	private static final long serialVersionUID = 1830399247771490527L;

	private String shortUrl;
	private String url;
	private String catalogueUUID;
	private String productName;
	private String productTitle;

	/**
	 * 
	 */
	public ItemUrls() {
		super();
	}

	/**
	 * @param shortUrl
	 * @param url
	 * @param catalogueUUID
	 */
	public ItemUrls(String shortUrl, String url, String catalogueUUID, String productName, String productTitle) {
		super();
		this.shortUrl = shortUrl;
		this.url = url;
		this.catalogueUUID = catalogueUUID;
		this.productName = productName;
		this.productTitle = productTitle;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCatalogueUUID() {
		return catalogueUUID;
	}

	public void setCatalogueUUID(String catalogueUUID) {
		this.catalogueUUID = catalogueUUID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	@Override
	public String toString() {
		return "ItemUrls [shortUrl=" + shortUrl + ", url=" + url
				+ ", catalogueUUID=" + catalogueUUID + ", productName="
				+ productName + ", productTitle=" + productTitle + "]";
	}	
}
