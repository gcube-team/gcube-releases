import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.junit.Test;

/**
 *
 */

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 20, 2014
 *
 */
public class UriResolverManagerTest {

	@Test
	public void testUriResolverManger(){
		UriResolverManager manager;
		try {
			ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
			manager = new UriResolverManager();
			System.out.println(manager.getCapabilities());
			System.out.println(manager.getApplicationTypes());
		} catch (UriResolverMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	@Test
	public void testGIS() {

		try {
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			UriResolverManager resolver = new UriResolverManager("GIS");
			Map<String, String> params = new HashMap<String, String>();
			params.put("gis-UUID", "5ac49f44-999f-4efe-a32b-af71da2b39ac");
			params.put("scope", "/gcube/devsec/devVRE");
			String shortLink = resolver.getLink(params, true);
			System.out.println(shortLink); //true, link is shorted otherwise none
		} catch (UriResolverMapException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testSMP() {

		try {
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			UriResolverManager resolver = new UriResolverManager("SMP");
			Map<String, String> params = new HashMap<String, String>();
			params.put("smp-uri","smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y");
			params.put("fileName", "wikipediaLogo");
			params.put("contentType", "");
			String shortLink = resolver.getLink(params, true); //true, link is shorted otherwise none
			System.out.println(shortLink);
		} catch (UriResolverMapException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Thread safe
	 */
//	@Test
	public void testSMPID(){

        try {
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			UriResolverManager resolver;
			resolver = new UriResolverManager("SMP-ID");
			Map<String, String> params = new HashMap<String, String>();
			params.put("smp-id","553f9265e4b0567b75021fce");
			params.put("fileName", "dog");
			params.put("contentType", "image/jpg");
			String shortLink = resolver.getLink(params, true); //true, link is shorted otherwise none
			System.out.println(shortLink);
		} catch (UriResolverMapException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Thread safe
	 */
//	@Test
	public void test2(){


		//create thread to print counter value
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {

                    	ScopeProvider.instance.set("/gcube/devsec/devVRE");
            			UriResolverManager resolver;
						resolver = new UriResolverManager("GIS");

            			Map<String, String> params = new HashMap<String, String>();
            			params.put("gis-UUID", "eb1a1b63-f324-47ee-9522-b8f5803e19ec");
            			params.put("scope", "/gcube/devsec/devVRE");
            			String shortLink = resolver.getLink(params, true);
            			System.out.println(shortLink); //true, link is shorted otherwise none

                        System.out.println("Thread "+Thread.currentThread().getId() +" reading counter is: " + resolver.countReaders());
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }catch (UriResolverMapException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
               }
            }

        });

        t.start();

		try {
			Thread.sleep(500000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
