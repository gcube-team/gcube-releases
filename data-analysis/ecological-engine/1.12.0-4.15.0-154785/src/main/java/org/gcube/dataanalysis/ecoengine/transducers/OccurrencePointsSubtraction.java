package org.gcube.dataanalysis.ecoengine.transducers;

public class OccurrencePointsSubtraction extends OccurrencePointsMerger{

	public OccurrencePointsSubtraction(){
		firstbest=false;
	}
	
	@Override
	public String getDescription() {
		return "A transducer algorithm that produces a table resulting from the difference between two occurrence points tables where points equivalence is identified via user defined comparison thresholds. Works with up to 10000 points per table. Between two Ocurrence Sets, keeps the elements of the Left Set that are not similar to any element in the Right Set.";
	}
	
	protected void manageHighProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		//cancel the left Occ record matching the right
		objectstodelete.add(leftOcc);	
	}
	
	@Override
	protected void manageLowProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		
	}
	
	
}
