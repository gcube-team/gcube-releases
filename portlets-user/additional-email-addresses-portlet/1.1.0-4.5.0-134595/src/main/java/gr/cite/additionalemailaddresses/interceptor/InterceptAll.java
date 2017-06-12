package gr.cite.additionalemailaddresses.interceptor;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.User;
import com.liferay.portal.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.portlet.PortletProps;

/**
 * @author mnikolopoulos
 *
 */
public class InterceptAll extends HandlerInterceptorAdapter{

	private static Log log = LogFactoryUtil.getLog(InterceptAll.class);

	@Override
	public boolean preHandleResource(ResourceRequest request, ResourceResponse response, Object handler) throws Exception {
		
		ServiceContext serviceContext = ServiceContextFactory.getInstance(request);
		long maxNumEmails = Long.parseLong(PortletProps.get("max-num-emails"));
		long userId = serviceContext.getUserId();
		User user = UserLocalServiceUtil.getUser(userId);
		
		if(request.getParameter("emailAddressId") == null && request.getParameter("emailAddress") == null){
			return true;
		}else if(request.getParameter("emailAddressId") != null){
			
			long emailAddressId = ParamUtil.getLong(request, "emailAddressId");
			
			EmailAddress emailAddress = EmailAddressLocalServiceUtil.getEmailAddress(emailAddressId);
			if(emailAddress.getUserId() == user.getUserId()){
				return true;
			}
			
			log.error("User " + emailAddress.getUserName()  + " can not perform this Action");
			return false;
			
		}else if(request.getParameter("emailAddress") != null){
			if(user.getEmailAddresses().size() >= maxNumEmails){
				log.error("User " + user.getScreenName()  + " has the maximun number of emails");
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void afterResourceCompletion(ResourceRequest request, ResourceResponse response, Object handler, Exception ex) throws Exception {

		super.afterResourceCompletion(request, response, handler, ex);
	}
	
	
}
