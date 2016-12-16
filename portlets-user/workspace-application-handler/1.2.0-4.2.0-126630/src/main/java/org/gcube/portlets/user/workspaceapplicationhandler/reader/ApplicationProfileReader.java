package org.gcube.portlets.user.workspaceapplicationhandler.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspaceapplicationhandler.entity.ApplicationProfile;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.ApplicationProfileNotFoundException;
import org.gcube.portlets.user.workspaceapplicationhandler.util.ScopeUtil;
import org.gcube.portlets.user.workspaceapplicationhandler.util.Util;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
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
	private ScopeBean scope;
	private ApplicationProfile applicationProfile;
	

	/**
	 * 
	 * @param scope - the scope to be searched
	 * @param genericResource - the name of generic resource
	 * @param portletClassName - the AppId of generic resource
	 */
	public ApplicationProfileReader(ScopeBean scope, String secondaryType, String portletClassName) { 
		this.scope = scope;
		this.secondaryType = secondaryType;
		this.appId = portletClassName;
		this.applicationProfile = readProfileFromInfrastrucure();	
	}

	public ApplicationProfile getApplicationProfile() {
		return applicationProfile;
	}

	/**
	 * this method looks up the applicationProfile profile among the ones available in the infrastructure
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @return the applicationProfile profile
	 */
	private ApplicationProfile readProfileFromInfrastrucure() {

			ApplicationProfile appProf = new ApplicationProfile();
			String queryString = Util.getGcubeGenericQueryString(secondaryType, appId);

//			ScopeBean scopeBean =  new ScopeBean(scope.toString());
			logger.trace("Trying to fetch applicationProfile profile from the infrastructure for " + secondaryType + " scope: " +  scope);
			
			try {
				
				String infra = ScopeUtil.getInfrastructuresNameFromScopeBean(this.scope);
				ScopeProvider.instance.set(infra);
				logger.trace("scope provider set instance: "+infra);

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
					else throw new ApplicationProfileNotFoundException("Your applicationProfile Image Url was not found in the profile, consider adding <ThumbnailURL> element in <Body>");
	
					
					currValue = helper.evaluate("/Resource/Profile/Body/EndPoint[Scope='"+scope.toString()+"']/Scope/text()");
				
					if (currValue != null && currValue.size() > 0) {
						List<String> scopes = currValue;
						String currentScope = scopes.get(0);
						
						int slashCount = StringUtils.countMatches(currentScope, "/");
	
						if(slashCount < 3){//CASE not VRE - set session scope
							logger.trace("Scope "+ scope.toString() + " is not a VRE");
	
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
							logger.trace("Scope "+ scope.toString() + " is a VRE");
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
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure");
			e.printStackTrace();
			return null;
		}

	}
}
