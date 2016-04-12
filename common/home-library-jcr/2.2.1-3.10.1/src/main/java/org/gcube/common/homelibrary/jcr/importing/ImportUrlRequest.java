/**
 * 
 */
package org.gcube.common.homelibrary.jcr.importing;
/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ImportUrlRequest extends ImportRequest {
	
	protected String url;

	/**
	 * Create an url import request.
	 * @param url the url to import.
	 */
	public ImportUrlRequest(String url) {
		super(ImportRequestType.URL);
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString()+" url: "+url;
	}

}
