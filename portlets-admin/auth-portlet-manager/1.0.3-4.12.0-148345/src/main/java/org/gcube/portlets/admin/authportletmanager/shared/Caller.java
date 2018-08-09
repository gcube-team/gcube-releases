package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;


/**
 * Caller class for 
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class Caller implements Serializable, Comparable<Caller> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8450499385104913365L;
	

	

	//private String typeCaller;
	
	
	public enum TypeCaller {
		  user,
		  role,
		  service;
		  
	}
	
	
	private String callerName;
	private TypeCaller typeCaller;
	
	public Caller() {
		super();
	}

	public Caller(TypeCaller typeCaller, String callerName) {
		super();
		this.typeCaller = typeCaller;
		this.callerName=callerName;
	}

	public TypeCaller getTypecaller() {
		return typeCaller;
	}
	
	public void setTypecaller(TypeCaller typeCaller) {
		this.typeCaller = typeCaller;
	}

	public String getCallerName() {
		return callerName;
	}
	
	

	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}

	
	@Override
	public int compareTo(Caller o) {
		// TODO Auto-generated method stub
		return -this.getCallerName().compareTo(o.getCallerName());
	
	}
	
	public int compare(Caller o1, Caller o2) {
		// TODO Auto-generated method stub
		return -o1.getCallerName().compareTo(o2.getCallerName());
	}

	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((callerName == null) ? 0 : callerName.hashCode());
		result = prime * result
				+ ((typeCaller == null) ? 0 : typeCaller.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Caller other = (Caller) obj;
		if (callerName == null) {
			if (other.callerName != null)
				return false;
		} else if (!callerName.equals(other.callerName))
			return false;
		if (typeCaller == null) {
			if (other.typeCaller != null)
				return false;
		} else if (!typeCaller.equals(other.typeCaller))
			return false;
		return true;
	}

    
	

	
}

