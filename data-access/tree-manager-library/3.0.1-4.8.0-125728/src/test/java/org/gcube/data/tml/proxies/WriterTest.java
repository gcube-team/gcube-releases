package org.gcube.data.tml.proxies;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.common.clients.delegates.MockDelegate.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;
import static org.gcube.data.tml.proxies.TServiceFactory.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.streams.TreeStreams.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.stubs.TWriterStub;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.trees.data.Tree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WriterTest {
	
	//SUT
	DefaultTWriter proxy;
	
	//drives test
	@Mock TWriterStub endpoint;
	
	Tree t = t("1",e("a",3));
	
	
	@Before
	public void setup() throws Exception {
	
		proxy = new DefaultTWriter(mockDelegate(writerPlugin,endpoint));
		
	}
	
	@Test
	public void writerAddsTrees() throws Exception {
		
		NodeHolder holder = new NodeHolder(t);
		
		when(endpoint.add(any(NodeHolder.class))).thenReturn(holder);
		
		Tree response = proxy.add(t);
		
		assertEquals(t,response);
			
	}
	
	@Test
	public void writerHandlesNullResponseInLookups() throws Exception {
		
		when(endpoint.add(any(NodeHolder.class))).thenReturn(null);
		
		try {
			proxy.add(t);
			fail();
		}
		catch(ServiceException e) {}
			
	}

	@Test
	public void writerHandleInvalidTreesInLookups() throws Exception {
		
		when(endpoint.add(any(NodeHolder.class))).thenThrow(new InvalidTreeException());
		
		try {
			proxy.add(t);
			fail();
		}
		catch(InvalidTreeException e) {}
			
	}
	
	@Test
	public void writerAddsManyTrees() throws Exception {
		
		List<Tree> trees = asList(t("1"),t("2"),t("3"));
		
		URI locator = publishTreesIn(convert(trees)).withDefaults();
		
		when(endpoint.addStream(anyString())).thenReturn(locator.toString());
		
		Stream<Tree> response = proxy.add(convert(trees));
		
		assertEquals(trees, elementsOf(response));
	}
	
	@Test
	public void writerHandlesNullOutputWhenAddingManyTrees() throws Exception {
		
		List<Tree> trees = asList(t("1"),t("2"),t("3"));
		
		when(endpoint.addStream(anyString())).thenReturn(null);
		
		try {
			proxy.add(convert(trees));
			fail();
		}
		catch(ServiceException e) {
			
		}
		
	}
	
	@Test
	public void writerUpdatesTrees() throws Exception {
		
		when(endpoint.update(any(NodeHolder.class))).thenReturn(new NodeHolder(t));
		
		Tree response = proxy.update(t);
		
		assertEquals(t,response);
			
	}
	
	@Test
	public void writerHandlesNullResponseInUpdates() throws Exception {
		
		when(endpoint.update(any(NodeHolder.class))).thenReturn(null);
		
		try {
			proxy.update(t);
			fail();
		}
		catch(ServiceException e) {}
			
	}
	
	@Test
	public void writerHandleUnknownTreesInUpdates() throws Exception {
		
		when(endpoint.update(any(NodeHolder.class))).thenThrow(new UnknownTreeException());
		
		try {
			proxy.update(t);
			fail();
		}
		catch(UnknownTreeException e) {}
			
	}
	
	@Test
	public void writerHandleInvalidDeltaTreesInUpdates() throws Exception {
		
		when(endpoint.update(any(NodeHolder.class))).thenThrow(new InvalidTreeException());
		
		try {
			proxy.update(t);
			fail();
		}
		catch(InvalidTreeException e) {}
			
	}
	
	@Test
	public void writerUpdatesManyTrees() throws Exception {
		
		List<Tree> trees = asList(t("1"),t("2"),t("3"));
		
		URI locator = publishTreesIn(convert(trees)).withDefaults();
		
		when(endpoint.updateStream(anyString())).thenReturn(locator.toString());
		
		Stream<Tree> response = proxy.update(convert(trees));
		
		assertEquals(trees, elementsOf(response));
	}
	
}
