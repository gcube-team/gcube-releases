/**
 * 
 */
package org.gcube.data.speciesplugin.store;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.gcube.data.speciesplugin.utils.SpeciesService;
import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tr.neo.NeoConstants;
import org.gcube.data.tr.neo.NeoStore;
import org.gcube.data.tr.neo.nodes.BindingMode;
import org.gcube.data.tr.neo.nodes.PersistentNode;
import org.gcube.data.tr.neo.nodes.PersistentTree;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.ReadableIndex;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public class SpeciesNeoStore implements SpeciesStore, Serializable {

	private static final long serialVersionUID = -3997578013033741026L;

	protected static final QName STORE_ID = new QName("storeId"); 
	protected NeoStore neoStore;

	public SpeciesNeoStore(String storeId) throws IllegalStateException {
		neoStore = new NeoStore(new SpeciesNeoDBProvider(), storeId);
	}

	/**
	 * {@inheritDoc}
	 */
	public String id() {
		return neoStore.id();
	}

	/**
	 * {@inheritDoc}
	 */
	public File location() {
		return neoStore.location();
	}

	/**
	 * {@inheritDoc}
	 */
	public long cardinality() {
		return neoStore.cardinality();
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(File storageLocation) {
		neoStore.start(storageLocation);
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		neoStore.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete() {
		neoStore.delete();
	}

	/**
	 * {@inheritDoc}
	 */
	public Tree get(String id, Pattern pattern) throws UnknownTreeException, InvalidTreeException {

		ReadableIndex<Node> autoIndex = neoStore.dbservice().index().getNodeAutoIndexer().getAutoIndex();
		Node node = autoIndex.get(NeoConstants.toAttribute(SpeciesService.SPECIES_SERVICE_ID), id).getSingle();
		
		if (node == null) throw new InvalidTreeException("tree with id " + id + " does not exists");		
		
		PersistentNode root = new PersistentNode(neoStore.dbservice(), node, BindingMode.READ);

		try {
			pattern.prune(root);
		} catch (Exception e) {
			throw new InvalidTreeException("tree " + id + " does not match " + pattern);
		}

		PersistentTree storeTree = new PersistentTree(root);
		Tree serviceTree = fromStoreToService(storeTree);
		return serviceTree;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<Tree> get(Pattern pattern) {

		final Iterator<Tree> iterator = neoStore.get(pattern);
		
		return new Iterator<Tree>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Tree next() {
				Tree serviceTree = fromStoreToService(iterator.next());
				return serviceTree;
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public Tree add(Tree tree) throws InvalidTreeException {
		return neoStore.add(tree);
	}

	/**
	 * {@inheritDoc}
	 */
	public Stream<Tree> add(Stream<Tree> treeStream) throws Exception {
		return neoStore.add(treeStream);
	}
	
	protected Tree fromStoreToService(Tree storeTree)
	{
		String storeId = storeTree.id();
		String serviceId = storeTree.attribute(SpeciesService.SPECIES_SERVICE_ID);
		Tree serviceTree = cloneTree(storeTree, serviceId);
		serviceTree.setAttribute(STORE_ID, storeId);
		return serviceTree;
	}

	protected Tree cloneTree(Tree tree, final String newId)
	{
		Tree clone = new Tree(newId, tree.attributes(), tree.edges().toArray(new Edge[tree.edges().size()]));
		clone.setSourceId(tree.sourceId());
		return clone;
	}

}
