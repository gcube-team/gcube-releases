package gr.cite.additionalemailaddresses.service;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.mailing.EmailNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import gr.cite.additionalemailaddresses.CheckAdditionalEmailAddresses;
import gr.cite.additionalemailaddresses.exceptions.EmailNotValidException;
import gr.cite.additionalemailaddresses.models.AdditionalEmailAddresses;
import gr.cite.additionalemailaddresses.models.Email;
import gr.cite.additionalemailaddresses.models.IsEmailAddressAlreadyUsedResponse;
import gr.cite.additionalemailaddresses.models.Email.Status;
import gr.cite.additionalemailaddresses.utils.Utilities;

/**
 * @author mnikolopoulos
 *
 */
@Component
public class AdditionalEmailAddressesService {

	private static Log log = LogFactoryUtil.getLog(AdditionalEmailAddressesService.class);

	private Utilities utilities;
	private ExpandoService expandoService;

	@Autowired
	public void setExpandoService(ExpandoService expandoService) {
		this.expandoService = expandoService;
	}

	@Autowired
	public void setUtilities(Utilities utilities) {
		this.utilities = utilities;
	}

	public void sendEmailVerification(ResourceRequest resourceRequest, ResourceResponse resourceResposne, String emailAddressName) throws Exception {



		if (this.utilities.validateEmail(emailAddressName)){

			User user = ((ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)).getUser();
			ServiceContext serviceContext = ServiceContextFactory.getInstance(resourceRequest);
			HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);

			String domainName = PortalUtil.getPortalURL(resourceRequest);
			String generatedUUID = this.utilities.generateUUID().toString();

			try{
				EmailAddress emailAddress = this.updateEmailAddressesOfUser(user, emailAddressName, serviceContext);
				this.expandoService.updateEmailAddressesTable(user, emailAddress, generatedUUID);

				String body = this.utilities.getBody(user.getFirstName(), emailAddressName, domainName, generatedUUID);
				String subject = this.utilities.getSubject(domainName);

				EmailNotification email = new EmailNotification(emailAddressName, subject, body, request);
				email.sendEmail();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}else{
			throw new EmailNotValidException(emailAddressName + " is not a valid email address");
		}
	}

	public IsEmailAddressAlreadyUsedResponse isEmailAddressAlreadyUsed(ResourceRequest resourceRequest, String emailAddress) {
		IsEmailAddressAlreadyUsedResponse isUsed = new IsEmailAddressAlreadyUsedResponse();
		isUsed.setIsUsed(false);
		try {

			User user = CheckAdditionalEmailAddresses.checkInIfAdditionalEmailAndIfVerified(emailAddress);
			if(user != null){
				isUsed.setIsUsed(true);
				//log.debug
				System.out.println("Email " + emailAddress + " has been found in additional Email Addresses. This address is already used by user: " + user.getScreenName());
			}else{
				try{
					user = UserLocalServiceUtil.getUserByEmailAddress(((ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)).getCompanyId(),emailAddress);
					if (user != null) {
						isUsed.setIsUsed(true);
						//log.debug
						System.out.println("Email " + emailAddress + " has been found in Email Addresses. This address is already used by user: " + user.getScreenName());
					}
				}catch (PortalException e){
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RETURNING IS MAIL USED -> " + isUsed.getIsUsed());
		return isUsed;

	}

	private EmailAddress updateEmailAddressesOfUser(User user, String emailAddress, ServiceContext serviceContext) throws NestableException {
		String className = Contact.class.getName();
		long classPK = user.getContactId();
		int typeId = ListTypeConstants.CONTACT_EMAIL_ADDRESS_DEFAULT;
		Boolean isPrimary = false;

		try {
			return EmailAddressLocalServiceUtil.addEmailAddress(user.getUserId(), className, classPK, emailAddress, typeId, isPrimary, serviceContext);
		} catch (PortalException | SystemException e) {
			log.error("Error during the addition of email address", e);
			e.printStackTrace();
			throw e;
		}
	}

	public AdditionalEmailAddresses getEmailAddresses(ResourceRequest resourceRequest) throws Exception {

		AdditionalEmailAddresses additionalEmailAddresses = new AdditionalEmailAddresses();

		try{
			User user = ((ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)).getUser();

			List<EmailAddress> nativeEmailAddresses = user.getEmailAddresses();
			List<Email> emails = new ArrayList<Email>();

			for(EmailAddress nativeEmailAddress : nativeEmailAddresses){
				Status status = this.expandoService.getStatusOfEmailAddress(nativeEmailAddress);
			//	Boolean isPrimary = nativeEmailAddress.getPrimary();
				emails.add(new Email(nativeEmailAddress.getEmailAddressId(), nativeEmailAddress, status));
			}
			additionalEmailAddresses.setEmailAddresses(emails);
		}catch(Exception e){
			log.error("Error during the fetching of email addresses", e);
			e.printStackTrace();
			throw e;
		}

		return additionalEmailAddresses;
	}


	public void removeAdditionalEmail(ResourceRequest resourceRequest, long emailAddressId) throws Exception {
		try{
			EmailAddressLocalServiceUtil.deleteEmailAddress(emailAddressId);
		}catch (Exception e) {
			log.error("Email address could be removed", e);
			e.printStackTrace();
			throw e;
		}
	}
//
//	public EmailAddress selectPrimaryEmailAddress(ResourceRequest request, long emailAddressId) throws Exception {
//		EmailAddress emailAddress = null;
//		try{
//			removeFormerPrimaryEmailAddress(request);
//			EmailAddress formerEmailAddress = EmailAddressLocalServiceUtil.getEmailAddress(emailAddressId);
//			formerEmailAddress.setPrimary(true);
//			emailAddress = EmailAddressLocalServiceUtil.updateEmailAddress(formerEmailAddress);
//		}catch (Exception e) {
//			log.error("Error while selecting primary email address", e);
//			e.printStackTrace();
//			throw e;
//		}
//		return emailAddress;
//	}

//	private void removeFormerPrimaryEmailAddress(ResourceRequest request) throws SystemException {
//		User user = ((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).getUser();
//		List<EmailAddress> emailAddresses = user.getEmailAddresses();
//		for (EmailAddress emailAddress : emailAddresses) {
//			emailAddress.setPrimary(false);
//			EmailAddressLocalServiceUtil.updateEmailAddress(emailAddress);
//		}
//	}

	public void resendVerificationEmail(ResourceRequest resourceRequest, long emailAddressId) throws Exception {

		User user = ((ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)).getUser();
		HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);

		String domainName = PortalUtil.getPortalURL(resourceRequest);
		String generatedUUID = this.utilities.generateUUID().toString();

		try {
			EmailAddress emailAddress = EmailAddressLocalServiceUtil.getEmailAddress(emailAddressId);

			this.expandoService.updateEmailAddressesTable(user, emailAddress, generatedUUID);

			String body = this.utilities.getBody(user.getFirstName(), emailAddress.getAddress(), domainName, generatedUUID);
			String subject = this.utilities.getSubject(domainName);

			EmailNotification email = new EmailNotification(emailAddress.getAddress(), subject, body, request);
			email.sendEmail();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
