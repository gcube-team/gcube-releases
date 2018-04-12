package org.gcube.portets.user.message_conversations.client.oracle;
import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portets.user.message_conversations.client.MessageService;
import org.gcube.portets.user.message_conversations.client.MessageServiceAsync;
import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.shared.CurrUserAndPortalUsersWrapper;
import org.gcube.portets.user.message_conversations.shared.WSUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class UserOracle extends MaterialSuggestionOracle{
	private final MessageServiceAsync convService = GWT.create(MessageService.class);

	private List<WSUser> contacts = new LinkedList<>();

	public void addContacts(List<WSUser> users) {
		contacts.addAll(users);
	}

	public UserOracle() {
		((ServiceDefTarget) convService).setServiceEntryPoint(Utils.getServiceEntryPoint());
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		Response resp = new Response();
		if(contacts.isEmpty()){
			callback.onSuggestionsReady(request, resp);
			return;
		}
		String text = request.getQuery();
		text = text.toLowerCase();

		final List<UserSuggestion> list = new ArrayList<>();
		convService.searchUsers(text, new AsyncCallback<ArrayList<WSUser>>() {

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("error");	
				callback.onSuggestionsReady(request, resp);
				return;
			}

			@Override
			public void onSuccess(ArrayList<WSUser> results) {
				for (WSUser contact : results){
					list.add(new UserSuggestion(contact));
				}				
				resp.setSuggestions(list);
				callback.onSuggestionsReady(request, resp);
			}
		});      
	}
}