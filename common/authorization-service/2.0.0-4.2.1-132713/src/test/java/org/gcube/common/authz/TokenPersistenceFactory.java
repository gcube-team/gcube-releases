package org.gcube.common.authz;

import javax.persistence.Persistence;

import org.gcube.common.authorizationservice.persistence.RelationDBPersistence;
import org.gcube.common.authorizationservice.util.TokenPersistence;
import org.glassfish.hk2.api.Factory;

public class TokenPersistenceFactory implements Factory<TokenPersistence> {

	@Override
	public void dispose(TokenPersistence arg0) {		
	}

	@Override
	public TokenPersistence provide() {
		RelationDBPersistence rdbp = new RelationDBPersistence();
		rdbp.setEntitymanagerFactory(Persistence.createEntityManagerFactory("TestPersistence"));
		return rdbp;
		
	}

}
