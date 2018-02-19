package org.gcube.data.tr.neo;

import static org.gcube.data.streams.dsl.Streams.*;
import static org.neo4j.graphdb.Direction.*;
import static org.neo4j.graphdb.DynamicRelationshipType.*;
import static org.neo4j.graphdb.ReturnableEvaluator.*;
import static org.neo4j.graphdb.StopEvaluator.*;
import static org.neo4j.graphdb.Traverser.Order.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tr.Store;
import org.gcube.data.tr.neo.nodes.BindingMode;
import org.gcube.data.tr.neo.nodes.DefaulNeoDBProvider;
import org.gcube.data.tr.neo.nodes.PersistentNode;
import org.gcube.data.tr.neo.nodes.PersistentTree;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoStore implements Store {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(NeoStore.class);

	private String storeId;
	private File location;

	private final NeoDBProvider dbProvider;
	
	transient private GraphDatabaseService db;
	transient private org.neo4j.graphdb.Node referenceNode;

	public NeoStore(String storeId) throws IllegalStateException {

		this(new DefaulNeoDBProvider(),storeId);
	}
	
	public NeoStore(NeoDBProvider provider, String storeId) throws IllegalStateException {

		this.dbProvider= provider;
		this.storeId = storeId;
	}

	@Override
	public String id() {
		return storeId;
	}

	@Override
	public File location() {
		return location;
	}

	@Override
	public long cardinality() {
		return (Long) referenceNode.getProperty(NeoConstants.COUNT_PROPERTY, 0L);
	}

	// testing back door
	public GraphDatabaseService dbservice() {
		return db;
	}

	/**
	 * LIFETIME OPERATIONS
	 */

	@Override
	public synchronized void start(File storageLocation) {

		if (db != null)
			return;

		File location = new File(storageLocation, storeId);
		log.info("creating a neo store {} in {}", storeId, location);

		this.location = location;

		_start(location);
	}

	// used post-serialisation too
	public void _start(File location) {

		db = dbProvider.newDatabase(location);
		referenceNode = db.getReferenceNode();

	}

	/** {@inheritDoc} */
	@Override
	public synchronized void stop() {

		if (db == null)
			return;

		log.info("stopping neo store at {} ", location);

		db.shutdown();

		db = null;

		// // give time to shutdown
		// try {
		// TimeUnit.MILLISECONDS.sleep(500);
		// } catch (InterruptedException e) {
		// log.warn("could not wait for shutdown to complete", e);
		// }

	}

	/** {@inheritDoc} */
	@Override
	public void delete() {

		stop();

		log.info("deleting neo store for collection {} at {}", storeId, location);

		deleteStore(location());
	}

	// helper
	void deleteStore(final File file) {
		if (file.exists()) {
			if (file.isDirectory())
				for (File child : file.listFiles())
					deleteStore(child);
			file.delete();
		}
	}

	/**
	 * READ OPERATIONS
	 */

	@Override
	public Tree get(String id, Pattern pattern) throws UnknownTreeException, InvalidTreeException {

		PersistentNode root = new PersistentNode(db, db.getNodeById(Long.valueOf(id)), BindingMode.READ);

		try {
			pattern.prune(root);
		} catch (Exception e) {
			throw new InvalidTreeException("tree " + id + " does not match " + pattern);
		}

		Tree retrieved = new PersistentTree(root);
		
		retrieved.setSourceId(storeId);
		
		return retrieved;
	}

	@Override
	public Iterator<Tree> get(final Pattern pattern) {

		Iterator<org.neo4j.graphdb.Node> dbnodes = referenceNode.traverse(BREADTH_FIRST, DEPTH_ONE, ALL_BUT_START_NODE,
				withName(NeoConstants.TREE_RELATION_NAME), OUTGOING).iterator();

		Generator<org.neo4j.graphdb.Node, Tree> prune = new Generator<org.neo4j.graphdb.Node, Tree>() {

			public Tree yield(org.neo4j.graphdb.Node dbnode) {
				PersistentNode root = new PersistentNode(db, dbnode, BindingMode.READ);
				try {
					pattern.prune(root);
				}
				catch(Exception e) {
					throw new StreamSkipSignal();
				}
				
				Tree retrieved = new PersistentTree(root);
				retrieved.setSourceId(storeId);
				return retrieved;
			}
		};

		return pipe(convert(dbnodes)).through(prune);
	}

	/**
	 * WRITE OPERATIONS
	 */

	@Override
	public Tree add(Tree tree) throws InvalidTreeException {

		// bound source check
		if (tree.sourceId() != null && !tree.sourceId().equals(storeId))
			throw new InvalidTreeException("cannot add " + tree.id() + " to " + storeId
					+ " as it is already bound to source" + tree.sourceId());

		Transaction tx = db.beginTx();
		try {

			PersistentNode node = new PersistentNode(db, tree);

			// appends doc to reference node
			referenceNode.createRelationshipTo(node.dbnode(), withName(NeoConstants.TREE_RELATION_NAME));

			changeCardinality(+1);

			tx.success();

			Tree added = new PersistentTree(node); 
			
			added.setSourceId(storeId);
			
			return added;

		} catch (RuntimeException e) {
			throw new InvalidTreeException("cannot add " + tree.id() + " to " + storeId, e);
		} finally {
			tx.finish();
		}
	}

	// internal helper
	private void changeCardinality(int change) {
		referenceNode.setProperty(NeoConstants.COUNT_PROPERTY, cardinality() + change);
	}

	@Override
	public Tree update(Tree delta) throws UnknownTreeException, InvalidTreeException {

		// bound source check
		if (delta.sourceId() != null && !delta.sourceId().equals(storeId))
			throw new InvalidTreeException("cannot update " + delta.id() + " to " + storeId
					+ " as it is already bound to source" + delta.sourceId());

		String id = delta.id();

		if (delta.id() == null)
			throw new InvalidTreeException(delta + " is a malformed delta tree as it does not have an identifier");

		Transaction tx = db.beginTx();

		try {

			PersistentNode root = new PersistentNode(db, db.getNodeById(Long.valueOf(id)), BindingMode.UPDATE);

			Tree tree = new PersistentTree(root);

			tree.update(delta); // fails with a runtime exception

			if (delta.state() == Node.State.DELETED)
				changeCardinality(-1);

			tx.success();
			
			tree.setSourceId(storeId);
			
			return tree;

		} catch (RuntimeException e) {
			throw new InvalidTreeException("cannot update " + delta.id() + " in " + storeId + " with " + delta, e);
		} finally {
			tx.finish();
		}

	}

	@Override
	public Stream<Tree> add(Stream<Tree> treeStream) throws Exception {

		Generator<Tree,Tree> addOne = new Generator<Tree, Tree>() {

			public Tree yield(Tree tree) {

				try {
					return add(tree);
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		TransactingGenerator<Tree, Tree> addTransaction = new TransactingGenerator<Tree, Tree>(db,
				addOne, "add-tree-stream");

		Stream<Tree> outcomes = pipe(treeStream).through(addTransaction);

		return monitor(outcomes).with(addTransaction);
	}

	@Override
	public Stream<Tree> update(Stream<Tree> deltaStream) throws Exception {

		Generator<Tree,Tree> updateOne = new Generator<Tree,Tree>() {

			public Tree yield(Tree delta) {

				try {
					return update(delta);
				} catch (Exception e) {
					throw new RuntimeException(delta.id(), e);
				}
			}
		};

		TransactingGenerator<Tree,Tree> updateTransaction = new TransactingGenerator<Tree, Tree>(
				db, updateOne, "updateDocuments");

		return pipe(deltaStream).through(updateTransaction);
	}

	/**
	 * @serialData the identifier and the db location.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	// invoked upon deserialisation, resets non-serializable defaults
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();

		// check invariants
		if (location == null || storeId == null)
			throw new IOException("invalid serialisation, missing identifier or location");

		_start(location);
	}

	@Override
	public String toString() {
		return "NeoStore [storeId=" + storeId + ", location=" + location + "]";
	}

}
