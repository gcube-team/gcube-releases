package gr.cite.additionalemailaddresses;

import java.io.IOException;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import gr.cite.additionalemailaddresses.authorize.Authorize;
import gr.cite.additionalemailaddresses.models.AdditionalEmailAddresses;
import gr.cite.additionalemailaddresses.models.IsEmailAddressAlreadyUsedResponse;
import gr.cite.additionalemailaddresses.models.ResponseMessage;
import gr.cite.additionalemailaddresses.service.AdditionalEmailAddressesService;
import gr.cite.additionalemailaddresses.utils.Json;

/**
 * @author mnikolopoulos
 *
 */
@Controller
@RequestMapping("VIEW")
public class HomeController {
	
	private static Log log = LogFactoryUtil.getLog(HomeController.class);
	
	private AdditionalEmailAddressesService additionalEmailAddressesService;
	private Authorize autorize;
	
	@Autowired
	public void setAdditionalEmailAddressesManager(AdditionalEmailAddressesService additionalEmailAddressesService) {
		this.additionalEmailAddressesService = additionalEmailAddressesService;
	}
	
	@Autowired
	public void setAutorize(Authorize autorize) {
		this.autorize = autorize;
	}
	
	@RenderMapping
	public String viewHomePage(RenderRequest renderRequest, RenderResponse renderResponse){
		Boolean hasPerirmision = autorize.hasPermisions(renderRequest);
		return hasPerirmision ? "home" : "hide";
    }
	
	@ResourceMapping(value = "sendEmailVerification")
	public void sendEmailVerification(@RequestParam("emailAddress") String emailAddress, ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<AdditionalEmailAddresses> responseMessage = null;
		try {
			this.additionalEmailAddressesService.sendEmailVerification(request, response, emailAddress);
			AdditionalEmailAddresses additionalEmailAddresses = additionalEmailAddressesService.getEmailAddresses(request);
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);	
	}
	
	@ResourceMapping(value = "listAdditionalEmailAddresses")
	public void listAdditionalEmailAddresses(ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<AdditionalEmailAddresses> responseMessage = null;
		
		try {
			AdditionalEmailAddresses additionalEmailAddresses = this.additionalEmailAddressesService.getEmailAddresses(request);
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);
		
	}
	
	@ResourceMapping(value = "isEmailAddressAlreadyUsed")
	public void isEmailAddressAlreadyUsed(@RequestParam("emailAddress") String emailAddress, ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<IsEmailAddressAlreadyUsedResponse> responseMessage = null;
		
		try {
			IsEmailAddressAlreadyUsedResponse additionalEmailAddresses = this.additionalEmailAddressesService.isEmailAddressAlreadyUsed(request, emailAddress);
			responseMessage = new ResponseMessage<IsEmailAddressAlreadyUsedResponse>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<IsEmailAddressAlreadyUsedResponse>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);
		
	}
	
	@ResourceMapping(value = "removeAdditionalEmail")
	public void removeAdditionalEmail(@RequestParam("emailAddressId") long emailAddressId, ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<AdditionalEmailAddresses> responseMessage = null;
		
		try {
			this.additionalEmailAddressesService.removeAdditionalEmail(request, emailAddressId);
			AdditionalEmailAddresses additionalEmailAddresses = this.additionalEmailAddressesService.getEmailAddresses(request);
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);
	}
	
	@ResourceMapping(value = "resendVerificationEmail")
	public void resendVerificationEmail(@RequestParam("emailAddressId") long emailAddressId, ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<AdditionalEmailAddresses> responseMessage = null;
		
		try {
			this.additionalEmailAddressesService.resendVerificationEmail(request, emailAddressId);
			AdditionalEmailAddresses additionalEmailAddresses = this.additionalEmailAddressesService.getEmailAddresses(request);
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);
	}
	
	@ResourceMapping(value = "selectPrimaryEmailAddress")
	public void listAdditionalEmailAddresses(@RequestParam("emailAddressId") long emailAddresId, ResourceRequest request, ResourceResponse response) throws IOException{
		
		ResponseMessage<AdditionalEmailAddresses> responseMessage = null;
		
		try {
			this.additionalEmailAddressesService.selectPrimaryEmailAddress(request, emailAddresId);
			AdditionalEmailAddresses additionalEmailAddresses = this.additionalEmailAddressesService.getEmailAddresses(request);
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(additionalEmailAddresses, null, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			responseMessage = new ResponseMessage<AdditionalEmailAddresses>(null, e.getMessage(), false);
		}
		
		Json.returnDeepJson(response, responseMessage);
	}
}
