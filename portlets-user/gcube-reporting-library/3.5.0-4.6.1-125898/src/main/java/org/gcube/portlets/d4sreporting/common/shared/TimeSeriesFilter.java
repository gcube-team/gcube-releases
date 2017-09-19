package org.gcube.portlets.d4sreporting.common.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Massimiilano Assante
 *
 */
public class TimeSeriesFilter implements Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 2387560401349355389L;
		private int from;
		private int to;
		private List<Integer> colsNumberToShow;
		
		/**
		 * k: the column to apply the filter on the values in the List of Strings with operator equal to
		 * v: the values to compare
		 */
		private HashMap<String, List<String>> equalsToColumnFilters;
		
		
		/**
		 * k: the column to apply the filter on the values in the List of Strings with operator equal to
		 * v: the values to compare
		 */
		private HashMap<String, List<String>> differentFromColumnFilters;

		/**
		 * 
		 */
		public TimeSeriesFilter() {
			super();
		}

		/**
		 * 
		 * @param colsNumberToShow .
		 * @param differentFromColumnFilters .
		 * @param equalsToColumnFilters .
		 * @param from .
		 * @param to .
		 */
		public TimeSeriesFilter(List<Integer> colsNumberToShow,
				HashMap<String, List<String>> differentFromColumnFilters,
				HashMap<String, List<String>> equalsToColumnFilters, int from,
				int to) {
			super();
			this.colsNumberToShow = colsNumberToShow;
			this.differentFromColumnFilters = differentFromColumnFilters;
			this.equalsToColumnFilters = equalsToColumnFilters;
			this.from = from;
			this.to = to;
		}

		/**
		 * 
		 * @return .
		 */
		public int getFrom() {
			return from;
		}

		/**
		 * 
		 * @param from .
		 */
		public void setFrom(int from) {
			this.from = from;
		}

		/**
		 * 
		 * @return .
		 */
		public int getTo() {
			return to;
		}

		/**
		 * 
		 * @param to .
		 */
		public void setTo(int to) {
			this.to = to;
		}

		/**
		 * 
		 * @return .
		 */
		public List<Integer> getColsNumberToShow() {
			return colsNumberToShow;
		}

		/**
		 * 
		 * @param colsNumberToShow .
		 */
		public void setColsNumberToShow(List<Integer> colsNumberToShow) {
			this.colsNumberToShow = colsNumberToShow;
		}

		/**
		 * 
		 * @return .
		 */
		public HashMap<String, List<String>> getEqualsToColumnFilters() {
			return equalsToColumnFilters;
		}

		/**
		 * 
		 * @param equalsToColumnFilters .
		 */
		public void setEqualsToColumnFilters(
				HashMap<String, List<String>> equalsToColumnFilters) {
			this.equalsToColumnFilters = equalsToColumnFilters;
		}

		/**
		 * 
		 * @return .
		 */
		public HashMap<String, List<String>> getDifferentFromColumnFilters() {
			return differentFromColumnFilters;
		}

		/**
		 * 
		 * @param differentFromColumnFilters .
		 */
		public void setDifferentFromColumnFilters(
				HashMap<String, List<String>> differentFromColumnFilters) {
			this.differentFromColumnFilters = differentFromColumnFilters;
		}
		
}
