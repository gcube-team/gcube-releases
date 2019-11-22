/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.server.persistence.GenericPersistenceDaoBuilder;
import org.gcube.portlets.user.performfishanalytics.server.util.csv.CSVReader;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.AreaData;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.KPIBatchTypeData;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.PeriodData;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.QuarterData;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.SpeciesData;
import org.gcube.portlets.user.performfishanalytics.server.util.database.data.YearData;
import org.gcube.portlets.user.performfishanalytics.shared.Area;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.Period;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Quarter;
import org.gcube.portlets.user.performfishanalytics.shared.Species;
import org.gcube.portlets.user.performfishanalytics.shared.Year;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class FillDatabasePerBatchType.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 27, 2019
 */
public class FillDatabasePerBatchType {

	/**
	 *
	 */
	public static final String RELATIVE_PATH_TO_BATCH_KPI = "BATCH/KPI";
	public static final String RELATIVE_PATH_TO_FARM_KPI = "FARM/KPI";
	protected static Logger log = LoggerFactory.getLogger(FillDatabasePerBatchType.class);

	/**
	 * Prints the database data.
	 *
	 * @param entityManagerFactory the entity manager factory
	 * @throws Exception the exception
	 */
	public static void printDatabaseData(EntityManagerFactory entityManagerFactory) throws Exception {

		GenericPersistenceDaoBuilder<Population> builderPopulation = new GenericPersistenceDaoBuilder<Population>(entityManagerFactory, Population.class.getSimpleName());

		for(Population population: builderPopulation.getPersistenceEntity().getList()){
			log.debug("\n\n#### Population: "+population.getName());
			for (PopulationType populationType : population.getListPopulationType()) {
				log.debug("\t{} Type: {}",populationType.getName(),populationType.getType());

				log.debug("\t**has Species: "+populationType.getListSpecies().size());
				for (Species species : populationType.getListSpecies()) {
					log.debug("\t\t"+species.getName());
				}

				log.debug("\t**has Quarter: "+populationType.getListQuarter().size());
				for (Quarter quarter : populationType.getListQuarter()) {
					log.debug("\t\t"+quarter.getName());
				}

				log.debug("\t**has Area: "+populationType.getListArea().size());
				for (Area area : populationType.getListArea()) {
					log.debug("\t\t"+area.getName());
				}

				log.debug("\t**has Period: "+populationType.getListPeriod().size());
				for (Period period : populationType.getListPeriod()) {
					log.debug("\t\t"+period.getName());
				}
				
				log.debug("\t**has Years: "+populationType.getListYears().size());
				for (Year year : populationType.getListYears()) {
					log.debug("\t\t"+year.getValue());
				}
				
				log.debug("\t**has KPIs: "+populationType.getListKPI().size());
				for (KPI kpi : populationType.getListKPI()) {
					printKPIs(kpi);
				}
			}
		}
	}

	/**
	 * Prints the kp is.
	 *
	 * @param kpi the kpi
	 * @param level the level
	 */
	public static void printKPIs(KPI kpi, int level){
		if(kpi==null)
			return;

		log.debug(String.format("\t%"+level*5+"d|KPI: %s, %s",level, kpi.getCode(), kpi.getName()));
		//log.debug("LIST KPI ARE: "+listKPI);
		level++;
		if(kpi.getListKPI()==null){
			//log.debug("[LEAF: "+kpi.getName()+"]");
			//I'm a LEAF
			return;
		}
		for (KPI kpiChild : kpi.getListKPI()) {
			printKPIs(kpiChild, kpiChild.getDeepIndex());
		}
	}


	/**
	 * Prints the kp is.
	 *
	 * @param kpi the kpi
	 */
	public static void printKPIs(KPI kpi){
		if(kpi==null || kpi.getListKPI()==null)
			return;

		log.debug(String.format("\t%"+kpi.getDeepIndex()*5+"d|KPI: %s, %s",kpi.getDeepIndex(), kpi.getCode(), kpi.getName()));
		for (KPI kpiChild : kpi.getListKPI()) {
			printKPIs(kpiChild, kpiChild.getDeepIndex());
		}
	}


	/**
	 * Fill database.
	 *
	 * @param entityManagerFactory the entity manager factory
	 * @param context the context
	 * @throws Exception the exception
	 */
	public static void fillDatabase(EntityManagerFactory entityManagerFactory, ServletContext context) throws Exception{

		//INSERT LEVEL OF KIND 'BATCH'
		Population batchPopulation = new Population(java.util.UUID.randomUUID().toString(), "BATCH", "BATCH", "BATCH Description", null);

		List<PopulationType> listBatchLevels = loadKPIForBatchLevelFromResource(context, RELATIVE_PATH_TO_BATCH_KPI);

		//FILLING BATCH LEVEL/POPULATIONS
		for (PopulationType batchType : listBatchLevels) {

			batchType.setPopulation(batchPopulation);

			if(batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.PRE_ONGROWING.name()) || batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.PRE_ONGROWING_CLOSED_BATCHES.name())){

				//Species
				batchType.setListSpecies(SpeciesData.getListSpecies(batchType));
				//Areas
				batchType.setListArea(AreaData.getListArea(batchType));
				//Periods
				batchType.setListPeriod(PeriodData.getListPeriods(batchType));

			}else if(batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.HATCHERY_INDIVIDUAL.name()) || batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.HATCHERY_INDIVIDUAL_CLOSED_BATCHES.name())){

				//Species
				batchType.setListSpecies(SpeciesData.getListSpecies(batchType));
				//Periods
				batchType.setListPeriod(PeriodData.getListPeriods(batchType));

			}else if(batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_INDIVIDUAL.name()) || batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_INDIVIDUAL_CLOSED_BATCHES.name())){

				//Species
				batchType.setListSpecies(SpeciesData.getListSpecies(batchType));
				//Quarters
				batchType.setListQuarter(QuarterData.getListQuarter(batchType));
				//Areas
				batchType.setListArea(AreaData.getListArea(batchType));

			}else if(batchType.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_AGGREGATED_CLOSED_BATCHES.name())){

				//Species
				batchType.setListSpecies(SpeciesData.getListSpecies(batchType));
				//Quarters
				batchType.setListQuarter(QuarterData.getListQuarter(batchType));
				//Areas
				batchType.setListArea(AreaData.getListArea(batchType));
			
			}

		}

		//INSERTING BATCH LEVEL/POPULATIONS
		GenericPersistenceDaoBuilder<Population> builderPopulation = new GenericPersistenceDaoBuilder<Population>(entityManagerFactory, Population.class.getSimpleName());
		batchPopulation.setListPopulationType(new ArrayList<PopulationType>(listBatchLevels));
		builderPopulation.getPersistenceEntity().insert(batchPopulation);

		
		//INSERT LEVEL OF KIND 'FARM'
		Population farmPopulation = new Population(java.util.UUID.randomUUID().toString(), "FARM", "FARM", "FARM Description", null);
		List<PopulationType> listFarmLevels = loadKPIForBatchLevelFromResource(context, RELATIVE_PATH_TO_FARM_KPI);
		//FILLING BATCH LEVEL/POPULATIONS
		
		log.info("FARM batch types are: "+listBatchLevels);
		for (PopulationType batchTypeFarm : listFarmLevels) {
			
			batchTypeFarm.setPopulation(farmPopulation);
			
			if(batchTypeFarm.getName().equals(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_AGGREGATED_CLOSED_BATCHES.name())){
				//Years
				batchTypeFarm.setListYears(YearData.getListYears(batchTypeFarm));
			}
		}
		
		//INSERTING BATCH LEVEL/POPULATIONS
		farmPopulation.setListPopulationType(new ArrayList<PopulationType>(listFarmLevels));
		builderPopulation.getPersistenceEntity().insert(farmPopulation);
		
	}

	/**
	 * Builds the populations.
	 *
	 * @return the list
	 */
	public static List<Population> buildPopulations(){
		Population popFarm = new Population(java.util.UUID.randomUUID().toString(), "FARM", "FARM", "FARM Description", null);
		Population popBatch = new Population(java.util.UUID.randomUUID().toString(), "BATCH", "BATCH", "BATCH Description", null);
		return Arrays.asList(popFarm, popBatch);
	}


	/**
	 * Builds the population type.
	 *
	 * @param type the type
	 * @param description the description
	 * @param poulation the poulation
	 * @return the population type
	 */
	public static PopulationType buildPopulationType(String type, String description, Population poulation){

		return new PopulationType(java.util.UUID.randomUUID().toString(), type, type, description, poulation);
	}


	/**
	 * Creates the batch type.
	 *
	 * @param fileName the file name
	 * @param reader the reader
	 * @return the population type
	 * @throws Exception the exception
	 */
	private static PopulationType createBatchType(String fileName, CSVReader reader) throws Exception{

		Population batchPopulation = null;
		String batchTypeName = fileName.substring(0, fileName.lastIndexOf("."));
		PopulationType batchType = buildPopulationType(batchTypeName,  batchTypeName+" description", batchPopulation);

		CSVFile csvFile = reader.getCsvFile();
		List<KPI> rootListKPI = new ArrayList<KPI>();
		//Map<String, KPI> kpiMap = new HashMap<String, KPI>();
		int rowIndex = 0;
		//KPI[] lastDeepLevel = new KPI[10];
//		lastDeepLevel[0] = null;
		for (CSVRow csvRow : csvFile.getValueRows()) {
			List<String> rowValues = csvRow.getListValues();
			String kpiCode = "";
			int levelIndex = 0;
			String kpiName = null;
			log.trace("Reading row values: {} ",rowValues);
			log.trace("Row index: {} ",rowIndex);
			//Looping on row values
			for (int j = 0; j < rowValues.size(); j++) {
				String value = rowValues.get(j);
				log.trace("\tColumn index: {} ",j);
				//first column I'm expecting the KPI code if exists
				if(j==0){
					kpiCode = value;
					log.trace("\tKPI code: {} ",kpiCode);
				}else if(value!=null && !value.isEmpty()){
					levelIndex = j;
					kpiName = value;
				}
			}


			PopulationType populationType = null;
			ArrayList<KPI> descendants = null;
			//DIRECT CHILD OF POPULATION (i.e. "BATCH")
			if(levelIndex==1){
				populationType = batchType;
			}

			KPI toAdd = KPIBatchTypeData.buildKPI(kpiCode, kpiName, kpiName + " description", descendants, populationType, levelIndex);
			log.debug("\t Created: {} ",toAdd);
			//lastDeepLevel[levelIndex-1] = toAdd;

			if(levelIndex==1){
				//ADDING TO ROOT LIST
				rootListKPI.add(toAdd);
//			}
//			else if(levelIndex==2){
//				//ADDING TO SECOND LEVEL
//				KPI lastRootKPI = rootListKPI.get(rootListKPI.size()-1);
//				addAsDescendant(lastRootKPI, toAdd);
			}else{
				KPI lastRootKPI = rootListKPI.get(rootListKPI.size()-1);
				//ADDING TO 3 LEVEL onwards
				KPI lastDescendant = getLastDescendantAtIndex(lastRootKPI, levelIndex-1);
				addAsDescendant(lastDescendant, toAdd);
			}

			rowIndex++;
		}

		batchType.setListKPI(rootListKPI);

		return batchType;
	}

	/**
	 * Gets the last descendant at index.
	 *
	 * @param kpi the kpi
	 * @param levelIndex the level index
	 * @return the last descendant at index
	 */
	private static KPI getLastDescendantAtIndex(KPI kpi, int levelIndex){

		if(kpi.getDeepIndex()==levelIndex)
			return kpi;

		KPI lastKPI = kpi.getListKPI().get(kpi.getListKPI().size()-1);
		return getLastDescendantAtIndex(lastKPI, levelIndex);

	}

	/**
	 * Adds the as descendant.
	 *
	 * @param parent the parent
	 * @param toAdd the to add
	 * @return the kpi
	 */
	private static KPI addAsDescendant(KPI parent, KPI toAdd){
		if(parent==null){
			return null;
		}
		List<KPI> asDescendants = parent.getListKPI();
		if(asDescendants==null){
			asDescendants = new ArrayList<KPI>();
		}
		asDescendants.add(toAdd);
		parent.setListKPI(asDescendants);
		return parent;
	}

	/**
	 * List files for folder.
	 *
	 * @param folder the folder
	 * @param files the files
	 */
	private static void listFilesForFolder(final File folder, List<File> files) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, files);
	        } else {
	        	files.add(fileEntry);
	            System.out.println(fileEntry.getName());
	        }
	    }
	}


	/**
	 * Load kpi for batch level from resource.
	 *
	 * @param context the context
	 * @return the list
	 */
	private static List<PopulationType> loadKPIForBatchLevelFromResource(ServletContext context, String pathToCSVKPI){

		String kpiFolder = null;
		List<PopulationType> batchTypes = null;
		try{

			String relKPIFolder = String.format("/WEB-INF/classes/%s", pathToCSVKPI);

			if(context==null){
				log.warn("ECLIPSE MODE ACTIVES ADDING USER DIRECTORY");
				String workingDir = System.getProperty("user.dir");
				kpiFolder = String.format(workingDir+"/%s/%s", "src/main/resources", pathToCSVKPI);
			}else{
				log.info("Getting real path of {}",relKPIFolder);
				kpiFolder = context.getRealPath(relKPIFolder);
			}

			log.info("KPI base folder is: {}",kpiFolder);
			List<File> listFile = new ArrayList<File>();
			listFilesForFolder(new File(kpiFolder), listFile);
			log.info("### Loading KPIs from path: {}",kpiFolder);
			batchTypes = new ArrayList<PopulationType>();
			for (File file : listFile) {
				try{
					log.info("Reading the csv: "+file.getName());
					CSVReader reader = new CSVReader(file);
					batchTypes.add(createBatchType(file.getName(), reader));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(log.isDebugEnabled()){
				for (PopulationType populationType : batchTypes) {
					log.info("****{} {}",PopulationType.class.getSimpleName(),populationType);
					log.debug("\t**has KPIs: "+populationType.getListKPI().size());
					for (KPI kpi : populationType.getListKPI()) {
						printKPIs(kpi);
					}
				}
			}

			log.info("### Procedure to load the KPIs from path "+kpiFolder+" terminated correctly");
		}catch(Exception e){
			log.error("Error on loading KPI from path: {}",kpiFolder, e);
		}

		return batchTypes;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try{
			String kpiFolder = String.format("%s/src/main/resources/BATCH/KPI", System.getProperty("user.dir"));
			log.info("KPI base folder is: {}",kpiFolder);
			List<File> listFile = new ArrayList<File>();
			listFilesForFolder(new File(kpiFolder), listFile);
			log.info("### Starting populating the DB: ");
			List<PopulationType> batchTypes = new ArrayList<PopulationType>();
			for (File file : listFile) {
				try{
					log.info("Reading the csv: "+file.getName());
					CSVReader reader = new CSVReader(file);
					batchTypes.add(createBatchType(file.getName(), reader));
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			for (PopulationType populationType : batchTypes) {
				log.info("****Batch {}",populationType);
				log.debug("\t**has KPIs: "+populationType.getListKPI().size());
				for (KPI kpi : populationType.getListKPI()) {
					printKPIs(kpi);
				}
			}

			log.info("### Procedure to populate the DB terminated");
		}catch(Exception e){
		}
	}

}
