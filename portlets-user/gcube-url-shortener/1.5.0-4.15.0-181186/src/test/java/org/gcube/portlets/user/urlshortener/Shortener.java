package org.gcube.portlets.user.urlshortener;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 13, 2014
 *
 */
public class Shortener {

	protected static Logger logger = LoggerFactory.getLogger(Shortener.class);
	protected static int tnt = 0;

	@Test
	public void testShortener(){

		Thread th = new Thread(){
			public void run() {
				try {
					String scope ="/gcube";
					ScopeProvider.instance.set(scope);
					UrlShortener urlSh = new UrlShortener();
					System.out.println("UrlShortener: "+urlSh);
					//https://example.page.link/?link=https://www.example.com/&apn=com.example.android&ibi=com.example.ios"
					String shorten = urlSh.shorten("https://data-d.d4science.org/shub/E_YmhuTVJ0R1F4SkhodmhZR2E1V2V3OGRFU0JOandPMEhnWVlEeXlkYlNBdW5YcGtWT2E4OEhOV2dLdTVFbGFhUg==");
					//String shorten = urlSh.shorten("https://data-dev.d4science.net?link=https://next.d4science.org/group/next/workspace?itemid%3D1b3a7f43-8a07-43f1-af0b-ac8396634891%26operation%3Dgotofolder");
					
					
					//String shorten = urlSh.shorten("https://developers.google.com/url-shortener/v1/getting_started?hl=it "+new Random().nextDouble());
					System.out.println("Shorted: "+shorten);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};


		/*while (true) {
			System.out.println("Tentative: "+tnt);
			incrementCount();
			th.run();
		}*/

		th.run();
	}

	public static synchronized void incrementCount() {
		  tnt++;
	}
}
