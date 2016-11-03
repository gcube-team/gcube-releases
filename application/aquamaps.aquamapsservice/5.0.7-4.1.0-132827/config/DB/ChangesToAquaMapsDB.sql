#meta sources

alter table meta_sources alter column sourcehcaf TYPE text;
alter table meta_sources alter column sourcehspen TYPE text;
alter table meta_sources alter column sourcehspec TYPE text;
alter table meta_sources alter column sourceoccurrencecells TYPE text;

alter table meta_sources alter column sourcehcaf drop default;
alter table meta_sources alter column sourcehspen drop default;
alter table meta_sources alter column sourcehspec drop default;
alter table meta_sources alter column sourceoccurrencecells drop default;

alter table meta_sources rename column sourcehcaf to sourcehcafids;
alter table meta_sources rename column sourcehspen to sourcehspenids;
alter table meta_sources rename column sourcehspec to sourcehspecids;
alter table meta_sources rename column sourceoccurrencecells to sourceoccurrencecellsids;

alter table meta_sources rename column sourceoccurrencecellstable to sourceoccurrencecellstables;
alter table meta_sources rename column sourcehspectable to sourcehspectables;
alter table meta_sources rename column sourcehspentable to sourcehspentables;
alter table meta_sources rename column sourcehcaftable to sourcehcaftables;

alter table meta_sources alter column sourcehcaftables type text;
alter table meta_sources alter column sourcehspentables type text;
alter table meta_sources alter column sourcehspectables type text;
alter table meta_sources alter column sourceoccurrencecellstables type text;


alter table meta_sources alter column parameters set default '[]';
update meta_sources set parameters='[]' where parameters is null;
alter table meta_sources drop column date_string_old ;
alter table meta_sources alter column generationtime set default 0;
update meta_sources set generationtime = 0 where generationtime is null;

#hspec_group_requests

alter table hspec_group_requests rename to source_generation_requests;

alter table source_generation_requests alter column sourcehcafid type text;
alter table source_generation_requests alter column sourcehspenid type text;
alter table source_generation_requests alter column sourceoccurrencecellsid type text;

alter table source_generation_requests rename column sourcehcafid to sourcehcafids;
alter table source_generation_requests rename column sourcehspenid to sourcehspenids;
alter table source_generation_requests rename column sourceoccurrencecellsid to sourceoccurrencecellsids;

#alter table source_generation_requests rename COLUMN additionalparameter to additionalparameters;

#alter table source_generation_requests alter column additionalparameters set default '[]';
#update source_generation_requests set additionalparameters = '[]' where additionalparameters is null;

alter table source_generation_requests add column additionalparameters text default '[]';


#Analysis

create table analysis_table (
	id varchar(50),
	title text,
	author text,
	description text,
	status varchar(30),
	
	submissiontime bigint,
	starttime bigint,
	endtime bigint,
	currentphasepercent real,
	
	reportid smallint,
	
	type varchar(30),
	
	archivelocation text,
	
	sources text,
	primary key (id)
);