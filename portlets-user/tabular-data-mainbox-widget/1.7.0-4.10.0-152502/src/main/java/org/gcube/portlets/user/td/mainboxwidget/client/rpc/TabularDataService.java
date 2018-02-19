package org.gcube.portlets.user.td.mainboxwidget.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@RemoteServiceRelativePath("tds")
public interface TabularDataService extends RemoteService {
	String hello() throws Exception;
}
