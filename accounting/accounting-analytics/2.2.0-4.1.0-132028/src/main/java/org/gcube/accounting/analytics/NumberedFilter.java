/**
 * 
 */
package org.gcube.accounting.analytics;

import java.util.Calendar;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class NumberedFilter extends Filter {

	protected Double d;
	protected String orderingProperty;
	
	public NumberedFilter(String key, String value, Number n, String orderingProperty) {
		super(key, value);
		this.d = n.doubleValue();
		this.orderingProperty = orderingProperty;
	}
	
	public NumberedFilter(Filter filter, Number n, String orderingProperty) {
		this(filter.key, filter.value, n, orderingProperty);
	}
	
	public NumberedFilter(Filter filter, Map<Calendar, Info> timeSeries, String orderingProperty) throws Exception {
		super(filter.key, filter.value);
		
		this.d = new Double(0);
		this.orderingProperty = orderingProperty;
		
		for(Info info : timeSeries.values()){
			JSONObject value = info.getValue();
			if(this.d == null){
				this.d = value.getDouble(orderingProperty);
			}else{
				this.d = this.d + value.getDouble(orderingProperty);
			}
		}
		
	}
	
	/**
	 * @return the d
	 */
	public Double getDouble() {
		return d;
	}

	/**
	 * @param d the d to set
	 */
	public void setDouble(Double d) {
		this.d = d;
	}

	/**
	 * @return the orderingProperty
	 */
	public String getOrderingProperty() {
		return orderingProperty;
	}

	/**
	 * @param orderingProperty the orderingProperty to set
	 */
	public void setOrderingProperty(String orderingProperty) {
		this.orderingProperty = orderingProperty;
	}

	public int compareTo(NumberedFilter numberedFilter) {
		int compareResult = this.d.compareTo(numberedFilter.d);
		return compareResult;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof NumberedFilter)) {
			return false;
		}
		NumberedFilter other = (NumberedFilter) obj;
		if (d == null) {
			if (other.d != null) {
				return false;
			}
		} else if (!d.equals(other.d)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return String.format("%s, %s : %d}", 
				super.toString().replace(" }", ""), 
				orderingProperty, d.longValue());
	}
	
}
