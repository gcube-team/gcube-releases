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
public class Rights {
	private static Logger logger = LoggerFactory.getLogger(Rights.class);

	private short read = 0;
	private short edit = 0;
	private short delete = 0;
	
	public Rights(short read, short edit, short delete) {
		super();
		logger.trace("Initializing Rights...");

		this.read = read;
		this.edit = edit;
		this.delete = delete;
		logger.trace("Initialized Rights");
	}
	
	public Rights(int read, int edit, int delete) {
		super();
		logger.trace("Initializing Rights...");
		
		this.read = (short)read;
		this.edit = (short)edit;
		this.delete = (short)delete;
		logger.trace("Initialized Rights");
	}
	public Rights() {
		super();
		logger.trace("Initialized default contructor for Rights");

	}
	public short getRead() {
		return read;
	}
	public void setRead(short read) {
		this.read = read;
	}
	public short getEdit() {
		return edit;
	}
	public void setEdit(short edit) {
		this.edit = edit;
	}
	public short getDelete() {
		return delete;
	}
	public void setDelete(short delete) {
		this.delete = delete;
	}	
}