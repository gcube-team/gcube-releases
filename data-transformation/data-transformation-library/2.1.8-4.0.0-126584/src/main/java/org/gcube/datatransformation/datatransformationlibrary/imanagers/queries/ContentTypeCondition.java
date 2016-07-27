package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

import java.util.ArrayList;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Condition for a content type query.
 * </p>
 */
public class ContentTypeCondition {

	private String mimetype;
	private String mimesubtype;
	private ArrayList<ContentTypeParameterCondition> contentTypeParameters = new ArrayList<ContentTypeParameterCondition>();

	/**
	 * @return The content types parameters conditions.
	 */
	public ArrayList<ContentTypeParameterCondition> getContentTypeParameters() {
		return contentTypeParameters;
	}
	/**
	 * @param contentTypeParameters The content types parameters conditions.
	 */
	public void setContentTypeParameters(
			ArrayList<ContentTypeParameterCondition> contentTypeParameters) {
		this.contentTypeParameters = contentTypeParameters;
	}
	/**
	 * @return The mimesubtype condition.
	 */
	public String getMimesubtype() {
		return mimesubtype;
	}
	/**
	 * @param mimesubtype The mimesubtype condition.
	 */
	public void setMimesubtype(String mimesubtype) {
		this.mimesubtype = mimesubtype;
	}
	/**
	 * @return The mimetype condition.
	 */
	public String getMimetype() {
		return mimetype;
	}
	/**
	 * @param mimetype The mimetype condition.
	 */
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	/**
	 * @param arg0 Adds a <tt>ContentTypeParameterCondition</tt>.
	 * @return true if added.
	 */
	public boolean addContentTypeParameterCondition(ContentTypeParameterCondition arg0) {
		return contentTypeParameters.add(arg0);
	}
	/**
	 * Returns a <tt>ContentTypeParameterCondition</tt>.
	 * @param arg0 the index of the array.
	 * @return the <tt>ContentTypeParameterCondition</tt>
	 */
	public ContentTypeParameterCondition getContentTypeParameterCondition(int arg0) {
		return contentTypeParameters.get(arg0);
	}
	/**
	 * @return true if at least a <tt>ContentTypeParameterCondition</tt> exists.
	 */
	public boolean hasContentTypeParameterConditions() {
		return contentTypeParameters.isEmpty();
	}
	/**
	 * @return The number of ContentTypeParameterConditions.
	 */
	public int sizeOfContentTypeParameterConditions() {
		return contentTypeParameters.size();
	}
}
