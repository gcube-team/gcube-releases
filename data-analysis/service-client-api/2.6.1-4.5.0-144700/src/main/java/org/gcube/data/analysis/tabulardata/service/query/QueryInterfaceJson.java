package org.gcube.data.analysis.tabulardata.service.query;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;

public interface QueryInterfaceJson {

	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter) throws NoSuchTableException;

	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryOrder order) throws NoSuchTableException;

	public abstract String queryAsJson(TableId tableId, QueryPage page, QuerySelect select) throws NoSuchTableException;

	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QueryOrder order) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QueryOrder order, QuerySelect select) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QueryOrder order, QuerySelect select, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QuerySelect select) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QuerySelect select, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryFilter filter, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryOrder order, QuerySelect select, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryOrder order, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page, QueryOrder order, QuerySelect select) throws NoSuchTableException;
		
	public abstract String queryAsJson(TableId tableId, QueryPage page, QuerySelect select, QueryGroup group) throws NoSuchTableException;
	
	public abstract String queryAsJson(TableId tableId, QueryPage page) throws NoSuchTableException;

}