package org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare;

public enum Resolution {
	TEN("10",10d,4),
	FIVE("5",5d,6),
	ONE("1",1d,8),
	HALF_DEGREE("0.5",0.5,10),
	TENTH_DEGREE("0.1",0.1,12),
	HALF_TENTH("0.05",0.05,14),
	CENTH_DEGREE("0.01",0.01,16),
	HALF_CENTH("0.005",0.005,18),
	MILLI_DEGREE("0.001",0.001,20),
	HALF_MILLI("0.0005",0.0005,22);
	
	
	
	
	private final String label;
	private final Double resolution;
	private final Integer csquareLength;
	
	Resolution(String label,Double resolution,Integer csquarelength){
		this.label=label;
		this.resolution=resolution;
		this.csquareLength=csquarelength;
	}

	public String getLabel() {
		return label;
	}

	public Double getResolution() {
		return resolution;
	}

	public Integer getCsquareLength() {
		return csquareLength;
	}
	
	public static final Resolution fromLabel(String label){
		for(Resolution res:Resolution.values())
			if(res.getLabel().equals(label)) return res;
		throw new IllegalArgumentException("Invalid label "+label);
	}
	
	
}
