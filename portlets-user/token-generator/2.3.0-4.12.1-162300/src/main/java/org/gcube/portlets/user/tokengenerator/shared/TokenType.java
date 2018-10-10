package org.gcube.portlets.user.tokengenerator.shared;

/**
 * Token types
 * @author Costantino Perciante AT ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum TokenType {
	
	/**
	 * The standard token type
	 */
	CONTEXT, 
	/**
	 * A qualified token, that is a token identified by a label
	 */
	QUALIFIED, 
	/**
	 * A token bound to a triple "username, context, application identifier"
	 */
	APPLICATION

}
