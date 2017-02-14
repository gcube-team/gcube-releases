package org.gcube.datatransfer.resolver.applicationprofile;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.applicationprofile.GcubeQuery.FIELD_TYPE;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class ApplicationProfileReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 6, 2016
 */
public class ApplicationProfileReader {

	protected static final String RESOURCE_PROFILE_BODY_END_POINT_URL_TEXT = "/Resource/Profile/Body/EndPoint/URL/text()";
	protected static final String RESOURCE_PROFILE_BODY_END_POINT_SCOPE_TEXT = "/Resource/Profile/Body/EndPoint/Scope/text()";
	protected static final String RESOURCE_PROFILE_BODY_TEXT = "/Resource/Profile/Body/text()";
	protected static final String RESOURCE_PROFILE_BODY_THUMBNAIL_URL_TEXT = "/Resource/Profile/Body/ThumbnailURL/text()";
	protected static final String RESOURCE_PROFILE_BODY_APP_ID_TEXT = "/Resource/Profile/Body/AppId/text()";
	protected static final String RESOURCE_PROFILE_DESCRIPTION_TEXT = "/Resource/Profile/Description/text()";
	protected static final String RESOURCE_PROFILE_NAME_TEXT = "/Resource/Profile/Name/text()";

	private Logger logger = Logger.getLogger(ApplicationProfileReader.class);
	private String secondaryType;
	private String appId;
	private String scope;
	private ApplicationProfile applicationProfile;
	private boolean useRootScope = false;


	/**
	 * Instantiates a new application profile reader.
	 *
	 * @param scope - the scope where to search the Application Profile
	 * @param secondaryType the secondary type of Application Profile
	 * @param portletClassName - the AppId of Generic Resource
	 * @param useRootScope the use root scope, if true the root scope is used to discovery the Application Profile, otherwise scope is used
	 */
	public ApplicationProfileReader(String scope, String secondaryType, String portletClassName, boolean useRootScope) {
		this.scope = scope;
		this.secondaryType = secondaryType;
		this.appId = portletClassName;
		this.useRootScope = useRootScope;
		this.applicationProfile = readProfileFromInfrastrucure();
	}

	/**
	 * Gets the application profile.
	 *
	 * @return the application profile
	 */
	public ApplicationProfile getApplicationProfile() {
		return applicationProfile;
	}

	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure.
	 *
	 * @return the applicationProfile profile
	 */
	private ApplicationProfile readProfileFromInfrastrucure() {

			ApplicationProfile appProf = new ApplicationProfile();
			String queryString = GcubeQuery.getGcubeGenericResource(secondaryType, FIELD_TYPE.APP_ID, appId);
			String originalScope = null;

			try {

				originalScope = ScopeProvider.instance.get();
				String discoveryScope = useRootScope?ScopeUtil.getInfrastructureNameFromScope(scope):scope;
				ScopeProvider.instance.set(discoveryScope);
				logger.info("Trying to fetch ApplicationProfile in the infra scope: "+discoveryScope+", SecondaryType: " + secondaryType + ", AppId: " +  appId);
				Query q = new QueryBox(queryString);
				DiscoveryClient<String> client = client();
			 	List<String> appProfile = client.submit(q);

				if (appProfile == null || appProfile.size() == 0)
					throw new ApplicationProfileNotFoundException("ApplicationProfile with SecondaryType: " + secondaryType + ", AppId: " +  appId +" is not registered in the infra scope: "+discoveryScope);
				else {
					String elem = appProfile.get(0);
					DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
					XPathHelper helper = new XPathHelper(node);

					List<String> currValue = null;
					currValue = helper.evaluate(RESOURCE_PROFILE_NAME_TEXT);
					if (currValue != null && currValue.size() > 0) {
						appProf.setName(currValue.get(0));
					}
					else throw new ApplicationProfileNotFoundException("Your applicationProfile NAME was not found in the profile");

					currValue = helper.evaluate(RESOURCE_PROFILE_DESCRIPTION_TEXT);
					if (currValue != null && currValue.size() > 0) {
						appProf.setDescription(currValue.get(0));
					}
					else logger.warn("No Description exists for " + appProf.getName());

					currValue = helper.evaluate(RESOURCE_PROFILE_BODY_APP_ID_TEXT);
					if (currValue != null && currValue.size() > 0) {
						appProf.setKey(currValue.get(0));
					}
					else throw new ApplicationProfileNotFoundException("Your applicationProfile ID n was not found in the profile, consider adding <AppId> element in <Body>");

					currValue = helper.evaluate(RESOURCE_PROFILE_BODY_THUMBNAIL_URL_TEXT);
					if (currValue != null && currValue.size() > 0) {
						appProf.setImageUrl(currValue.get(0));
					}
					else{
						logger.warn("Null or empty <ThumbnailURL> element in <Body>" + appProf.getName());
					}

					currValue = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/Scope/text()");

					if (currValue != null && currValue.size() > 0) {
						List<String> scopes = currValue;
						String currentScope = scopes.get(0);

						int slashCount = StringUtils.countMatches(currentScope, "/");

						if(slashCount < 3){//CASE not VRE - set session scope
							logger.info("Scope "+ scope.toString() + " is not a VRE");

							List<String> listSessionScope = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/Sessionscope/text()"); //get session scope of i+1-mo scope

							if(listSessionScope!=null && listSessionScope.size()>0){ //If sessions scope exists

								logger.trace("setting session scope "+ listSessionScope.get(0));
								appProf.setScope(listSessionScope.get(0));
							}
							else{
								logger.trace("session scope not exists setting scope "+ scope.toString());
								appProf.setScope(scope.toString());
							}
						}
						else{ //CASE IS A VRE
							logger.info("Scope "+ scope.toString() + " is a VRE");
							appProf.setScope(scope.toString());

						}

						//RETRIEVE URL
						currValue = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/URL/text()");

						if (currValue != null && currValue.size() > 0) {
							String url = currValue.get(0);
	//						System.out.println("URL "+url);
							if(url!=null)
								appProf.setUrl(url);
							else
								throw new ApplicationProfileNotFoundException("Your applicationProfile URL was not found in the profile for Scope: " + scope.toString());
						}
						else throw new ApplicationProfileNotFoundException("Your applicationProfile URL was not found in the profile for Scope: " + scope.toString());

					}
					else throw new ApplicationProfileNotFoundException("Your applicationProfile with scope "+scope.toString()+" was not found in the profile, consider adding <EndPoint><Scope> element in <Body>");

				return appProf;
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		}finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationProfileReader [secondaryType=");
		builder.append(secondaryType);
		builder.append(", appId=");
		builder.append(appId);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", applicationProfile=");
		builder.append(applicationProfile);
		builder.append("]");
		return builder.toString();
	}

	/*
	public static void main(String[] args) {

		String portletClassName = "org.gcube.portlets.user.gisviewerapp.server.GisViewerAppServiceImpl";
		String scope ="/gcube";
		String secondaryType = "ApplicationProfile";
		ApplicationProfileReader reader = new ApplicationProfileReader(scope, secondaryType, portletClassName);

		System.out.println(reader);

	}*/

}
