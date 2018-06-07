package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;

public class AquamapsDBCleaner {

	static String selectTablesToClean = "SELECT tablename FROM pg_tables where tablename <> 'workingtables' and tablename <> 'temptables' and tablename <> 'tempfolders' and tablename <> 'submitted' and tablename <> 'speciesoccursum' and tablename <> 'selectedspecies' and tablename <> 'occurrencecells' and tablename <> 'meta_sources' and tablename <> 'maxminlat_hspen' and tablename <> 'hcaf_s' and tablename <> 'hcaf_d_2050' and tablename <> 'hcaf_d' and tablename <> 'hspen' and tablename <> 'hspec_group_requests' and tablename <> 'codeconversiontable' and tablename <> 'codeconversiontable' and tablename <> 'connectiontesttable' and tablename <> 'countries' and tablename <> 'countriessquares' and tablename <> 'hspec_suitable' and tablename <> 'hcaf_d_micro' and tablename <> 'hspec_2050_native' and tablename <> 'hspen_validation' and tablename <> 'hspen_origin' and tablename <> 'hspec_2050_suitable' and tablename <> 'maxminlat_hspen_validation' and tablename <> 'crossspecies' and tablename <> 'hspec_validation' and tablename <> 'hspec_native' and tablename <> 'crossspecies_nonreviewed' and tablename <> 'hcaf_species_native_unreviewed' and tablename <> 'hspen_new' and tablename <> 'hspec_suitable_micro' and tablename <> 'point_geometries_example'  and tablename <> 'crossspecies_nonreviewed' and tablename <> 'hcaf_species_native_unreviewed' and tablename <> 'hspen_new' and tablename <> 'hspec_suitable_micro' and tablename <> 'point_geometries_example' and tablename <> 'pg_foreign_data_wrapper' and tablename <> 'pg_foreign_data_wrapper' and tablename <> 'pg_foreign_server' and tablename <> 'pg_user_mapping' and tablename <> 'pg_type' and tablename <> 'sql_features' and tablename <> 'sql_implementation_info' and tablename <> 'sql_languages' and tablename <> 'pg_statistic' and tablename <> 'sql_packages' and tablename <> 'sql_parts' and tablename <> 'sql_sizing' and tablename <> 'sql_sizing_profiles' and tablename <> 'pg_authid'  and tablename <> 'pg_ts_parser' and tablename <> 'pg_database' and tablename <> 'pg_shdepend' and tablename <> 'pg_shdescription' and tablename <> 'pg_ts_config' and tablename <> 'pg_ts_config_map' and tablename <> 'pg_ts_dict' and tablename <> 'pg_ts_template' and tablename <> 'pg_auth_members' and tablename <> 'pg_attribute' and tablename <> 'pg_proc' and tablename <> 'pg_class' and tablename <> 'pg_autovacuum' and tablename <> 'pg_attrdef' and tablename <> 'pg_constraint' and tablename <> 'pg_inherits' and tablename <> 'pg_index' and tablename <> 'pg_operator' and tablename <> 'pg_opfamily' and tablename <> 'pg_opclass' and tablename <> 'pg_am' and tablename <> 'pg_amop' and tablename <> 'pg_amproc' and tablename <> 'pg_language' and tablename <> 'pg_largeobject' and tablename <> 'pg_aggregate' and tablename <> 'pg_rewrite' and tablename <> 'pg_trigger' and tablename <> 'pg_listener'  and tablename <> 'pg_description' and tablename <> 'pg_cast' and tablename <> 'pg_enum' and tablename <> 'pg_namespace' and tablename <> 'pg_conversion' and tablename <> 'pg_depend' and tablename <> 'pg_tablespace' and tablename <> 'pg_pltemplate'"; 

	public static void main (String[] args) throws Exception{
		System.out.println("INIT");
		ConnectionsManager connectionsManager;
		connectionsManager = new ConnectionsManager("./cfg/");
		connectionsManager.initAquamapsConnection(null);
		System.out.println("SELECTING");
		List<Object> tables2drop  = connectionsManager.AquamapsQuery(selectTablesToClean);
		System.out.println("CLEANING");
		for (Object t:tables2drop) {
			System.out.println("->"+t);
			String q = "drop table "+t;
			connectionsManager.AquamapsUpdate(q);
		}
		System.out.println("FINISHED");
	}
	
}
