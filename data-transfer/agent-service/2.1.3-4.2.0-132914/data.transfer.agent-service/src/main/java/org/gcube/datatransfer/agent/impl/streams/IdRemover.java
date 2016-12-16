package org.gcube.datatransfer.agent.impl.streams;

import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;

/**
 * 
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class IdRemover implements Generator<Tree,Tree> {

	 Tree clone(Tree node) {
		Tree inode = new Tree(null,node.attributes());
		for (Edge e : node.edges())
			inode.add(new Edge(e.label(),clone(e.target())));
		return inode;
	 }
	
	Node clone(Node node) {
		if (node instanceof InnerNode)
			return clone((InnerNode) node);
		else
			return clone((Leaf) node);
	}

	Node clone(Leaf l) {
		return new Leaf(l.value(),l.attributes());
	}
	

	Node clone(InnerNode node) {
		 InnerNode inode = new InnerNode(null,node.attributes());
		 for (Edge e : node.edges())
			 inode.add(new Edge(e.label(),clone(e.target())));
		 return inode;
	}
	
	@Override
	public Tree yield(Tree node) {	
		return clone(node);
	}
}

