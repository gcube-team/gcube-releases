package gr.cite.additionalemailaddresses.authorize;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.liferay.portal.util.PortalUtil;

/**
 * @author mnikolopoulos
 *
 */
@Service
public class AuthorizeImpl implements Authorize{
	
	@Override
	public <T extends PortletRequest> Boolean hasPermisions(T porteletRequest) {
		
		Boolean hasPermission = true;
		HttpServletRequest request = PortalUtil.getHttpServletRequest(porteletRequest);
		String currentUrl = PortalUtil.getCurrentURL(request);
		String [] parameters = currentUrl.split("\\?");
		
		if (parameters.length > 1){
			hasPermission = false;
		}
		
		return hasPermission;
	}

}
