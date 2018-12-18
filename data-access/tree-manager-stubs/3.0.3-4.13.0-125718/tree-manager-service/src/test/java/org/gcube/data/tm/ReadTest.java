/**
 * 
 */
package org.gcube.data.tm;


import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;
import static org.gcube.data.tm.TestUtils.*;
import static org.gcube.data.tm.testplugin.PluginBuilder.*;
import static org.gcube.data.tm.utils.Utils.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.gcube.data.trees.streams.TreeStreams.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import javax.inject.Named;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tm.stubs.GetByIDParams;
import org.gcube.data.tm.stubs.GetByIDsParams;
import org.gcube.data.tm.stubs.GetParams;
import org.gcube.data.tm.stubs.InvalidTreeFault;
import org.gcube.data.tm.stubs.Path;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tm.stubs.TReaderPortType;
import org.gcube.data.tm.stubs.UnknownPathFault;
import org.gcube.data.tm.stubs.UnknownTreeFault;
import org.gcube.data.tm.stubs.service.TReaderServiceAddressingLocator;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownPathException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.gcube.data.trees.patterns.Pattern;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyContainerTestRunner.class)
public class ReadTest {

	@Deployment
	static Gar gar = gar();
	
	@Named(Constants.TBINDER_NAME)
	static TBinderService binder;
	
	Source source;

	@BeforeClass
	public static void setup() throws Exception {

		serviceIsReady();

		setCurrentScope(devsec);
	}


	@Test
	public void treeCanBeLookedup() throws Exception {

		Tree tree = mockTree();

		SourceReader reader = reader();
		
		when(reader.get(any(String.class), any(Pattern.class))).thenReturn(tree);
		
		source = source().with(reader).build();

		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();

		Tree result = toTree(stub.getByID(new GetByIDParams(toHolder(tree()),"anythingwilldo")));

		//the service returns the tree provided by the plugin, which this may be
		assertEquals(tree.id(), result.id());
		

	}

	@Test
	public void treeLookupFailuresAreReported() throws Exception {

		SourceReader reader = reader();

		when(reader.get(any(String.class), any(Pattern.class))).thenThrow(new UnknownTreeException(),
				new InvalidTreeException());

		source = source().with(reader).build();

		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();

		// exercise
		try {
			stub.getByID(new GetByIDParams(toHolder(tree()),"foo"));
			fail();
		} 
		catch (UnknownTreeFault e) {
		}

		// exercise
		try {
			stub.getByID(new GetByIDParams(toHolder(tree()),"foo"));
			fail();
		} 
		catch (InvalidTreeFault e) {
		}

	}

	@Test
	public void lookupNode() throws Exception {

		Node node = l("2","val");
		
		SourceReader reader = reader();
		when(reader.getNode("1","2")).thenReturn(node);
		
		source = source().with(reader).build();

		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		// pass a path to leaf
		Node result = XMLBindings.nodeFromElement(toElement(stub.getNode(new Path(new String[]{"1","2"}))));

		assertEquals(node,result);

	}

	@Test
	public void lookupNodeWithBadPath() throws Exception {

		SourceReader reader = reader();
		when(reader.getNode("bad","path")).thenThrow(new UnknownPathException());

		source = source().with(reader).build();
		
		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		try {
			stub.getNode(new Path(new String[]{"bad", "path"}));
			fail();
		} 
		catch (UnknownPathFault e) {
		}

	}

	@Test
	public void manyTreesCanBeLookedup() throws Exception {
		
		List<Tree> trees = asList(t("1",e("a",1)),t("2",e("b",2)));
		//prepare reader
		SourceReader reader = reader();
		
		@SuppressWarnings("unchecked")
		Stream<String> anyStream = (Stream<String>) any(Stream.class);
		when(reader.get(anyStream,any(Pattern.class))).thenReturn(convert(trees));
		
		source = source().with(reader).build();
		
		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		// publish identifiers
		URI idLocator = publishStringsIn(convert("does ot matter")).withDefaults();

		// exercise SUT
		String locator = stub.getByIDs(new GetByIDsParams(idLocator.toString(),toHolder(tree())));
		
		Stream<Tree> retrieved = treesIn(URI.create(locator));
		
		assertEquals(trees,elementsOf(retrieved));

	}

	@Test
	public void manyTreesLookupFailureAreReported() throws Exception {

		//prepare reader
		SourceReader reader = reader();
		
		@SuppressWarnings("unchecked")
		Stream<String> anyStream = (Stream<String>) any(Stream.class); 
		
		RuntimeException ute = new RuntimeException(new UnknownTreeException());
		List<Object> trees = asList(t("1",e("a",1)),ute,t("3",e("c",3)));
		
		when(reader.get(anyStream,any(Pattern.class))).thenReturn(convertWithFaults(Tree.class, trees));
				
		// extract known identifiers
		List<String> identifiers = asList("1","2","3");

		// publish identifiers
		URI idLocator = publishStringsIn(convert(identifiers)).withDefaults();

		source = source().with(reader).build();
		
		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		// exercise SUT
		String locator = stub.getByIDs(new GetByIDsParams(idLocator.toString(), toHolder(tree())));

		Stream<Tree> retrieved = treesIn(URI.create(locator));
		
		Object secondElement = elementsOf(retrieved).get(1);

		assertTrue(secondElement instanceof RuntimeException);

		assertTrue(((RuntimeException) secondElement).getCause() instanceof UnknownTreeException);

	}

	@Test
	public void treesCanBeQueried() throws Exception {

		//prepare reader
		SourceReader reader = reader();
		
		List<Tree> trees = asList(t("1",e("a",1)),t("2",e("b",2)),t("3",e("c",3)));
				
		when(reader.get(any(Pattern.class))).thenReturn(convert(trees));
				
		// actual pattern is irrelevant to test, plugins apply it we do not need to test for it
		Pattern pattern = tree();

		source = source().with(reader).build();
		
		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		// exercise SUT
		String locator = stub.get(new GetParams(toHolder(pattern)));

		Stream<Tree> retrieved = treesIn(URI.create(locator));
		
		assertEquals(trees, elementsOf(retrieved));
	}

	@Test
	public void queriesMayHaveNoResults() throws Exception {

		//prepare reader
		SourceReader reader = reader();
		
		List<Tree> trees = emptyList();
						
		when(reader.get(any(Pattern.class))).thenReturn(convert(trees));
				
		// actual pattern is irrelevant to test, plugins apply it we do not need to test for it
		Pattern pattern = tree();

		source = source().with(reader).build();
		
		plugin().with(source).install();
		
		TReaderPortType stub = bindSourceAndGetReaderStub();
		
		// exercise SUT
		String locator = stub.get(new GetParams(toHolder(pattern)));

		Stream<Tree> retrieved = treesIn(URI.create(locator));
		
		assertEquals(trees, elementsOf(convert(retrieved)));

	}
	
	@After
	public void cleanup() throws Exception {
		removeSource(source);
	}
	
	
	//helper
	public TReaderPortType bindSourceAndGetReaderStub() throws Exception {

		// stages a source binding
		SourceBinding binding = binder.bind(request).getBindings()[0];

		// sets up a proxy shared by tests
		return new TReaderServiceAddressingLocator().getTReaderPortTypePort(binding.getReaderEndpoint());
		
	}


}
