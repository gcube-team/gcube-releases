//package org.gcube.portlets.user.results.client.model;
//
//import java.io.Serializable;
//
//import com.google.gwt.user.client.rpc.IsSerializable;
//
//
///**
// * represents the references of a Digital Object
// * @author massi
// *
// */
//public class Client_StorageReference implements Serializable {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	String Source;
//	String Target;
//	String uri;
//	String SecondaryRole;
//	String Propagation;
//	
//	/**
//	 * Empty Constructor
//	 *
//	 */
//	public Client_StorageReference() {
//		super();
//	}
//
//	/**
//	 * 
//	 * @param source
//	 * @param target
//	 * @param role
//	 * @param secondaryRole
//	 * @param propagation
//	 */
//	public Client_StorageReference(String source, String target, String uri, String secondaryRole, String propagation) {
//		super();
//		Source = source;
//		Target = target;
//		this.uri = uri;
//		SecondaryRole = secondaryRole;
//		Propagation = propagation;
//	}
//
//	public String getPropagation() {
//		return Propagation;
//	}
//
//	public void setPropagation(String propagation) {
//		Propagation = propagation;
//	}
//
//	public String getUri() {
//		return uri;
//	}
//
//	public void setRole(String uri) {
//		this.uri = uri;
//	}
//
//	public String getSecondaryRole() {
//		return SecondaryRole;
//	}
//
//	public void setSecondaryRole(String secondaryRole) {
//		SecondaryRole = secondaryRole;
//	}
//
//	public String getSource() {
//		return Source;
//	}
//
//	public void setSource(String source) {
//		Source = source;
//	}
//
//	public String getTarget() {
//		return Target;
//	}
//
//	public void setTarget(String target) {
//		Target = target;
//	}
//	
//	
//	
//}
