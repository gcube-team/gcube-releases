package org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum CellFields implements IsSerializable{

	//*************************HCAF_S Fields
	
	csquarecode   ,
	loiczid       ,
	nlimit        ,
	slimit        ,
	wlimit        ,
	elimit        ,
	centerlat     ,
	centerlong    ,
	cellarea      ,
	oceanarea     ,
	celltype      ,
	pwater        ,
	faoaream      ,
	faoareain     ,
	countrymain   ,
	countrysecond ,
	countrythird  ,
	eezfirst      ,
	eezsecond     ,
	eezthird      ,
	eezall        ,
	eezremark     ,
	lme           ,
	oceanbasin    ,
	longhurst     ,
	islandsno     ,
	area0_20      ,
	area20_40     ,
	area40_60     ,
	area60_80     ,
	area80_100    ,
	areabelow100  ,
	elevationmin  ,
	elevationmax  ,
	elevationmean ,
	elevationsd   ,
	waveheight    ,
	tidalrange    ,
	landdist      ,
	shelf        ,
	slope       ,
	abyssal    ,
	coral     ,
	estuary  ,
	seagrass,
	seamount,
	
//******************* HCAF_D Fields
	
	depthmin,
	depthmax,
	depthmean,
	depthsd  ,
	sstanmean ,
	sstansd   ,
	sstmnmax  ,
	sstmnmin  ,
	sstmnrange,
	sbtanmean ,
	salinitymean,
	salinitysd  ,
	salinitymax ,
	salinitymin ,
	salinitybmean,
	primprodmean ,
	iceconann    ,
	iceconspr    ,
	iceconsum    ,
	iceconfal    ,
	iceconwin    ,
	
	//******************* Occurrence Cells Fields
	
	
	speccode    ,
	goodcell    ,
	infaoarea   ,
	inboundbox  ,
//	centerlat   , REPEATED
//	centerlong  , REPEATED
//	faoaream    , REPEATED
	recordid    ,
	
	
	
}
