/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;

import java.io.Serializable;


/**
 * The Class TdBaseData.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 25, 2015
 */
public abstract class TdBaseData implements Serializable, Comparable<TdBaseData>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 354060459041714337L;
	protected String id;
	protected String label;
	
	/**
	 * Instantiates a new td base data.
	 */
	public TdBaseData() {
	}

	/**
	 * Instantiates a new td base data.
	 *
	 * @param id the id
	 * @param label the label
	 */
	public TdBaseData(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TdBaseData o) {
		
		if(o==null)
			return -1;
		
		try{
			int i1 = Integer.parseInt(this.getId());
			int i2 = Integer.parseInt(o.getId());
			
			if(i1<i2)
				return -1;
			if (i1>i2)
				return 1;
			
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return o.getId().compareTo(o.getId());
	}



	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * Sets the label.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdBaseData [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
}
