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
import org.gcube.portlets.user.gcubegeoexplorer.server.entity.GeoexplorerDefaultLayer;
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
 * @Jul 26, 2013
 *
 */
public class GeoexplorerDefaultLayersApplicationProfileReader {

	protected static final String RESOURCE_PROFILE_BODY_DEFAULTLAYERS_URL_TEXT = "/Resource/Profile/Body/DefaultLayers/DefaultLayer/Name/text()";
	protected static final String RESOURCE_PROFILE_BODY_DEFAULTLAYERS__DEFAULTLAYER_URL_TEXT = "/Resource/Profile/Body/DefaultLayers/DefaultLayer/Description/text()";

//	public static GCUBELog logger = new GCUBELog(GeoexplorerApplicationProfileReader.class);

	public static Logger logger = Logger.getLogger(GeoexplorerDefaultLayersApplicationProfileReader.class);

	private String secondaryType;
	private ScopeBean scope;
	private List<GeoexplorerDefaultLayer> listGeoexplorerDefaultLayer;


//	public static String SECONDARY_TYPE_GEOEXPLORER = "GeoexplorerDefaultLayersTest";

	/**
	 *
	 * @param scope - the scope to be searched
	 * @param secondaryType - the secondaryType name
	 * @throws Exception
	 */
	public GeoexplorerDefaultLayersApplicationProfileReader(ScopeBean scope) throws Exception {
		this.scope = scope;
		GeoexplorerGenericResourcePropertyReader gr = new GeoexplorerGenericResourcePropertyReader();
		this.secondaryType = gr.getDefaultLayersSecondaryType();
		this.listGeoexplorerDefaultLayer = readProfileFromInfrastrucure();
	}


	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @return the applicationProfile profile
	 * @throws Exception
	 */
	private List<GeoexplorerDefaultLayer> readProfileFromInfrastrucure() throws Exception {
		logger.trace("read secondary type: "+secondaryType);

		if(this.scope==null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.info("ScopeBean passed is: "+scopeString);

		String orgininalScope = ScopeProvider.instance.get();

		List<GeoexplorerDefaultLayer> list = new ArrayList<GeoexplorerDefaultLayer>();

		try {

			ScopeProvider.instance.set(scopeString);
			logger.info("scope provider set instance: "+scopeString);

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
				list = getListDefaultLayerFromNode(helper);
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
	private List<GeoexplorerDefaultLayer> getListDefaultLayerFromNode(XPathHelper helper) throws ApplicationProfileNotFoundException{

		List<String> defaultLayersUUID;

		List<GeoexplorerDefaultLayer> listGeoexplorerDefaultLayer = new ArrayList<GeoexplorerDefaultLayer>();

		try {

			defaultLayersUUID = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer/UUID/text()");

			if (defaultLayersUUID != null && defaultLayersUUID.size() > 0) {

				List<String> currValue = null;

				for (String uuid : defaultLayersUUID) {
					logger.trace("found UUID : "+uuid);
					GeoexplorerDefaultLayer geoDefLayer = new GeoexplorerDefaultLayer();
					geoDefLayer.setUUID(uuid);
					geoDefLayer.setScope(scope.toString());

//					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/@isBaseLayer");
					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/IsBaseLayer/text()");

					logger.trace("is base layer?" +currValue);
					if (currValue != null && currValue.size() > 0){
						boolean isBase = Boolean.parseBoolean(currValue.get(0));
						geoDefLayer.setBaseLayer(isBase);
//						logger.trace("isBaseLayer : "+isBase);
					}

					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/Description/text()");

					String descr = "";
					if (currValue != null && currValue.size() > 0) {
						descr = currValue.get(0);
						geoDefLayer.setDescription(descr);
//						logger.trace("description: "+descr+" for UUID: "+uuid);
					}

					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/Name/text()");
					descr = "";
					if (currValue != null && currValue.size() > 0) {
						descr = currValue.get(0);
						geoDefLayer.setName(descr);
//						logger.trace("name: "+descr+" for UUID: "+uuid);
					}

					logger.trace("Filled object: "+geoDefLayer);
					listGeoexplorerDefaultLayer.add(geoDefLayer);
				}

			}

			else throw new ApplicationProfileNotFoundException("Your applicationProfile with scope "+scope.toString()+" is wrong, consider adding <DefaultLayer><UUID> element in <Body>");

		} catch (Exception e) {
			logger.error("An error occurred in getListDefaultLayerFromNode ", e);
			return listGeoexplorerDefaultLayer;
		}

		return listGeoexplorerDefaultLayer;
	}

	/**
	 *
	 * @param helper
	 * @param isBaseLayer
	 * @return
	 * @throws ApplicationProfileNotFoundException
	 */
	public List<GeoexplorerDefaultLayer> getListDefaultLayerFromNode(XPathHelper helper, boolean isBaseLayer) throws ApplicationProfileNotFoundException{

		List<String> defaultLayersUUID;

		List<GeoexplorerDefaultLayer> listGeoexplorerDefaultLayer = new ArrayList<GeoexplorerDefaultLayer>();

		try {

			defaultLayersUUID = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[@isBaseLayer=\""+isBaseLayer+"\"]/UUID/text()");

			if (defaultLayersUUID != null && defaultLayersUUID.size() > 0) {

				List<String> currValue = null;

				//FOR EACH UUID
				for (String uuid : defaultLayersUUID) {
					logger.trace("found UUID : "+uuid);

					GeoexplorerDefaultLayer geoDefLayer = new GeoexplorerDefaultLayer();
					geoDefLayer.setUUID(uuid);
					geoDefLayer.setBaseLayer(isBaseLayer);
					geoDefLayer.setScope(scope.toString());

					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/Description/text()");

					String descr = "";

					if (currValue != null && currValue.size() > 0) {
						descr = currValue.get(0);
						geoDefLayer.setDescription(descr);
//						logger.trace("found description: "+descr+" for UUID: "+uuid);
					}


					currValue = helper.evaluate("/Resource/Profile/Body/DefaultLayers/DefaultLayer[UUID='"+uuid+"']/Name/text()");

					descr = "";

					if (currValue != null && currValue.size() > 0) {
						descr = currValue.get(0);
						geoDefLayer.setName(descr);
//						logger.trace("found name: "+descr+" for UUID: "+uuid);
					}

					logger.trace("Filled object: "+geoDefLayer);
					listGeoexplorerDefaultLayer.add(geoDefLayer);
	//				appProf.putLayer(layerName, descr);
				}

			}

			else throw new ApplicationProfileNotFoundException("Your applicationProfile with scope "+scope.toString()+" is wrong, consider adding <DefaultLayer><UUID> element in <Body>");

		} catch (Exception e) {
			logger.error("An error occurred in getListDefaultLayerFromNode ", e);
			return listGeoexplorerDefaultLayer;
		}

		return listGeoexplorerDefaultLayer;
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

	public List<GeoexplorerDefaultLayer> getListGeoexplorerDefaultLayer() {
		return listGeoexplorerDefaultLayer;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoexplorerApplicationProfileReader [secondaryType=");
		builder.append(secondaryType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", listGeoexplorerDefaultLayer=");
		builder.append(listGeoexplorerDefaultLayer);
		builder.append("]");
		return builder.toString();
	}


	public static void main(String[] args) throws InterruptedException, PropertyFileNotFoundException {

		String scopeString = "/gcube/devsec/devVRE";
		final ScopeBean scope  = new ScopeBean(scopeString);
		GeoexplorerDefaultLayersApplicationProfileReader reader;
		try {
			reader = new GeoexplorerDefaultLayersApplicationProfileReader(scope);
			System.out.println(reader);

//			String value = "true";
//			if (value != null){
//				boolean isBase = Boolean.parseBoolean(value);
//				System.out.println(isBase);
////				logger.trace("isBaseLayer : "+isBase);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
