package org.gcube.datatransformation.datatransformationlibrary.model;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>TransformationRuleElement</tt> is the base implementation of the source or targets.
 * </p>
 */
public class TransformationRuleElement {
	
	protected TransformationUnit transformationUnit;
	protected ContentType contentType=null;
	
	/**
	 * Returns the content type of this element.
	 * @return The content type of this element.
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type of this element.
	 * @param contentType The content type of this element.
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * Returns the transformation unit in which this element belongs to.
	 * @return The transformation unit in which this element belongs to.
	 */
	public TransformationUnit getTransformationUnit() {
		return transformationUnit;
	}

	/**
	 * Sets the transformation unit in which this element belongs to.
	 * @param transformation the transformation unit in which this element belongs to.
	 */
	public void setTransformationUnit(TransformationUnit transformation) {
		this.transformationUnit = transformation;
	}
}
