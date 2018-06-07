package org.gcube.smartgears.context;

import static org.gcube.smartgears.utils.Utils.*;


/**
 * A named property with a value and a description.
 * 
 * @author Fabio Simeoni
 *
 */
public class Property {

	private final String name;
	private final String description;
	private final Object value;
	private boolean display =true;
	
	/**
	 * Creates an instance with a given name and value.
	 * @param name the name
	 * @param value the value
	 */
	public Property(String name, Object value) {
		this(name,value,null);
	}
	
	/**
	 * Creates an instance with a given name, value, and description.
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	public Property(String name, Object value, String description) {
		
		notNull("property name",name);
		notNull("property value",value);
		
		this.name=name;
		this.description=description;
		this.value=value;
	}
	
	/**
	 * Returns the name of this property.
	 * @return the name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Sets whether the property is intended for display
	 * @param display <code>true</code> if the property is intended for display
	 */
	public void display(boolean display) {
		this.display = display;
	}
	
	/**
	 * Returns <code>true</code> if the property is intended for display
	 * @return <code>true</code> if the property is intended for display
	 */
	public boolean isDisplay() {
		return display;
	}
	
	/**
	 * Returns the value of this property.
	 * 
	 * @return the value
	 */
	public Object value() {
		return value;
	}
	
	/**
	 * Returns the value of this property under a given type.
	 * 
	 * @return the value
	 * 
	 * @throws IllegalStateException if the value cannot be returned under the given type.
	 */
	public <S> S value(Class<S> type) {
		
		if (is(type))
			return type.cast(value());
		
		throw new IllegalStateException("property value "+value()+" of type "+value().getClass()+" cannot be typed as "+type.getCanonicalName());
		
	}
	
	/**
	 * Returns <code>true</code> if the value of this property has a given type.
	 * @param type the type
	 * @return <code>true</code> if the value of this property has a given type
	 */
	public boolean is(Class<?> type) {
		return type.isInstance(value());
	}
	
	/**
	 * Returns the description of this property.
	 * 
	 * @return the description
	 */
	public String description() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Property other = (Property) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name+"="+value;
	}
	
	

}
