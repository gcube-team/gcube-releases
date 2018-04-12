package gr.cite.additionalemailaddresses;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
/**
 * 
 * @author mnikolopoulos
 *
 */
public class AddVerificationFieldsInAdditionalEmailsAction extends SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		try {
			updateAdditionalEmailAddressesTable(GetterUtil.getLong(ids[0]));
		}
		catch (Exception e) {
			throw new ActionException(e);
		}
	}

	private void addColumn(long tableId, String name, int type, UnicodeProperties properties) throws PortalException, SystemException {

		ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(tableId, name);

		if (column != null) {
			return;
		}

		ExpandoColumn verified = ExpandoColumnLocalServiceUtil.addColumn(tableId, name, type);

		ExpandoColumnLocalServiceUtil.updateTypeSettings(verified.getColumnId(), properties.toString());
		
	}

	private void updateAdditionalEmailAddressesTable(long companyId) throws Exception {
		ExpandoTable expandoTable = null;

		try {
			expandoTable = ExpandoTableLocalServiceUtil.addTable(companyId, EmailAddress.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}
		catch (Exception e) {
			expandoTable = ExpandoTableLocalServiceUtil.getTable(companyId, EmailAddress.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		UnicodeProperties properties = new UnicodeProperties();

		properties.setProperty("hidden", "true");
		properties.setProperty("visible-with-update-permission", "false");

		addColumn(expandoTable.getTableId(), "verifiedAdditionalEmail", ExpandoColumnConstants.BOOLEAN, properties);
		addColumn(expandoTable.getTableId(), "verificationCodeAdditionalEmail", ExpandoColumnConstants.STRING, properties);
	}

}