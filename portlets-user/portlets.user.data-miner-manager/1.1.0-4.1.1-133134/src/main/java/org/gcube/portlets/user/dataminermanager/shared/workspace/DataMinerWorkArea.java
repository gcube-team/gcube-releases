package org.gcube.portlets.user.dataminermanager.shared.workspace;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerWorkArea implements Serializable {

	private static final long serialVersionUID = -7906477664944910362L;
	private ItemDescription dataMinerWorkAreaFolder;
	private InputDataSets inputDataSets;
	private OutputDataSets outputDataSets;
	private Computations computations;

	public DataMinerWorkArea() {
		super();
	}

	/**
	 * 
	 * @param dataMinerWorkAreaFolder
	 */
	public DataMinerWorkArea(ItemDescription dataMinerWorkAreaFolder) {
		super();
		this.dataMinerWorkAreaFolder = dataMinerWorkAreaFolder;

	}

	/**
	 * 
	 * @param dataMinerWorkAreaFolder
	 * @param inputDataSets
	 * @param outputDataSets
	 * @param computations
	 */
	public DataMinerWorkArea(ItemDescription dataMinerWorkAreaFolder,
			InputDataSets inputDataSets, OutputDataSets outputDataSets,
			Computations computations) {
		super();
		this.dataMinerWorkAreaFolder = dataMinerWorkAreaFolder;
		this.inputDataSets = inputDataSets;
		this.outputDataSets = outputDataSets;
		this.computations = computations;
	}

	public ItemDescription getDataMinerWorkAreaFolder() {
		return dataMinerWorkAreaFolder;
	}

	public void setDataMinerWorkAreaFolder(ItemDescription dataMinerWorkAreaFolder) {
		this.dataMinerWorkAreaFolder = dataMinerWorkAreaFolder;
	}

	public InputDataSets getInputDataSets() {
		return inputDataSets;
	}

	public void setInputDataSets(InputDataSets inputDataSets) {
		this.inputDataSets = inputDataSets;
	}

	public OutputDataSets getOutputDataSets() {
		return outputDataSets;
	}

	public void setOutputDataSets(OutputDataSets outputDataSets) {
		this.outputDataSets = outputDataSets;
	}

	public Computations getComputations() {
		return computations;
	}

	public void setComputations(Computations computations) {
		this.computations = computations;
	}

	@Override
	public String toString() {
		return "DataMinerWorkArea [dataMinerWorkAreaFolder="
				+ dataMinerWorkAreaFolder + ", inputDataSets=" + inputDataSets
				+ ", outputDataSets=" + outputDataSets + ", computations="
				+ computations + "]";
	}

	


}
