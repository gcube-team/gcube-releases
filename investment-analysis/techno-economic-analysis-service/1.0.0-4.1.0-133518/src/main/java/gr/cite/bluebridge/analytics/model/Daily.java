package gr.cite.bluebridge.analytics.model;

public class Daily{
    private String day;
    private int bm ;
    private double fcre;	
    private double fcrb;
    private double food;
    private int bmdead;
    
    public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getBm() {
		return bm;
	}
	public void setBm(int bm) {
		this.bm = bm;
	}
	public double getFcre() {
		return fcre;
	}
	public void setFcre(double fcre) {
		this.fcre = fcre;
	}
	public double getFcrb() {
		return fcrb;
	}
	public void setFcrb(double fcrb) {
		this.fcrb = fcrb;
	}
	public double getFood() {
		return food;
	}
	public void setFood(double food) {
		this.food = food;
	}
	public int getBmdead() {
		return bmdead;
	}
	public void setBmdead(int bmdead) {
		this.bmdead = bmdead;
	}
}