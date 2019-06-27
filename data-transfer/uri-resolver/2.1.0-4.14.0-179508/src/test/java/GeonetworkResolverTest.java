//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver;
//import org.gcube.datatransfer.resolver.gis.geonetwork.HTTPCallsUtils;
//import org.gcube.datatransfer.resolver.gis.util.GetResponseRecordFilter;
//import org.gcube.spatial.data.geonetwork.GeoNetwork;
//import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
//import org.gcube.spatial.data.geonetwork.LoginLevel;
//import org.gcube.spatial.data.geonetwork.configuration.Configuration;
//import org.gcube.spatial.data.geonetwork.model.Account;
//import org.gcube.spatial.data.geonetwork.model.Account.Type;
//
//
///**
// *
// */
///**
// *
// * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
// * Sep 8, 2016
// */
//public class GeonetworkResolverTest {
//
//	private static String scope = "/d4science.research-infrastructures.eu";
//
//	private static LoginLevel loginLevel = LoginLevel.SCOPE;
//
//	private static Type accountType = Type.CKAN;
//
//	/**
//	 * The main method.
//	 *
//	 * @param args the arguments
//	 * @throws UnsupportedEncodingException the unsupported encoding exception
//	 */
//	public static void main(String[] args) throws UnsupportedEncodingException {
//
//		/*String scopeValue ="/gcube/devsec/devVRE";
//		String remainValue = "/srv/en/mef.export";
//		String queryString = "scope=/gcube/devsec/devVRE&remainPath=/srv/en/mef.export&version=2.0.2&request=GetCapabilities&service=CSW";
//		ServerParameters geonetworkParams = new ServerParameters("http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork", "", "");
//
//		String newQueryString = purgeScopeFromQueryString(scopeValue, queryString);
//		logger.info("Purged query string from "+scopeValue+" is: "+newQueryString);
//
//		String baseURL = remainValue==null ||remainValue.isEmpty()?geonetworkParams.getUrl()+"/"+CSW_SERVER:geonetworkParams.getUrl()+"/"+CSW_SERVER+remainValue;
//		logger.info("New base URL "+baseURL);
//		newQueryString = purgeRemainFromQueryString(remainValue, newQueryString);
//		logger.info("Purged query string from "+remainValue+" is: "+newQueryString);
//
//		String gnGetlURL = newQueryString==null || newQueryString.isEmpty()? baseURL : baseURL+"?"+newQueryString;
//		logger.info("Sending get request to URL: "+gnGetlURL);*/
//
//		try{
//			HTTPCallsUtils httpUtils = new HTTPCallsUtils();
//			String data ="";
//
//			ScopeProvider.instance.set(scope);
//			GeoNetworkPublisher reader=GeoNetwork.get();
//
//
//			Configuration config = reader.getConfiguration();
//			Account account=config.getScopeConfiguration().getAccounts().get(accountType);
//			reader.login(loginLevel);
//
//			String contentType = "application/xml ; charset=\"UTF-8\"";
//			String uuid = "c15ae8e5-71c0-4b8b-aa29-304cc4e97238";
//			String gnCSWlURL = "http://geonetwork.d4science.org/geonetwork/srv/en/csw";
////			String gnCSWlURL = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork/srv/en//srv/en/mef.export";
//
//			/*File file = File.createTempFile(uuid, ".xml");
//			InputStream response = httpUtils.post(gnCSWlURL, file, contentType);*/
//
//			//MAP
//			Map<String, String[]> map = new HashMap<String, String[]>();
//			String[] value = new String[1];
//			value[0]=uuid;
//			map.put(GeonetworkResolver.UUID, value);
////			data = "<request><uuid>"+uuid+"</uuid></request>";
//
//			data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//				"<csw:GetRecordById xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\" version=\"2.0.2\" " +
//				"outputSchema=\"csw:IsoRecord\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2/CSW-discovery.xsd\" xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" " +
//				"xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dct=\"http://purl.org/dc/terms/\" xmlns:gml=\"http://www.opengis.net/gml\" " +
//				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
//				"<csw:Id>"+uuid+"</csw:Id>" +
//				"<ElementSetName typeNames=\"csw:Record\">full</ElementSetName>" +
//				"</csw:GetRecordById>";
//
//			byte[] byteArray = data.getBytes();
//			InputStream response = httpUtils.post(gnCSWlURL, new ByteArrayInputStream(byteArray), contentType, map);
//
////			String respToString = IOUtils.toString(response);
////			System.out.println("Response returned by request: \n"+respToString);
////			InputStream responseToIs = IOUtils.toInputStream(respToString);
//			if(response!=null){
//				try {
//
//					InputStream re = GetResponseRecordFilter.overrideResponseIdsByListIds(response, new ArrayList<String>(), "Replaced UUID");
////					String theString = IOUtils.toString(re);
////					System.out.println("Response returned after overriding: \n"+theString);
//
//					final Path destination = Paths.get("test.xml");
//					Files.copy(re, destination);
//				}
//				catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//	}
//}
