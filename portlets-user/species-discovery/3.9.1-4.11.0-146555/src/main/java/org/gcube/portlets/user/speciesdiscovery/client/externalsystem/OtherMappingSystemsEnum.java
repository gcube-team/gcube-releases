package org.gcube.portlets.user.speciesdiscovery.client.externalsystem;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public enum OtherMappingSystemsEnum implements ExternalSystemInterface{
	GBIF("GBIF", "http://data.gbif.org/species/species/", ""),
	DISCOVERLIFE("DiscoverLife", "http://www.discoverlife.org/mp/20q?search=", "");
	
	
	private String name;
	private String url;
	private String suffixUrl;
	
	/**
	 * 
	 * @param name
	 * @param url
	 */
	private OtherMappingSystemsEnum(String name, String url, String suffixUrl) {
		this.name = name;
		this.url = url;
		this.suffixUrl = suffixUrl;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getBaseUrl() {
		return url;
	}

	@Override
	public String getSuffixUrl() {
		return this.suffixUrl;
	}

}
