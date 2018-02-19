/**
 * 
 */
package org.gcube.data.tm;

import static junit.framework.Assert.*;
import static org.gcube.data.tm.TestUtils.*;
import static org.gcube.data.tm.testplugin.PluginBuilder.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.inject.Named;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tm.context.TReaderContext;
import org.gcube.data.tm.context.TWriterContext;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tm.state.SourceHome;
import org.gcube.data.tm.state.SourceResource;
import org.gcube.data.tm.state.TBinderHome;
import org.gcube.data.tm.state.TReaderHome;
import org.gcube.data.tm.state.TWriterHome;
import org.gcube.data.tm.stubs.InvalidRequestFault;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceBinder;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author Fabio Simeoni
 * 
 */
@RunWith(MyContainerTestRunner.class)
public class BindTest {

	public static Logger log = LoggerFactory.getLogger("test");
	
	@Deployment
	static Gar gar = TestUtils.gar();
	
	@Named(Constants.TBINDER_NAME)
	static TBinderService binder;

	@BeforeClass
	public static void setup() throws Exception {

		serviceIsReady();

		setCurrentScope(devsec);
	}

	@Test
	public void sourceIsBoundPublishedAndPeristed() throws Exception {

		// stage plugin for test
		Source source = source().with(reader()).with(writer()).build();

		plugin().with(source).install();

		// exercise (broadcast induces serialisation of binder)
		SourceBinding binding = binder.bind(broadcastRequest).getBindings()[0];

		// outcome is as expected
		assertEquals(source.id(), binding.getSourceID());
		assertNotNull(binding.getReaderEndpoint());
		assertNotNull(binding.getWriterEndpoint());

		// source has been initialised and stored
		verify(source.lifecycle()).init();

		//verify properties
		resourceIsAccessible(binding.getReaderEndpoint());
		resourceIsAccessible(binding.getWriterEndpoint());

		//verify persistence
		sourceIsPersistentAndCanBeReloaded(source.id());
		readerIsPersistedAndCanBeReloaded(source.id());
		writerIsPersistedAndCanBeReloaded(source.id());
		binderIsPersistedAndCanBeReloaded();
		
		
		removeSource(source);
		
		verify(source.lifecycle()).terminate();
	}
	
	@Test
	public void invalidPluginRequestsAreReported() throws Exception {

		// stage plugin for test
		SourceBinder pluginBinder = mock(SourceBinder.class);
		when(pluginBinder.bind(any(Element.class))).thenThrow(new InvalidRequestException());
		
		plugin().with(pluginBinder).install();
		
		// exercise
		try {
			binder.bind(request);
			fail();
		}
		catch(InvalidRequestFault e){}
		
	}

	@Test
	public void neitherReaderNorWriterIsReported() throws Exception {

		// stage plugin for test
		Source source = source().build();
		
		plugin().with(source).install();
		
		
		// exercise
		try {
			binder.bind(request);
			fail();
		}
		catch(AxisFault e){}
		
	}

	@Test
	public void noBoundSourceIsReported() throws Exception {

		// stage plugin for test
		SourceBinder pluginBinder = mock(SourceBinder.class);
		when(pluginBinder.bind(any(Element.class))).thenReturn(null);
		
		plugin().with(pluginBinder).install();
		
		// exercise
		try {
			binder.bind(request);
			fail();
		}
		catch(AxisFault e){
			log.info(e.toString());
		}
		
	}
	
	@Test
	public void sourceIsReconfigured() throws Exception {
		
		// stage plugin for test
		Source source = source().with(reader()).build();

		plugin().with(source).install();
				
		// exercise  twice
		binder.bind(request);
		
		binder.bind(request);
		
		verify(source.lifecycle()).reconfigure(any(Element.class));
	}
	
	@Test
	public void manySourcesAreBound() throws Exception {
		
		// stage plugin for test
		Source[] sources = {source().called("source1").with(reader()).build(),
							source().called("source2").with(writer()).build()};
		
		plugin().with(sources).install();
				
		// exercise (broadcast induces serialisation of binder)
		SourceBinding[] bindings = binder.bind(request).getBindings();
		
		assertEquals(2,bindings.length);
		
		SourceBinding first = bindings[0];
		SourceBinding second = bindings[1];
		
		//verify properties
		resourceIsAccessible(first.getReaderEndpoint());
		resourceIsAccessible(second.getWriterEndpoint());
		
		//verify persistence
		readerIsPersistedAndCanBeReloaded(first.getSourceID());
		writerIsPersistedAndCanBeReloaded(second.getSourceID());
	}
	
	void resourceIsAccessible(EndpointReferenceType address) {
		String properties = queryProperties(address);
		log.info(properties);
	}

	SourceResource sourceIsPersistentAndCanBeReloaded(String sourceId) throws Exception {

		SourceHome localHome = (SourceHome) TReaderContext.getContext().getLocalHome();
		return localHome.load(sourceId);
	}
	
	void binderIsPersistedAndCanBeReloaded() throws Exception {

		TBinderHome home = (TBinderHome) TBinderContext.getContext().getWSHome();
		home.load();
	}
	
	void readerIsPersistedAndCanBeReloaded(String sourceId) throws Exception {

		TReaderHome home = (TReaderHome) TReaderContext.getContext().getWSHome();
		home.load(key(sourceId));
		SourceHome localHome = (SourceHome) TReaderContext.getContext().getLocalHome();
		localHome.load(sourceId);
		
	}

	void writerIsPersistedAndCanBeReloaded(String sourceId) throws Exception {

		TWriterHome home = (TWriterHome) TWriterContext.getContext().getWSHome();
		home.load(key(sourceId));
	}
}
