package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsNative;

public class AquamapsNativeNode extends AquamapsSuitableNode{
	
	public AquamapsNativeNode(){
		super();
	}
	
	public String getName() {
		return "AQUAMAPS_NATIVE";
	}

	public String getDescription() {
		return "Algorithm for Native Range by Aquamaps on a single node";
	}
	
	// writes the distribution model on the DB: input species vector + list of areas vectors to report
	public void singleStepPostprocess(Object species) {
			System.out.println("Aquamaps Algorithm Single Step PostProcess-> Analyzing Species distribution");
			// write info on DB
			Queue<String> rows = new ConcurrentLinkedQueue<String>();
			String speciesID = AquamapsSuitableFunctions.getMainInfoID(species);
			Map<String, Float> csquaresMap = operations.completeDistribution.get(speciesID);
			
			if (csquaresMap != null) {
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Getting csquare probabilites");
				// write only processed areas
				for (String singleCsquare : csquaresMap.keySet()) {
					String additionalInformation = operations.getAdditionalInformation(species, operations.processedAreas.get(singleCsquare));
					if (additionalInformation == null)
						additionalInformation = "";
					else if (additionalInformation.length() > 0)
						additionalInformation = "," + additionalInformation.trim();

					float prob = 0f;
					try {
						prob = csquaresMap.get(singleCsquare);
					} catch (Exception e) {
						System.out.println("Aquamaps Algorithm Single Step PostProcess ->Error in getting probability value at " + speciesID + " , " + singleCsquare);
					}
					if (prob > 0)
						rows.offer("'" + speciesID + "','" + singleCsquare + "','" + MathFunctions.roundDecimal(prob, 3) + "'" + additionalInformation);
				}
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Filtering probabilities. Size:"+rows.size());
				Queue<String> newrows = new AquamapsNative().filterProbabilitySet(rows);
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Filtered probabilities. Size:"+newrows.size());
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Writing rows on DB");
				List<String> toWrite = new ArrayList<String>();
				for (String row:newrows){
					toWrite.add(row);
//					System.out.println("Added row: "+row);
				}
				AquamapsSuitableFunctions.writeOnDB(toWrite, currentconfig.getParam("DistributionTable"), dbHibConnection);
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Rows written on DB");
			}
			else
				System.out.println("Aquamaps Algorithm Single Step PostProcess-> Probability distribution is void");

		}
	
}
