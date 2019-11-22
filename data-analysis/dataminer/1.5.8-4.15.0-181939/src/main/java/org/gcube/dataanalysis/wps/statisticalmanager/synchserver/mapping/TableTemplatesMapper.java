package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.util.HashMap;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class TableTemplatesMapper {

	
	public HashMap<String,String> dbTemplatesMap = new  HashMap<String, String>();
	public HashMap<String,String> linksMap = new  HashMap<String, String>();
	public HashMap<String,String> varsMap = new  HashMap<String, String>();
	
	public TableTemplatesMapper(){
		variablesMapping();
		tablesMapping();
		linksMapping();
	}
	
	public void tablesMapping(){
		dbTemplatesMap = new HashMap<String, String>();
		dbTemplatesMap.put(TableTemplates.HSPEN.name(), "CREATE TABLE #table_name# (#vars#, CONSTRAINT #table_name#_pkey PRIMARY KEY (speciesid, lifestage))");
		dbTemplatesMap.put(TableTemplates.HCAF.name(), "CREATE TABLE #table_name# (#vars#,  CONSTRAINT #table_name#_pkey PRIMARY KEY (csquarecode))");
		dbTemplatesMap.put(TableTemplates.HSPEC.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.OCCURRENCE_AQUAMAPS.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.OCCURRENCE_SPECIES.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.MINMAXLAT.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.TRAININGSET.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.TESTSET.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.CLUSTER.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.TIMESERIES.name(), "CREATE TABLE #table_name# (#vars#)");
		dbTemplatesMap.put(TableTemplates.GENERIC.name(), "CREATE TABLE #table_name# (#vars#)");
	}	

	public void linksMapping(){
		linksMap = new HashMap<String, String>();
		linksMap.put(TableTemplates.HSPEN.name(), "(HSPEN) http://goo.gl/4zDiAK");
		linksMap.put(TableTemplates.HCAF.name(), "(HCAF) http://goo.gl/SZG9uM");
		linksMap.put(TableTemplates.HSPEC.name(),"(HSPEC) http://goo.gl/OvKa1h");
		linksMap.put(TableTemplates.OCCURRENCE_AQUAMAPS.name(), "(OCCURRENCE_AQUAMAPS) http://goo.gl/vHil5T");
		linksMap.put(TableTemplates.OCCURRENCE_SPECIES.name(), "(OCCURRENCE_SPECIES) http://goo.gl/4ExuR5");
		linksMap.put(TableTemplates.MINMAXLAT.name(), "(MINMAXLAT) http://goo.gl/cRzwgN");
		linksMap.put(TableTemplates.TRAININGSET.name(), "(TRAININGSET) http://goo.gl/Br44UQ");
		linksMap.put(TableTemplates.TESTSET.name(), "(TESTSET) http://goo.gl/LZHNXt");
		linksMap.put(TableTemplates.CLUSTER.name(), "(CLUSTER) http://goo.gl/PnKhhb");
		linksMap.put(TableTemplates.TIMESERIES.name(), "(TIMESERIES) http://goo.gl/DoW6fg");
		linksMap.put(TableTemplates.GENERIC.name(), "(GENERIC) A generic comma separated csv file in UTF-8 encoding");
	}	
	
	public void variablesMapping(){
		varsMap = new HashMap<String, String>();
		varsMap.put(TableTemplates.HSPEN.name(), "speccode integer,  speciesid character varying NOT NULL,  lifestage character varying NOT NULL,  faoareas character varying(100),  faoareasref character varying,  faocomplete smallint,  nmostlat real,  smostlat real,  wmostlong real,  emostlong real,  lme character varying(180),  depthyn smallint,  depthmin integer,  depthmax integer, depthprefmin integer,  depthprefmax integer,  meandepth smallint,  depthref character varying,  pelagic smallint,  tempyn smallint,   tempmin real,  tempmax real,   tempprefmin real,   tempprefmax real,   tempref character varying,   salinityyn smallint,  salinitymin real,   salinitymax real,   salinityprefmin real,   salinityprefmax real,   salinityref character varying,  primprodyn smallint,   primprodmin real,   primprodmax real,   primprodprefmin real,   primprodprefmax real,   primprodprefref character varying,   iceconyn smallint, iceconmin real,   iceconmax real,  iceconprefmin real,   iceconprefmax real, iceconref character varying,   landdistyn smallint,   landdistmin real,   landdistmax real,   landdistprefmin real,   landdistprefmax real,   landdistref character varying,   remark character varying,   datecreated timestamp without time zone,   datemodified timestamp without time zone,   expert integer,   dateexpert timestamp without time zone,   envelope smallint,   mapdata smallint,   effort smallint,   layer character(1),   usepoints smallint,   rank smallint");
		varsMap.put(TableTemplates.HCAF.name(), "csquarecode character varying(10) NOT NULL,  depthmin real,  depthmax real,  depthmean real,  depthsd real,  sstanmean real,  sstansd real,  sstmnmax real,  sstmnmin real,  sstmnrange real,  sbtanmean real,  salinitymean real,  salinitysd real,  salinitymax real,  salinitymin real,  salinitybmean real,  primprodmean integer,  iceconann real,  iceconspr real,  iceconsum real,  iceconfal real,  iceconwin real,  faoaream integer,  eezall character varying,  lme integer,  landdist integer,  oceanarea real,  centerlat real,  centerlong real");
		varsMap.put(TableTemplates.HSPEC.name(), "speciesid character varying,  csquarecode character varying,  probability real,  boundboxyn smallint,  faoareayn smallint,  faoaream integer,  eezall character varying,  lme integer");
		varsMap.put(TableTemplates.OCCURRENCE_AQUAMAPS.name(), "csquarecode character varying(10) NOT NULL,  speciesid character varying NOT NULL,  speccode integer,  goodcell smallint,  infaoarea smallint,  inboundbox smallint,  centerlat numeric,  centerlong numeric,  faoaream smallint,  recordid integer NOT NULL");
		varsMap.put(TableTemplates.OCCURRENCE_SPECIES.name(), "institutioncode character varying, collectioncode character varying, cataloguenumber character varying, dataset character varying, dataprovider character varying, datasource character varying, scientificnameauthorship character varying, identifiedby character varying, credits character varying, recordedby character varying, eventdate timestamp without time zone, modified timestamp without time zone, scientificname character varying, kingdom character varying, family character varying, locality character varying, country character varying, citation character varying, decimallatitude double precision, decimallongitude double precision, coordinateuncertaintyinmeters character varying, maxdepth double precision, mindepth double precision, basisofrecord character varying" );
		varsMap.put(TableTemplates.MINMAXLAT.name(), "speciesid character varying,  maxclat real,  minclat real");
		varsMap.put(TableTemplates.TRAININGSET.name(), "<column_name_1 real,  column_name_2 real, ..., column_name_n real>,  label real, groupID character varying");
		varsMap.put(TableTemplates.TESTSET.name(), "<column_name_1 real,  column_name_2 real, ..., column_name_n real>,  tvalue real");
		varsMap.put(TableTemplates.CLUSTER.name(), "<column_name_1 real,  column_name_2 real, ..., column_name_n real> ,  clusterid character varying,  outlier boolean");
		varsMap.put(TableTemplates.TIMESERIES.name(), "<column_name_1 real,  column_name_2 real, ..., column_name_n real>, time timestamp without time zone");
		varsMap.put(TableTemplates.GENERIC.name(), "<column_name_1 real,  column_name_2 real, ..., column_name_n real>");
	}
	
	public String generateCreateStatement(String tablename, String template, String variables){
		return dbTemplatesMap.get(template).replace("#table_name#", tablename).replace("#vars#", variables);
	} 
	
}
