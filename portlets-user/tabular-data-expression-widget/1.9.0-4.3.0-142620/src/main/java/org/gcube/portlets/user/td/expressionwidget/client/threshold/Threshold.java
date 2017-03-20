package org.gcube.portlets.user.td.expressionwidget.client.threshold;

import java.io.Serializable;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class Threshold implements Serializable {
	
	private static final long serialVersionUID = 3713817747863556150L;
	
	private Integer id;
	private Float value;
	private String label;
	
	
	public Threshold(Integer id,Integer value,String label){
		this.id=id;
		this.value=new Float(value);
		this.label=label;
	}

	public Threshold(Integer id,Float value, String label){
		this.id=id;
		this.value=value;
		this.label=label;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getValue() {
		return value;
	}

	public Integer getIntegerValue() {
		return value.intValue();
	}
	
	public void setValue(Float value) {
		this.value = value;
	}
	
	
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Threshold [id=" + id + ", value=" + value + ", label=" + label
				+ "]";
	}
	
}
