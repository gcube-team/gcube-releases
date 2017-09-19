package org.gcube.common.resources.gcore.utils;

import static java.util.Arrays.*;

import java.util.Collection;
import java.util.Iterator;

public class ReadOnlyGroup<T> implements Iterable<T>  {

	protected final Collection<T> base;
	protected final Class<? extends T> clazz;
	
	public ReadOnlyGroup(Collection<T> values, Class<? extends T> clazz) {
		this.base=values;
		this.clazz=clazz;
	}
	
	public boolean contains(Object o) {
		return base.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return base.containsAll(c);
	}
	
	public boolean containsAll(Object ... elements) {
		return base.containsAll(asList(elements));
	}

//	public boolean equals(Object o) {
//		return base.equals(o);
//	}
//
//	public int hashCode() {
//		return base.hashCode();
//	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReadOnlyGroup<?> other = (ReadOnlyGroup<?>) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}

	public boolean isEmpty() {
		return base.isEmpty();
	}

	public Collection<T> asCollection() {
		return base;
	}

	public int size() {
		return base.size();
	}

	public Object[] toArray() {
		return base.toArray();
	}

	public <E> E[] toArray(E[] a) {
		return base.toArray(a);
	}

	
	public Iterator<T> iterator() {
		return base.iterator();
	}
	
	
}

