package org.gcube.data.analysis.dataminermanagercl.shared.data.computations;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationItemPropertiesValue implements Serializable,
		Comparable<ComputationItemPropertiesValue> {

	private static final long serialVersionUID = 8917614711815918760L;
	private Integer order;
	private String key;
	private String value;

	public ComputationItemPropertiesValue() {
		super();
	}

	public ComputationItemPropertiesValue(Integer order, String key,
			String value) {
		super();
		this.order = order;
		this.key = key;
		this.value = value;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(ComputationItemPropertiesValue o) {
		return order.compareTo(o.getOrder());
	}

	@Override
	public String toString() {
		return "ComputationItemPropertiesValue [order=" + order + ", key="
				+ key + ", value=" + value + "]";
	}

}
