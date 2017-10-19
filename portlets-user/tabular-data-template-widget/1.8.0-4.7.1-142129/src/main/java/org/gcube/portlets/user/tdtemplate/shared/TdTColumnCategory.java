/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 17, 2014
 *
 */
public class TdTColumnCategory implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2954563041495609450L;
	
	private String id;
	private String name;
	private List<TdTDataType> tdtDataType;
	
	/**
	 * 
	 */
	public TdTColumnCategory() {
	}


	/**
	 * @param id
	 * @param name
	 */
	public TdTColumnCategory(String id, String name) {
		this.id = id;
		this.name = name;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TdTDataType> getTdtDataType() {
		return tdtDataType;
	}

	public void setTdtDataType(List<TdTDataType> tdtDataType) {
		this.tdtDataType = tdtDataType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTColumnCategory [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", tdtDataType=");
		builder.append(tdtDataType);
		builder.append("]");
		return builder.toString();
	}

}
