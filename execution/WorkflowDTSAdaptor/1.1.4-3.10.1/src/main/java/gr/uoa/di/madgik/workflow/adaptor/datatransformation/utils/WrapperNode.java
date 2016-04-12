package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils;

import gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils.wrappers.FunctionalityWrapper;

import java.util.List;

/**
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class WrapperNode {

	public FunctionalityWrapper wrapper;
	public List<WrapperNode> children;
	
	public WrapperNode(FunctionalityWrapper wrapper, List<WrapperNode> children) {
		this.wrapper = wrapper;
		this.children = children;
	}
}
