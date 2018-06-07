import javax.servlet.ServletException;

import org.gcube.datatransfer.resolver.GeonetworkRequestDecoder;
import org.junit.Test;


/**
 *
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 27, 2016
 */
public class GeonetworkRequestDecoderTest {


	//@Test
	public void test1() throws ServletException{
		String request = "/geonetwork/gcube|devsec|devVRE#filterpublicids";
		System.out.println("Testing request: "+request);
		GeonetworkRequestDecoder gd = new GeonetworkRequestDecoder(request,"");
		System.out.println(gd);
	}

	//@Test
	public void test2() throws ServletException{
		String request = "/geonetwork/gcube|devsec|devVRE#noauthentication";
		System.out.println("Testing request: "+request);
		GeonetworkRequestDecoder gd = new GeonetworkRequestDecoder(request,"");
		System.out.println(gd);
	}


	@Test
	public void test3() throws ServletException{
		String request = "/geonetwork/gcube#filterpublicids";
		System.out.println("Testing request: "+request);
		GeonetworkRequestDecoder gd = new GeonetworkRequestDecoder(request,"");
		System.out.println(gd);
	}


}
