import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;

/**
 * 
 */

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 20, 2014
 *
 */
public class UriResolverManagerMain {
	
	public static void main(String[] args) {
		try {
			ScopeProvider.instance.set("/gcube/devsec/devVRE");
			UriResolverManager resolver = new UriResolverManager("GIS");
			System.out.println(resolver.getCapabilities());
			System.out.println(resolver.getApplicationTypes());
//			System.out.println(resolver.discoveryServiceParameters(resolver.getResolver("SMP-ID")));
			
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
}
