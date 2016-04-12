/**
 * 
 */
package org.gcube.portlets.widget.collectionsindexedwords.client.rpc;


import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.DataException;
import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.OnlyOpensearchException;
import org.gcube.portlets.widget.collectionsindexedwords.shared.IndexData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("IndexClientCaller")
public interface IndexClientCaller extends RemoteService {
	public IndexData getValues(Integer queryID, Integer maxStats) throws OnlyOpensearchException, DataException;
	public String getClusterValues(Integer queryID) throws Exception;
}
