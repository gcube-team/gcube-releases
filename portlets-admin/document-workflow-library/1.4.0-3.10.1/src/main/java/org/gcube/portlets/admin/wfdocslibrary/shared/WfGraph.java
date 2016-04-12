package org.gcube.portlets.admin.wfdocslibrary.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <code> WfGraph </code> class represent a workflow to be associated to a document 
 * it is implemented as an Adjacency List 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (0.1) 
 */
@SuppressWarnings("serial")
public class WfGraph implements Serializable {
	/**
	 * the graph
	 */
	private ForwardAction[][] matrix;

	private Step[] steps;
	public WfGraph() {}
	/**
	 * @param matrix
	 * @param steps
	 */
	public WfGraph(ForwardAction[][] matrix, Step[] steps) {
		super();
		this.matrix = matrix;
		this.steps = steps;
	}

	/**
	 * creates an empty workflow
	 */
	public WfGraph(Step...steps) {
		super();
		this.steps = steps;
		this.matrix = new ForwardAction[steps.length][steps.length];
		for (int i = 0; i < steps.length; i++) {
			for (int j = 0; j < steps.length; j++) {
				matrix[i][j] = null;
			}
		}
	}



	/**
	 * add the edge from node1 to node 2
	 * @param v1 vertex from 
	 * @param v2 vertex to
	 */
	public void addEdge(Step v1, Step v2, ForwardAction edgeAction) {
		matrix[indexOf(v1)][indexOf(v2)] = edgeAction;
	}

	/**
	 * return the index of a given label
	 * @param label
	 * @return
	 */
	public int indexOf(Step toCompare) {
//		if (steps == null)
//		GWT.log("Steps NULL");
		for (int i = 0; i < steps.length; i++) {
			if (steps[i].getLabel().equalsIgnoreCase(toCompare.getLabel()))
				return i;
		}
		return -1;
	}
	
	/**
	 * return the forward actions associated to a given source step
	 * @param source
	 * @return
	 */
	public ArrayList<ForwardAction> getForwardActions(Step source) {
		ArrayList<ForwardAction> fwActions = new ArrayList<ForwardAction>();
		int i = indexOf(source);
		if (i < 0) {
			throw new AssertionError("The source step doesn not belong to this graph");
		}
		for (int j = 0; j < steps.length; j++) {
			if (matrix[i][j] != null) {
				fwActions.add(matrix[i][j]);
			}
		}
		return fwActions;
	}
	
	
	
	/**
	 * 
	 * @param node
	 * @return the list of adjacents node
	 */
	public List<Step> adjacentNodes(Step v) {
		ArrayList<Step>  adjacent = new ArrayList<Step>();
		int row = indexOf(v);
		for (int i = 0; i < steps.length; i++) {
			if (matrix[row][i] != null)
				adjacent.add(steps[i]);
		}
		return adjacent;
	}

	public ForwardAction[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(ForwardAction[][] matrix) {
		this.matrix = matrix;
	}
	public Step[] getSteps() {
		return steps;
	}
	public void setSteps(Step[] steps) {
		this.steps = steps;
	}

	public String toString() {
		String toReturn = "";
		for (int i = 0; i < steps.length; i++) {
			toReturn += " " + steps[i] ;
			for (int j = 0; j < steps.length; j++) {
				if (matrix[i][j] != null) {
					toReturn += "-> " + steps[j] + " " + matrix[i][j] + " ";
				}
			}
			toReturn += "";
		}
		return toReturn;
	}
}
