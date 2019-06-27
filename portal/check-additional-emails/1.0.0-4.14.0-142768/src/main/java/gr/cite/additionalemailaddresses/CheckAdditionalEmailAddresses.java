package gr.cite.additionalemailaddresses;

import java.util.List;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.User;
import com.liferay.portal.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

/**
 * @author mnikolopoulos
 *
 */
public class CheckAdditionalEmailAddresses {
	
	private static Log log = LogFactoryUtil.getLog(CheckAdditionalEmailAddresses.class);
	
	public static User checkInIfAdditionalEmailAndIfVerified(String emailAddressName) throws Exception {
		long columnId = getColumnId();
		List<EmailAddress> emailAddresses = getEmailAddresses(emailAddressName);
		User user = checkIfIsVerified(columnId, emailAddresses);
		
		return user;
	}

	private static User checkIfIsVerified(long columnId, List<EmailAddress> emailAddresses) throws SystemException, NoSuchUserException {
		User user = null;
		for(EmailAddress emailAddress : emailAddresses){
			
			DynamicQuery dynamicQueryValue = DynamicQueryFactoryUtil.forClass(ExpandoValue.class, PortletClassLoaderUtil.getClassLoader());
			Criterion critirioValue = RestrictionsFactoryUtil.eq(CheckAdditionalEmailAddressConstants.FIELD_COLUMN_ID, columnId);
			critirioValue = RestrictionsFactoryUtil.and(critirioValue, RestrictionsFactoryUtil.eq(CheckAdditionalEmailAddressConstants.FIELD_DATA, "true"));
			critirioValue = RestrictionsFactoryUtil.and(critirioValue, RestrictionsFactoryUtil.eq(CheckAdditionalEmailAddressConstants.FIELD_CLASS_PK, emailAddress.getEmailAddressId()));
			dynamicQueryValue.add(critirioValue);
			
			@SuppressWarnings("unchecked")
			List<ExpandoValue> expandoValues = ExpandoValueLocalServiceUtil.dynamicQuery(dynamicQueryValue);
			
			if (expandoValues.size() > 0){
				long userId = emailAddress.getUserId();
				try{
					user = UserLocalServiceUtil.getUser(userId);
				}catch (Exception e) {
					log.error("User with id " + userId + " was not found");
					throw new NoSuchUserException();
				}
				break;
			}
		}
		return user;
	}


	private static List<EmailAddress> getEmailAddresses(String emailAddressName) throws SystemException {
		DynamicQuery dynamicQueryEmailAddresses = DynamicQueryFactoryUtil.forClass(EmailAddress.class, PortletClassLoaderUtil.getClassLoader());
		Criterion critirioColumn = RestrictionsFactoryUtil.eq("address", emailAddressName);
		dynamicQueryEmailAddresses.add(critirioColumn);
		
		@SuppressWarnings("unchecked")
		List<EmailAddress> emailAddresses = EmailAddressLocalServiceUtil.dynamicQuery(dynamicQueryEmailAddresses);
		return emailAddresses;
	}


	private static long getColumnId() throws Exception {
		DynamicQuery dynamicQueryColumn = DynamicQueryFactoryUtil.forClass(ExpandoColumn.class, PortletClassLoaderUtil.getClassLoader());
		Criterion critirioColumn = RestrictionsFactoryUtil.eq(CheckAdditionalEmailAddressConstants.FIELD_NAME, CheckAdditionalEmailAddressConstants.VERIFY);
		dynamicQueryColumn.add(critirioColumn);
		
		@SuppressWarnings("unchecked")
		List<ExpandoColumn> expandoColumns = ExpandoColumnLocalServiceUtil.dynamicQuery(dynamicQueryColumn);
		
		if (expandoColumns.size() != 1){
			log.error("Expando table has more than one column with name" + CheckAdditionalEmailAddressConstants.VERIFY);
			throw new Exception("Expando table has more than one column with name" + CheckAdditionalEmailAddressConstants.VERIFY);
		}
		
		ExpandoColumn expandoColumn = expandoColumns.get(0);
		long columnId = expandoColumn.getColumnId();
		return columnId;
	}

}
