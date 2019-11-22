package org.gcube.portlets.user.workspace.server.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * The Class ApplicationProfileReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 14, 2016
 */
public class ApplicationProfileReader {

	/**
	 *
	 */
	public static final String SECONDARY_TYPE = "ApplicationProfile";
	public static final String WORKSPACE_EXPLORER_APP_NAME = "Workspace-Explorer-App";

	protected static final String RESOURCE_PROFILE_BODY_END_POINT_URL_TEXT = "/Resource/Profile/Body/EndPoint/URL/text()";
	protected static final String RESOURCE_PROFILE_BODY_END_POINT_SCOPE_TEXT = "/Resource/Profile/Body/EndPoint/Scope/text()";
	protected static final String RESOURCE_PROFILE_BODY_TEXT = "/Resource/Profile/Body/text()";
	protected static final String RESOURCE_PROFILE_BODY_THUMBNAIL_URL_TEXT = "/Resource/Profile/Body/ThumbnailURL/text()";
	protected static final String RESOURCE_PROFILE_BODY_APP_ID_TEXT = "/Resource/Profile/Body/AppId/text()";
	protected static final String RESOURCE_PROFILE_DESCRIPTION_TEXT = "/Resource/Profile/Description/text()";
	protected static final String RESOURCE_PROFILE_NAME_TEXT = "/Resource/Profile/Name/text()";

	private Logger logger = LoggerFactory.getLogger(ApplicationProfileReader.class);
	private String secondaryType;
	private String scope;
	private String resourceName;
	private String appID;


	/**
	 * Instantiates a new application profile reader.
	 *
	 * @param resourceName the resource name
	 * @param appID the app id
	 * @throws Exception the exception
	 */
	public ApplicationProfileReader(String resourceName, String appID) throws Exception {

		this.resourceName = resourceName;
		this.appID = appID;
		this.secondaryType = SECONDARY_TYPE;
		this.scope = ScopeProvider.instance.get();
	}


	/**
	 * this method looks up the generic resource among the ones available in the infrastructure using scope provider {@link  ScopeProvider.instance.get()}
	 * resource name {@value #WORKSPACE_EXPLORER_APP_NAME} and secondaryType {@value #SECONDARY_TYPE}
	 *
	 * @return the applicationProfile profile
	 */
	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure.
	 *
	 * @return the applicationProfile profile
	 */
	public ApplicationProfile readProfileFromInfrastrucure() {

			ApplicationProfile appProf = new ApplicationProfile();
			String queryString = getGcubeGenericQueryString(secondaryType, appID);

			try {

				if(scope==null)
					throw new Exception("Scope is null, set scope into ScopeProvider");

				logger.info("Trying to fetch ApplicationProfile in the scope: "+scope+", SecondaryType: " + secondaryType + ", AppId: " +  appID);
				Query q = new QueryBox(queryString);
				DiscoveryClient<String> client = client();
			 	List<String> appProfile = client.submit(q);

				if (appProfile == null || appProfile.size() == 0)
					throw new ApplicationProfileNotFoundException("ApplicationProfile with SecondaryType: " + secondaryType + ", AppId: " +  appID +" is not registered in the scope: "+scope);
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

						//int slashCount = StringUtils.countMatches(currentScope, "/");
						
						boolean isVRE = WsUtil.isVRE(currentScope);

						//if(slashCount < 3){//CASE not VRE - set session scope
						if(!isVRE){//CASE not VRE - set session scope
							logger.info("Scope "+ scope.toString() + " is not a VRE");

							List<String> listSessionScope = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/Sessionscope/text()"); //get session scope of i+1-mo scope
							logger.debug("ListSessionScope is: "+ listSessionScope.toString());

							if(listSessionScope!=null && listSessionScope.size()>0){ //If sessions scope exists

								logger.debug("setting session scope "+ listSessionScope.get(0));
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

				logger.debug("returning: "+appProf);
				return appProf;
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		}finally{
			/*
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}*/
		}

	}

	/**
	 * Gets the gcube generic query string.
	 *
	 * @param secondaryType the secondary type
	 * @param appId the app id
	 * @return the gcube generic query string
	 */
	public static String getGcubeGenericQueryString(String secondaryType, String appId){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
				"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' and  $profile/Profile/Body/AppId/string() " +
				" eq '" + appId + "'" +
				"return $profile";
	}

	/**
	 * Gets the secondary type.
	 *
	 * @return the secondary type
	 */
	public String getSecondaryType() {
		return secondaryType;
	}


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Gets the resource name.
	 *
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationProfileReader [secondaryType=");
		builder.append(secondaryType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", appID=");
		builder.append(appID);
		builder.append("]");
		return builder.toString();
	}


//	public static void main(String[] args) {
//
//		try {
//			ScopeProvider.instance.set("/gcube");
//			ApplicationProfileReader ap = new ApplicationProfileReader("Workspace-Explorer-App", "org.gcube.portlets.user.workspaceexplorerapp.server.WorkspaceExplorerAppServiceImpl");
//			System.out.println(ap.readProfileFromInfrastrucure());
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}

}
