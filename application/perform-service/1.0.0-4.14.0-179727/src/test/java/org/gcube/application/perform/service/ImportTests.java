package org.gcube.application.perform.service;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class ImportTests extends CommonTest{
	
	Long farmid=12682101l;
	
	@Test
	public void submit() {
		/**
		 * http://perform.dev.d4science.org/perform-service/gcube/service/import?
		 * batch_type=HATCHERY_INDIVIDUAL&
		 * farmid=12682101&
		 * source=https://data1-d.d4science.org/shub/E_aUJUbDNzeUlLL29KL2xlZUloWFQ5TEdlZ0ZnZzlNNTVLNUEzeDVRNFVoVHlLMW5DVG5RbVVXVzlYeUUzZWFXRA==
		 * source_version=1.1
		 */
		
		WebTarget target=
				target(ServiceConstants.Import.PATH).				
				queryParam(ServiceConstants.Import.BATCH_TYPE_PARAMETER, "HATCHERY_INDIVIDUAL").
				queryParam(ServiceConstants.Import.FARM_ID_PARAMETER, farmid).
				queryParam(ServiceConstants.Import.EXCEL_FILE_PARAMETER, "https://data1-d.d4science.org/shub/E_aUJUbDNzeUlLL29KL2xlZUloWFQ5TEdlZ0ZnZzlNNTVLNUEzeDVRNFVoVHlLMW5DVG5RbVVXVzlYeUUzZWFXRA==").
				queryParam(ServiceConstants.Import.EXCEL_FILE_VERSION_PARAMETER, "1.1");
		
		System.out.println(target.getUri());
		Response resp=target.request().post(null);
		System.out.println("Status : "+resp.getStatus() );	
		try {
			Thread.sleep(1000*60*10);
		} catch (InterruptedException e) {
			
		}
	}

	
	@Test
	public void getAll() {
		WebTarget target=
				target(ServiceConstants.Import.PATH).
				path(12682549+"");
		System.out.println(target.getUri());
		Response resp=target.request().get();		
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));		
	}
	
	@Test
	public void getGrouped() {
		WebTarget target=
				target(ServiceConstants.Import.PATH).
				path(ServiceConstants.Import.LAST_METHOD).
				path(13625424+"");
		System.out.println(target.getUri());
		Response resp=target.request().get();		
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));		
	}
}
