package org.gcube.portlets.user.urlshortener;
import org.gcube.common.scope.api.ScopeProvider;
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
public class TestUrlShortener {

	protected static Logger logger = LoggerFactory.getLogger(TestUrlShortener.class);
	protected static int tnt = 0;

//	@Test
	public void testShortener(){

		Thread th = new Thread(){
			public void run() {
				try {
					String scope ="/gcube/devsec";
					ScopeProvider.instance.set(scope);
					UrlShortener urlSh = new UrlShortener();
					System.out.println("UrlShortener: "+urlSh);
					String shorten = urlSh.shorten("http://data.d4science.org/uri-resolver/UUFYTGtkaEtiK2s0TURzdTBQckpBSDJmbVkrOXAzazVHbWJQNStIS0N6Yz0");
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
