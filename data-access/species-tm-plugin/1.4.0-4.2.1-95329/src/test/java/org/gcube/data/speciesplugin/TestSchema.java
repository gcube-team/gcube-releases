/**
 * 
 */
package org.gcube.data.speciesplugin;

import org.gcube.data.speciesplugin.requests.SpeciesRequest;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestSchema {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sampleDataSchema = Utils.toSchema(SpeciesRequest.class);
		System.out.println(sampleDataSchema);
	}

}
