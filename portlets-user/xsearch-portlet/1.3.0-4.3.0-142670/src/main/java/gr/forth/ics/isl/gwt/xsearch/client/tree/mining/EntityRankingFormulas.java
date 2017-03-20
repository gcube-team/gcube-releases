/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.gwt.xsearch.client.tree.mining;

import gr.forth.ics.isl.gwt.xsearch.client.XSearch;

/**
 * A class that contains a set of different function that implement
 * different ranking methods.
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class EntityRankingFormulas {


	public static final int RANKING_FORMULA_AGGREGATIONS=1;
	
	public EntityRankingFormulas(){
		
	}	
	
        /**
         * A function that it takes a string with the list of docIds concated 
         * with comma and the ranking formula that we want to use.
         * @param listOfDocIds a string with the doc ids con-cut with comma
         * @param rankingFormula the ranking formula that we want to use
         * @return the rank value of the entity
         */
	public double rankEntity(String listOfDocIds, int rankingFormula){
		
		double rank = 0;
		switch (rankingFormula) {	
		
			case RANKING_FORMULA_AGGREGATIONS:
				rank = rankingFormulaAggregations(listOfDocIds);
				break;
				
			default:
				rank = rankingFormulaAggregations(listOfDocIds);
				break;				
		}
		
		return rank;
	}
	
	/** 
         * Ranking formula (1) of IRF paper
         * @param listOfDocIds a string with the doc ids con-cut with comma
         * @return the  rank value of the entity
         */
	public double rankingFormulaAggregations(String listOfDocIds){
		
		String[] tableOfDocIds = listOfDocIds.split(",");
		double rank = 0;
		int numOfTotalAnalyzedResults = XSearch.lastAnalyzedDocId;
		for(String docId : tableOfDocIds){
			rank += (numOfTotalAnalyzedResults - Integer.parseInt(docId));
		}	
		
		return rank;
	}
}
