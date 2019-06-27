package org.gcube.dataharvest.datamodel;

public class VREAccessesReportRow {

	private String pagePath;
	private int visitNumber;

	public VREAccessesReportRow(String pagePath, int visitNumber) {
		super();
		this.pagePath = pagePath;
		this.visitNumber = visitNumber;
	}


	public VREAccessesReportRow() {
		// TODO Auto-generated constructor stub
	}


	public String getPagePath() {
		return pagePath;
	}


	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}


	public int getVisitNumber() {
		return visitNumber;
	}


	public void setVisitNumber(int visitNumber) {
		this.visitNumber = visitNumber;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VREAccessesReport [pagePath=");
		builder.append(pagePath);
		builder.append(", visitNumber=");
		builder.append(visitNumber);
		builder.append("]");
		return builder.toString();
	}


}
