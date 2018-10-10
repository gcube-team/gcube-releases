package org.gcube.portlets.widgets.pickitem.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("pickItemServlet")
public interface PickItemService extends RemoteService {
	ArrayList<ItemBean> searchEntities(String keyword, String vreContext);
}
