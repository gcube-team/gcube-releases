package org.gcube.rest.commons.db.app;

import javax.inject.Inject;

import com.google.inject.persist.PersistService;

public class ApplicationInitializer {

	PersistService service = null;

	@Inject
	ApplicationInitializer(final PersistService service) {
		this.service = service;
		service.start();

//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				try {
//					service.stop();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	public void stop() {
		this.service.stop();
	}

	// At this point JPA is started and ready.

	// other application initializations if necessary
}
