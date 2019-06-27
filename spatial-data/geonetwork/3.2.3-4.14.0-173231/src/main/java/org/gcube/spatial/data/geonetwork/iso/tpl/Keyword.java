package org.gcube.spatial.data.geonetwork.iso.tpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Keyword {

	
	public static class Types{
		public static final String DISCIPLINE="discipline";
		public static final String PLACE="place";
		public static final String STRATUM="stratum";
		public static final String TEMPORAL="temporal";
		public static final String THEME="theme";
	}
	
	public static class Themes{
		public static final String ADDRESSES="Addresses";
		public static final String ADMINISTRATIVE_UNITS="Administrative units";
		public static final String CADASTRAL_PARCELS="Cadastral parcels";
		public static final String COORDINATE_REFERENCE_SYSTEM="Coordinate reference systems";
		public static final String GEOGRAPHICAL_GRID_SYSTEM="Geographical grid systems"; 	
		public static final String GEOGRAPHICAL_NAMES="Geographical names";
		public static final String HYDROGRAPHY="Hydrography"; 	
		public static final String PROTECTED_SITES="Protected sites";
		public static final String TRANSPORT_NETWORKS="Transport networks"; 	
//		ANNEX: 2
		public static final String ELEVATION="Elevation"; 	
		public static final String GEOLOGY="Geology";
		public static final String LAND_COVER="Land cover"; 	
		public static final String ORTHOIMAGERY="Orthoimagery";
//		ANNEX: 3
		public static final String AGRICULTURAL_AND_AQUACULTURE_FACILITIES="Agricultural and aquaculture facilities"; 	
		public static final String AREA_MANAGEMENT_RESTRICTION_REGULATION_ZONES_AND_REPORT_UNITS="Area management / restriction / regulation zones & reporting units";
		public static final String ATMOSPHERIC_CONDITIONS="Atmospheric conditions"; 	
		public static final String BIO_GEOGRAPHICAL_REGIONS="Bio-geographical regions";
		public static final String BUILDINGS="Buildings";
		public static final String ENERGY_RESOURCES="Energy Resources";
		public static final String ENVINROMENTAL_MONITORING_FACILITIES="Environmental monitoring Facilities"; 	
		public static final String HABITATS_AND_BIOTOPES="Habitats and biotopes";
		public static final String HUMAN_HEALTH_AND_SAFETY="Human health and safety"; 	
		public static final String LAND_USE="Land use";
		public static final String METEOROLOGICAL_GEOGRAPHICAL_FEATURES="Meteorological geographical features"; 	
		public static final String MINERAL_RESOURCES="Mineral Resources";
		public static final String NATURAL_RISK_ZONES="Natural risk zones"; 	
		public static final String OCEANOGRAPHIC_GEOGRAPHICAL_FEATURES="Oceanographic geographical features";
		public static final String POPULATION_DISTRIBUTION_AND_DEMOGRAPHY="Population distribution and demography"; 	
		public static final String PRODUCTION_AND_INDUSTRIAL_FACILITIES="Production and industrial facilities";
		public static final String SEA_REGIONS="Sea regions"; 	
		public static final String SOIL="Soil";
		public static final String SPECIES_DISTRIBUTION="Species distribution"; 	
		public static final String STATISTICAL_UNITS="Statistical units";
		public static final String UTILITY_AND_GOVERNMENTAL_SERVICES="Utility and governmental services"; 	
	}
	
	public static final Date INSPIRE_THEME_PUBLICATION_TIME=new Date(2008,06,01);
	public static final String INSPIRE_THEME_THESAURUS_NAME="GEMET - INSPIRE themes, version 1.0";
	
	
	
	
	
	private Collection<String> values;
	private Date creationDate;
	private String type;
	private String thesaurus;
	
	
	public static Keyword getInspireTheme(String theme){
		return new Keyword(Collections.singleton(theme), INSPIRE_THEME_PUBLICATION_TIME, Types.THEME, INSPIRE_THEME_THESAURUS_NAME);
	}
}
