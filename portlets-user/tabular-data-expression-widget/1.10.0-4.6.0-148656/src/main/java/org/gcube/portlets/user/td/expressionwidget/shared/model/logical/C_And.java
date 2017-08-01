package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_And extends C_Expression {
	private static final long serialVersionUID = -4462616033767138280L;
	protected List<C_Expression> arguments;
	protected String id = "And";
	
	public C_And(){
		
	}
	
	public C_And(C_Expression... arguments) {
		this.arguments = Arrays.asList(arguments);
		String cList=new String();
		for(C_Expression arg:arguments){
			if(arg!=null){
				if(cList.isEmpty()){
					cList=cList.concat(arg.getReadableExpression());
				} else {
					cList=cList.concat(",").concat(arg.getReadableExpression());
				}	

			}
		}
		this.readableExpression = "And("+cList+")";
		
	}

	public C_And(List<C_Expression> arguments) {
		this.arguments = arguments;
		String cList=new String();
		for(C_Expression arg:arguments){
			if(arg!=null){
				if(cList.isEmpty()){
					cList=cList.concat(arg.getReadableExpression());
				} else {
					cList=cList.concat(",").concat(arg.getReadableExpression());
				}	

			}
		}
		this.readableExpression = "And("+cList+")";
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.AND;
	}
	
	public String getReturnedDataType() {		
		return "Boolean";
	}

	public List<C_Expression> getArguments() {
		return arguments;
	}

	public void setArguments(List<C_Expression> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public String getId() {
		return id;
	}


	@Override
	public String toString() {
		return "And [arguments=" + arguments + ", id=" + id + "]";
	}
	
	
}
