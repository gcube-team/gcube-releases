package org.gcube.vremanagement.softwaregateway.impl.repositorymanager;

public class RepositoryManagerFactory {
	
	NexusRepositoryManager nexus;
	
	public RepositoryManager getRepositoryManager(String [] servers, boolean cache){
		if(nexus==null)
			nexus= new NexusRepositoryManager(servers, cache);
		return nexus;
	}

}
