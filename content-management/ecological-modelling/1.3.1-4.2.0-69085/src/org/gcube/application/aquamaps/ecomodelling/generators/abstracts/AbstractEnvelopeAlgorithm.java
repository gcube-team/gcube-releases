package org.gcube.application.aquamaps.ecomodelling.generators.abstracts;

import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.EnvelopeSet;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;
import org.hibernate.SessionFactory;

public abstract class AbstractEnvelopeAlgorithm {

	
	
	
	//gets the initialization value for a string object
	public static String getElement(Object[] featuresVector,int index){
		if (featuresVector[index] != null) return ""+featuresVector[index];
		else return null;
	}
	
	//gets the initialization value for a numeric object
	public static double getNumber(Object[] featuresVector,int index){
		
		double number = -9999;
		try{
			number = ((Number)featuresVector[index]).doubleValue();
		}catch(Exception e){}
		
		return number;
	}
	
	//returns the instructions to be insert into the database - To change with a more abstract object
	public abstract EnvelopeSet calculateEnvelopes(String species, SessionFactory vreConnection, String occurrencePointsTable, String HcafTable,Object[] singleSpeciesValues);
	
	public abstract EnvelopeSet calculateEnvelopes(String species, Object[] singleSpeciesValues, OccurrencePointSets occurrencePointsList);
	
	//gets the initialization value for a numeric object
	public abstract Object[] hspen2ObjectArray(Hspen hspen);
	
}
