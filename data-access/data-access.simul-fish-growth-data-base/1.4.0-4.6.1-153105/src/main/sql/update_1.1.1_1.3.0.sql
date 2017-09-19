-- VIEWS, drop them all, we will recreate them 

drop view if exists SiteUsageView;
drop view if exists SimulUsageView;
drop view if exists SiteFullView;
drop view if exists ScenarioFullView;
drop view if exists SimulModelFullView;


-- actual updates 
alter table SampleData add column inclusion INTEGER;
ALTER TABLE sampledata ALTER COLUMN inclusion SET DEFAULT 1;
update SampleData set inclusion=1;

ALTER TABLE Site ADD COLUMN	oxygenPeriodJanA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodJanB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodFebA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodFebB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodMarA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodMarB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodAprA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodAprB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodMayA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodMayB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodJunA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodJunB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodJulA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodJulB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodAugA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodAugB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodSepA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodSepB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodOctA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodOctB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodNovA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodNovB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodDecA INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodDecB INTEGER;
ALTER TABLE Site ADD COLUMN oxygenPeriodYear INTEGER;


UPDATE site set
	oxygenPeriodJanA = 0,
	oxygenPeriodJanB = 0,
	oxygenPeriodFebA = 0,
	oxygenPeriodFebB = 0,
	oxygenPeriodMarA = 0,
	oxygenPeriodMarB = 0,
	oxygenPeriodAprA = 0,
	oxygenPeriodAprB = 0,
	oxygenPeriodMayA = 0,
	oxygenPeriodMayB = 0,
	oxygenPeriodJunA = 0,
	oxygenPeriodJunB = 0,
	oxygenPeriodJulA = 0,
	oxygenPeriodJulB = 0,
	oxygenPeriodAugA = 0,
	oxygenPeriodAugB = 0,
	oxygenPeriodSepA = 0,
	oxygenPeriodSepB = 0,
	oxygenPeriodOctA = 0,
	oxygenPeriodOctB = 0,
	oxygenPeriodNovA = 0,
	oxygenPeriodNovB = 0,
	oxygenPeriodDecA = 0,
	oxygenPeriodDecB = 0,
	oxygenPeriodYear = 0;

	
-- VIEWS

VIEWS, copy "as-is" from views.sql 
	