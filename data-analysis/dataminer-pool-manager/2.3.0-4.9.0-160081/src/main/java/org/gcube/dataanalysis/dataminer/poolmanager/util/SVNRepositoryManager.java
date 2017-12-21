package org.gcube.dataanalysis.dataminer.poolmanager.util;

import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.Configuration;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNRepositoryManager {

	private SVNRepository svnRepository;
	private static  SVNRepositoryManager instance;
	
	private SVNRepositoryManager (Configuration configuration) throws SVNException
	{
		this.svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(configuration.getSVNRepository()));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
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
