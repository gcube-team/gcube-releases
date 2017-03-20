package gr.cite.commons.util.datarepository;

import java.io.File;
import java.util.List;

import gr.cite.commons.util.datarepository.elements.RepositoryFile;

public interface DataRepository
{
	public String persist(RepositoryFile file) throws Exception;
	public String update(RepositoryFile file) throws Exception;
	/**
	 * Retrieves a specific file. The local image of the file is not guaranteed to have its original name
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public RepositoryFile retrieve(String id) throws Exception;
	public void delete(String id) throws Exception;
	
	
	public List<String> listIds() throws Exception;
	
	
	public String persistToFolder(List<RepositoryFile> files) throws Exception;
	public String updateToFolder(RepositoryFile file, String folderId) throws Exception;
	public String updateToFolder(List<RepositoryFile> files, String folderId) throws Exception;
	public String addToFolder(RepositoryFile file, String folderId) throws Exception;
	public String addToFolder(List<RepositoryFile> files, String folderId) throws Exception;
	public File retrieveFolder(String folderId) throws Exception;
	public List<String> listFolder(String folderId) throws Exception;
	
	public long getTotalSize();
	public Long getLastSweep();
	public Long getSweepSizeReduction();
	
	public Long close(RepositoryFile file) throws Exception;
}
