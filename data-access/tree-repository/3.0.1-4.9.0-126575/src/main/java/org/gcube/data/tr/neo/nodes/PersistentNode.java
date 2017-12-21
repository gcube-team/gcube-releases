/**
 * 
 */
package org.gcube.data.tr.neo.nodes;

import static java.lang.String.*;
import static org.gcube.data.tr.neo.NeoConstants.*;
import static org.gcube.data.tr.neo.nodes.BindingMode.*;
import static org.neo4j.graphdb.Direction.*;
import static org.neo4j.graphdb.DynamicRelationshipType.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link InnerNode} with persistent state.
 * <p>
 * Persistent nodes load their state on demand, i.e. when access is required.
 * This optimises partial tree traversals, such as those performed to prune the
 * results of read operations or to update trees in place.
 * <p>
 * Persistent nodes that are created in {@link BindingMode#READ READ} mode, do
 * not persist state changes. Those that are created in
 * {@link BindingMode#UPDATE UPDATE} mode do. This side-effect is visible to
 * clients to the extent that new nodes acquire identifiers. The following
 * example captures the implications:
 * 
 * <pre>
 * PersistentNode node =..
 * Edge e = ....
 * node.add(e);
 * assertFalse(node.remove(e));
 * </pre>
 * 
 * @author Fabio Simeoni
 * 
 */
public class PersistentNode extends InnerNode {

	static Logger log = LoggerFactory.getLogger(PersistentNode.class);

	// the database
	private final GraphDatabaseService db;

	// the database node that acts as the entry point to the persistent state of
	// the node
	private final org.neo4j.graphdb.Node dbnode;

	// the mode of binding between the node and its persistent state
	private final BindingMode mode;

	// state-loading flags
	private boolean attributesAreLoaded = false;
	private boolean edgesAreLoaded = false;

	/**
	 * Creates an instance in {@link BindingMode#ADD} that persists an
	 * {@link InnerNode}, assigning an identifier to it and its descendants.
	 * 
	 * <p>
	 * This works as a copy constructor with side-effects in the database. As it
	 * clones the node in memory, it also serialises its state in the database.
	 * 
	 * @param the
	 *            database
	 * @param node
	 *            the node
	 * 
	 * @throws IllegalArgumentException
	 *             if the input has already an identifier.
	 */
	public PersistentNode(GraphDatabaseService db, InnerNode node)
			throws IllegalArgumentException {

		this(db, db.createNode(), ADD);

		if (node.id() != null)
			throw new IllegalArgumentException(node.id()
					+ " has been already persisted in database");

		// marks database node with its type in order to disambiguate later
		// bindings
		dbnode.setProperty(INNER_TYPE_TAG, true);

		// copies attributes (will persist them)
		for (Entry<QName, String> attr : node.attributes().entrySet())
			if (attr.getValue() != null)
				setAttribute(attr.getKey(), attr.getValue());

		// copies edges (will persist them)
		add(node.edges());
	}

	/**
	 * Creates an instance over a database node, in a given binding mode.
	 * 
	 * @param db
	 *            the database
	 * @param dbnode
	 *            the database node
	 * @param mode
	 *            the binding mode
	 */
	public PersistentNode(GraphDatabaseService db,
			org.neo4j.graphdb.Node dbnode, BindingMode mode) {
		super(valueOf(dbnode.getId())); // id, null state, empty attributes so
										// far
		this.db = db;
		this.dbnode = dbnode;
		this.mode = mode;
	}

	/**
	 * Returns the database node that acts as the entry point to the persistent
	 * state of this node
	 * 
	 * @return the database node
	 */
	public org.neo4j.graphdb.Node dbnode() {
		return dbnode;
	}

	// ..............attributes

	/** {@inheritDoc} */
	@Override
	public synchronized Map<QName, String> attributes() {

		// first-time we pull from database in order to answer
		if (!attributesAreLoaded)
			loadAttributes();

		return super.attributes();

	}

	/** {@inheritDoc} */
	@Override
	public synchronized String setAttribute(QName name, String value) {

		// we make sure we know existing attributes to discriminate
		// between adding new attributes and changing existing attributes
		if (!attributesAreLoaded)
			loadAttributes();

		// change in memory
		String oldValue = super.setAttribute(name, value);

		// change in the database if we are adding a new node or updating an
		// existing node
		if (value != null && mode != READ)
			dbnode.setProperty(toAttribute(name), value);

		return oldValue;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String removeAttribute(QName name) {

		// we make sure we know existing attributes in order to discriminate
		// between adding new attributes and changing existing attributes
		if (!attributesAreLoaded)
			loadAttributes();

		// change in memory
		String value = super.removeAttribute(name);

		// change in the database if we are updating an existing node
		if (value != null && mode == UPDATE)
			dbnode.removeProperty(toAttribute(name));

		return value;
	}

	// shared behaviour
	void loadAttributes() {

		// fragile code: set flag eagerly or this method and setAttribute() will
		// loop
		attributesAreLoaded = true;

		// if we adding, there is nothing already in the db to load
		if (mode == ADD)
			return;

		for (String key : dbnode.getPropertyKeys())
			if (isAttribute(key))
				setAttribute(QName.valueOf(key.substring(1)),
						valueOf(dbnode.getProperty(key)));

	}

	// ..............edges

	/** {@inheritDoc} */
	@Override
	public synchronized List<Edge> edges() {

		// we pull edges from store on demand
		if (!edgesAreLoaded)
			loadEdges();

		return super.edges();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized boolean add(Edge e) {

		// we make sure we know existing edges in order to tell we're adding new
		// ones or not
		if (!edgesAreLoaded)
			loadEdges();

		//if we are not writing to the db, things proceed as usual
		if (mode == READ)
			return super.add(e);

		boolean exists = edges().contains(e);

		// we are in ADD/UPDATE mode here, need to add a persistent version of
		// the edge in both memory and database
		if (!exists) {

			Node target = e.target();

			org.neo4j.graphdb.Node child;
			Edge persistentEdge;

			// we propagate request by constructing inner and leaves
			// with same database and in same write mode
			if (target instanceof InnerNode) {
				PersistentNode pnode = new PersistentNode(db,
						(InnerNode) target);
				child = pnode.dbnode();
				persistentEdge = new Edge(e.label(), pnode);
			} else {
				PersistentLeaf pleaf = new PersistentLeaf(db, (Leaf) target);
				child = pleaf.dbnode();
				persistentEdge = new Edge(e.label(), pleaf);
				;
			}

			// add in memory
			super.add(persistentEdge);

			// create persistent relationship
			dbnode.createRelationshipTo(child, withName(e.label().toString()));

		}

		return exists;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized boolean remove(Edge e) {

		// we make sure we know existing edges first
		if (!edgesAreLoaded)
			loadEdges();

		// NB: this will trigger recursive deletion
		boolean deletedEdge = super.remove(e);

		// CAREFUL: we cannot refer to input here as even toString(), equals()
		// and hashCode()
		// will try to access a deleted dbnode

		return deletedEdge;
	}

	/** {@inheritDoc} */
	@Override
	public void delete() {

		// we make sure we know existing edges first
		if (!edgesAreLoaded)
			loadEdges();

		// delete in memory and trigger recursion on children, whatever the mode
		super.delete();

		// if we are updating, we are left with removing relationships and
		// delete
		// the database node itself
		if (mode == UPDATE) {
			for (Relationship relationship : dbnode.getRelationships())
				relationship.delete();
			dbnode.delete(); 	// finally, we can delete the database node and its
								// properties
		}
	}

	// shared behaviour
	void loadEdges() {

		// fragile code: set flag eagerly or this method and add() will loop
		edgesAreLoaded = true;

		// if we adding, there is nothing already in the db to load
		if (mode == ADD)
			return;

		for (Relationship relationship : dbnode.getRelationships(OUTGOING)) {

			org.neo4j.graphdb.Node dbtarget = relationship.getEndNode();

			Node target = null;

			// disambiguates nodes using type tags

			if (dbtarget.hasProperty(INNER_TYPE_TAG))
				target = new PersistentNode(db, dbtarget, mode);
			else if (dbtarget.hasProperty(LEAF_TYPE_TAG))
				target = new PersistentLeaf(dbtarget, mode);
			else
				throw new RuntimeException("malformed tree has no type tag");

			// reconstruct an edge
			Edge edge = new Edge(QName.valueOf(relationship.getType().name()),
					target);

			super.add(edge);

		}

	}

	// NB: the optimisations below in READ mode below cater for more efficient
	// comparisons, which may be significant in batch operations
	// over large trees (avoid fetching from store). However, this optimisation
	// breaks set-based equivalence of edge sets when the two nodes being
	// compared are a mix
	// of normal vs. persistent (Innernode.equals(PersistentNode), or viceversa.
	// the problem is in the hash code, which differs across the two
	// implementations (set comparison in superclass is based
	// on hashset implementations, hence hashcode matters). this problem may
	// occur in cloning (e.g. tree(tree), where tree is an
	// instance of this class. to achieve cloning, the document must be re-read
	// from the store.

	// if these prove more problematic than expected they will be removed
	// (similar optimisations apply to leaves, where they are
	// much more significant and will not create problems as long as normal and
	// persistent versions are not explicitly mixed in
	// hashmaps.

	/** {@inheritDoc} */
	@Override
	public synchronized boolean equals(Object obj) {
		if (mode == READ)
			return id().equals(((Node) obj).id()); // optimisation on reading
		else
			return super.equals(obj);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		if (mode == READ)
			return id().hashCode(); // optimisation on reading
		else
			return super.hashCode();
	}

}
