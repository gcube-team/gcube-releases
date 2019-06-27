package org.gcube.data.access.storagehub.fs;

import java.nio.file.Paths;
import java.util.List;

import org.cache2k.Cache;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {

	public static Logger logger = LoggerFactory.getLogger(PathUtils.class);
	
	private Cache<String,ItemContainer<Item>> cache;
	private FolderContainer rootDirectory;
	private StorageHubClient client;
	
	public PathUtils(Cache<String, ItemContainer<Item>> cache, FolderContainer rootDirectory, StorageHubClient client) {
		super();
		this.cache = cache;
		this.rootDirectory = rootDirectory;
		this.client = client;
	}

	
	
	public String getLastComponent(String path) {
		while (path.substring(path.length() - 1).equals("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.isEmpty()) {
			return "";
		}
		return path.substring(path.lastIndexOf("/") + 1);
	}

	public String getParentPath(String path) {
		return Paths.get(path).getParent().toString();
	}

	public ItemContainer<? extends Item> getPath(String path) {

		if (path.equals("/")) return rootDirectory;

		if (cache.containsKey(path)) {
			ItemContainer<? extends Item> cached = cache.peek(path);
			logger.trace("path "+path+" retrieved in cache with id "+cached.getId());
			return cached;
		} else logger.trace("path "+path+" not in cache");

		synchronized (this) {
			ItemContainer<? extends Item> retrievedItem =  getPathRecursive(path, rootDirectory);	
			if (retrievedItem!=null)cache.put(path, (ItemContainer<Item>) retrievedItem);
			return retrievedItem;
		}
	}

	public ItemContainer<? extends Item> getPathRecursive(String path, FolderContainer parentContainer) {
		try {
			while (path.startsWith("/")) {
				path = path.substring(1);
			}
			if (!path.contains("/")) {
				logger.trace("seaching path "+path+" in "+parentContainer.get().getTitle());
				List<ItemContainer<? extends Item>> items = parentContainer.findByName(path).withContent().getContainers();
				logger.trace("found? "+(items.size()>0));
				return items.size()>0? items.get(0): null;
			}
			if (path.startsWith(StorageHubFS.VREFOLDERS_NAME)) {
				List<ItemContainer<? extends Item>> vreFolders = client.getVREFolders().getContainers();
				String vreName = path.split("/")[1];
				for (ItemContainer<? extends Item> vreContainer : vreFolders) {
					SharedFolder veFolder = (SharedFolder)vreContainer.get();
					if (veFolder.getDisplayName().equals(vreName)) {
						String nextPath = path.replace(StorageHubFS.VREFOLDERS_NAME+"/"+veFolder.getDisplayName(), "");
						if(nextPath.isEmpty()) return vreContainer;
						else return getPathRecursive(nextPath, (FolderContainer)vreContainer);						
					}
				}
			}
			
			
			String nextName = path.substring(0, path.indexOf("/"));
			String rest = path.substring(path.indexOf("/"));
			
			for (ItemContainer<? extends Item> container : parentContainer.findByName(nextName).withContent().getContainers()) {
				if (container instanceof FolderContainer) {
					logger.trace("seaching path "+rest+" in "+container.get().getTitle());
					return getPathRecursive(rest, (FolderContainer)container);
				}
			}
		}catch(Exception e) {
			logger.error("error in gpath recursive",e);
		}
		return null;
	}
}
