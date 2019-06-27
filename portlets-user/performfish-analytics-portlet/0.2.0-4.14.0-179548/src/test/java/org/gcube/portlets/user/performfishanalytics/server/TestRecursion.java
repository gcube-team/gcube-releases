/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.server.util.database.data.KPIBatchTypeData;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;

import database.PrintDatabasePerBatchType;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 18, 2019
 */
public class TestRecursion {

	public static void main(String[] args) {

		PopulationType batchType_GROW_OUT_INDIVIDUAL =
			buildPopulationType(
				"GROW_OUT_INDIVIDUAL", "GROW_OUT_INDIVIDUAL DESRIPTION", null);
		KPI Losses = KPIBatchTypeData.getLosses(batchType_GROW_OUT_INDIVIDUAL);
		KPI TreatmentsForGrowOut =
			KPIBatchTypeData.getTreatmentsForGrowOutIndividual(batchType_GROW_OUT_INDIVIDUAL);
		ArrayList<KPI> listKPI = new ArrayList<KPI>();
		listKPI.add(Losses);
		listKPI.add(TreatmentsForGrowOut);
		System.out.println("Converting START");
		List<KPI> listGWTKPI = new ArrayList<KPI>(listKPI.size());
		for (KPI toKPI : listKPI) {
			KPI gwtKPI = convert(toKPI);
			// gwtKPI.setLeaf(toKPI.getListKPI()==null ||
			// toKPI.getListKPI().isEmpty());
			listGWTKPI.add(gwtKPI);
		}
		System.out.println("PRINTING START");
		for (KPI kpi : listGWTKPI) {
			PrintDatabasePerBatchType.printKPIs(kpi, kpi.getDeepIndex());
		}
	}

	public static KPI convert(KPI kpi) {

		if (kpi.getListKPI() == null) {
			System.out.println("LEAF " + kpi);
			return getGWTKPI(kpi, null);
		}
		KPI gwtKPI = getGWTKPI(kpi, null);
		System.out.println("Converted: " + gwtKPI);
		for (KPI kpiChild : kpi.getListKPI()) {
			KPI convertedChild = convert(kpiChild);
			if (gwtKPI.getListKPI() == null) {
				List<KPI> listKPI = new ArrayList<KPI>();
				gwtKPI.setListKPI(listKPI);
			}
			gwtKPI.getListKPI().add(convertedChild);
		}
		System.out.println("Filled children of: " + gwtKPI.getName());
		if (gwtKPI.getListKPI() != null) {
			for (KPI chKPI : gwtKPI.getListKPI()) {
				System.out.println("\t" + chKPI);
			}
		}
		return gwtKPI;
	}

	/**
	 * Gets the gwtkpi.
	 *
	 * @param toKPI
	 *            the to kpi
	 * @param populationType
	 *            the population type
	 * @return the gwtkpi
	 */
	public static KPI getGWTKPI(KPI toKPI, PopulationType populationType) {

		KPI gwtKPI =
			new KPI(
				toKPI.getId(), toKPI.getCode(), toKPI.getName(),
				toKPI.getDescription(), null, populationType,
				toKPI.getDeepIndex());
		gwtKPI.setLeaf(toKPI.getListKPI() == null ||
			toKPI.getListKPI().isEmpty());
		return gwtKPI;
	}

	/**
	 * Builds the population type.
	 *
	 * @param type
	 *            the type
	 * @param description
	 *            the description
	 * @param poulation
	 *            the poulation
	 * @return the population type
	 */
	public static PopulationType buildPopulationType(
		String type, String description, Population poulation) {

		return new PopulationType(
			java.util.UUID.randomUUID().toString(), type, type, description,
			poulation);
	}
}
