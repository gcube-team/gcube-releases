package org.gcube.portlets.user.joinnew.server.portlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LoginPortlet extends GenericPortlet {
	public static final String GATEWAY_NAME = "GATEWAY_NAME";
	private static Log _log = LogFactoryUtil.getLog(LoginPortlet.class);
	public void init() throws PortletException {
	}

	public void doView(RenderRequest request, RenderResponse response)	throws PortletException, IOException {
		response.setContentType("text/html");
		ScopeHelper.setContext(request);
		try {
			setGatewayName(request);  //set the gateway name in the session by rieadin a prop file
		} catch (Exception e) {
			e.printStackTrace();
		} 

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/Joinnew_view.jsp");
		dispatcher.include(request, response);		
	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
	}

	/**
	 * @return the default community URL
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	private void setGatewayName(RenderRequest renderRequest) throws PortalException, SystemException {
		try {
			//set the gateway label in the session
			String gatewayLabel = getGatewayLabelName();
			renderRequest.getPortletSession().setAttribute(GATEWAY_NAME, gatewayLabel, PortletSession.APPLICATION_SCOPE);

			_log.debug("Set Gateway name: " + gatewayLabel);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			_log.warn("gcube-data.properties not found, could not set Gateway name");
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private String getGatewayLabelName() throws IOException {
		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";


		String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-data.properties";			
		File propsFile = new File(propertyfile);
		FileInputStream fis = new FileInputStream(propsFile);
		props.load( fis);
		toReturn = props.getProperty("portalinstancename");

		return toReturn;
	}
}


