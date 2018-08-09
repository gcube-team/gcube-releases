package org.gcube.portlets.user.td.expressionwidget.shared.model.leaf;

import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.shared.expression.C_MultivaluedExpression;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class C_ConstantList extends C_Leaf implements C_MultivaluedExpression {
	private static final long serialVersionUID = 222662008523199480L;

	protected String id = "ConstantList";
	protected List<TD_Value> arguments;

	public C_ConstantList(){}
	
	public C_ConstantList(List<TD_Value> arguments) {
		this.arguments = arguments;
		String cList=new String();
		for(TD_Value tdValue:arguments){
			if(tdValue!=null){
				if(cList.isEmpty()){
					cList=cList.concat(tdValue.getReadableExpression());
				} else {
					cList=cList.concat(",").concat(tdValue.getReadableExpression());
				}	

			}
		}
		this.readableExpression = "CostantList("+cList+")";
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIdMulti() {
		return id;
	}


	public List<TD_Value> getArguments() {
		return arguments;
	}

	public void setArguments(List<TD_Value> arguments) {
		this.arguments = arguments;
	}

	@Override
	public String getReadableMultivaluedString() {
		return readableExpression;
	}
	
	@Override
	public String toString() {
		return "ConstantList [id=" + id + ", arguments=" + arguments + "]";
	}

	

	
	
	

}
