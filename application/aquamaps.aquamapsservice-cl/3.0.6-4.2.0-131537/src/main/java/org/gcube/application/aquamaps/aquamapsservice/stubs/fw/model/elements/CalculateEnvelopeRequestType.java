package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalculateEnvelopeRequestType {
	
	@XmlElement
	private String speciesID;
	@XmlElement
	private String FaoAreas;
	@XmlElement
	private double BoundingNorth;
	@XmlElement
	private double BoundingSouth;
	@XmlElement
	private double BoundingEast;
	@XmlElement
	private double BoundingWest;
	@XmlElement
	private boolean useFAO;
	@XmlElement
	private boolean useBounding;
	@XmlElement
	private boolean useBottomSeaTempAndSalinity;
	
	public CalculateEnvelopeRequestType() {
		// TODO Auto-generated constructor stub
	}

	public CalculateEnvelopeRequestType(String speciesID, String faoAreas,
			double boundingNorth, double boundingSouth, double boundingEast,
			double boundingWest, boolean useFAO, boolean useBounding,
			boolean useBottomSeaTempAndSalinity) {
		super();
		this.speciesID = speciesID;
		FaoAreas = faoAreas;
		BoundingNorth = boundingNorth;
		BoundingSouth = boundingSouth;
		BoundingEast = boundingEast;
		BoundingWest = boundingWest;
		this.useFAO = useFAO;
		this.useBounding = useBounding;
		this.useBottomSeaTempAndSalinity = useBottomSeaTempAndSalinity;
	}

	
	public String speciesID() {
		return speciesID;
	}
	
	public void speciesID(String speciesID) {
		this.speciesID = speciesID;
	}
	
	/**
	 * @return the faoAreas
	 */
	public String faoAreas() {
		return FaoAreas;
	}

	/**
	 * @param faoAreas the faoAreas to set
	 */
	public void faoAreas(String faoAreas) {
		FaoAreas = faoAreas;
	}

	/**
	 * @return the boundingNorth
	 */
	public double boundingNorth() {
		return BoundingNorth;
	}

	/**
	 * @param boundingNorth the boundingNorth to set
	 */
	public void boundingNorth(double boundingNorth) {
		BoundingNorth = boundingNorth;
	}

	/**
	 * @return the boundingSouth
	 */
	public double boundingSouth() {
		return BoundingSouth;
	}

	/**
	 * @param boundingSouth the boundingSouth to set
	 */
	public void boundingSouth(double boundingSouth) {
		BoundingSouth = boundingSouth;
	}

	/**
	 * @return the boundingEast
	 */
	public double boundingEast() {
		return BoundingEast;
	}

	/**
	 * @param boundingEast the boundingEast to set
	 */
	public void boundingEast(double boundingEast) {
		BoundingEast = boundingEast;
	}

	/**
	 * @return the boundingWest
	 */
	public double boundingWest() {
		return BoundingWest;
	}

	/**
	 * @param boundingWest the boundingWest to set
	 */
	public void boundingWest(double boundingWest) {
		BoundingWest = boundingWest;
	}

	/**
	 * @return the useFAO
	 */
	public boolean useFAO() {
		return useFAO;
	}

	/**
	 * @param useFAO the useFAO to set
	 */
	public void useFAO(boolean useFAO) {
		this.useFAO = useFAO;
	}

	/**
	 * @return the useBounding
	 */
	public boolean useBounding() {
		return useBounding;
	}

	/**
	 * @param useBounding the useBounding to set
	 */
	public void useBounding(boolean useBounding) {
		this.useBounding = useBounding;
	}

	/**
	 * @return the useBottomSeaTempAndSalinity
	 */
	public boolean useBottomSeaTempAndSalinity() {
		return useBottomSeaTempAndSalinity;
	}

	/**
	 * @param useBottomSeaTempAndSalinity the useBottomSeaTempAndSalinity to set
	 */
	public void useBottomSeaTempAndSalinity(boolean useBottomSeaTempAndSalinity) {
		this.useBottomSeaTempAndSalinity = useBottomSeaTempAndSalinity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalculateEnvelopeRequestType [spciesID=");
		builder.append(speciesID);
		builder.append(", FaoAreas=");
		builder.append(FaoAreas);
		builder.append(", BoundingNorth=");
		builder.append(BoundingNorth);
		builder.append(", BoundingSouth=");
		builder.append(BoundingSouth);
		builder.append(", BoundingEast=");
		builder.append(BoundingEast);
		builder.append(", BoundingWest=");
		builder.append(BoundingWest);
		builder.append(", useFAO=");
		builder.append(useFAO);
		builder.append(", useBounding=");
		builder.append(useBounding);
		builder.append(", useBottomSeaTempAndSalinity=");
		builder.append(useBottomSeaTempAndSalinity);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
