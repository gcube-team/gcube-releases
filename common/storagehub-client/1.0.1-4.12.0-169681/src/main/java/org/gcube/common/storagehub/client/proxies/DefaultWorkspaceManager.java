package org.gcube.common.storagehub.client.proxies;

import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Query;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultWorkspaceManager implements WorkspaceManagerClient {

	private final ProxyDelegate<WebTarget> delegate;

	
	public DefaultWorkspaceManager(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	@Override
	public <T extends Item> T getWorkspace(String ... excludeNodes) {
		Call<WebTarget, ItemWrapper<T>> call = new Call<WebTarget, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager;
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemWrapper<T> response = builder.get(ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<T> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T extends Item> T retieveItemByPath(String relativePath, String... excludeNodes) {
		Call<WebTarget, ItemWrapper<T>> call = new Call<WebTarget, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager;
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				
				myManager = manager.queryParam("relPath", relativePath);
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemWrapper<T> response = builder.get(ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<T> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<? extends Item> getVreFolders(String ... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("vrefolders");
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemList response = builder.get(ItemList.class);
				return response;
			}
		};
		try {
			ItemList result = delegate.make(call);
			return result.getItemlist();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<? extends Item> getVreFolders(int start, int limit, String ... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("vrefolders");
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				
				myManager = myManager.queryParam("start", start).queryParam("limit", limit);
				
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemList response = builder.get(ItemList.class);
				return response;
			}
		};
		try {
			ItemList result = delegate.make(call);
			return result.getItemlist();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Item>  T getVreFolder(String ... excludeNodes) {
		Call<WebTarget, ItemWrapper<T>> call = new Call<WebTarget, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("vrefolder");
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemWrapper<T> response = builder.get(ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<T> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<? extends Item>  getRecentModifiedFilePerVre() {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("vrefolder").path("recents");
								
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemList response = builder.get(ItemList.class);
				return response;
			}
		};
		try {
			ItemList result = delegate.make(call);
			return result.getItemlist();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T extends Item>  T getTrashFolder(String ... excludeNodes) {
		Call<WebTarget, ItemWrapper<T>> call = new Call<WebTarget, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("trash");
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemWrapper<T> response = builder.get(ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<T> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<? extends Item> search(Query<SearchableItem<?>> query, String ... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path("query");
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				
				if (query.getOrderFields() !=null && query.getOrderFields().size() >0) {
					for (OrderField field :query.getOrderFields())
						myManager =  myManager.queryParam("o","["+field.getField().getName()+"] "+field.getMode().toString());
				}
				
				myManager = myManager.queryParam("n", query.getSearchableItem().getNodeValue());
				
				if (query.getLimit()!=-1)
					myManager = myManager.queryParam("l", query.getLimit());
				
				if (query.getOffset()!=-1)
					myManager = myManager.queryParam("f", query.getOffset());
				
				
				
				ObjectMapper mapper = new ObjectMapper();
				String serializedJson = mapper.writeValueAsString(query.getExpression());
				
				myManager = myManager.queryParam("e", URLEncoder.encode(serializedJson));
				
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemList response = builder.get(ItemList.class);
				return response;
			}
		};
		try {
			ItemList result = delegate.make(call);
			return result.getItemlist();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	
}
