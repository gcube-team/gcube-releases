package org.gcube.search.sru.consumer.parser.sruparser.tree;

import java.io.Serializable;

public class GCQLNode implements Serializable {
	
	private static final long serialVersionUID = 103643332306263456L;

	/**
	 * Decompiles a parse-tree into a CQL query.
	 * @return
	 */
	public String toCQL() {
		return "";
	}
	
	/**
	 * prints the node as part of the tree to which it belongs
	 * @param numSpaces
	 */
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++)
			System.out.print("*");
		System.out.println(this.toString());
	}

}
