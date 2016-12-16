package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
@Entity
public class ItemParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;
	
	ItemParameter(){}

	public ItemParameter(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Comparator<ItemParameter> COMPARATOR = new Comparator<ItemParameter>()
    {
	// This is where the sorting happens.
        public int compare(ItemParameter o1, ItemParameter o2)
        {
            return o1.getKey().compareToIgnoreCase(o2.getKey());
        }
    };

	public int getInternalId() {
		return internalId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemParameter [key=");
		builder.append(key);
		builder.append(", value=");
		builder.append(value);
		builder.append(", internalId=");
		builder.append(internalId);
		builder.append("]");
		return builder.toString();
	}
}
