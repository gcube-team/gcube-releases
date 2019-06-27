package org.gcube.data.publishing.gCatFeeder.service;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class ExecutionsTest extends BaseTest {

	
	@Test
	public void getAll() {
		WebTarget target=
				target(ServiceConstants.Executions.PATH);

		System.out.println(target.getUri());		
		Response resp=target.request().get();
		if(resp.getStatus()!=200) {
			System.err.println("GET ALL RESPONSE STATUS : "+resp.getStatus());			
			System.err.println(resp.readEntity(String.class));
			throw new RuntimeException("GetAll error should never happen");
		}
		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));
	}

	@Test
	public void submitALLAndPoll() {
		WebTarget target=
				target(ServiceConstants.Executions.PATH);			

		System.out.println(target.getUri());		
		Response resp=target.request().post(Entity.json(""));
//		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));
		
		ExecutionDescriptor desc=resp.readEntity(ExecutionDescriptor.class);
		Long id=desc.getId();

		waitForSuccess(id);
	}
	
	private void waitForSuccess(Long executionId) {
		WebTarget pollTarget=
				target(ServiceConstants.Executions.PATH).path(executionId+"");
		
		boolean end=false;
		do {
			Response pollResp=pollTarget.request().get();
			if(pollResp.getStatus()!=200) throw new RuntimeException("Unexpected status "+pollResp.getStatus()+" while polling. Msg : "+pollResp.readEntity(String.class));
			else {
				ExecutionDescriptor pollResult=pollResp.readEntity(ExecutionDescriptor.class);
				System.out.println("Current status : "+pollResult.getStatus());
				switch(pollResult.getStatus()) {
				case FAILED: 
				case STOPPED : throw new RuntimeException("Unexpected execution status "+pollResult.getStatus()); 
				case SUCCESS : {
					System.out.println(pollResult);
					end=true; break;
				}
				default : try {
						Thread.sleep(400);} catch (InterruptedException e) {}
				}
			}
		}while(!end);
	}
	
	
	@Test
	public void checkSimilar() {
		WebTarget target=
				target(ServiceConstants.Executions.PATH);			

		System.out.println(target.getUri());		
		Response resp=target.request().post(Entity.json(""));
//		System.out.println(resp.getStatus() + " : "+ resp.readEntity(String.class));
		
		ExecutionDescriptor desc=resp.readEntity(ExecutionDescriptor.class);
		Long id=desc.getId();
		resp=target.request().post(Entity.json(""));
		Assert.assertEquals(id, resp.readEntity(ExecutionDescriptor.class).getId());	
		waitForSuccess(id);
	}
	
	
	@Test
	public void wrongSubmission() {
		WebTarget target=
				target(ServiceConstants.Executions.PATH).
				queryParam(ServiceConstants.Executions.CATALOGUE_ID_PARAMETER, TestCommon.FAKE_CATALOGUE_ID+"_NOT");
		
		System.out.println(target.getUri());		
		Response resp=target.request().post(Entity.json(""));
		if(resp.getStatus()!=400) throw new RuntimeException("Expected ERROR STATUS 400 BUT received "+resp.getStatus());
	}
	
}
