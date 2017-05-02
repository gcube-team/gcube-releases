/**
 * 
 */
package org.gcube.data.tr.neo.nodes;

import static java.lang.String.*;
import static org.gcube.data.tr.neo.NeoConstants.*;
import static org.gcube.data.tr.neo.nodes.BindingMode.*;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link Leaf} with persistent state.
 * 
 * @author Fabio Simeoni
 * @see PersistentNode
 */
public class PersistentLeaf extends Leaf {

	static Logger log = LoggerFactory.getLogger(PersistentLeaf.class);
	
	private final org.neo4j.graphdb.Node dbnode;
	
	private final BindingMode mode;
	private boolean attributesAreLoaded =false; 

	/** 
	 * Creates an instance from a given {@link Leaf} and using a given {@link NodeBinder}. 
	 * @param dbnode the persistent node
	 */
	public PersistentLeaf(GraphDatabaseService db,Leaf node) throws IllegalArgumentException {
		
		this(db.createNode(),ADD);
		
		if (node.id()!=null)
			throw new IllegalArgumentException(node.id()+" has been already persisted in database");
		
		//marks database node with its type in order to disambiguate later bindings
		dbnode.setProperty(LEAF_TYPE_TAG,true);
		
		for (Entry<QName,String> attr : node.attributes().entrySet())
			setAttribute(attr.getKey(),attr.getValue());
		
		value(node.value());
	}
	
	public PersistentLeaf(org.neo4j.graphdb.Node dbnode, BindingMode mode) {
		super(valueOf(dbnode.getId()));
		this.dbnode=dbnode;
		this.mode=mode;
	}
	
	/**
	 * Returns the database node that acts as the entry point to the persistent state of this node
	 * @return the database node
	 */
	public org.neo4j.graphdb.Node dbnode() {
		return dbnode;
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized Map<QName, String> attributes() {
		
		//first-time we pull from database in order to answer
		if (!attributesAreLoaded)
			loadAttributes();
		
		return super.attributes();

	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String setAttribute(QName name, String value) {
		
		//we make sure we know existing attributes to discriminate 
		//between adding new attributes and changing existing attributes  
		if (!attributesAreLoaded)
			loadAttributes();
		
		//change in memory
		String oldValue = super.setAttribute(name,value);
		
		//change in the database
		if (value!=null && mode!=READ)
			dbnode.setProperty(toAttribute(name),value);
		
		return oldValue;
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String removeAttribute(QName name) {
		
		//we make sure we know existing attributes in order to discriminate 
		//between additions of new attributes and changes to existing attributes 
		if (!attributesAreLoaded)
			loadAttributes();
		
		//change in memory
		String value = super.removeAttribute(name);
		
		//change in the database
		if (value!=null && mode==UPDATE)
			dbnode.removeProperty(toAttribute(name));
		
		return value;
	}
	
	//shared behaviour
	void loadAttributes() {

		//fragile code: set flag eagerly or this method and setAttribute() will loop
		attributesAreLoaded = true;

		if (mode==ADD)
			return;
		
		for (String key : dbnode.getPropertyKeys())
			if (isAttribute(key))
				setAttribute(QName.valueOf(key.substring(1)), valueOf(dbnode.getProperty(key)));
		
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String value() {
		return dbnode.hasProperty(VALUE_PROPERTY)? (String) dbnode.getProperty(VALUE_PROPERTY):null;
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized void value(String v) throws IllegalArgumentException {
		super.value(v);
		if (mode!=READ && v!=null)
			dbnode.setProperty(VALUE_PROPERTY,v);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized void delete() {
		//logger.trace("deleting leaf "+id());
		super.delete();
		if (mode==UPDATE) {
			for (Relationship relationship : dbnode.getRelationships())
				relationship.delete();
			dbnode.delete(); //finally, we can delete the database node and its properties
		}
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized boolean equals(Object obj) {
		if (mode==READ)
			return id().equals(((Node)obj).id()); //optimisation on reading
		else 
			return super.equals(obj);
	}
	
	/**{@inheritDoc}*/
	@Override
	public int hashCode() {
		if (mode==READ)
			return id().hashCode(); //optimisation on reading
		else
			return super.hashCode();
	}

}
