package org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo;

import java.io.Serializable;
import java.util.Collection;

public class DistributionInfoItem implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2396607369858036087L;

	/**
     * Provides information about the distributor.
     *
     */
	private Collection<DistributorItem> distributors;
	
	 /**
     * Provides information about technical means and media by which a resource is obtained
     * from the distributor.
     *
     */
    private Collection<DigitalTransferOptionsItem> transferOptions;
	
	public DistributionInfoItem(){
		
	}
	
	

	public DistributionInfoItem(Collection<DistributorItem> distributors,Collection<DigitalTransferOptionsItem> transferOptions) {
		this.distributors = distributors;
		this.transferOptions = transferOptions;
	}


	/**
	 * 
	 * @return Information about the distributor.
	 */
	public Collection<DistributorItem> getDistributors() {
		return distributors;
	}

	public void setDistributors(Collection<DistributorItem> distributors) {
		this.distributors = distributors;
	}

	/**
	 * @return Technical means and media by which a resource is obtained from the distributor.
	 */
	public Collection<DigitalTransferOptionsItem> getTransferOptions() {
		return transferOptions;
	}

	public void setTransferOptions(Collection<DigitalTransferOptionsItem> transferOptions) {
		this.transferOptions = transferOptions;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DistributionInfoItem [distributors=");
		builder.append(distributors);
		builder.append(", transferOptions=");
		builder.append(transferOptions);
		builder.append("]");
		return builder.toString();
	}

}
