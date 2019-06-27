package org.gcube.application.perform.service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class AnagraphicTests extends CommonTest{

	private static int CALLS=100;



	@Test
	public void getBatch() {
		WebTarget target=		
				//				target(ServiceConstants.SERVICE_NAME).
				//				path(ServiceConstants.APPLICATION_PATH).
				target(ServiceConstants.Mappings.PATH).
				path(ServiceConstants.Mappings.BATCHES_METHOD).
				queryParam(ServiceConstants.Mappings.BATCH_NAME_PARAMETER, "gino").
				queryParam(ServiceConstants.Mappings.BATCH_TYPE_PARAMETER, "pino").				
				queryParam(ServiceConstants.Mappings.FARM_ID_PARAMETER, 13639641);

		System.out.println(target.getUri());		
		Response resp=target.request().get();
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));		
	}

	@Test
	public void getFarm() {
		WebTarget target=		
				//				target(ServiceConstants.SERVICE_NAME).
				//				path(ServiceConstants.APPLICATION_PATH).
				target(ServiceConstants.Mappings.PATH).
				path(ServiceConstants.Mappings.FARM_METHOD).
				queryParam(ServiceConstants.Mappings.FARM_ID_PARAMETER, 13639641).
				queryParam(ServiceConstants.Mappings.FARM_UUID_PARAMETER, "pino");				

		System.out.println(target.getUri());
		
		Response resp=target.request().get();
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));		
	}

	@Test
	public void parallelRequests() {
		final AtomicLong currentExecution=new AtomicLong(0);
		final Semaphore sem=new Semaphore((CALLS-1)*(-1));
		final WebTarget farmTarget=target(ServiceConstants.Mappings.PATH).
				path(ServiceConstants.Mappings.FARM_METHOD).
				queryParam(ServiceConstants.Mappings.FARM_ID_PARAMETER, 126825).
				queryParam(ServiceConstants.Mappings.FARM_UUID_PARAMETER, "pino");
		final WebTarget batchesTarget=target(ServiceConstants.Mappings.PATH).
				path(ServiceConstants.Mappings.BATCHES_METHOD).
				queryParam(ServiceConstants.Mappings.BATCH_NAME_PARAMETER, "gino").
				queryParam(ServiceConstants.Mappings.BATCH_TYPE_PARAMETER, "pino").
				queryParam(ServiceConstants.Mappings.FARM_ID_PARAMETER, 12682549);
		for(int i=0;i<CALLS;i++) {
			Thread t=new Thread() {
				@Override
				public void run() {
					farmTarget.request().get();
					batchesTarget.request().get();
					System.out.println("Performed "+currentExecution.incrementAndGet());
					sem.release();
				}
			};
			t.setDaemon(true);
			t.start();
		}
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			System.out.println("COMPLETED");
		}
	}
}
