package org.gcube.application.aquamaps.ecomodelling.generators.connectors;


public class Hcaf {

	
	private String csquarecode;
	private String depthmean;
	private String depthmax;
	private String depthmin;
	private String sstanmean;
	private String sbtanmean;
	private String salinitymean;
	private String salinitybmean;
	private String primprodmean;
	private String iceconann;
	private String landdist;
	private String oceanarea;
	private String centerlat;
	private String centerlong;
	private String faoaream;
	private String eezall;
	private String lme;
	
	public String getCsquarecode() {
		return csquarecode;
	}
	public void setCsquareCode(String csquarecode) {
		this.csquarecode = csquarecode;
	}
	public String getDepthmean() {
		return depthmean;
	}
	public void setDepthmean(String depthmean) {
		this.depthmean = depthmean;
	}
	public String getDepthmax() {
		return depthmax;
	}
	public void setDepthmax(String depthmax) {
		this.depthmax = depthmax;
	}
	public String getDepthmin() {
		return depthmin;
	}
	public void setDepthmin(String depthmin) {
		this.depthmin = depthmin;
	}
	public String getSstanmean() {
		return sstanmean;
	}
	public void setSstanmean(String sstanmean) {
		this.sstanmean = sstanmean;
	}
	public String getSbtanmean() {
		return sbtanmean;
	}
	public void setSbtanmean(String sbtanmean) {
		this.sbtanmean = sbtanmean;
	}
	public String getSalinitymean() {
		return salinitymean;
	}
	public void setSalinitymean(String salinitymean) {
		this.salinitymean = salinitymean;
	}
	public String getSalinitybmean() {
		return salinitybmean;
	}
	public void setSalinitybmean(String salinitybmean) {
		this.salinitybmean = salinitybmean;
	}
	public String getPrimprodmean() {
		return primprodmean;
	}
	public void setPrimprodmean(String primprodmean) {
		this.primprodmean = primprodmean;
	}
	public String getIceconann() {
		return iceconann;
	}
	public void setIceconann(String iceconann) {
		this.iceconann = iceconann;
	}
	public String getLanddist() {
		return landdist;
	}
	public void setLanddist(String landdist) {
		this.landdist = landdist;
	}
	public String getOceanarea() {
		return oceanarea;
	}
	public void setOceanarea(String oceanarea) {
		this.oceanarea = oceanarea;
	}
	public String getCenterlat() {
		return centerlat;
	}
	public void setCenterlat(String centerlat) {
		this.centerlat = centerlat;
	}
	public String getCenterlong() {
		return centerlong;
	}
	public void setCenterlong(String centerlong) {
		this.centerlong = centerlong;
	}
	public String getFaoaream() {
		return faoaream;
	}
	public void setFaoaream(String faoaream) {
		this.faoaream = faoaream;
	}
	
	public Object[] toObjectArray(){
		
		Object[] array = new Object[17];
		
		array[0]=csquarecode;array[1]=depthmean;array[2]=depthmax;array[3]=depthmin;
		array[4]=sstanmean;array[5]=sbtanmean;array[6]=salinitymean;array[7]=salinitybmean;
		array[8]=primprodmean;array[9]=iceconann;array[10]=landdist;array[11]=oceanarea;
		array[12]=centerlat;array[13]=centerlong;array[14]=faoaream;array[15]=eezall;array[16]=lme;
		
		return array;
	}
	public void setEezall(String eezall) {
		this.eezall = eezall;
	}
	public String getEezall() {
		return eezall;
	}
	public void setLme(String lme) {
		this.lme = lme;
	}
	public String getLme() {
		return lme;
	}

}
