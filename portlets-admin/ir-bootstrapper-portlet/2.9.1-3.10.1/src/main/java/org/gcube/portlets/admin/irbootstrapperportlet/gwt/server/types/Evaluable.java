/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types;


/**
 * This interface describes an object which is 'evaluable', meaning that expressions can be evaluated on
 * this object, in order to select portions of the data it contains.
 *  
 * @author Spyros Boutsis, NKUA
 */
public interface Evaluable {

	public EvaluationResult evaluate(String expression) throws Exception;
}
