package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.io.StringWriter;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Declares a node which contains unbound parameters.
 * </p>
 */
public class TNodeWithUnbound {
	private TNode node;
	private List<Parameter> unbound;
	private boolean srctrgunbound=true;
	protected boolean isRefToSource(){
		return srctrgunbound;
	}
	protected boolean isRefToTarget(){
		return !srctrgunbound;
	}
	protected void setRefsToSource(){
		this.srctrgunbound=true;
	}
	protected void setRefsToTarget(){
		this.srctrgunbound=false;
	}
	protected TNodeWithUnbound(TNode node, List<Parameter> unbound) {
		this.node = node;
		this.unbound = unbound;
	}
	protected TNode getNode() {
		return node;
	}
	protected void setNode(TNode node) {
		this.node = node;
	}
	protected List<Parameter> getUnbound() {
		return unbound;
	}
	protected void setUnbound(List<Parameter> unbound) {
		this.unbound = unbound;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return A string representation of the <tt>TNodeWithUnbound</tt>.
	 */
	@Override
	public String toString(){
		StringWriter writer= new StringWriter();
		writer.append("TNodeWithUn: "+node.toString()+"\nrefsTo: "+srctrgunbound);
		if(unbound!=null){
			for(Parameter param: unbound){
				writer.append(" - "+param.getName()+"=\""+param.getValue()+"\"");
			}
		}writer.append("\nend of TNodeWithUn");
		return writer.toString();
	}
}
