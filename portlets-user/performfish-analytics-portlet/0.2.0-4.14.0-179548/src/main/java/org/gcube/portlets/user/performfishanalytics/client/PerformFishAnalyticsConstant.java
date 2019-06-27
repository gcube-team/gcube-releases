/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.client;


/**
 * The Class PerformFishAnalyticsConstant.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         Jan 30, 2019
 */
public class PerformFishAnalyticsConstant {

	public static final String QUERY_STRING_FARMID_PARAM = "farmid";
	public static final String QUERY_STRING_BATCHTYPE_PARAM = "batchtype";
	public static final String QUERY_STRING_ANNUAL_PARAMETER = "annual";
	// PERFORM-FISH SERVICE KEYS
	public static final String BATCHES_TABLE_INTERNAL = "BatchesTable_internal";
	public static final String BATCHES_TABLE = "BatchesTable";
	// PARAMETERS TO CALL DATAMINER
	public static final String DATA_INPUTS = "DataInputs";
	public static final String DM_SCALEP_PARAM = "scaleP";
	public static final String DM_BATCHTYPE_PARAM = "batchtype";
	public static final String DM_CHARTTYPE_PARAM = "charttype";
	public static final String DM_YEARS_PARAM = "years";
	public static final String DM_FARMFILE_PARAM = "farmFile";
	public static final String DM_FOCUS_PARAM = "focus";
	public static final String DM_INPUT_KPI_PARAM = "inputKPI";
	public static final String DM_OUTPUT_KPI_PARAM = "outputKPI";
	public static final int CSV_BATCHES_TABLE_MINIMUM_SIZE = 4;

	public static final String PERFORM_FISH_AREA_PARAM = "area";
	public static final String PERFORM_FISH_QUARTER_PARAM = "quarter";
	public static final String PERFORM_FISH_PERIOD_PARAM = "period";

	public static final String PERFORM_FISH_FARMID_PARAM = "farmid";
	public static final String PERFORM_FISH_BATCH_TYPE_PARAM = "batch_type";
	public static final String PERFORM_FISH_SPECIES_ID_PARAM = "speciesid";



	public static final String DM_FOCUS_ID_ALL_ITEM_VALUE = "ALL";
	public static final String DM_FOCUS_ID_ALL_ITEM_TEXT = "ALL the above";



	/**
	 * The Enum POPULATION_LEVEL.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 * May 8, 2019
	 */
	public static enum POPULATION_LEVEL{BATCH, FARM};
	
	
	/**
	 * The Enum BATCH_LEVEL.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Feb 27, 2019
	 */
	public static enum BATCH_LEVEL{
		PRE_ONGROWING,
		PRE_ONGROWING_CLOSED_BATCHES,
		HATCHERY_INDIVIDUAL,
		HATCHERY_INDIVIDUAL_CLOSED_BATCHES,
		GROW_OUT_INDIVIDUAL,
		GROW_OUT_INDIVIDUAL_CLOSED_BATCHES,
		GROW_OUT_AGGREGATED,
		GROW_OUT_AGGREGATED_CLOSED_BATCHES
	}
	
	
	/**
	 * The Enum PFServiceToDMMappingTable.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 * May 8, 2019
	 */
	public static enum PFSERVICE_TO_DM_MAPPING_TABLE{

		LethalIncidentsTable("LethalIncidentsTable", "lethalIncidentsFile"),
		AntibioticsTable_internal("AntibioticsTable_internal", null),
		AntibioticsTable("AntibioticsTable", "antibioticFile"),
		AnnualTable_internal("AnnualTable_internal", null),
		AnnualTable("AnnualTable", "annualFile"),
		AntiparasiticTable("AntiparasiticTable", "antiparasiticFile"),
		AntiparasiticTable_internal("AntiparasiticTable_internal", null);
		

		/**
		 * Instantiates a new PF service to DM mapping table.
		 *
		 * @param performFishTable the perform fish table
		 * @param dataMinerTable the data miner table
		 */
		PFSERVICE_TO_DM_MAPPING_TABLE(String performFishTable, String dataMinerTable){
			this.performFishTable = performFishTable;
			this.dataMinerTable=dataMinerTable;
		}
		
		String performFishTable;
		String dataMinerTable;
		
		
		public String getPerformFishTable() {
			return performFishTable;
		}
		
		public String getDataMinerTable() {
			return dataMinerTable;
		}
		
	}


}
