package org.gcube.portlets.user.speciesdiscovery.client.externalsystem;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public enum OtherInformationSystemsEnum implements ExternalSystemInterface{
	
//	CATALOGUEOFLIFE("Catalogue Of Life", "http://www.catalogueoflife.org/annual-checklist/2006/search_results.php?search_string="),
	CATALOGUEOFLIFE("Catalogue Of Life", "	http://www.catalogueoflife.org/col/search/all/key/", "/match/1"),
	ENCICOLPEDIAOFLIFE("Encyclopedia of Life","http://www.eol.org/search?ie=UTF-8&search_type=text&q=", ""),
	BARCODEOFLIFE("Barcode of Life","http://www.boldsystems.org/index.php/Taxbrowser_Taxonpage?taxon=", ""),
	GENBANK("GenBank","http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?name=", ""),
	UBIO("uBio","http://www.ubio.org/browser/search.php?search_all=", ""),
	SPECIESIDENTIFICATIONORG("Species-identification.org","http://species-identification.org/search.php?search_mode=basic&search_for=", "");
	
	private String name;
	private String baseUrl;
	private String suffixUrl;

	/**
	 * 
	 * @param name
	 * @param value
	 */
	private OtherInformationSystemsEnum(String name, String baseUrl, String suffixUrl) {
		this.name = name;
		this.baseUrl = baseUrl;
		this.suffixUrl = suffixUrl;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getBaseUrl() {
		return baseUrl;
	}

	@Override
	public String getSuffixUrl() {
		return suffixUrl;
	}

}
