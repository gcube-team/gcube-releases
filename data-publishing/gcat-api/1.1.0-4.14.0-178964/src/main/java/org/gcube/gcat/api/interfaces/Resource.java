package org.gcube.gcat.api.interfaces;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface Resource<C,D> {
	
	String RESOURCES = "resources";
	
	public String list(String itemID);
	
	public C create(String itemID, String json);
	
	public String read(String itemID,String resourceID);
	
	public String update(String itemID, String resourceID, String json);
	
	public D delete(String itemID, String resourceID);
}
