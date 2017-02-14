package gr.uoa.di.madgik.gcubesearchlibrary.model.beans;

import gr.uoa.di.madgik.gcubesearchlibrary.utils.PropertiesConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a result record object of a search query
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class ResultBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2125040520162650307L;

	private String objectURI = null;
	
	private String collectionID = null;
	
	private List<FieldBean> fields;
	
	/**
	 * Constructor
	 * 
	 * @param fields A list with @FieldBean
	 * @param objectURI Record's object URI
	 * @param collectionID Record's collectionID
	 */
	public ResultBean(List<FieldBean> fields, String objectURI, String collectionID) {
		this.fields = fields;
		this.objectURI = objectURI;
		this.collectionID = collectionID;
	}

	/**
	 * 
	 * @return A list with @FieldBean
	 */
	public List<FieldBean> getResultRecord() {
		return fields;
	}
	
	/**
	 * Returns the list with the short result record
	 * @return  A list with @FieldBean
	 */
	public List<FieldBean> getShortResultRecord() {
		List<FieldBean> sFields = new ArrayList<FieldBean>();
		for (FieldBean f : fields) {
			if (f.isPartOfShortResult())
				sFields.add(f);
		}
		if (!sFields.isEmpty())
			return sFields;
		// return the full result record if none of the fields are part of the short description
		else
			return this.fields;
	}
	
	public String getShortResultRecordInHtml() {
		String result = "";
		String title = null;
		String snippet = null;
		for (FieldBean f : fields) {
			if (f.getName().equalsIgnoreCase(PropertiesConstants.TITLE_FIELD))
				title = f.getId();
			else if (f.getName().equalsIgnoreCase(PropertiesConstants.SNIPPET))
				snippet = f.getId();
		}
		if (title != null)
			result += "<b>" + title + "</b><br>";
		if (snippet != null)
			result += snippet;
		return result;
	}
	/**
	 * Object's URI
	 * @return 
	 */
	public String getObjectURI() {
		return objectURI;
	}

	/**
	 * The collection ID of the result
	 * @return
	 */
	public String getCollectionID() {
		return collectionID;
	}
	
}
