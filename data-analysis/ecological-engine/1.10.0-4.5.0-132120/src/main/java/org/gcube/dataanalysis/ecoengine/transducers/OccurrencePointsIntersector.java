package org.gcube.dataanalysis.ecoengine.transducers;

import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
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
	
	
	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setParam(longitudeColumn, "decimallongitude");
		config.setParam(latitudeColumn, "decimallatitude");
		config.setParam(recordedByColumn, "recordedby");
		config.setParam(scientificNameColumn, "scientificname");
		config.setParam(eventDateColumn, "eventdate");
		config.setParam(lastModificationColumn, "modified");
		config.setParam(rightTableNameF, "whitesharkoccurrences2");
		config.setParam(leftTableNameF, "whitesharkoccurrences1");
		config.setParam(finalTableNameF, "whitesharkoccurrencesintersected");
		config.setParam(spatialTolerance, "0.5");
		config.setParam(confidence, "0.8");

		OccurrencePointsIntersector occm = new OccurrencePointsIntersector();
		occm.setConfiguration(config);
		occm.init();
		occm.compute();
	}
	
}
