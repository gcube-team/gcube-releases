-- LONG <- BIGINT

-- TABLES

create table Status (
	id BIGINT not null primary key,
	designation CHARACTER VARYING(200) null
);

create table BroodstockQuality (
	id BIGINT not null primary key,
	aa INTEGER,
	designation CHARACTER VARYING(200) null
);

create table CurrentRating (
	id BIGINT not null primary key,
	aa INTEGER,
	designation CHARACTER VARYING(200) null
);

create table FeedQuality (
	id BIGINT not null primary key,
	aa INTEGER,
	designation CHARACTER VARYING(200) null
);

create table OxygenRating (
	id BIGINT not null primary key,
	aa INTEGER,
	designation CHARACTER VARYING(200) null
);

create table Region (
	id BIGINT not null primary key,
	designation CHARACTER VARYING(200) null
);

create table Fcr (
	id BIGSERIAL not null primary key,
	ownerId CHARACTER VARYING(100),
	simulModelId BIGINT,
	temperature INTEGER,
	fromWeight DOUBLE PRECISION,
	value DOUBLE PRECISION,

	CONSTRAINT fcr_uid UNIQUE (ownerid, simulmodelid, fromweight, temperature)
);

create table Mortality (
	id BIGSERIAL primary key,
	ownerId CHARACTER VARYING(100),
	simulModelId BIGINT,
	temperature INTEGER,
	fromWeight DOUBLE PRECISION,
	value DOUBLE PRECISION,

	CONSTRAINT Mortality_uid UNIQUE (ownerid, simulmodelid, fromweight, temperature)
);

create table Sfr (
	id BIGSERIAL primary key,
	ownerId CHARACTER VARYING(100),
	simulModelId BIGINT,
	temperature INTEGER,
	fromWeight DOUBLE PRECISION,
	value DOUBLE PRECISION,

	CONSTRAINT Sfr_uid UNIQUE (ownerid, simulmodelid, fromweight, temperature)
);

create table Sgr (
	id BIGSERIAL primary key,
	ownerId CHARACTER VARYING(100),
	simulModelId BIGINT,
	temperature INTEGER,
	fromWeight DOUBLE PRECISION,
	value DOUBLE PRECISION,

	CONSTRAINT Sgr_uid UNIQUE (ownerid, simulmodelid, fromweight, temperature)
);

create table Scenario (
	id BIGINT not null primary key,
	ownerId CHARACTER VARYING(100),
	designation CHARACTER VARYING(200) null,
	comments TEXT null,
	simulModelId BIGINT,
	startDate DATE null,
	fishNo INTEGER,
	weight DOUBLE PRECISION,
	targetDate DATE null,
	resultsWeight DOUBLE PRECISION,
	resultsGrowth DOUBLE PRECISION,
	resultsEconFCR DOUBLE PRECISION,
	resultsBiolFCR DOUBLE PRECISION,
	resultsSGR DOUBLE PRECISION,
	resultsMortality DOUBLE PRECISION,
	resultsGraphData TEXT null,
	statusId BIGINT
);

create table SimulModel (
	id BIGINT not null primary key,
	ownerId CHARACTER VARYING(100),
	designation CHARACTER VARYING(200) null,
	comments TEXT null,
	speciesId BIGINT,
	siteId BIGINT,
	broodstockQualityId BIGINT,
	broodstockGeneticImprovement BOOLEAN,
	feedQualityId BIGINT,
	uploadFilenameData CHARACTER VARYING(100),
	uploadFilenameWeights CHARACTER VARYING(100),
	uploadfilelocationData CHARACTER VARYING(100),
	uploadfilelocationWeights CHARACTER VARYING(100),
	uploadfiletypeData CHARACTER VARYING(10),
	uploadfiletypeWeights CHARACTER VARYING(10),
	statusId BIGINT
);


create table Site (
	id BIGINT not null primary key,
	ownerId CHARACTER VARYING(100),
	designation CHARACTER VARYING(200) null,
	periodJanA INTEGER,
	periodJanB INTEGER,
	periodFebA INTEGER,
	periodFebB INTEGER,
	periodMarA INTEGER,
	periodMarB INTEGER,
	periodAprA INTEGER,
	periodAprB INTEGER,
	periodMayA INTEGER,
	periodMayB INTEGER,
	periodJunA INTEGER,
	periodJunB INTEGER,
	periodJulA INTEGER,
	periodJulB INTEGER,
	periodAugA INTEGER,
	periodAugB INTEGER,
	periodSepA INTEGER,
	periodSepB INTEGER,
	periodOctA INTEGER,
	periodOctB INTEGER,
	periodNovA INTEGER,
	periodNovB INTEGER,
	periodDecA INTEGER,
	periodDecB INTEGER,
	periodYear INTEGER,
	oxygenPeriodJanA INTEGER,
	oxygenPeriodJanB INTEGER,
	oxygenPeriodFebA INTEGER,
	oxygenPeriodFebB INTEGER,
	oxygenPeriodMarA INTEGER,
	oxygenPeriodMarB INTEGER,
	oxygenPeriodAprA INTEGER,
	oxygenPeriodAprB INTEGER,
	oxygenPeriodMayA INTEGER,
	oxygenPeriodMayB INTEGER,
	oxygenPeriodJunA INTEGER,
	oxygenPeriodJunB INTEGER,
	oxygenPeriodJulA INTEGER,
	oxygenPeriodJulB INTEGER,
	oxygenPeriodAugA INTEGER,
	oxygenPeriodAugB INTEGER,
	oxygenPeriodSepA INTEGER,
	oxygenPeriodSepB INTEGER,
	oxygenPeriodOctA INTEGER,
	oxygenPeriodOctB INTEGER,
	oxygenPeriodNovA INTEGER,
	oxygenPeriodNovB INTEGER,
	oxygenPeriodDecA INTEGER,
	oxygenPeriodDecB INTEGER,
	oxygenPeriodYear INTEGER,
	oxygenRatingId BIGINT,
	currentRatingId BIGINT,
	regionId BIGINT,
	latitude CHARACTER VARYING(200) null,
	longitude CHARACTER VARYING(200) null
);

create table Species (
	id BIGINT not null primary key,

	designation CHARACTER VARYING(200) null
);

create table SimilarSite (
	id BIGINT not null primary key,
	siteId BIGINT not null,
	similarid BIGINT not null,
	grade integer
);


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
	sgr DOUBLE PRECISION not null,
	inclusion INTEFER DEFAULT 1
);


create table WeightLimit (
	id BIGSERIAL not null primary key,
	simulModelId BIGINT,
	uploadSource CHARACTER VARYING(100) not null,
	kpiKind INTEGER,
	toWeight DOUBLE PRECISION,

	CONSTRAINT WeightLimit_uid UNIQUE (simulmodelid, kpiKind, toWeight)
);

