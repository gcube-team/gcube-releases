package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;


public class Hspen {

	private String speciesID;
	private Envelope depth;
	private String meanDepth;
	private boolean pelagic;
	private String layer;
	private Envelope temperature;
	private Envelope salinity;
	private Envelope primaryProduction;
	private Envelope iceConcentration;
	private boolean landDistanceYN;
	private Envelope landDistance;
	private Coordinates coordinates;
	private String faoAreas;
	
	public void setDepth(Envelope depth) {
		this.depth = depth;
	}
	public Envelope getDepth() {
		return depth;
	}
	public void setMeanDepth(String meanDepth) {
		this.meanDepth = meanDepth;
	}
	public String getMeanDepth() {
		return meanDepth;
	}
	public void setPelagic(boolean pelagic) {
		this.pelagic = pelagic;
	}
	public boolean isPelagic() {
		return pelagic;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getLayer() {
		return layer;
	}
	public void setTemperature(Envelope temperature) {
		this.temperature = temperature;
	}
	public Envelope getTemperature() {
		return temperature;
	}
	public void setSalinity(Envelope salinity) {
		this.salinity = salinity;
	}
	public Envelope getSalinity() {
		return salinity;
	}
	public void setPrimaryProduction(Envelope primaryProduction) {
		this.primaryProduction = primaryProduction;
	}
	public Envelope getPrimaryProduction() {
		return primaryProduction;
	}
	public void setIceConcentration(Envelope iceConcentration) {
		this.iceConcentration = iceConcentration;
	}
	public Envelope getIceConcentration() {
		return iceConcentration;
	}
	public void setLandDistanceYN(boolean landDistanceYN) {
		this.landDistanceYN = landDistanceYN;
	}
	public boolean isLandDistanceYN() {
		return landDistanceYN;
	}
	public void setLandDistance(Envelope landDistance) {
		this.landDistance = landDistance;
	}
	public Envelope getLandDistance() {
		return landDistance;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setFaoAreas(String faoAreas) {
		this.faoAreas = faoAreas;
	}
	public String getFaoAreas() {
		return faoAreas;
	}
	
	
	
	
	public Object[] toObjectArray(){
		Object[] array = new Object[33];
		array[0] = depth.getMin();array[1] = meanDepth; array[2] = depth.getPrefmin(); 
		array[3] = (pelagic)?1:0; 
		array[4] = depth.getPrefmax(); array[5] = depth.getMax(); 
		array[6] = temperature.getMin();
		array[7] = layer;
		array[8] = temperature.getPrefmin();array[9] = temperature.getPrefmax();array[10] = temperature.getMax();
		array[11] = salinity.getMin();array[12] = salinity.getPrefmin();array[13] = salinity.getPrefmax();array[14] = salinity.getMax();
		array[15] = primaryProduction.getMin();array[16] = primaryProduction.getPrefmin();array[17] = primaryProduction.getPrefmax();array[18] = primaryProduction.getMax();
		array[19] = iceConcentration.getMin();array[20] = iceConcentration.getPrefmin();array[21] = iceConcentration.getPrefmax();array[22] = iceConcentration.getMax();
		array[23] = (landDistanceYN)?1:0;
		array[24] = landDistance.getMin();array[25] = landDistance.getPrefmin();array[26] = landDistance.getPrefmax();array[27] = landDistance.getMax();
		array[28] = coordinates.getNMostLat();array[29] = coordinates.getSMostLat();array[30] = coordinates.getWMostLong();array[31] = coordinates.getEMostLong();
		array[32] = faoAreas;
		return array;
	}
	
	public Object[] latitudeExtent(){
			Object[] array = new Object[2];
			array[0] = coordinates.getMaxCenterLat();
			array[1] = coordinates.getMinCenterLat();
			return array;
	}
	public void setSpeciesID(String speciesID) {
		this.speciesID = speciesID;
	}
	public String getSpeciesID() {
		return speciesID;
	}
	
}
