-- VIEWS, drop them all, we will recreate them 

drop view if exists SiteUsageView;
drop view if exists SimulUsageView;
drop view if exists SiteFullView;
drop view if exists ScenarioFullView;
drop view if exists SimulModelFullView;


-- actual updates 

create table SampleData (
	id BIGINT not null primary key,
	simulModelId BIGINT not null,
	uploadSource CHARACTER VARYING(100) not null,
	dateFrom Date not null, 
	dateTo Date not null, 
	openWeight DOUBLE PRECISION not null,
	closeWeight DOUBLE PRECISION not null,
	avgTemperature INTEGER not null,
	openFishNo INTEGER not null,
	closeFishNo INTEGER not null,
	fcr DOUBLE PRECISION not null,
	mortalityRate DOUBLE PRECISION not null,
	sfr DOUBLE PRECISION not null,
	sgr DOUBLE PRECISION not null
);

alter table SimulModel add column uploadFilenameData CHARACTER VARYING(100);
alter table SimulModel add column uploadFilenameWeights CHARACTER VARYING(100);
alter table SimulModel add column uploadfilelocationData CHARACTER VARYING(100);
alter table SimulModel add column uploadfilelocationWeights CHARACTER VARYING(100);
alter table SimulModel add column uploadfiletypeData CHARACTER VARYING(100);
alter table SimulModel add column uploadfiletypeWeights CHARACTER VARYING(100);
alter table SimulModel drop column uploadFilename1;
alter table SimulModel drop column uploadFilename2;
alter table SimulModel drop column uploadFilename3;
alter table SimulModel drop column uploadFilename4;
alter table SimulModel drop column uploadfilelocation1;
alter table SimulModel drop column uploadfilelocation2;
alter table SimulModel drop column uploadfilelocation3;
alter table SimulModel drop column uploadfilelocation4;
alter table SimulModel drop column uploadfiletype1;
alter table SimulModel drop column uploadfiletype2;
alter table SimulModel drop column uploadfiletype3;
alter table SimulModel drop column uploadfiletype4;

alter table Site add column periodYear INTEGER;
-- dummy, in order to avoid null value
update site set periodyear=1;

create table SimilarSite (
	id BIGINT not null primary key,
	siteId BIGINT not null,
	similarid BIGINT not null,
	grade integer
);

create table WeightLimit (
	id BIGINT not null primary key,
	simulModelId BIGINT,
	uploadSource CHARACTER VARYING(100) not null,
	kpiKind INTEGER,
	toWeight DOUBLE PRECISION,

	CONSTRAINT WeightLimit_uid UNIQUE (simulmodelid, kpiKind, toWeight)
);


CREATE FUNCTION GetSimilarSimulmodelids(bigint, integer) RETURNS SETOF bigint AS
$BODY$
BEGIN
  RETURN QUERY 
    select sm.id
    from  simulmodel sm
    where sm.siteid in (
      select ss.similarid 
      from
        similarsite ss 
        inner join simulmodel smorig on (smorig.siteid=ss.siteid) and (smorig.speciesid=sm.speciesid)
      where 
        smorig.id = $1
        and
        ss.grade <= $2
      );
  RETURN;       

END
$BODY$
LANGUAGE plpgsql;

-- VIEWS, copy "as-is" from views.sql 

-- VIEWS

VIEWS, copy "as-is" from views.sql 
