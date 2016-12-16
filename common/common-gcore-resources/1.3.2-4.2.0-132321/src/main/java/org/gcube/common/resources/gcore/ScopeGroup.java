package org.gcube.common.resources.gcore;

import java.util.Collection;

import org.gcube.common.resources.gcore.utils.ReadOnlyGroup;

public class ScopeGroup<T> extends ReadOnlyGroup<T> {

	public ScopeGroup(Collection<T> values, Class<? extends T> clazz) {
		super(values, clazz);
	}
	
	protected boolean add(T element) {
		return base.add(element);
	}
	
	protected boolean remove(T element) {
		return base.remove(element);
	}
	
}
