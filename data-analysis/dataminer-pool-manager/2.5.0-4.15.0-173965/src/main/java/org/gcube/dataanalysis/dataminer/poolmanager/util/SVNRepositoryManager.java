package org.gcube.dataanalysis.dataminer.poolmanager.util;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNRepositoryManager {

	private SVNRepository svnRepository;
	private static  SVNRepositoryManager instance;
	private Logger logger;
	
	private SVNRepositoryManager (Configuration configuration) throws SVNException
	{
		this.logger = LoggerFactory.getLogger(SVNRepositoryManager.class);
		org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.SVNRepository repository = configuration.getSVNRepository();
		this.svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repository.getBaseUrl()));
		ISVNAuthenticationManager authManager = null;
		
		if (repository.getUsername() == null)
		{
			this.logger.debug("Using SVN default credentials");
			authManager  = SVNWCUtil.createDefaultAuthenticationManager();
		}
		else
		{
			this.logger.debug("Using IS credentials");
			authManager = SVNWCUtil.createDefaultAuthenticationManager(repository.getUsername(),repository.getPassword());

		}
		
		this.svnRepository.setAuthenticationManager(authManager);
	}
	

	public static SVNRepositoryManager getInstance (Configuration configuration) throws SVNException
	{
		if (instance == null) instance = new SVNRepositoryManager(configuration);
	
		return instance;
	}


	public SVNRepository getSvnRepository() {
		return svnRepository;
	}
	
	
	
}
