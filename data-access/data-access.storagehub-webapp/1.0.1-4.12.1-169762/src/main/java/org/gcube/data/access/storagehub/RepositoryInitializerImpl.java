package org.gcube.data.access.storagehub;

import javax.inject.Singleton;
import javax.jcr.Repository;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.gcube.data.access.storagehub.services.RepositoryInitializer;

@Singleton
public class RepositoryInitializerImpl implements RepositoryInitializer{

	private Repository repository;
	
	@Override
	public Repository getRepository(){
		return repository;
	}
	
	public RepositoryInitializerImpl() throws Exception{
		InitialContext context = new InitialContext();
		Context environment = (Context) context.lookup("java:comp/env");
		repository = (Repository) environment.lookup("jcr/repository");
	}
	





}
