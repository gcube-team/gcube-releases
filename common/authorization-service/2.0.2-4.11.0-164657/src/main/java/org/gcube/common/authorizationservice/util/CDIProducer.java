package org.gcube.common.authorizationservice.util;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CDIProducer {

	@Produces
	EntityManagerFactory createFactory(){
		return Persistence.createEntityManagerFactory("authorization");
	}
	
}
