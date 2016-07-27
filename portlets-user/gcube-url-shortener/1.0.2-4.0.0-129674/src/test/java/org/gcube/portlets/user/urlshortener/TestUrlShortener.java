package org.gcube.portlets.user.urlshortener;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.junit.Test;

/**
 * 
 */

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 13, 2014
 *
 */
public class TestUrlShortener {
	
	protected static Logger logger = Logger.getLogger(TestUrlShortener.class);
	
	@Test
	public void testShortener(){
		
	String shorten;
    	
		try {
			String scope ="/gcube/devsec";
			UrlShortener urlSh = new UrlShortener(scope);
			logger.trace("UrlShortener: "+urlSh);
			shorten = urlSh.shorten("https://developers.google.com/url-shortener/v1/getting_started?hl=it");
			logger.trace("Shorted: "+shorten);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

}
