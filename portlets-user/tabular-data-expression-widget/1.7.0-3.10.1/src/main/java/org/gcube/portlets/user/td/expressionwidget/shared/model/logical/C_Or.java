package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class C_Or extends C_Expression {
	private static final long serialVersionUID = 1855717717443945397L;
	protected String id = "Or";
	protected List<C_Expression> arguments;

	public C_Or(){
		
	}
	
	public C_Or(C_Expression... arguments) {
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
		this.readableExpression = "Or("+cList+")";
		
	}

	public C_Or(List<C_Expression> arguments) {
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
		this.readableExpression = "Or("+cList+")";
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.OR;
	}
	
	public String getReturnedDataType() {		
		return "Boolean";
	}

	@Override
	public String getId() {
		return id;
	}


	public List<C_Expression> getArguments() {
		return arguments;
	}

	public void setArguments(List<C_Expression> arguments) {
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		return "Or [id=" + id + ", arguments=" + arguments + "]";
	}
	
	
}
