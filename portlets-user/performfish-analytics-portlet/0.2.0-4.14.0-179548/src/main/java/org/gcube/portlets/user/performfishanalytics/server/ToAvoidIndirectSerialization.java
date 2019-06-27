/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.Area;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.Period;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationTypeProperties;
import org.gcube.portlets.user.performfishanalytics.shared.Quarter;
import org.gcube.portlets.user.performfishanalytics.shared.ReferencePopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Species;
import org.gcube.portlets.user.performfishanalytics.shared.Year;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ToAvoidIndirectSerialization.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class ToAvoidIndirectSerialization {

	protected static Logger log = LoggerFactory.getLogger(ToAvoidIndirectSerialization.class);

	//TO FIX Type 'org.eclipse.persistence.indirection.IndirectList'
	//was not included in the set of types which can be serialized
	//by this SerializationPolicy or its Class object could not be loaded.
	//For security purposes, this type will not be serialized.: instance = {IndirectList: not instantiated}
	/**
	 * To gwt serializable.
	 *
	 * @param listPopulationType the list population type
	 * @param population the population
	 * @param fetchProperties the fetch properties
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static List<PopulationType> toGWTSerializable(List<PopulationType> listPopulationType, Population population, boolean fetchProperties){

		if(listPopulationType==null)
			return new ArrayList<PopulationType>(1);

		List<PopulationType> gwtSerializableList = new ArrayList<PopulationType>(listPopulationType.size());
		for (PopulationType populationType : listPopulationType){
			log.trace("Converting Population Type: "+populationType);

			if(fetchProperties){

				List<Species> listSpecies = populationType.getListSpecies();
				populationType.setListSpecies((List<Species>) toListPopulationProperties(listSpecies, null));

				List<Quarter> listQuarter = populationType.getListQuarter();
				populationType.setListQuarter((List<Quarter>) toListPopulationProperties(listQuarter, null));

				List<Area> listArea = populationType.getListArea();
				populationType.setListArea((List<Area>) toListPopulationProperties(listArea, null));

				List<Period> listPeriod = populationType.getListPeriod();
				populationType.setListPeriod((List<Period>) toListPopulationProperties(listPeriod, null));
				
				List<Year> listYears = populationType.getListYears();
				populationType.setListYears((List<Year>) toListPopulationProperties(listYears, null));

			}else{
				populationType.setListSpecies(null);
				populationType.setListQuarter(null);
				populationType.setListArea(null);
				populationType.setListPeriod(null);
				populationType.setListYears(null);
			}

			populationType.setListKPI(null);
			populationType.setPopulation(population);
			gwtSerializableList.add(populationType);
		}

		if(population!=null)
			population.setListPopulationType(gwtSerializableList);

		return gwtSerializableList;

	}


	/**
	 * Sets the population types.
	 *
	 * @param list the list
	 * @param type the type
	 * @return the list<? extends reference population type>
	 */
	public static List<? extends ReferencePopulationType> setPopulationTypes(List<? extends ReferencePopulationType> list, PopulationType type){

		if(list==null)
			return null;

		for (ReferencePopulationType refencePopulationType : list) {
			refencePopulationType.setPopulationType(type);
		}

		return list;
	}

	/**
	 * To list population properties.
	 *
	 * @param list the list
	 * @param type the type
	 * @return the list<? extends population type properties>
	 */
	public static List<? extends PopulationTypeProperties> toListPopulationProperties(List<? extends PopulationTypeProperties> list, PopulationType type){

		List<PopulationTypeProperties> listGWT = new ArrayList<PopulationTypeProperties>();
		for (PopulationTypeProperties populationTypeProperties : list) {
			PopulationTypeProperties pop = toPopulationProperties(populationTypeProperties, type);
			listGWT.add(pop);
		}

		return listGWT;

	}

	/**
	 * To population properties.
	 *
	 * @param <T> the generic type
	 * @param object the object
	 * @param type the type
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PopulationTypeProperties> T toPopulationProperties(T object, PopulationType type){

		if(object instanceof Area){
			return (T) new Area(object.getId(), object.getName(), object.getDescription(), type);
		}else if(object instanceof Species){
			return (T) new Species(object.getId(), object.getName(), object.getDescription(), type);
		}else if(object instanceof Quarter){
			return (T) new Quarter(object.getId(), object.getName(), object.getDescription(), type);
		}else if(object instanceof Period){
			return (T) new Period(object.getId(), object.getName(), object.getDescription(), type);
		}else if(object instanceof Year){
			return (T) new Year(object.getId(), ((Year) object).getValue(), type);
		}else if(object instanceof KPI){
			KPI toKPI = (KPI) object;
			KPI gwtKPI = new KPI(toKPI.getId(),toKPI.getCode(),toKPI.getName(),toKPI.getDescription(), null,type,toKPI.getDeepIndex());
			gwtKPI.setLeaf(toKPI.getListKPI()==null || toKPI.getListKPI().isEmpty());
			return (T) gwtKPI;
		}
		return null;

	}
}
