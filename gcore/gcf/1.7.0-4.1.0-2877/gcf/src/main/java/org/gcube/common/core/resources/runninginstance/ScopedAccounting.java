package org.gcube.common.core.resources.runninginstance;

import java.util.HashMap;

import org.gcube.common.core.scope.GCUBEScope;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class ScopedAccounting {
		
	private GCUBEScope scope;
	
	private Long totalINCalls;
	
	private String topCallerGHN;
	
	private Long topCallerGHNtotalCalls;
	
	private Double topCallerGHNavgHourlyCalls;
	
	private Double topCallerGHNavgDailyCalls;
	
	private HashMap<Long,Double> averageCallsMap;
	
	private HashMap<Long,Double> averageTimeMap;
	
	
	/**
	 * gets the scope
	 * @return the scope
	 */
	public GCUBEScope getScope() {
		return scope;
	}

	/**
	 * Sets the scopes
	 * @param scope the scope
	 */
	public void setScope(GCUBEScope scope) {
		this.scope = scope;
	}

	/**
	 * Gets the value of the topCallerGHN
	 * @return the TopCallerGHN
	 */
	public String getTopCallerGHN() {
		return topCallerGHN;
	}
	
	/**
	 * Sets the TopCallerGHN value
	 * @param topCallerGHN value
	 */
	public void setTopCallerGHN(String topCallerGHN) {
		this.topCallerGHN = topCallerGHN;
	}
	
	/**
	 * Get the totalCalls attribute related to the TopCallerGHN
	 * @return the totalCalls attribute related to the TopCallerGHN
	 */
	public Long getTopCallerGHNtotalCalls() {
		return topCallerGHNtotalCalls;
	}
	
	/**
	 * Sets the totalCalls attribute related to the TopCallerGHN
	 * @param topCallerGHNtotalCalls
	 */
	public void setTopCallerGHNtotalCalls(Long topCallerGHNtotalCalls) {
		this.topCallerGHNtotalCalls = topCallerGHNtotalCalls;
	}
	
	/**
	 * Gets the avg calls x hour related to the  TopCallerGHN
	 * @return the avg calls x hour related to the  TopCallerGHN
	 */
	public Double getTopCallerGHNavgHourlyCalls() {
		return topCallerGHNavgHourlyCalls;
	}
	/**
	 * Sets the avg calls x day related to the  TopCallerGHN
	 * @param the avg calls x hour related to the  TopCallerGHN
	 */
	public void setTopCallerGHNavgHourlyCalls(Double topCallerGHNavgHourlyCalls) {
		this.topCallerGHNavgHourlyCalls = topCallerGHNavgHourlyCalls;
	}
	/**
	 * Gets the avg calls x day related to the  TopCallerGHN
	 * @return the avg calls x day related to the  TopCallerGHN
	 */
	public Double getTopCallerGHNavgDailyCalls() {
		return topCallerGHNavgDailyCalls;
	}
	/**
	 * Sets the avg calls x day related to the  TopCallerGHN
	 * @param the avg calls x day related to the  TopCallerGHN
	 */
	public void setTopCallerGHNavgDailyCalls(Double topCallerGHNavgDailyCalls) {
		this.topCallerGHNavgDailyCalls = topCallerGHNavgDailyCalls;
	}
	/**
	 * Gets the Total Number of Incoming Call
	 * @return the total  number of Incoming Calls
	 */
	public Long getTotalINCalls() {
		return totalINCalls;
	}
	/**
	 * Sets the Total Number of Incoming Call
	 * @param totalINCalls the totalInCalls
	 */
	public void setTotalINCalls(Long totalINCalls) {
		this.totalINCalls = totalINCalls;
	}
	
	/**
	 * Get the value of the AverageCallsMap property
	 * @return HashMap<Long, Double>
	 */
	public HashMap<Long, Double> getAverageCallsMap() {
		 if (averageCallsMap == null) {
			 averageCallsMap = new HashMap<Long,Double>();
	        }
	        return this.averageCallsMap;
		
	}
	
	/**
	 * Get the value of the AverageTimeMap property
	 * @return HashMap<Long, Double> 
	 */
	public HashMap<Long, Double> getAverageTimeMap() {
		 if (averageTimeMap == null) {
			 averageTimeMap = new HashMap<Long,Double>();
	        }
	        return this.averageTimeMap;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final ScopedAccounting other = (ScopedAccounting) obj;
		
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (! scope.equals(other.scope))
			return false;
		
		if (totalINCalls == null) {
			if (other.totalINCalls != null)
				return false;
		} else if (! totalINCalls.equals(other.totalINCalls))
			return false;
		
		if (topCallerGHN == null) {
			if (other.topCallerGHN != null)
				return false;
		} else if (! topCallerGHN.equals(other.topCallerGHN))
			return false;

		if (topCallerGHNtotalCalls == null) {
			if (other.topCallerGHNtotalCalls != null)
				return false;
		} else if (! topCallerGHNtotalCalls.equals(other.topCallerGHNtotalCalls))
			return false;

		if (topCallerGHNavgHourlyCalls == null) {
			if (other.topCallerGHNavgHourlyCalls != null)
				return false;
		} else if (! topCallerGHNavgHourlyCalls.equals(other.topCallerGHNavgHourlyCalls))
			return false;

		if (topCallerGHNavgDailyCalls == null) {
			if (other.topCallerGHNavgDailyCalls != null)
				return false;
		} else if (! topCallerGHNavgDailyCalls.equals(other.topCallerGHNavgDailyCalls))
			return false;
		
		if (averageCallsMap == null) {
			if (other.averageCallsMap != null)
				return false;
		} else if (! averageCallsMap.equals(other.averageCallsMap))
			return false;
		
		if (averageTimeMap == null) {
			if (other.averageTimeMap != null)
				return false;
		} else if (! averageTimeMap.equals(other.averageTimeMap))
			return false;
		return true;
	}
	
		
}
