package org.gcube.common.vremanagement.deployer.impl.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * An extension of the standard {@link HashSet} handling multiple instances of
 * the same element.
 * 
 * @param <E>
 *            element type
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see HashSet
 * @see Set
 * 
 */
public class CountableHashSet<E> extends HashSet<E> implements Serializable {

	private static final long serialVersionUID = 6889625341844385704L;

	/** key counter */
	private Map<E, Integer> keycounter = new HashMap<E, Integer>();

	/**
	 * {@inheritDoc}
	 */
	public CountableHashSet() {
	}

	public CountableHashSet(Collection<? extends E> c) {
		super(c);
	}

	public CountableHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	public CountableHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E o) {
		if (this.keycounter.containsKey(o)) {
			Integer i = this.keycounter.get(o);
			i++;
		} else {
			super.add(o);
			this.keycounter.put(o, 1);
		}
		// anyhow, we return true, since the element was accepted
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		if (this.keycounter.containsKey(o)) {
			Integer i = this.keycounter.get(o);
			if (1 == i) {
				super.remove(o);
				this.keycounter.remove(o);
			} else
				i--;
		} else
			// should be strange to go in this case....
			return super.remove(o);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		if (c.size() == 0)
			return modified;
		
		if (this.size() > c.size()) {
			for (Iterator<?> i = c.iterator(); i.hasNext();)
				modified |= this.remove(i.next());
		} else {
			for (Iterator<?> i = iterator(); i.hasNext();) {
				if (c.contains(i.next())) {
					i.remove();
					modified = true;
				}
			}
		}
		return modified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		if (c.size() == 0)
			return modified;
		
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext()) {
		    if (this.add(e.next()))
		    	modified = true;
		}
		return modified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		super.clear();
		this.keycounter.clear();
	}
	
	/**
	 * Gets the key counter of the given element
	 * 
	 * @param o the element to count
	 * @return the key counter
	 */
	public int getCounter(Object o) {
		if (this.keycounter.containsKey(o))
			return this.keycounter.get(o);
		else 
			return 0;
	}
}
