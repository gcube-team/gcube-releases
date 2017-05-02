package org.gcube.accounting.analytics;

import java.util.List;
/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 *
 */
public class FiltersValue {

	
	protected List<Filter> filters;
	protected Double d;
	protected String orderingProperty;
	
	public FiltersValue(){}
	
	public FiltersValue(List<Filter> filters, Number n, String orderingProperty) {
		super();
		this.filters=filters;
		this.d = n.doubleValue();
		this.orderingProperty = orderingProperty;
	}

	public List<Filter> getFiltersValue() {
		return filters;
	}

	public void setFiltersValue(List<Filter> filters) {
		this.filters = filters;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public String getOrderingProperty() {
		return orderingProperty;
	}

	public void setOrderingProperty(String orderingProperty) {
		this.orderingProperty = orderingProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime
				* result
				+ ((orderingProperty == null) ? 0 : orderingProperty.hashCode());
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
		FiltersValue other = (FiltersValue) obj;
		if (d == null) {
			if (other.d != null)
				return false;
		} else if (!d.equals(other.d))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (orderingProperty == null) {
			if (other.orderingProperty != null)
				return false;
		} else if (!orderingProperty.equals(other.orderingProperty))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FiltersValue [filters=" + filters + ", d=" + d
				+ ", orderingProperty=" + orderingProperty + "]";
	}


	
}
