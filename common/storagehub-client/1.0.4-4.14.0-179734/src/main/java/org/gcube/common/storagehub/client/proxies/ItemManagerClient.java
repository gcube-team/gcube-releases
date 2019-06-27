package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.Version;



public interface ItemManagerClient {
	
	List<? extends Item> getChildren(String id, boolean includeHidden, String ... excludeNodes) throws StorageHubException;
	
	List<? extends Item> getChildren(String id, Class<? extends Item> onlyOfType, boolean includeHidden, String ... excludeNodes) throws StorageHubException;
	
	List<? extends Item> getChildren(String id, int start, int limit, boolean includeHidden, Class<? extends Item> onlyOfType,  String ... excludeNodes) throws StorageHubException;
	
	List<? extends Item> getChildren(String id, int start, int limit, boolean includeHidden, String ... excludeNodes) throws StorageHubException;
	
	List<? extends Item> getAnchestors(String id, String ... excludeNodes) throws StorageHubException;
	
	Integer childrenCount(String id,boolean includeHidden) throws StorageHubException ;
	
	Integer childrenCount(String id, boolean includeHidden, Class<? extends Item> onlyOfType) throws StorageHubException;
	
	Item get(String id, String ... excludeNodes) throws StorageHubException;

	StreamDescriptor download(String id, String... excludeNodes) throws StorageHubException;
	
	String uploadFile(InputStream stream, String parentId, String fileName, String description) throws StorageHubException;

	String createFolder(String parentId, String name, String description, boolean hidden) throws StorageHubException;

	@Deprecated
	String createFolder(String parentId, String name, String description) throws StorageHubException;
	
	List<ACL> getACL(String id) throws StorageHubException;
	
	String changeACL(String id, String user, AccessType accessType) throws StorageHubException;

	@Deprecated
	void delete(String id) throws StorageHubException;

	URL getPublickLink(String id) throws StorageHubException;
	
	URL getPublickLink(String id, String version) throws StorageHubException;

	List<? extends Item> findChildrenByNamePattern(String id, String name, String ... excludeNodes) throws StorageHubException;

	Item getRootSharedFolder(String id) throws StorageHubException;

	String shareFolder(String id, Set<String> users, AccessType accessType) throws StorageHubException;
	
	String copy(String id, String destinationFolderId, String newFilename) throws StorageHubException;

	String uploadArchive(InputStream stream, String parentId, String extractionFolderName) throws StorageHubException;

	String unshareFolder(String id, Set<String> users) throws StorageHubException;

	String move(String id, String destinationFolderId) throws StorageHubException;

	String rename(String id, String newName) throws StorageHubException;

	List<Version> getFileVersions(String id) throws StorageHubException;

	StreamDescriptor downloadSpecificVersion(String id, String version) throws StorageHubException;
	
	String setMetadata(String id,Metadata metadata) throws StorageHubException;

	String createGcubeItem(String parentId, GCubeItem item) throws StorageHubException;

	StreamDescriptor resolvePublicLink(String identifier) throws StorageHubException;

	@Deprecated
	List<? extends Item> getChildren(String id, Class<? extends Item> onlyOfType, String... excludeNodes) throws StorageHubException;

	@Deprecated
	List<? extends Item> getChildren(String id, String ... excludeNodes) throws StorageHubException;

	@Deprecated
	Integer childrenCount(String id, Class<? extends Item> onlyOfType) throws StorageHubException;

	@Deprecated
	Integer childrenCount(String id) throws StorageHubException;

	void delete(String id, boolean force) throws StorageHubException;

	boolean canWriteInto(String id) throws StorageHubException;

	
	
}
