package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class InfoContactModelSuggestion implements SuggestOracle.Suggestion {

	private InfoContactModel infoContactModel;

	public InfoContactModelSuggestion(InfoContactModel infoContactModel) {
		this.infoContactModel = infoContactModel;
	}

	@Override
	public String getDisplayString() {
		return getReplacementString();
	}

	@Override
	public String getReplacementString() {
		if (infoContactModel != null && infoContactModel.getName() != null && !infoContactModel.getName().isEmpty()) {
			if (infoContactModel.getEmailDomain() == null || infoContactModel.getEmailDomain().isEmpty()) {
				return infoContactModel.getName();
			} else {
				return infoContactModel.getName() + " (" + infoContactModel.getEmailDomain() + ")";

			}
		} else {
			return "";
		}

	}

	public InfoContactModel getInfoContactModel() {
		return infoContactModel;
	}

	@Override
	public String toString() {
		return "InfoContactModelSuggestion [infoContactModel=" + infoContactModel + "]";
	}

}
