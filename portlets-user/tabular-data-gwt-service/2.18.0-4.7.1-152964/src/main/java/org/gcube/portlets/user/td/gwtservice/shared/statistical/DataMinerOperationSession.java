/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.statistical;

import java.io.Serializable;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class DataMinerOperationSession implements Serializable {

	private static final long serialVersionUID = -8968614490778086448L;
	private TRId trId;
	private Operator operator;

	public DataMinerOperationSession() {
		super();
	}

	public DataMinerOperationSession(TRId trId, Operator operator) {
		super();
		this.trId = trId;
		this.operator = operator;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

}
