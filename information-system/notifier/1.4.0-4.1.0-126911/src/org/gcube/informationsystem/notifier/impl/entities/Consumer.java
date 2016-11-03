package org.gcube.informationsystem.notifier.impl.entities;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.informationsystem.notifier.util.EPR;

public class Consumer extends EPR{

			
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2762225240390235557L;
	
	private String precondition= null;
	private String selection= null;
	
	public Consumer(EndpointReferenceType epr){
		super(epr);
	}

	public String getPrecondition() {
		return precondition;
	}

	public void setPrecondition(String precondition) {
		this.precondition = precondition;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	public boolean equals(Object obj){
		if (!(obj instanceof Consumer))
			return false;
		Consumer tmpCons= (Consumer) obj;
		
		if (this.precondition==null && tmpCons.getPrecondition()==null)
			return super.equals(obj);
		else if(this.precondition==null && tmpCons.getPrecondition()!=null)
			return false;
		else if(this.precondition!=null && tmpCons.getPrecondition()==null) {
			return false;
		}else{
			return this.precondition.compareTo(tmpCons.getPrecondition())==0 && super.equals(obj);
		}
	}
	
}
