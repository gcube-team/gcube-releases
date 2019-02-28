package org.gcube.portlets.widgets.pickitem.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PickItemServiceAsync {

	void searchEntities(String keyword, String vreContext, AsyncCallback<ArrayList<ItemBean>> callback);

}
