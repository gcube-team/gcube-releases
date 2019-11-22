/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared.dataminer;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;


/**
 * The Class DataMinerResponse.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 29, 2019
 */
public class DataMinerResponse implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5553199389404083085L;
	private PerformFishResponse peformFishReponse;
	private List<OutputFile> listOutput;

	/**
	 * Instantiates a new data miner response.
	 */
	DataMinerResponse(){

	}


	/**
	 * Instantiates a new data miner response.
	 *
	 * @param peformFishReponse the peform fish reponse
	 * @param listOutput the list output
	 */
	public DataMinerResponse(
		PerformFishResponse peformFishReponse, List<OutputFile> listOutput) {

		this.peformFishReponse = peformFishReponse;
		this.listOutput = listOutput;
	}


	/**
	 * Gets the peform fish reponse.
	 *
	 * @return the peformFishReponse
	 */
	public PerformFishResponse getPeformFishReponse() {

		return peformFishReponse;
	}


	/**
	 * Gets the list output.
	 *
	 * @return the listOutput
	 */
	public List<OutputFile> getListOutput() {

		return listOutput;
	}


	/**
	 * Sets the peform fish reponse.
	 *
	 * @param peformFishReponse the peformFishReponse to set
	 */
	public void setPeformFishReponse(PerformFishResponse peformFishReponse) {

		this.peformFishReponse = peformFishReponse;
	}


	/**
	 * Sets the list output.
	 *
	 * @param listOutput the listOutput to set
	 */
	public void setListOutput(List<OutputFile> listOutput) {

		this.listOutput = listOutput;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DataMinerResponse [peformFishReponse=");
		builder.append(peformFishReponse);
		builder.append(", listOutput=");
		builder.append(listOutput);
		builder.append("]");
		return builder.toString();
	}

}
