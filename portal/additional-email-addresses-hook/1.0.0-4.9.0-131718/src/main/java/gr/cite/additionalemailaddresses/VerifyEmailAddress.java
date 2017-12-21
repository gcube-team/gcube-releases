package gr.cite.additionalemailaddresses;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portal.landingpage.LandingPageManager;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.User;
import com.liferay.portal.service.EmailAddressLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import gr.citeadditionalemailaddresses.util.AdditionalEmailAddressesConstants;

/**
 * @author mnikolopoulos
 *
 */
public class VerifyEmailAddress extends BaseStrutsAction {

	private static Log log = LogFactoryUtil.getLog(VerifyEmailAddress.class);
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String code = ParamUtil.getString(request, "code");
		String landingPage = null;
		
		ExpandoValue expandoValue = getExpandoValueForCode(code);
		long emailAddressId = expandoValue.getClassPK();
		EmailAddress emailAddress = EmailAddressLocalServiceUtil.getEmailAddress(emailAddressId);
		verifyEmailAddress(emailAddress);
		
		User user = UserLocalServiceUtil.getUser(emailAddress.getUserId());
		
		landingPage = LandingPageManager.getLandingPagePath(request, user);
		response.sendRedirect(landingPage);
		
		return null;
	}

	private void verifyEmailAddress(EmailAddress emailAddress) throws PortalException, SystemException {
		ExpandoValueLocalServiceUtil.addValue(emailAddress.getCompanyId(), EmailAddress.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME,
											AdditionalEmailAddressesConstants.VERIFY, emailAddress.getEmailAddressId(), true);
		
	}

	@SuppressWarnings("unchecked")
	private ExpandoValue getExpandoValueForCode(String code) throws Exception {
		
		DynamicQuery dynamicQueryColumn = DynamicQueryFactoryUtil.forClass(ExpandoColumn.class, PortletClassLoaderUtil.getClassLoader());
		Criterion critirioColumn = RestrictionsFactoryUtil.eq("name", AdditionalEmailAddressesConstants.CODE);
		dynamicQueryColumn.add(critirioColumn);
		
		List<ExpandoColumn> expandoColumns = ExpandoColumnLocalServiceUtil.dynamicQuery(dynamicQueryColumn);
		
		if (expandoColumns.size() != 1){
			throw new Exception();
		}
		
		ExpandoColumn expandoColumn = expandoColumns.get(0);
		
		long columnId = expandoColumn.getColumnId();
		
		DynamicQuery dynamicQueryValue = DynamicQueryFactoryUtil.forClass(ExpandoValue.class, PortletClassLoaderUtil.getClassLoader());
		Criterion critirioValue = RestrictionsFactoryUtil.eq("columnId", columnId);
		critirioValue = RestrictionsFactoryUtil.and(critirioValue, RestrictionsFactoryUtil.eq("data", code));
		dynamicQueryValue.add(critirioValue);
		
		List<ExpandoValue> expandoValues = ExpandoValueLocalServiceUtil.dynamicQuery(dynamicQueryValue);
		
		if (expandoValues.size() != 1){
			throw new Exception();
		}
		
		return expandoValues.get(0);
	}
	
	public String getStringFromTable(long companyId, Class<?> prortalModel, String columnName, long modelPrimaryKey) throws NestableException {
		String value = null;
		try {
			ExpandoValue expandoValue = ExpandoValueLocalServiceUtil.getValue(companyId, prortalModel.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, columnName, modelPrimaryKey);
			if (expandoValue != null) value = expandoValue.getData();
		} catch (SystemException e) {
			log.error("Error while getting data from table", e);
			e.printStackTrace();
			throw e;
		}
		return value;
	}
}
