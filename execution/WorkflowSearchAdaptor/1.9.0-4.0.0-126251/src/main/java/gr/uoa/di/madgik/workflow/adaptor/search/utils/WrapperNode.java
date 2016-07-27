package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.FunctionalityWrapper;

import java.io.Serializable;
import java.util.List;

public class WrapperNode implements Serializable
{

	private static final long serialVersionUID = 1L;
	public FunctionalityWrapper wrapper;
	public List<WrapperNode> children;
	
	public WrapperNode(FunctionalityWrapper wrapper, List<WrapperNode> children) 
	{
		this.wrapper = wrapper;
		this.children = children;
	}
}
