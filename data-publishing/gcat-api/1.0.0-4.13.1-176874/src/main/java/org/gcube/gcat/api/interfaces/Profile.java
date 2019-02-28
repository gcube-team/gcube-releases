package org.gcube.gcat.api.interfaces;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface Profile<C,D> {
	
	String PROFILES = "profiles";

	public String list();
	
	public C create(String name, String xml);
	
	public String read(String name);
	
	public String update(String name, String xml);
	
	public D delete(String name);	
	
}
