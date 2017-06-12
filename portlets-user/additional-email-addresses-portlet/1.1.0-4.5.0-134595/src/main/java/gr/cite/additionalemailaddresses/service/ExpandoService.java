package gr.cite.additionalemailaddresses.service;

import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.User;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import gr.cite.additionalemailaddresses.models.Email.Status;
import gr.cite.additionalemailaddresses.utils.AdditionalEmailAddressesConstants;

/**
 * @author mnikolopoulos
 *
 */
@Component
public class ExpandoService {
	
	private static Log log = LogFactoryUtil.getLog(ExpandoService.class);

	public void addValueToTable(long companyId, Class<?> portalModel, String columnName, long modelPrimaryKey, String value) throws NestableException{
		try {
			ExpandoValueLocalServiceUtil.addValue(companyId, portalModel.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName, modelPrimaryKey, value);
		} catch (PortalException | SystemException e) {
			log.error("Error during the addition of the value", e);
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public void addValueToTable(long companyId, Class<?> portalModel, String columnName, long modelPrimaryKey, Boolean value) throws NestableException{
		try {
			ExpandoValueLocalServiceUtil.addValue(companyId, portalModel.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName, modelPrimaryKey, value);
		} catch (PortalException | SystemException e) {
			log.error("Error during the addition of the value", e);
			e.printStackTrace();
			throw e;
		}
		
	}

	public void updateEmailAddressesTable(User user, EmailAddress emailAddress, String generatedUUID) throws NestableException {
		try {
			this.addValueToTable(emailAddress.getCompanyId(), EmailAddress.class, AdditionalEmailAddressesConstants.EMAIL_COLUMN_CODE, emailAddress.getEmailAddressId(), generatedUUID);
			this.addValueToTable(emailAddress.getCompanyId(), EmailAddress.class, AdditionalEmailAddressesConstants.EMAIL_COLUMN_VERIFIED, emailAddress.getEmailAddressId(), false);
		} catch (NestableException e) {
			log.error("Error while updating Table", e);
			e.printStackTrace();
			throw e;
		}
	}

	public Boolean getBooleanFromTable(long companyId, Class<?> prortalModel, String columnName, long modelPrimaryKey) throws NestableException {
		Boolean status = false;
		try {
			ExpandoValue expandoValue = ExpandoValueLocalServiceUtil.getValue(companyId, prortalModel.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName, modelPrimaryKey);
			if (expandoValue != null) status = expandoValue.getBoolean();
		} catch (PortalException | SystemException e) {
			log.error("Error while getting data from table", e);
			e.printStackTrace();
			throw e;
		}
		return status;
	}
	
	public Status getStatusOfEmailAddress(EmailAddress emailAddress) throws Exception{
		Boolean state = false;
		Status status = Status.UNKOWN;
		try{
			state = this.getBooleanFromTable(emailAddress.getCompanyId(), EmailAddress.class, AdditionalEmailAddressesConstants.EMAIL_COLUMN_VERIFIED, emailAddress.getEmailAddressId());
			status = state ? Status.ACTIVE : Status.INACTIVE;
		}catch (Exception e) {
			log.error("Error while retriving the status of email address", e);
			e.printStackTrace();
			throw e;
		}
		return status;
	}
}
