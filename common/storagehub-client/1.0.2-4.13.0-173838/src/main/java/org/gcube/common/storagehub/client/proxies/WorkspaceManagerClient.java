package org.gcube.common.storagehub.client.proxies;

import java.util.List;

import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Query;

public interface WorkspaceManagerClient {

	<T extends Item>  T getWorkspace(String ... excludeNodes);
	
	//<T extends Item>  T retieveItemByPath(String relativePath, String ... excludeNodes);
	
	List<? extends Item> getVreFolders(String ... excludeNodes);
	
	List<? extends Item> getVreFolders(int start, int limit, String ... excludeNodes);

	<T extends Item> T getVreFolder(String ... excludeNodes);

	List<? extends Item> search(Query<SearchableItem<?>> query, String ... excludeNodes);

	<T extends Item> T getTrashFolder(String ... excludeNodes);

	List<? extends Item> getRecentModifiedFilePerVre();

	String restoreFromTrash(String id);

	void emptyTrash();
	
}
