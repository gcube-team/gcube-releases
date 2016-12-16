package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;


public class JCRQuery extends JCRWorkspaceFolderItem implements Query {


	public JCRQuery(JCRWorkspace workspace, ItemDelegate itemDelegate) throws RepositoryException, InternalErrorException {
		super(workspace,itemDelegate);

	}

	public JCRQuery(JCRWorkspace workspace, ItemDelegate itemDelegate, String name, String description,
			String query, QueryType queryType) throws RepositoryException {
		super(workspace, itemDelegate, name, description);

		Validate.notNull(query, "Query must be not null");
		Validate.notNull(queryType, "Query Type must be not null");


		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.QUERY, query);
		content.put(NodeProperty.QUERY_TYPE, queryType.toString());

	}

	@Override
	public long getLength() throws InternalErrorException {
		return delegate.getContent().get(NodeProperty.QUERY).length();	
	}

	@Override
	public String getQuery()   {
		return delegate.getContent().get(NodeProperty.QUERY);	
	}

	@Override
	public QueryType getQueryType() {
		return QueryType.valueOf(delegate.getContent().get(NodeProperty.QUERY_TYPE));		
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.QUERY;
	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}


}
