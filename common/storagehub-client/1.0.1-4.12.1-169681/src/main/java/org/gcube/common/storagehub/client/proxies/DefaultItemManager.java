package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

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
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.types.ACLList;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

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
	public Item getRootSharedFolder(String id) {
		Call<WebTarget, ItemWrapper<Item>> call = new Call<WebTarget, ItemWrapper<Item>>() {
			@Override
			public ItemWrapper<Item> call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("rootSharedFolder");
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
	public URL getPublickLink(String id) {
		Call<WebTarget, URL> call = new Call<WebTarget, URL>() {
			@Override
			public URL call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("publiclink");
				Invocation.Builder builder = myManager.request(MediaType.APPLICATION_JSON);
				URL response = builder.get(URL.class);
				return response;
			}
		};
		try {
			URL result = delegate.make(call);
			return result;
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
	public List<? extends Item> findChildrenByNamePattern(String id, String name, String... excludeNodes) {
		Call<WebTarget, ItemList> call = new Call<WebTarget, ItemList>() {
			@Override
			public ItemList call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id).path("items").path(name);
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
	public String uploadFile(InputStream stream, String parentId, String fileName, String description) {
		Call<WebTarget, String> call = new Call<WebTarget, String>() {
			@Override
			public String call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.register(MultiPartFeature.class).path(parentId)
						.path("create").path("FILE");
				Invocation.Builder builder = myManager.request();

				FormDataMultiPart multipart = new FormDataMultiPart();

				multipart.field("name", fileName);
				multipart.field("description", description);
				multipart.field("file", stream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
				String response = builder.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE),String.class);
				return response;

			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createFolder(String parentId, String name, String description) {
		Call<WebTarget, String> call = new Call<WebTarget, String>() {
			@Override
			public String call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(parentId)
						.path("create").path("FOLDER").queryParam("name", name).queryParam("description", description);
				Invocation.Builder builder = myManager.request();

				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
				formData.add("name", name);
				formData.add("description", description);
				String response = builder.post(Entity.form(formData),String.class);
				return response;
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String shareFolder(String id, Set<String> users, AccessType accessType) {
		Call<WebTarget, String> call = new Call<WebTarget, String>() {
			@Override
			public String call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.register(MultiPartFeature.class).path(id)
						.path("share");
				Invocation.Builder builder = myManager.request();

				try (FormDataMultiPart multipart = new FormDataMultiPart()){
					multipart.field("defaultAccessType", accessType, MediaType.APPLICATION_JSON_TYPE);
					multipart.field("users", users, MediaType.APPLICATION_JSON_TYPE);
					String response = builder.put(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE),String.class);
					return response;
				}
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String id) {
		Call<WebTarget, Void> call = new Call<WebTarget, Void>() {
			@Override
			public Void call(WebTarget manager) throws Exception {
				WebTarget myManager = manager.path(id);
				Invocation.Builder builder = myManager.request();
				builder.delete();
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
