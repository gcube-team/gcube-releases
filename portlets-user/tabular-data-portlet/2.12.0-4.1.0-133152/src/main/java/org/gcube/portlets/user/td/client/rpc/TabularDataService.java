package org.gcube.portlets.user.td.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
@RemoteServiceRelativePath("tds")
public interface TabularDataService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;
}
