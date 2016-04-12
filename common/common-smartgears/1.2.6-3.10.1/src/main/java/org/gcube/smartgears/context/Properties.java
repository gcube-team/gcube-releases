package org.gcube.smartgears.context;

import static org.gcube.smartgears.utils.Utils.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A collection of uniquely named {@link Property}s.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Properties implements Iterable<Property> {

	private final Map<String, Property> properties = new HashMap<String, Property>();
	private Properties parent;
	
	/**
	 * Creates an instance that delegates to another for unknown properties.
	 * @param parent the parent instance
	 */
	public Properties(Properties parent) {
		this.parent=parent;
	}
	
	/**
	 * Creates an instance.
	 */
	public Properties() {}
	
	@Override
	public Iterator<Property> iterator() {
		return properties.values().iterator();
	}

	/**
	 * Adds one or more properties to this collection.
	 * 
	 * @param properties the properties
	 */
	public synchronized void add(Property ... properties) {
		
		notNull("properties",properties);
		
		for (Property property : properties)
			this.properties.put(property.name(),property);
	}

	/**
	 * Returns <code>true</code> if this collection contains a given property.
	 * 
	 * @param name the name of the property
	 * @return <code>true</code> if this collection contains a property with the given name
	 */
	public synchronized boolean contains(String name) {
		
		notNull("property name",name);
		
		return this.properties.containsKey(name) || 
				parent==null?
						false: 
						this.parent.contains(name);
	}

	/**
	 * Removes a given property.
	 * 
	 * @param name the name of the property
	 * 
	 * @throws IllegalStateException if a property with the given name does not exist in this collection
	 */
	public void delete(String name) {

		System.out.println("here");
		notNull("property name",name);
		
		if (this.properties.remove(name) == null)
			if (parent==null)
				throw new IllegalStateException("unknown property " + name);
			else 
				parent.delete(name);
	}

	/**
	 * Returns a given property in this collection.
	 * 
	 * @param name the name of the property
	 * @return the property
	 * 
	 * @throws IllegalStateException if a property with a given name does not exist in this collection
	 */
	public synchronized Property lookup(String name) {

		notNull("property name",name);
		
		Property property = this.properties.get(name);

		if (property == null)
			if (parent==null)
				throw new IllegalStateException("unknown property " + name);
			else
				property = parent.lookup(name);

		return property;

	}

	/**
	 * Returns <code>true</code> if this collection has no properties.
	 * 
	 * @return <code>true</code> if this collection has no properties
	 */
	public synchronized boolean isEmpty() {
		return properties.isEmpty() || parent==null? true: parent.isEmpty();
	}


	@Override
	public String toString() {
		final int maxLen = 100;
		return "board=" + (properties != null ? toString(properties.entrySet(), maxLen) : null);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Properties other = (Properties) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	
}
