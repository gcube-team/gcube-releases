package org.gcube.common.scope.impl;



/**
 * A object model of a scope.
 * 
 * @author Fabio Simeoni
 *
 */
public class ScopeBean {

	/**
	 * Scope separators used in linear syntax.
	 */
	protected static String separator = "/";
	
	/**
	 * Scope types	 *
	 */
	public static enum Type implements Comparable<Type> {VRE,VO,INFRASTRUCTURE}
		
	/**
	 * The name of the scope.
	 */
	private String name;
	
	/**
	 * The type of the scope.
	 */
	private Type type;
	
	/**
	 * The enclosing scope, if any.
	 */
	private ScopeBean enclosingScope;
	
	
	/**
	 * Returns the name of the scope.
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns <code>true</code> if the scope has a given {@link Type}.
	 * @param type the type
	 * @return <code>true</code> if the scope has the given type, <code>false</code> otherwise
	 */
	public boolean is(Type type) {
		return this.type.equals(type);
	}
	
	/**
	 * Returns the {@link Type} of the scope.
	 * @return the type
	 */
	public Type type() {
		return type;
	}
	
	/**
	 * Returns the enclosing scope, if any.
	 * @return the enclosing scope, or <code>null</code> if the scope is top-level
	 */
	public ScopeBean enclosingScope() {
		return enclosingScope;
	}
	
	public ScopeBean(String scope) throws IllegalArgumentException {
		
		String[] components=scope.split(separator);
		
		if (components.length<2 || components.length>4) 
			throw new IllegalArgumentException("scope "+scope+" is malformed");
		
		if(components.length>3) {
			this.name=components[3];
			this.enclosingScope = new ScopeBean(separator+components[1]+separator+components[2]);
			this.type=Type.VRE;
		}
		else if (components.length>2) {
			this.name=components[2];
			this.enclosingScope=new ScopeBean(separator+components[1]);
			this.type=Type.VO;
		}
		else {
			this.name=components[1];
			this.type=Type.INFRASTRUCTURE;
		}
			
	}
	
	/**
	 * Returns the linear expression of the scope.
	 */
	public String toString() {
		return is(Type.INFRASTRUCTURE)?separator+name():
			   enclosingScope().toString()+separator+name();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enclosingScope == null) ? 0 : enclosingScope.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ScopeBean other = (ScopeBean) obj;
		if (enclosingScope == null) {
			if (other.enclosingScope != null)
				return false;
		} else if (!enclosingScope.equals(other.enclosingScope))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
