package org.gcube.gcat.api.interfaces;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface Organization<C,D> extends CRUD<C,D> {
	
	String ORGANIZATIONS = "organizations";

	public String list(int limit, int offset);
	
	public String patch(String name, String json);
	
}
