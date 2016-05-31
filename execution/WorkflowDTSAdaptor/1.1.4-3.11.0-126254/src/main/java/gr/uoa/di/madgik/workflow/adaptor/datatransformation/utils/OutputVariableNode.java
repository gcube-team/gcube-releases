package gr.uoa.di.madgik.workflow.adaptor.datatransformation.utils;

import java.util.List;

public class OutputVariableNode {
	public String variableName;
	public List<OutputVariableNode> children;
	
	public OutputVariableNode(String variableName, List<OutputVariableNode> children) {
		this.variableName = variableName;
		this.children = children;
	}
}
