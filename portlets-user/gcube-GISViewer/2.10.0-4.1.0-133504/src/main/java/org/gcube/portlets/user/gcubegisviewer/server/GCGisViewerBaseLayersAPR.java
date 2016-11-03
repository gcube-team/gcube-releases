package org.gcube.portlets.user.gcubegisviewer.server;

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
import org.gcube.portlets.user.gcubegisviewer.client.GisViewerBaseLayer;
import org.gcube.portlets.user.gcubegisviewer.server.util.GisViewerGenericResourcePropertyReader;
import org.gcube.portlets.user.gcubegisviewer.server.util.PropertyFileNotFoundException;
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
 *      Gcube GisViewer Application Profile Reader
 */
public class GCGisViewerBaseLayersAPR {

	protected static final String RESOURCE_PROFILE_BODY_METADATA_NAME_TEXT = "/Resource/Profile/Body/BaseLayers/BaseLayer/Name/text()";
	protected static final String RESOURCE_PROFILE_BODY_METADATA_TITLE_TEXT = "/Resource/Profile/Body/BaseLayers/BaseLayer/Title/text()";

	// public static GCUBELog logger = new
	// GCUBELog(GeoexplorerApplicationProfileReader.class);

	public static Logger logger = Logger.getLogger(GCGisViewerBaseLayersAPR.class);

	private String secondaryType;
	private ScopeBean scope;
	private List<GisViewerBaseLayer> listGisViewerBaseLayer;

	// public static String SECONDARY_TYPE_GEOEXPLORER =
	// "GeoexplorerDefaultLayersTest";

	/**
	 * 
	 * @param scope
	 *            - the scope to be searched
	 * @param secondaryType
	 *            - the secondaryType name
	 * @throws Exception
	 */
	public GCGisViewerBaseLayersAPR(ScopeBean scope) throws Exception {
		this.scope = scope;
		GisViewerGenericResourcePropertyReader gr = new GisViewerGenericResourcePropertyReader();
		this.secondaryType = gr.getBaseLayersSecondaryType();
		this.listGisViewerBaseLayer = readProfileFromInfrastrucure();
	}

	/**
	 * this method looks up the applicationProfile profile among the ones
	 * available in the infrastructure
	 * 
	 * @param portletClassName
	 *            your servlet class name will be used ad unique identifier for
	 *            your applicationProfile
	 * @return the applicationProfile profile
	 * @throws Exception
	 */
	private List<GisViewerBaseLayer> readProfileFromInfrastrucure()
			throws Exception {
		logger.trace("read secondary type: " + secondaryType);

		if (this.scope == null)
			throw new Exception("Scope is null");

		String scopeString = this.scope.toString();
		logger.trace("read scope: " + scopeString);

		List<GisViewerBaseLayer> list = new ArrayList<GisViewerBaseLayer>();

		try {

			ScopeProvider.instance.set(scopeString);
			logger.trace("scope provider set instance: " + scopeString);

			String queryString = getGcubeGenericQueryString(secondaryType);

			logger.trace("queryString: " + queryString);

			Query q = new QueryBox(queryString);

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0)
				throw new ApplicationProfileNotFoundException(
						"Your applicationProfile is not registered in the infrastructure");
			else {
				String elem = appProfile.get(0);
				DocumentBuilder docBuilder = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(
						new InputSource(new StringReader(elem)))
						.getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				// System.out.println("node: "+node);
				list = getListBaseLayersFromNode(helper);
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile "
					+ secondaryType + " from the infrastructure, " + e);
			return list;
		}

		return list;

	}

	/**
	 * 
	 * @param helper
	 * @return
	 * @throws ApplicationProfileNotFoundException
	 */
	private List<GisViewerBaseLayer> getListBaseLayersFromNode(
			XPathHelper helper) throws ApplicationProfileNotFoundException {

		List<String> metadataTitles;

		List<GisViewerBaseLayer> listGeoexplorerMetadataStyle = new ArrayList<GisViewerBaseLayer>();

		try {

			metadataTitles = helper
					.evaluate(RESOURCE_PROFILE_BODY_METADATA_TITLE_TEXT);

			if (metadataTitles != null && metadataTitles.size() > 0) {

				List<String> currValue = null;

				for (String title : metadataTitles) {
					logger.trace("found Title : " + title);

					GisViewerBaseLayer gisViewerBaseLayer = new GisViewerBaseLayer();
					gisViewerBaseLayer.setTitle(title);
					gisViewerBaseLayer.setScope(scope.toString());


					currValue = helper.evaluate("/Resource/Profile/Body/BaseLayers/BaseLayer[Title='"+ title + "']/Display/text()");
					logger.trace("display layer?" + currValue);
					if (currValue != null && currValue.size() > 0) {
						boolean display = Boolean.parseBoolean(currValue.get(0));
						gisViewerBaseLayer.setDisplay(display);
						// logger.trace("isBaseLayer : "+isBase);
					}

					currValue = helper.evaluate("/Resource/Profile/Body/BaseLayers/BaseLayer[Title='"+ title + "']/Name/text()");
					String name = "";
					if (currValue != null && currValue.size() > 0) {
						name = currValue.get(0);
						gisViewerBaseLayer.setName(name);
						// logger.trace("name: "+descr+" for UUID: "+uuid);
					}
					
					currValue = helper.evaluate("/Resource/Profile/Body/BaseLayers/BaseLayer[Title='"+ title + "']/WmsUrl/text()");
					String wmsUrl = "";
					if (currValue != null && currValue.size() > 0) {
						wmsUrl = currValue.get(0);
						gisViewerBaseLayer.setWmsURL(wmsUrl);
						// logger.trace("name: "+descr+" for UUID: "+uuid);
					}

					logger.trace("Filled object: " + gisViewerBaseLayer);
					listGeoexplorerMetadataStyle.add(gisViewerBaseLayer);
				}

			}

			else
				throw new ApplicationProfileNotFoundException(
						"Your applicationProfile with scope "
								+ scope.toString()
								+ " is wrong, consider adding <MetadataStyle><STYLE> element in <Body>");

		} catch (Exception e) {
			logger.error("An error occurred in getListMetadataStyleFromNode ",
					e);
			return listGeoexplorerMetadataStyle;
		}

		return listGeoexplorerMetadataStyle;
	}

	public synchronized String getGcubeGenericQueryString(String secondaryType) {

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource"
				+ " where $profile/Profile/SecondaryType/string() eq '"
				+ secondaryType + "'" + " return $profile";
	}

	public String getSecondaryType() {
		return secondaryType;
	}

	public ScopeBean getScope() {
		return scope;
	}

	public List<GisViewerBaseLayer> getListGisViewerBaseLayer() {
		return listGisViewerBaseLayer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GCGisViewerBaseLayersAPR [secondaryType=");
		builder.append(secondaryType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", listGisViewerBaseLayer=");
		builder.append(listGisViewerBaseLayer);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) throws InterruptedException, PropertyFileNotFoundException {

		String scopeString = "/gcube/devsec/devVRE";

		final ScopeBean scope = new ScopeBean(scopeString);

		GCGisViewerBaseLayersAPR reader;
		try {
			reader = new GCGisViewerBaseLayersAPR(scope);
			System.out.println(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
