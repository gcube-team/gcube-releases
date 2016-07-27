/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

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
public class CommonName implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1140787822064828769L;

	public final static String ID_FIELD = "id";
	public final static String NAME= "name";
	public final static String LANGUAGE= "language";
	public final static String REFERENCE_RESULTROW_ID = "resultRowId";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int internalId;
	
	
	protected int id;
	
	private int resultRowId;

	protected String name;

	protected String language;
	
	public CommonName(){}
	
	/**
	 * @param name
	 * @param language
	 */
	public CommonName(String name, String language, int resultRowId) {
		this.name = name;
		this.language = language;
		this.resultRowId = resultRowId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonName [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", language=");
		builder.append(language);
		builder.append("]");
		return builder.toString();
	}

	public int getResultRowId() {
		return resultRowId;
	}
	
}
