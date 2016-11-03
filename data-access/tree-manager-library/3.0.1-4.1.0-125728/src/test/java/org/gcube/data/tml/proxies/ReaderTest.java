package org.gcube.data.tml.proxies;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.common.clients.delegates.MockDelegate.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;
import static org.gcube.data.tml.proxies.TServiceFactory.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.gcube.data.trees.streams.TreeStreams.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.stubs.TReaderStub;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.tml.stubs.Types.LookupRequest;
import org.gcube.data.tml.stubs.Types.LookupStreamRequest;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReaderTest {
	
	//SUT
	DefaultTReader proxy;
	
	//drives test
	@Mock TReaderStub endpoint;
	
	//test objects
	String id = "id";
	
	
	@Before
	public void setup() throws Exception {
	
		proxy = new DefaultTReader(mockDelegate(readerPlugin,endpoint));
		
		
	}
	
	@Test
	public void readerLooksupTrees() throws Exception {
		
		Tree tree = t(id, e("a",3));
		NodeHolder holder = new NodeHolder();
		holder.element = XMLBindings.toElement(tree);

		when(endpoint.lookup(any(LookupRequest.class))).thenReturn(holder);
		
		Tree response = proxy.get(id);
		
		assertEquals(tree,response);
			
	}
	
	@Test
	public void readerHandlesNullLookupResponse() throws Exception {
		
		when(endpoint.lookup(any(LookupRequest.class))).thenReturn(null);
		
		try {
			proxy.get(id);
			fail();
		}
		catch(ServiceException e) {}
			
	}
	
	@Test
	public void readerHandleUnknownTrees() throws Exception {
		
		when(endpoint.lookup(any(LookupRequest.class))).thenThrow(new UnknownTreeException());
		
		try {
			proxy.get(id);
			fail();
		}
		catch(UnknownTreeException e) {}
			
	}
	
	@Test
	public void readerLooksupManyTrees() throws Exception {
		
		List<Tree> trees = asList(t("1"),t("2"),t("3"));
		
		URI locator = publishTreesIn(convert(trees)).withDefaults();
		
		when(endpoint.lookupStream(any(LookupStreamRequest.class))).thenReturn(locator.toString());
		
		Stream<String> ids = convert("1","2","3");
		
		Stream<Tree> response = proxy.get(ids,tree());
		
		assertEquals(trees, elementsOf(response));
	}
	
	@Test
	public void readerHandlesNullOutputWhenLookingupManyTrees() throws Exception {
		
		when(endpoint.lookupStream(any(LookupStreamRequest.class))).thenReturn(null);
		
		Stream<String> ids = convert("1","2","3");
		
		try {
			proxy.get(ids,tree());
		}
		catch(ServiceException e) {}
	}
}
