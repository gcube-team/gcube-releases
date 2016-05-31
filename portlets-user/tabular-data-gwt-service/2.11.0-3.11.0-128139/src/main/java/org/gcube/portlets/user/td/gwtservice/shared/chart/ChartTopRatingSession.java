/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.chart;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ChartTopRatingSession implements Serializable {
	
	private static final long serialVersionUID = 4988917715929182427L;
	
	private TRId trId;
	private ColumnData column;
	private Integer sampleSize;
	private String valueOperation;
	
	public ChartTopRatingSession() {
		super();
	}
	
	/**
	 * 
	 * @param trId
	 * @param column
	 * @param sampleSize
	 * @param valueOperation
	 */
	public ChartTopRatingSession(TRId trId, ColumnData column,
			Integer sampleSize, String valueOperation) {
		super();
		this.trId = trId;
		this.column = column;
		this.sampleSize = sampleSize;
		this.valueOperation = valueOperation;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public Integer getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(Integer sampleSize) {
		this.sampleSize = sampleSize;
	}

	public String getValueOperation() {
		return valueOperation;
	}

	public void setValueOperation(String valueOperation) {
		this.valueOperation = valueOperation;
	}

	@Override
	public String toString() {
		return "ChartTopRatingSession [trId=" + trId + ", column=" + column
				+ ", sampleSize=" + sampleSize + ", valueOperation="
				+ valueOperation + "]";
	}

	
	
	
	
	
	
	
}
