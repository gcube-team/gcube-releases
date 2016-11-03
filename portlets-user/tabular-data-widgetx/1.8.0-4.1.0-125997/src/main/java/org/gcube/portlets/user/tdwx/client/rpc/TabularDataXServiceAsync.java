/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.rpc;

import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TabularDataXServiceAsync {

	void getCurrentTableDefinition(int id, AsyncCallback<TableDefinition> callback);

	void openTable(int tdSessionId, TableId tableId, AsyncCallback<TableDefinition> callback);
	
	void getTableDefinition(TableId id, AsyncCallback<TableDefinition> callback);
	
	void setCurrentTableColumnsReordering(int tdSessionId, ColumnsReorderingConfig columnReorderingConfig, AsyncCallback<TableDefinition> callback);
	
	void closeTable(int tdSessionId, AsyncCallback<Void> callback);

}
