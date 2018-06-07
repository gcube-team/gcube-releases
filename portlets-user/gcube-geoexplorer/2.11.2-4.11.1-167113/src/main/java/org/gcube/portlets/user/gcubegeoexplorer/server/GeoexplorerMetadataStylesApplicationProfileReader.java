package org.gcube.portlets.user.gcubegeoexplorer.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.gcubegeoexplorer.client.GeoexplorerMetadataStyle;
import org.gcube.portlets.user.gcubegeoexplorer.server.util.GeoexplorerGenericResourcePropertyReader;
import org.gcube.portlets.user.gcubegeoexplorer.server.util.PropertyFileNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 14, 2014
 *
 */
public class GeoexplorerMetadataStylesApplicationProfileReader {

	protected static final String RESOURCE_PROFILE_BODY_METADATA_NAME_TEXT = "/Resource/Profile/Body/MetadataStyles/MetadataStyle/name/text()";
	protected static final String RESOURCE_PROFILE_BODY_METADATA_STYLE_TEXT = "/Resource/Profile/Body/MetadataStyles/MetadataStyle/style/text()";

//	public static GCUBELog logger = new GCUBELog(GeoexplorerApplicationProfileReader.class);

	public static Logger logger = Logger.getLogger(GeoexplorerMetadataStylesApplicationProfileReader.class);

	private String secondaryType;
	private ScopeBean scope;
	private List<GeoexplorerMetadataStyle> listGeoexplorerMetadataStyles;


//	public static String SECONDARY_TYPE_GEOEXPLORER = "GeoexplorerDefaultLayersTest";

	/**
	 *
	 * @param scope - the scope to be searched
	 * @param secondaryType - the secondaryType name
	 * @throws Exception
	 */
	public GeoexplorerMetadataStylesApplicationProfileReader(ScopeBean scope) throws Exception {
		this.scope = scope;
		GeoexplorerGenericResourcePropertyReader gr = new GeoexplorerGenericResourcePropertyReader();
		this.secondaryType = gr.getMetadataStylesSecondaryType();
		this.listGeoexplorerMetadataStyles = readProfileFromInfrastrucure();
	}


	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @return the applicationProfile profile
	 * @throws Exception
	 */
	private List<GeoexplorerMetadataStyle> readProfileFromInfrastrucure() throws Exception {
		logger.trace("read secondary type: "+secondaryType);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: "+scopeString);

		List<GeoexplorerMetadataStyle> list = new ArrayList<GeoexplorerMetadataStyle>();
		String orgininalScope = ScopeProvider.instance.get();

		try {

			ScopeProvider.instance.set(scopeString);
			logger.trace("scope provider set instance: "+scopeString);

			String queryString = getGcubeGenericQueryString(secondaryType);

			logger.trace("queryString: " +queryString);

			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
		 	List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

//				System.out.println("node: "+node);
				list = getListMetadataStyleFromNode(helper);
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile "+secondaryType+" from the infrastructure, "+e);
			return list;
		}finally{
			if(orgininalScope!=null && !orgininalScope.isEmpty()){
				logger.info("Setting scope used by caller: "+orgininalScope);
				ScopeProvider.instance.set(orgininalScope);
			}else{
				logger.info("Resetting scope...");
				ScopeProvider.instance.reset();
			}
		}

		return list;

	}

	/**
	 *
	 * @param helper
	 * @return
	 * @throws ApplicationProfileNotFoundException
	 */
	private List<GeoexplorerMetadataStyle> getListMetadataStyleFromNode(XPathHelper helper) throws ApplicationProfileNotFoundException{

		List<String> metadataStyles;

		List<GeoexplorerMetadataStyle> listGeoexplorerMetadataStyle = new ArrayList<GeoexplorerMetadataStyle>();

		try {

			metadataStyles = helper.evaluate(RESOURCE_PROFILE_BODY_METADATA_STYLE_TEXT);

			if (metadataStyles != null && metadataStyles.size() > 0) {

				List<String> currValue = null;

				for (String style : metadataStyles) {
					logger.trace("found Style : "+style);

					GeoexplorerMetadataStyle geoMetadataStyle = new GeoexplorerMetadataStyle();
					geoMetadataStyle.setStyle(style);
					geoMetadataStyle.setScope(scope.toString());

//					currValue = helper.evaluate("/Resource/Profile/Body/MetadataStyles/MetadataStyle[style='"+style+"']/@display");
					currValue = helper.evaluate("/Resource/Profile/Body/MetadataStyles/MetadataStyle[style='"+style+"']/display/text()");
					logger.trace("display style?" +currValue);
					if (currValue != null && currValue.size() > 0){
						boolean display = Boolean.parseBoolean(currValue.get(0));
						geoMetadataStyle.setDisplay(display);
//						logger.trace("isBaseLayer : "+isBase);
					}


					currValue = helper.evaluate("/Resource/Profile/Body/MetadataStyles/MetadataStyle[style='"+style+"']/name/text()");
					String name = "";
					if (currValue != null && currValue.size() > 0) {
						name = currValue.get(0);
						geoMetadataStyle.setName(name);
//						logger.trace("name: "+descr+" for UUID: "+uuid);
					}

					logger.trace("Filled object: "+geoMetadataStyle);
					listGeoexplorerMetadataStyle.add(geoMetadataStyle);
				}

			}

			else throw new ApplicationProfileNotFoundException("Your applicationProfile with scope "+scope.toString()+" is wrong, consider adding <MetadataStyle><STYLE> element in <Body>");

		} catch (Exception e) {
			logger.error("An error occurred in getListMetadataStyleFromNode ", e);
			return listGeoexplorerMetadataStyle;
		}

		return listGeoexplorerMetadataStyle;
	}




	public synchronized String getGcubeGenericQueryString(String secondaryType){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource" +
				" where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"'" +
				" return $profile";
	}

	public String getSecondaryType() {
		return secondaryType;
	}

	public ScopeBean getScope() {
		return scope;
	}

	public List<GeoexplorerMetadataStyle> getListGeoexplorerMetadataStyles() {
		return listGeoexplorerMetadataStyles;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoexplorerMetadataStylesApplicationProfileReader [secondaryType=");
		builder.append(secondaryType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", listGeoexplorerMetadataStyles=");
		builder.append(listGeoexplorerMetadataStyles);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) throws InterruptedException, PropertyFileNotFoundException {

		String scopeString = "/gcube/devsec/devVRE";

		final ScopeBean scope  = new ScopeBean(scopeString);

		GeoexplorerMetadataStylesApplicationProfileReader reader;
		try {
			reader = new GeoexplorerMetadataStylesApplicationProfileReader(scope);
			System.out.println(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
