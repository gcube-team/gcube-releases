/**
 * 
 */
package org.gcube.portlets.user.tdw.client.rpc;

import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
@RemoteServiceRelativePath("tdw")
public interface TabularDataService extends RemoteService {
	
	public TableDefinition openTable(int tdSessionId, TableId tableId) throws TabularDataServiceException;
	
	public TableDefinition getCurrentTableDefinition(int tdSessionId) throws TabularDataServiceException;

	public TableDefinition getTableDefinition(TableId id) throws TabularDataServiceException;
	
	public void closeTable(int tdSessionId) throws TabularDataServiceException;

}
