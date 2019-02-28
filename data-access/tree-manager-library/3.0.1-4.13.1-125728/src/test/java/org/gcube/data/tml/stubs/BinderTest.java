package org.gcube.data.tml.stubs;

import static junit.framework.Assert.*;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.data.tml.Constants.*;
import static org.gcube.data.tml.TestUtils.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.mycontainer.Scope;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tm.stubs.BindParameters;
import org.gcube.data.tm.stubs.InvalidRequestFault;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tm.stubs.SourceBindings;
import org.gcube.data.tm.utils.BindParametersWrapper;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.stubs.Types.BindingsHolder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)  
@Scope("/gcube/devsec")
public class BinderTest {

	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/tree-manager-service.gar"));
		
	static TBinderStub binderStub;
	
	@Inject
	static MyContainer container;
	
	@Named(binderWSDDName)
	static URI binderAddress;
	
	@Named(readerWSDDName)
	static EndpointReferenceType readerEpr;
	
	@Named(writerWSDDName)
	static EndpointReferenceType writerEpr;
	
	@BeforeClass
	public static void setup() {
	
		//setProxy("localhost",8081); //comment after on-the-wire analysis
		
		binderStub = stubFor(binder).at(binderAddress);
	}
	
	@Test
	public void bind() throws Exception {

		//input
		final BindRequest params = new BindRequest("plugin",newDocument().createElement("payload"));
		
		//output
		SourceBinding binding = new SourceBinding(readerEpr,"source",writerEpr);
		final SourceBindings output = new SourceBindings(new SourceBinding[]{binding});
		
		TBinderService mock  = new TBinderService() {
			
			public SourceBindings bind(BindParameters input) throws InvalidRequestFault, GCUBEFault {
				
				BindParametersWrapper wrapper = new BindParametersWrapper(input);
				assertEquals("plugin",wrapper.getPlugin());
				assertEquals(false,wrapper.isBroadcast());
				
				assertEquals("payload",wrapper.getPayload().getLocalName());
				
				return output;
			}
		};
		
		container.setEndpoint(binderWSDDName,mock);

		//exercise
		BindingsHolder holder = binderStub.bind(params);
		
		assertEquals("source",holder.bindings.get(0).source());
		assertNotNull(holder.bindings.get(0).readerRef());
		assertNotNull(holder.bindings.get(0).writerRef());
		
	}
	
	@Test(expected=org.gcube.data.tml.stubs.Types.InvalidRequestFault.class)
	public void bindErrors() throws Exception {

		//prepare input
		final BindRequest params = new BindRequest("plugin",newDocument().createElement("payload"));
		
		TBinderService mock  = mock(TBinderService.class);
		when(mock.bind(any(BindParameters.class))).thenThrow(new InvalidRequestFault());
		
		//install mock
		container.setEndpoint(binderWSDDName,mock);

		//exercise
		binderStub.bind(params);
				
	}
}
