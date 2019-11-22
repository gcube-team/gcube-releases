package org.gcube.data.access.storagehub.fs;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxrest.response.outbound.ErrorCode;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.ContainerType;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.exceptions.UserNotAuthorizedException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jnr.ffi.Pointer;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Timespec;

public class StorageHubFS extends FuseStubFS {

	public static Logger logger = LoggerFactory.getLogger(StorageHubFS.class);

	StorageHubClient client;

	String token;

	String scope;

	HashMap<String, SHFile> tempFiles = new HashMap<>(); 

	protected static final String VREFOLDERS_NAME= "VREFolders";

	Cache<String,ItemContainer<Item>> cache;

	PathUtils pathUtils;

	private FolderContainer rootDirectory;

	public StorageHubFS(String token, String scope) {
		super();
		this.token = token;
		this.scope = scope;
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);
		client = new StorageHubClient();
		rootDirectory = client.getWSRoot();
		cache = new Cache2kBuilder<String, ItemContainer<Item>>() {}
		.expireAfterWrite(30, TimeUnit.SECONDS)    
		.resilienceDuration(30, TimeUnit.SECONDS) 
		.build();
		pathUtils = new PathUtils(cache, rootDirectory, client);
	}

	/*
	 * fileUpload
	 * @see ru.serce.jnrfuse.FuseStubFS#write(java.lang.String, jnr.ffi.Pointer, long, long, ru.serce.jnrfuse.struct.FuseFileInfo)
	 */
	@Override
	public synchronized int write(String path, Pointer buf, long size, long offset, FuseFileInfo fi) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace(Thread.currentThread().getName()+" ) calling write "+ size+" "+offset);
		SHFile file = tempFiles.get(path);

		return file.write(buf, size, offset);
	}

	@Override
	public int flush(String path, FuseFileInfo fi) {
		logger.trace("called flush for "+path);
		SHFile file = tempFiles.get(path);
		file.flush();
		if (!(file instanceof FileUpload)) {
			logger.trace("file have been removed? {}", (tempFiles.remove(path)!=null));
			cache.remove(pathUtils.getParentPath(path));
			cache.remove(path);
		}
		return 0;
	}


	/*
	 * fileCreation
	 * @see ru.serce.jnrfuse.FuseStubFS#write(java.lang.String, jnr.ffi.Pointer, long, long, ru.serce.jnrfuse.struct.FuseFileInfo)
	 */
	@Override
	public synchronized int create(final String path, @mode_t long mode, FuseFileInfo fi) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace(Thread.currentThread().getName()+" ) calling create "+path);
		if (pathUtils.getPath(path) != null) {
			return -ErrorCodes.EEXIST();
		}


		return uploadFile(path);
	}


	private int uploadFile(String path) {
		final ItemContainer<? extends Item> parentContainer;

		if (path.substring(1).contains("/")) {				
			String parentPath = Paths.get(path).getParent().toString();
			parentContainer= pathUtils.getPath(parentPath);
		} else parentContainer = rootDirectory;

		try {
			if (!((FolderContainer) parentContainer).canWrite())
				return -ErrorCodes.EACCES();
		}catch (Exception e) {
			return -ErrorCodes.EIO();
		}

		final FSInputStream stream = new FSInputStream();

		FileUpload fileUpload = new FileUpload(stream);			
		tempFiles.put(path, fileUpload);
		new Thread(AuthorizedTasks.bind(new Runnable() {

			@Override
			public void run() {
				try {
					((FolderContainer) parentContainer).uploadFile(stream, pathUtils.getLastComponent(path), "");
				}catch(Throwable t) {
					tempFiles.get(path).flush();
				}
				logger.trace("file have been removed? {}", (tempFiles.remove(path)!=null));
				cache.remove(pathUtils.getParentPath(path));
				cache.remove(path);
			}
		})).start();
		return 0;
	}


	@Override
	public synchronized int getattr(String path, FileStat stat) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace(Thread.currentThread().getName()+" ) calling getattr "+path);
		if (Objects.equals(path, "/") || path.contains("Trash") || path.equals("/"+VREFOLDERS_NAME)) {
			stat.st_mode.set(FileStat.S_IFDIR | 0755);
			stat.st_nlink.set(2);
		} else if(pathUtils.getLastComponent(path).startsWith(".")) {
			logger.trace("start with /.");
			return super.getattr(path, stat);
		} else if (tempFiles.containsKey(path)){
			return tempFiles.get(path).getAttr(stat);
		}else {
			logger.trace("trying items");
			ItemContainer<? extends Item> container = pathUtils.getPath(path);
			logger.trace("item for path "+path+" is null ? "+(container==null));
			if (container==null) {
				return -ErrorCodes.ENOENT();
			}else  
				try{
					getAttrSHItem(container, stat);
				}catch (Throwable e) {
					logger.error("error gettign attributes ",e);
					return -ErrorCodes.ENOENT();
				}
		}
		return 0;
	}



	private void getAttrSHItem(ItemContainer<? extends Item> container, FileStat stat) throws IllegalArgumentException{
		if (container.getType()==ContainerType.FILE) {

			AbstractFileItem fileItem = ((AbstractFileItem)container.get());
			stat.st_size.set(fileItem.getContent().getSize());
			setCommonAttributes(fileItem, stat, FileStat.S_IFREG);
			logger.trace("fileContent is "+fileItem.getContent().getSize());


		} else if (container.getType()==ContainerType.FOLDER) {
			FolderItem folderItem = ((FolderItem)container.get());
			stat.st_size.set(4096);
			setCommonAttributes(folderItem, stat, FileStat.S_IFDIR);
		} else throw new IllegalArgumentException("container type not valid");
	}


	private void setCommonAttributes(Item item, FileStat stat, int type) {
		if (item.isShared()) {
			stat.st_mode.set(type | FileStat.S_IROTH);
		}else {
			stat.st_mode.set(type | 0777);
		}
		stat.st_mtim.tv_sec.set(item.getLastModificationTime().toInstant().getEpochSecond());
		stat.st_mtim.tv_nsec.set(item.getLastModificationTime().toInstant().getNano());
		stat.st_ctim.tv_sec.set(item.getLastModificationTime().toInstant().getEpochSecond());
		stat.st_ctim.tv_nsec.set(item.getLastModificationTime().toInstant().getNano());
		//stat.st_atim.tv_sec.set(System.currentTimeMillis()/1000);
		//stat.st_birthtime.tv_nsec.set(item.getLastModificationTime().toInstant().getNano());
	}



	@Override
	public int mkdir(String path, @mode_t long mode) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace(Thread.currentThread().getName()+" ) calling mkdir");

		ItemContainer<? extends Item> parentContainer;

		if (path.substring(1).contains("/")) {				
			String parentPath = Paths.get(path).getParent().toString();
			parentContainer= pathUtils.getPath(parentPath);
		} else parentContainer = rootDirectory;

		FolderContainer parentDir = (FolderContainer) parentContainer;
		String dirName= pathUtils.getLastComponent(path);
		try {
			parentDir.newFolder(dirName,dirName );
			return 0;
		} catch (Exception e) {
			logger.error("error in mkdir",e);
			return -ErrorCodes.ENOENT();

		}		
	}

	/*
	 * fileDownload
	 * @see ru.serce.jnrfuse.FuseStubFS#write(java.lang.String, jnr.ffi.Pointer, long, long, ru.serce.jnrfuse.struct.FuseFileInfo)
	 */
	@Override
	public int read(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace("!!! read called in path {} with size {} and offset {} and pointer address {}",path, size, offset, buf.address());

		SHFile fileDownload = null;

		boolean loop =false;
		do {
			synchronized (tempFiles) {
				if (tempFiles.containsKey(path) && tempFiles.get(path) instanceof FileUpload) {
					loop = true;
					logger.trace("upload not finished yet for {}",path);
				}
				else {
					loop=false;
				}
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

			}
		}while(loop);


		synchronized (tempFiles) {

			if (tempFiles.containsKey(path) && tempFiles.get(path) instanceof FileDownload) {
				logger.trace("path {} found in tmpFiles");
				fileDownload = tempFiles.get(path);
			} else {
				ItemContainer<? extends Item> item = pathUtils.getPath(path);
				if (item == null) {
					return -ErrorCodes.ENOENT();
				}
				if (item.getType()!=ContainerType.FILE) {
					return -ErrorCodes.EISDIR();
				}

				try {
					fileDownload = new FileDownload((FileContainer)item);
				} catch (Exception e) {
					logger.error("error reading remote file",e);
					return -ErrorCodes.ENOENT();
				}

				tempFiles.put(path, fileDownload);
			}
		}

		int toReturn =  fileDownload.read(buf, size, offset);

		logger.trace("!!! read ---- returning {}",toReturn);

		return toReturn;
	}


	/*
	 * list dir
	 * @see ru.serce.jnrfuse.FuseStubFS#readdir()
	 */
	@Override
	public int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi) {
		logger.trace("readdir called");
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		logger.trace(Thread.currentThread().getName()+" ) calling readdir "+path);
		if (path.contains(".Trash")) return 0;

		List<ItemContainer <? extends Item>> containers;

		if (path.equals("/"+VREFOLDERS_NAME)) {
			try {
				containers= client.getVREFolders().getContainers();
			}catch(StorageHubException she) {
				logger.error("error reading dir",she);
				return -ErrorCodes.EACCES();
			}
		}else {

			ItemContainer<? extends Item> container = pathUtils.getPath(path);
			if (container == null) {
				return -ErrorCodes.ENOENT();
			}
			if (!(container.getType()==ContainerType.FOLDER)) {
				return -ErrorCodes.ENOTDIR();
			}
			try { 
				logger.trace("reading folder "+path);
				containers = ((FolderContainer)container).list().withContent().getContainers();
				logger.trace("folder read "+path);
			}catch(UserNotAuthorizedException una) {
				logger.error("folder error ",una);
				return -ErrorCodes.EACCES();
			}catch(StorageHubException she) {
				logger.error("folder error ",she);
				return -ErrorCodes.EREMOTEIO();
			}catch(Throwable t) {
				logger.error("folder error ",t);
				throw new RuntimeException(t);
			}
		}
		filter.apply(buf, ".", null, 0);
		filter.apply(buf, "..", null, 0);

		for (ItemContainer <? extends Item> child : containers ) {
			try {
				Item it = child.get();
				String name = (it instanceof SharedFolder && ((SharedFolder)it).isVreFolder())? ((SharedFolder)it).getDisplayName() : it.getTitle();
				filter.apply(buf, name, null, 0);
				if (path.charAt(path.length() - 1)!='/')
					path+="/";
				cache.put(path+name, (ItemContainer<Item>) child);

			}catch (Exception e) {
				logger.error("error riding children ",e);
			}
		}

		logger.trace("tempFiles.entrySet() is empty ? {}",(tempFiles.entrySet().isEmpty()));

		for(Entry<String, SHFile> entry: tempFiles.entrySet()) {
			logger.trace("entry in temp map {}", entry.getKey());
			if (entry.getValue() instanceof FileUpload && pathUtils.getParentPath(entry.getKey()).equals(path)) {
				filter.apply(buf, pathUtils.getLastComponent(entry.getKey()), null, 0);
				logger.trace("last temp entry added {}", entry.getKey());
			}
		}

		if (path.equals("/")) filter.apply(buf, VREFOLDERS_NAME , null, 0); 

		return 0;

	}


	/*	@Override
	public int statfs(String path, Statvfs stbuf) {
		if (Platform.getNativePlatform().getOS() == WINDOWS) {
			// statfs needs to be implemented on Windows in order to allow for copying
			// data from other devices because winfsp calculates the volume size based
			// on the statvfs call.
			// see https://github.com/billziss-gh/winfsp/blob/14e6b402fe3360fdebcc78868de8df27622b565f/src/dll/fuse/fuse_intf.c#L654
			if ("/".equals(path)) {
				stbuf.f_blocks.set(1024 * 1024); // total data blocks in file system
				stbuf.f_frsize.set(1024);        // fs block size
				stbuf.f_bfree.set(1024 * 1024);  // free blocks in fs
			}
		}
		return super.statfs(path, stbuf);
	}
	 */


	@Override
	public int rename(String path, String newName) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		ItemContainer<? extends Item> folder = pathUtils.getPath(path);
		if (folder == null) {
			return -ErrorCodes.ENOENT();
		}
		ItemContainer<? extends Item> newParent = pathUtils.getPath(pathUtils.getParentPath(newName));
		if (newParent == null) {
			return -ErrorCodes.ENOENT();
		}
		if (newParent.getType()!=ContainerType.FOLDER) {
			return -ErrorCodes.ENOTDIR();
		}

		try {
			if (newParent.getId()!=folder.get().getParentId()) {
				folder.move((FolderContainer)newParent);
			} 

			if (!pathUtils.getLastComponent(newName).equals(pathUtils.getLastComponent(path)))
				folder.rename(pathUtils.getLastComponent(newName));
			cache.remove(path);
		}catch(UserNotAuthorizedException una) {
			return -ErrorCodes.EACCES();
		}catch(StorageHubException she) {
			return -ErrorCodes.EREMOTEIO();
		}


		return 0;
	}

	@Override
	public int rmdir(String path) {
		if (path.equals("/"+VREFOLDERS_NAME)) 
			return -ErrorCodes.EACCES(); 

		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		ItemContainer<? extends Item> folder = pathUtils.getPath(path);
		if (folder == null) {
			return -ErrorCodes.ENOENT();
		}
		if (folder.getType()!=ContainerType.FOLDER) {
			return -ErrorCodes.ENOTDIR();
		}
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);
		try {
			checkSpecialFolderRemove(path);

			if (folder.get() instanceof SharedFolder && ((SharedFolder) folder.get()).isVreFolder())
				return -ErrorCodes.EACCES(); 

			folder.delete();
			cache.remove(path);
		}catch(UserNotAuthorizedException una) {
			return -ErrorCodes.EACCES();
		}catch(StorageHubException she) {
			return -ErrorCodes.EREMOTEIO();
		}
		return 0;
	}

	public void checkSpecialFolderRemove(String path) throws UserNotAuthorizedException{
		if (path.equals(String.format("/%s", VREFOLDERS_NAME))) throw new UserNotAuthorizedException(VREFOLDERS_NAME+" cannot be deleted");
	}


	/*
	 * delete file
	 * @see ru.serce.jnrfuse.FuseStubFS#write(java.lang.String, jnr.ffi.Pointer, long, long, ru.serce.jnrfuse.struct.FuseFileInfo)
	 */
	@Override
	public int unlink(String path) {
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);

		ItemContainer<? extends Item> file = pathUtils.getPath(path);
		if (file == null) {
			return -ErrorCodes.ENOENT();
		}
		if (file.getType()!=ContainerType.FILE) {
			return -ErrorCodes.EISDIR();
		}
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);
		try {
			file.delete();
			cache.remove(path);
		}catch(UserNotAuthorizedException una) {
			return -ErrorCodes.EACCES();
		}catch(StorageHubException she) {
			return -ErrorCodes.EREMOTEIO();
		}
		return 0;
	}


	@Override
	public int readlink(String path, Pointer buf, @size_t long size) {
		logger.info("readlink called {}",path);
		return 0;
	}

	@Override
	public int open(String path, FuseFileInfo fi) {
		logger.info("open called {} {}",path, fi.fh.getMemory().address());
		return 0;
	}

	@Override
	public int release(String path, FuseFileInfo fi) {
		logger.info("release called {} {}",path, fi.fh.getMemory().address());
		return 0;
	}

	@Override
	public int truncate(String path, long size) {
		logger.info("truncate called {} ",path);

		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);
		cache.remove(path);
		uploadFile(path);
		return 0;
	}


	@Override
	public int access(String path, int mask) {
		logger.trace("access function called "+path+" "+mask);
		return 0;
	}

	@Override
	public int utimens(String path, Timespec[] timespec) {
		logger.trace("utimens called "+path);
		return 0;
	}



}
