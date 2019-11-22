package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ServerMultiWordSuggestOracle extends MultiWordSuggestOracle {

	private HashMap<String, ArrayList<InfoContactModelSuggestion>> suggestionsMap;

	public ServerMultiWordSuggestOracle() {
		super();
		suggestionsMap = new HashMap<>();
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		final String keyword = request.getQuery().toLowerCase();
		if (suggestionsMap.containsKey(keyword)) {
			MultiWordSuggestOracle.Response response = new MultiWordSuggestOracle.Response(suggestionsMap.get(keyword));
			callback.onSuggestionsReady(request, response);
		} else {
			WorkspaceSharingServiceAsync.INSTANCE.getUsersByKeyword(keyword,
					new AsyncCallback<List<InfoContactModel>>() {

						@Override
						public void onSuccess(List<InfoContactModel> result) {
							ArrayList<InfoContactModelSuggestion> suggestions = new ArrayList<>();
							for (int i = 0; i < result.size(); i++) {
								InfoContactModelSuggestion suggestion = new InfoContactModelSuggestion(result.get(i));
								suggestions.add(suggestion);
							}
							suggestionsMap.put(keyword, suggestions);

							MultiWordSuggestOracle.Response response = new MultiWordSuggestOracle.Response(suggestions);
							callback.onSuggestionsReady(request, response);

						}

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Error retrieving users in ServerMultiWordSuggest", caught);
						}
					});
		}
	}

}