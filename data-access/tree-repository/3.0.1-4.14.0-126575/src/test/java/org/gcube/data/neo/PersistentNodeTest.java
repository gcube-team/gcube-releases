/**
 * 
 */
package org.gcube.data.neo;

import static java.util.Calendar.*;
import static org.gcube.data.TestFixture.*;
import static org.gcube.data.TestUtils.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.io.XMLBindings.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;

import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tr.neo.NeoStore;
import org.gcube.data.tr.neo.nodes.BindingMode;
import org.gcube.data.tr.neo.nodes.PersistentNode;
import org.gcube.data.tr.neo.nodes.PersistentTree;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Simeoni
 * 
 */
public class PersistentNodeTest {

	private static Logger log = LoggerFactory.getLogger("test");

	static NeoStore store;
	static Tree readOnlyTree;
	static Tree persistentTree;

	@BeforeClass
	public static void setup() throws Exception {

		store = newTestStoreWith(TEST_TREE);

		// this is a read-only node, no connection to database after reading
		readOnlyTree = store.get("1", tree());

		GraphDatabaseService db = store.dbservice();
		persistentTree = new PersistentTree(new PersistentNode(db,
				db.getNodeById(1), BindingMode.UPDATE));
	}

	@AfterClass
	public static void teardown() {
		if (store!=null)
			store.delete();
	}
	
	@Test
	public void readOnlyNodesManageAttributesCorrectly() {

		testAttributesFor(readOnlyTree);
	}

	@Test
	public void readOnlyNodesManageEdgesCorrectly() {

		testEdgesFor(readOnlyTree);
	}

	@Test
	public void readOnlyNodesManageChildrenCorrectly() {

		// get metadata children
		InnerNode metadata = readOnlyTree.children(N, METADATA).get(0);

		testAttributesFor(metadata);
		testEdgesFor(metadata);

		// check other access methods
		metadata.child(CREATION_TIME);
		metadata.attributes();

	}

	@Test
	public void readOnlyNodesMatchCorrectly() throws UnknownTreeException {

		assertTrue(tree().matches(readOnlyTree));

		assertTrue(tree(atleast(METADATA, tree())).matches(readOnlyTree));

		assertTrue(tree(one(TYPE, text())).matches(readOnlyTree));

		assertTrue(tree(one(TYPE, text(is(SOMETYPE)))).matches(readOnlyTree));

		assertTrue(tree(
				many(ANNOTATION,
						tree(one(CREATION_TIME, calendar(before(getInstance()))))))
				.matches(readOnlyTree));

		assertFalse(tree(one(TYPE, text(is("wrong")))).matches(readOnlyTree));

		assertFalse(tree(atleast(PART, any())).matches(readOnlyTree));
	}

	@Test
	public void readOnlyNodesPruneCorrectly() throws Exception {

		// clone by re-reading: we cannot use standard cloning mechanisms (see
		// documentation in PersistentNode)
		Tree clone = store.get("1", tree());

		// no real pruning test
		tree().prune(clone);

		assertEquals(readOnlyTree, clone);

		tree(atleast(METADATA, tree())).prune(clone);

		// metadata are the only children left
		assertEquals(new HashSet<Node>(readOnlyTree.children(METADATA)), new HashSet<Node>(clone.children()));

		clone = store.get("1", tree());

		tree(one(TYPE, text())).prune(clone);

		assertEquals(1, clone.children().size());

		assertEquals(readOnlyTree.child(TYPE), clone.children().get(0));

		clone = store.get("1", tree());

		tree(
				many(ANNOTATION,
						tree(many(CREATION_TIME,
								calendar(before(getInstance()))))))
				.prune(clone);

		assertEquals(clone.children(ANNOTATION), clone.children());

		for (InnerNode a : clone.children(N)) {
			assertEquals(1, a.children().size());
			assertEquals(a.child(CREATION_TIME), a.children().get(0));
		}

		try {
			tree(atleast(PART, any())).prune(store.get("1", tree()));
			fail();
		} catch (Exception e) {}
		
		

	}

	@Test
	public void readOnlyNodesSerialiseCorrectly() throws Exception {

		StringWriter writer = new StringWriter();
		toStream(readOnlyTree, writer);
		log.info(writer.toString());
	}

	@Test
	public void persistentNodesManageAttributesCorrectly()
			throws UnknownTreeException, InvalidTreeException {

		Transaction transaction = store.dbservice().beginTx();

		try {

			// root
			testAttributesFor(persistentTree);

			// child
			InnerNode child = persistentTree.children(N, METADATA).get(0);
			testAttributesFor(child);

		} finally { // convenient not to commit changes
			transaction.finish();
		}
	}

	@Test
	public void persistentNodesManageEdgesCorrectly()
			throws UnknownTreeException {

		Transaction transaction = store.dbservice().beginTx();

		try {

			int size = persistentTree.edges().size();

			// add edge: this has side-effects as nodes receive their ids as
			// they are persisted
			Edge leafEdge = e("leafedge", "leafval");
			persistentTree.add(leafEdge);

			assertTrue(persistentTree.hasEdge(leafEdge.label()));
			assertEquals(size + 1, persistentTree.edges().size());

			// the original edge will not be removed (the child has still no id
			// there)
			assertFalse(persistentTree.remove(leafEdge));

			// we can only remove what we added: this is not a problem though as
			// we either add or remove an edge during a service operation
			// add followed by remove should never occur
			Edge added = persistentTree.edge(leafEdge.label());
			assertTrue(persistentTree.remove(added));


		} finally {// convenient not to commit changes
			transaction.finish();
		}
	}

	@Test
	public void inplaceUpdate() throws UnknownTreeException, Exception {

		// a bit of gimmick: clone to obtain an in-memory tree as available to client;
		StringWriter w = new StringWriter();
		toStream(readOnlyTree, w);
		Tree classicTree = fromStream(new StringReader(w.toString()));

		Tree future = t(classicTree);

		int children = future.children().size();
		int annotations = future.children(N, ANNOTATION).size();
		int metadatachildren = future.children(N, METADATA).get(0).children()
				.size();
		
		// change
		future.add(e("newedge", "newval"));
		future.setAttribute(ATTR1, "newvalue");
		future.remove(future.edge(TYPE));
		future.remove(future.edges(ANNOTATION).get(0));

		InnerNode metadata = future.children(N, METADATA).get(0);
		metadata.setAttribute(ATTR1, "newvalue");
		metadata.add(e("newedge", "newval"));
		metadata.remove(metadata.edge(BYTESTREAM));

		Tree delta = classicTree.delta(future);
		
		log.info(delta.toString());


		Transaction transaction = store.dbservice().beginTx();

		try {
			
			persistentTree.update(delta);
			assertTrue(persistentTree.hasEdge("newedge"));
			assertEquals("newval", persistentTree.child(L, "newedge").value());

			// we have removed two and added one
			assertEquals(children - 1, persistentTree.children().size());
			// we have removed one
			assertEquals(annotations - 1, persistentTree.children(N, ANNOTATION).size());
			// we have removed one and added one
			assertEquals(metadatachildren, persistentTree.children(N, METADATA).get(0)
					.children().size());

			// System.out.println("after update\n"+doc);
			// transaction.success();
		} finally {
			transaction.finish();
		}

	}

	@Test
	public void delete() throws UnknownTreeException, Exception {

		// a bit of gimmick: clone to obtain an in-memory tree as available to client;
		StringWriter w = new StringWriter();
		toStream(readOnlyTree, w);
		Tree classicTree = fromStream(new StringReader(w.toString()));

		classicTree.delete();

		Transaction transaction = store.dbservice().beginTx();

		try {
			store.update(classicTree);
			assertEquals(0, store.cardinality());
			
		} finally {
			transaction.finish();
		}

	}

	// helper
	void testAttributesFor(Node node) {

		// initially does not have the attribute
		assertFalse(node.hasAttribute("attr"));

		// remember attribute number
		int attrSize = node.attributes().size();

		// add an attribute
		node.setAttribute("attr", "val");

		// confirm it has it
		assertTrue(node.hasAttribute("attr"));

		// check new numbers
		assertEquals(attrSize + 1, node.attributes().size());

		// remove attribute
		assertEquals("val", node.removeAttribute("attr"));

		// checl numbers
		assertEquals(attrSize, node.attributes().size());

		try {
			node.removeAttribute("attr");
			fail();
		} catch (IllegalStateException e) {
		}
	}

	void testEdgesFor(InnerNode node) {

		// initially does not have the edge
		assertFalse(node.hasEdge("edge"));

		// remember child number
		int childSize = node.children().size();

		// add edge
		Edge edge = e("edge", l("val"));
		node.add(edge);

		// confirm it has it
		assertTrue(node.hasEdge("edge"));

		// check numbers
		assertEquals(childSize + 1, node.children().size());

		// check child
		assertEquals(l("val"), node.child("edge"));

		// remove edge
		assertTrue(node.remove(edge));

		// check numbers
		assertEquals(childSize, node.children().size());
	}

}
