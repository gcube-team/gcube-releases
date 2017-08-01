/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.rpc;

import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.Constants;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
@RemoteServiceRelativePath(Constants.REMOTE_SERVICE_RELATIVE_PATH)
public interface TabularDataXService extends RemoteService {
	
	public TableDefinition openTable(int tdSessionId, TableId tableId) throws TabularDataXServiceException;
	
	public TableDefinition getCurrentTableDefinition(int tdSessionId) throws TabularDataXServiceException;
	
	public TableDefinition setCurrentTableColumnsReordering(int tdSessionId, ColumnsReorderingConfig columnReorderingConfig) throws TabularDataXServiceException;
	
	public TableDefinition getTableDefinition(TableId id) throws TabularDataXServiceException;
	
	public void closeTable(int tdSessionId) throws TabularDataXServiceException;
	
	
}
