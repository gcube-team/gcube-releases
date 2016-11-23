package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraphImpl.Consistency;

/**
 * @author Dimitris
 * 
 * This class finds transformationUnit paths in the graph...
 * !!!!Is not Thread Safe...
 */
public class PathFinder {
	private ArrayList<TNode> visited=new ArrayList<TNode>();
	private Path currpath=new Path();

	private ArrayList<Path> paths = null;
	private TNodeWithUnbound tnode;
	private NodesCollection nodes=null;
	private Consistency consistencyLevel=null;
	
	protected ArrayList<Path> getPaths(TNodeWithUnbound snode, TNodeWithUnbound tnode, Consistency consistencyLevel, NodesCollection nodes){
		paths = new ArrayList<Path>();
		this.tnode=tnode;
		this.nodes=nodes;
		this.consistencyLevel=consistencyLevel;
		search(snode);
		return paths;
	}
	
	private static Logger log = LoggerFactory.getLogger(PathFinder.class);
	
	/* 
	 * TODO: insert consistency and graph nodelist:
	 * should find support or generic support nodes of each target node of an edge
	 * and check also these nodes
	 */
	private void search(TNodeWithUnbound curnode){
		if(curnode.getNode().equals(tnode.getNode())){
			if(currpath.size()==0){
				log.warn("Target and current nodes are the same but path size is 0. We are searching from the same to the same node. These occasions may be checked in adnvance...");
				return;
			}
			Path newpath = currpath.clone();
			TEdge last = newpath.getPath().get(newpath.size()-1);
			newpath.putTUnbound(last, tnode.getUnbound());
			paths.add(newpath);
			return;
		}

//		System.out.println(curnode.getNode().toString());
		for(TEdge edge: curnode.getNode().getEdges()){
			ArrayList<TNodeWithUnbound> tosearch, tosearchby;
			if(consistencyLevel.equals(TransformationsGraphImpl.Consistency.EXACT)){
				tosearch=new ArrayList<TNodeWithUnbound>();
				tosearch.add(new TNodeWithUnbound(edge.getToNode(), null));
			}else if(consistencyLevel.equals(TransformationsGraphImpl.Consistency.SUPPORT)){
				tosearch=nodes.getAnyThatSupportWithUnbound(edge.getToNode());
//				System.out.println("Printing tosearch");
//				for(TNodeWithUnbound nwunbound: tosearch){
//					System.out.println(nwunbound.toString());
//				}
//				System.out.println("Printing tosearchby");
				tosearchby=nodes.getAnySupportedByWithUnbound(edge.getToNode());
//				for(TNodeWithUnbound nwunbound: tosearchby){
//					System.out.println(nwunbound.toString());
//				}
				for(TNodeWithUnbound nwunboundby: tosearchby){
					boolean exists = false;
					for(TNodeWithUnbound nwunbound: tosearch){
						if(nwunbound.getNode().equals(nwunboundby.getNode())){
							exists=true;
							break;
						}
					}
					if(!exists){
						tosearch.add(nwunboundby);
					}
				}
			}else if(consistencyLevel.equals(TransformationsGraphImpl.Consistency.GENERIC)){
				tosearch=nodes.getGenericallySupported(edge.getToNode());
			}else{
				tosearch=new ArrayList<TNodeWithUnbound>();
			}
				
			for(TNodeWithUnbound node: tosearch){//Should reconsider that? 
				//In support and generic consistency, tnode is a content format that the edge (transformationUnit) may not be dirrectly performed
				//but there are other nodes that support the format of the target node 
//				System.out.println("tosearch: "+node.getNode().toString());
				if(!visited.contains(node.getNode())){ 
					visited.add(node.getNode());
					
					currpath.add(edge);
					
					if(curnode.isRefToSource()){
//						if(curnode.getUnbound()!=null){
//							for(Parameter param: curnode.getUnbound())
//								System.out.println("puttings_"+param.getName()+"=\""+param.getValue()+"\"");
//						}
						currpath.putSUnbound(edge, curnode.getUnbound());
					}
					
//					These params will be put as target to unbound of the previous edge  
					if(node.isRefToTarget()){
//						if(node.getUnbound()!=null){
//							for(Parameter param: node.getUnbound())
//								System.out.println("puttingt_"+param.getName()+"=\""+param.getValue()+"\"");
//						}
						currpath.putTUnbound(edge, node.getUnbound());
					}
					
					currpath.addCost(edge.getCost());
					search(node);
					visited.remove(node.getNode());
					currpath.remove(edge);
					if(curnode.isRefToSource())
						currpath.removeSUnbound(edge);
					if(node.isRefToTarget()){
						currpath.removeTUnbound(edge);
					}
					currpath.subCost(edge.getCost());
				}
			}
		}
	}
}
