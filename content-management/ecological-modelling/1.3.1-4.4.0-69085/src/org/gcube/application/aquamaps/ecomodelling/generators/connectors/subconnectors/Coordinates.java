package org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors;

public class Coordinates {

	private String name;
	private String NMostLat;
	private String SMostLat;
	private String WMostLong;
	private String EMostLong;
	private String maxCenterLat;
	private String minCenterLat;
	
	public Coordinates(String nmostLat,String smostLat,String wmostLong,String emostLong,String maxCenterLat,String minCenterLat){
		NMostLat = nmostLat;
		SMostLat = smostLat;
		WMostLong = wmostLong;
		EMostLong = emostLong;
		maxCenterLat = maxCenterLat;
		minCenterLat = minCenterLat;
	}
	public void setNMostLat(String nMostLat) {
		NMostLat = nMostLat;
	}
	public String getNMostLat() {
		return NMostLat;
	}
	public void setSMostLat(String sMostLat) {
		SMostLat = sMostLat;
	}
	public String getSMostLat() {
		return SMostLat;
	}
	public void setWMostLong(String wMostLong) {
		WMostLong = wMostLong;
	}
	public String getWMostLong() {
		return WMostLong;
	}
	public void setEMostLong(String eMostLong) {
		EMostLong = eMostLong;
	}
	public String getEMostLong() {
		return EMostLong;
	}
	public void setMaxCenterLat(String maxCenterLat) {
		maxCenterLat = maxCenterLat;
	}
	public String getMaxCenterLat() {
		return maxCenterLat;
	}
	public void setMinCenterLat(String minCenterLat) {
		minCenterLat = minCenterLat;
	}
	public String getMinCenterLat() {
		return minCenterLat;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
