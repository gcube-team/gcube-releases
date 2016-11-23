import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver;
import org.gcube.datatransfer.resolver.gis.geonetwork.HTTPCallsUtils;


/**
 *
 */
/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 8, 2016
 */
public class GeonetworkResolverTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		/*String scopeValue ="/gcube/devsec/devVRE";
		String remainValue = "/srv/en/mef.export";
		String queryString = "scope=/gcube/devsec/devVRE&remainPath=/srv/en/mef.export&version=2.0.2&request=GetCapabilities&service=CSW";
		ServerParameters geonetworkParams = new ServerParameters("http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork", "", "");

		String newQueryString = purgeScopeFromQueryString(scopeValue, queryString);
		logger.info("Purged query string from "+scopeValue+" is: "+newQueryString);

		String baseURL = remainValue==null ||remainValue.isEmpty()?geonetworkParams.getUrl()+"/"+CSW_SERVER:geonetworkParams.getUrl()+"/"+CSW_SERVER+remainValue;
		logger.info("New base URL "+baseURL);
		newQueryString = purgeRemainFromQueryString(remainValue, newQueryString);
		logger.info("Purged query string from "+remainValue+" is: "+newQueryString);

		String gnGetlURL = newQueryString==null || newQueryString.isEmpty()? baseURL : baseURL+"?"+newQueryString;
		logger.info("Sending get request to URL: "+gnGetlURL);*/

		HTTPCallsUtils httpUtils = new HTTPCallsUtils();
		String data ="";

		String contentType = "application/x-www-form-urlencoded";
		String uuid = "fao-fsa-map-27.7.j";
		String gnCSWlURL = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/srv/en//srv/en/mef.export";

		try {
			/*File file = File.createTempFile(uuid, ".xml");
			InputStream response = httpUtils.post(gnCSWlURL, file, contentType);*/

			//MAP
			Map<String, String[]> map = new HashMap<String, String[]>();
			String[] value = new String[1];
			value[0]=uuid;
			map.put(GeonetworkResolver.UUID, value);
//			data = "<request><uuid>"+uuid+"</uuid></request>";
			data = "uuid="+uuid;
			byte[] byteArray = data.getBytes();
			InputStream response = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray), contentType, map);

			if(response!=null){
				try {
					final Path destination = Paths.get("test");
					Files.copy(response, destination);
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
