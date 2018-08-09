/**
 * 
 */
package org.gcube.data.tm;

import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;
import static org.gcube.data.tm.TestUtils.*;
import static org.gcube.data.tm.testplugin.PluginBuilder.*;
import static org.gcube.data.tm.utils.Utils.*;
import static org.gcube.data.trees.streams.TreeStreams.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.inject.Named;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.data.streams.Stream;
import org.gcube.data.tm.services.TBinderService;
import org.gcube.data.tm.stubs.InvalidTreeFault;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tm.stubs.TWriterPortType;
import org.gcube.data.tm.stubs.UnknownTreeFault;
import org.gcube.data.tm.stubs.service.TWriterServiceAddressingLocator;
import org.gcube.data.tm.utils.Utils;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceWriter;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Tree;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fabio Simeoni
 * 
 */
@RunWith(MyContainerTestRunner.class)
public class WriteTest {

	@Deployment
	static Gar gar = gar();

	@Named(Constants.TBINDER_NAME)
	static TBinderService binder;

	Source source;

	@BeforeClass
	public static void before() throws Exception {

		serviceIsReady();

		setCurrentScope(devsec);

	}

	@Test
	public void failuresUponAdditionAreReported() throws Exception {

		Tree tree = mockTree();

		SourceWriter writer = writer();

		when(writer.add(any(Tree.class))).thenReturn(tree);

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		// the service returns the tree provided by the plugin, which this may be
		Tree added = toTree(stub.add(toAnyHolder(tree)));

		assertEquals(tree.id(), added.id());

	}

	@Test
	public void treesCanBeAdded() throws Exception {

		Tree tree = mockTree();

		SourceWriter writer = writer();

		when(writer.add(any(Tree.class))).thenReturn(tree);

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		// the service returns the tree provided by the plugin, which this may be
		Tree added = toTree(stub.add(toAnyHolder(tree)));

		assertEquals(tree.id(), added.id());

	}


	@Test
	public void treeAdditionFailuresAreReported() throws Exception {

		Tree tree = mockTree();

		SourceWriter writer = writer();

		when(writer.add(any(Tree.class))).thenThrow(new InvalidTreeException());

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		try {
			// the service returns the failure raised by the plugin
			stub.add(toAnyHolder(tree));
			fail();

		} catch (InvalidTreeFault e) {
		}
	}

	@Test
	public void manyTreesCanBeAdded() throws Exception {

		Stream<Tree> trees = convert(mockTree("1"), mockTree("2"));

		URI locator = publishTreesIn(trees).nonstop().withDefaults();

		SourceWriter writer = writer();

		@SuppressWarnings("unchecked")
		Stream<Tree> treeStream = (Stream<Tree>) any(Stream.class);
		when(writer.add(treeStream)).thenReturn(trees);

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		String returnedLocator = stub.addRS(locator.toString());

		Stream<Tree> returned = treesIn(URI.create(returnedLocator));

		// the service returns what the plugin provides
		assertEquals(elementsOf(trees), elementsOf(returned));
	}

	@Test
	public void treesCanBeUpdated() throws Exception {

		Tree tree = mockTree();

		SourceWriter writer = writer();

		when(writer.update(any(Tree.class))).thenReturn(tree);

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		// the service returns the tree provided by the plugin, which this may be
		Tree added = toTree(stub.update(toAnyHolder(tree)));

		assertEquals(tree.id(), added.id());

	}

	@Test
	public void treeUpdateFailuresAreReported() throws Exception {

		Tree tree = mockTree();

		SourceWriter writer = writer();

		when(writer.update(any(Tree.class))).thenThrow(new UnknownTreeException(), new InvalidTreeException());

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		try {
			// the service returns the failure raised by the plugin
			stub.update(toAnyHolder(tree));
			fail();

		} catch (UnknownTreeFault e) {
		}

		try {
			// the service returns the failure raised by the plugin
			stub.update(Utils.toAnyHolder(tree));
			fail();

		} catch (InvalidTreeFault e) {
		}
	}

	@Test
	public void manyTreesCanBeUpdated() throws Exception {

		Stream<Tree> trees = convert(mockTree("1"), mockTree("2"));

		URI locator = publishTreesIn(trees).nonstop().withDefaults();

		SourceWriter writer = writer();

		@SuppressWarnings("unchecked")
		Stream<Tree> treeStream = (Stream<Tree>) any(Stream.class);
		when(writer.update(treeStream)).thenReturn(trees);

		source = source().with(writer).build();

		plugin().with(source).install();

		TWriterPortType stub = bindSourceAndGetWriterStub();

		String retrievedLocator = stub.updateRS(locator.toString());

		Stream<Tree> retrieved = treesIn(URI.create(retrievedLocator));

		// the service returns what the plugin provides
		assertEquals(elementsOf(trees), elementsOf(retrieved));
	}

	@After
	public void cleanup() throws Exception {
		removeSource(source);
	}

	// helper
	public TWriterPortType bindSourceAndGetWriterStub() throws Exception {

		// stages a source binding
		SourceBinding binding = binder.bind(request).getBindings()[0];

		// sets up a proxy shared by tests
		return new TWriterServiceAddressingLocator().getTWriterPortTypePort(binding.getWriterEndpoint());

	}
}
