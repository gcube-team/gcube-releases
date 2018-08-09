/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vfloros
 *
 */
public class UsersAndRights {
	private static Logger logger = LoggerFactory.getLogger(UsersAndRights.class);

	private String id = "";
	Rights rights = new Rights();
	
	public UsersAndRights() {
		super();
		logger.trace("Initialized default contructor for UsersAndRights");

	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Rights getRights() {
		return rights;
	}
	public void setRights(Rights rights) {
		this.rights = rights;
	}
}
