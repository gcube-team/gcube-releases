package gr.cite.commons.util.datarepository.filesystem;

import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.UUIDGenerator;
import gr.cite.commons.util.datarepository.elements.FolderInfo;
import gr.cite.commons.util.datarepository.elements.RelativePathAdapter;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;
import gr.cite.commons.util.datarepository.elements.RepositoryRegistry;
import gr.cite.commons.util.datarepository.recovery.DataRepositoryRecoveryFetcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;

//@Component
public class FileSystemDataRepository implements DataRepository {
	private static final Logger log = LoggerFactory.getLogger(FileSystemDataRepository.class);

	private static final String StashFolderName = "stash";

	private static final String sizeThresholdParameterName = "sizeThreshold";
	private static final String shortSweepPeriodParameterName = "shortSweepPeriod";
	private static final String shortSweepPeriodUnitParameterName = "shortSweepPeriodUnit";
	private static final String longSweepPeriodParameterName = "longSweepPeriod";
	private static final String longSweepPeriodUnitParameterName = "longSweepPeriodUnit";
	private static final String recoverParameterName = "recover";

	private static final long sizeThresholdDefault = 1000000000;
	private static final long shortSweepPeriodDefault = 2;
	private static final TimeUnit shortSweepPeriodUnitDefault = TimeUnit.HOURS;
	private static final long longSweepPeriodDefault = 5;
	private static final TimeUnit longSweepPeriodUnitDefault = TimeUnit.HOURS;
	private static final boolean recoverDefault = false;

	private long sizeThreshold = sizeThresholdDefault;
	private long shortSweepPeriod = shortSweepPeriodDefault;
	private TimeUnit shortSweepPeriodUnit = shortSweepPeriodUnitDefault;
	private long longSweepPeriod = longSweepPeriodDefault;
	private TimeUnit longSweepPeriodUnit = longSweepPeriodUnitDefault;
	private boolean recover = recoverDefault;
	private long lastSweep = new Date().getTime();
	boolean sweeped = false;
	private long sizeReduction = -1;
	
	private boolean checkForRecovery = false;

	private ConcurrentHashMap<RepositoryFile, Future<Long>> future = new ConcurrentHashMap<RepositoryFile, Future<Long>>();
	private RepositoryRegistry registry = null;
	private File registryFile = null;
	private Marshaller m = null;
	private Unmarshaller um = null;

	private String fileRepositoryPath = null;

	DataRepositoryRecoveryFetcher recoveryFetcher = null;

	@Resource(name = "fileRepositoryConfig")
	public void setConfig(Map<String, String> params) throws Exception {
		if (!params.containsKey("fileRepositoryPath"))
			throw new Exception("No base path found for file data repository");
		this.fileRepositoryPath = params.get("fileRepositoryPath");

		if (params.containsKey(sizeThresholdParameterName))
			this.sizeThreshold = Long.parseLong(params.get(sizeThresholdParameterName));

		if (params.containsKey(shortSweepPeriodParameterName))
			this.shortSweepPeriod = Long.parseLong(params.get(shortSweepPeriodParameterName));

		if (params.containsKey(shortSweepPeriodUnitParameterName))
			this.shortSweepPeriodUnit = TimeUnit.valueOf(params.get(shortSweepPeriodUnitParameterName));

		if (params.containsKey(longSweepPeriodParameterName))
			this.longSweepPeriod = Long.parseLong(params.get(longSweepPeriodParameterName));

		if (params.containsKey(longSweepPeriodUnitParameterName))
			this.longSweepPeriodUnit = TimeUnit.valueOf(params.get(longSweepPeriodUnitParameterName));

		if(params.containsKey(recoverParameterName))
			this.recover = Boolean.valueOf(params.get(recoverParameterName));
		
		JAXBContext ctx = JAXBContext.newInstance(RepositoryRegistry.class);
		RelativePathAdapter rpa = new RelativePathAdapter(fileRepositoryPath);

		m = ctx.createMarshaller();
		m.setAdapter(RelativePathAdapter.class, rpa);
		um = ctx.createUnmarshaller();
		um.setAdapter(RelativePathAdapter.class, rpa);

		boolean createdRepositoryFolder = false;
		File rep = new File(this.fileRepositoryPath);
		if (!rep.exists()) {
			rep.mkdir();
			createdRepositoryFolder = true;
		}

		boolean createdRegistry = false;
		registryFile = new File(rep, "registry.xml");
		if (!registryFile.exists()) {
			createdRegistry = true;
			registry = new RepositoryRegistry(fileRepositoryPath);
			m.marshal(registry, registryFile);
			registryFile.createNewFile();
		}
		
		if(this.recover && !createdRepositoryFolder && createdRegistry)
			checkForRecovery = true;
		
		registry = (RepositoryRegistry) um.unmarshal(registryFile);
			
		registry.createLookups();

		if (registry.getLastSweep() != null)
			lastSweep = registry.getLastSweep();

		if (registry.getLastSweepSizeReduction() != null)
			sizeReduction = registry.getLastSweepSizeReduction();

		File stashFolder = new File(rep, StashFolderName);
		if (!stashFolder.exists())
			stashFolder.mkdir();
		if (createdRegistry) {
			registry.addFolder(StashFolderName, stashFolder.toURI());
			m.marshal(registry, registryFile);
		}
	}
	
	@PostConstruct
	private void recoverIfNecessary() {
		if(checkForRecovery) {
			try
			{
				recover();
			}catch(Exception e) {
				log.error("An error has occurred while recovering from error. Give up.", e);
			}
		}
		checkForRecovery = false;
	}
	
	public void setRecoveryFetcher(DataRepositoryRecoveryFetcher fetcher)
	{
		this.recoveryFetcher = fetcher;
	}
	
	public Long getLastSweep() {
		return sweeped ? lastSweep : null;
	}

	public Long getSweepSizeReduction() {
		return sizeReduction != -1 ? sizeReduction : null;
	}

	private void sweep() throws Exception {
		Set<FolderInfo> toDelete = new HashSet<FolderInfo>();
		Set<URI> toDeleteFiles = new HashSet<URI>();
		Set<URI> toDeleteFolders = new HashSet<URI>();
		long oldSize = -1;
		long newSize = -1;
		long now = new Date().getTime();

		synchronized (registry) {
			oldSize = registry.getTotalSize();
			if (log.isInfoEnabled())
				log.info("File system repository sweep start: Initial repository size= " + registry.getTotalSize());

			for (FolderInfo folder : registry.getFolderInfo()) {
				List<String> toDeleteF = new ArrayList<String>();
				for (RepositoryFile file : folder.getFiles()) {
					if (!file.isPermanent()
							&& (now - file.getTimestamp() > TimeUnit.MILLISECONDS.convert(longSweepPeriod,
									longSweepPeriodUnit))) {
						if (file.getLocalImage() == null)
							throw new Exception("Could not find local file " + file.getId() + " in repository");
						if (!file.getLocalImage().getScheme().equals("file"))
							throw new Exception("The URI " + file.getLocalImage() + " of local file " + file.getId()
									+ " is not of the correct type");
						toDeleteF.add(file.getId());
						toDeleteFiles.add(file.getLocalImage());
					}
				}
				for (String id : toDeleteF)
					registry.removeFile(id, folder.getId());

				if (folder.getFiles().isEmpty() && !folder.getUri().toString().endsWith(StashFolderName)
						&& !folder.getUri().toString().endsWith(StashFolderName + "/")) {
					toDelete.add(folder);
					toDeleteFolders.add(folder.getUri());
				}
			}
			for (FolderInfo d : toDelete)
				registry.removeFolder(d.getId());
			newSize = registry.getTotalSize();
			lastSweep = new Date().getTime();
			sweeped = true;
			sizeReduction = oldSize - newSize;

			registry.setLastSweep(lastSweep);
			registry.setLastSweepSizeReduction(sizeReduction);

			m.marshal(registry, registryFile);
		}

		for (URI f : toDeleteFiles)
			new File(f).delete();
		for (URI fol : toDeleteFolders) {
			File dir = new File(fol);
			File[] dirFiles = dir.listFiles();
			for (File f : dirFiles)
				f.delete(); // delete files created by user processes which are
							// not recorded by the repository
			new File(fol).delete();
		}

		if (log.isInfoEnabled())
			log.info("File system data repository sweep end: Removed " + toDeleteFiles.size() + " files and "
					+ toDeleteFolders.size() + " directories. Repository size= " + newSize);
	}

	/**
	 * Persists to stash
	 * After the stream has been fully consumed, {@link RepositoryFile}'s stream is reset so that it can be opened anew when requested via {@link RepositoryFile#getInputStream()}
	 * and {@link RepositoryFile#getLocalImage()} will also be valid
	 * Note that the stream is consumed asynchronously, and can be forced by blocking method {@link #close(RepositoryFile)}
	 */
	@Override
	public String persist(RepositoryFile f) throws Exception {
		return persistToFolder(f, StashFolderName, true, false);
	}

	@Override
	public String update(RepositoryFile f) throws Exception {
		return updateToFolder(f, StashFolderName);
	}

	@Override
	public Long close(RepositoryFile file) throws Exception {
		if (!future.containsKey(file))
			throw new Exception("The file could not be located in pending files");
		Future<Long> f = future.get(file);
		long size = 0;
		try {
			size = f.get();
			file.setSize(size);
			file.markPersisted();
		} catch (ExecutionException e) {
			throw e;
		} finally {
			future.remove(file);
		}
		return new Long(size);
	}

	/**
	 * 
	 * @param f
	 * @param folderId
	 * @param createFile
	 * @param createFolder
	 * @return
	 * @throws Exception
	 */
	/*
	 * A thread execute read from BufferedInputStream. It's feature return the
	 * size that has been created after read - write on FileSystem
	 */
	@SuppressWarnings("resource")
	private String persistToFolder(final RepositoryFile f, String folderId, boolean createFile, boolean createFolder)
			throws Exception {
		if (f.getId() == null) {
			UUID id = UUIDGenerator.randomUUID();
			f.setId(id.toString());
		}

		if (createFolder)
			folderId = UUIDGenerator.randomUUID().toString();
		File folder = new File(new File(fileRepositoryPath), folderId);
		if (!folderId.equals(StashFolderName) && createFolder)
			folder.mkdir();
		File store = new File(folder, f.getId());
		URI folderUri = folder.toURI();

		if (createFile) {
			store.createNewFile();
			f.setLocalImage(store.toURI());
			f.setTimestamp(new Date().getTime());
		}

		InputStream is = null;
		if (f.getInputStream() != null)
			is = f.getInputStream();
		else if (f.getLocalImage() != null)
			is = f.getLocalImage().toURL().openStream();
		else
			throw new Exception("Could not open input stream for file");

		final BufferedInputStream bis = new BufferedInputStream(is);

		final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(store));

		ExecutorService executor = Executors.newFixedThreadPool(5);

		future.put(f, executor.submit(new Callable<Long>() {
			public Long call() throws IOException {
				long size = 0;
				try {

					byte[] buffer = new byte[1024];
					int bread = 0;
					while ((bread = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, bread);
						size += (long) bread;
					}
				} catch (IOException io) {
					Thread.currentThread().interrupt();
					throw io;
				} finally {
					bis.close();
					bos.close();
					
					/**
					 * @author ikavvouras
					 * UGLY: had to be nulled in order to destroy the last reference of ByteArrayInpytStream.class 
					 */
					f.setInputStream(null);
					
					f.markPersisted();
				}
				return size;
			}
		}));
		executor.shutdown();

		if (createFile || createFolder) {
			synchronized (registry) {
				if (!folderId.equals(StashFolderName) && createFolder) {
					registry.addFolder(folderId, folderUri);
				}
				if (createFile)
					registry.addFile(f, folderId);
				m.marshal(registry, registryFile);
			}
		}
		long now = new Date().getTime();
		if ((registry.getTotalSize() > sizeThreshold && (now - lastSweep > TimeUnit.MILLISECONDS.convert(
				shortSweepPeriod, shortSweepPeriodUnit)))
				|| (now - lastSweep > TimeUnit.MILLISECONDS.convert(longSweepPeriod, longSweepPeriodUnit)))
			sweep();
		return f.getId().toString();
	}

	@Override
	public List<String> listIds() throws Exception {
		return registry.listIds();
	}

	@Override
	public RepositoryFile retrieve(String id) throws Exception {
		String folderId = null;
		RepositoryFile f = null;
		synchronized (registry) {
			folderId = registry.lookup(id);
			FolderInfo fi = registry.lookupFolder(folderId);
			if (fi == null)
				return null;
			f = fi.getFile(id);
			if(f == null)
				return null;
		}
		RepositoryFile ret = new RepositoryFile();
		ret.setLocalImage(f.getLocalImage());
		ret.setDataType(f.getDataType());
		ret.setOriginalName(f.getOriginalName());
		ret.setSize(f.getSize());
		ret.setId(f.getId());
		ret.markPersisted();
		return ret;
	}

	private String updateToFolder(List<RepositoryFile> files, String folderId, boolean createFiles, boolean createFolder)
			throws Exception {
		if (createFolder)
			folderId = UUIDGenerator.randomUUID().toString();
		File folder = new File(new File(fileRepositoryPath), folderId); // Do
																		// not
																		// lookup
																		// registry,
																		// find
																		// the
																		// folder
																		// directly
																		// in
																		// the
																		// file
																		// system
																		// instead
		if (createFolder && folder.exists())
			throw new Exception("Folder " + folderId + " already exists");
		else if (!createFolder && !folder.exists())
			throw new Exception("Folder " + folderId + " does not exist");

		URI folderUri = folder.toURI();
		if (createFolder)
			folder.mkdir();

		boolean updated = false;
		for (RepositoryFile f : files) {
			UUID id = UUIDGenerator.randomUUID();

			File store = null;
			if (createFiles) {
				f.setId(id.toString());
				store = new File(folder, f.getId());
				store.createNewFile();
				f.setLocalImage(store.toURI());
				f.setTimestamp(new Date().getTime());
			} else {
				RepositoryFile ex = null;
				synchronized (registry) {
					ex = registry.lookupFolder(folderId).getFile(f.getId()); // TODO
																				// null!
																				// correct
					ex.setDataType(f.getDataType());
					ex.setSize(f.getSize());
					System.out.println("---->" + ex.getSize());
					ex.setOriginalName(f.getOriginalName());
					ex.setTimestamp(new Date().getTime());
					updated = true;
				}
				store = new File(ex.getLocalImage());
			}
			if (f.hasInputStream()) {
				InputStream is = f.getInputStream();

				BufferedInputStream bis = new BufferedInputStream(is);

				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(store));

				try {
					byte[] buffer = new byte[1024];
					int bread = 0;
					while ((bread = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, bread);
					}
				} finally {
					bis.close();
					bos.close();
					f.markPersisted();
				}
			}
		}
		if (createFolder || createFiles || updated) {
			synchronized (registry) {
				if (createFolder)
					registry.addFolder(folderId, folderUri);
				if (createFiles) {
					for (RepositoryFile f : files)
						registry.addFile(f, folderId);
				}
				m.marshal(registry, registryFile);
			}
		}
		long now = new Date().getTime();
		if ((registry.getTotalSize() > sizeThreshold && (now - lastSweep > TimeUnit.MILLISECONDS.convert(
				shortSweepPeriod, shortSweepPeriodUnit)))
				|| (now - lastSweep > TimeUnit.MILLISECONDS.convert(longSweepPeriod, longSweepPeriodUnit)))
			sweep();
		return folderId;
	}

	/**
	 * Persists file to folder with id folderId.
	 * After the stream has been fully consumed, {@link RepositoryFile}'s stream is reset so that it can be opened anew when requested via {@link RepositoryFile#getInputStream()}
	 * and {@link RepositoryFile#getLocalImage()} will also be valid
	 * TODO asynchronous stream read
	 * 
	 * @param f
	 * @param folderId
	 * @param createFile
	 * @param createFolder
	 * @return
	 * @throws Exception
	 */
	@Override
	public String persistToFolder(List<RepositoryFile> files) throws Exception {
		return updateToFolder(files, null, true, true);
	}

	@Override
	public String updateToFolder(RepositoryFile file, String folderId) throws Exception {
		return updateToFolder(Collections.singletonList(file), folderId, false, false);
	}

	@Override
	public String updateToFolder(List<RepositoryFile> files, String folderId) throws Exception {
		return updateToFolder(files, folderId, false, false);
	}

	@Override
	public String addToFolder(RepositoryFile file, String folderId) throws Exception {
		return updateToFolder(Collections.singletonList(file), folderId, false, true);
	}

	@Override
	public String addToFolder(List<RepositoryFile> files, String folderId) throws Exception {
		return updateToFolder(files, folderId, false, true);
	}

	@Override
	public List<String> listFolder(String folderId) throws Exception {
		List<String> ids = new ArrayList<String>();
		synchronized (registry) {
			FolderInfo f = registry.lookupFolder(folderId);
			if (folderId == null)
				throw new Exception("Folder " + folderId + " does not exist");
			Set<RepositoryFile> files = f.getFiles();
			for (RepositoryFile file : files)
				ids.add(file.getId());
		}
		return ids;
	}

	@Override
	public File retrieveFolder(String folderId) throws Exception {

		Map<String, String> toRename = new HashMap<String, String>();
		File base = new File(new File(this.fileRepositoryPath), folderId);

		synchronized (registry) {
			FolderInfo fi = registry.lookupFolder(folderId);
			if (fi == null)
				return null;
			for (RepositoryFile f : fi.getFiles()) {
				if (!f.getLocalImage().toString().equals(f.getOriginalName())) {
					f.setLocalImage(new File(base, f.getOriginalName()).toURI());
					toRename.put(f.getId(), f.getOriginalName());
				}
			}
			if (!toRename.isEmpty())
				m.marshal(registry, registryFile);
		}

		for (Map.Entry<String, String> e : toRename.entrySet()) {
			File oldFile = new File(base, e.getKey());
			File newFile = new File(base, e.getValue());
			oldFile.renameTo(newFile);
		}

		return base;
	}

	@Override
	public void delete(String id) throws Exception {
		String folderId = null;
		URI fileUri = null;
		synchronized (registry) {
			folderId = registry.lookup(id);
			if (folderId == null)
				throw new Exception("Could not find a folder containing file " + id);
			FolderInfo fi = registry.lookupFolder(folderId);
			RepositoryFile rf = fi.getFile(id);
			fileUri = rf.getLocalImage();
			fi.removeFile(id);
			registry.setTotalSize(registry.getTotalSize() - rf.getSize());
			m.marshal(registry, registryFile);
		}
		File f = new File(fileUri);
		if (!f.exists())
			throw new Exception("Could not locate file " + fileUri);
		f.delete();
	}

	@Override
	public long getTotalSize() {
		synchronized (registry) {
			return registry.getTotalSize();
		}
	}
	
	public void recover() throws Exception {
		
		Map<String, RepositoryFile> info = null;
		if(recoveryFetcher == null) {
			info = new HashMap<String, RepositoryFile>();
		} else {
			info = recoveryFetcher.fetch();
		}
		
		long now = new Date().getTime();
		File rep = new File(this.fileRepositoryPath);
		if (!rep.exists())
			rep.mkdir();
		
		registryFile = new File(rep, "registry.xml");
		if (!registryFile.exists()) {
			registry = new RepositoryRegistry(fileRepositoryPath);
			m.marshal(registry, registryFile);
			registryFile.createNewFile();
		}
		
		registry = (RepositoryRegistry) um.unmarshal(registryFile);
		
		registry.createLookups();
		
		Collection<File> existingFiles = FileUtils.listFiles(rep, 
				new NotFileFilter(new NameFileFilter("registry.xml")), DirectoryFileFilter.DIRECTORY);
		for(File existingFile: existingFiles) {
			RepositoryFile fileInfo = info.get(existingFile.getName());
			RepositoryFile rf = new RepositoryFile();
			rf.setId(existingFile.getName());
			rf.setOriginalName(fileInfo != null ? fileInfo.getOriginalName() : existingFile.getName());
			rf.setPermanent(fileInfo != null ? fileInfo.isPermanent() : true);
			rf.setSize(fileInfo != null ? fileInfo.getSize() : existingFile.length());
			rf.setTimestamp(fileInfo != null ? fileInfo.getTimestamp() : now);
			rf.setDataType(fileInfo != null ? fileInfo.getDataType() : "application/octet-stream");
			String folderId = existingFile.toPath().getParent().toString().replace(fileRepositoryPath, "").replace(fileRepositoryPath.replace("/", "\\"), "");
			if(folderId.startsWith("/") || folderId.startsWith("\\"))
				folderId = folderId.substring(1);
			File folder = new File(new File(fileRepositoryPath), folderId);
			
			File store = new File(folder, existingFile.getName());
			URI folderUri = folder.toURI();

			rf.setLocalImage(store.toURI());
			
			if(registry.lookupFolder(folderId) == null)
				registry.addFolder(folderId, folderUri);
			if(registry.lookup(rf.getId().toString()) == null)
				registry.addFile(rf, folderId);
		}
		
		m.marshal(registry, registryFile);
		
		registry = (RepositoryRegistry) um.unmarshal(registryFile);
		registry.createLookups();
	}
	
	public Marshaller getMarshaller() {
		return m;
	}

	public Unmarshaller getUnmarshaller() {
		return um;
	}

}
