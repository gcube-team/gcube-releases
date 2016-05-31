/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types;

import org.w3c.dom.Node;

/**
 * This class contains the result of call to the 'evaluate()' method of the
 * {@link ExecutionEntity} class or the {@link DataType} class.
 * 
 * @author Spyros Boutsis, NKUA
 */
public class EvaluationResult {

	/** The object that the evaluated expression refers to. This object is evaluable
	 * itself. */
	private Evaluable evaluatedObject;
	
	/** The node of the evaluated object's XML definition that the evaluated expression
	 * specifically refers to */
	private Node evaluatedNode;
	
	public EvaluationResult(Evaluable object, Node node) {
		this.evaluatedObject = object;
		this.evaluatedNode = node;
	}
	
	public Evaluable getEvaluatedObject() {
		return this.evaluatedObject;
	}
	
	public Node getEvaluatedNode() {
		return this.evaluatedNode;
	}
}
