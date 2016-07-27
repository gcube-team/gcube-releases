package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

public class SubmissionParameters {

	private Operator op;

	private String description;
	private String title;
	public SubmissionParameters(Operator op, String description, String title) {
		super();
		this.op = op;
		this.description = description;
		this.title = title;
	}
	/**
	 * @return the op
	 */
	public Operator getOp() {
		return op;
	}
	/**
	 * @param op the op to set
	 */
	public void setOp(Operator op) {
		this.op = op;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmissionParameters [op=");
		builder.append(op);
		builder.append(", description=");
		builder.append(description);
		builder.append(", title=");
		builder.append(title);
		builder.append("]");
		return builder.toString();
	}
	
	public Map<String,String> getParametersMap(){
		HashMap<String,String> toReturn=new HashMap<String,String>();
		for(Parameter param:op.getOperatorParameters()){
			toReturn.put(param.getName(), param.getValue());
		}
		return toReturn;
	}

	
	
}
