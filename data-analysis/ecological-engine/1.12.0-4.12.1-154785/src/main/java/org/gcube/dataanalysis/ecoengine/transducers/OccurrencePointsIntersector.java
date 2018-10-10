package org.gcube.dataanalysis.ecoengine.transducers;

import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;

public class OccurrencePointsIntersector extends OccurrencePointsMerger{

	public OccurrencePointsIntersector(){
		firstbest=true;
	}
	
	@Override
	public String getDescription() {
		return "A transducer algorithm that produces a table of species occurrence points that are contained in both the two starting tables where points equivalence is identified via user defined comparison thresholds. Works with up to 10000 points per table. Between two ocurrence sets, it keeps the elements of the Right Set that are similar to elements in the Left Set.";
	}
	
	@Override
	protected void prepareFinalTable() throws Exception{
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(leftTableName, finalTableName), dbconnection);
	}
	
	@Override
	protected void manageHighProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		objectstoinsert.add(rightOcc);
		/*
		if (
				((leftOcc.modifdate!=null)&&(rightOcc.modifdate!=null)&&leftOcc.modifdate.before(rightOcc.modifdate)) 
				|| 
				(leftOcc.modifdate==null)&&(rightOcc.modifdate!=null)
				)
			objectstoinsert.add(rightOcc);
		else if ((leftOcc.modifdate!=null)&&(rightOcc.modifdate!=null)&&leftOcc.modifdate.after(rightOcc.modifdate) 
			|| 
			(leftOcc.modifdate!=null)&&(rightOcc.modifdate==null))
			objectstoinsert.add(leftOcc);
		else
			objectstoinsert.add(leftOcc);
			*/
	}
	
	@Override
	protected void manageLowProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		
	}
	
}
