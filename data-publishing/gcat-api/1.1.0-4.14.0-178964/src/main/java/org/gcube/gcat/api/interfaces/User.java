package org.gcube.gcat.api.interfaces;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface User<C,D> extends CRUD<C,D> {
	
	String USERS = "users";

	public String list();
	
	@Override
	public String read(String username);
	
	@Override
	public String update(String username, String json);
	
	@Override
	public D delete(String username);

}
