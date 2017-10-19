package gr.cite.bluebridge.analytics.model;

public class TargetIndicators {

	private double irr;
	
	private double npv;//Net present value: https://support.d4science.org/projects/bluebridge/wiki/VRE_6_2_Specification#Net-Present-Value-NPV
	
	public double getIRR() {
		return this.irr;
	}

	public void setIRR(double irr) {
		this.irr = irr;
	}

	public double getNPV() {
		return this.npv;
	}

	public void setNPV(double npv) {
		this.npv = npv;
	}
}
