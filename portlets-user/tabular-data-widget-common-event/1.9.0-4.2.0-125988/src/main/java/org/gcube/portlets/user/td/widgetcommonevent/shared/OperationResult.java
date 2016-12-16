package org.gcube.portlets.user.td.widgetcommonevent.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OperationResult implements Serializable {
	
	private static final long serialVersionUID = 7612440231512398232L;

	private TRId trId;
	private ArrayList<TRId> collateralTRIds;
	
	public OperationResult(){
		
	}
	
	/**
	 * 
	 * @param trId
	 * @param collateralTRIds
	 */
	public OperationResult(TRId trId, ArrayList<TRId> collateralTRIds) {
		super();
		this.trId = trId;
		this.collateralTRIds = collateralTRIds;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<TRId> getCollateralTRIds() {
		return collateralTRIds;
	}

	public void setCollateralTRIds(ArrayList<TRId> collateralTRIds) {
		this.collateralTRIds = collateralTRIds;
	}

	@Override
	public String toString() {
		return "OperationResult [trId=" + trId + ", collateralTRIds="
				+ collateralTRIds + "]";
	}

		
	
	
}
