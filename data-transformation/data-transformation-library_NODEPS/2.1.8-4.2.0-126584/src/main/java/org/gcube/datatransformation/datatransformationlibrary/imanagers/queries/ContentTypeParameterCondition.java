package org.gcube.datatransformation.datatransformationlibrary.imanagers.queries;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Condition of a content type.
 * </p>
 */
public class ContentTypeParameterCondition {

	private String name;
	private String value;
	
	/**
	 * @return The name of the condition.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name of the condition.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return The value of the condition.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value of the condition.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * Instantiates a <tt>ContentTypeParameterCondition</tt>.
	 */
	public ContentTypeParameterCondition(){}
	
	/**
	 * Instantiates a <tt>ContentTypeParameterCondition</tt>.
	 * @param name The name of the condition.
	 * @param value The value of the condition.
	 */
	public ContentTypeParameterCondition(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
}
