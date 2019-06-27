package org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo;

import java.io.Serializable;
import java.util.Collection;

import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.OnlineResourceItem;


public class DigitalTransferOptionsItem implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5216339199750720197L;
	
	/**
     * Estimated size of a unit in the specified transfer format, expressed in megabytes.
     * The transfer size is &gt; 0.0.
     *
     */
	private Double transferSize;
	
    /**
     * Tiles, layers, geographic areas, <cite>etc.</cite>, in which data is available.
     *
     */
    private String unitsOfDistribution;
	

	 /**
     * Information about online sources from which the resource can be obtained.
     *
     */
	private Collection<OnlineResourceItem> onLine;
	
	public DigitalTransferOptionsItem(){
		
	}
	
	public DigitalTransferOptionsItem(Double transferSize, String unitsOfDistribution, Collection<OnlineResourceItem> onLine) {
		
		this.transferSize = transferSize;
		this.unitsOfDistribution = unitsOfDistribution;
		this.onLine = onLine;
	}

	

	public void setTransferSize(Double transferSize) {
		this.transferSize = transferSize;
	}

	/**
	 * 
	 *  @return Estimated size of a unit in the specified transfer format in megabytes, or {@code null}.
	 */
	public Double getTransferSize() {
		return transferSize;
	}

	/**
	 *  @return Online sources from which the resource can be obtained.
	 */
	public Collection<OnlineResourceItem> getOnLines() {
		return onLine;
	}

	public void setOnLine(Collection<OnlineResourceItem> onLine) {
		this.onLine = onLine;
	}

	/**
	 * 
	 * @return  Tiles, layers, geographic areas, etc
	 */
	public String getUnitsOfDistribution() {
		return unitsOfDistribution;
	}

	public void setUnitsOfDistribution(String unitsOfDistribution) {
		this.unitsOfDistribution = unitsOfDistribution;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DigitalTransferOptionsItem [transferSize=");
		builder.append(transferSize);
		builder.append(", unitsOfDistribution=");
		builder.append(unitsOfDistribution);
		builder.append(", onLine=");
		builder.append(onLine);
		builder.append("]");
		return builder.toString();
	}
	
	

}
