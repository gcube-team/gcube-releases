package org.gcube.data.tr;

import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.impl.AbstractSource;
import org.gcube.data.tr.requests.Mode;
import org.gcube.data.trees.data.Tree;

/**
 * A local {@link Source} of {@link Tree}s.
 * 
 * @author Fabio Simeoni
 *
 */
public class TreeSource extends AbstractSource {

	private static final long serialVersionUID = 1L;

	private final Store store;
	private final List<QName> types;
	
	
	/**
	 * Creates an instance with a given identifier.
	 * @param id the identifier
	 */
	public TreeSource(String id,List<QName> types,Store store) {
		super(id);
		this.store=store;
		this.types=types;
	}
	
	@Override
	public List<Property> properties() {
		return null;
	}
	
	@Override
	public List<QName> types() {
		return types;
	}

	/**
	 * Returns the underlying tree store
	 * @return the store
	 */
	public Store store() {
		return this.store;
	}
	
	@Override
	public synchronized Long cardinality() {
		return store.cardinality();
	}
	
	/**
	 * Sets the access mode.
	 * @param mode the mode
	 */
	void setMode(Mode mode) {
		
		if ((mode == Mode.FULLACESSS || mode == Mode.READABLE) && reader()==null)
			setReader(new Reader(this));
			
		if ((mode == Mode.FULLACESSS || mode == Mode.WRITABLE) && writer()==null)
			setWriter(new Writer(this));
		
	}
	
	@Override
	public String toString() {
		return super.toString()+"["+store+"]";
	}
}
