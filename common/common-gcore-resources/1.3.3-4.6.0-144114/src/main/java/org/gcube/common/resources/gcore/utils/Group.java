package org.gcube.common.resources.gcore.utils;

import java.util.Arrays;
import java.util.Collection;

public class Group<T> extends ReadOnlyGroup<T> implements Collection<T>  {

	public Group(Collection<T> values, Class<? extends T> clazz) {
		super(values,clazz);
	}
	
	/**
	 * Adds a new element to the collection and returns it.
	 * @return the new element
	 */
	public T add() {
		try {
			T t = clazz.newInstance();
			add(t);
			return t;
		}
		catch(Exception e) {
			throw new RuntimeException("invalid model class, cannot be instantiated reflectively",e);
		}
	}
	
	public <S extends T> S add(Class<S> subclass) {
		try {
			S s = subclass.newInstance();
			add(s);
			return s;
		}
		catch(Exception e) {
			throw new RuntimeException("invalid model class, cannot be instantiated reflectively",e);
		}
	}
	

	public boolean add(T element) {
		return base.add(element);
	};
	
	public boolean remove(Object element) {
		return base.remove(element);
	}
		
	public void clear() {
		base.clear();
	}

	public boolean removeAll(Collection<?> c) {
		return base.removeAll(c);
	}
	
	public boolean removeAll(Object ... elements) {
		return base.removeAll(Arrays.asList(elements));
	}

	public boolean retainAll(Collection<?> c) {
		return base.retainAll(c);
	}
	
	public boolean retainAll(Object ... elements) {
		return base.retainAll(Arrays.asList(elements));
	}
	
	public boolean addAll(@SuppressWarnings("unchecked") T ... elements) {
		return base.addAll(Arrays.asList(elements));
	}
	
	public boolean addAll(Collection<? extends T> c) {
		return base.addAll(c);
	}

		
}
