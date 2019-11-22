/**
 * 
 */
package org.gcube.portlets.user.tdw.client.rpc;

import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface TabularDataServiceAsync {

	void getCurrentTableDefinition(int id, AsyncCallback<TableDefinition> callback);

	void openTable(int tdSessionId, TableId tableId, AsyncCallback<TableDefinition> callback);
	
	void getTableDefinition(TableId id, AsyncCallback<TableDefinition> callback);

	void closeTable(int tdSessionId, AsyncCallback<Void> callback);

}
