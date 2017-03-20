package org.gcube.dataanalysis.seadatanet;

public class DivaAnalysisGetResponse {

	String identifier;
	Double vmin;
	Double vmax;
	Integer stat_obs_count_used;
	Double stat_posteriori_stn;

	public DivaAnalysisGetResponse(String identifier, Double vmin,
	Double vmax,
	Integer stat_obs_count_used,
	Double stat_posteriori_stn){
		
		super();
		this.identifier=identifier;
		this.vmin=vmin;
		this.vmax=vmax;
		this.stat_obs_count_used=stat_obs_count_used;
	    this.stat_posteriori_stn=stat_posteriori_stn;
		
	}

	

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String id) {
		this.identifier = id;
	}
	
	public Double getVmin() {
		return vmin;
	}

	public void setVmin(Double vmin) {
		this.vmin = vmin;
	}
	
	public Double getVmax() {
		return vmax;
	}

	public void setVmax(Double vmax) {
		this.vmax = vmax;
	}

	public Integer getStat_obs_count_used() {
		return stat_obs_count_used;
	}

	public void setStat_obs_count_used(Integer stat_obs_count_used) {
		this.stat_obs_count_used = stat_obs_count_used;
	}

	public Double getStat_posteriori_stn() {
		return stat_posteriori_stn;
	}

	public void setStat_posteriori_stn(Double stat_posteriori_stn) {
		this.stat_posteriori_stn = stat_posteriori_stn;
	}

	@Override
	public String toString() {
		return "DivaFilePostResponse [ IDFILE="+ identifier +", VMAX=" + vmax + ", VMIN="
				+ vmin + ", STAT_OBS_COUNT_USED=" + stat_obs_count_used + ", STAT_POSTERIORI_STN=" + stat_posteriori_stn
				+ "]";
	}

}
