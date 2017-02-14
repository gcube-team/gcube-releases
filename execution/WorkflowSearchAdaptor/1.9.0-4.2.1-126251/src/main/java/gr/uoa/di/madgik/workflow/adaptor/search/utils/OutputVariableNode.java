package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import java.io.Serializable;
import java.util.List;

public class OutputVariableNode  implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	public String variableName;
	public List<OutputVariableNode> children;
	
	public OutputVariableNode(String variableName, List<OutputVariableNode> children) {
		this.variableName = variableName;
		this.children = children;
	}
}
