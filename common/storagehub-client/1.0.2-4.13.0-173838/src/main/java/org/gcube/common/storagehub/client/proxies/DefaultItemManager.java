package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.gxrest.request.GXWebTargetAdapterRequest;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.exceptions.BackendGenericError;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.ItemList;
import org.gcube.common.storagehub.model.service.ItemWrapper;
import org.gcube.common.storagehub.model.service.Version;
import org.gcube.common.storagehub.model.service.VersionList;
import org.gcube.common.storagehub.model.types.ACLList;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class DefaultItemManager implements ItemManagerClient {

	private final ProxyDelegate<GXWebTargetAdapterRequest> delegate;


	public DefaultItemManager(ProxyDelegate<GXWebTargetAdapterRequest> config){
		this.delegate = config;
	}


	@Override
	public List<? extends Item> getChildren(String id, Class<? extends Item> onlyOfType, String ... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("children");
				Map<String, Object[]> params = new HashMap<>();
				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);
				
				if (onlyOfType!=null)
					params.put("onlyType", new Object[] {resolveNodeType(onlyOfType)});
				
				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				ItemList items = response.getSource().readEntity(ItemList.class);
			
				return items;
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
	public List<? extends Item> getChildren(String id, int start, int limit, Class<? extends Item> onlyOfType, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("children").path("paged");
				Map<String, Object[]> params = new HashMap<>();
				
				
				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);
				
				if (onlyOfType!=null)
					params.put("onlyType", new Object[] {resolveNodeType(onlyOfType)});
				
				params.put("start", new Object[] {start});
				params.put("limit", new Object[] {limit});
				
				
				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				ItemList items = response.getSource().readEntity(ItemList.class);
				
				return items;
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
	public List<? extends Item> getChildren(String id, int start, int limit,
			String... excludeNodes) {
		return getChildren(id, start, limit, null,  excludeNodes);
	}
	
	@Override
	public List<? extends Item> getChildren(String id, String ... excludeNodes) {
		return getChildren(id, null,  excludeNodes);
	}


	@Override
	public Integer childrenCount(String id , Class<? extends Item> onlyOfType) {
		Call<GXWebTargetAdapterRequest, Integer> call = new Call<GXWebTargetAdapterRequest, Integer>() {
			@Override
			public Integer call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("children").path("count");
				Map<String, Object[]> params = new HashMap<>();
				if (onlyOfType!=null)
					params.put("onlyType", new Object[] {resolveNodeType(onlyOfType)});
				
				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(Integer.class);
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
	public Integer childrenCount(String id) {
		return childrenCount(id, null);
	}
	
	private String resolveNodeType(Class<? extends Item> itemClass){
		if (!itemClass.isAnnotationPresent(RootNode.class)) return null;
		String nodeType=  itemClass.getAnnotation(RootNode.class).value();
		return nodeType;
	}
	

	@Override
	public StreamDescriptor download(String id, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, StreamDescriptor> call = new Call<GXWebTargetAdapterRequest, StreamDescriptor>() {
			@Override
			public StreamDescriptor call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("download");
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
				
				Response resp = response.getSource();
				
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
	public StreamDescriptor downloadSpecificVersion(String id, String version) {
		Call<GXWebTargetAdapterRequest, StreamDescriptor> call = new Call<GXWebTargetAdapterRequest, StreamDescriptor>() {
			@Override
			public StreamDescriptor call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("versions").path(version).path("download");
				
				GXInboundResponse response = myManager.get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				Response resp = response.getSource();
				
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
	public List<Version> getFileVersions(String id) {
		Call<GXWebTargetAdapterRequest, VersionList> call = new Call<GXWebTargetAdapterRequest, VersionList>() {
			@Override
			public VersionList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("versions");
								
				GXInboundResponse response = myManager.get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				VersionList versions = response.getSource().readEntity(VersionList.class);
			
				return versions;
			}
		};
		try {
			VersionList result = delegate.make(call);
			return result.getItemlist();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Item get(String id, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemWrapper<Item>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<Item>>() {
			@Override
			public ItemWrapper<Item> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id);
				Map<String, Object[]> params = new HashMap<>();
				
				if (excludeNodes !=null && excludeNodes.length>0)
					params.put("exclude",excludeNodes);
				
				GXInboundResponse response = myManager.queryParams(params).get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError("error response from the service with code: "+response.getHTTPCode());
				}
				
				ItemWrapper<Item> item = response.getSource().readEntity(ItemWrapper.class);
				
				
				return item;
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
		Call<GXWebTargetAdapterRequest, ItemWrapper<Item>> call = new Call<GXWebTargetAdapterRequest, ItemWrapper<Item>>() {
			@Override
			public ItemWrapper<Item> call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("rootSharedFolder");
				GXInboundResponse response = myManager.get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				ItemWrapper<Item> item = response.getSource().readEntity(ItemWrapper.class);
				
				
				return item;
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
		Call<GXWebTargetAdapterRequest, URL> call = new Call<GXWebTargetAdapterRequest, URL>() {
			@Override
			public URL call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("publiclink");
				GXInboundResponse response = myManager.get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				URL item = response.getSource().readEntity(URL.class);
				
				return item;
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
	public List<? extends Item> findChildrenByNamePattern(String id, String name, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("items").path(name);
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
				
				ItemList items = response.getSource().readEntity(ItemList.class);
				
				return items;
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
	public String uploadFile(InputStream stream, String parentId, String fileName, String description) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.register(MultiPartFeature.class).path(parentId)
						.path("create").path("FILE");
								
				FormDataMultiPart multipart = new FormDataMultiPart();

				multipart.field("name", fileName);
				multipart.field("description", description);
				multipart.field("file", stream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
				
				GXInboundResponse response = myManager.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
				
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
	public String uploadArchive(InputStream stream, String parentId, String extractionFolderName) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.register(MultiPartFeature.class).path(parentId)
						.path("create").path("ARCHIVE");
				
				FormDataMultiPart multipart = new FormDataMultiPart();
				multipart.field("parentFolderName", extractionFolderName);
				multipart.field("file", stream, MediaType.APPLICATION_OCTET_STREAM_TYPE);

				GXInboundResponse response = myManager.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE));
				
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
	public String createFolder(String parentId, String name, String description) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(parentId)
						.path("create").path("FOLDER");
				
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
				formData.add("name", name);
				formData.add("description", description);
				
				
				GXInboundResponse response = myManager.post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
				
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
	public String createGcubeItem(String parentId, GCubeItem item) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(parentId)
						.path("create").path("GCUBEITEM");
											
				
				GXInboundResponse response = myManager.post(Entity.json(item));
				
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
	public String shareFolder(String id, Set<String> users, AccessType accessType) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				System.out.println("id is: "+id);
				
				GXWebTargetAdapterRequest myManager = manager.register(MultiPartFeature.class).path(id)
						.path("share");
								
				try (FormDataMultiPart multipart = new FormDataMultiPart()){
					multipart.field("defaultAccessType", accessType, MediaType.APPLICATION_JSON_TYPE);
					multipart.field("users", users, MediaType.APPLICATION_JSON_TYPE);
					
					GXInboundResponse response = myManager.put(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE));
					
					if (response.isErrorResponse()) {
						if (response.hasException()) 
							throw response.getException();
						else 
							throw new BackendGenericError();
					}
					
					return response.getSource().readEntity(String.class);
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
	public String unshareFolder(String id, Set<String> users) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.register(MultiPartFeature.class).path(id)
						.path("unshare");
				
				try (FormDataMultiPart multipart = new FormDataMultiPart()){
					multipart.field("users", users, MediaType.APPLICATION_JSON_TYPE);
					GXInboundResponse response = myManager.put(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE));
					
					if (response.isErrorResponse()) {
						if (response.hasException()) 
							throw response.getException();
						else 
							throw new BackendGenericError();
					}
					
					return response.getSource().readEntity(String.class);
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
		Call<GXWebTargetAdapterRequest, Void> call = new Call<GXWebTargetAdapterRequest, Void>() {
			@Override
			public Void call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id);
				GXInboundResponse response = myManager.delete();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError("error response returned from server "+response.getHTTPCode());
					
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
	public List<? extends Item> getAnchestors(String id, String... excludeNodes) {
		Call<GXWebTargetAdapterRequest, ItemList> call = new Call<GXWebTargetAdapterRequest, ItemList>() {
			@Override
			public ItemList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("anchestors");
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
	public List<ACL> getACL(String id) {
		Call<GXWebTargetAdapterRequest, ACLList> call = new Call<GXWebTargetAdapterRequest, ACLList>() {
			@Override
			public ACLList call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id).path("acls");
				GXInboundResponse response = myManager.get();
				
				if (response.isErrorResponse()) {
					if (response.hasException()) 
						throw response.getException();
					else 
						throw new BackendGenericError();
				}
				
				return response.getSource().readEntity(ACLList.class);
			}
		};
		try {
			return delegate.make(call).getAcls();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public String copy(String id, String destinationFolderId, String newFilename) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id)
						.path("copy");
				
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
				formData.add("destinationId", destinationFolderId);
				formData.add("fileName", newFilename);
				
				
				GXInboundResponse response = myManager.put(Entity.form(formData));
				
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
	public String move(String id, String destinationFolderId) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id)
						.path("move");
				
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
				formData.add("destinationId", destinationFolderId);
				
				
				GXInboundResponse response = myManager.put(Entity.form(formData));
				
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
	public String rename(String id, String newName) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id)
						.path("rename");
				
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
				formData.add("newName", newName);
				
				
				GXInboundResponse response = myManager.put(Entity.form(formData));
				
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
	public String setMetadata(String id, Metadata metadata) {
		Call<GXWebTargetAdapterRequest, String> call = new Call<GXWebTargetAdapterRequest, String>() {
			@Override
			public String call(GXWebTargetAdapterRequest manager) throws Exception {
				GXWebTargetAdapterRequest myManager = manager.path(id)
						.path("metadata");
					
				
				GXInboundResponse response = myManager.put(Entity.json(metadata));
				
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
	
}
