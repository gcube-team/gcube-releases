package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.types.ACLList;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class DefaultItemManager implements ItemManagerClient {

	private final ProxyDelegate<WebTarget> delegate;

	
	public DefaultItemManager(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}


	@Override
	public List<? extends Item> getChildren(String id, String ... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("children");
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
	public StreamDescriptor download(String id) {
		Call<WebTarget, StreamDescriptor> call = new Call<WebTarget, StreamDescriptor>() {
			@Override
			public StreamDescriptor call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("download");
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_OCTET_STREAM);
				Response resp = builder.get();
				InputStream stream = resp.readEntity(InputStream.class);
				String disposition = resp.getHeaderString("Content-Disposition");
				String fileName = disposition.replaceFirst("attachment; filename = ([^/s]+)?", "$1");
				return new StreamDescriptor(stream, fileName);
			}
		};
		try {
			StreamDescriptor result = delegate.make(call);
			return result;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public Item get(String id, String... excludeNodes) {
		Call<WebTarget, ItemWrapper<Item>> call = new Call<WebTarget, ItemWrapper<Item>>() {
			@Override
			public ItemWrapper<Item> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id);
				if (excludeNodes !=null && excludeNodes.length>0)
					myManager =  myManager.queryParam("exclude",excludeNodes);
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ItemWrapper<Item> response = builder.get(ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<Item> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public List<? extends Item> getChildren(String id, int start, int limit, String... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("children").path("paged");
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
	public Integer childrenCount(String id) {
		Call<WebTarget, Integer> call = new Call<WebTarget, Integer>() {
			@Override
			public Integer call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("children").path("count");
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				Integer response = builder.get(Integer.class);
				return response;
			}
		};
		try {
			Integer result = delegate.make(call);
			return result;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public AbstractFileItem uploadFile(InputStream stream, String parentId, String fileName, String description) {
		Call<WebTarget, ItemWrapper<AbstractFileItem>> call = new Call<WebTarget, ItemWrapper<AbstractFileItem>>() {
			@Override
			public ItemWrapper<AbstractFileItem> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.register(MultiPartFeature.class).path(parentId)
						.path("create").path("FILE").queryParam("name", fileName).queryParam("description", description);
				Invocation.Builder builder = myManager.request();
				ItemWrapper<AbstractFileItem> response = builder.post(Entity.entity(stream, MediaType.APPLICATION_OCTET_STREAM), ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<AbstractFileItem> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FolderItem createFolder(String parentId, String name, String description) {
		Call<WebTarget, ItemWrapper<FolderItem>> call = new Call<WebTarget, ItemWrapper<FolderItem>>() {
			@Override
			public ItemWrapper<FolderItem> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(parentId)
						.path("create").path("FOLDER").queryParam("name", name).queryParam("description", description);
				Invocation.Builder builder = myManager.request();
				
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
			    formData.add("name", name);
			    formData.add("description", description);
			   	ItemWrapper<FolderItem> response = builder.post(Entity.form(formData),ItemWrapper.class);
				return response;
			}
		};
		try {
			ItemWrapper<FolderItem> result = delegate.make(call);
			return result.getItem();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<? extends Item> getAnchestors(String id, String... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("anchestors");
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
	public List<ACL> getACL(String id) {
		Call<WebTarget, ACLList> call = new Call<WebTarget, ACLList>() {
			@Override
			public ACLList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("acls");
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				ACLList response = builder.get(ACLList.class);
				return response;
			}
		};
		try {
			return delegate.make(call).getAcls();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
