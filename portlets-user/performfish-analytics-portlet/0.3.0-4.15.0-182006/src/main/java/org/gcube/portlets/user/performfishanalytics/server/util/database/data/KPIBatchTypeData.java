/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;


/**
 * The Class BatchPopulationData.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2019
 */
public class KPIBatchTypeData {


	/**
	 * Builds the kpi.
	 *
	 * @param code the code
	 * @param value the value
	 * @param description the description
	 * @param descendants the descendants
	 * @param populationType the population type
	 * @param deepIndex the deep index
	 * @return the kpi
	 */
	public static KPI buildKPI(String code, String value, String description, ArrayList<KPI> descendants, PopulationType populationType, int deepIndex){
		return new KPI(java.util.UUID.randomUUID().toString(), code, value, description, descendants, populationType, deepIndex);
	}


	/**
	 * Gets the pre on growing kp is.
	 *
	 * @param batchType the batch type
	 * @return the pre on growing kp is
	 */
	public static List<KPI> getPreOnGrowingKPIs(PopulationType batchType){
		/*
		//LEVEL 3
		KPI P1 = buildKPI("P1", "Deformed fish %", "", null,null, 3);
		//LEVEL 2
		KPI Deformedfish = buildKPI("", "Deformed fish", "", new ArrayList<KPI>(Arrays.asList(P1)), null, 2);

		//LEVEL 3
		KPI P2 = buildKPI("P2", "Discarded slow grower fish %", "", null, null, 3);
		//LEVEL 2
		KPI Discardedslowgrowerfish = buildKPI("", "Discarded slow grower fish", "", new ArrayList<KPI>(Arrays.asList(P2)), null, 2);

		//LEVEL 3
		KPI P3 = buildKPI("P3", "Mortalities - total %", "", null, null, 2);
		//LEVEL 2
		KPI Mortalitiestotal = buildKPI("", "Mortalities - total", "", new ArrayList<KPI>(Arrays.asList(P3)), null, 2);

		//LEVEL 1
		KPI PregrowTotalProductivityKPIs = buildKPI("", "Pre-grow Total Productivity KPIs", "", new ArrayList<KPI>(Arrays.asList(Deformedfish, Discardedslowgrowerfish, Mortalitiestotal)), batchType, 1);


		//LEVEL 3
		KPI P4 = buildKPI("P4", "Specific Growth Rate (SGR) (% day^-1)", "", null,null, 3);
		//LEVEL 2
		KPI SpecificGrowthRateSGR = buildKPI("", "Specific Growth Rate (SGR)", "", new ArrayList<KPI>(Arrays.asList(P4)), null, 2);

		KPI FishGrowtKPIs = buildKPI("", "Fish Growth KPIs", "", new ArrayList<KPI>(Arrays.asList(SpecificGrowthRateSGR)), batchType, 1);


		//LEVEL 3
		KPI P4 = buildKPI("P4", "Specific Growth Rate (SGR) (% day^-1)", "", null,null, 3);
		//LEVEL 2
		KPI SpecificGrowthRateSGR = buildKPI("", "Specific Growth Rate (SGR)", "", new ArrayList<KPI>(Arrays.asList(P4)), null, 2);

		KPI FishGrowtKPIs = buildKPI("", "Fish Growth KPIs", "", new ArrayList<KPI>(Arrays.asList(SpecificGrowthRateSGR)), batchType, 1);
		*/
		return null;
	}



	/**
	 * OK
	 * Gets the list kpi for hatcher y_ individual.
	 *
	 * @param batchType the batch type
	 * @return the list kpi for hatcher y_ individual
	 */
	public static KPI getHatcheryTotalProductivityKPIsHATCHERY_INDIVIDUAL(PopulationType batchType){

		//LEVEL 3
		KPI H1 = buildKPI("H1", "Weaned fish (%)", "", null, null, 3);
		//LEVEL 2
		KPI Weanedfish = buildKPI("", "Weaned fish", "", new ArrayList<KPI>(Arrays.asList(H1)), null, 2);

		//LEVEL 3
		KPI H2_1_1 = buildKPI("H2_1_1", "Discarded fish for head deformities - at 1200dd (%)", "", null, null, 3);
		KPI H2_1_2 = buildKPI("H2_1_2", "Discarded fish for head deformities - at 1800dd (%)", "", null, null, 3);
		KPI H2_1_3 = buildKPI("H2_1_3", "Discarded fish for head deformities - at 2400dd (%)", "", null, null, 3);
		KPI H2_2_1 = buildKPI("H2_2_1", "Discarded fish for spinal deformities - at 1200dd (%)", "", null, null, 3);
		KPI H2_2_2 = buildKPI("H2_2_2", "Discarded fish for spinal deformities - at 1800dd (%)", "", null, null, 3);
		KPI H2_2_3 = buildKPI("H2_2_3", "Discarded fish for spinal deformities - at 2400dd (%)", "", null, null, 3);
		KPI H2_3_1 = buildKPI("H2_3_1", "Discarded fish for fin deformities - at 1200dd (%)", "", null, null, 3);
		KPI H2_3_2 = buildKPI("H2_3_2", "Discarded fish for fin deformities - at 1800dd (%)", "", null, null, 3);
		KPI H2_3_3 = buildKPI("H2_3_3", "Discarded fish for fin deformities - at 2400dd (%)", "", null, null, 3);
		KPI H7 = buildKPI("H7", "Head deformities (at 2400dd) %", "", null, null, 3);
		// LEVEL 2
		KPI Discardeddeformedfish =
			buildKPI("","Discarded deformed fish","",new ArrayList<KPI>(Arrays.asList(
					H7, H2_1_1, H2_1_2, H2_1_3, H2_2_1, H2_2_2, H2_2_3, H2_3_1,H2_3_2, H2_3_3, H7)), null, 2);


		//LEVEL 3
		KPI H3_1 = buildKPI("H3_1", "Discarded slow grower fish - at 1200dd (%)", "", null, null, 3);
		KPI H3_2 = buildKPI("H3_2", "Discarded slow grower fish - at 1800dd (%)", "", null, null, 3);
		KPI H3_3 = buildKPI("H3_3", "Discarded slow grower fish - at 2400dd (%)", "", null, null, 3);
		//LEVEL 2
		KPI Discardedslowgrowerfish = buildKPI("", "Discarded slow grower fish", "", new ArrayList<KPI>(Arrays.asList(H3_1, H3_2, H3_3)), null, 2);


		//LEVEL 3
		KPI H4 = buildKPI("H4", "Fish produced - at 2400dd (%)", "", null, null, 3);
		//LEVEL 2
		KPI FishProduced = buildKPI("", "Fish produced", "", new ArrayList<KPI>(Arrays.asList(H4)), null, 2);


		//LEVEL 3
		KPI H5_1_1 = buildKPI("H5_1_1", "Number fish/FTE employees in 2018", "", null, null, 3);
		KPI H5_1_2 = buildKPI("H5_1_2", "Number fish/FTE employees in 2019", "", null, null, 3);
		KPI H5_1_3 = buildKPI("H5_1_3", "Number fish/FTE employees in 2020", "", null, null, 3);
		//LEVEL 2
		KPI FishproducedperFTEemployees = buildKPI("", "Fish produced per FTE employees", "", new ArrayList<KPI>(Arrays.asList(H5_1_1, H5_1_2, H5_1_3)), null, 2);


		//LEVEL 3
		KPI H6_1 = buildKPI("H6_1", "Survival estimation - at 1200dd (%)", "", null, null, 3);
		KPI H6_2 = buildKPI("H6_2", "Survival estimation - at 1800dd (%)", "", null, null, 3);
		KPI H6_3 = buildKPI("H6_3", "Survival estimation - at 2400dd (%)", "", null, null, 3);
		//LEVEL 2
		KPI Survivalestimation = buildKPI("", "Survival estimation", "", new ArrayList<KPI>(Arrays.asList(H6_1, H6_2, H6_3)), null, 2);


		//LEVEL 3
		KPI H15_1 = buildKPI("H15_1", "Vaccinated fish against P. damselae - at 2400dd (%)", "", null, null, 3);
		KPI H15_2 = buildKPI("H15_2", "Vaccinated fish against V. anguillarum - at 2400dd (%)", "", null, null, 3);
		KPI H15_3 = buildKPI("H15_3", "Vaccinated fish against Betanodavirus - at 2400dd (%)", "", null, null, 3);
		//LEVEL 2
		KPI Vaccinatedfishbydisease = buildKPI("", "Vaccinated fish by disease", "", new ArrayList<KPI>(Arrays.asList(H15_1, H15_2, H15_3)), null, 2);


		//LEVEL 1
		KPI Deformities = buildKPI("", "Hatchery Total Productivity KPIs", "", new ArrayList<KPI>(Arrays.asList(
			Weanedfish, Discardeddeformedfish, Discardedslowgrowerfish, FishProduced, FishproducedperFTEemployees, Survivalestimation, Vaccinatedfishbydisease)), batchType, 1);

		return Deformities;
	}



	/**
	 *
	 * OK
	 * Gets the fish deformities kp isfor hatcher y_ individual.
	 *
	 * @param batchType the batch type
	 * @return the fish deformities kp isfor hatcher y_ individual
	 */
	public static KPI getFishDeformitiesKPIsforHATCHERY_INDIVIDUAL(PopulationType batchType){

		//LEVEL 3
		KPI H7_1 = buildKPI("H7_1", "Alive fish with head deformities - at 2400dd (%)", "", null, null, 3);
		KPI H7_2 = buildKPI("H7_2", "Alive fish with head deformities - at end of weaning (%)", "", null, null, 3);
		//LEVEL 2
		KPI Headdeformities = buildKPI("", "Head deformities", "", new ArrayList<KPI>(Arrays.asList(H7_1, H7_2)), null, 2);

		//LEVEL 3
		KPI H8 = buildKPI("H8", "Alive fish with spinal deformities - at 2400dd (%)", "", null, null, 3);
		KPI H8_1 = buildKPI("H8_1", "Alive fish with spinal deformities - at end of weaning (%)", "", null, null, 3);
		//LEVEL 2
		KPI Spinaldeformities = buildKPI("", "Spinal deformities", "", new ArrayList<KPI>(Arrays.asList(H8, H8_1)), null, 2);

		//LEVEL 3
		KPI H9 = buildKPI("H9", "Alive fish with fin deformities - at 2400dd (%)", "", null, null, 3);
		KPI H9_1 = buildKPI("H9_1", "Alive fish with fin deformities - at end of weaning (%)", "", null, null, 3);
		//LEVEL 2
		KPI Findeformities = buildKPI("", "Fin deformities", "", new ArrayList<KPI>(Arrays.asList(H9, H9_1)), null, 2);

		//LEVEL 3
		KPI H10 = buildKPI("H10", "Swim bladder non inflation - at 500dd (%)", "", null, null, 3);
		//LEVEL 2
		KPI Swimbladdernoninflation = buildKPI("", "Swim bladder non inflation", "", new ArrayList<KPI>(Arrays.asList(H10)), null, 2);

		//LEVEL 1
		KPI Deformities = buildKPI("", "Fish Deformities KPIs", "", new ArrayList<KPI>(Arrays.asList(
			Headdeformities, Spinaldeformities, Findeformities, Swimbladdernoninflation)), batchType, 1);

		return Deformities;
	}


	/**
	 * OK
	 * Gets the fish growth kp isfor hatcher y_ individual.
	 *
	 * @param batchType the batch type
	 * @return the fish growth kp isfor hatcher y_ individual
	 */
	public static KPI getFishGrowthKPIsforHATCHERY_INDIVIDUAL(PopulationType batchType){

		//LEVEL 3
		KPI H11_1 = buildKPI("H11_1", "Average body weight (BW) - at weaning (g)", "", null, null, 3);
		KPI H11_2 = buildKPI("H11_2", "Average body weight (BW) - at 1200dd (g)", "", null, null, 3);
		KPI H11_3 = buildKPI("H11_3", "Average body weight (BW) - at 1800dd (g)", "", null, null, 3);
		KPI H11_4 = buildKPI("H11_4", "Average body weight (BW) - at 2400dd (g)", "", null, null, 3);
		//LEVEL 2
		KPI AverageBodyWeightABW = buildKPI("", "Average Body Weight (ABW)", "", new ArrayList<KPI>(Arrays.asList(H11_1, H11_2, H11_3, H11_4)), null, 2);


		//LEVEL 3
		KPI H12_1 = buildKPI("H12_1", "Specific Growth Rate (SGR) - at 1200dd (% day^-1)", "", null, null, 3);
		KPI H12_2 = buildKPI("H12_2", "Specific Growth Rate (SGR) - at 1800dd (% day^-1)", "", null, null, 3);
		KPI H12_3 = buildKPI("H12_3", "Specific Growth Rate (SGR) - at 2400dd (% day^-1)", "", null, null, 3);
		//LEVEL 2
		KPI SpecificGrowthRateSGR = buildKPI("", "Specific Growth Rate (SGR)", "", new ArrayList<KPI>(Arrays.asList(H12_1, H12_2, H12_3)), null, 2);


		KPI FishGrowthKPIs = buildKPI("", "Fish Growth KPIs", "", new ArrayList<KPI>(Arrays.asList(
			AverageBodyWeightABW, SpecificGrowthRateSGR)), batchType, 1);

		return FishGrowthKPIs;
	}



	/**
	 * Gets the live feed requirement kp isfor hatcher y_ individual.
	 *
	 * @param batchType the batch type
	 * @return the live feed requirement kp isfor hatcher y_ individual
	 */
	public static KPI getLiveFeedRequirementKPIsforHATCHERY_INDIVIDUAL(PopulationType batchType){

		//LEVEL 3
		KPI H13 = buildKPI("H13", "Artemia requirement - at 2400dd (Kg of Artemia/million fish)", "", null, null, 3);
		//LEVEL 2
		KPI Artemiarequirement = buildKPI("", "Artemia requirement", "", new ArrayList<KPI>(Arrays.asList(H13)), null, 2);

		//LEVEL 3
		KPI H14 = buildKPI("H14", "Rotifers requirement - at 2400dd (Billions rotifer/million fish)", "", null, null, 3);

		//LEVEL 2
		KPI Rotiferrequirement = buildKPI("", "Rotifer requirement", "", new ArrayList<KPI>(Arrays.asList(H14)), null, 2);

		KPI FishGrowthKPIs = buildKPI("", "Live Feed Requirement KPIs", "", new ArrayList<KPI>(Arrays.asList(
			Artemiarequirement, Rotiferrequirement)), batchType, 1);

		return FishGrowthKPIs;
	}


	/**
	 * Gets the losses.
	 *
	 * @param batchType the batch type
	 * @return the losses
	 */
	public static KPI getLosses(PopulationType batchType){
		//LEVEL 3 - G1
		KPI G1 = buildKPI("G1", "Mortalities - Total (at harvest) %", "", null, null, 3);
		KPI G1_1 = buildKPI("G1_1", "Mortalities - Total (Stocking-50g) %", "", null, null, 3);
		KPI G1_2 = buildKPI("G1_2", "Mortalities - Total (50g-150g) %", "", null, null, 3);
		KPI G1_3 = buildKPI("G1_3", "Mortalities - Total (150g-250g) %", "", null, null, 3);
		KPI G1_4 = buildKPI("G1_4", "Mortalities - Total (250g-400g) %", "", null, null, 3);
		KPI G1_5 = buildKPI("G1_5", "Mortalities - Total (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G1
		KPI Mortality_Total = buildKPI("", "Mortality - Total", "", new ArrayList<KPI>(Arrays.asList(G1, G1_1, G1_2, G1_3, G1_4, G1_5)), null, 2);

		//LEVEL 3 - G2
		KPI G2 = buildKPI("G2", "Mortalities - by disease Total (at harvest) %", "", null, null, 3);
		KPI G2_1 = buildKPI("G2_1", "Mortalities - by disease (Stocking-50g) %", "", null, null, 3);
		KPI G2_2 = buildKPI("G2_2", "Mortalities - by disease (50g-150g) %", "", null, null, 3);
		KPI G2_3 = buildKPI("G2_3", "Mortalities - by disease (150g-250g) %", "", null, null, 3);
		KPI G2_4 = buildKPI("G2_4", "Mortalities - by disease (250g-400g) %", "", null, null, 3);
		KPI G2_5 = buildKPI("G2_5", "Mortalities - by disease (400g-800g-final harvest) %", "", null, null, 3);


		//LEVEL 2 - G2
		KPI Mortality_by_disease = buildKPI("", "Mortality by disease", "", new ArrayList<KPI>(Arrays.asList(G2, G2_1, G2_2, G2_3, G2_4, G2_5)), null, 2);

		//LEVEL 2
		KPI G3 = buildKPI("G3", "Mortalities first 3 days after transport and stocking %", "", null, null, 2);
		KPI G4 = buildKPI("G4", "Mortalities first 10 days after transport and stocking %", "", null, null, 2);
		KPI G5 = buildKPI("G5", "Unaccounted losses %", "", null, null, 2);
		KPI G6 = buildKPI("G3", "Total losses %", "", null, null, 2);
		KPI G7 = buildKPI("G7", "Discarded fish at slaughter %", "", null, null, 2);

		//LEVEL 1 - Losses
		KPI Losses = buildKPI("", "Losses", "", new ArrayList<KPI>(Arrays.asList(Mortality_Total, Mortality_by_disease, G3, G4, G5, G6, G7)), batchType, 1);

		return Losses;
	}


	/**
	 * Gets the treatments for grow out individual.
	 *
	 * @param batchType the batch type
	 * @return the treatments for grow out individual
	 */
	public static KPI getTreatmentsForGrowOutIndividual(PopulationType batchType){
		//LEVEL 2 - G8_X and G9 and G10
		KPI G8_1 = buildKPI("G8_1", "Vaccinated fish by disease (P. damselae) %", "", null, null, 2);
		KPI G8_2 = buildKPI("G8_2", "Vaccinated fish by disease (V. Anguillarum) %", "", null, null, 2);
		KPI G8_3 = buildKPI("G8_3", "Vaccinated fish by disease (Betanodavirus) %", "", null, null, 2);
		KPI G9 = buildKPI("G9", "Number of antiparasitic treatments", "", null, null, 2);
		KPI G10 = buildKPI("G10", "Number of antibiotic treatments", "", null, null, 2);

		//LEVEL 1 - Treatments
		KPI Treatments = buildKPI("", "Treatments", "", new ArrayList<KPI>(Arrays.asList(G8_1, G8_2, G8_3, G9, G10)), batchType, 1);

		return Treatments;
	}


	/**
	 * Gets the growth for grow out individual.
	 *
	 * @param batchType the batch type
	 * @return the growth for grow out individual
	 */
	public static KPI getGrowthForGrowOutIndividual(PopulationType batchType){
		//########## Growth
		//LEVEL 3 - G13_X
		KPI G13_1 = buildKPI("G13_1", "Specific growth rate (SGR) (50g-150g) %", "", null, null, 3);
		KPI G13_2 = buildKPI("G13_2", "Specific growth rate (SGR) (150g-250g) %", "", null, null, 3);
		KPI G13_3 = buildKPI("G13_3", "Specific growth rate (SGR) (250g-400g) %", "", null, null, 3);
		KPI G13_4 = buildKPI("G13_4", "Specific growth rate (SGR) (400g-800g) %", "", null, null, 3);
		//LEVEL 2 - G13_X
		KPI Specific_growth_rate = buildKPI("", "Specific growth rate", "", new ArrayList<KPI>(Arrays.asList(G13_1, G13_2, G13_3, G13_4)), null, 2);

		//LEVEL 3 - G14_X
		KPI G14_1 = buildKPI("G14_1", "Grams per day (GPD) (50g-150g) %", "", null, null, 3);
		KPI G14_2 = buildKPI("G14_2", "Grams per day (GPD) (150g-250g) %", "", null, null, 3);
		KPI G14_3 = buildKPI("G14_3", "Grams per day (GPD) (250g-400g) %", "", null, null, 3);
		KPI G14_4 = buildKPI("G14_4", "Grams per day (GPD) (400g-800g) %", "", null, null, 3);
		//LEVEL 2 - G14_X
		KPI Grams_per_day = buildKPI("", "Grams per day", "", new ArrayList<KPI>(Arrays.asList(G14_1, G14_2, G14_3, G14_4)), null, 2);

		//LEVEL 3 - G15_X
		KPI G15_1 = buildKPI("G15_1", "Thermal Growth Coefficient (TGC) (50g-150g) %", "", null, null, 3);
		KPI G15_2 = buildKPI("G15_2", "Thermal Growth Coefficient (TGC) (150g-250g) %", "", null, null, 3);
		KPI G15_3 = buildKPI("G15_3", "Thermal Growth Coefficient (TGC) (250g-400g) %", "", null, null, 3);
		KPI G15_4 = buildKPI("G15_4", "Thermal Growth Coefficient (TGC) (400g-800g) %", "", null, null, 3);
		//LEVEL 2 - G15_X
		KPI Thermal_Growth = buildKPI("", "Thermal Growth", "", new ArrayList<KPI>(Arrays.asList(G15_1, G15_2, G15_3, G15_4)), null, 2);

		//LEVEL 1
		KPI Growth = buildKPI("", "Growth", "", new ArrayList<KPI>(Arrays.asList(Specific_growth_rate, Grams_per_day, Thermal_Growth)), batchType, 1);

		return Growth;
	}


	/**
	 * Gets the efficiency.
	 *
	 * @param batchType the batch type
	 * @return the efficiency
	 */
	public static KPI getEfficiency(PopulationType batchType){
		//########## Efficiency
		//LEVEL 3 - G16_X
		KPI G16_1 = buildKPI("G16_1", "Feed Conversion Ratio (FCR) (50g-150g) %", "", null, null, 3);
		KPI G16_2 = buildKPI("G16_2", "Feed Conversion Ratio (FCR) (150g-250g) %", "", null, null, 3);
		KPI G16_3 = buildKPI("G16_3", "Feed Conversion Ratio (FCR) (250g-400g) %", "", null, null, 3);
		KPI G16_4 = buildKPI("G16_4", "Feed Conversion Ratio (FCR) (400g-800g-final harvest) %", "", null, null, 3);
		KPI G16 = buildKPI("G16", "Feed Conversion Ratio (FCR) (harvest) %", "", null, null, 3);
		//LEVEL 2 - G16_X
		KPI Feed_Conversion_Ratio = buildKPI("", "Feed Conversion Ratio", "", new ArrayList<KPI>(Arrays.asList(G16_1, G16_2, G16_3, G16_4, G16)), null, 2);

		//LEVEL 3 - G17_X
		KPI G17_1 = buildKPI("G17_1", "Biological Feed Conversion Ratio (FCRBIO) (50g-150g) %", "", null, null, 3);
		KPI G17_2 = buildKPI("G17_2", "Biological Feed Conversion Ratio (FCRBIO) (150g-250g) %", "", null, null, 3);
		KPI G17_3 = buildKPI("G17_3", "Biological Feed Conversion Ratio (FCRBIO) (250g-400g) %", "", null, null, 3);
		KPI G17_4 = buildKPI("G17_4", "Feed Conversion Ratio (FCR) (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G14_X
		KPI Biological_Feed_Conversion_Ratio = buildKPI("", "Biological Feed Conversion Ratio", "", new ArrayList<KPI>(Arrays.asList(G17_1, G17_2, G17_3, G17_4)), null, 2);

		//LEVEL 3 - G18_X
		KPI G18_1 = buildKPI("G18_1", "Feed Intake (FI) (50g-150g) %", "", null, null, 3);
		KPI G18_2 = buildKPI("G18_2", "Feed Intake (FI) (150g-250g) %", "", null, null, 3);
		KPI G18_3 = buildKPI("G18_3", "Feed Intake (FI) (250g-400g) %", "", null, null, 3);
		KPI G18_4 = buildKPI("G18_4", "Feed Intake (FI) (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G18_X
		KPI Feed_Intake = buildKPI("", "Feed Intake", "", new ArrayList<KPI>(Arrays.asList(G18_1, G18_2, G18_3, G18_4)), null, 2);

		//LEVEL 2 - G19
		KPI Stocking_density = buildKPI("G19", "Stocking density", "",null, null, 2);

		//LEVEL 1
		KPI Efficiency = buildKPI("", "Efficiency", "", new ArrayList<KPI>(Arrays.asList(Feed_Conversion_Ratio, Biological_Feed_Conversion_Ratio, Feed_Intake, Stocking_density)), batchType, 1);

		return Efficiency;
	}




	/**
	 * Gets the productivity.
	 *
	 * @param batchType the batch type
	 * @return the productivity
	 */
	public static KPI getProductivityForHATCHERY_INDIVIDUAL(PopulationType batchType){
		//LEVEL 2 - H1
		KPI H1 = buildKPI("H1", "Weaned fish %", "",null, null, 2);

		//LEVEL 3  H4 and H5_1_X
		KPI H5_1_1 = buildKPI("H5_1_1", "Fish produced per FTE employees (at 2400dd) % in 2018", "", null, null, 3);
		KPI H5_1_2 = buildKPI("H5_1_2", "Fish produced per FTE employees (at 2400dd) % in 2019", "", null, null, 3);
		KPI H5_1_3 = buildKPI("H5_1_3", "Fish produced per FTE employees (at 2400dd) % in 2020", "", null, null, 3);
		KPI H4 = buildKPI("H4", "Fish produced (at the end of cycle) %", "", null, null, 3);
		//LEVEL 2 - Produced_fish
		KPI Produced_fish = buildKPI("", "Produced fish", "", new ArrayList<KPI>(Arrays.asList(H5_1_1, H5_1_2, H5_1_3, H4)), null, 2);

		KPI H13 = buildKPI("H13", "Artemia requirement (at the end of weaning)", "",null, null, 2);
		KPI H14 = buildKPI("H14", "Rotifer requirement (at the end of weaning)", "",null, null, 2);

		//LEVEL 1
		KPI Productivity = buildKPI("", "Productivity", "", new ArrayList<KPI>(Arrays.asList(H1, Produced_fish, H13, H14)), batchType, 1);

		return Productivity;
	}


	/**
	 * Gets the growth for hatchery individual.
	 *
	 * @param batchType the batch type
	 * @return the growth for hatchery individual
	 */
	public static KPI getGrowthForHATCHERY_INDIVIDUAL(PopulationType batchType){

		//LEVEL 3 - H11_X
		KPI H11_1 = buildKPI("H11_1", "Average Body Weight (ABW) at end of weaning", "", null, null, 3);
		KPI H11_2 = buildKPI("H11_2", "Average Body Weight (ABW) at 1200dd", "", null, null, 3);
		KPI H11_3 = buildKPI("H11_3", "Average Body Weight (ABW) at 1800dd", "", null, null, 3);
		KPI H11_4 = buildKPI("H11_4", "Average Body Weight (ABW) at 2400dd", "", null, null, 3);
		//LEVEL 2 - Body_weight
		KPI Body_weight = buildKPI("", "Body weight", "", new ArrayList<KPI>(Arrays.asList(H11_1, H11_2, H11_3, H11_4)), null, 2);

		//LEVEL 3 - H12_X
		KPI H12_1 = buildKPI("H12_1", "Specific Growth Rate (SGR) at 1200dd", "", null, null, 3);
		KPI H12_2 = buildKPI("H12_2", "Specific Growth Rate (SGR) at 1800dd", "", null, null, 3);
		KPI H12_3 = buildKPI("H12_3", "Specific Growth Rate (SGR) at 2400dd", "", null, null, 3);
		//LEVEL 2 - Specific_growth_rate
		KPI Specific_growth_rate = buildKPI("", "Specific growth rate", "", new ArrayList<KPI>(Arrays.asList(H12_1, H12_2, H12_3)), null, 2);

		//LEVEL 1
		KPI Growth = buildKPI("", "Growth", "", new ArrayList<KPI>(Arrays.asList(Body_weight, Specific_growth_rate)), batchType, 1);

		return Growth;
	}


	/**
	 * Gets the survival discard rates.
	 *
	 * @param batchType the batch type
	 * @return the survival discard rates
	 */
	public static KPI getSurvivalDiscardRatesForHATCHERY_INDIVIDUAL(PopulationType batchType){
		//LEVEL 3 - H3_X
		KPI H3_1 = buildKPI("H3_1", "Discarded slow grower fish % at 1200dd", "", null, null, 3);
		KPI H3_2 = buildKPI("H3_2", "Discarded slow grower fish % at 1800dd", "", null, null, 3);
		KPI H3_3 = buildKPI("H3_3", "Discarded slow grower fish % at 2400dd", "", null, null, 3);
		//LEVEL 2 - Discarded
		KPI Discarded = buildKPI("", "Discarded", "", new ArrayList<KPI>(Arrays.asList(H3_1, H3_2, H3_3)), null, 2);

		//LEVEL 3 - H12_X
		KPI H6_1 = buildKPI("H6_1", "Survival estimation % at 1200dd", "", null, null, 3);
		KPI H6_2 = buildKPI("H6_2", "Survival estimation % at 1800dd", "", null, null, 3);
		KPI H6_3 = buildKPI("H6_3", "Survival estimation % at 2400dd", "", null, null, 3);
		//LEVEL 2 - Survival
		KPI Survival = buildKPI("", "Survival", "", new ArrayList<KPI>(Arrays.asList(H6_1, H6_2, H6_3)), null, 2);

		//LEVEL 1
		KPI Survival_Discard_rates = buildKPI("", "Survival/Discard rates", "", new ArrayList<KPI>(Arrays.asList(Discarded, Survival)), batchType, 1);

		return Survival_Discard_rates;
	}


	/**
	 * Gets the treatments for hatchery individual.
	 *
	 * @param batchType the batch type
	 * @return the treatments for hatchery individual
	 */
	public static KPI getTreatmentsForHATCHERY_INDIVIDUAL(PopulationType batchType){
		//LEVEL 3 - H15_X
		KPI H15_1 = buildKPI("H15_1", "Vaccinated fish by disease (at 2400dd) % against P. Damselae", "", null, null, 2);
		KPI H15_2 = buildKPI("H15_2", "Vaccinated fish by disease (at 2400dd) % against V. Anguillarum", "", null, null, 2);
		KPI H15_3 = buildKPI("H15_3", "Vaccinated fish by disease (at 2400dd) % against Betanodavirus", "", null, null, 2);
		//LEVEL 2 - Discarded
		KPI Treatments = buildKPI("", "Treatments", "", new ArrayList<KPI>(Arrays.asList(H15_1, H15_2, H15_3)), batchType, 1);

		return Treatments;
	}


	/**
	 * Gets the deformities for hatcher y_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the deformities for hatcher y_ aggregated
	 */
	public static KPI getDeformitiesForHATCHERY_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - H7 and H2_1_3
		KPI H7 = buildKPI("H7", "Head deformities (at 2400dd) %", "", null, null, 3);
		KPI H2_1_3 = buildKPI("H2_1_3", "ead deformed fish % at 2400dd", "", null, null, 3);
		//LEVEL 2 - Head
		KPI Head = buildKPI("", "Head", "", new ArrayList<KPI>(Arrays.asList(H7, H2_1_3)), null, 2);

		//LEVEL 3 - H8 and H2_2_3
		KPI H8 = buildKPI("H8", "Spinal deformities (at 2400dd) %", "", null, null, 3);
		KPI H2_2_3 = buildKPI("H2_2_3", "Spinal deformed fish % at 2400dd", "", null, null, 3);

		//LEVEL 2 - Spinal
		KPI Spinal = buildKPI("", "Spinal", "", new ArrayList<KPI>(Arrays.asList(H8, H2_2_3)), null, 2);

		//LEVEL 3 - H9 and H2_3_3
		KPI H9 = buildKPI("H9", "Fin deformities (at 2400dd) %", "", null, null, 3);
		KPI H2_3_3 = buildKPI("H2_3_3", "Fin deformed fish % at 2400dd", "", null, null, 3);
		//LEVEL 2 - G18_X
		KPI Fin = buildKPI("", "Fin", "", new ArrayList<KPI>(Arrays.asList(H9, H2_3_3)), null, 2);

		//LEVEL 2 - H10
		KPI H10 = buildKPI("H10", "Swim bladder non inflation (at 500dd)", "",null, null, 2);

		//LEVEL 1
		KPI Deformities = buildKPI("", "Deformities", "", new ArrayList<KPI>(Arrays.asList(Head, Spinal, Fin, H10)), batchType, 1);

		return Deformities;
	}


	/**
	 * Gets the productivity for hatcher y_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the productivity for hatcher y_ aggregated
	 */
	public static KPI getProductivityForHATCHERY_AGGREGATED(PopulationType batchType) {

		//LEVEL 2 - H1
		KPI H1 = buildKPI("H1", "Weaned fish %", "",null, null, 2);

		//LEVEL 3  H4 and H5_1_X
		KPI H4 = buildKPI("H4", "Fish produced at 2400dd %", "", null, null, 3);
		KPI H5_1_1 = buildKPI("H5_1_1", "Fish produced per FTE employees (at 2400dd) % in 2018", "", null, null, 3);
		KPI H5_1_2 = buildKPI("H5_1_2", "Fish produced per FTE employees (at 2400dd) % in 2019", "", null, null, 3);
		KPI H5_1_3 = buildKPI("H5_1_3", "Fish produced per FTE employees (at 2400dd) % in 2020", "", null, null, 3);
		//LEVEL 2 - Produced_fish
		KPI Produced_fish = buildKPI("", "Produced fish", "", new ArrayList<KPI>(Arrays.asList(H4, H5_1_1, H5_1_2, H5_1_3)), null, 2);

		KPI H13 = buildKPI("H13", "Artemia requirement (at the end of weaning)", "",null, null, 2);
		KPI H14 = buildKPI("H14", "Rotifer requirement (at the end of weaning)", "",null, null, 2);

		//LEVEL 1
		KPI Productivity = buildKPI("", "Productivity", "", new ArrayList<KPI>(Arrays.asList(H1, Produced_fish, H13, H14)), batchType, 1);

		return Productivity;
	}


	/**
	 * Gets the survival discard rates for hatcher y_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the survival discard rates for hatcher y_ aggregated
	 */
	public static KPI getSurvivalDiscardRatesForHATCHERY_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - H3_3
		KPI H3_3 = buildKPI("H3_3", "Discarded slow grower fish % at 2400dd", "", null, null, 3);
		//LEVEL 2 - Discarded
		KPI Discarded = buildKPI("", "Discarded", "", new ArrayList<KPI>(Arrays.asList(H3_3)), null, 2);

		//LEVEL 3 - H6
		KPI H6 = buildKPI("H6", "Survival estimation % at 2400dd", "", null, null, 3);
		//LEVEL 2 - Survival
		KPI Survival = buildKPI("", "Survival", "", new ArrayList<KPI>(Arrays.asList(H6)), null, 2);

		//LEVEL 1
		KPI Survival_Discard_rates = buildKPI("", "Survival/Discard rates", "", new ArrayList<KPI>(Arrays.asList(Discarded, Survival)), batchType, 1);

		return Survival_Discard_rates;
	}


	/**
	 * Gets the treatments for hatcher y_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the treatments for hatcher y_ aggregated
	 */
	public static KPI getTreatmentsForHATCHERY_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - H15_X
		KPI H15_1 = buildKPI("H15_1", "Vaccinated fish by disease (at 2400dd) % against P. Damselae", "", null, null, 2);
		KPI H15_2 = buildKPI("H15_2", "Vaccinated fish by disease (at 2400dd) % against V. Anguillarum", "", null, null, 2);
		KPI H15_3 = buildKPI("H15_3", "Vaccinated fish by disease (at 2400dd) % against Betanodavirus", "", null, null, 2);
		//LEVEL 2 - Discarded
		KPI Treatments = buildKPI("", "Treatments", "", new ArrayList<KPI>(Arrays.asList(H15_1, H15_2, H15_3)), batchType, 1);

		return Treatments;
	}



	/**
	 * Gets the losses for gro w_ ou t_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the losses for gro w_ ou t_ aggregated
	 */
	public static KPI getLossesForGROW_OUT_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - G1_X
		KPI G1 = buildKPI("G1", "Mortalities - Total (at harvest) %", "", null, null, 3);
		KPI G1_1 = buildKPI("G1_1", "Mortalities - Total (Stocking-150g) %", "", null, null, 3);
		KPI G1_4 = buildKPI("G1_4", "Mortalities - Total (150g-400g) %", "", null, null, 3);
		KPI G1_5 = buildKPI("G1_5", "Mortalities - Total (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G1
		KPI Mortality_Total = buildKPI("", "Mortality - Total", "", new ArrayList<KPI>(Arrays.asList(G1, G1_1, G1_4, G1_5)), null, 2);

		//LEVEL 3 - G2
		KPI G2 = buildKPI("G2", "Mortalities - by disease Total (at harvest) %", "", null, null, 3);
		KPI G2_1 = buildKPI("G2_1", "Mortalities - by disease (Stocking-150g) %", "", null, null, 3);
		KPI G2_4 = buildKPI("G2_4", "Mortalities - by disease (250g-400g) %", "", null, null, 3);
		KPI G2_5 = buildKPI("G2_5", "Mortalities - by disease (400g-800g-final harvest) %", "", null, null, 3);

		//LEVEL 2 - G2
		KPI Mortality_by_disease = buildKPI("", "Mortality by disease", "", new ArrayList<KPI>(Arrays.asList(G2, G2_1, G2_4, G2_5)), null, 2);

		//LEVEL 2
		KPI G3 = buildKPI("G3", "Mortalities first 3 days after transport and stocking %", "", null, null, 2);
		KPI G4 = buildKPI("G4", "Mortalities first 10 days after transport and stocking %", "", null, null, 2);
		KPI G5 = buildKPI("G5", "Unaccounted losses %", "", null, null, 2);
		KPI G6 = buildKPI("G3", "Total losses %", "", null, null, 2);
		KPI G7 = buildKPI("G7", "Discarded fish at slaughter %", "", null, null, 2);

		//LEVEL 1 - Losses
		KPI Losses = buildKPI("", "Losses", "", new ArrayList<KPI>(Arrays.asList(Mortality_Total, Mortality_by_disease, G3, G4, G5, G6, G7)), batchType, 1);

		return Losses;
	}


	/**
	 * Gets the treatments for gro w_ ou t_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the treatments for gro w_ ou t_ aggregated
	 */
	public static KPI getTreatmentsForGROW_OUT_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - G8_X
		KPI G8_1 = buildKPI("G8_1", "Vaccinated fish by disease (P. damselae) %", "", null, null, 2);
		KPI G8_2 = buildKPI("G8_2", "Vaccinated fish by disease (V. Anguillarum) %", "", null, null, 2);
		KPI G8_3 = buildKPI("G8_3", "Vaccinated fish by disease (Betanodavirus) %", "", null, null, 2);
		//LEVEL 2 - Treatments
		KPI Treatments = buildKPI("", "Treatments", "", new ArrayList<KPI>(Arrays.asList(G8_1, G8_2, G8_3)), batchType, 1);

		return Treatments;
	}


	/**
	 * Gets the growth for gro w_ ou t_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the growth for gro w_ ou t_ aggregated
	 */
	public static KPI getGrowthForGROW_OUT_AGGREGATED(PopulationType batchType) {

		//LEVEL 3 - G13_X
		KPI G13_3 = buildKPI("G13_3", "Specific growth rate (SGR) (150g-400g) %", "", null, null, 3);
		KPI G13_4 = buildKPI("G13_4", "Specific growth rate (SGR) (400g-800g) %", "", null, null, 3);
		//LEVEL 2 - pecific growth rate
		KPI Body_weight = buildKPI("", "Specific growth rate", "", new ArrayList<KPI>(Arrays.asList(G13_3, G13_4)), null, 2);

		//LEVEL 3 - G14_X
		KPI G14_3 = buildKPI("G14_3", "Grams per day (GPD) (150g-400g) %", "", null, null, 3);
		KPI G14_4 = buildKPI("G14_4", "Grams per day (GPD) (400g-800g) %", "", null, null, 3);
		//LEVEL 2 - Specific_growth_rate
		KPI Grams_per_day = buildKPI("", "Grams per day", "", new ArrayList<KPI>(Arrays.asList(G14_3, G14_4)), null, 2);

		//LEVEL 1
		KPI Growth = buildKPI("", "Growth", "", new ArrayList<KPI>(Arrays.asList(Body_weight, Grams_per_day)), batchType, 1);

		return Growth;

	}


	/**
	 * Gets the efficiency for gro w_ ou t_ aggregated.
	 *
	 * @param batchType the batch type
	 * @return the efficiency for gro w_ ou t_ aggregated
	 */
	public static KPI getEfficiencyForGROW_OUT_AGGREGATED(PopulationType batchType) {

		//########## Efficiency
		//LEVEL 3 - G16_X
		KPI G16_3 = buildKPI("G16_3", "Feed Conversion Ratio (FCR) (150g-400g) %", "", null, null, 3);
		KPI G16_4 = buildKPI("G16_4", "Feed Conversion Ratio (FCR) (400g-800g-final harvest) %", "", null, null, 3);
		KPI G16 = buildKPI("G16", "Feed Conversion Ratio (FCR) (harvest) %", "", null, null, 3);
		//LEVEL 2 - G16_X
		KPI Feed_Conversion_Ratio = buildKPI("", "Feed Conversion Ratio", "", new ArrayList<KPI>(Arrays.asList(G16_3, G16_4, G16)), null, 2);

		//LEVEL 3 - G17_X
		KPI G17_3 = buildKPI("G17_3", "Biological Feed Conversion Ratio (FCRBIO) (150g-400g) %", "", null, null, 3);
		KPI G17_4 = buildKPI("G17_4", "Biological Feed Conversion Ratio (FCRBIO) (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G14_X
		KPI Biological_Feed_Conversion_Ratio = buildKPI("", "Biological Feed Conversion Ratio", "", new ArrayList<KPI>(Arrays.asList(G17_3, G17_4)), null, 2);

		//LEVEL 3 - G18_X
		KPI G18_3 = buildKPI("G18_3", "Feed Intake (FI) (150g-400g) %", "", null, null, 3);
		KPI G18_4 = buildKPI("G18_4", "Feed Intake (FI) (400g-800g-final harvest) %", "", null, null, 3);
		//LEVEL 2 - G18_X
		KPI Feed_Intake = buildKPI("", "Feed Intake", "", new ArrayList<KPI>(Arrays.asList(G18_3, G18_4)), null, 2);

		//LEVEL 2 - G19
		KPI Stocking_density = buildKPI("G19", "Stocking density", "",null, null, 2);

		//LEVEL 1
		KPI Efficiency = buildKPI("", "Efficiency", "", new ArrayList<KPI>(Arrays.asList(Feed_Conversion_Ratio, Biological_Feed_Conversion_Ratio, Feed_Intake, Stocking_density)), batchType, 1);

		return Efficiency;
	}
}
