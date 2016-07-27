package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import java.io.Serializable;
import java.util.List;

/**
 * A class that represents the status of search
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchStatusBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124539980975772384L;

	private boolean isFullTextSupported = false;
	
	private FieldBean fullTextSearchableField;
	
	private List<FieldBean> searchableFields;
	
	/**
	 * Constructor
	 * 
	 * @param isFullTextSupported True if fulltext search is supported, else False
	 * @param fullTextField The field for the fulltext search
	 * @param searchableFields A list with the available searchable fields
	 */
	public SearchStatusBean(boolean isFullTextSupported, FieldBean fullTextField, List<FieldBean> searchableFields) {
		this.isFullTextSupported = isFullTextSupported;
		this.fullTextSearchableField = fullTextField;
		this.searchableFields = searchableFields;
	}

	/**
	 * True or False if fulltext search is supported or not
	 * 
	 * @return 
	 */
	public boolean isFullTextSupported() {
		return isFullTextSupported;
	}

	/**
	 * A list with the available searchable fields
	 * 
	 * @return 
	 */
	public List<FieldBean> getSearchableFields() {
		return searchableFields;
	}
	
	/**
	 * Returns the fulltext searchable field
	 * 
	 * @return
	 */
	public FieldBean getFullTextSearchableField() {
		return fullTextSearchableField;
	}
	
	
}
