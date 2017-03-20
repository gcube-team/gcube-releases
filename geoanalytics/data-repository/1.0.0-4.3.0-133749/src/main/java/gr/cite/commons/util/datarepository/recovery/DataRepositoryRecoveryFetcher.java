package gr.cite.commons.util.datarepository.recovery;

import java.util.Map;

import gr.cite.commons.util.datarepository.elements.RepositoryFile;

public interface DataRepositoryRecoveryFetcher {
	public Map<String, RepositoryFile> fetch() throws Exception;
}
