package org.gcube.common.storagehub.client.proxies;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Query;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultWorkspaceManager implements WorkspaceManagerClient {

	private final ProxyDelegate<GXWebTargetAdapterRequest> delegate;


	public DefaultWorkspaceManager(ProxyDelegate<GXWebTargetAdapterRequest> config){
		this.delegate = config;
	}

	@Override
	public <T extends Item> T getWorkspace(String ... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemWrapper<T>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager;

				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				GXInboundResponse response = myManager.queryParams(params).get();

				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}

				return response.getSource().readEntity(ItemWrapper.class);
			}
		};
		try {
			ItemWrapper<T> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	@Override
	public <T extends Item> T retieveItemByPath(String relativePath, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemWrapper<T>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager;
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
	}*/

	@Override
	public List<? extends Item> getVreFolders(String ... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("vrefolders");
				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				GXInboundResponse response = myManager.queryParams(params).get();

				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(ItemList.class);
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
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("vrefolders").path("paged");
				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				params.put("start", new Object[] {start});
				params.put("limit", new Object[] {limit});

				GXInboundResponse response = myManager.queryParams(params).get();

				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				
				return response.getSource().readEntity(ItemList.class);

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
		Call<GXWebTargetAdapterRequest, ItemWrapper<T>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("vrefolder");
				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(ItemWrapper.class);
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
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("vrefolder").path("recents");

				GXInboundResponse response = myManager.get();

				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				
				return response.getSource().readEntity(ItemList.class);
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
		Call<GXWebTargetAdapterRequest, ItemWrapper<T>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<T>>() {
			@Override
			public ItemWrapper<T> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("trash");
				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(ItemWrapper.class);
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
	public void emptyTrash() {
		Call<GXWebTargetAdapterRequest, Void> call = new Call<GXWebTargetAdapterRequest, Void>() {
			@Override
			public Void call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("trash").path("empty");
				
				GXInboundResponse response = myManager.delete();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return null;
			}
		};
		try {
			delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String restoreFromTrash(final String id) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("trash").path("restore");
				
				GXInboundResponse response = myManager.put(Entity.text(id));
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}				
				
				return response.getSource().readEntity(String.class);
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<? extends Item> search(Query<SearchableItem<?>> query, String ... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path("query");
				Map<String, Object[]> params = new HashMap<>();

				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);

				if (query.getOrderFields() !=null && query.getOrderFields().size() >0) {
					List<String> orders = new ArrayList<>();
					for (OrderField field :query.getOrderFields())
						orders.add(String.format("[%s]%s",field.getField().getName(),field.getMode().toString()));
					params.put("o", orders.toArray(new Object[orders.size()]));
				}

				params.put("n", new Object[] {query.getSearchableItem().getNodeValue()});

				if (query.getLimit()!=-1)
					params.put("l", new Object[] { query.getLimit()});

				if (query.getOffset()!=-1)
					params.put("f", new Object[] { query.getOffset()});



				ObjectMapper mapper = new ObjectMapper();
				String serializedJson = mapper.writeValueAsString(query.getExpression());

				params.put("e", new Object[] { URLEncoder.encode(serializedJson)});

				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(ItemList.class);
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
