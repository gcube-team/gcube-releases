/**
 *
 */
package database;

import javax.persistence.EntityManagerFactory;

import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.user.performfishanalytics.server.persistence.GenericPersistenceDaoBuilder;
import org.gcube.portlets.user.performfishanalytics.shared.Area;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.Period;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Quarter;
import org.gcube.portlets.user.performfishanalytics.shared.Species;


/**
 * The Class FillDatabasePerBatchType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 14, 2019
 */
public class PrintDatabasePerBatchType {


	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("START!");
		System.out.println("\nPRINTING DB DATA:");
		printDatabaseData();
		System.out.println("\nDB DATA PRINTED!");

		System.out.println("\n\nEND!");
	}

	public static void printDatabaseData() throws Exception {

		EntityManagerFactoryCreator.instanceLocalMode();
		EntityManagerFactory entityManagerFactory = EntityManagerFactoryCreator.getEntityManagerFactory();

		GenericPersistenceDaoBuilder<Population> builderPopulation = new GenericPersistenceDaoBuilder<Population>(entityManagerFactory, "Population");

		for(Population population: builderPopulation.getPersistenceEntity().getList()){
			System.out.println("\n\n#### Population: "+population.getName());
			for (PopulationType populationType : population.getListPopulationType()) {
				System.out.println("\n\t*PopulationType: "+populationType.getName() +" Type: "+populationType.getType());

				System.out.println("\t**has Species: "+populationType.getListSpecies().size());
				for (Species species : populationType.getListSpecies()) {
					System.out.println("\t\t"+species.getName());
				}

				System.out.println("\t**has Quarter: "+populationType.getListQuarter().size());
				for (Quarter quarter : populationType.getListQuarter()) {
					System.out.println("\t\t"+quarter.getName());
				}

				System.out.println("\t**has Area: "+populationType.getListArea().size());
				for (Area area : populationType.getListArea()) {
					System.out.println("\t\t"+area.getName());
				}

				System.out.println("\t**has Period: "+populationType.getListPeriod().size());
				for (Period period : populationType.getListPeriod()) {
					System.out.println("\t\t"+period.getName());
				}
				System.out.println("\t**has KPIs: "+populationType.getListKPI().size());
				for (KPI kpi : populationType.getListKPI()) {
					printKPIs(kpi);
				}
			}
		}

		entityManagerFactory.close();
	}


//	public static void printKPIs(List<KPI> listKPI, int level){
//		if(listKPI==null || listKPI.isEmpty())
//			return;
//
//		//System.out.println("LIST KPI ARE: "+listKPI);
//		level++;
//		for (KPI kpi : listKPI) {
//
//			//System.out.println("\t|%"+level*10+"d|KPI: "+kpi.getCode() +" Value: "+kpi.getValue());
//			System.out.println(String.format("\t%"+level*5+"d|KPI: %s, %s",level, kpi.getCode(), kpi.getValue()));
//		}
//
//		printKPIs(kpi.getListKPI(), level);
//	}

	public static void printKPIs(KPI kpi, int level){
		if(kpi==null)
			return;

		System.out.println(String.format("\t%"+level*5+"d|KPI: %s, %s",level, kpi.getCode(), kpi.getName()));
		//System.out.println("LIST KPI ARE: "+listKPI);
		level++;
		if(kpi.getListKPI()==null){
			//System.out.println("[LEAF: "+kpi.getName()+"]");
			//I'm a LEAF
			return;
		}
		for (KPI kpiChild : kpi.getListKPI()) {
			printKPIs(kpiChild, kpiChild.getDeepIndex());
		}
	}


	public static void printKPIs(KPI kpi){
		if(kpi==null || kpi.getListKPI()==null)
			return;

		System.out.println(String.format("\t%"+kpi.getDeepIndex()*5+"d|KPI: %s, %s",kpi.getDeepIndex(), kpi.getCode(), kpi.getName()));
		for (KPI kpiChild : kpi.getListKPI()) {
			printKPIs(kpiChild, kpiChild.getDeepIndex());
		}
	}

}
