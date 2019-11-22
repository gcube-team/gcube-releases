package org.gcube.common.core.scope;

import org.gcube.common.scope.api.ScopeProvider;



/**
 * Models a scope within a gCube infrastructure: 
 * <p> 1) scopes are named, typed, and may be enclosed in another scopes. Three levels of nesting are possible: 
 * the type of root scopes is <code>INFRASTRUCTURE</code>, the type of scopes below infrastructures is <code>VO</code>, and the type of scopes below that
 * is <code>VRE</code>. 
 * <br>2) scopes are generated from linear expressions of the form <code>/[string]/[string]/[string]</code>, where each
 * separator introduces a level of scope and each string names the scope.
 *
 * 
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBEScope {

	/**
	 * Scope separators used in linear syntax.
	 */
	protected static char pathSeparator = '/';
	
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
	 * The map of service endpoints associated with the scope.
	 */
	protected ServiceMap serviceMap;
	
	/**
	 * The enclosing scope, if any.
	 */
	private GCUBEScope enclosingScope;
	

	/**
	 * Raised when parsing malformed scope expressions.
	 *
	 */
	public static class MalformedScopeExpressionException extends IllegalArgumentException{

		private static final long serialVersionUID = 1L;
		public MalformedScopeExpressionException() {super();}
		public MalformedScopeExpressionException(String msg) {super(msg);}
	}

		
	/**
	 * Helper class to parse scope expressions.
	 *
	 */
	public static class ScopeExpression {
		
		private String VO;
		private String infrastructure;
		private String VRE;
		
		public ScopeExpression(String expression) throws MalformedScopeExpressionException {
			String[] components=expression.split("/");

			if (components.length>1) this.VO = components[1];
			if (components.length>2) this.infrastructure = components[2];
			if (components.length>3) this.VRE = components[3];
			
			if (components.length<2 || components.length>4) throw new MalformedScopeExpressionException(); 
			
		}
		public String getInfrastructure() {return VO;}
		public String getVO() {return infrastructure;}
		public String getVRE() {return VRE;}
	}
	
	/**
	 * Returns a scope from its linear expression.
	 * @param exp the expressions.
	 * @return the scope.
	 * @throws MalformedScopeExpressionException if the expression is malformed. 
	 */
	public static GCUBEScope getScope(String exp) throws MalformedScopeExpressionException {
		ScopeExpression expression = new ScopeExpression(exp);
		GCUBEScope infrastructure = new VO(expression.getInfrastructure());
		infrastructure.type=Type.INFRASTRUCTURE;
		if (expression.getVO()!=null) {
			GCUBEScope vo = new VO(expression.getVO());
			vo.setEnclosingScope(infrastructure);
			vo.type=Type.VO;
			if (expression.getVRE()!=null) {
				GCUBEScope vre = new VRE(expression.getVRE());
				vre.setEnclosingScope(vo);
				vre.type=Type.VRE;
				return vre;				
			}
			return vo;
		}
		return infrastructure;
		
	}

	/**
	 * Used internally to creates a new scope with a given name.
	 * @param name the name.
	 */
	public void makeCurrent() {
		ScopeProvider.instance.set(this.toString());
	}
	
	/**
	 * Returns the infrastructure of this scope.
	 * @return the infrastructure.
	 */
	public GCUBEScope getInfrastructure() {
		if (this.isInfrastructure()) return this;
		return this.getEnclosingScope().getInfrastructure();
	}
	
	/**
	 * Used internally to creates a new scope with a given name.
	 * @param name the name.
	 */
	protected GCUBEScope(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the scope.
	 * @return the name.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Returns the enclosing scope, if any.
	 * @return the enclosing scope, or <code>null</code> if the scope is the whole infrastructure.
	 */
	public GCUBEScope getEnclosingScope() {
		return enclosingScope;
	}

	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	public boolean isInfrastructure() {
		return (this.getEnclosingScope()==null);
	}
	
	/**
	 * Used internally to set the enclosing scope of the scope.
	 * @param enclosingScope the enclosing scope.
	 */
	protected void setEnclosingScope(GCUBEScope enclosingScope) {
		this.enclosingScope = enclosingScope;
	}
	
	/**
	 * Returns the type of the scope.
	 * @return the type.
	 */	
	public Type getType() {
		return type;
	}
	
	/**
	 * Returns the linear expression of the scope.
	 */
	public String toString() {
		return this.isInfrastructure()?pathSeparator+this.getName():this.getEnclosingScope().toString()+pathSeparator+this.getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		if (this.getClass()!=object.getClass()) return false;
		return this.toString().equals(object.toString());
	}


	public boolean isEnclosedIn(GCUBEScope scope) {
		int check = this.getType().compareTo(scope.getType());//enumeration are compared in order of declaration!
		if (check==0) return this.equals(scope);
		if (check<0)return (this.getEnclosingScope().isEnclosedIn(scope));
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	/**
	 * Returns the map of service endpoints associated with the scope.
	 * @return the map
	 * @throws GCUBEScopeNotSupportedException if no service map is associated with the scope. 
	 */
	public abstract ServiceMap getServiceMap() throws GCUBEScopeNotSupportedException ;
	
	/**
	 * Sets the map of service endpoint associated with the scope.
	 * @param map the service map.
	 */
	public void setServiceMap(ServiceMap map) {};
	
}
