package gr.cite.gaap.datatransferobjects;

public class TheCoords {
	private String[] coord0;
	private String[] coord1;
	private String[] coord2;
	private String[] coord3;
	private String[] extent;
	
	public String[] getExtent() {
		return extent;
	}
	public void setExtent(String[] extent) {
		this.extent = extent;
	}
	public String[] getCoord0() {
		return coord0;
	}
	public void setCoord0(String[] coord0) {
		this.coord0 = coord0;
	}
	public String[] getCoord1() {
		return coord1;
	}
	public void setCoord1(String[] coord1) {
		this.coord1 = coord1;
	}
	public String[] getCoord2() {
		return coord2;
	}
	public void setCoord2(String[] coord2) {
		this.coord2 = coord2;
	}
	public String[] getCoord3() {
		return coord3;
	}
	public void setCoord3(String[] coord3) {
		this.coord3 = coord3;
	}
	public String getExtentString(){
		StringBuilder stringBuilder = new StringBuilder();
		String[] extents = getExtent();
		for(int i=0;i<extents.length;i++){
			if(i==extents.length-1){
				stringBuilder.append(extents[i]);
				continue;
			}
			stringBuilder.append(extents[i] + ",");
		}
		
		return stringBuilder.toString();
	}
}
