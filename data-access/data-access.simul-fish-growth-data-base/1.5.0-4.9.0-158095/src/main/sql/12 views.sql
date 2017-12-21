create or replace  view SimulModelFullView as (
select 
	sm.*,
	sp.designation as speciesDesignation,
	si.designation as siteDesignation,
	bq.designation as broodstockQualityDesignation,
	fq.designation as feedQualityDesignation,
	st.designation as statusDesignation
from 
	SimulModel sm
	join Species sp on (sm.speciesId=sp.id)
	join Site si on (sm.siteId=si.id)
	join BroodstockQuality bq on (sm.broodstockQualityId=bq.id)
	join FeedQuality fq on (sm.feedQualityId=fq.id)
	join Status st on (sm.statusId=st.id)
);

create or replace view ScenarioFullView as (
select 
	sn.*,
	sm.designation AS simulModelDesignation,
    sm.statusid AS simulModelStatusid,
    sm.statusdesignation AS simulModelStatusDesignation,
    st.designation AS statusDesignation
from 
	Scenario sn
	join SimulModelFullView sm on (sn.simulModelId=sm.id)
	join Status st on (sm.statusId=st.id)
);

create or replace view SiteFullView as (
select 
	si.*,
	rg.designation as regionDesignation,
	oxr.designation as oxygenRatingDesignation,
	cr.designation as currentRatingDesignation
from 
	Site si
	join Region rg on (si.regionId=rg.id)
	join OxygenRating oxr on (si.oxygenRatingId=oxr.id)
	join CurrentRating cr on (si.currentRatingId=cr.id)
);

create or replace view SiteUsageView as
select
  st.id id,
  count(sm.id) simulcount
from
  site st
  left outer join simulmodel sm on (st.id=sm.siteid)
group by
  st.id;
  
create or replace view SimulUsageView as
select
  sm.id id,
  count(sc.id) scenariocount
from
  simulmodel sm
  left outer join scenario sc on (sm.id=sc.simulmodelid)
group by
  sm.id;
  

  
create or replace view sitetempsview as 
SELECT
  id ,
  ownerid,
  designation,
  periodjana,
  periodjanb,
  periodfeba,
  periodfebb,
  periodmara,
  periodmarb,
  periodapra,
  periodaprb,
  periodmaya,
  periodmayb,
  periodjuna,
  periodjunb,
  periodjula,
  periodjulb,
  periodauga,
  periodaugb,
  periodsepa,
  periodsepb,
  periodocta,
  periodoctb,
  periodnova,
  periodnovb,
  perioddeca,
  perioddecb,
  periodyear as yearly FROM Site;