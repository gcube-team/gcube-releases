/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 4, 2014
 *
 */
public interface SmartDialogInterface {

	List<InfoContactModel> getSharedListUsers();
	List<CredentialModel> getSharedListUsersCredential();
	boolean isValidForm(boolean displayAlert);
}
