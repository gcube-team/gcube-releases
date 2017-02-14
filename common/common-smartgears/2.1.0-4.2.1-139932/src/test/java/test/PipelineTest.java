package test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.gcube.smartgears.handlers.application.ApplicationEvent;
import org.gcube.smartgears.handlers.application.ApplicationHandler;
import org.gcube.smartgears.handlers.application.ApplicationPipeline;
import org.junit.Test;

public class PipelineTest {

	
	@Test
	@SuppressWarnings("all")
	public void pipelineforwards() {
		
		List<ApplicationHandler> mocks = new ArrayList<ApplicationHandler>();
		
		final ApplicationEvent e  = mock(ApplicationEvent.class);
		
		for (int i=0;i<3;i++)
			mocks.add(mock(ApplicationHandler.class));
		
		final ApplicationPipeline pipeline = new ApplicationPipeline(mocks);
		
		//test
		pipeline.forward(e);
		pipeline.forward(e);
		
		for (ApplicationHandler mock : mocks)
			verify(mock,times(2)).onEvent(e);
		
		

	}
	
	@Test
	@SuppressWarnings("all") //a regression test!
	public void resetsOnFailures() {
		
		List<ApplicationHandler> mocks = new ArrayList<ApplicationHandler>();
		
		final ApplicationEvent e  = mock(ApplicationEvent.class);
		
		for (int i=0;i<3;i++)
			mocks.add(mock(ApplicationHandler.class));
		
		//first time fails, second does not
		doThrow(new RuntimeException()).doNothing().when(mocks.get(1)).onEvent(anyObject());
		
		final ApplicationPipeline pipeline = new ApplicationPipeline(mocks);
		
		//test
		try {
			pipeline.forward(e);
			fail();
		}
		catch(Exception ex) {
			
		}
		
		pipeline.forward(e);
		
		//the first mock is invoked both times because cursor is reset even with failures
		verify(mocks.get(0),times(2)).onEvent(e);
		
		

	}
}
