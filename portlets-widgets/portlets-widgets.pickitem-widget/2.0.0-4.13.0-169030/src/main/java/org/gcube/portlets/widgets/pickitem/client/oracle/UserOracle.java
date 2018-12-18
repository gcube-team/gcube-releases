package org.gcube.portlets.widgets.pickitem.client.oracle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.widgets.pickitem.client.bundle.CssAndImages;
import org.gcube.portlets.widgets.pickitem.client.rpc.PickItemService;
import org.gcube.portlets.widgets.pickitem.client.rpc.PickItemServiceAsync;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class UserOracle extends MultiWordSuggestOracle{
	private final PickItemServiceAsync pickuserService = GWT.create(PickItemService.class);

	private List<ItemBean> contacts = new LinkedList<>();
	private String context;

	public UserOracle(String context) {
		this.context = context;
	}

	public void addContacts(List<ItemBean> users) {
		for (ItemBean bean : users) {
			// if it is a team, set the avatar
			if(bean.isItemGroup())
				bean.setThumbnailURL(CssAndImages.INSTANCE.iconTeam().getSafeUri().asString());
			contacts.add(bean);
		}	
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		final Response resp = new Response();
		if(contacts.isEmpty()){
			callback.onSuggestionsReady(request, resp);
			return;
		}
		String text = request.getQuery();
		text = text.toLowerCase();

		final List<UserSuggestion> list = new ArrayList<>();
		pickuserService.searchEntities(text, context, new AsyncCallback<ArrayList<ItemBean>>() {

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("error");	
				callback.onSuggestionsReady(request, resp);
				return;
			}

			@Override
			public void onSuccess(ArrayList<ItemBean> results) {
				for (ItemBean contact : results){
					list.add(new UserSuggestion(contact));
				}				
				resp.setSuggestions(list);
				callback.onSuggestionsReady(request, resp);
			}
		});      
	}
}