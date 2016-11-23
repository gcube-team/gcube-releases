package gr.cite.commons.util.datarepository.elements;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlSeeAlso(FolderInfo.class)
public class RepositoryRegistry
{
	private long totalSize = 0;
	private Long lastSweep = null;
	private Long lastSweepSizeReduction = null;
	
	private final String fileRepositoryPath;
	
	private List<FolderInfo> folderInfo = new ArrayList<FolderInfo>();

	@XmlTransient private Map<String, FolderInfo> dictionary = new HashMap<String, FolderInfo>();
	@XmlTransient private Map<String, FolderInfo> folderLookup = new HashMap<String, FolderInfo>();
	
	private RepositoryRegistry() {
		fileRepositoryPath = null;
	}

	public RepositoryRegistry(String fileRepositoryPath) {
		this.fileRepositoryPath = fileRepositoryPath;
	}

	@XmlElementWrapper(name = "folders")
	@XmlElement(name="folder")
	public List<FolderInfo> getFolderInfo()
	{
		return this.folderInfo;
	}
	
	public void setFolderInfo(List<FolderInfo> folderInfo)
	{
		this.folderInfo = folderInfo;
		
		this.dictionary = new HashMap<String, FolderInfo>();
		for(FolderInfo f : folderInfo)
		{
			this.folderLookup.put(f.getId(), f);
			for(RepositoryFile file : f.getFiles())
				this.dictionary.put(file.getId(), f);
		}
	}
	
	public long getTotalSize()
	{
		return totalSize;
	}

	@XmlElement
	public void setTotalSize(long totalSize)
	{
		this.totalSize = totalSize;
	}
	
	public Long getLastSweep()
	{
		return lastSweep;
	}
	
	@XmlElement(required=false)
	public void setLastSweep(long lastSweep)
	{
		this.lastSweep = lastSweep;
	}
	
	public Long getLastSweepSizeReduction()
	{
		return lastSweepSizeReduction;
	}
	
	@XmlElement(required=false)
	public void setLastSweepSizeReduction(long lastSweepSizeReduction)
	{
		this.lastSweepSizeReduction = lastSweepSizeReduction;
	}
	
	public void addFolder(String id, URI uri) throws Exception
	{
		if(this.folderLookup.containsKey(id)) throw new Exception("Folder " + id + " already exists");
		FolderInfo f = new FolderInfo();
		f.setId(id);
		f.setUri(uri);
		folderInfo.add(f);
		this.folderLookup.put(id, f);
	}
	
	public void removeFolderIfEmpty(String id) throws Exception
	{
		FolderInfo f = this.folderLookup.get(id);
		if(f == null) throw new Exception("Folder " + id + " does not exist");
		
		if(!f.getFiles().isEmpty()) throw new Exception("Folder " + id + " is not empty");
		
		folderInfo.remove(f);
		folderLookup.remove(id);
	}
	
	public void removeFolder(String id) throws Exception
	{
		FolderInfo f = folderLookup.get(id);
		if(f == null) throw new Exception("Folder " + id + " does not exist");
		
		folderInfo.remove(f);
		folderLookup.remove(id);
		for(RepositoryFile file : f.getFiles()) totalSize -= file.getSize();
	}
	
	public List<String> listIds() throws Exception
	{
		List<String> list = new ArrayList<String>();
		Iterator iterator = dictionary.keySet().iterator();
		while(iterator.hasNext()){
		  String key   = (String) iterator.next();
		  Object value = dictionary.get(key);
		  list.add(key);
		}
		return list;
	}
	
	public void addFile(RepositoryFile file, String folderId) throws Exception
	{
		FolderInfo f = folderLookup.get(folderId);

		if(f == null) throw new Exception("Folder " + folderId + " does not exist");
		if(dictionary.containsKey(file.getId())) throw new Exception("File " + file.getId() + " already exists");
		this.dictionary.put(file.getId(), f);
		f.addFile(file);
		
		this.totalSize += file.getSize();
	}
	
	public void removeFile(String id, String folderId) throws Exception
	{
		FolderInfo f = folderLookup.get(folderId);
		if(f == null) throw new Exception("Folder " + folderId + " does not exist");
		if(!dictionary.containsKey(id)) throw new Exception("File " + id + "does not exist");
		this.dictionary.remove(id);
		this.totalSize -= f.getFile(id).getSize();
		f.removeFile(id);
	}
	
	/**
	 * Returns folder id
	 * 
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	public String lookup(String fileId) throws Exception
	{
		FolderInfo f = dictionary.get(fileId);
		if(f == null) return null;
		else return f.getId();
	}
	
	/**
	 * 
	 */
	public FolderInfo lookupFolder(String folderId) throws Exception
	{
		return this.folderLookup.get(folderId);
	}

	public void createLookups()
	{
		this.dictionary = new HashMap<String, FolderInfo>();
		for(FolderInfo f : folderInfo)
		{
			f.createLookups();
			this.folderLookup.put(f.getId(), f);
			for(RepositoryFile file : f.getFiles())
				this.dictionary.put(file.getId(), f);
		}
	}
	
}