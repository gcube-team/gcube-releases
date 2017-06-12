package org.gcube.portlets.user.workspaceapplicationhandler.reader;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.Serializable;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspaceapplicationhandler.entity.GcubeApplication;
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
public class GcubeApplicationReader implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5254152784210626995L;
	
	protected static final String APPLICATION_PROFILE = "ApplicationProfile";
	protected static final String RESOURCE_PROFILE_BODY_APP_ID_TEXT = "/Resource/Profile/Body/AppId/text()";
	protected static final String RESOURCE_PROFILE_NAME_TEXT = "/Resource/Profile/Name/text()";
	
	
	private Logger logger = Logger.getLogger(GcubeApplicationReader.class);
	private String secondaryType;
	private String appId;
	private ScopeBean scope;
	private GcubeApplication gcubeApplication;
	private String type;
	

	/**
	 * @param type - the FolderItemType to be searched
	 * @param scope - the scope to be searched
	 * @param genericResource - the name of generic resource
	 * @param portletClassName - the AppId of generic resource
	 */
	public GcubeApplicationReader(String type, ScopeBean scope, String secondaryType, String portletClassName) {
		this.type = type;
		this.scope = scope;
		this.secondaryType = secondaryType;
		this.appId = portletClassName;
		this.gcubeApplication = new GcubeApplication(this.type);
		readGcubeApplicationFromInfrastrucure();
		
		ApplicationProfileReader appProfReader = new ApplicationProfileReader(this.scope, APPLICATION_PROFILE, this.gcubeApplication.getAppId());
		
		this.gcubeApplication.setAppProfile(appProfReader.getApplicationProfile());
		
	}


	public GcubeApplication getGcubeApplication() {
		return gcubeApplication;
	}


	private void readGcubeApplicationFromInfrastrucure() {

		String queryString = Util.getGcubeGenericQueryString(secondaryType, appId);

//		ScopeBean scopeBean =  new ScopeBean(scope.toString());
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
					this.gcubeApplication.setName(currValue.get(0));
				} 
				else throw new ApplicationProfileNotFoundException("Your GcubeApplication NAME was not found in the profile");

				currValue = helper.evaluate(RESOURCE_PROFILE_BODY_APP_ID_TEXT);
				if (currValue != null && currValue.size() > 0) {
					this.gcubeApplication.setAppId(currValue.get(0));
				}
				else throw new ApplicationProfileNotFoundException("Your GcubeApplication ID n was not found in the profile, consider adding <AppId> element in <Body>");
				
				currValue = helper.evaluate("/Resource/Profile/Body/GcubeApps/GcubeApp/Type/text()");
				if (currValue != null && currValue.size() > 0) {
					List<String> types = currValue;
					boolean foundUrl = false;
					for (int i = 0; i < types.size(); i++) {
						if (currValue.get(i).trim().compareTo(type) == 0) {	
							this.gcubeApplication.setAppId(helper.evaluate("/Resource/Profile/Body/GcubeApps/GcubeApp/AppId/text()").get(i));
							this.gcubeApplication.setKeyOID(helper.evaluate("/Resource/Profile/Body/GcubeApps/GcubeApp/KeyOID/text()").get(i));
							foundUrl = true;
							break;
						}						
					}
					if (! foundUrl)
						throw new ApplicationProfileNotFoundException("Your GcubeApplication Type: "+type+" was not found in the profile");
				}
				else throw new ApplicationProfileNotFoundException("Your GcubeApplication Type was not found in the profile, consider adding <GcubeApp><Type> element in <Body>");
			}

		} catch (Exception e) {
			logger.error("Error while trying to fetch GcubeApplication profile from the infrastructure");
			e.printStackTrace();
		}
	}
}
